package com.java.boticaintegrador.repository;

import com.java.boticaintegrador.model.Accion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccionRepository extends JpaRepository<Accion, Long> {

    @Query("SELECT a FROM Accion a WHERE a.fechaHora BETWEEN :inicio AND :fin")
    List<Accion> findByFechaHoraBetween(@Param("inicio") LocalDateTime inicio, 
                                        @Param("fin") LocalDateTime fin);

    @Query("SELECT a FROM Accion a WHERE a.fechaHora BETWEEN :inicio AND :fin " +
           "AND (:usuarioId IS NULL OR a.usuario.id = :usuarioId) " +
           "AND (:tipoAccion IS NULL OR a.tipoAccion = :tipoAccion)")
    List<Accion> filtrarAcciones(@Param("inicio") LocalDateTime inicio,
                                  @Param("fin") LocalDateTime fin,
                                  @Param("usuarioId") Long usuarioId,
                                  @Param("tipoAccion") String tipoAccion);

    @Query("SELECT COUNT(a) FROM Accion a WHERE a.fechaHora >= :fecha")
    long countByFechaHoraAfter(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT COUNT(a) FROM Accion a WHERE a.fechaHora BETWEEN :inicio AND :fin")
    long countByFechaHoraBetween(@Param("inicio") LocalDateTime inicio, 
                                  @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(a) FROM Accion a WHERE a.tipoAccion = 'MODIFICACIÓN' AND a.fechaHora >= :fecha")
    long countModificacionesHoy(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT COUNT(a) FROM Accion a WHERE a.tipoAccion = 'ELIMINACIÓN' AND a.fechaHora >= :fecha")
    long countEliminacionesHoy(@Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT a FROM Accion a ORDER BY a.fechaHora DESC")
    List<Accion> findTop50ByOrderByFechaHoraDesc();
}