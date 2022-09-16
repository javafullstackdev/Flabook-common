package com.msquare.flabook.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.configurations.S3Bucket;
import com.msquare.flabook.dto.MetatagDto;
import com.msquare.flabook.dto.NotificationDto;
import com.msquare.flabook.dto.OrderDto;
import com.msquare.flabook.dto.mapper.GoodsMapper;
import com.msquare.flabook.dto.shop.*;
import com.msquare.flabook.models.ImageResource;

import com.msquare.flabook.models.User;
import com.msquare.flabook.models.shop.Goods;
import com.msquare.flabook.models.shop.GoodsEtc;
import com.msquare.flabook.repository.GoodsRepository;
import com.msquare.flabook.repository.UserRepository;
import com.msquare.flabook.service.elasticsearch.CacheValues;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.*;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopApiService {

    private static final ZoneId ASIA_SEOUL_ZONEID = ZoneId.of("Asia/Seoul");
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String ACCESS_TOKEN_HEADER = "access_token";
    private static final String ACCEPT_HEADER = "Accept";
    private static final String DELIMITER = "/";
    private static final GoodsResponseDto EMPTY_GOODS_RESPONSE = new GoodsResponseDto(new HeaderDto("000", "0", 1, "1", 1), Collections.emptyList());
    private static final OrderResponseDto EMPTY_ORDER_RESPONSE = new OrderResponseDto(new HeaderDto("000", "0", 1, "1", 1), Collections.emptyList());
    private static final Pattern XML_EMPTY_PATTERN = Pattern.compile("<return>\r?\n?</return>", Pattern.MULTILINE);
    private static final Pattern GOODS_PATTERN = Pattern.compile("^(\\d{10})$");
    @SuppressWarnings("unused")
    private final ResourceLoader resourceLoader;
    private final S3Bucket s3Bucket;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final GoodsRepository goodsRepository;

    private static final ObjectMapper JAVA_TIMEMODULE_XMLMAPPER = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        JavaTimeModule module = new JavaTimeModule();
        module.addDeserializer(LocalDateTime.class, new GodoLocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        ObjectMapper om = new XmlMapper();
        om.enable(SerializationFeature.INDENT_OUTPUT);
        om.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
        om.registerModule(module);
        return om;
    }

    //업데이트 날짜 관리
    private LocalDateTime goodsLastUpdatedAt = LocalDateTime.of(1970, 1, 1, 0, 0);

    @Value("${flabook.shop.logo:https://cdn.flabook.co.kr/commons/logo.png}")
    public String shopLogo;

    @Value("${flabook.shop.domain:http://m.flabook.shop}")
    public String shopDomain;

    @Value("${flabook.shop.urls.home:/index}")
    private String shopHomeUrl;

    @Value("${flabook.shop.urls.cart:/order/cart.php}")
    private String shopCartUrl;

    @Value("${flabook.shop.urls.coupon:/mypage/coupon.php}")
    private String shopCouponListUrl;

    @Value("${flabook.shop.urls.review:/mypage/mypage_goods_review.php}")
    private String shopReviewListUrl;

    @Value("${flabook.shop.urls.wish:/mypage/wish_list.php}")
    private String shopWishListUrl;

    @Value("${flabook.shop.urls.order:/mypage/order_list.php}")
    private String shopOrderListUrl;

    @Value("${flabook.shop.urls.cancer:/mypage/order_list.php?mode=cancel}")
    private String shopCancerListUrl;

    @Value("${flabook.shop.urls.deposit:/mypage/deposit.php}")
    private String shopDepositUrl;

    @Value("${flabook.shop.urls.mileage:/mypage/mileage.php}")
    private String shopMileageUrl;

    @Value("${flabook.shop.urls.qa:/mypage/mypage_goods_qa.php}")
    private String shopQaUrl;

    @Value("${flabook.shop.urls.refund:/mypage/order_list.php?mode=refund}")
    private String shopRefundUrl;

    @Value("${flabook.shop.urls.shipping:/mypage/shipping.php}")
    private String shopShippingUrl;

    @Value("${flabook.shop.urls.recommended_goods:/goods/goods_list.php?cateCd=023}")
    private String shopRecommendedGoodsUrl;

    @Value("${flabook.shop.api.partner_key:JUQzQXklMTYlQkVfJTEzJTk4}")
    private String partnerKey;

    @Value("${flabook.shop.api.key:JTNGY00lRTclQzEwJTBEJUI0JUMwJUE3JTg0SyVBQyVDOSU5QyVENXMlMDMlQ0MlQUElN0MlM0MlQkUlQjclRUZrJUJGcUc4JUJGJTlFJTExJTNBJUUzZQ==}")
    private String apiKey;


    public String generateRedirectUrl(String accessToken, String targetUrl) throws UnsupportedEncodingException {
        return String.format("%s/api/redirect?url=%s&access_token=%s", shopDomain, URLEncoder.encode(targetUrl, "utf-8"), accessToken);
    }

    public String generateGoodsUrl(String id) {
        return shopDomain + ((isGoodsPatterns(id)) ? "/goods/goods_view.php?goodsNo=" + id : "/goods/goods_list_infobank.php?cateCd=" + id);
    }

    public String generateRedirectUrlByResourceId(String accessToken, String resourceId) throws UnsupportedEncodingException {
        int goodsIdx = resourceId.indexOf("goods/");
        if(goodsIdx >= 0) {
            return generateRedirectUrl(accessToken, generateGoodsUrl(resourceId.substring(goodsIdx + 6)));
        } else {
            return generateRedirectUrl(accessToken, generateUrlByName(resourceId));
        }
    }

    //앱에서 도메인 주소를 확인해 맞을 경우 native 화면으로 전화되는걸 막기위해 #을 붙임
    private String getShopHomeUrl() {
        return shopHomeUrl + "#home";
    }

    public String generateUrlByName(String name){
        String suffix;
        switch (name) {
            case "cart" :
                suffix = shopCartUrl;
                break;
            case "coupon" :
                suffix = shopCouponListUrl;
                break;
            case "wish" :
                suffix = shopWishListUrl;
                break;
            case "review" :
                suffix = shopReviewListUrl;
                break;
            case "order" :
                suffix = shopOrderListUrl;
                break;
            case "cancel" :
                suffix = shopCancerListUrl;
                break;
            case "deposit" :
                suffix = shopDepositUrl;
                break;
            case "qa" :
                suffix = shopQaUrl;
                break;
            case "recommended" :
                suffix = shopRecommendedGoodsUrl;
                break;
            case "mileage" :
                suffix = shopMileageUrl;
                break;
            case "refund" :
                suffix = shopRefundUrl;
                break;
            case "shipping" :
                suffix = shopShippingUrl;
                break;
            default :
                //TODO resourceId 가 쇼핑몰 페이지 주소일 경우 처리 추가.
                if(name.startsWith("http")) {
                    //웹 페이지 주소일 경우
                    suffix = name;
                } else {
                    suffix = getShopHomeUrl();
                }
                break;
        }

        String targetUrl;
        if(!suffix.contains("http")) {
            targetUrl = shopDomain + suffix;
        } else {
            targetUrl = suffix;
        }

        return targetUrl;
    }


    private static final Map<MetaKey, MetatagDto> METATAG_MAP = new ConcurrentHashMap<>();

    private static boolean isGoodsPatterns(String id) {
        Matcher matcher = GOODS_PATTERN.matcher(id);
        return matcher.find();
    }

    private String buildPageUrl(String id, boolean isGoods, @SuppressWarnings("SameParameterValue") boolean meta/*, String accessToken*/) throws URISyntaxException {
        URIBuilder b = new URIBuilder(shopDomain + "/api/shops");
        if(isGoods) {
            b.addParameter("goodsno", id);
        } else {
            b.addParameter("catecd", id);
        }

        if(meta) {
            b.addParameter("metatag", "1");
        }
        return b.build().toString();
    }

    private String buildPageUrl(String id, boolean isGoods) throws URISyntaxException {
        return buildPageUrl(id, isGoods, true);
    }

    @Data
    @AllArgsConstructor
    @EqualsAndHashCode(of = {"id", "isGoods"})
    private static class MetaKey {
        private String id;
        private boolean isGoods;
    }

    private MetatagDto goodsMeta(String name, JsonNode jsonNode, String url, MetaKey metakey) {
        if(jsonNode.findValue("goodsNm") != null) {
            name = jsonNode.findValue("goodsNm").asText();
        }

        String image = shopLogo;
        if(jsonNode.findValue("imageUrl") != null) {
            image = jsonNode.findValue("imageUrl").asText();
        }

        String description = name;
        if(jsonNode.findValue("shortDescription") != null) {
            description = jsonNode.findValue("shortDescription").asText();
        }
        return new MetatagDto(name, description, image, url, metakey.id);
    }

    private MetatagDto cateMeta(String name, JsonNode jsonNode, String url, MetaKey metakey) {
        String description = "";
        if(jsonNode.findValue("cateNm") != null && "y".equalsIgnoreCase(jsonNode.findValue("cateDisplayMobileFl").asText())) {
            name = jsonNode.findValue("cateNm").asText();
            description = "모야모샵 - " + name;
        }

        String image = shopLogo;
        return new MetatagDto(name, description, image, url, metakey.id);
    }

    @PostConstruct
    public void postConstruct() {
        getMetaFunction = metakey -> {
            final String errorTitle = (metakey.isGoods) ? "상품정보를 찾을 수 없습니다." : "카테고리를 찾을 수 없습니다.";
            try {
                String url = buildPageUrl(metakey.id, metakey.isGoods);
                JsonNode jsonNode = restTemplate.getForObject(url, JsonNode.class);
                JsonNode code = null;
                if (jsonNode != null) {
                    code = jsonNode.findValue("code");
                }
                assert code != null;
                if("0".equals(code.asText())) {
                    MetatagDto meta;
                    if(metakey.isGoods) {
                        meta = goodsMeta(errorTitle, jsonNode, url, metakey);
                    } else {
                        meta = cateMeta(errorTitle, jsonNode, url, metakey);
                    }
                    return meta;
                }
                return new MetatagDto(errorTitle, "", null, "", metakey.id);
            } catch (Exception e) {
                return new MetatagDto(errorTitle, "", null, "", metakey.id);
            }
        };
    }

    private Function<MetaKey, MetatagDto> getMetaFunction;

    @Cacheable(value = CacheValues.GOODS_METATAG, key = "{#id}")
    public MetatagDto requestSearchOrders(String id)  {
        return METATAG_MAP.computeIfAbsent(new MetaKey(id, isGoodsPatterns(id)), getMetaFunction);
    }

    private String generateTitle(OrderDataDto data) {
        return data.getOrderGoodsNm() + " 이(가) " + data.getOrderStatus().getDescription() + " 되었습니다.";
    }

    private final Map<OrderDataDto.OrderStatus, LocalDateTime> lastUpdatedMap = new ConcurrentHashMap<>();

    @Transactional
    public NotificationDto test(String id)  { //NOSONAR
        for(OrderDataDto.OrderStatus orderStatus : OrderDataDto.OrderStatus.values()) { //NOSONAR

            if(!orderStatus.isUse()) continue;

            OrderResponseDto orderResponseDto = this.requestSearchOrders(orderStatus);

            if(orderResponseDto.getOrderDatas() == null)
                continue;

            LocalDateTime lastUpdated = lastUpdatedMap.computeIfAbsent(orderStatus, orderStatus1 -> ZonedDateTime.now().minusMinutes(10).withZoneSameInstant(ASIA_SEOUL_ZONEID).toLocalDateTime());

            for(OrderDataDto orderData : orderResponseDto.getOrderDatas()) { //NOSONAR
                //최초 설정시 10분 이전 부터 확인함 (중복 발송될 수 있음)

                if(lastUpdated != null && lastUpdated.isAfter(orderData.getModDt())) {
                    log.info("skip second {}, {}", lastUpdated, orderData.getModDt());
                    continue;
                }

                MetatagDto metatagDto = METATAG_MAP.computeIfAbsent(new MetaKey(id, isGoodsPatterns(id)), getMetaFunction);

                //발송 로직
                OrderDto orderDto = OrderDto.builder().id(orderData.getOrderNo()).text(generateTitle(orderData)).owner(userRepository.getOne(1L)).thumbnail(metatagDto.getImage()).build();
                if(orderData.getMemId() == null)
                    continue;

                //
                Matcher matcher = XML_EMPTY_PATTERN.matcher(orderData.getMemId());
                if(!matcher.find())
                    continue;

                if(log.isDebugEnabled()) {
                    log.debug("match : {}, {}", matcher.group(1), matcher.group(2));
                }

                Optional<User> optionalUser = userRepository.findById(Long.parseLong(matcher.group(2)));
                optionalUser.ifPresent(user -> {
                    try {
                        notificationService.afterNewOrder(orderDto, user);
                    } catch (Exception e) {
                        log.error("e", e);
                    }
                });

                lastUpdated = orderData.getModDt();
            }

            if(lastUpdated != null) {
                lastUpdatedMap.put(orderStatus, lastUpdated);
            }

            break;
        }
        return null;
    }



    /**
     *
     * @param path 다운로드 파일 주소
     * @param url 고도몰 이미지 주소
     * @return path
     * @throws IOException 에러
     */
    private String download(String path, String url) throws IOException {
        Path filePath = Paths.get(path);

        if(Files.exists(filePath, LinkOption.NOFOLLOW_LINKS)) {
            log.info("exists dir : {}, {}", filePath.getParent(), filePath);
            return filePath.toString();
        } else {
            InputStream in = new URL(url).openStream(); //NOSONAR
            Path pPath = filePath.getParent();
            Files.createDirectories(pPath);
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        log.info("dir : {}, {}", filePath.getParent(), filePath);
        return filePath.toString();
    }

    /**
     * godomall 이미지 주소
     * @param url url
     * @return 리소스
     */
    private ImageResource urlToS3Bucket(String url) {
        ImageResource resource = null;
        String dir = System.getProperty("user.dir");
        try {
            String key = url.substring(url.indexOf("/data") + 6);
            String prefix = key.substring(0, key.lastIndexOf(DELIMITER));
            String filename = key.substring(key.lastIndexOf(DELIMITER) + 1);
            String path = "static/goodses/" + prefix;
            String fullPath = dir + DELIMITER+ path + DELIMITER + filename;
            String downloadPath = download(fullPath, url);
            s3Bucket.upload(prefix, Files.newInputStream(Paths.get(downloadPath)), filename);
            resource = new ImageResource(key, filename);
        } catch (IOException e) {
            log.error("urlToS3Bucket", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("urlToS3Bucket", e);
        }
        return resource;
    }

    @Transactional
    public void insert(List<GoodsDataDto> goodses) {

        List<String> goodsNos = goodses.stream().map(GoodsDataDto::getGoodsNo).collect(Collectors.toList());
        List<Goods> productList = goodsRepository.findAllById(goodsNos);
        Map<String, Goods> productMap = productList.stream().collect(Collectors.toMap(Goods::getGoodsNo, product -> product));

        for (GoodsDataDto goodsDataDto : goodses) {
            Goods goods = productMap.get(goodsDataDto.getGoodsNo());
            if (goods != null) {
                GoodsEtc etc = goods.getEtc();
                goodsRepository.delete(goods);
                Goods newGoods = GoodsMapper.INSTANCE.of(goodsDataDto);
                newGoods.setGoodsNm(newGoods.getGoodsNm());

                if(goodsDataDto.getMagnifyImageDatas() != null && !goodsDataDto.getMagnifyImageDatas().isEmpty()) {
                    ImageResource imageResource = urlToS3Bucket(goodsDataDto.getMagnifyImageDatas().get(0));
                    newGoods.setImageResource(imageResource);
                }
                newGoods.setEtc(etc);
                goodsRepository.save(newGoods);
            } else {
                goods = GoodsMapper.INSTANCE.of(goodsDataDto);
                if(goodsDataDto.getMagnifyImageDatas() != null && !goodsDataDto.getMagnifyImageDatas().isEmpty()) {
                    ImageResource imageResource = urlToS3Bucket(goodsDataDto.getMagnifyImageDatas().get(0));
                    goods.setImageResource(imageResource);
                }
                goods.setEtc(new GoodsEtc());
                goodsRepository.save(goods);
            }
        }
    }

    /**
     * 고도몰 상품정보 연동
     * @param p page
     * @param reindex  전체 인덱싱여부
     */
    @Transactional
    public void syncGoods(Integer p, boolean reindex) { //NOSONAR

        LocalDate localDate = null;
        String searchDateType;
        if(reindex) {
            goodsLastUpdatedAt = LocalDateTime.of(1970, 1, 1, 0, 0);
            if(p == null)
                goodsRepository.deleteAll();

            searchDateType = "regDt";
        } else {
            localDate = ZonedDateTime.now().withZoneSameInstant(ASIA_SEOUL_ZONEID).toLocalDate();
            searchDateType = "modDt";
        }
        log.info("goods update {}", localDate);

        int page = (p == null) ? 1 : p;
        GoodsResponseDto result;
        do {
            result = this.requestSearchGoods(page, searchDateType, localDate, Optional.empty());
            List<GoodsDataDto> goodses = (result.getGoodsDataDtos() == null) ? Collections.emptyList() : result.getGoodsDataDtos().stream().sorted((o1, o2) -> {

                LocalDateTime a = o1.getModDt();
                if(a == null) {
                    a = o1.getRegDt();
                }

                LocalDateTime b = o2.getModDt();
                if(b == null) {
                    b = o2.getRegDt();
                }

                if(a == null || b == null)
                    return -1;

                return (a.isAfter(b)) ? 1 : -1; //NOSONAR
            }).collect(Collectors.toList());
            List<GoodsDataDto> goodsDataDtos = new ArrayList<>();
            for(GoodsDataDto goodsDataDto : goodses) {
                if(!reindex && goodsLastUpdatedAt.isAfter(goodsDataDto.getModDt()))
                    continue;

                goodsDataDtos.add(goodsDataDto);
                goodsLastUpdatedAt = goodsDataDto.getModDt();
            }

            Runnable runnable = () -> insert(goodsDataDtos);
            runnable.run();

        } while (p == null && Integer.parseInt(result.getHeader().getMaxPage()) > page++);

    }

    public static class MetaScheduleJob {
        //10분마다 갱신
        @Scheduled(cron="${flabook.jobs.meta.cron:0 */10 * * * *}")
        public void worker() {
            log.debug("meta worker start");
            ZonedDateTime now = ZonedDateTime.now();
            for (Map.Entry<MetaKey, MetatagDto> entry : METATAG_MAP.entrySet()) {
                if (entry.getValue().expiresAt.isBefore(now)) {
                    log.debug("expired : {} ({})", entry.getKey(), entry.getValue().expiresAt);
                    METATAG_MAP.remove(entry.getKey());
                } else {
                    log.debug("not expired : {} ({})", entry.getKey(), entry.getValue().expiresAt);
                }
            }
            log.debug("meta worker end");
        }
    }

    /**
     * 메타 스케쥴러
     * @return MetaScheduleJob
     */
    @Bean
    @ConditionalOnProperty(value = "flabook.jobs.meta.enable", matchIfMissing = true, havingValue = "false")
    public MetaScheduleJob metaUpdateScheduledJob() {
        return new MetaScheduleJob();
    }

    //0000-00-00 00:00:00 처리용
    private static class GodoLocalDateTimeDeserializer extends LocalDateTimeDeserializer { // NOSONAR

        private static final long serialVersionUID = 1L;
        private static final DateTimeFormatter DEFAULT_FORMATTER;
        public static final LocalDateTimeDeserializer INSTANCE;

        private GodoLocalDateTimeDeserializer() {
            this(DEFAULT_FORMATTER);
        }

        public GodoLocalDateTimeDeserializer(DateTimeFormatter formatter) {
            super(formatter);
        }

        @SuppressWarnings("unused")
        protected GodoLocalDateTimeDeserializer(LocalDateTimeDeserializer base, Boolean leniency) {
            super(base, leniency);
        }

        static {
            DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            INSTANCE = new GodoLocalDateTimeDeserializer();
        }

        private static final String NULL_DATE_FORMAT = "0000-00-00 00:00:00";

        @Override
        public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException { //NOSONAR
            if (parser.hasTokenId(6)) {
                String string = parser.getText().trim();
                if(NULL_DATE_FORMAT.equals(string)) {
                    return null;
                }

                if (string.length() == 0) {
                    return !this.isLenient() ? this._failForNotLenient(parser, context, JsonToken.VALUE_STRING) : null;
                } else {
                    try {
                        if (this._formatter == DEFAULT_FORMATTER && string.length() > 10 && string.charAt(10) == 'T') {
                            return string.endsWith("Z") ? LocalDateTime.ofInstant(Instant.parse(string), ZoneOffset.UTC) : LocalDateTime.parse(string, DEFAULT_FORMATTER);
                        } else {
                            return LocalDateTime.parse(string, this._formatter);
                        }
                    } catch (DateTimeException var12) {
                        return this._handleDateTimeException(context, var12, string);
                    }
                }
            } else {
                if (parser.isExpectedStartArrayToken()) {
                    JsonToken t = parser.nextToken();
                    if (t == JsonToken.END_ARRAY) {
                        return null;
                    }

                    LocalDateTime result;
                    if ((t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT) && context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                        result = this.deserialize(parser, context);
                        if (parser.nextToken() != JsonToken.END_ARRAY) {
                            this.handleMissingEndArrayForSingle(parser, context);
                        }

                        return result;
                    }

                    if (t == JsonToken.VALUE_NUMBER_INT) {
                        int year = parser.getIntValue();
                        int month = parser.nextIntValue(-1);
                        int day = parser.nextIntValue(-1);
                        int hour = parser.nextIntValue(-1);
                        int minute = parser.nextIntValue(-1);
                        t = parser.nextToken();
                        if (t == JsonToken.END_ARRAY) {
                            result = LocalDateTime.of(year, month, day, hour, minute);
                        } else {
                            int second = parser.getIntValue();
                            t = parser.nextToken();
                            if (t == JsonToken.END_ARRAY) {
                                result = LocalDateTime.of(year, month, day, hour, minute, second);
                            } else {
                                int partialSecond = parser.getIntValue();
                                if (partialSecond < 1000 && !context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
                                    partialSecond *= 1000000;
                                }

                                if (parser.nextToken() != JsonToken.END_ARRAY) {
                                    throw context.wrongTokenException(parser, this.handledType(), JsonToken.END_ARRAY, "Expected array to end");
                                }

                                result = LocalDateTime.of(year, month, day, hour, minute, second, partialSecond);
                            }
                        }

                        return result;
                    }

                    context.reportInputMismatch(this.handledType(), "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT", t);
                }

                if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
                    return (LocalDateTime)parser.getEmbeddedObject();
                } else {
                    if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                        this._throwNoNumericTimestampNeedTimeZone(parser, context);
                    }

                    return this._handleUnexpectedToken(context, parser, "Expected array or string.");
                }
            }
        }
    }



    @Cacheable(value="shopOrders", key="#orderStatus")
    public OrderResponseDto requestSearchOrders(OrderDataDto.OrderStatus orderStatus) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ZonedDateTime zonedDateTime = ZonedDateTime.now().withZoneSameInstant(ASIA_SEOUL_ZONEID).minusMinutes(30);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("partner_key", partnerKey);
        map.add("key", apiKey);
        map.add("dateType", "modify");
        map.add("orderStatus", orderStatus.toString());
        map.add("startDate", zonedDateTime.format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        map.add("endDate", zonedDateTime.format(DateTimeFormatter.ofPattern(DATE_PATTERN)));

        log.info("request : {}", map);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity( "https://openhub.godo.co.kr/godomall5/order/Order_Search.php", request , String.class );
        String body = response.getBody();
        if(log.isTraceEnabled()) log.trace("response : {}", body);

        if(body == null) return EMPTY_ORDER_RESPONSE;

        Matcher macher = XML_EMPTY_PATTERN.matcher(body);
        if(macher.find()) return EMPTY_ORDER_RESPONSE;

        try {
            OrderResponseDto orderResponseDto = JAVA_TIMEMODULE_XMLMAPPER.readValue(response.getBody(), OrderResponseDto.class);
            if(log.isTraceEnabled()) log.trace("parse : {}", orderResponseDto);
            return orderResponseDto;

        } catch (JsonProcessingException e) {
            log.error("parse", e);
        }

        return null;
    }

    @Cacheable(value="shopGoods", key="{#page, #searchDateType, #localDate, #optionalGoodsNo}")
    public GoodsResponseDto requestSearchGoods(int page, String searchDateType, LocalDate localDate, Optional<String> optionalGoodsNo) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("partner_key", partnerKey);
        map.add("key", apiKey);
        map.add("page", String.valueOf(page));

        //공급사 예외없이 수집(map.add("scmNo", "1");)
        if(localDate != null) {
            map.add("searchDateType", searchDateType);
            map.add("startDate", localDate.format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
            map.add("endDate", localDate.format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        }

        optionalGoodsNo.ifPresent(goodsNo -> map.add("goodsNo", goodsNo));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity( "https://openhub.godo.co.kr/godomall5/goods/Goods_Search.php", request , String.class );
        String body = response.getBody();

        if(log.isTraceEnabled()) log.trace("response : {}", body);
        assert body != null;
        Matcher macher = XML_EMPTY_PATTERN.matcher(body);
        if(macher.find()) return EMPTY_GOODS_RESPONSE;

        try {
            GoodsResponseDto data = JAVA_TIMEMODULE_XMLMAPPER.readValue(body, GoodsResponseDto.class);
            if(log.isTraceEnabled()) log.trace("parse : {}", data);
            return data;

        } catch (JsonProcessingException e) {
            log.error("parse", e);
        }

        return EMPTY_GOODS_RESPONSE;
    }

    /**
     * @param user 사용자
     * @return DepositDto
     */
    @Cacheable(value="shopDeposit", key="{#user.id}")
    public DepositDto requestGetDeposit(User user) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(shopDomain + "/api/deposit")
                .queryParam(ACCESS_TOKEN_HEADER, user.getSecurity().getAccessToken());

        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCEPT_HEADER, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<DepositDto> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, DepositDto.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("deposit error user : " + user.getId() ,  e);
            return new DepositDto(BigDecimal.ZERO.toString(), "");
        }
    }

    /**
     * @param user 사용자
     * @return MileageDto
     */
    @Cacheable(value="shopMileage", key="{#user.id}")
    public MileageDto requestGetMileage(User user) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(shopDomain + "/api/mileage")
                .queryParam(ACCESS_TOKEN_HEADER, user.getSecurity().getAccessToken());

        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCEPT_HEADER, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<MileageDto> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, MileageDto.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("mileage error user : " + user.getId() ,  e);
            return new MileageDto(BigDecimal.ZERO.toString(), "");
        }
    }

    /**
     * @param user 사용자
     * @return WalletDto
     */
    @Cacheable(value="shopWallet", key="{#user.id}")
    public WalletDto requestGetWallet(User user) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(shopDomain + "/api/wallet")
                .queryParam(ACCESS_TOKEN_HEADER, user.getSecurity().getAccessToken());

        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCEPT_HEADER, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<WalletDto> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, WalletDto.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("mileage error user : " + user.getId() ,  e);
            return new WalletDto(BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }
}
