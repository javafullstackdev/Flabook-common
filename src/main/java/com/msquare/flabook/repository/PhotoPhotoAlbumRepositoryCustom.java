package com.msquare.flabook.repository;

import lombok.AllArgsConstructor;
import com.msquare.flabook.models.PhotoAlbum;
import com.msquare.flabook.models.PhotoPhotoAlbum;
import com.msquare.flabook.models.board.Photo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class PhotoPhotoAlbumRepositoryCustom {
    private final EntityManager em;

    @SuppressWarnings("java:S3740")
    public List<PhotoPhotoAlbum> photoAlbumsPhotoList(long albumId, Long sinceId, Long maxId, int count, String orderby, int offset) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery(PhotoPhotoAlbum.class);


        Root root = query.from(PhotoPhotoAlbum.class);

        Path<Long> id = root.get("id");
        Path<PhotoAlbum> photoAlbum = root.get("photoAlbum");
        Path<Photo> photo = root.get("photo");

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.and(
                cb.equal(photoAlbum.get("id"), albumId),
                cb.notEqual(photo.get("isDelete"), true)
        ));

        if(orderby.equals("id")) {
            if (maxId != null && maxId > 0) predicates.add(cb.le(id, maxId));
            if (sinceId != null && sinceId > 0) predicates.add(cb.gt(id, sinceId));
            query.where(predicates.toArray(new Predicate[]{})).orderBy(cb.desc(id));
            return em.createQuery(query).setMaxResults(count).getResultList();
        }
        else{
            query.where(predicates.toArray(new Predicate[]{})).orderBy(cb.desc(photo.get("likeCount")), cb.asc(photo.get("id")));
            return em.createQuery(query).setFirstResult(offset).setMaxResults(count).getResultList();
        }


    }

}
