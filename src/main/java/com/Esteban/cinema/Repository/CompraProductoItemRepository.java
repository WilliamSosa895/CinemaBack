package com.Esteban.cinema.Repository;

import com.Esteban.cinema.Model.CompraProducto;
import com.Esteban.cinema.Model.CompraProductoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompraProductoItemRepository extends JpaRepository<CompraProductoItem, Long> {
    List<CompraProductoItem> findByCompraProductoOrderByIdAsc(CompraProducto compraProducto);
}
