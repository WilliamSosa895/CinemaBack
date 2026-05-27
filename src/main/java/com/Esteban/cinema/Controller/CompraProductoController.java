package com.Esteban.cinema.Controller;

import com.Esteban.cinema.DTO.Request.ItemCarritoDto;
import com.Esteban.cinema.DTO.Response.CompraProductoDto;
import com.Esteban.cinema.Service.CompraProductoService;
import com.Esteban.cinema.Repository.UserRepository;
import com.Esteban.cinema.Model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras-dulceria")
public class CompraProductoController {

    @Autowired
    private CompraProductoService compraProductoService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<CompraProductoDto> procesarCompra(
            @RequestAttribute("idUser") Long idUser,
            @RequestBody List<ItemCarritoDto> items) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compraProductoService.procesarCompra(idUser, items));
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<CompraProductoDto>> comprasPorUsuario(
            @RequestAttribute("idUser") Long idUser,
            @PathVariable Long id) {
        if (!esAdmin() && !idUser.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(compraProductoService.listarPorUsuario(id));
    }

    private boolean esAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
