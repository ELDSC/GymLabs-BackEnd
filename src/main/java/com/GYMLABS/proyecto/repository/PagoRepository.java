package com.GYMLABS.proyecto.repository;

import com.GYMLABS.proyecto.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    @org.springframework.data.jpa.repository.Query(value = "SELECT SUM(monto) FROM pago WHERE estado_pago = 'COMPLETADO' AND MONTH(fecha_pago) = MONTH(CURRENT_DATE) AND YEAR(fecha_pago) = YEAR(CURRENT_DATE)", nativeQuery = true)
    java.math.BigDecimal sumIngresosMesActual();

    java.util.List<Pago> findByEstadoPagoAndFechaPagoAfter(com.GYMLABS.proyecto.model.EstadoPago estado, java.time.LocalDate date);
}
