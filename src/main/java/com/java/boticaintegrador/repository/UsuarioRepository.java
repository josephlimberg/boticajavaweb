package com.java.boticaintegrador.repository;

import com.java.boticaintegrador.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Spring Boot detecta el nombre y crea la consulta SQL automáticamente:
    // SELECT * FROM usuarios WHERE username = ?
    Optional<Usuario> findByUsername(String username);
    
}