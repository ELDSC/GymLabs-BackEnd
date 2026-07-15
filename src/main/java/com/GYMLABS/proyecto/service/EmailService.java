package com.GYMLABS.proyecto.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    public void enviarAlertaVencimiento(String emailDestino, String nombreCliente, int diasRestantes) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(resendApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            // Nota: Para cuentas de prueba de Resend, el "from" DEBE ser onboarding@resend.dev
            // y el "to" DEBE ser tu propio correo registrado en Resend.
            body.put("from", "GYMLABS <onboarding@resend.dev>");
            body.put("to", Collections.singletonList(emailDestino));
            body.put("subject", "Aviso de Vencimiento de Membresía - GYMLABS");
            body.put("html", "<p>Hola <strong>" + nombreCliente + "</strong>,</p>" +
                    "<p>Te recordamos que tu membresía está por vencer en <strong>" + diasRestantes + " días</strong>.</p>" +
                    "<p>Por favor, acércate a tu gimnasio para renovar tu plan y no perder tus beneficios.</p>" +
                    "<br><p>¡Gracias por ser parte de nosotros!</p>");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity("https://api.resend.com/emails", request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Correo de alerta enviado exitosamente a {}", emailDestino);
            } else {
                log.error("Error de Resend al enviar correo a {}: {}", emailDestino, response.getBody());
                throw new RuntimeException("Error en la API de Resend");
            }
        } catch (Exception e) {
            log.error("Error al enviar correo HTTP a {}: {}", emailDestino, e.getMessage());
            throw new RuntimeException("Error al enviar el correo", e);
        }
    }
}
