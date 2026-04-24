package com.Esteban.cinema.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.Esteban.cinema.Model.Seats;

public interface SeatRepository extends JpaRepository<Seats, Long> {
    @Query("SELECT s FROM Seats s WHERE s.showtime.idShowtime = :showtimeId")
    List<Seats> findByShowtime(Long showtimeId);
}
