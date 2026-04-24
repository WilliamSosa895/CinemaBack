package com.Esteban.cinema.Repository;

import com.Esteban.cinema.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    @Query("SELECT e FROM Users e WHERE e.email=:email")
    Optional<Users> findByEmail(String email);
}
