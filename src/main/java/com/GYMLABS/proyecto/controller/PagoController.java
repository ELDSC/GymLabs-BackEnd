package com.GYMLABS.proyecto.controller;

import com.GYMLABS.proyecto.model.Pago;
import com.GYMLABS.proyecto.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @GetMapping
    public List<Pago> listarTodos() {
        return pagoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pago> buscarPorId(@PathVariable Integer id) {
        return pagoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Pago> guardar(@RequestBody Pago pago) {
        return new ResponseEntity<>(pagoService.guardar(pago), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pago> actualizar(@PathVariable Integer id, @RequestBody Pago pago) {
        if (!pagoService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        pago.setIdPago(id);
        return ResponseEntity.ok(pagoService.guardar(pago));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!pagoService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        pagoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
