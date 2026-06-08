package com.Esteban.cinema.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "estrenos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estreno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "fecha_estreno", nullable = false)
    private LocalDate fechaEstreno;

    @Column(name = "sinopsis")
    private String sinopsis;

    @Column(name = "imagen_url")
    private String imagenUrl;
}
