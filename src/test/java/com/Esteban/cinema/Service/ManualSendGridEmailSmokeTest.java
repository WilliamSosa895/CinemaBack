package com.Esteban.cinema.Service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class ManualSendGridEmailSmokeTest {

    @Autowired
    private EmailService emailService;

    @Test
    void sendSendGridConfirmationEmail() {
        assertDoesNotThrow(() -> emailService.sendPurchaseTextConfirmation(
            "williamsosa2703@gmail.com",
            "LOCAL-SMOKE-1",
            "150.00",
            null,
            "<p>Prueba de productos</p>"
        ));
    }
}