package com.GYMLABS.proyecto.controller;

import com.GYMLABS.proyecto.model.Plan;
import com.GYMLABS.proyecto.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planes")
public class PlanController {

    @Autowired
    private PlanService planService;

    @GetMapping
    public List<Plan> listarTodos() {
        return planService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plan> buscarPorId(@PathVariable Integer id) {
        return planService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Plan> guardar(@RequestBody Plan plan) {
        return new ResponseEntity<>(planService.guardar(plan), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Plan> actualizar(@PathVariable Integer id, @RequestBody Plan plan) {
        if (!planService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        plan.setIdPlan(id);
        return ResponseEntity.ok(planService.guardar(plan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!planService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        planService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
