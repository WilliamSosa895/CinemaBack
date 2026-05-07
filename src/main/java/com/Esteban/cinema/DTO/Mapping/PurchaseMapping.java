package com.Esteban.cinema.DTO.Mapping;
import com.Esteban.cinema.DTO.Response.PurchaseResponse;
import com.Esteban.cinema.DTO.Response.SeatsResponse;
import com.Esteban.cinema.Model.Purchases;
import com.Esteban.cinema.Service.QrCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.stream.Collectors;

@Component
public class PurchaseMapping {

    @Autowired
    private SeatMapping seatMapping;
    @Autowired
    private QrCodeService qrCodeService;

    public PurchaseResponse purchaseView(Purchases purchase) {
        PurchaseResponse response = new PurchaseResponse();
        response.setId(purchase.getIdPurchase());
        String folio = "CP-" + purchase.getIdPurchase();
        response.setFolio(folio);
        response.setMovieTitle(purchase.getShowtime().getMovie().getTitle());
        response.setRoomName(purchase.getShowtime().getRoom().getName());
        response.setSeats(seatMapping.buildSeatsResponse(purchase.getSeats())
                .stream()
                .map(SeatsResponse::getSeatNumber)
                .collect(Collectors.joining(", ")));
        response.setTotalAmount(String.format("$%.2f", purchase.getTotalAmount()));

        byte[] qrBytes;
        try {
            qrBytes = qrCodeService.generateQrPng(folio, 200, 200);
        } catch (Exception e) {
            throw new RuntimeException("Error generating QR: " + e.getMessage(), e);
        }
        String qrBase64 = Base64.getEncoder().encodeToString(qrBytes);

        response.setQrImageBase64(qrBase64);

        return response;
    }
}