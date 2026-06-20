package com.java.boticaintegrador.controller;

import com.java.boticaintegrador.dto.ErrorResponse;
import com.java.boticaintegrador.dto.UsuarioDTO;
import com.java.boticaintegrador.model.Usuario;
import com.java.boticaintegrador.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listarUsuarios(Model model) {
        try {
            List<Usuario> usuarios = usuarioService.listarTodos();
            
            long total = usuarioService.contarTotal();
            long activos = usuarioService.contarActivos();
            long inactivos = usuarioService.contarInactivos();
            
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("totalUsuarios", total);
            model.addAttribute("activos", activos);
            model.addAttribute("inactivos", inactivos);
            
            return "usuarios";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("usuarios", List.of());
            model.addAttribute("totalUsuarios", 0L);
            model.addAttribute("activos", 0L);
            model.addAttribute("inactivos", 0L);
            
            ErrorResponse error = new ErrorResponse(
                "Error al cargar usuarios: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getStackTrace().length > 0 ? e.getStackTrace()[0].toString() : "Sin detalles",
                System.currentTimeMillis()
            );
            model.addAttribute("error", error);
            return "usuarios";
        }
    }

    @GetMapping("/buscar")
    @ResponseBody
    public List<Map<String, Object>> buscarUsuarios(@RequestParam String query) {
        try {
            List<Usuario> usuarios = usuarioService.buscarPorNombre(query);
            return usuarios.stream().map(u -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", u.getId());
                map.put("username", u.getUsername());
                map.put("nombreCompleto", u.getNombreCompleto());
                map.put("rol", u.getRol());
                map.put("estado", u.getEstado());
                map.put("iniciales", getIniciales(u.getNombreCompleto()));
                return map;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerUsuario(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id);
            if (usuario == null) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", usuario.getId());
            response.put("username", usuario.getUsername());
            response.put("nombreCompleto", usuario.getNombreCompleto());
            response.put("rol", usuario.getRol());
            response.put("estado", usuario.getEstado());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute UsuarioDTO dto, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== GUARDANDO USUARIO ===");
            System.out.println("ID: " + dto.getId());
            System.out.println("Username: " + dto.getUsername());
            System.out.println("Nombre: " + dto.getNombreCompleto());
            System.out.println("Rol: " + dto.getRol());
            System.out.println("Estado: " + dto.getEstado());
            
            // Validar campos obligatorios
            if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
                redirectAttributes.addAttribute("error", "El nombre de usuario es obligatorio");
                return "redirect:/usuarios";
            }
            
            if (dto.getNombreCompleto() == null || dto.getNombreCompleto().trim().isEmpty()) {
                redirectAttributes.addAttribute("error", "El nombre completo es obligatorio");
                return "redirect:/usuarios";
            }
            
            if (dto.getRol() == null || dto.getRol().trim().isEmpty()) {
                redirectAttributes.addAttribute("error", "El rol es obligatorio");
                return "redirect:/usuarios";
            }
            
            // Validar username único
            Usuario existente = usuarioService.buscarPorUsername(dto.getUsername());
            if (existente != null && !existente.getId().equals(dto.getId())) {
                redirectAttributes.addAttribute("error", "El nombre de usuario ya existe: " + dto.getUsername());
                return "redirect:/usuarios";
            }
            
            // Si es nuevo usuario, validar contraseña
            if (dto.getId() == null || dto.getId() == 0) {
                if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
                    redirectAttributes.addAttribute("error", "La contraseña es obligatoria");
                    return "redirect:/usuarios";
                }
                
                if (!usuarioService.validarPassword(dto.getPassword())) {
                    redirectAttributes.addAttribute("error", 
                        "La contraseña debe tener mínimo 12 caracteres, incluir mayúscula, minúscula, número y signo");
                    return "redirect:/usuarios";
                }
                
                if (!dto.getPassword().equals(dto.getConfirmPassword())) {
                    redirectAttributes.addAttribute("error", "Las contraseñas no coinciden");
                    return "redirect:/usuarios";
                }
            }
            
            // Guardar usuario
            usuarioService.guardarDesdeDTO(dto);
            
            redirectAttributes.addAttribute("success", "Usuario guardado exitosamente");
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Error al guardar: " + e.getMessage());
        }
        
        return "redirect:/usuarios";
    }

    @PostMapping("/eliminar")
    public String eliminarUsuario(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminar(id);
            redirectAttributes.addAttribute("deleted", "Usuario eliminado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/cambiar-estado")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cambiarEstado(@RequestParam Long id, @RequestParam String estado) {
        try {
            usuarioService.cambiarEstado(id, estado);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("estado", estado);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
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
}