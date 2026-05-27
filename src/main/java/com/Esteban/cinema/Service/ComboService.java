package com.Esteban.cinema.Service;

import com.Esteban.cinema.Model.Combo;
import com.Esteban.cinema.Repository.ComboRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ComboService {

    @Autowired
    private ComboRepository comboRepository;

    @Transactional(readOnly = true)
    public List<Combo> listar() {
        return comboRepository.findAll();
    }

    @Transactional
    public Combo crear(Combo combo) {
        combo.setId(null);
        if (combo.getActivo() == null) {
            combo.setActivo(true);
        }
        return comboRepository.save(combo);
    }

    @Transactional
    public Combo editar(Long id, Combo request) {
        Combo combo = comboRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Combo no encontrado"));

        combo.setNombre(request.getNombre());
        combo.setDescripcion(request.getDescripcion());
        combo.setPrecio(request.getPrecio());
        if (request.getActivo() != null) {
            combo.setActivo(request.getActivo());
        }

        return comboRepository.save(combo);
    }

    @Transactional
    public void eliminar(Long id) {
        Combo combo = comboRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Combo no encontrado"));
        comboRepository.delete(combo);
    }
}
