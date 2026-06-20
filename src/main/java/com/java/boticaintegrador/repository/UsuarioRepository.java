package com.java.boticaintegrador.repository;

import com.java.boticaintegrador.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Spring Boot detecta el nombre y crea la consulta SQL automáticamente:
    // SELECT * FROM usuarios WHERE username = ?
    Optional<Usuario> findByUsername(String username);
    
    List<Usuario> findByEstado(String estado);

    List<Usuario> findByRol(String rol);

    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nombreCompleto) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Usuario> buscarPorNombreOUsername(@Param("query") String query);

    boolean existsByUsername(String username);
    
}