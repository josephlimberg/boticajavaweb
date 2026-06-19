package com.java.boticaintegrador.controller;

import com.java.boticaintegrador.model.Paciente;
import com.java.boticaintegrador.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @GetMapping
    public String listarPacientes(Model model) {
        try {
            List<Paciente> pacientes = pacienteService.listarTodos();
            
            // Estadísticas
            long total = pacienteService.contarTotal();
            long activos = pacienteService.contarActivos();
            long frecuentes = pacienteService.contarFrecuentes();
            long nuevosHoy = pacienteService.contarNuevosHoy();
            
            model.addAttribute("pacientes", pacientes);
            model.addAttribute("totalClientes", total);
            model.addAttribute("activos", activos);
            model.addAttribute("frecuentes", frecuentes);
            model.addAttribute("nuevosHoy", nuevosHoy);
            
            return "pacientes";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("pacientes", List.of());
            model.addAttribute("totalClientes", 0L);
            model.addAttribute("activos", 0L);
            model.addAttribute("frecuentes", 0L);
            model.addAttribute("nuevosHoy", 0L);
            model.addAttribute("error", "Error al cargar pacientes: " + e.getMessage());
            return "pacientes";
        }
    }

    @GetMapping("/buscar")
    @ResponseBody
    public List<Map<String, Object>> buscarPacientes(@RequestParam String query) {
        try {
            List<Paciente> pacientes = pacienteService.buscarPorNombre(query);
            return pacientes.stream().map(p -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", p.getId());
                map.put("nombre", p.getNombre());
                map.put("apellido", p.getApellido());
                map.put("dni", p.getDni());
                map.put("telefono", p.getTelefono());
                map.put("genero", p.getGenero());
                map.put("estado", p.getEstado());
                map.put("comprasRealizadas", p.getComprasRealizadas());
                return map;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Map<String, Object> obtenerPaciente(@PathVariable Long id) {
        try {
            Paciente paciente = pacienteService.buscarPorId(id);
            if (paciente == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Paciente no encontrado");
                return error;
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", paciente.getId());
            response.put("nombre", paciente.getNombre());
            response.put("apellido", paciente.getApellido());
            response.put("dni", paciente.getDni());
            response.put("telefono", paciente.getTelefono());
            response.put("genero", paciente.getGenero());
            response.put("estado", paciente.getEstado());
            response.put("comprasRealizadas", paciente.getComprasRealizadas());
            
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al obtener paciente: " + e.getMessage());
            return error;
        }
    }

    @PostMapping("/guardar")
    public String guardarPaciente(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("apellido") String apellido,
            @RequestParam("dni") String dni,
            @RequestParam("telefono") String telefono,
            @RequestParam(value = "genero", required = false) String genero,
            @RequestParam(value = "estado", required = false) String estado,
            RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== GUARDANDO PACIENTE ===");
            System.out.println("ID: " + id);
            System.out.println("Nombre: " + nombre);
            System.out.println("Apellido: " + apellido);
            System.out.println("DNI: " + dni);
            System.out.println("Teléfono: " + telefono);
            System.out.println("Género: " + genero);
            System.out.println("Estado: " + estado);
            System.out.println("==========================");
            
            // Validar campos obligatorios
            if (nombre == null || nombre.trim().isEmpty()) {
                redirectAttributes.addAttribute("error", "El nombre es obligatorio");
                return "redirect:/pacientes";
            }
            
            if (apellido == null || apellido.trim().isEmpty()) {
                redirectAttributes.addAttribute("error", "El apellido es obligatorio");
                return "redirect:/pacientes";
            }
            
            if (dni == null || dni.trim().isEmpty()) {
                redirectAttributes.addAttribute("error", "El DNI es obligatorio");
                return "redirect:/pacientes";
            }
            
            // Crear o buscar paciente
            Paciente paciente;
            if (id != null && id > 0) {
                // Editar paciente existente
                paciente = pacienteService.buscarPorId(id);
                if (paciente == null) {
                    redirectAttributes.addAttribute("error", "Paciente no encontrado");
                    return "redirect:/pacientes";
                }
            } else {
                // Nuevo paciente
                paciente = new Paciente();
                paciente.setComprasRealizadas(0);
            }
            
            // Llenar datos
            paciente.setNombre(nombre.trim());
            paciente.setApellido(apellido.trim());
            paciente.setDni(dni.trim());
            paciente.setTelefono(telefono.trim());
            paciente.setGenero(genero);
            
            // Establecer estado por defecto si viene vacío
            if (estado == null || estado.trim().isEmpty()) {
                paciente.setEstado("ACTIVO");
            } else {
                paciente.setEstado(estado);
            }
            
            // Verificar DNI duplicado
            Paciente existente = pacienteService.buscarPorDni(dni.trim());
            if (existente != null && !existente.getId().equals(paciente.getId())) {
                redirectAttributes.addAttribute("error", "Ya existe un paciente con el DNI: " + dni);
                return "redirect:/pacientes";
            }
            
            // Guardar paciente
            pacienteService.guardar(paciente);
            redirectAttributes.addAttribute("success", "Cliente guardado exitosamente");
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Error al guardar: " + e.getMessage());
        }
        
        return "redirect:/pacientes";
    }

    @PostMapping("/eliminar")
    public String eliminarPaciente(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            pacienteService.eliminar(id);
            redirectAttributes.addAttribute("deleted", "Cliente eliminado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/pacientes";
    }
}