package com.GYMLABS.proyecto.repository;

import com.GYMLABS.proyecto.model.Personal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalRepository extends JpaRepository<Personal, Integer> {
    java.util.Optional<Personal> findByCorreo(String correo);
    java.util.List<Personal> findBySede_Empresa_IdEmpresaAndActivoTrue(Integer idEmpresa);
    boolean existsByCorreo(String correo);
    boolean existsByDni(String dni);
}
