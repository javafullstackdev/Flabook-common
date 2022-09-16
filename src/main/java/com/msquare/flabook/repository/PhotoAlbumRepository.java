package com.msquare.flabook.repository;

import com.msquare.flabook.models.PhotoAlbum;
import com.msquare.flabook.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PhotoAlbumRepository extends JpaRepository<PhotoAlbum, Long> {
    @Query("select pa from PhotoAlbum pa where pa.user = :user and pa.name = :name")
    Optional<PhotoAlbum> findPhotoAlbumByName(@Param("user")User currentUser, @Param("name")String name);

    @Modifying
    @Query(value = "UPDATE PhotoAlbum pa set pa.photoCnt = pa.photoCnt + 1 where pa.id = :id")
    int updateIncrementPhotoCnt(@Param("id") Long id);

    @Modifying
    @Query(value = "UPDATE PhotoAlbum pa set pa.photoCnt = pa.photoCnt - 1 where pa.id = :id")
    int updateDecrementPhotoCnt(@Param("id") Long id);

    @Query("select pa from PhotoAlbum pa where pa.user = :user and pa.name = :name ")
    List<PhotoAlbum> findPhotoAlbumListByName(@Param("user") User user, @Param("name") String name);

    @Query("select pa from PhotoAlbum pa where pa.user = :user and pa.name like concat(:name,'%') ")
    List<PhotoAlbum> findPhotoAlbumListByNameContains(@Param("user") User user, @Param("name") String name);

    @Query("select pa from PhotoAlbum pa where pa.user = :user and pa.name like concat('%',:query,'%')")
    List<PhotoAlbum> findPhotoAlbumByUser(@Param("user") User user, @Param("query") String query, Pageable pageable);

    @Query("select pa from PhotoAlbum pa where pa.user = :user and pa.name like concat('%',:query,'%')")
    List<PhotoAlbum> findPhotoAlbumByUser(@Param("user") User user, @Param("query") String query);
}
