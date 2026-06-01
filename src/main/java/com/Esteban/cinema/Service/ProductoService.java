package com.Esteban.cinema.Service;

import com.Esteban.cinema.DTO.Request.ProductoRequestDto;
import com.Esteban.cinema.DTO.Response.ProductoDto;
import com.Esteban.cinema.Model.Producto;
import com.Esteban.cinema.Repository.ComboProductoRepository;
import com.Esteban.cinema.Repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ComboProductoRepository comboProductoRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Transactional(readOnly = true)
    public List<ProductoDto> listarActivos() {
        return productoRepository.findAllByCantidadDisponibleGreaterThan(0)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoDto buscarPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        return toDto(producto);
    }

    @Transactional
    public ProductoDto crear(ProductoRequestDto dto, MultipartFile imagen) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        producto.setCantidadDisponible(dto.getCantidadDisponible());

        Producto saved = productoRepository.save(producto);

        if (imagen != null && !imagen.isEmpty()) {
            String imagenUrl = cloudinaryService.subirImagen(imagen, "productos/producto_" + saved.getId());
            saved.setImagenUrl(imagenUrl);
            saved = productoRepository.save(saved);
        }

        return toDto(saved);
    }

    @Transactional
    public ProductoDto editar(Long id, ProductoRequestDto dto, MultipartFile imagen) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        producto.setCantidadDisponible(dto.getCantidadDisponible());

        if (imagen != null && !imagen.isEmpty()) {
            String imagenUrl = cloudinaryService.subirImagen(imagen, "productos/producto_" + producto.getId());
            producto.setImagenUrl(imagenUrl);
        }

        return toDto(productoRepository.save(producto));
    }

    @Transactional
    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        if (comboProductoRepository.existsByProducto_IdAndCombo_ActivoTrue(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El producto pertenece a un combo activo");
        }

        try {
            productoRepository.delete(producto);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El producto pertenece a un combo activo", ex);
        }
    }

    @Transactional
    public void actualizarInventario(Long id, int delta) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        int nuevoStock = producto.getCantidadDisponible() + delta;
        if (nuevoStock < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El inventario no puede quedar en negativo");
        }

        producto.setCantidadDisponible(nuevoStock);
        productoRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public Producto getProductoEntidad(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    private ProductoDto toDto(Producto producto) {
        return ProductoDto.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .precio(producto.getPrecio())
                .cantidadDisponible(producto.getCantidadDisponible())
                .imagenUrl(producto.getImagenUrl())
                .build();
    }
}
