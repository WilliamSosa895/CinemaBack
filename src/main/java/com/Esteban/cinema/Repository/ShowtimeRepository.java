package com.Esteban.cinema.Repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Esteban.cinema.Model.Showtimes;

public interface ShowtimeRepository extends JpaRepository<Showtimes, Long> {

    @Query("SELECT s FROM Showtimes s WHERE s.movie.idMovie= :idMovie AND s.active=true")
    List<Showtimes> findByMovieIdMovieAndActiveTrue(@Param("idMovie")Long idMovie);

    @Query("SELECT s FROM Showtimes s WHERE s.movie.idMovie= :idMovie")
    List<Showtimes> findByMovieIdMovie(@Param("idMovie")Long idMovie);

    boolean existsByRoom_IdRoomAndShowtimeAndActiveTrue(Long idRoom, Timestamp showtime);

    @Query("SELECT s FROM Showtimes s WHERE LOWER(s.movie.title) LIKE LOWER(CONCAT('%', :titleSearch, '%')) AND s.active=true")
    List<Showtimes> findByMovieTitle(@Param("titleSearch")String titleSearch);

}
