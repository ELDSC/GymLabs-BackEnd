package com.GYMLABS.proyecto.config;

import com.GYMLABS.proyecto.model.*;
import com.GYMLABS.proyecto.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private EmpresaRepository empresaRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private MembresiaRepository membresiaRepository;
    @Autowired
    private PagoRepository pagoRepository;

    @Override
    public void run(String... args) throws Exception {
        if (clienteRepository.count() > 15) {
            System.out.println("La base de datos ya tiene suficientes datos. Seeder omitido.");
            return; // Ya hay datos suficientes
        }

        System.out.println("Iniciando inyección de datos falsos para el Dashboard...");

        // 1. Asegurar Empresa
        Empresa empresa = empresaRepository.findById(1).orElseGet(() -> {
            Empresa e = new Empresa();
            e.setIdEmpresa(1);
            e.setNombre("GymLabs Oficial");
            e.setDireccion("Avenida Principal 123");
            return empresaRepository.save(e);
        });

        // 2. Asegurar Planes
        Plan planBasico = planRepository.findById(1).orElseGet(() -> {
            Plan p = new Plan();
            p.setNombrePlan("Básico");
            p.setDescripcion("Acceso estándar");
            p.setPrecio(new BigDecimal("29.90"));
            p.setDuracionMeses(1);
            p.setEmpresa(empresa);
            return planRepository.save(p);
        });
        Plan planPro = planRepository.findById(2).orElseGet(() -> {
            Plan p = new Plan();
            p.setNombrePlan("Pro");
            p.setDescripcion("Acceso total + Clases");
            p.setPrecio(new BigDecimal("59.90"));
            p.setDuracionMeses(1);
            p.setEmpresa(empresa);
            return planRepository.save(p);
        });

        List<Plan> planes = Arrays.asList(planBasico, planPro);
        Random random = new Random();

        // 3. Crear Clientes para diferentes meses
        String[] nombres = {"Carlos", "Maria", "Jorge", "Lucia", "Andres", "Sofía", "Miguel", "Valeria", "Diego", "Camila", "Fernando", "Isabella", "Ricardo", "Valentina", "Hugo", "Elena", "Raul", "Gabriela"};
        String[] apellidos = {"Gomez", "Perez", "Rodriguez", "Sanchez", "Fernandez", "Torres", "Ramirez", "Flores", "Benitez", "Castro", "Ortiz", "Morales", "Herrera", "Rojas", "Vargas", "Silva", "Mendoza", "Rios"};

        // Generar 18 clientes, 3 para cada mes (Enero a Junio)
        int dniCounter = 74000000;
        int clienteContador = 0;

        for (int mes = 1; mes <= 6; mes++) {
            for (int i = 0; i < 3; i++) {
                LocalDateTime fechaRegistro = LocalDateTime.of(2026, mes, random.nextInt(28) + 1, 10, 0);
                
                String nombreActual = nombres[clienteContador];
                String apellidoActual = apellidos[clienteContador];

                Cliente c = new Cliente();
                c.setNombre(nombreActual);
                c.setApellido(apellidoActual);
                c.setDni(String.valueOf(dniCounter++));
                c.setTelefono("9" + (10000000 + random.nextInt(89999999)));
                c.setCorreo(nombreActual.toLowerCase() + "." + apellidoActual.toLowerCase() + "@gmail.com");
                c.setFechaRegistro(fechaRegistro);
                c.setEmpresa(empresa);
                c.setActivo(random.nextInt(10) > 1); // 80% activos

                c = clienteRepository.save(c);

                // 4. Crear Membresia
                Plan planElegido = planes.get(random.nextInt(2));
                Membresia m = new Membresia();
                m.setCliente(c);
                m.setPlan(planElegido);
                m.setFechaInicio(fechaRegistro.toLocalDate());
                
                // Si el cliente sigue activo, le damos una membresia que vence en el futuro cercano o recién vencida
                if (c.getActivo()) {
                    m.setFechaFin(LocalDate.now().plusDays(random.nextInt(40) - 10)); // Vence entre hace 10 días y 30 días futuro
                    m.setEstado(EstadoMembresia.ACTIVA);
                } else {
                    m.setFechaFin(fechaRegistro.toLocalDate().plusMonths(1));
                    m.setEstado(EstadoMembresia.VENCIDA);
                }
                
                m = membresiaRepository.save(m);

                // 5. Crear Pago asociado al mes en que se registró
                Pago p = new Pago();
                p.setMembresia(m);
                p.setMonto(planElegido.getPrecio());
                p.setFechaPago(fechaRegistro.toLocalDate());
                p.setEstadoPago(EstadoPago.COMPLETADO);
                p.setMetodoPago(random.nextBoolean() ? MetodoPago.TARJETA : MetodoPago.EFECTIVO);
                
                pagoRepository.save(p);
                
                // Si la membresia es activa y el registro fue hace meses, generar pagos intermedios para simular recurrencia
                if (c.getActivo() && mes < 5) {
                    Pago p2 = new Pago();
                    p2.setMembresia(m);
                    p2.setMonto(planElegido.getPrecio());
                    p2.setFechaPago(LocalDate.of(2026, mes + 1, random.nextInt(28) + 1));
                    p2.setEstadoPago(EstadoPago.COMPLETADO);
                    p2.setMetodoPago(MetodoPago.TRANSFERENCIA);
                    pagoRepository.save(p2);
                }

                clienteContador++;
            }
        }
        System.out.println("Inyección completada. Dashboard ahora tiene datos históricos.");
    }
}
