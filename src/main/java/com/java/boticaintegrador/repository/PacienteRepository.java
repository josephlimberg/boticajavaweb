package com.java.boticaintegrador.repository;

import com.java.boticaintegrador.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByDni(String dni);
    
    List<Paciente> findByEstado(String estado);
    
    long countByEstado(String estado);
    
    long countByComprasRealizadasGreaterThanEqual(int compras);
    
    long countByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fin);
    
    // Búsqueda por nombre, apellido o DNI (más eficiente)
    @Query("SELECT p FROM Paciente p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.apellido) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR p.dni LIKE CONCAT('%', :query, '%')")
    List<Paciente> buscarPorNombreApellidoODni(@Param("query") String query);
}