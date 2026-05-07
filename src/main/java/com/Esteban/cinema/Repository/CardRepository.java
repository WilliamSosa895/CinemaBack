package com.Esteban.cinema.Repository;

import com.Esteban.cinema.Model.Cards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Cards, Long> {
    @Query("SELECT c FROM Cards c WHERE c.user.idUser=:idUser")
    List<Cards> findAllByUser(@Param("idUser")Long idUser);

    @Query("SELECT c FROM Cards c WHERE c.idCard=:idCard AND c.user.idUser=:idUser")
    Optional<Cards> findByIdCardAndIdUser(@Param("idCard")Long idCard, @Param("idUser")Long idUser);
}
