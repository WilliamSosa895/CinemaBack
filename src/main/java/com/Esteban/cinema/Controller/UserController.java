package com.Esteban.cinema.Controller;

import com.Esteban.cinema.Configuration.JwtService;
import com.Esteban.cinema.DTO.Request.LoginRequest;
import com.Esteban.cinema.Model.Users;
import com.Esteban.cinema.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<Void> register(@Valid @RequestBody Users request) {
        userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/signin")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Users logined = userService.login(request);

        String token = jwtService.generateToken(logined.getEmail(), logined.getIdUser(), logined.getRole());

        Map<String, Object> body = new HashMap<>();
        body.put("token", token);
        body.put("role", logined.getRole());

        return ResponseEntity.ok(body);
    }

    @PutMapping()
    public ResponseEntity<Void> update(@RequestAttribute("idUser") Long idUser, @Valid @RequestBody Users request) {
        userService.updateUser(request, idUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<Users> getUserDetails(@RequestAttribute("idUser") Long idUser) {
        Users userDetails = userService.getUserDetails(idUser);
        return ResponseEntity.ok(userDetails);
    }
}