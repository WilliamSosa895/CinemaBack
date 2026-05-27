package com.Esteban.cinema.DTO.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCarritoDto {
    @NotNull
    private Long productoId;

    @Min(1)
    private int cantidad;

    @NotNull
    private BigDecimal precioUnitario;
}
