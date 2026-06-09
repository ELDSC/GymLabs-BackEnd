package com.GYMLABS.proyecto.service;

import com.GYMLABS.proyecto.dto.ChartData;
import com.GYMLABS.proyecto.dto.DashboardDTO;
import com.GYMLABS.proyecto.model.Cliente;
import com.GYMLABS.proyecto.model.EstadoMembresia;
import com.GYMLABS.proyecto.model.EstadoPago;
import com.GYMLABS.proyecto.model.Pago;
import com.GYMLABS.proyecto.repository.ClienteRepository;
import com.GYMLABS.proyecto.repository.MembresiaRepository;
import com.GYMLABS.proyecto.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private MembresiaRepository membresiaRepository;

    public DashboardDTO getDashboardStats() {
        DashboardDTO dto = new DashboardDTO();
        
        dto.setTotalClientes(clienteRepository.countByActivoTrue());
        BigDecimal ingresos = pagoRepository.sumIngresosMesActual();
        dto.setIngresosMes(ingresos != null ? ingresos : BigDecimal.ZERO);
        dto.setMembresiasActivas(membresiaRepository.countByEstado(EstadoMembresia.ACTIVA));

        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(5).withDayOfMonth(1);
        
        // Grafico Ingresos
        List<Pago> pagos = pagoRepository.findByEstadoPagoAndFechaPagoAfter(EstadoPago.COMPLETADO, sixMonthsAgo.minusDays(1));
        
        Map<String, Double> ingresosPorMes = new LinkedHashMap<>();
        for(int i = 5; i >= 0; i--) {
            LocalDate d = LocalDate.now().minusMonths(i);
            String monthName = d.getMonth().getDisplayName(TextStyle.SHORT, new Locale("es", "ES"));
            ingresosPorMes.put(monthName, 0.0);
        }
        
        for (Pago p : pagos) {
            String monthName = p.getFechaPago().getMonth().getDisplayName(TextStyle.SHORT, new Locale("es", "ES"));
            if (ingresosPorMes.containsKey(monthName)) {
                ingresosPorMes.put(monthName, ingresosPorMes.get(monthName) + p.getMonto().doubleValue());
            }
        }
        
        List<ChartData> chartIngresos = ingresosPorMes.entrySet().stream()
                .map(e -> new ChartData(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        dto.setGraficoIngresos(chartIngresos);

        // Grafico Nuevos Clientes
        List<Cliente> clientes = clienteRepository.findByActivoTrueAndFechaRegistroAfter(sixMonthsAgo.atStartOfDay());
        Map<String, Double> clientesPorMes = new LinkedHashMap<>();
        for(int i = 5; i >= 0; i--) {
            LocalDate d = LocalDate.now().minusMonths(i);
            String monthName = d.getMonth().getDisplayName(TextStyle.SHORT, new Locale("es", "ES"));
            clientesPorMes.put(monthName, 0.0);
        }

        for (Cliente c : clientes) {
            String monthName = c.getFechaRegistro().getMonth().getDisplayName(TextStyle.SHORT, new Locale("es", "ES"));
            if (clientesPorMes.containsKey(monthName)) {
                clientesPorMes.put(monthName, clientesPorMes.get(monthName) + 1.0);
            }
        }

        List<ChartData> chartClientes = clientesPorMes.entrySet().stream()
                .map(e -> new ChartData(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        dto.setGraficoNuevosClientes(chartClientes);

        return dto;
    }
}
