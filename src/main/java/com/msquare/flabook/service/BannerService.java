package com.msquare.flabook.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.dto.BannerDto;
import com.msquare.flabook.enumeration.BannerStatus;
import com.msquare.flabook.exception.FlabookGlobalException;
import com.msquare.flabook.form.CreateBannerVo;
import com.msquare.flabook.form.UpdateBannerOrderVo;
import com.msquare.flabook.form.UpdateBannerVo;
import com.msquare.flabook.models.*;
import com.msquare.flabook.repository.BannerManagerRepository;
import com.msquare.flabook.repository.BannerRepository;
import com.msquare.flabook.repository.PostingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BannerService {

    private static final ZoneId ASIA_SEOUL_ZONEID = ZoneId.of("Asia/Seoul");

    private final EntityManager em;
    private final BannerRepository bannerRepository;
    private final PostingRepository postingRepository;
    private final BannerManagerRepository bannerManagerRepository;
    private final ImageUploadService imageUploadService;

    public List<BannerDto> findList(int offset, int count, Optional<BannerStatus> optionalStatus) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Banner> query = cb.createQuery(Banner.class);
        Root<Banner> banner = query.from(Banner.class);

        query.select(banner);
        query.orderBy(cb.asc(banner.get("seq")), cb.asc(banner.get("id")));

        final List<Predicate> predicates = new ArrayList<>();
        optionalStatus.ifPresent(status -> predicates.add(cb.equal(banner.get("status"), status)));

        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ASIA_SEOUL_ZONEID);
        predicates.add(cb.greaterThanOrEqualTo(banner.get("end"), now));
        predicates.add(cb.lessThanOrEqualTo(banner.get("start"), now));

        query.where(predicates.toArray(new Predicate[]{}));
        return em.createQuery(query).setFirstResult(offset).setMaxResults(count).getResultList().stream().map(BannerDto::of).collect(Collectors.toList());
    }


    private BannerManager createBannerManager() {
        List<BannerManager> managers = bannerManagerRepository.findAll();
        if(!managers.isEmpty())
            return managers.get(0);

        BannerManager bannerManager = new BannerManager();
        bannerManager.setBanners(new ArrayList<>());
        return bannerManagerRepository.save(bannerManager);
    }

    @Transactional
    public BannerDto createBanner(CreateBannerVo vo) throws FlabookGlobalException, IOException, InterruptedException {

        BannerManager bannerManager = createBannerManager();

        Resource resource;
        if(vo.getResourceType().isBoardType()) {
            Posting posting = postingRepository.findById(Long.parseLong(vo.getResourceId())).orElseThrow(() -> new FlabookGlobalException(FlabookGlobalException.Messages.NOT_FOUND_RESOURCE));
            resource = posting.asResource();
        } else {
            resource = new Resource(vo.getResourceId(), vo.getResourceType(), vo.getResourceId(), vo.getResourceType());
        }



        ImageUploadService.ImageResourceInfo imageResourceInfo = imageUploadService.upload(FolderDatePatterns.BANNERS, vo.getFile());
        Banner banner = Banner.builder()
                .resource(resource)
                .title(vo.getTitle())
                .start(ZonedDateTime.of(vo.getStart(), LocalTime.of(0,0,0), ASIA_SEOUL_ZONEID))
                .end(ZonedDateTime.of(vo.getEnd(), LocalTime.of(23,59,59), ASIA_SEOUL_ZONEID))
                .status(vo.getStatus())
                .manager(bannerManager).imageResource(imageResourceInfo.getImageResource())
                .build();

        bannerManager.getBanners().add(banner);
        return BannerDto.of(bannerRepository.save(banner));
    }

    @Transactional
    public BannerDto updateBanner(long id, UpdateBannerVo vo) throws FlabookGlobalException, IOException, InterruptedException  {
        Banner banner = bannerRepository.findById(id).orElseThrow(() -> new FlabookGlobalException(FlabookGlobalException.Messages.NOT_FOUND_RESOURCE));
        banner.setTitle(vo.getTitle());
        banner.setStart(ZonedDateTime.of(vo.getStart(), LocalTime.of(0,0,0), ASIA_SEOUL_ZONEID));
        banner.setEnd(ZonedDateTime.of(vo.getEnd(), LocalTime.of(23,59,59), ASIA_SEOUL_ZONEID));
        banner.setStatus(vo.getStatus());

        if(vo.getFile() != null && !vo.getFile().isEmpty()) {
            ImageUploadService.ImageResourceInfo imageResourceInfo = imageUploadService.upload(FolderDatePatterns.BANNERS, vo.getFile());
            banner.setImageResource(imageResourceInfo.getImageResource());
        }

        return BannerDto.of(banner);
    }

    @Transactional
    public BannerDto remove(long id) throws FlabookGlobalException {
        Banner banner = bannerRepository.findById(id).orElseThrow(() -> new FlabookGlobalException(FlabookGlobalException.Messages.NOT_FOUND_RESOURCE));
        BannerManager manager = createBannerManager();
        manager.getBanners().remove(banner);
        return BannerDto.of(banner);
    }


    @Transactional
    public void updateBannerOrder(UpdateBannerOrderVo vo) throws FlabookGlobalException {
        if(vo.getId().length != vo.getRankOrder().length) {
            throw new FlabookGlobalException("형식이 올바르지 않습니다.");
        }

        BannerManager manager = createBannerManager();
        Map<Long, Integer> orderMap = new HashMap<>();
        for(int i = 0 ; i < vo.getId().length ; i++) {
            orderMap.put(vo.getId()[i], vo.getRankOrder()[i]);
        }

        manager.getBanners().sort((o1, o2) -> {
            Integer no1 = orderMap.getOrDefault(o1.getId(), o1.getSeq());
            Integer no2 = orderMap.getOrDefault(o2.getId(), o2.getSeq());

            if(no1.equals(no2)) {
                return Integer.compare(o1.getSeq(), o2.getSeq());
            }

            return no1.compareTo(no2);
        });

    }

}
