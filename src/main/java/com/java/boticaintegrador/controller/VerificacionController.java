package com.java.boticaintegrador.controller;

import com.java.boticaintegrador.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class VerificacionController {

    @Autowired
    private SmsService smsService;

    @GetMapping("/verificacion")
    public String mostrarVerificacion(Model model, HttpSession session) {
        // Si ya está verificado, redirigir a medicamentos
        Boolean verificado = (Boolean) session.getAttribute("verificado");
        if (verificado != null && verificado) {
            return "redirect:/medicamentos";
        }
        
        // Enviar código automáticamente al cargar la página
        try {
            String telefono = smsService.obtenerNumeroVerificacion();
            String codigo = smsService.generarCodigo();
            boolean enviado = smsService.enviarSms(telefono, codigo);
            
            if (enviado) {
                session.setAttribute("telefonoVerificacion", telefono);
                model.addAttribute("mensaje", "Código enviado a tu teléfono");
            } else {
                model.addAttribute("error", "Error al enviar el código");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
        }
        
        return "verificacion";
    }

    @PostMapping("/verificacion/enviar")
    public String enviarCodigo(HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            String telefono = smsService.obtenerNumeroVerificacion();
            String codigo = smsService.generarCodigo();
            
            boolean enviado = smsService.enviarSms(telefono, codigo);
            
            if (enviado) {
                session.setAttribute("telefonoVerificacion", telefono);
                session.setAttribute("codigoEnviado", true);
                redirectAttributes.addFlashAttribute("mensaje", "Código enviado a tu teléfono");
            } else {
                redirectAttributes.addFlashAttribute("error", "Error al enviar el código");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/verificacion";
    }

    @PostMapping("/verificacion/validar")
    public String validarCodigo(
            @RequestParam("codigo") String codigo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        String telefono = (String) session.getAttribute("telefonoVerificacion");
        
        if (telefono == null) {
            redirectAttributes.addFlashAttribute("error", "Sesión expirada, solicita un nuevo código");
            return "redirect:/verificacion";
        }
        
        boolean valido = smsService.verificarCodigo(telefono, codigo);
        
        if (valido) {
            session.setAttribute("verificado", true);
            session.removeAttribute("telefonoVerificacion");
            return "redirect:/medicamentos";
        } else {
            redirectAttributes.addFlashAttribute("error", "Código incorrecto, intenta nuevamente");
            return "redirect:/verificacion";
        }
    }
}