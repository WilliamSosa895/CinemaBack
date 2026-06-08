package com.Esteban.cinema.Service;

import com.Esteban.cinema.DTO.Response.CompraProductoDto;
import com.Esteban.cinema.DTO.Response.HistorialResponseDto;
import com.Esteban.cinema.DTO.Response.PurchaseResponse;
import com.Esteban.cinema.Model.Users;
import com.Esteban.cinema.Repository.CompraProductoRepository;
import com.Esteban.cinema.Repository.PurchaseRepository;
import com.Esteban.cinema.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class HistorialService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private CompraProductoRepository compraProductoRepository;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private CompraProductoService compraProductoService;

    @Transactional(readOnly = true)
    public HistorialResponseDto obtenerHistorial(Long usuarioId) {
        Users usuario = userRepository.findById(usuarioId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        List<PurchaseResponse> boletos = purchaseRepository.findByUserOrderByDateDesc(usuario)
                .stream()
                .map(purchaseService::getPurchaseByEntity)
                .toList();

        List<CompraProductoDto> dulceria = compraProductoRepository.findByUsuarioOrderByFechaDesc(usuario)
                .stream()
                .map(compraProductoService::toDtoPublic)
                .toList();

        return HistorialResponseDto.builder()
                .boletos(boletos)
                .dulceria(dulceria)
                .build();
    }
}
