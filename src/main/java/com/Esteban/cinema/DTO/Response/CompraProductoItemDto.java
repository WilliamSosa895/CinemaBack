package com.Esteban.cinema.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraProductoItemDto {
    private Long productoId;
    private String nombreProducto;
    private Integer cantidad;
}
