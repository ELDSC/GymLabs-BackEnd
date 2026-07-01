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
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Integer id) {
        return clienteRepository.findById(id);
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
            
            return clienteGuardado;
        }
        return null;
    }
}
