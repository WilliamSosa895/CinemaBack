package com.Esteban.cinema.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class CompraProductoNotificationService {

    private static final Logger log = LoggerFactory.getLogger(CompraProductoNotificationService.class);

    private final EmailService emailService;

    public CompraProductoNotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    public void enviarConfirmacionSimple(String to,
                                         Long compraId,
                                         String total,
                                         String codigoQr,
                                         String productosHtml) {
        try {
            emailService.sendPurchaseTextConfirmation(to, String.valueOf(compraId), total, codigoQr, productosHtml);
        } catch (Exception e) {
            log.warn("No se pudo enviar el correo de compra {}", compraId, e);
        }
    }
}
