package com.msquare.flabook.repository;

import com.msquare.flabook.models.Home;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HomeRepository extends JpaRepository<Home, Long> {

    @Query("select h from Home h where h.genre=:genre")
    Optional<Home> findByGenre(@Param("genre") String genre);
}
