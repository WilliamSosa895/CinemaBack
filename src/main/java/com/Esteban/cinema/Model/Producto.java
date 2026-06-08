package com.Esteban.cinema.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    @NotBlank
    private String nombre;

    @Column(name = "precio", nullable = false)
    @NotNull
    private BigDecimal precio;

    @Column(name = "cantidad_disponible", nullable = false)
    @NotNull
    private Integer cantidadDisponible;

    @Column(name = "imagen_url")
    private String imagenUrl;
}
