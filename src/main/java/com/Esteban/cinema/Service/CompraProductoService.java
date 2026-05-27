package com.Esteban.cinema.Service;

import com.Esteban.cinema.DTO.Request.ItemCarritoDto;
import com.Esteban.cinema.DTO.Response.CompraProductoDto;
import com.Esteban.cinema.Model.CompraProducto;
import com.Esteban.cinema.Model.Producto;
import com.Esteban.cinema.Model.Users;
import com.Esteban.cinema.Repository.CompraProductoRepository;
import com.Esteban.cinema.Repository.ProductoRepository;
import com.Esteban.cinema.Repository.UserRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Service
public class CompraProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CompraProductoRepository compraProductoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompraProductoNotificationService notificationService;

    @Transactional
    public CompraProductoDto procesarCompra(Long usuarioId, List<ItemCarritoDto> items) {
        for (ItemCarritoDto item : items) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
            if (producto.getCantidadDisponible() < item.getCantidad()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Stock insuficiente para: " + producto.getNombre());
            }
        }

        for (ItemCarritoDto item : items) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
            producto.setCantidadDisponible(producto.getCantidadDisponible() - item.getCantidad());
            productoRepository.save(producto);
        }

        Users usuario = userRepository.getReferenceById(usuarioId);

        CompraProducto compra = new CompraProducto();
        compra.setUsuario(usuario);
        compra.setFecha(Timestamp.from(Instant.now()));
        compra.setTotal(calcularTotal(items));
        compra = compraProductoRepository.save(compra);

        String contenido = "DULCERIA-COMPRA-" + compra.getId();
        byte[] qrBytes = generarQR(contenido, 250, 250);
        compra.setCodigoQr(Base64.getEncoder().encodeToString(qrBytes));

        CompraProducto saved = compraProductoRepository.save(compra);

        if (usuario.getEmail() != null && !usuario.getEmail().isBlank()) {
            notificationService.enviarConfirmacionSimple(usuario.getEmail(), saved.getId(), saved.getTotal().toPlainString());
        }

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<CompraProductoDto> listarPorUsuario(Long usuarioId) {
        Users usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return compraProductoRepository.findByUsuarioOrderByFechaDesc(usuario)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private BigDecimal calcularTotal(List<ItemCarritoDto> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCarritoDto item : items) {
            total = total.add(item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())));
        }
        return total;
    }

    private byte[] generarQR(String contenido, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(contenido, BarcodeFormat.QR_CODE, width, height);
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
                return baos.toByteArray();
            }
        } catch (WriterException | IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generando QR", e);
        }
    }

    private CompraProductoDto toDto(CompraProducto compra) {
        return CompraProductoDto.builder()
                .id(compra.getId())
                .usuarioId(compra.getUsuario() != null ? compra.getUsuario().getIdUser() : null)
                .fecha(compra.getFecha())
                .total(compra.getTotal())
                .codigoQr(compra.getCodigoQr())
                .build();
    }
}
