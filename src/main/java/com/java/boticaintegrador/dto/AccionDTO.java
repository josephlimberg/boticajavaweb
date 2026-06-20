package com.java.boticaintegrador.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccionDTO {
    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private String usuarioIniciales;
    private String tipoAccion;
    private LocalDateTime fechaHora;
    private String descripcion;
    private String modulo;
    private String detalles;
    private String ipOrigen;
    private String colorTipo;
}