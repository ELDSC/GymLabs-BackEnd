package com.GYMLABS.proyecto.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class DashboardDTO {
    private Long totalClientes;
    private BigDecimal ingresosMes;
    private Long membresiasActivas;
    private List<ChartData> graficoIngresos;
    private List<ChartData> graficoNuevosClientes;
}
