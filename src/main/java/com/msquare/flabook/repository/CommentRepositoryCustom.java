package com.msquare.flabook.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.models.Comment;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Slf4j
@Repository
@AllArgsConstructor
public class CommentRepositoryCustom {

    private final EntityManager em;

    @SuppressWarnings("unused")
    public Boolean existComments(long userId, long postingId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Comment> root = query.from(Comment.class);
        query.select(cb.count(root.get("id"))).where().where(
                cb.and(
                        cb.equal(root.get("posting").get("id"), postingId)
                        , cb.equal(root.get("owner").get("id"), userId)
                ));
        return em.createQuery(query).setMaxResults(1).getSingleResult() > 0;
    }

}
