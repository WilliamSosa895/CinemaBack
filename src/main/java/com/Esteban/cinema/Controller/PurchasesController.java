package com.Esteban.cinema.Controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Esteban.cinema.DTO.Request.PurchaseRequest;
import com.Esteban.cinema.DTO.Response.PurchaseResponse;
import com.Esteban.cinema.Service.PurchaseService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/purchases")
public class PurchasesController {
    @Autowired
    private PurchaseService purchaseService;

    @PostMapping()
    public ResponseEntity<Void> createPurchase(@RequestAttribute("idUser") Long idUser, @Valid @RequestBody PurchaseRequest request) {
        purchaseService.savePurchase(request, idUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{idPurchase}")
    public ResponseEntity<PurchaseResponse> getPurchase(@RequestAttribute("idUser") Long idUser, @PathVariable("idPurchase") Long idPurchase) {
        return ResponseEntity.ok(purchaseService.getPurchaseByIdForUser(idPurchase, idUser));
    }

    @GetMapping()
    public ResponseEntity<List<PurchaseResponse>> getAllPurchasesByUser(@RequestAttribute("idUser") Long idUser){
        return ResponseEntity.ok(purchaseService.getAllPurchasesByUser(idUser));
    }
}
