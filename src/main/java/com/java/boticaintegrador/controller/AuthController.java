package com.java.boticaintegrador.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String mostrarLogin() {
        // Retorna el nombre del archivo HTML (login.html) que estará en la carpeta templates
        return "login";
    }
}