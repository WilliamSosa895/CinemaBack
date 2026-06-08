package com.Esteban.cinema.Controller;

import com.Esteban.cinema.Model.Cards;
import com.Esteban.cinema.Service.CardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardsController {
    @Autowired
    CardService cardService;

    @PostMapping()
    public ResponseEntity<Void> saveCard(@RequestAttribute(value = "idUser", required = false) Long idUser, @Valid @RequestBody Cards request) {
        if (idUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }
        cardService.saveCard(request, idUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@RequestAttribute(value = "idUser", required = false) Long idUser, @PathVariable Long cardId) {
        if (idUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }
        cardService.deleteCard(cardId, idUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<List<Cards>> getCardsByUser(@RequestAttribute(value = "idUser", required = false) Long idUser) {
        if (idUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }
        List<Cards> cards = cardService.findAllByUser(idUser);
        return ResponseEntity.ok(cards);
    }
}
