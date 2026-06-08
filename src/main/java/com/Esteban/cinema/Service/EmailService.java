package com.Esteban.cinema.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.google.zxing.WriterException;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final Pattern FROM_PATTERN = Pattern.compile("^(.*?)\\s*<([^>]+)>\\s*$");

    @Value("${sendgrid.api.key:}")
    private String sendgridApiKey;

    @Value("${sendgrid.from:}")
    private String sendgridFrom;

    private final QrCodeService qrCodeService;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public EmailService(QrCodeService qrCodeService, ObjectMapper objectMapper) {
        this.qrCodeService = qrCodeService;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    private void sendEmail(String to, String subject, String htmlBody)
            throws IOException {

        if (sendgridApiKey == null || sendgridApiKey.isBlank()) {
            throw new IOException("Missing SendGrid API key");
        }

        sendWithSendGrid(to, subject, htmlBody);
    }

    private void sendWithSendGrid(String to, String subject, String htmlBody) throws IOException {
        log.info("Preparing SendGrid email to {} with subject {}", to, subject);

        ObjectNode payload = objectMapper.createObjectNode();
        ObjectNode fromNode = payload.putObject("from");
        FromAddress parsedFrom = parseFromAddress(sendgridFrom);
        fromNode.put("email", parsedFrom.email());
        if (parsedFrom.name() != null && !parsedFrom.name().isBlank()) {
            fromNode.put("name", parsedFrom.name());
        }

        ObjectNode personalization = payload.putArray("personalizations").addObject();
        personalization.putArray("to").addObject().put("email", to);

        payload.put("subject", subject);
        payload.putArray("content").addObject()
                .put("type", "text/html")
                .put("value", htmlBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.sendgrid.com/v3/mail/send"))
                .timeout(Duration.ofSeconds(20))
                .header("Authorization", "Bearer " + sendgridApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload), StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() != 202) {
                String responseBody = response.body();
                log.error("SendGrid email failed for {}. Status: {} Body: {}", to, response.statusCode(), responseBody);
                throw new IOException("SendGrid API returned status " + response.statusCode());
            }

            log.info("SendGrid email sent successfully to {}", to);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while sending email via SendGrid", e);
        }
    }

    public void loadHtmlTemplatePurchaseAndSend(String pelicula,
                                                String sala,
                                                String asientos,
                                                String folio,
                                                String total,
                                                String email)
            throws IOException, WriterException {

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

        byte[] qrBytes = qrCodeService.generateQrPng(folio, 120, 120);
        String qrDataUri = "data:image/png;base64," + Base64.getEncoder().encodeToString(qrBytes);
        html = html.replace("{{QR_IMAGE}}", qrDataUri);

        sendEmail(email, "Confirmación de compra", html);
    }

    public void sendPurchaseTextConfirmation(String email, String compraId, String total, String codigoQrBase64, String productosHtml) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/email/PurchaseDulceria.html");

        String html;
        try (var in = resource.getInputStream()) {
            html = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }

        html = html.replace("{{COMPRA}}", "DULCERIA-COMPRA-" + compraId);
        html = html.replace("{{TOTAL}}", total);
        html = html.replace("{{PRODUCTOS}}", productosHtml == null || productosHtml.isBlank() ? "<p style=\"margin:0;color:#6b7280;\">Sin productos detallados.</p>" : productosHtml);

        String qrDataUri = "";
        if (codigoQrBase64 != null && !codigoQrBase64.isBlank()) {
            qrDataUri = "data:image/png;base64," + codigoQrBase64;
        }
        html = html.replace("{{QR_IMAGE}}", qrDataUri);

        sendEmail(email, "Confirmación de compra en dulcería", html);
    }

    private FromAddress parseFromAddress(String value) {
        if (value == null || value.isBlank()) {
            return new FromAddress(null, null);
        }

        Matcher matcher = FROM_PATTERN.matcher(value.trim());
        if (matcher.matches()) {
            String name = matcher.group(1) == null ? null : matcher.group(1).trim();
            String email = matcher.group(2).trim();
            return new FromAddress(name, email);
        }

        return new FromAddress(null, value.trim());
    }

    private record FromAddress(String name, String email) {}
}