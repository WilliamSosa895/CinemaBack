package com.Esteban.cinema.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.Esteban.cinema.Model.Rooms;

public interface RoomRepository extends JpaRepository<Rooms, Long> {

    @Query("SELECT COUNT(r) FROM Rooms r WHERE r.status=true")
    Long countByStatusTrue();
}
