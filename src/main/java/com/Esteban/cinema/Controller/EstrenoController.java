package com.Esteban.cinema.Controller;

import com.Esteban.cinema.DTO.Response.EstrenoDto;
import com.Esteban.cinema.Service.EstrenoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/estrenos")
public class EstrenoController {

    @Autowired
    private EstrenoService estrenoService;

    @GetMapping
    public ResponseEntity<List<EstrenoDto>> listarTodos() {
        return ResponseEntity.ok(estrenoService.listarTodos());
    }

    @GetMapping("/destacados")
    public ResponseEntity<List<EstrenoDto>> listarDestacados() {
        return ResponseEntity.ok(estrenoService.listarDestacados());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstrenoDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(estrenoService.buscarPorId(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstrenoDto> crear(
            @Valid @RequestPart("dto") EstrenoDto dto,
            @RequestPart("imagen") MultipartFile imagen) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estrenoService.crear(dto, imagen));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstrenoDto> editar(
            @PathVariable Long id,
            @Valid @RequestPart("dto") EstrenoDto dto,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) {
        return ResponseEntity.ok(estrenoService.editar(id, dto, imagen));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        estrenoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
