package com.GYMLABS.proyecto.repository;

import com.GYMLABS.proyecto.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    @org.springframework.data.jpa.repository.Query("SELECT SUM(p.monto) FROM Pago p WHERE p.estadoPago = 'COMPLETADO' AND MONTH(p.fechaPago) = MONTH(CURRENT_DATE) AND YEAR(p.fechaPago) = YEAR(CURRENT_DATE) AND (:empresaId IS NULL OR p.membresia.cliente.empresa.idEmpresa = :empresaId)")
    java.math.BigDecimal sumIngresosMesActual(@org.springframework.data.repository.query.Param("empresaId") Integer empresaId);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(p.monto) FROM Pago p WHERE p.estadoPago = 'COMPLETADO' AND MONTH(p.fechaPago) = :mes AND YEAR(p.fechaPago) = :anio AND (:empresaId IS NULL OR p.membresia.cliente.empresa.idEmpresa = :empresaId)")
    java.math.BigDecimal sumIngresosPorMesYAnio(@org.springframework.data.repository.query.Param("mes") int mes, @org.springframework.data.repository.query.Param("anio") int anio, @org.springframework.data.repository.query.Param("empresaId") Integer empresaId);

    @org.springframework.data.jpa.repository.Query("SELECT p FROM Pago p WHERE p.estadoPago = :estado AND p.fechaPago >= :date AND (:empresaId IS NULL OR p.membresia.cliente.empresa.idEmpresa = :empresaId)")
    java.util.List<Pago> findPagosAfter(@org.springframework.data.repository.query.Param("estado") com.GYMLABS.proyecto.model.EstadoPago estado, @org.springframework.data.repository.query.Param("date") java.time.LocalDate date, @org.springframework.data.repository.query.Param("empresaId") Integer empresaId);

    @org.springframework.data.jpa.repository.Query("SELECT p FROM Pago p WHERE p.estadoPago = :estado AND p.fechaPago BETWEEN :start AND :end AND (:empresaId IS NULL OR p.membresia.cliente.empresa.idEmpresa = :empresaId)")
    java.util.List<Pago> findPagosBetween(@org.springframework.data.repository.query.Param("estado") com.GYMLABS.proyecto.model.EstadoPago estado, @org.springframework.data.repository.query.Param("start") java.time.LocalDate start, @org.springframework.data.repository.query.Param("end") java.time.LocalDate end, @org.springframework.data.repository.query.Param("empresaId") Integer empresaId);
}
