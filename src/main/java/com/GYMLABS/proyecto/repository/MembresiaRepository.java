package com.GYMLABS.proyecto.repository;

import com.GYMLABS.proyecto.model.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Integer> {
    @Query("SELECT COUNT(m) FROM Membresia m WHERE m.estado = :estado AND (:empresaId IS NULL OR m.cliente.empresa.idEmpresa = :empresaId)")
    long countByEstadoAndEmpresa(@Param("estado") com.GYMLABS.proyecto.model.EstadoMembresia estado, @Param("empresaId") Integer empresaId);
    
    java.util.List<Membresia> findByCliente_IdClienteOrderByFechaFinDesc(Integer idCliente);

    @Query("SELECT m FROM Membresia m WHERE m.idMembresia IN (SELECT MAX(m2.idMembresia) FROM Membresia m2 WHERE m2.cliente.idCliente IN :clienteIds GROUP BY m2.cliente.idCliente)")
    List<Membresia> findLatestMembresiasByClienteIds(@Param("clienteIds") List<Integer> clienteIds);
}
