package com.GYMLABS.proyecto.config;

import com.GYMLABS.proyecto.model.Empresa;
import com.GYMLABS.proyecto.model.Personal;
import com.GYMLABS.proyecto.model.Rol;
import com.GYMLABS.proyecto.model.Sede;
import com.GYMLABS.proyecto.repository.EmpresaRepository;
import com.GYMLABS.proyecto.repository.PersonalRepository;
import com.GYMLABS.proyecto.repository.RolRepository;
import com.GYMLABS.proyecto.repository.SedeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private SedeRepository sedeRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PersonalRepository personalRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Verificar si ya existe la empresa para no duplicar datos si se reinicia la app
        if (empresaRepository.count() == 0) {
            // 1. Crear Empresa
            Empresa empresa = new Empresa();
            empresa.setNombre("FitLabs");
            empresa.setDireccion("av24 de octubre"); // Usando la dirección proporcionada
            empresa = empresaRepository.save(empresa);

            // 2. Crear Sede
            Sede sede = new Sede();
            sede.setNombre("Sede Principal");
            sede.setDireccion("av24 de octubre");
            sede.setEmpresa(empresa);
            sede = sedeRepository.save(sede);

            // 3. Crear Rol Admin
            Rol rolAdmin = new Rol();
            rolAdmin.setNombre("ADMIN");
            rolAdmin = rolRepository.save(rolAdmin);

            // 4. Crear Personal (Usuario Admin Josue)
            Personal admin = new Personal();
            admin.setNombre("Josue");
            admin.setApellido("Sarango");
            admin.setDni("74379097");
            admin.setTelefono("947102850");
            admin.setCorreo("sarangojosue6@gmail.com");
            admin.setPassword("admin123");
            admin.setFechaContratacion(LocalDate.now());
            admin.setRol(rolAdmin);
            admin.setSede(sede);
            
            personalRepository.save(admin);
            
            System.out.println("==========================================================");
            System.out.println("✅ DATOS INICIALES CREADOS CORRECTAMENTE:");
            System.out.println("Empresa: FitLabs");
            System.out.println("Usuario Admin: Josue Sarango (" + admin.getCorreo() + ")");
            System.out.println("==========================================================");
        }
    }
}
