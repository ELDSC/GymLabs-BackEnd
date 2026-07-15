package com.GYMLABS.proyecto.controller;

import com.GYMLABS.proyecto.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/alertas")
public class AlertasController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/enviar")
    public ResponseEntity<?> enviarAlerta(@RequestBody Map<String, Object> request) {
        try {
            String nombreCliente = (String) request.get("nombreCliente");
            Integer diasRestantes = (Integer) request.get("diasRestantes");
            String emailDestino = (String) request.get("emailDestino");

            if (emailDestino == null || emailDestino.isEmpty()) {
                return ResponseEntity.badRequest().body("El cliente no tiene un correo válido.");
            }

            emailService.enviarAlertaVencimiento(emailDestino, nombreCliente, diasRestantes);
            
            return ResponseEntity.ok(Map.of("mensaje", "Correo enviado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al enviar la alerta: " + e.getMessage());
        }
    }
}
