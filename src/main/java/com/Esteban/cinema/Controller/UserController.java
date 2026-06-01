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
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody Users request) {
        Users createdUser = userService.register(request);
        Map<String, Object> response = new HashMap<>();
        response.put("idUser", createdUser.getIdUser());
        response.put("email", createdUser.getEmail());
        response.put("fullName", createdUser.getFullName());
        response.put("role", createdUser.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Users logined = userService.login(request);

        String token = jwtService.generateToken(logined.getEmail(), logined.getIdUser(), logined.getRole());

        Map<String, Object> body = new HashMap<>();
        body.put("token", token);
        body.put("idUser", logined.getIdUser());
        body.put("email", logined.getEmail());
        body.put("fullName", logined.getFullName());
        body.put("role", logined.getRole());

        return ResponseEntity.ok(body);
    }

    @PutMapping()
    public ResponseEntity<Map<String, Object>> update(@RequestAttribute("idUser") Long idUser, @Valid @RequestBody Users request) {
        Users updatedUser = userService.updateUser(request, idUser);
        Map<String, Object> response = new HashMap<>();
        response.put("idUser", updatedUser.getIdUser());
        response.put("email", updatedUser.getEmail());
        response.put("fullName", updatedUser.getFullName());
        response.put("role", updatedUser.getRole());
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<Users> getUserDetails(@RequestAttribute("idUser") Long idUser) {
        Users userDetails = userService.getUserDetails(idUser);
        return ResponseEntity.ok(userDetails);
    }
}