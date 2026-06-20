package com.java.boticaintegrador.controller;

import com.java.boticaintegrador.service.AccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {

    @Autowired
    private AccionService accionService;

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Obtener el usuario actual antes de cerrar sesión
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : "desconocido";
            
            // Registrar acción de logout
            accionService.registrarAccionConUsuario(
                username,
                "SISTEMA",
                "Cerró sesión del sistema",
                "Seguridad",
                "Logout exitoso",
                request.getRemoteAddr()
            );
        } catch (Exception e) {
            System.out.println("Error al registrar logout: " + e.getMessage());
        }

        // Realizar logout de Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        
        return "redirect:/login?logout";
    }
}