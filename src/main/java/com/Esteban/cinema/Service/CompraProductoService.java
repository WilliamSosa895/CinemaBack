package com.Esteban.cinema.Service;

import com.Esteban.cinema.DTO.Request.ItemCarritoDto;
import com.Esteban.cinema.DTO.Response.CompraProductoItemDto;
import com.Esteban.cinema.DTO.Response.CompraProductoDto;
import com.Esteban.cinema.Model.Combo;
import com.Esteban.cinema.Model.ComboProducto;
import com.Esteban.cinema.Model.CompraProducto;
import com.Esteban.cinema.Model.CompraProductoItem;
import com.Esteban.cinema.Model.Producto;
import com.Esteban.cinema.Model.Users;
import com.Esteban.cinema.Repository.CompraProductoRepository;
import com.Esteban.cinema.Repository.CompraProductoItemRepository;
import com.Esteban.cinema.Repository.ComboProductoRepository;
import com.Esteban.cinema.Repository.ComboRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CompraProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CompraProductoRepository compraProductoRepository;

    @Autowired
    private CompraProductoItemRepository compraProductoItemRepository;

    @Autowired
    private ComboProductoRepository comboProductoRepository;

    @Autowired
    private ComboRepository comboRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompraProductoNotificationService notificationService;

    @Transactional
    public CompraProductoDto procesarCompra(Long usuarioId, List<ItemCarritoDto> items) {
        Map<Long, Integer> requiredByProduct = new HashMap<>();

        for (ItemCarritoDto item : items) {
            if (item.getTipo() != null && item.getTipo().equalsIgnoreCase("combo")) {
                Combo combo = comboRepository.findById(item.getProductoId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Combo no encontrado"));

                List<ComboProducto> componentes = comboProductoRepository.findByCombo_Id(combo.getId());
                if (componentes == null || componentes.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Combo sin productos");
                }

                for (ComboProducto componente : componentes) {
                    Long productId = componente.getProducto().getId();
                    int requiredQuantity = componente.getCantidad() * item.getCantidad();
                    requiredByProduct.merge(productId, requiredQuantity, Integer::sum);
                }
            } else {
                requiredByProduct.merge(item.getProductoId(), item.getCantidad(), Integer::sum);
            }
        }

        Set<Long> productIds = requiredByProduct.keySet();
        List<Producto> productos = productoRepository.findAllById(productIds);

        if (productos.size() != productIds.size()) {
            Set<Long> found = productos.stream().map(Producto::getId).collect(Collectors.toSet());
            for (Long productId : productIds) {
                if (!found.contains(productId)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado: " + productId);
                }
            }
        }

        for (Producto producto : productos) {
            int requiredQuantity = requiredByProduct.getOrDefault(producto.getId(), 0);
            if (producto.getCantidadDisponible() < requiredQuantity) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente para: " + producto.getNombre());
            }
        }

        for (Producto producto : productos) {
            int requiredQuantity = requiredByProduct.getOrDefault(producto.getId(), 0);
            producto.setCantidadDisponible(producto.getCantidadDisponible() - requiredQuantity);
        }
        productoRepository.saveAll(productos);

        Users usuario = userRepository.getReferenceById(usuarioId);

        CompraProducto compra = new CompraProducto();
        compra.setUsuario(usuario);
        compra.setFecha(Timestamp.from(Instant.now()));
        compra.setTotal(calcularTotal(items));
        compra = compraProductoRepository.save(compra);

        final CompraProducto compraGuardada = compra;
        List<CompraProductoItem> detalleItems = productos.stream()
                .map(producto -> CompraProductoItem.builder()
                        .compraProducto(compraGuardada)
                        .producto(producto)
                        .cantidad(requiredByProduct.getOrDefault(producto.getId(), 0))
                        .build())
                .toList();

        compraProductoItemRepository.saveAll(detalleItems);

        String productosHtml = detalleItems.stream()
                .map(item -> "<li style=\"margin:0 0 6px;\"><strong>"
                        + item.getCantidad() + " x "
                        + escapeHtml(item.getProducto() != null ? item.getProducto().getNombre() : "Producto")
                        + "</strong></li>")
                .collect(Collectors.joining("", "<ul style=\"margin:0;padding-left:18px;\">", "</ul>"));

        String contenido = "DULCERIA-COMPRA-" + compra.getId();
        byte[] qrBytes = generarQR(contenido, 250, 250);
        compra.setCodigoQr(Base64.getEncoder().encodeToString(qrBytes));

        CompraProducto saved = compraProductoRepository.save(compra);

        if (usuario.getEmail() != null && !usuario.getEmail().isBlank()) {
            notificationService.enviarConfirmacionSimple(
                    usuario.getEmail(),
                    saved.getId(),
                    saved.getTotal().toPlainString(),
                    saved.getCodigoQr(),
                    productosHtml
            );
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
        List<CompraProductoItemDto> items = compraProductoItemRepository
            .findByCompraProductoOrderByIdAsc(compra)
            .stream()
            .map(item -> CompraProductoItemDto.builder()
                .productoId(item.getProducto() != null ? item.getProducto().getId() : null)
                .nombreProducto(item.getProducto() != null ? item.getProducto().getNombre() : null)
                .cantidad(item.getCantidad())
                .build())
            .toList();

        return CompraProductoDto.builder()
                .id(compra.getId())
                .usuarioId(compra.getUsuario() != null ? compra.getUsuario().getIdUser() : null)
                .fecha(compra.getFecha())
                .total(compra.getTotal())
                .codigoQr(compra.getCodigoQr())
            .items(items)
                .build();
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    public CompraProductoDto toDtoPublic(CompraProducto compra) {
        return toDto(compra);
    }
}
