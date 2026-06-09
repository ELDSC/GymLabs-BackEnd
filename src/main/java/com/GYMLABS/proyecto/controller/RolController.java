package com.GYMLABS.proyecto.controller;

import com.GYMLABS.proyecto.model.Rol;
import com.GYMLABS.proyecto.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping
    public List<Rol> listarTodos() {
        return rolService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rol> buscarPorId(@PathVariable Integer id) {
        return rolService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Rol> guardar(@RequestBody Rol rol) {
        return new ResponseEntity<>(rolService.guardar(rol), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rol> actualizar(@PathVariable Integer id, @RequestBody Rol rol) {
        if (!rolService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        rol.setIdRol(id);
        return ResponseEntity.ok(rolService.guardar(rol));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!rolService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        rolService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
