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
    public List<Personal> listarTodos(@RequestParam(defaultValue = "1") Integer empresaId) {
        return personalService.listarPorEmpresa(empresaId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Personal> buscarPorId(@PathVariable Integer id) {
        return personalService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestParam(defaultValue = "1") Integer empresaId, @RequestBody com.GYMLABS.proyecto.dto.PersonalDto dto) {
        try {
            Personal guardado = personalService.guardarDesdeDto(dto, empresaId, false);
            return new ResponseEntity<>(guardado, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestParam(defaultValue = "1") Integer empresaId, @RequestBody com.GYMLABS.proyecto.dto.PersonalDto dto) {
        if (personalService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            dto.setIdPersonal(id);
            Personal guardado = personalService.guardarDesdeDto(dto, empresaId, true);
            return ResponseEntity.ok(guardado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (personalService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        personalService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
