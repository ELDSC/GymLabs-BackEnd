package com.GYMLABS.proyecto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarAlertaVencimiento(String emailDestino, String nombreCliente, int diasRestantes) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailDestino);
            message.setSubject("Aviso de Vencimiento de Membresía - GYMLABS");
            message.setText("Hola " + nombreCliente + ",\n\n" +
                    "Te recordamos que tu membresía está por vencer en " + diasRestantes + " días.\n" +
                    "Por favor, acércate a tu gimnasio para renovar tu plan y no perder tus beneficios.\n\n" +
                    "¡Gracias por ser parte de nosotros!");
            
            mailSender.send(message);
            log.info("Correo de alerta enviado exitosamente a {}", emailDestino);
        } catch (Exception e) {
            log.error("Error al enviar correo a {}: {}", emailDestino, e.getMessage());
            throw new RuntimeException("Error al enviar el correo", e);
        }
    }
}
