package com.msquare.flabook.repository;

import com.vividsolutions.jts.geom.Geometry;
import com.msquare.flabook.models.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Comment c where c.id = :id")
    Optional<Tag> findLockOnly(@Param("id") long id);

    Optional<Tag> findByName(String name);

    @Query(value = "select t from Tag t where intersects(t.geometry, :area) = true and tag_type = 2 and visibility = 0")
    List<Tag> findByLocation(@Param("area") Geometry area);

    @Query(value = "select t.id from Tag t where intersects(t.geometry, :area) = true and tag_type = 2 and visibility = 0")
    List<Long> findIdByLocation(@Param("area") Geometry area);

	List<Tag> findByNameLike(String name, Pageable pageable);

    List<Tag> findByPlantId(Long plantId);

    @Modifying
    @Query(value = "delete from Tag t where t.plantId = :plantId")
    void deleteByPlantId(@Param("plantId") Long plantId);
}
