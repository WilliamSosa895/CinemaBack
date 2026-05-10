package com.Esteban.cinema.Repository;
import com.Esteban.cinema.Model.Purchases;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchases, Long> {

    @Query("SELECT p FROM Purchases p WHERE p.user.idUser=:idUser")
    List<Purchases> getAllPurchasesByUser(@Param("idUser")Long idUser);
}