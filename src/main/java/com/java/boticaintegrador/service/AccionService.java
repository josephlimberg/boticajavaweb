package com.java.boticaintegrador.service;

import com.java.boticaintegrador.dto.AccionDTO;
import com.java.boticaintegrador.model.Accion;
import com.java.boticaintegrador.model.Usuario;
import com.java.boticaintegrador.repository.AccionRepository;
import com.java.boticaintegrador.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccionService {

    @Autowired
    private AccionRepository accionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Accion guardar(Accion accion) {
        if (accion.getFechaHora() == null) {
            accion.setFechaHora(LocalDateTime.now());
        }
        return accionRepository.save(accion);
    }

    @Transactional
    public Accion registrarAccion(String tipoAccion, String descripcion, 
                                   String modulo, String detalles, String ipOrigen) {
        // Obtener el usuario autenticado
        String username = obtenerUsuarioActual();
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        if (usuario == null) {
            return null;
        }

        Accion accion = new Accion();
        accion.setUsuario(usuario);
        accion.setTipoAccion(tipoAccion);
        accion.setFechaHora(LocalDateTime.now());
        accion.setDescripcion(descripcion);
        accion.setModulo(modulo);
        accion.setDetalles(detalles);
        accion.setIpOrigen(ipOrigen);

        return accionRepository.save(accion);
    }

    @Transactional
    public Accion registrarAccionConUsuario(String username, String tipoAccion, String descripcion, 
                                             String modulo, String detalles, String ipOrigen) {
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        if (usuario == null) {
            return null;
        }

        Accion accion = new Accion();
        accion.setUsuario(usuario);
        accion.setTipoAccion(tipoAccion);
        accion.setFechaHora(LocalDateTime.now());
        accion.setDescripcion(descripcion);
        accion.setModulo(modulo);
        accion.setDetalles(detalles);
        accion.setIpOrigen(ipOrigen);

        return accionRepository.save(accion);
    }

    private String obtenerUsuarioActual() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                return auth.getName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "admin";
    }

    public List<Accion> listarTodas() {
        return accionRepository.findAll();
    }

    public List<Accion> listarUltimas() {
        return accionRepository.findTop50ByOrderByFechaHoraDesc();
    }

    public List<Accion> filtrarAcciones(LocalDateTime inicio, LocalDateTime fin, 
                                         Long usuarioId, String tipoAccion) {
        return accionRepository.filtrarAcciones(inicio, fin, usuarioId, tipoAccion);
    }

    public Accion buscarPorId(Long id) {
        return accionRepository.findById(id).orElse(null);
    }

    public long contarAccionesHoy() {
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fin = LocalDate.now().atTime(23, 59, 59);
        return accionRepository.countByFechaHoraBetween(inicio, fin);
    }

    public long contarModificacionesHoy() {
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        return accionRepository.countModificacionesHoy(inicio);
    }

    public long contarEliminacionesHoy() {
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        return accionRepository.countEliminacionesHoy(inicio);
    }

    public long contarTotalRegistros() {
        return accionRepository.count();
    }

    public List<AccionDTO> convertirADTO(List<Accion> acciones) {
        return acciones.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public AccionDTO convertirADTO(Accion accion) {
        AccionDTO dto = new AccionDTO();
        dto.setId(accion.getId());
        dto.setUsuarioId(accion.getUsuario().getId());
        dto.setUsuarioNombre(accion.getUsuario().getNombreCompleto());
        dto.setUsuarioIniciales(getIniciales(accion.getUsuario().getNombreCompleto()));
        dto.setTipoAccion(accion.getTipoAccion());
        dto.setFechaHora(accion.getFechaHora());
        dto.setDescripcion(accion.getDescripcion());
        dto.setModulo(accion.getModulo());
        dto.setDetalles(accion.getDetalles());
        dto.setIpOrigen(accion.getIpOrigen());
        dto.setColorTipo(getColorTipo(accion.getTipoAccion()));
        return dto;
    }

    private String getIniciales(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isEmpty()) {
            return "??";
        }
        String[] partes = nombreCompleto.trim().split(" ");
        if (partes.length == 1) {
            return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
        }
        return (partes[0].charAt(0) + "" + partes[partes.length - 1].charAt(0)).toUpperCase();
    }

    private String getColorTipo(String tipoAccion) {
        switch (tipoAccion.toUpperCase()) {
            case "CREACIÓN":
                return "badge-creacion";
            case "MODIFICACIÓN":
                return "badge-modificacion";
            case "ELIMINACIÓN":
                return "badge-eliminacion";
            case "SISTEMA":
                return "badge-sistema";
            default:
                return "";
        }
    }
}