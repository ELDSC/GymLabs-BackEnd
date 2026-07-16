package com.GYMLABS.proyecto.repository;

import com.GYMLABS.proyecto.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    boolean existsByDni(String dni);
    boolean existsByCorreo(String correo);

    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.activo = true AND (:empresaId IS NULL OR c.empresa.idEmpresa = :empresaId)")
    long countActiveClients(@Param("empresaId") Integer empresaId);

    @Query("SELECT c FROM Cliente c WHERE c.activo = true AND (:empresaId IS NULL OR c.empresa.idEmpresa = :empresaId) AND c.fechaRegistro >= :date")
    java.util.List<Cliente> findActiveClientsAfter(@Param("date") java.time.LocalDateTime date, @Param("empresaId") Integer empresaId);

    @Query("SELECT c FROM Cliente c WHERE c.activo = true AND (:empresaId IS NULL OR c.empresa.idEmpresa = :empresaId) AND c.fechaRegistro BETWEEN :start AND :end")
    java.util.List<Cliente> findActiveClientsBetween(@Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end, @Param("empresaId") Integer empresaId);

    @Query("SELECT c FROM Cliente c WHERE " +
           "(:empresaId IS NULL OR c.empresa.idEmpresa = :empresaId) AND " +
           "(:searchTerm IS NULL OR :searchTerm = '' OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.apellido) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR c.dni LIKE CONCAT('%', :searchTerm, '%')) AND " +
           "(:filterStatus IS NULL OR :filterStatus = 'ALL' OR " +
           "(:filterStatus = 'ACTIVE' AND c.activo = true) OR " +
           "(:filterStatus = 'INACTIVE' AND c.activo = false) OR " +
           "(:filterStatus = 'EXPIRING' AND c.activo = true AND EXISTS (SELECT 1 FROM Membresia m WHERE m.cliente = c AND m.estado = 'ACTIVA' AND m.fechaFin <= :expiringDate)))")
    Page<Cliente> buscarClientesConFiltros(@Param("empresaId") Integer empresaId, @Param("searchTerm") String searchTerm, @Param("filterStatus") String filterStatus, @Param("expiringDate") java.time.LocalDate expiringDate, Pageable pageable);
}
