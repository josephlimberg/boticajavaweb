package com.java.boticaintegrador.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentoLoteDTO {
    // Medicamento fields
    private Long id;
    private String nombre;
    private Long categoriaId;
    private String formaFarmaceutica;
    private String descripcion;
    private String proveedor;
    private Integer stock;
    private Integer nivelMinimo;
    private BigDecimal precio;
    private String ubicacionEstante;
    private LocalDate fechaVencimiento;
    
    // Lote fields
    private String codigoLote;
    private Integer stockLote;
    private LocalDate fechaFabricacion;
    private BigDecimal precioCompra;
}