package com.GYMLABS.proyecto.repository;

import com.GYMLABS.proyecto.model.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Integer> {
    long countByEstado(com.GYMLABS.proyecto.model.EstadoMembresia estado);
}
