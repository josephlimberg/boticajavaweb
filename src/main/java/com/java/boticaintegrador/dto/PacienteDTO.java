package com.java.boticaintegrador.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PacienteDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String genero;
    private String estado;
    private Integer comprasRealizadas;
    private String nombreCompleto;
}