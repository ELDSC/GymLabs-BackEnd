package com.GYMLABS.proyecto.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class ChartData {
    private String name; // Month (e.g. "Ene", "Feb")
    private Double value; // Value (Revenue or Client count)
}
