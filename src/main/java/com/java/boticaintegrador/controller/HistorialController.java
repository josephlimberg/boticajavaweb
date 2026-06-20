package com.java.boticaintegrador.controller;

import com.java.boticaintegrador.dto.AccionDTO;
import com.java.boticaintegrador.dto.ErrorResponse;
import com.java.boticaintegrador.model.Accion;
import com.java.boticaintegrador.model.Usuario;
import com.java.boticaintegrador.service.AccionService;
import com.java.boticaintegrador.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/historial")
public class HistorialController {

    @Autowired
    private AccionService accionService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String mostrarHistorial(Model model, HttpServletRequest request) {
        try {
            // Obtener el usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : "Invitado";
            
            // Buscar el usuario en la base de datos
            Usuario usuario = usuarioService.buscarPorUsername(username);
            String nombreUsuario = usuario != null ? usuario.getNombreCompleto() : username;
            String rolUsuario = usuario != null ? usuario.getRol() : "Usuario";
            
            // Agregar al modelo para mostrarlo en la vista
            model.addAttribute("nombreUsuario", nombreUsuario);
            model.addAttribute("rolUsuario", rolUsuario);
            System.out.println("=== CARGANDO HISTORIAL ===");
            
            accionService.registrarAccion(
                "SISTEMA",
                "Visualizó historial de acciones",
                "Historial",
                null,
                request.getRemoteAddr()
            );
            
            List<Accion> acciones = accionService.listarUltimas();
            List<AccionDTO> accionesDTO = accionService.convertirADTO(acciones);
            System.out.println("Acciones encontradas: " + accionesDTO.size());

            long totalHoy = accionService.contarAccionesHoy();
            long modificacionesHoy = accionService.contarModificacionesHoy();
            long eliminacionesHoy = accionService.contarEliminacionesHoy();
            long totalRegistros = accionService.contarTotalRegistros();

            List<Usuario> usuarios = usuarioService.listarTodos();

            model.addAttribute("acciones", accionesDTO);
            model.addAttribute("totalHoy", totalHoy);
            model.addAttribute("modificacionesHoy", modificacionesHoy);
            model.addAttribute("eliminacionesHoy", eliminacionesHoy);
            model.addAttribute("totalRegistros", totalRegistros);
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("fechaActual", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            System.out.println("=== HISTORIAL CARGADO EXITOSAMENTE ===");
            return "historial";

        } catch (Exception e) {
            System.out.println("=== ERROR EN HISTORIAL ===");
            e.printStackTrace();
            
            ErrorResponse error = new ErrorResponse(
                "Error al cargar el historial: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getStackTrace().length > 0 ? e.getStackTrace()[0].toString() : "Sin detalles",
                System.currentTimeMillis()
            );
            model.addAttribute("error", error);
            
            model.addAttribute("acciones", List.of());
            model.addAttribute("totalHoy", 0L);
            model.addAttribute("modificacionesHoy", 0L);
            model.addAttribute("eliminacionesHoy", 0L);
            model.addAttribute("totalRegistros", 0L);
            model.addAttribute("usuarios", List.of());
            model.addAttribute("fechaActual", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            
            return "historial";
        }
    }

    @GetMapping("/filtrar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> filtrarHistorial(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate hasta,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) String tipoAccion) {

        try {
            LocalDateTime inicio = desde != null ? desde.atStartOfDay() : LocalDate.now().minusDays(30).atStartOfDay();
            LocalDateTime fin = hasta != null ? hasta.atTime(23, 59, 59) : LocalDateTime.now();

            List<Accion> acciones = accionService.filtrarAcciones(inicio, fin, usuarioId, tipoAccion);
            List<AccionDTO> accionesDTO = accionService.convertirADTO(acciones);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("acciones", accionesDTO);
            response.put("total", accionesDTO.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}