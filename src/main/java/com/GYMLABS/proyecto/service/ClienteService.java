package com.GYMLABS.proyecto.service;

import com.GYMLABS.proyecto.model.Cliente;
import com.GYMLABS.proyecto.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.GYMLABS.proyecto.repository.MembresiaRepository;
import com.GYMLABS.proyecto.repository.PlanRepository;
import com.GYMLABS.proyecto.model.Membresia;
import com.GYMLABS.proyecto.model.EstadoMembresia;
import com.GYMLABS.proyecto.model.Pago;
import com.GYMLABS.proyecto.model.EstadoPago;
import com.GYMLABS.proyecto.model.MetodoPago;
import com.GYMLABS.proyecto.repository.PagoRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final MembresiaRepository membresiaRepository;
    private final PlanRepository planRepository;
    private final PagoRepository pagoRepository;

    @Transactional
    public Page<Cliente> listarTodos(Integer empresaId, String searchTerm, String filterStatus, Pageable pageable) {
        LocalDate expiringDate = LocalDate.now().plusDays(10);
        Page<Cliente> page = clienteRepository.buscarClientesConFiltros(empresaId, searchTerm, filterStatus, expiringDate, pageable);
        
        List<Integer> clienteIds = page.getContent().stream()
                .map(Cliente::getIdCliente)
                .collect(Collectors.toList());

        if (!clienteIds.isEmpty()) {
            List<Membresia> latestMembresias = membresiaRepository.findLatestMembresiasByClienteIds(clienteIds);
            Map<Integer, Membresia> membresiaMap = latestMembresias.stream()
                    .collect(Collectors.toMap(m -> m.getCliente().getIdCliente(), m -> m));

            List<Cliente> clientesAModificar = new ArrayList<>();
            List<Membresia> membresiasAModificar = new ArrayList<>();

            for (Cliente c : page.getContent()) {
                Membresia m = membresiaMap.get(c.getIdCliente());
                if (m != null) {
                    c.setFechaVencimiento(m.getFechaFin());
                    // Lazy-evaluation: if membership is expired but client is active, deactivate them
                    if (Boolean.TRUE.equals(c.getActivo()) && m.getFechaFin().isBefore(LocalDate.now())) {
                        c.setActivo(false);
                        m.setEstado(EstadoMembresia.VENCIDA);
                        clientesAModificar.add(c);
                        membresiasAModificar.add(m);
                        log.info("Membresia vencida procesada en batch para el cliente ID: {}", c.getIdCliente());
                    }
                }
            }
            
            if (!clientesAModificar.isEmpty()) {
                clienteRepository.saveAll(clientesAModificar);
                membresiaRepository.saveAll(membresiasAModificar);
            }
        }
        return page;
    }

    @Transactional(readOnly = true)
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

    @Transactional
    public Cliente guardar(Cliente cliente, Integer planId) {
        if (cliente.getFechaRegistro() == null) {
            cliente.setFechaRegistro(LocalDateTime.now());
        }
        
        Cliente saved = clienteRepository.save(cliente);
        
        if (planId != null) {
            asignarNuevoPlan(saved, planId);
            saved = clienteRepository.save(saved);
        }
        
        return saved;
    }

    private void asignarNuevoPlan(Cliente cliente, Integer planId) {
        Integer idPlanAUsar = planId != null ? planId : 1;
        planRepository.findById(idPlanAUsar).ifPresent(p -> {
            Membresia nueva = new Membresia();
            nueva.setCliente(cliente);
            nueva.setEstado(EstadoMembresia.ACTIVA);
            nueva.setFechaInicio(LocalDate.now());
            nueva.setPlan(p);
            nueva.setFechaFin(LocalDate.now().plusMonths(p.getDuracionMeses()));
            Membresia membresiaGuardada = membresiaRepository.save(nueva);
            
            // Generar el pago automáticamente en estado PENDIENTE
            Pago nuevoPago = new Pago();
            nuevoPago.setFechaPago(LocalDate.now());
            nuevoPago.setMonto(p.getPrecio() != null ? p.getPrecio() : java.math.BigDecimal.ZERO);
            nuevoPago.setMetodoPago(MetodoPago.EFECTIVO);
            nuevoPago.setEstadoPago(EstadoPago.PENDIENTE);
            nuevoPago.setMembresia(membresiaGuardada);
            pagoRepository.save(nuevoPago);
            
            cliente.setActivo(true);
            cliente.setFechaVencimiento(nueva.getFechaFin());
        });
    }

    @Transactional
    public void eliminar(Integer id) {
        clienteRepository.deleteById(id);
    }

    @Transactional
    public Cliente toggleStatus(Integer id, Integer planId) {
        Optional<Cliente> opt = clienteRepository.findById(id);
        if (opt.isPresent()) {
            Cliente c = opt.get();
            boolean nuevoEstado = c.getActivo() == null ? true : !c.getActivo();
            c.setActivo(nuevoEstado);
            
            Cliente clienteGuardado = clienteRepository.save(c);
            
            if (nuevoEstado) {
                asignarNuevoPlan(clienteGuardado, planId);
                clienteGuardado = clienteRepository.save(clienteGuardado);
            } else {
                // Al desactivar, marcar la membresía actual (si existe) como VENCIDA
                List<Membresia> membresias = membresiaRepository.findByCliente_IdClienteOrderByFechaFinDesc(id);
                if (!membresias.isEmpty()) {
                    Membresia ultimaMembresia = membresias.get(0);
                    ultimaMembresia.setEstado(EstadoMembresia.VENCIDA);
                    membresiaRepository.save(ultimaMembresia);
                    clienteGuardado.setFechaVencimiento(ultimaMembresia.getFechaFin());
                }
            }
            
            log.info("Estado del cliente ID: {} cambiado a {}", c.getIdCliente(), nuevoEstado);
            return clienteGuardado;
        }
        return null;
    }
}
