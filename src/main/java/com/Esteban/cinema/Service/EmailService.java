package com.Esteban.cinema.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.google.zxing.WriterException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${mail.from}")
    private String fromEmail;

    private final QrCodeService qrCodeService;
    private final JavaMailSender mailSender;

    public EmailService(QrCodeService qrCodeService, JavaMailSender mailSender) {
        this.qrCodeService = qrCodeService;
        this.mailSender = mailSender;
    }

    private void sendEmail(String to, String subject, String htmlBody, byte[] qrBytes)
            throws MessagingException {

        log.info("Preparing SMTP email to {} with subject {}", to, subject);
        MimeMessage mime = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            if (qrBytes != null && qrBytes.length > 0) {
                ByteArrayResource bar = new ByteArrayResource(qrBytes);
                helper.addInline("qrImage", bar, "image/png");
            }

            mailSender.send(mime);
            log.info("SMTP email sent successfully to {}", to);
        } catch (MailException | jakarta.mail.MessagingException e) {
            log.error("SMTP email failed for {}", to, e);
            throw new MessagingException("Error enviando correo via SMTP", e);
        }
    }

    public void loadHtmlTemplatePurchaseAndSend(String pelicula,
                                                String sala,
                                                String asientos,
                                                String folio,
                                                String total,
                                                String email)
            throws IOException, MessagingException, WriterException {

        ClassPathResource resource =
                new ClassPathResource("templates/email/Purchase.html");

        String html;
        try (var in = resource.getInputStream()) {
            html = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }

        html = html.replace("{{PELICULA}}", pelicula);
        html = html.replace("{{SALA}}", sala);
        html = html.replace("{{ASIENTOS}}", asientos);
        html = html.replace("{{FOLIO}}", folio);
        html = html.replace("{{TOTAL}}", total);

        String qrText = folio;
        byte[] qrBytes = qrCodeService.generateQrPng(qrText, 120, 120);

        sendEmail(email, "Confirmación de compra", html, qrBytes);
    }
}