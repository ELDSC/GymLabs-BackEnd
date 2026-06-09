package com.GYMLABS.proyecto.controller;

import com.GYMLABS.proyecto.model.Membresia;
import com.GYMLABS.proyecto.service.MembresiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membresias")
public class MembresiaController {

    @Autowired
    private MembresiaService membresiaService;

    @GetMapping
    public List<Membresia> listarTodas() {
        return membresiaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Membresia> buscarPorId(@PathVariable Integer id) {
        return membresiaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Membresia> guardar(@RequestBody Membresia membresia) {
        return new ResponseEntity<>(membresiaService.guardar(membresia), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Membresia> actualizar(@PathVariable Integer id, @RequestBody Membresia membresia) {
        if (!membresiaService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        membresia.setIdMembresia(id);
        return ResponseEntity.ok(membresiaService.guardar(membresia));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!membresiaService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        membresiaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
