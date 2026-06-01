package com.Esteban.cinema.Repository;

import com.Esteban.cinema.Model.ComboProducto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ComboProductoRepository extends JpaRepository<ComboProducto, Long> {
	@Query("SELECT CASE WHEN COUNT(cp) > 0 THEN true ELSE false END FROM ComboProducto cp WHERE cp.producto.id = :productoId AND cp.combo.activo = true")
	boolean existsByProducto_IdAndCombo_ActivoTrue(@Param("productoId") Long productoId);

    List<ComboProducto> findByCombo_Id(Long comboId);
}

