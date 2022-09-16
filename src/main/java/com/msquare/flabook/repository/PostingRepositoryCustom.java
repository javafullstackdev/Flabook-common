package com.msquare.flabook.repository;

import lombok.AllArgsConstructor;
import com.msquare.flabook.enumeration.PostingType;
import com.msquare.flabook.models.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class PostingRepositoryCustom {

    private final EntityManager em;

    public List<ScrapPosting> crippingList(PostingType postingType, long userId, Long sinceId, Long maxId, int count) {
        return list(ScrapPosting.class, postingType, userId, sinceId, maxId, count);
    }

    public List<LikePosting> likeList(PostingType postingType, long userId, Long sinceId, Long maxId, int count) {
        return list(LikePosting.class, postingType, userId, sinceId, maxId, count);
    }

    public List<AdoptComment> adoptedList(PostingType postingType, long userId, Long sinceId, Long maxId, int count) {
        return list(AdoptComment.class, postingType, userId, sinceId, maxId, count);
    }

    private <T extends IUserPostingActivity> List<T> list(Class<T> clazz1, PostingType postingType, long userId, Long sinceId, Long maxId, int count) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz1);

        Root<T> likeRoot = query.from(clazz1);

        Path<Long> id = likeRoot.get("id");
        Path<UserPostingRelation> relation = likeRoot.get("relation");

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.and(
                cb.equal(relation.get("user").get("id"), userId),
                cb.notEqual(likeRoot.get("relation").get("posting").get("isDelete"), true)
        ));

        if (postingType != null)
            predicates.add(cb.equal(likeRoot.get("postingType"), postingType));

        if (maxId != null && maxId > 0) predicates.add(cb.le(id, maxId));
        if (sinceId != null && sinceId > 0) predicates.add(cb.gt(id, sinceId));

        query.where(predicates.toArray(new Predicate[]{})).orderBy(cb.desc(id));

        return em.createQuery(query).setMaxResults(count).getResultList();
    }


}
