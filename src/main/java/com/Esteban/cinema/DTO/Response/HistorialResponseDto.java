package com.Esteban.cinema.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialResponseDto {
    private List<PurchaseResponse> boletos;
    private List<CompraProductoDto> dulceria;
}
