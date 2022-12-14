package com.msquare.flabook.service;

import com.drew.imaging.ImageProcessingException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.*;
import com.msquare.flabook.enumeration.UserRole;
import com.msquare.flabook.exception.CommonException;
import com.msquare.flabook.exception.FlabookGlobalException;
import com.msquare.flabook.form.GambleVo;
import com.msquare.flabook.models.*;
import com.msquare.flabook.repository.GambleRepository;
import com.msquare.flabook.repository.GambleResultRepository;
import com.msquare.flabook.repository.GambleVersionRepository;
import com.msquare.flabook.repository.UserEventInfoRepository;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class GambleService {

    private static final String GAMBLE_PATH = "gamble";
    private static final String VERSION_PATH = "version";
    private static final ZoneId ASIA_SEOUL_ZONEID = ZoneId.of("Asia/Seoul");

    private final GambleRepository gambleRepository;
    private final GambleResultRepository gambleResultRepository;
    private final GambleVersionRepository gambleVersionRepository;
    private final ShareService shareService;
    private final EntityManager em;
    private final UserEventInfoRepository userEventInfoRepository;
    private final ImageUploadService imageUploadService;

    private CriteriaBuilder createCriteriaBuilder() {
        return em.getCriteriaBuilder();
    }

    private void initGamble(Gamble gamble, ZonedDateTime now) {
        if(gamble == null)
            return;

        int updateVersion = gamble.getVersion() + 1;
        ZonedDateTime initDate = now.withHour(0).withMinute(0).withSecond(0).withNano(0).withZoneSameInstant(ASIA_SEOUL_ZONEID);
        Optional<GambleVersion> optionalGambleVersion = gambleVersionRepository.findByGamble(gamble, initDate);
        GambleVersion gambleVersion= optionalGambleVersion.orElseGet(() -> {
            GambleVersion gv = GambleVersion.builder().gamble(gamble).version(updateVersion).date(initDate).build();
            return gambleVersionRepository.save(gv);
        });

        gamble.setVersion(gambleVersion.getVersion());
        gamble.setInitDate(initDate);
        //??? ???????????? ??????????????? ?????????
        gamble.getItems().forEach(item ->
            item.setRemains(item.getAmount())
        );
    }

    private static void initResult(Gamble gamble) {
        if(gamble == null)
            return;

        gamble.getResults().clear();
    }

    private static void decrease(GambleItem item) {
        if(item != null && item.getRemains() > 0)
            item.setRemains(item.getRemains() - 1);
    }

    private static void increase(GambleItem item) {
        if(item != null && item.getAmount() > item.getRemains())
            item.setRemains(item.getRemains() + 1);
    }

    @Transactional
    public GambleDto findByUser(Long id, User user) {
        Gamble gamble = gambleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(FlabookGlobalException.Messages.NOT_FOUND_ENTITY + id));
        ZonedDateTime now = ZonedDateTime.now(ASIA_SEOUL_ZONEID);

        //????????? ?????? ??????
        ZonedDateTime initDate = gamble.getInitDate().withZoneSameInstant(ASIA_SEOUL_ZONEID).withHour(gamble.getInitHour()).withMinute(0).withSecond(0).withNano(0);
        log.info("init date {} after {}", now, initDate);

        //?????? ???????????? ????????? ????????? ????????? ????????? ???????????? ???????????? ???????????? version + 1.
        int gambleVersion = gamble.getVersion();
        if(now.isAfter(initDate) && now.getDayOfYear() > initDate.getDayOfYear()) {
            gambleVersion = gambleVersion + 1;
        }

        Tuple tuple = gambleResultRepository.findGambleDateWithCount(gamble.getId(), user.getId(), gambleVersion);
        int attempt = Optional.ofNullable(tuple.get(1, Long.class)).map(Long::intValue).orElse(0);


        GambleDto gambleDto = GambleDto.of(gamble);
        if(gamble.getStartDate().isAfter(now) || gamble.getEndDate().isBefore(now)) {
            //????????? ??????????????? ????????????
            gambleDto.setAttempt(gambleDto.getMaxAttempt());
        } else {
            gambleDto.setAttempt(attempt);
        }

        if(attempt == 0) {
            gambleDto.setAlready(false);
        } else {
            ZonedDateTime lastBetDate = tuple.get(0, ZonedDateTime.class);
            gambleDto.setAlready(now.isBefore(lastBetDate.plusHours(gamble.getRetryHour())));
        }


        return gambleDto;
    }

    @Transactional(readOnly = true)
    public GambleDto find(Long id) {
        Gamble gamble = gambleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(FlabookGlobalException.Messages.NOT_FOUND_ENTITY + id));
        return GambleDto.of(gamble);
    }

    @Transactional(readOnly = true)
    public List<GambleVersionDto> findGambleVersionList(Long id) {
        Gamble gamble = gambleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(FlabookGlobalException.Messages.NOT_FOUND_ENTITY + id));
        return gambleVersionRepository.findByGamble(gamble).stream().map(GambleVersionDto::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long findGambleResultCount(Long id, int version) {
        if(version == 0) {
            return gambleResultRepository.findGambleResultCount(id);
        } else {
            return gambleResultRepository.findGambleResultCount(id, version);
        }
    }

    private void createGamblePredicates(CriteriaBuilder cb, Root<Gamble> gambleRoot, List<Predicate> predicates, Optional<ZonedDateTime> optionalNow, Optional<Boolean> optionalActive) {
        optionalActive.ifPresent(active ->
            predicates.add(cb.equal(gambleRoot.get("active"), active))
        );

        optionalNow.ifPresent(now -> {
            predicates.add(cb.greaterThanOrEqualTo(gambleRoot.get("endDate"), now));
            predicates.add(cb.lessThan(gambleRoot.get("startDate"), now));
        });
    }

    @Transactional(readOnly = true)
    public Long getCount(Optional<ZonedDateTime> optionalNow, Optional<Boolean> optionalActive) {
        CriteriaBuilder cb = createCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Gamble> gambleRoot = query.from(Gamble.class);
        query.select(cb.count(query.from(Gamble.class)));
        List<Predicate> predicates = new ArrayList<>();
        createGamblePredicates(cb, gambleRoot, predicates, optionalNow, optionalActive);
        query.where(predicates.toArray(new Predicate[]{}));
        return em.createQuery(query).getSingleResult();
    }

    @Transactional
    public GambleDto init(Long id) {
        Gamble gamble = gambleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(FlabookGlobalException.Messages.NOT_FOUND_ENTITY + id));
        ZonedDateTime now = ZonedDateTime.now();
        initGamble(gamble, now);
        initResult(gamble);
        return GambleDto.of(gamble);
    }

    @Transactional
    public void deleteResult(Long id, User user) {
        GambleResult result = gambleResultRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(FlabookGlobalException.Messages.NOT_FOUND_ENTITY +  id));
        if(!(user.getRole().equals(UserRole.ADMIN) || result.getUser().equals(user))) {
            throw new CommonException(CommonResponseCode.USER_LOGIN_AUTH_FAIL, null);
        }

        if(BooleanUtils.isTrue(result.getNeedAddress())) {
            throw new CommonException(CommonResponseCode.USER_LOGIN_AUTH_FAIL, null);
        }

        if(result.getVersion() == result.getGamble().getVersion()) {
            //version ??? ????????? ?????? ??????
            increase(result.getItem());
        }

        gambleResultRepository.delete(result);
    }

    public List<GambleDto> findList(int offset, int count, Optional<ZonedDateTime> optionalNow, Optional<Boolean> optionalActive) {
        CriteriaBuilder cb = createCriteriaBuilder();
        CriteriaQuery<Gamble> query = cb.createQuery(Gamble.class);
        Root<Gamble> gambleRoot = query.from(Gamble.class);
        List<Predicate> predicates = new ArrayList<>();
        createGamblePredicates(cb, gambleRoot, predicates, optionalNow, optionalActive);
        query.where(predicates.toArray(new Predicate[]{}));
        query.orderBy(cb.desc(gambleRoot.get("id")));
        return em.createQuery(query).setFirstResult(offset).setMaxResults(count).getResultList().stream().map(GambleDto::of).collect(Collectors.toList());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatResult {
        private String message;
        private GambleResultDto gambleResult;
    }

    private static void buildBlankMessage(StringBuilder stringBuilder, boolean hasNext, int retryHour) {
        if(hasNext) {
            if(retryHour > 0) {
                stringBuilder.append("????????????\r\n");
                stringBuilder.append(String.format("%d?????? ??? ?????? ????????? ?????????!", retryHour));
            } else {
                stringBuilder.append("????????????\r\n?????? ????????? ?????????!");
            }
        } else {
            stringBuilder.append("????????????\r\n?????? ?????? ????????? ?????????!");
        }
    }

    private static void buildMessage(StringBuilder stringBuilder, GambleItem item, boolean hasNext, int retryHour) {
        stringBuilder.append(item.getName());
        stringBuilder.append(" ??????!");

        if(hasNext) {
            stringBuilder.append("\r\n");
            stringBuilder.append(String.format("%d?????? ??? ??? ??? ??? ????????? ??? ????????????.", retryHour));
        } else {
            stringBuilder.append("\r\n?????? ?????? ????????? ?????????!");
        }
    }

    private static String toMessage(GambleItem item, boolean hasNext, int retryHour) {

        if(item == null)
            return "????????? ????????? ????????????.";

        StringBuilder stringBuilder = new StringBuilder();
        if(item.isAddress()) {
            stringBuilder.append("??????????????????.\r\n");
            stringBuilder.append(item.getName());
            stringBuilder.append(" ??????!!!!");
        } else {

            if(item.isBlank()) {
                buildBlankMessage(stringBuilder, hasNext, retryHour);
            } else {
                buildMessage(stringBuilder, item, hasNext, retryHour);
            }
        }
        return stringBuilder.toString();
    }

    private static final Random RANDOM = new Random();

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BatResult bet(Long id, User user) { //NOSONAR

        ZonedDateTime now = ZonedDateTime.now(ASIA_SEOUL_ZONEID);
        Gamble gamble = gambleRepository.findByIdLockOnly(id).orElseThrow(() -> new IllegalArgumentException("not found entity"));

        if(gamble.getStartDate().isAfter(now) || gamble.getEndDate().isBefore(now)) {
            throw new CommonException(CommonResponseCode.FAIL.getResultCode(), "????????? ??????????????? ????????????.");
        }

        if(gamble.getInitDate() == null) {
            gamble.setInitDate(ZonedDateTime.of(LocalDate.MIN, LocalTime.MIN, ASIA_SEOUL_ZONEID));
        }

        //????????? ?????? ??????
        ZonedDateTime initDate = gamble.getInitDate().withZoneSameInstant(ASIA_SEOUL_ZONEID).withHour(gamble.getInitHour()).withMinute(0).withSecond(0).withNano(0);
        log.info("init date {} after {}", now, initDate);

        //?????? ???????????? ????????? ????????? ????????? ????????? ???????????? ?????????
        if(now.isAfter(initDate) && now.getDayOfYear() > initDate.getDayOfYear()) {
            initGamble(gamble, now);
        }

        //????????? ?????? ????????? ????????? ??????
        List<GambleResult> results = gambleResultRepository.findGambleResult(gamble.getId(), user.getId(), gamble.getVersion());
        if(gamble.getMaxAttempt() > 0 && gamble.getMaxAttempt() <= results.size()) {
            //????????? ?????? ??????
            log.error("????????? ?????? ??????");
            throw new CommonException(CommonResponseCode.FAIL.getResultCode(), "????????? ?????? ?????? ??????!%n?????? ?????? ????????? ?????????.");
        } else if(!results.isEmpty()) {
            GambleResult gambleResult = results.get(results.size() - 1);
            if(now.isBefore(gambleResult.getCreatedAt().plusHours(gamble.getRetryHour()))) {
                //????????? ???????????? ???????????? ??????
                log.error("????????? ?????? ??????");
                throw new CommonException(CommonResponseCode.FAIL.getResultCode(), String.format("%d??? ?????? ??????.%n%d?????? ??? ???????????? ???????????????.%n?????? ??? ?????? ????????? ?????????!", results.size(), gamble.getRetryHour()));
            }
        }

        Optional<GambleItem> optionalBlankItem = gamble.getItems().stream().filter(GambleItem::isBlank).findAny();
        int max = gamble.getItems().stream().map(GambleItem::getRemains).mapToInt(value -> value).sum();

        GambleItem jackpot = null;
        if(max == 0) {
            //?????? ????????? ?????? ?????? ??????
            jackpot = optionalBlankItem.orElseThrow(() -> {
                log.error("?????? ??????");
                return new CommonException(CommonResponseCode.FAIL.getResultCode(), "?????? ???????????? ?????????????????????.");
            });
        } else {
            int index = RANDOM.nextInt(max);

            int sum = 0;
            for(GambleItem item : gamble.getItems()) {
                sum += item.getRemains();
                if(sum > index) {
                    jackpot = item;
                    break;
                }
            }
        }

        decrease(jackpot);

        String message = toMessage(jackpot, gamble.getMaxAttempt() > results.size() + 1, gamble.getRetryHour());
        GambleResult gambleResult = GambleResult.builder().gamble(gamble).item(jackpot).user(user).createdAt(now).version(gamble.getVersion()).build();
        //??????????????? ????????? item ?????? ????????? ????????? ???????????? ?????? ?????? needAddress == true
        if(jackpot != null && jackpot.isAddress()) {
            Optional<UserEventInfo> optionalUserEventInfo = userEventInfoRepository.findByUser(user);
            gambleResult.setNeedAddress(jackpot.isAddress() && !optionalUserEventInfo.isPresent());
        } else {
            gambleResult.setNeedAddress(null);
        }

        return new BatResult(message, GambleResultDto.of(gambleResultRepository.save(gambleResult)));
    }

    @Transactional(readOnly = true)
    public List<GambleResultDto> findResultList(Long id, Optional<Long> optionalItemId, int version, Optional<Integer> optionalOffset, Optional<Integer> optionalCount) {
        CriteriaBuilder cb = createCriteriaBuilder();
        CriteriaQuery<GambleResult> query = cb.createQuery(GambleResult.class);

        List<Predicate> predicates = new ArrayList<>();
        buildQuery(query, cb, id, predicates, optionalItemId, version);
        query.where(predicates.toArray(new Predicate[]{}));

        TypedQuery<GambleResult> typedQuery = em.createQuery(query);
        optionalOffset.ifPresent(typedQuery::setFirstResult);
        optionalCount.ifPresent(typedQuery::setMaxResults);
        return typedQuery.getResultList().stream().map(GambleResultDto::of).collect(Collectors.toList());
    }

    private void buildQuery(CriteriaQuery<?> query, CriteriaBuilder cb, Long id, List<Predicate> predicates, Optional<Long> optionalItemId, int version) {
        Root<GambleResult> gambleResultRoot = query.from(GambleResult.class);
        Path<Gamble> gamblePath = gambleResultRoot.get(GAMBLE_PATH);
        predicates.add(cb.equal(gamblePath.get("id"), id));
        optionalItemId.ifPresent(itemId -> {
            Path<GambleItem> itemPath = gambleResultRoot.get("item");
            predicates.add(cb.equal(itemPath.get("id"), itemId));
        });

        if(version > 0) {
            predicates.add(cb.equal(gambleResultRoot.get(VERSION_PATH), version));
        }
    }

    @SuppressWarnings("unused")
    @Transactional(readOnly = true)
    public long findResultCount(Long id, Optional<Long> optionalItemId, int version) {
        CriteriaBuilder cb = createCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<GambleResult> gambleResultRoot = query.from(GambleResult.class);
        List<Predicate> predicates = new ArrayList<>();
        buildQuery(query, cb, id, predicates, optionalItemId, version);
        query.select(cb.count(gambleResultRoot));
        query.where(predicates.toArray(new Predicate[]{}));

        TypedQuery<Long> typedQuery = em.createQuery(query);
        return typedQuery.getSingleResult();
    }

    private void writeStreamExcel(List<GambleResult> resultList, boolean godo, HttpServletResponse response) {
        try (HSSFWorkbook workbook = new HSSFWorkbook()) {
            //???????????? ??????
            HSSFSheet sheet = workbook.createSheet();
            //??? ??????
            HSSFRow row = sheet.createRow(0);
            //??? ??????
            HSSFCell cell;

            //?????? ?????? ??????


            //???????????? size ?????? row??? ??????
            if (godo) {

                cell = row.createCell(0);
                cell.setCellValue("????????? ???????????????");

                int rowNum = 1;
                for (GambleResult gambleResult : resultList) {
                    ShopUserDto shopUserDto = ShopUserDto.of(gambleResult.getUser());
                    //??? ??????
                    row = sheet.createRow(rowNum++);
                    cell = row.createCell(0);
                    cell.setCellValue(shopUserDto.getShopUserId());
                    cell.setCellType(CellType.STRING);
                }

            } else {

                cell = row.createCell(0);
                cell.setCellValue("#");


                cell = row.createCell(1);
                cell.setCellValue("??????");

                cell = row.createCell(2);
                cell.setCellValue("?????????");

                cell = row.createCell(3);
                cell.setCellValue("?????????ID");

                cell = row.createCell(4);
                cell.setCellValue("?????????");

                cell = row.createCell(5);
                cell.setCellValue("?????????");
                cell = row.createCell(6);
                cell.setCellValue("??????");
                cell = row.createCell(7);
                cell.setCellValue("????????????");
                cell = row.createCell(8);
                cell.setCellValue("????????????1");
                cell = row.createCell(9);
                cell.setCellValue("????????????2");

                int rowNum = 1;

                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                for (GambleResult gambleResult : resultList) {

                    ShopUserDto shopUserDto = ShopUserDto.of(gambleResult.getUser());
////??? ??????
                    row = sheet.createRow(rowNum++);
                    cell = row.createCell(0);
                    cell.setCellValue(String.valueOf(gambleResult.getId()));
                    cell.setCellType(CellType.STRING);

                    cell = row.createCell(1);
                    cell.setCellValue(gambleResult.getItem().getName());
                    cell.setCellType(CellType.STRING);

                    cell = row.createCell(2);
                    cell.setCellValue(gambleResult.getCreatedAt().withZoneSameInstant(ASIA_SEOUL_ZONEID).format(dateTimeFormatter));
                    cell.setCellType(CellType.STRING);

                    cell = row.createCell(3);
                    cell.setCellValue(shopUserDto.getShopUserId());
                    cell.setCellType(CellType.STRING);

                    cell = row.createCell(4);
                    cell.setCellValue(shopUserDto.getNickname());
                    cell.setCellType(CellType.STRING);

                    if (gambleResult.getItem().isAddress()) {
                        ShippingDto shippingDto = ShippingDto.of(gambleResult.getUser().getEventInfo());
                        if (shippingDto != null) {
                            cell = row.createCell(5);
                            cell.setCellValue(shippingDto.getName());
                            cell.setCellType(CellType.STRING);

                            cell = row.createCell(6);
                            cell.setCellValue(String.format("%s %s", shippingDto.getRoadAddress(), Optional.ofNullable(shippingDto.getDetailAddress()).orElse("")));
                            cell.setCellType(CellType.STRING);

                            cell = row.createCell(7);
                            cell.setCellValue(shippingDto.getPostCode());
                            cell.setCellType(CellType.STRING);

                            cell = row.createCell(8);
                            cell.setCellValue(shippingDto.getPhone1());
                            cell.setCellType(CellType.STRING);

                            cell = row.createCell(9);
                            cell.setCellValue(shippingDto.getPhone2());
                            cell.setCellType(CellType.STRING);
                        }
                    }
                }
            }

            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("error", e);
        }

    }

    @Transactional(readOnly = true)
    public void download(Long id, Optional<Long> optionalItemId, int version, HttpServletResponse response, boolean godo) throws IOException {
        Gamble gamble = gambleRepository.findById(id).orElseThrow(() -> new FlabookGlobalException("????????? ?????? ??? ????????????."));
        StringBuilder fileNameBuilder = new StringBuilder();
        if(version > 0) {
            GambleVersion gambleVersion = gambleVersionRepository.findByGamble(gamble, version).orElseThrow(() -> new FlabookGlobalException("????????? ?????? ??? ????????????."));
            fileNameBuilder.append(gamble.getTitle()).append("_").append(gambleVersion.getDate().withZoneSameInstant(ASIA_SEOUL_ZONEID).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } else {
            fileNameBuilder.append(gamble.getTitle()).append("_").append("????????????");
        }

        optionalItemId.ifPresent(itemId -> {
            String itemName = gamble.getItems().stream().filter(item -> item.getId().equals(itemId)).map(GambleItem::getName).findAny().orElse("");
            if(StringUtils.isNotBlank(itemName)) {
                fileNameBuilder.append(" ");
                fileNameBuilder.append(itemName);
            }
        });

        if(godo) {
            fileNameBuilder.append(" ");
            fileNameBuilder.append("?????????????????????");
        }

        fileNameBuilder.append(".xls");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileNameBuilder.toString().replace(" ", "+"), "utf-8"));
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<GambleResult> query = cb.createQuery(GambleResult.class);

        List<Predicate> predicates = new ArrayList<>();
        buildQuery(query, cb, id, predicates, optionalItemId, version);
        query.where(predicates.toArray(new Predicate[]{}));

        List<GambleResult> gambleResults = em.createQuery(query).getResultList();
        writeStreamExcel(gambleResults, godo, response);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ShareDto share(Long id, User user) {
        return shareService.doShareGamble(user, id);
    }

    private void changeResource(Gamble gamble, MultipartFile file) throws InterruptedException, IOException, ImageProcessingException {
        ImageUploadService.ImageResourceInfoWithMetadata infoWithMetadata;
        infoWithMetadata = imageUploadService.uploadWithMeta(FolderDatePatterns.EVENTS, file);
        gamble.setResource(infoWithMetadata.getImageResource());
    }

    private void updateResource(Gamble gamble, MultipartFile file, boolean enableInit) {
        if(file != null && !file.isEmpty()) {
            try {
                changeResource(gamble, file);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            } catch (IOException | ImageProcessingException e) {
                e.printStackTrace();
            }
            //????????? enableInit ????????? ?????? ??????
        } else if(enableInit) {
            gamble.setResource(null);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    @Transactional
    public GambleDto createGamble(GambleVo.CreateGamble vo ) {

        Gamble gamble = Gamble.builder().title(vo.getTitle()).url(vo.getUrl()).maxAttempt(vo.getMaxAttempt()).retryHour(vo.getRetryHour())
                .startDate(vo.getStart()).endDate(vo.getEnd()).items(new ArrayList<>()).active(vo.getActive()).build();
        for(GambleVo.CreateItem itemVo : vo.getItems()) {
            GambleItem item = GambleItem.builder().name(itemVo.getName())
                    .gamble(gamble).amount(itemVo.getAmount())
                    .address(itemVo.getAddress()).blank(itemVo.getBlank()).build();
            gamble.getItems().add(item);
        }

        updateResource(gamble, vo.getFile(), false);

        return GambleDto.of(gambleRepository.save(gamble));
    }

    @SuppressWarnings("UnusedReturnValue")
    @Transactional
    public GambleDto updateGamble(Long id, GambleVo.UpdateGamble vo ) {

        Gamble gamble = gambleRepository.findById(id).orElseThrow(() -> new FlabookGlobalException("not found entity"));
        gamble.setTitle(vo.getTitle());
        gamble.setUrl(vo.getUrl());
        gamble.setMaxAttempt(vo.getMaxAttempt());
        gamble.setRetryHour(vo.getRetryHour());
        gamble.setStartDate(vo.getStart());
        gamble.setEndDate(vo.getEnd());
        gamble.setActive(vo.getActive());

        log.info("update gamble : {}", gamble);
        Map<String, GambleVo.UpdateItem> liveIdMap = vo.getItems().stream().filter(item -> StringUtils.isNotBlank(item.getId())).collect(Collectors.toMap(GambleVo.UpdateItem::getId, item-> item));

        //?????? ID item ??????
        gamble.getItems().removeIf(item -> {
            boolean contains = liveIdMap.containsKey(String.valueOf(item.getId()));
            return !contains;
        });

        gamble.getItems().forEach(item -> {
            GambleVo.UpdateItem itemVo = liveIdMap.get(String.valueOf(item.getId()));
            item.setAmount(itemVo.getAmount());
            item.setBlank(itemVo.getBlank());
            item.setAddress(itemVo.getAddress());
            item.setName(itemVo.getName());
        });

        //ID ??? ?????? ?????? ??????.
        vo.getItems().stream().filter(item -> StringUtils.isBlank(item.getId())).forEach(itemVo -> {
            GambleItem item = GambleItem.builder().gamble(gamble).name(itemVo.getName()).blank(itemVo.getBlank()).amount(itemVo.getAmount()).address(itemVo.getAddress()).remains(itemVo.getAmount()).build();
            gamble.getItems().add(item);
        });

        updateResource(gamble, vo.getFile(), true);

        return GambleDto.of(gambleRepository.save(gamble));
    }

}
