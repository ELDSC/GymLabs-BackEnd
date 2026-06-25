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

    public DashboardDTO getDashboardStats(Integer mes, Integer anio) {
        DashboardDTO dto = new DashboardDTO();
        
        // Total Clientes Activos Global
        dto.setTotalClientes(clienteRepository.countByActivoTrue());
        
        // Membresías Activas Global
        dto.setMembresiasActivas(membresiaRepository.countByEstado(EstadoMembresia.ACTIVA));

        if (mes != null && anio != null) {
            // Lógica para un mes y año específicos (Opción A: dividida por 4 semanas)
            BigDecimal ingresos = pagoRepository.sumIngresosPorMesYAnio(mes, anio);
            dto.setIngresosMes(ingresos != null ? ingresos : BigDecimal.ZERO);

            LocalDate startDate = LocalDate.of(anio, mes, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

            // Gráfico Ingresos (4 semanas)
            List<Pago> pagos = pagoRepository.findByEstadoPagoAndFechaPagoBetween(EstadoPago.COMPLETADO, startDate.minusDays(1), endDate.plusDays(1));
            Map<String, Double> ingresosSemanas = new LinkedHashMap<>();
            ingresosSemanas.put("Semana 1", 0.0);
            ingresosSemanas.put("Semana 2", 0.0);
            ingresosSemanas.put("Semana 3", 0.0);
            ingresosSemanas.put("Semana 4", 0.0);

            for (Pago p : pagos) {
                int day = p.getFechaPago().getDayOfMonth();
                String week = "Semana 4";
                if (day <= 7) week = "Semana 1";
                else if (day <= 14) week = "Semana 2";
                else if (day <= 21) week = "Semana 3";
                
                ingresosSemanas.put(week, ingresosSemanas.get(week) + p.getMonto().doubleValue());
            }

            dto.setGraficoIngresos(ingresosSemanas.entrySet().stream()
                    .map(e -> new ChartData(e.getKey(), e.getValue()))
                    .collect(Collectors.toList()));

            // Gráfico Nuevos Clientes (4 semanas)
            List<Cliente> clientes = clienteRepository.findByActivoTrueAndFechaRegistroBetween(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
            Map<String, Double> clientesSemanas = new LinkedHashMap<>();
            clientesSemanas.put("Semana 1", 0.0);
            clientesSemanas.put("Semana 2", 0.0);
            clientesSemanas.put("Semana 3", 0.0);
            clientesSemanas.put("Semana 4", 0.0);

            for (Cliente c : clientes) {
                int day = c.getFechaRegistro().getDayOfMonth();
                String week = "Semana 4";
                if (day <= 7) week = "Semana 1";
                else if (day <= 14) week = "Semana 2";
                else if (day <= 21) week = "Semana 3";
                
                clientesSemanas.put(week, clientesSemanas.get(week) + 1.0);
            }

            dto.setGraficoNuevosClientes(clientesSemanas.entrySet().stream()
                    .map(e -> new ChartData(e.getKey(), e.getValue()))
                    .collect(Collectors.toList()));

        } else {
            // Lógica actual (últimos 6 meses)
            BigDecimal ingresos = pagoRepository.sumIngresosMesActual();
            dto.setIngresosMes(ingresos != null ? ingresos : BigDecimal.ZERO);

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
            dto.setGraficoIngresos(ingresosPorMes.entrySet().stream()
                    .map(e -> new ChartData(e.getKey(), e.getValue()))
                    .collect(Collectors.toList()));

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
            dto.setGraficoNuevosClientes(clientesPorMes.entrySet().stream()
                    .map(e -> new ChartData(e.getKey(), e.getValue()))
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
