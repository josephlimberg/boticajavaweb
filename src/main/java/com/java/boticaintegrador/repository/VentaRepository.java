package com.java.boticaintegrador.repository;

import com.java.boticaintegrador.model.Venta;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByFechaVenta(LocalDate fechaVenta);
}