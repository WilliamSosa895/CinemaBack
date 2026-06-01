package com.Esteban.cinema.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraProductoDto {
    private Long id;
    private Long usuarioId;
    private Timestamp fecha;
    private BigDecimal total;
    private String codigoQr;
    private List<CompraProductoItemDto> items;
}
