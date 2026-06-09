package com.GYMLABS.proyecto.repository;

import com.GYMLABS.proyecto.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    boolean existsByDni(String dni);
    boolean existsByCorreo(String correo);

    long countByActivoTrue();
    java.util.List<Cliente> findByActivoTrueAndFechaRegistroAfter(java.time.LocalDateTime date);
}
