package com.msquare.flabook.service;

import lombok.RequiredArgsConstructor;
import com.msquare.flabook.dto.mapper.RecommendKeywordAdminMapper;
import com.msquare.flabook.enumeration.RecommendKeywordType;
import com.msquare.flabook.models.RecommendKeyword;
import com.msquare.flabook.models.RecommendKeywordAdmin;
import com.msquare.flabook.repository.RecommendKeywordAdminRepository;
import com.msquare.flabook.repository.RecommendKeywordRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendKeywordAdminService {

    private final RecommendKeywordAdminRepository recommendKeywordAdminRepository;
    private final RecommendKeywordRepository recommendKeywordRepository;

    @Transactional
    public RecommendKeywordAdmin createRecommendKeywordAdmin(RecommendKeywordType recommendKeywordType){
        RecommendKeywordAdmin recommendKeywordAdmin = new RecommendKeywordAdmin();
        recommendKeywordAdmin.setType(recommendKeywordType);
        recommendKeywordAdmin.setContent(recommendKeywordType.getName());
        return recommendKeywordAdminRepository.save(recommendKeywordAdmin);
    }

    @Transactional
    public void addRecommendKeywordAdmin(RecommendKeywordAdmin recommendKeywordAdmin, RecommendKeywordType type, List<String> contents){
        for(String content : contents) {
            content = content.replace(" ","");
            content = content.replace("#","");
            if(content.equals("")) continue;

            RecommendKeyword recommendKeyword = new RecommendKeyword(content, type, 1, recommendKeywordAdmin);
            recommendKeywordRepository.save(recommendKeyword);
        }
    }

    @Transactional
    public Map<String, Object> getAllRecommendKeywordAdmin( String searchType, int draw, String query, Pageable pageable) {
        Map<String, Object> map = new HashMap<>();

        List<RecommendKeywordAdmin> recommendKeywordAdminList ;
        List<RecommendKeywordAdmin> recommendKeywordAdminAllList ;
        if(searchType.equals("all")){
            if(query.isEmpty()){
                recommendKeywordAdminList = recommendKeywordAdminRepository.findAll(pageable).toList();
                recommendKeywordAdminAllList = recommendKeywordAdminRepository.findAll();
            }
            else{
                recommendKeywordAdminList = recommendKeywordAdminRepository.findBySearchQuery(query, pageable);
                recommendKeywordAdminAllList = recommendKeywordAdminRepository.findBySearchQuery(query);
            }
        }
        else{
            RecommendKeywordType recommendKeywordType = RecommendKeywordType.valueOf(searchType);
            if(query.isEmpty()){
                recommendKeywordAdminList = recommendKeywordAdminRepository.findBySearchType(recommendKeywordType, pageable);
                recommendKeywordAdminAllList = recommendKeywordAdminRepository.findBySearchType(recommendKeywordType);
            }
            else{
                recommendKeywordAdminList = recommendKeywordAdminRepository.findBySearchQueryAndSearchType(recommendKeywordType, query, pageable);
                recommendKeywordAdminAllList = recommendKeywordAdminRepository.findBySearchQueryAndSearchType(recommendKeywordType, query);
            }
        }

        int cnt = recommendKeywordAdminAllList.size();
        map.put("data", recommendKeywordAdminList.stream().map(RecommendKeywordAdminMapper.INSTANCE::of).collect(Collectors.toList()));
        map.put("totalCnt", cnt);
        map.put("recordsFiltered", cnt);
        map.put("draw",draw);
        return map;
    }

    @Transactional
    public void updateRecommendKeywordAdmin(Long recommendKeywordAdminId, RecommendKeywordType recommendKeywordType, List<String> keywords, List<Long> keywordIds) {

        RecommendKeywordAdmin recommendKeywordAdmin = recommendKeywordAdminRepository.findById(recommendKeywordAdminId).orElse(null);
        if(Objects.isNull(recommendKeywordAdmin)) return;
        if(ObjectUtils.isEmpty(keywordIds)) {
            keywordIds = new ArrayList<>();
        }

        recommendKeywordAdmin.setType(recommendKeywordType);
        recommendKeywordAdmin.setContent(recommendKeywordType.getName());

        for (Long keywordId : keywordIds) { //NOSONAR
            String keyword = keywords.get(0);
            keywords.remove(0);
            keyword = keyword.replace(" ", "");
            keyword = keyword.replace("#", "");

            RecommendKeyword recommendKeyword = recommendKeywordRepository.findById(keywordId).orElse(null);
            if (Objects.isNull(recommendKeyword)) continue;
            if (keyword.equals("")) {
                recommendKeywordRepository.delete(recommendKeyword);
                continue;
            }

            recommendKeyword.setName(keyword);
            recommendKeyword.setType(recommendKeywordType);
            recommendKeywordRepository.save(recommendKeyword);
        }
        addRecommendKeywordAdmin(recommendKeywordAdmin, recommendKeywordType, keywords);
    }

}
