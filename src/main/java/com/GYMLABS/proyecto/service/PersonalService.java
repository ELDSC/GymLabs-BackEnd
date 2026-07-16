package com.GYMLABS.proyecto.service;

import com.GYMLABS.proyecto.model.Personal;
import com.GYMLABS.proyecto.repository.PersonalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonalService {

    @Autowired
    private PersonalRepository personalRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private com.GYMLABS.proyecto.repository.SedeRepository sedeRepository;
    
    @Autowired
    private com.GYMLABS.proyecto.repository.RolRepository rolRepository;

    public List<Personal> listarPorEmpresa(Integer idEmpresa) {
        return personalRepository.findBySede_Empresa_IdEmpresaAndActivoTrue(idEmpresa);
    }

    public Optional<Personal> buscarPorId(Integer id) {
        return personalRepository.findById(id);
    }

    public Personal guardarDesdeDto(com.GYMLABS.proyecto.dto.PersonalDto dto, Integer idEmpresa, boolean isUpdate) {
        // Validar unicidad
        if (!isUpdate) {
            if (personalRepository.existsByCorreo(dto.getCorreo())) {
                throw new IllegalArgumentException("El correo ya está registrado.");
            }
            if (personalRepository.existsByDni(dto.getDni())) {
                throw new IllegalArgumentException("El DNI ya está registrado.");
            }
        } else {
            // Si es actualizacion, solo validar si cambió el correo o DNI
            Personal existente = personalRepository.findById(dto.getIdPersonal()).orElseThrow();
            if (!existente.getCorreo().equals(dto.getCorreo()) && personalRepository.existsByCorreo(dto.getCorreo())) {
                throw new IllegalArgumentException("El correo ya está registrado por otro usuario.");
            }
            if (!existente.getDni().equals(dto.getDni()) && personalRepository.existsByDni(dto.getDni())) {
                throw new IllegalArgumentException("El DNI ya está registrado por otro usuario.");
            }
        }

        Personal personal = isUpdate ? personalRepository.findById(dto.getIdPersonal()).orElse(new Personal()) : new Personal();
        
        personal.setNombre(dto.getNombre());
        personal.setApellido(dto.getApellido());
        personal.setDni(dto.getDni());
        personal.setTelefono(dto.getTelefono());
        personal.setCorreo(dto.getCorreo());
        
        if (!isUpdate) {
            personal.setFechaContratacion(java.time.LocalDate.now());
            // Asignar primera Sede de la empresa
            var sedes = sedeRepository.findAll().stream().filter(s -> s.getEmpresa().getIdEmpresa().equals(idEmpresa)).toList();
            if (sedes.isEmpty()) throw new IllegalStateException("La empresa no tiene sedes configuradas.");
            personal.setSede(sedes.get(0));
            personal.setActivo(true);
        }
        
        // Asignar Rol
        String rolBuscado = dto.getRol() != null ? dto.getRol() : "RECEPCIONISTA";
        var rol = rolRepository.findAll().stream().filter(r -> r.getNombre().equals(rolBuscado)).findFirst();
        rol.ifPresent(personal::setRol);

        if (dto.getContrasena() != null && !dto.getContrasena().trim().isEmpty()) {
            personal.setPassword(passwordEncoder.encode(dto.getContrasena()));
        } else if (!isUpdate) {
            throw new IllegalArgumentException("La contraseña es obligatoria para nuevos usuarios.");
        }

        return personalRepository.save(personal);
    }

    public void softDelete(Integer id) {
        Personal personal = personalRepository.findById(id).orElseThrow();
        personal.setActivo(false);
        personalRepository.save(personal);
    }
}
