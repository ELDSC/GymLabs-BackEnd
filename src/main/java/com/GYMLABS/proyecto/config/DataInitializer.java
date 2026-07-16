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

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

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

            // 3. Crear Roles
            Rol rolAdmin = new Rol();
            rolAdmin.setNombre("ADMIN");
            rolAdmin = rolRepository.save(rolAdmin);

            Rol rolSuperAdmin = new Rol();
            rolSuperAdmin.setNombre("SUPERADMIN");
            rolSuperAdmin = rolRepository.save(rolSuperAdmin);

            // 4. Crear Personal (Usuario Admin Josue)
            Personal admin = new Personal();
            admin.setNombre("Josue");
            admin.setApellido("Sarango");
            admin.setDni("74379097");
            admin.setTelefono("947102850");
            admin.setCorreo("sarangojosue6@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFechaContratacion(LocalDate.now());
            admin.setRol(rolAdmin);
            admin.setSede(sede);
            
            personalRepository.save(admin);

            // 5. Crear Super Admin del SaaS
            Personal superadmin = new Personal();
            superadmin.setNombre("Creador");
            superadmin.setApellido("SaaS");
            superadmin.setDni("00000000");
            superadmin.setCorreo("superadmin@gymlabs.com");
            superadmin.setPassword(passwordEncoder.encode("super123"));
            superadmin.setFechaContratacion(LocalDate.now());
            superadmin.setRol(rolSuperAdmin);
            superadmin.setSede(sede); // Associated to default sede temporarily
            
            personalRepository.save(superadmin);
            
            System.out.println("==========================================================");
            System.out.println("✅ DATOS INICIALES CREADOS CORRECTAMENTE:");
            System.out.println("Empresa: FitLabs");
            System.out.println("Usuario Admin: " + admin.getCorreo() + " (admin123)");
            System.out.println("Súper Admin: " + superadmin.getCorreo() + " (super123)");
            System.out.println("==========================================================");
        }

        // Siempre asegurarnos de que el superadmin exista y las contraseñas estén encriptadas
        java.util.List<Personal> todoPersonal = personalRepository.findAll();
        boolean superadminExists = false;
        
        for (Personal p : todoPersonal) {
            if ("superadmin@gymlabs.com".equals(p.getCorreo())) {
                superadminExists = true;
            }
            if (p.getPassword() != null && !p.getPassword().startsWith("$2a$")) {
                p.setPassword(passwordEncoder.encode(p.getPassword()));
                personalRepository.save(p);
                System.out.println("Contraseña de " + p.getCorreo() + " encriptada exitosamente.");
            }
        }

        if (!superadminExists && empresaRepository.count() > 0) {
            Rol rolSuperAdmin = rolRepository.findAll().stream().filter(r -> "SUPERADMIN".equals(r.getNombre())).findFirst().orElse(null);
            if (rolSuperAdmin == null) {
                rolSuperAdmin = new Rol();
                rolSuperAdmin.setNombre("SUPERADMIN");
                rolSuperAdmin = rolRepository.save(rolSuperAdmin);
            }
            Sede sede = sedeRepository.findAll().get(0);
            
            Personal superadmin = new Personal();
            superadmin.setNombre("Creador");
            superadmin.setApellido("SaaS");
            superadmin.setDni("00000000");
            superadmin.setCorreo("superadmin@gymlabs.com");
            superadmin.setPassword(passwordEncoder.encode("super123"));
            superadmin.setFechaContratacion(LocalDate.now());
            superadmin.setRol(rolSuperAdmin);
            superadmin.setSede(sede); 
            
            personalRepository.save(superadmin);
            System.out.println("✅ Súper Admin creado retroactivamente: superadmin@gymlabs.com (super123)");
        }
        
        // Asegurar que todos los roles básicos existan (retroactivo)
        java.util.List<String> rolesBasicos = java.util.Arrays.asList("ADMIN", "SUPERADMIN", "RECEPCIONISTA");
        for (String nombreRol : rolesBasicos) {
            boolean existe = rolRepository.findAll().stream().anyMatch(r -> r.getNombre().equalsIgnoreCase(nombreRol));
            if (!existe) {
                Rol nuevoRol = new Rol();
                nuevoRol.setNombre(nombreRol);
                rolRepository.save(nuevoRol);
                System.out.println("✅ Rol creado retroactivamente: " + nombreRol);
            }
        }
    }
}
