package com.Esteban.cinema.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "combo_productos", uniqueConstraints = {@UniqueConstraint(columnNames = {"combo_id","producto_id"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComboProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "combo_id", nullable = false)
    private Combo combo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;
}
