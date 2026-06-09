package com.GYMLABS.proyecto.controller;

import com.GYMLABS.proyecto.model.Personal;
import com.GYMLABS.proyecto.service.PersonalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personal")
public class PersonalController {

    @Autowired
    private PersonalService personalService;

    @GetMapping
    public List<Personal> listarTodos() {
        return personalService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Personal> buscarPorId(@PathVariable Integer id) {
        return personalService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Personal> guardar(@RequestBody Personal personal) {
        return new ResponseEntity<>(personalService.guardar(personal), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Personal> actualizar(@PathVariable Integer id, @RequestBody Personal personal) {
        if (!personalService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        personal.setIdPersonal(id);
        return ResponseEntity.ok(personalService.guardar(personal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!personalService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        personalService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
