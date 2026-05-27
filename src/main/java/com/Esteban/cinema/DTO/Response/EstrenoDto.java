package com.Esteban.cinema.DTO.Response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstrenoDto {
    private Long id;

    @NotBlank
    private String titulo;

    @NotNull
    private LocalDate fechaEstreno;

    private String sinopsis;

    private String imagenUrl;
}
