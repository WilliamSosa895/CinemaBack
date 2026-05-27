package com.Esteban.cinema.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class CompraProductoNotificationService {

    private static final Logger log = LoggerFactory.getLogger(CompraProductoNotificationService.class);

    private final JavaMailSender mailSender;

    public CompraProductoNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void enviarConfirmacionSimple(String to, Long compraId, String total) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Confirmación de compra en dulcería");
            helper.setText(
                    "Tu compra de dulcería fue registrada correctamente.\n" +
                    "Compra: DULCERIA-COMPRA-" + compraId + "\n" +
                    "Total: " + total,
                    false
            );
            mailSender.send(mime);
        } catch (MessagingException e) {
            log.warn("No se pudo preparar el correo de compra {}", compraId, e);
        } catch (Exception e) {
            log.warn("No se pudo enviar el correo de compra {}", compraId, e);
        }
    }
}
