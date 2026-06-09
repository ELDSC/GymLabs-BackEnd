package com.GYMLABS.proyecto.controller;

import com.GYMLABS.proyecto.model.Sede;
import com.GYMLABS.proyecto.service.SedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sedes")
public class SedeController {

    @Autowired
    private SedeService sedeService;

    @GetMapping
    public List<Sede> listarTodas() {
        return sedeService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sede> buscarPorId(@PathVariable Integer id) {
        return sedeService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Sede> guardar(@RequestBody Sede sede) {
        return new ResponseEntity<>(sedeService.guardar(sede), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sede> actualizar(@PathVariable Integer id, @RequestBody Sede sede) {
        if (!sedeService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        sede.setIdSede(id);
        return ResponseEntity.ok(sedeService.guardar(sede));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!sedeService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        sedeService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
