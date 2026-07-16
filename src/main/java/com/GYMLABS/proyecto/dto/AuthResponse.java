package com.GYMLABS.proyecto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String nombre;
    private String rol;
    private Integer idEmpresa;
    private String empresaNombre;
    private String apellido;
    private String correo;
    private Integer idPersonal;
    private String dni;
    private String telefono;
}
