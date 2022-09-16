package com.msquare.flabook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.dto.GoodsDto;
import com.msquare.flabook.models.shop.Goods;
import com.msquare.flabook.service.elasticsearch.CacheValues;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopService extends AbstractSearchIndexer {

    private final EntityManager em;

    private FullTextQuery createQuery(String query, Optional<Boolean> optionalRecommended, String orderBy) {
        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(em);

        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(Goods.class)
                .get();

        BooleanJunction<?> booleanJunction = qb.bool();
        BooleanJunction<?> queryJunction = qb.bool();

        if(query != null && !query.isEmpty()) {
            queryJunction.should(qb.keyword().onField("goodsNm").matching(query).createQuery());
            queryJunction.should(qb.keyword().onField("goodsSearchWord").matching(query).createQuery());
        } else {
            queryJunction.must(qb.all().createQuery());
        }
        booleanJunction.must(qb.keyword().onField("goodsSellFl").matching("y").createQuery());
        booleanJunction.must(qb.keyword().onField("etc.isBlind").matching(false).createQuery());

        optionalRecommended.ifPresent(recommended -> {
            if(BooleanUtils.isTrue(recommended)) {
                booleanJunction.must(qb.keyword().onField("allCateCd").matching("023").createQuery());
            }
        });

        booleanJunction.must(queryJunction.createQuery());
        Query luceneQuery = booleanJunction.createQuery();

        Sort sort;
        if("popular".equals(orderBy)) {
            sort = qb.sort().byScore().desc().createSort();
        } else {
            sort = qb.sort().byField("modDt").desc().createSort();
        }
        return fullTextEntityManager.createFullTextQuery(luceneQuery, Goods.class).setSort(sort);
    }



    @Transactional(readOnly = true)
    @Cacheable(value=CacheValues.GOODS_SEARCH, key = "{#offset, #count, #query, #optionalRecommended, #orderBy}", condition = "#offset==0 && #count==10")
    public List<GoodsDto> searchList(int offset, int count, String query, Optional<Boolean> optionalRecommended, String orderBy) {
        FullTextQuery fullTextQuery = createQuery(query, optionalRecommended, orderBy);
        fullTextQuery.setFirstResult(offset);
        fullTextQuery.setMaxResults(count);
        @SuppressWarnings("unchecked")
        List<Goods> goods = fullTextQuery.getResultList();
        return goods.stream().map(GoodsDto::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value=CacheValues.GOODS_SEARCH_RESULT_COUNT, key = "{#query, #optionalRecommended, #orderBy}")
    public Integer searchResultCount(String query, Optional<Boolean> optionalRecommended, String orderBy) {
        FullTextQuery fullTextQuery = createQuery(query, optionalRecommended, orderBy);
        return fullTextQuery.getResultSize();
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Class<?> getClazz() {
        return Goods.class;
    }
}
