package com.GYMLABS.proyecto.controller;

import com.GYMLABS.proyecto.model.Empresa;
import com.GYMLABS.proyecto.service.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @GetMapping
    public List<Empresa> listarTodas() {
        return empresaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empresa> buscarPorId(@PathVariable Integer id) {
        return empresaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Empresa> guardar(@RequestBody Empresa empresa) {
        return new ResponseEntity<>(empresaService.guardar(empresa), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empresa> actualizar(@PathVariable Integer id, @RequestBody Empresa empresa) {
        if (!empresaService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        empresa.setIdEmpresa(id);
        return ResponseEntity.ok(empresaService.guardar(empresa));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!empresaService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        empresaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
