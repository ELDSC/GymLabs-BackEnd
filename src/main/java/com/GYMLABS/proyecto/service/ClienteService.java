package com.GYMLABS.proyecto.service;

import com.GYMLABS.proyecto.model.Cliente;
import com.GYMLABS.proyecto.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import com.GYMLABS.proyecto.repository.MembresiaRepository;
import com.GYMLABS.proyecto.model.Membresia;
import com.GYMLABS.proyecto.model.EstadoMembresia;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private MembresiaRepository membresiaRepository;

    public List<Cliente> listarTodos() {
        List<Cliente> clientes = clienteRepository.findAll();
        for (Cliente c : clientes) {
            List<Membresia> membresias = membresiaRepository.findByCliente_IdClienteOrderByFechaFinDesc(c.getIdCliente());
            if (!membresias.isEmpty()) {
                c.setFechaVencimiento(membresias.get(0).getFechaFin());
            }
        }
        return clientes;
    }

    public Optional<Cliente> buscarPorId(Integer id) {
        Optional<Cliente> opt = clienteRepository.findById(id);
        opt.ifPresent(c -> {
            List<Membresia> membresias = membresiaRepository.findByCliente_IdClienteOrderByFechaFinDesc(c.getIdCliente());
            if (!membresias.isEmpty()) {
                c.setFechaVencimiento(membresias.get(0).getFechaFin());
            }
        });
        return opt;
    }

    public Cliente guardar(Cliente cliente) {
        if (cliente.getFechaRegistro() == null) {
            cliente.setFechaRegistro(LocalDateTime.now());
        }
        return clienteRepository.save(cliente);
    }

    public void eliminar(Integer id) {
        clienteRepository.deleteById(id);
    }

    public Cliente toggleStatus(Integer id) {
        Optional<Cliente> opt = clienteRepository.findById(id);
        if (opt.isPresent()) {
            Cliente c = opt.get();
            boolean nuevoEstado = c.getActivo() == null ? true : !c.getActivo();
            c.setActivo(nuevoEstado);
            
            Cliente clienteGuardado = clienteRepository.save(c);
            
            // Sincronizar Membresía
            List<Membresia> membresias = membresiaRepository.findByCliente_IdClienteOrderByFechaFinDesc(id);
            if (!membresias.isEmpty()) {
                Membresia ultimaMembresia = membresias.get(0);
                if (nuevoEstado) {
                    // Activar cliente: renovar 30 días desde hoy y poner ACTIVA
                    ultimaMembresia.setEstado(EstadoMembresia.ACTIVA);
                    ultimaMembresia.setFechaFin(LocalDate.now().plusDays(30));
                } else {
                    // Desactivar cliente: poner membresia en VENCIDA
                    ultimaMembresia.setEstado(EstadoMembresia.VENCIDA);
                }
                membresiaRepository.save(ultimaMembresia);
            }
            
            
            List<Membresia> updatedMembresias = membresiaRepository.findByCliente_IdClienteOrderByFechaFinDesc(id);
            if (!updatedMembresias.isEmpty()) {
                clienteGuardado.setFechaVencimiento(updatedMembresias.get(0).getFechaFin());
            }
            return clienteGuardado;
        }
        return null;
    }
}
