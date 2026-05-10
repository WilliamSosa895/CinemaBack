package com.Esteban.cinema.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Esteban.cinema.Model.Movies;

public interface MovieRepository extends JpaRepository<Movies, Long> {
    @Query("SELECT m FROM Movies m WHERE m.active=true")
    List<Movies> findAllIfActivate();

    @Query("SELECT m FROM Movies m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%')) AND m.active = true")
    List<Movies> findByTitle(@Param("title") String title);
}

