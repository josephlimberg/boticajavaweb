package com.java.boticaintegrador.repository;

import com.java.boticaintegrador.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByFechaVenta(LocalDate fechaVenta);
    
    @Query("SELECT v FROM Venta v WHERE v.fechaVenta BETWEEN :inicio AND :fin")
    List<Venta> findByFechaVentaBetween(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}