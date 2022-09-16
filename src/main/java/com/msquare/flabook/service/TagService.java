package com.msquare.flabook.service;

import com.vividsolutions.jts.geom.Geometry;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.dto.TagAdminDto;
import com.msquare.flabook.dto.TagDto;
import com.msquare.flabook.enumeration.RecommendKeywordType;
import com.msquare.flabook.form.CreateTagByCmsVo;
import com.msquare.flabook.form.CreateTagVo;
import com.msquare.flabook.models.Tag;
import com.msquare.flabook.repository.RecommendKeywordAdminRepository;
import com.msquare.flabook.repository.TagRepository;
import com.msquare.flabook.service.elasticsearch.CacheValues;
import com.msquare.flabook.util.GeoUtils;
import com.msquare.flabook.util.HangleUtil;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class TagService extends AbstractSearchIndexer {

    private final TagRepository tagRepository;
    private final EntityManager em;
    private final RecommendKeywordAdminRepository recommendKeywordAdminRepository;

    @Override
    public Class<?> getClazz() {
        return Tag.class;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @SuppressWarnings("unused")
    public Optional<TagDto> findTag(long id) {
        return tagRepository.findById(id).map(TagDto::of);
    }


    @Transactional
    public TagDto createTag(CreateTagVo vo) {
        Tag tag = tagRepository.findByName(vo.getName()).orElseGet(() -> {
            Tag t = Tag.builder().tagType(vo.getTagType()).visibility(vo.getVisibility()).build();
            t.setName(vo.getName());
            return tagRepository.save(t);
        });
        return TagDto.of(tag);
    }


    private Tag createTagObject(String originalName, String secondName, String plantId) {
        Tag tag = Tag.builder().tagType(Tag.TagType.dictionary)
                .visibility(Tag.Visibility.visible)
                .originalName(originalName)
                .plantId(Long.parseLong(plantId)).build();
        tag.setName(secondName);
        return tag;
    }

    /**
     * 도감관리페이지에 도감이 새로 등록될 때 호출
     *
     * @param plantId plantId
     * @param vo CreateTagByCmsVo
     * @return List<TagDto>
     */
    @Transactional
    public List<TagDto> createTagByPlantId(String plantId, CreateTagByCmsVo vo) {

        tagRepository.deleteByPlantId(Long.parseLong(plantId));

        String originalName = vo.getName();
        String[] secondNames = (vo.getTags() != null) ? vo.getTags().split(";") : new String[]{};

        List<Tag> createTags = new ArrayList<>();
        createTags.add(createTagObject(originalName, originalName, plantId));
        for(String secondName : secondNames) {
            createTags.add(createTagObject(originalName, secondName, plantId));
        }

        return tagRepository.saveAll(createTags).stream().map(TagDto::of).collect(Collectors.toList());
    }

    /**
     * 도감관리페이지에 도감이 상태 변경될 때 호출
     *
     * @param plantId plantId
     * @return List<TagDto>
     */
    @Transactional
    public List<TagDto> removeTagByPlantId(String plantId) {
        List<Tag> tags = tagRepository.findByPlantId(Long.parseLong(plantId));
        tagRepository.deleteAll(tags);
        return tags.stream().map(TagDto::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheValues.TAGS, key = "{#name}")
    public TagDto findTag(String name) {
        return tagRepository.findByName(name).map(TagDto::of).orElse(TagDto.of(name));
    }

    @Transactional
    public TagDto simplify(Long id) {
        return tagRepository.findById(id).map(tag -> {
            Geometry geometry = GeoUtils.simplify(tag.getGeometry(), 10);
                    //tag.getGeometry().getFactory()

            tag.setGeometry(geometry);
            log.info("tag geometry points : {}", geometry.toText());
            return tag;
        }).map(TagDto::of).orElse(new TagDto());
    }


    @Transactional(readOnly = true)
    @Cacheable(value = CacheValues.RECOMMEND_KEYWORDS, key = "{#recommendKeywordType, #offset, #count}")
    public List<String> getRecommendKeywords(RecommendKeywordType recommendKeywordType, int offset, int count) {

        if(recommendKeywordType != null) {
            return recommendKeywordAdminRepository.findRecentlyKeyword(recommendKeywordType.name());
        } else {
            return recommendKeywordAdminRepository.findRecentlyKeyword();
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<String> search(int offset, int count, String query) {

        FullTextEntityManager fullTextEntityManager =
                org.hibernate.search.jpa.Search.getFullTextEntityManager(em);

        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(Tag.class).get();

        org.apache.lucene.search.Query luceneQuery;
        if (StringUtils.isNotBlank(query)) {

            String alphabet = HangleUtil.hangulToAlphabet(query);
            alphabet = alphabet.replaceAll("[^0-9a-zA-Z]", "");
            if (3 > alphabet.length())
                return Collections.emptyList();

            luceneQuery =
                    qb.bool()
                            .must(qb.bool()
                                    .must(qb.keyword()
                                            .onField("nameAlphabet").matching(alphabet).createQuery())
                                    .must(qb.keyword().onField("visibility")
                                            .matching(Tag.Visibility.visible).createQuery())
                                    .createQuery())
                            .should(qb.keyword().onField("name").boostedTo(2).matching(query.toLowerCase()).createQuery())
                            .createQuery();
        } else {
            luceneQuery = qb.all().createQuery();
        }

        org.hibernate.search.jpa.FullTextQuery jpaQuery =
                fullTextEntityManager.createFullTextQuery(luceneQuery, Tag.class);
        jpaQuery.setProjection("name");
        jpaQuery.setResultTransformer(CriteriaSpecification.PROJECTION);
        jpaQuery.setFirstResult(offset);
        jpaQuery.setMaxResults(count);
        // execute search
        return jpaQuery.getResultList();
    }

    /*
     * For Admin
     */
    public List<TagAdminDto> findAll(Pageable pageable) {
		List<Tag> tagList = tagRepository.findAll(pageable).toList();
		return tagList.stream().map(TagAdminDto::of).collect(Collectors.toList());
	}

    public List<TagAdminDto> findByNameLike(String query , Pageable pageable) {
    	List<Tag> tagList = tagRepository.findByNameLike(query, pageable);
    	return tagList.stream().map(TagAdminDto::of).collect(Collectors.toList());
    }

    public Long getTagCount() {
    	return tagRepository.count();
    }

}
