package com.GYMLABS.proyecto.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonalDto {
    private Integer idPersonal;
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String correo;
    private String rol; // "ADMIN" o "RECEPCIONISTA"
    private String contrasena;
}
