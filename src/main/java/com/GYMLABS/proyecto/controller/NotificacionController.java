package com.GYMLABS.proyecto.controller;

import com.GYMLABS.proyecto.model.Notificacion;
import com.GYMLABS.proyecto.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @GetMapping
    public List<Notificacion> listarTodas() {
        return notificacionService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> buscarPorId(@PathVariable Integer id) {
        return notificacionService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Notificacion> guardar(@RequestBody Notificacion notificacion) {
        return new ResponseEntity<>(notificacionService.guardar(notificacion), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notificacion> actualizar(@PathVariable Integer id, @RequestBody Notificacion notificacion) {
        if (!notificacionService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        notificacion.setIdNotificacion(id);
        return ResponseEntity.ok(notificacionService.guardar(notificacion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!notificacionService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        notificacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
