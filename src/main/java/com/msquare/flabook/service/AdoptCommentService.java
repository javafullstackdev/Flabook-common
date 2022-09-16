package com.msquare.flabook.service;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.models.AdoptComment;
import org.apache.lucene.search.Sort;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AdoptCommentService {

    private static final List<String> MATCH_FIELDS = Arrays.asList("relation.posting.title", "relation.posting.posting_text", "relation.posting.comments.comment_not_analyzed_text");
    private final EntityManager em;

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<AdoptComment> findAdopedPostingListByUser(Long userId, Long sinceId, Long maxId, int count, String query) {

        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(AdoptComment.class)
                .get();

        BooleanJunction<?> booleanJunction = qb.bool();
        BooleanJunction<?> keywordQuery = qb.bool();
        if(StringUtils.isNotBlank(query)) {
            keywordQuery.should(qb.keyword().onFields(MATCH_FIELDS.toArray(new String[]{})).matching(query).createQuery());
            booleanJunction.must(keywordQuery.createQuery());
        }

        TimelineJunction.addJunction(booleanJunction, sinceId, maxId);

        booleanJunction.must(qb.keyword().onField("relation.user.id").matching(userId).createQuery());
        Sort sort = qb.sort().byField("id").desc().createSort();
        javax.persistence.Query persistenceQuery =
                fullTextEntityManager.createFullTextQuery(booleanJunction.createQuery(), AdoptComment.class)
                        .setFirstResult(0)
                        .setMaxResults(count).setSort(sort);
        return persistenceQuery.getResultList();
    }

}

