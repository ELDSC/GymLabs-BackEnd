package com.GYMLABS.proyecto.controller;

import com.GYMLABS.proyecto.model.Boleta;
import com.GYMLABS.proyecto.service.BoletaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boletas")
public class BoletaController {

    @Autowired
    private BoletaService boletaService;

    @GetMapping
    public List<Boleta> listarTodas() {
        return boletaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Boleta> buscarPorId(@PathVariable Integer id) {
        return boletaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Boleta> guardar(@RequestBody Boleta boleta) {
        return new ResponseEntity<>(boletaService.guardar(boleta), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boleta> actualizar(@PathVariable Integer id, @RequestBody Boleta boleta) {
        if (!boletaService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        boleta.setIdBoleta(id);
        return ResponseEntity.ok(boletaService.guardar(boleta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!boletaService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        boletaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
