package com.GYMLABS.proyecto.controller;

import com.GYMLABS.proyecto.model.Cliente;
import com.GYMLABS.proyecto.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<Page<Cliente>> listarClientes(
            @RequestParam(required = false) Integer empresaId,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "ALL") String filterStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "idCliente"));
        return ResponseEntity.ok(clienteService.listarTodos(empresaId, searchTerm, filterStatus, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Integer id) {
        return clienteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Cliente> guardar(@RequestBody Cliente cliente, @RequestParam(required = false) Integer planId) {
        return new ResponseEntity<>(clienteService.guardar(cliente, planId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable Integer id, @RequestBody Cliente cliente) {
        if (!clienteService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        cliente.setIdCliente(id);
        return ResponseEntity.ok(clienteService.guardar(cliente, null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!clienteService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-estado")
    public ResponseEntity<Cliente> toggleEstado(@PathVariable Integer id, @RequestParam(required = false) Integer planId) {
        Cliente actualizado = clienteService.toggleStatus(id, planId);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actualizado);
    }
}
