package com.java.boticaintegrador.controller;

import com.java.boticaintegrador.model.Medicamento;
import com.java.boticaintegrador.service.CategoriaService;
import com.java.boticaintegrador.service.MedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/medicamentos")
public class MedicamentoController {

    @Autowired
    private MedicamentoService medicamentoService;

    @Autowired
    private CategoriaService categoriaService;

    // Método para mostrar la pantalla de inventario (HU-03)
    @GetMapping
    public String listarInventario(Model model) {
        // Enviamos la lista de medicamentos a la tabla HTML
        model.addAttribute("listaMedicamentos", medicamentoService.listarTodos());

        // Enviamos las categorías para el <select> del formulario
        model.addAttribute("listaCategorias", categoriaService.listarTodas());

        // Enviamos un objeto vacío para que el formulario lo llene
        model.addAttribute("nuevoMedicamento", new Medicamento());

        return "inventario"; // Retorna inventario.html
    }

    // Método para guardar un nuevo medicamento (HU-02)
    @PostMapping("/guardar")
    public String guardarMedicamento(@ModelAttribute("nuevoMedicamento") Medicamento medicamento) {
        medicamentoService.guardar(medicamento);
        // Redirige de vuelta a la página de medicamentos tras guardar
        return "redirect:/medicamentos";
    }
}