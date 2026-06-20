package com.java.boticaintegrador.service;

import com.java.boticaintegrador.dto.UsuarioDTO;
import com.java.boticaintegrador.model.Usuario;
import com.java.boticaintegrador.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{12,}$"
    );

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRol())
                .build();
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarActivos() {
        return usuarioRepository.findByEstado("ACTIVO");
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username).orElse(null);
    }

    public List<Usuario> buscarPorNombre(String query) {
        return usuarioRepository.buscarPorNombreOUsername(query);
    }

    public boolean existeUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    public boolean validarPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    @Transactional
    public Usuario guardar(Usuario usuario) {
        // Encriptar contraseña si es nueva o está cambiando
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario guardarDesdeDTO(UsuarioDTO dto) {
        Usuario usuario;

        if (dto.getId() != null && dto.getId() > 0) {
            usuario = buscarPorId(dto.getId());
            if (usuario == null) {
                usuario = new Usuario();
            }
        } else {
            usuario = new Usuario();
        }

        usuario.setUsername(dto.getUsername());
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setRol(dto.getRol());
        usuario.setEstado(dto.getEstado() != null ? dto.getEstado() : "ACTIVO");

        // Si se proporciona contraseña, encriptarla
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public void cambiarEstado(Long id, String estado) {
        Usuario usuario = buscarPorId(id);
        if (usuario != null) {
            usuario.setEstado(estado);
            usuarioRepository.save(usuario);
        }
    }

    @Transactional
    public void actualizarUltimoLogin(String username) {
        Usuario usuario = buscarPorUsername(username);
        if (usuario != null) {
            usuario.setLastLogin(LocalDateTime.now());
            usuarioRepository.save(usuario);
        }
    }

    public long contarTotal() {
        return usuarioRepository.count();
    }

    public long contarActivos() {
        return usuarioRepository.findByEstado("ACTIVO").size();
    }

    public long contarInactivos() {
        return usuarioRepository.findByEstado("INACTIVO").size();
    }
}