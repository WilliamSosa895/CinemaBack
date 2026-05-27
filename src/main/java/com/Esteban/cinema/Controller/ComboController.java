package com.Esteban.cinema.Controller;

import com.Esteban.cinema.Model.Combo;
import com.Esteban.cinema.Service.ComboService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/combos")
public class ComboController {

    @Autowired
    private ComboService comboService;

    @GetMapping
    public ResponseEntity<List<Combo>> listar() {
        return ResponseEntity.ok(comboService.listar());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Combo> crear(@Valid @RequestBody Combo combo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(comboService.crear(combo));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Combo> editar(@PathVariable Long id, @Valid @RequestBody Combo combo) {
        return ResponseEntity.ok(comboService.editar(id, combo));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        comboService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
