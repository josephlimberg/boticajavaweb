package com.java.boticaintegrador.controller;

import com.java.boticaintegrador.model.Medicamento;
import com.java.boticaintegrador.model.Paciente;
import com.java.boticaintegrador.model.Usuario;
import com.java.boticaintegrador.model.Venta;
import com.java.boticaintegrador.model.DetalleVenta;
import com.java.boticaintegrador.service.AccionService;
import com.java.boticaintegrador.service.MedicamentoService;
import com.java.boticaintegrador.service.PacienteService;
import com.java.boticaintegrador.service.UsuarioService;
import com.java.boticaintegrador.service.VentaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private MedicamentoService medicamentoService;

    @Autowired
    private VentaService ventaService;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private AccionService accionService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public String mostrarVenta(Model model, HttpServletRequest request) {
        try {
            // Obtener usuario autenticado
            String nombreUsuario = obtenerNombreUsuario();
            String rolUsuario = obtenerRolUsuario();
            model.addAttribute("nombreUsuario", nombreUsuario);
            model.addAttribute("rolUsuario", rolUsuario);

            accionService.registrarAccion(
                "SISTEMA",
                "Visualizó pantalla de ventas",
                "Ventas",
                "Usuario: " + nombreUsuario,
                request.getRemoteAddr()
            );
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("nombreUsuario", "Usuario");
            model.addAttribute("rolUsuario", "Rol");
        }
        return "registrarVenta";
    }

    @GetMapping("/buscar")
    @ResponseBody
    public List<Map<String, Object>> buscarMedicamentos(@RequestParam String query) {
        LocalDate hoy = LocalDate.now();
        List<Medicamento> medicamentos = medicamentoService.listarTodos();
        
        return medicamentos.stream()
                .filter(m -> m.getFechaVencimiento() != null && 
                            m.getFechaVencimiento().isAfter(hoy) &&
                            m.getStock() > 0 &&
                            m.getNombre().toLowerCase().contains(query.toLowerCase()))
                .map(m -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", m.getId());
                    map.put("nombre", m.getNombre());
                    map.put("precio", m.getPrecio());
                    map.put("stock", m.getStock());
                    map.put("formaFarmaceutica", m.getFormaFarmaceutica() != null ? m.getFormaFarmaceutica() : "");
                    return map;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/buscar-pacientes")
    @ResponseBody
    public List<Map<String, Object>> buscarPacientes(@RequestParam String query) {
        try {
            List<Paciente> pacientes = pacienteService.buscarPorNombre(query);
            return pacientes.stream().map(p -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", p.getId());
                map.put("nombre", p.getNombre());
                map.put("apellido", p.getApellido());
                map.put("nombreCompleto", p.getNombre() + " " + p.getApellido());
                map.put("dni", p.getDni());
                map.put("telefono", p.getTelefono());
                map.put("genero", p.getGenero());
                map.put("estado", p.getEstado());
                map.put("comprasRealizadas", p.getComprasRealizadas());
                map.put("tieneDescuento", p.tieneDescuentoEnProximaCompra());
                map.put("comprasFaltantes", p.comprasFaltantesParaDescuento());
                return map;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @GetMapping("/paciente/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerPaciente(@PathVariable Long id) {
        Paciente paciente = pacienteService.buscarPorId(id);
        if (paciente == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", paciente.getId());
        response.put("nombre", paciente.getNombre());
        response.put("apellido", paciente.getApellido());
        response.put("nombreCompleto", paciente.getNombre() + " " + paciente.getApellido());
        response.put("dni", paciente.getDni());
        response.put("telefono", paciente.getTelefono());
        response.put("genero", paciente.getGenero());
        response.put("estado", paciente.getEstado());
        response.put("comprasRealizadas", paciente.getComprasRealizadas());
        response.put("tieneDescuento", paciente.tieneDescuentoEnProximaCompra());
        response.put("comprasFaltantes", paciente.comprasFaltantesParaDescuento());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/registrar")
    public String registrarVenta(
            @RequestParam("detalles") String detallesJson,
            @RequestParam("total") BigDecimal total,
            @RequestParam("subtotal") BigDecimal subtotal,
            @RequestParam("igv") BigDecimal igv,
            @RequestParam("metodoPago") String metodoPago,
            @RequestParam("clienteNombre") String clienteNombre,
            @RequestParam(value = "pacienteId", required = false) Long pacienteId,
            @RequestParam(value = "descuento", required = false) BigDecimal descuento,
            Authentication authentication,
            HttpServletRequest request) {
        
        try {
            Usuario usuario = ventaService.obtenerUsuarioPorUsername(authentication.getName());
            
            Venta venta = new Venta();
            venta.setUsuario(usuario);
            venta.setTotal(total);
            venta.setMetodoPago(metodoPago);
            venta.setClienteAnonimoNombre(clienteNombre);
            venta.setFechaVenta(LocalDate.now());
            
            Long pacienteIdFinal = null;
            if (pacienteId != null && pacienteId > 0) {
                pacienteIdFinal = pacienteId;
                pacienteService.incrementarCompras(pacienteId);
            }
            
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> detallesList = objectMapper.readValue(detallesJson, List.class);
            
            for (Map<String, Object> detalleMap : detallesList) {
                Long medicamentoId = Long.valueOf(detalleMap.get("medicamentoId").toString());
                Integer cantidad = Integer.valueOf(detalleMap.get("cantidad").toString());
                BigDecimal precioUnitario = new BigDecimal(detalleMap.get("precioUnitario").toString());
                BigDecimal subtotalDetalle = new BigDecimal(detalleMap.get("subtotal").toString());
                
                Medicamento medicamento = medicamentoService.buscarPorId(medicamentoId);
                
                if (medicamento.getStock() < cantidad) {
                    throw new RuntimeException("Stock insuficiente para: " + medicamento.getNombre());
                }
                
                medicamento.setStock(medicamento.getStock() - cantidad);
                medicamentoService.guardar(medicamento);
                
                DetalleVenta detalle = new DetalleVenta();
                detalle.setMedicamento(medicamento);
                detalle.setCantidad(cantidad);
                detalle.setPrecioUnitario(precioUnitario);
                detalle.setSubtotal(subtotalDetalle);
                detalle.setVenta(venta);
                
                venta.getDetalles().add(detalle);
            }
            
            ventaService.guardar(venta);
            
            String descripcionAccion = "Registró nueva venta para " + clienteNombre;
            String detallesAccion = "Total: S/" + total + 
                                   ", Método: " + metodoPago +
                                   (pacienteIdFinal != null ? ", Paciente ID: " + pacienteIdFinal : ", Cliente anónimo") +
                                   (descuento != null && descuento.compareTo(BigDecimal.ZERO) > 0 ? ", Descuento: S/" + descuento : "");
            
            accionService.registrarAccion(
                "CREACIÓN",
                descripcionAccion,
                "Ventas",
                detallesAccion,
                request.getRemoteAddr()
            );
            
            return "redirect:/ventas?success";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/ventas?error=" + e.getMessage();
        }
    }

    @GetMapping("/diarias")
    public String ventasDiarias(Model model, @RequestParam(required = false) String fecha, HttpServletRequest request) {
        try {
            // Obtener usuario autenticado
            String nombreUsuario = obtenerNombreUsuario();
            String rolUsuario = obtenerRolUsuario();
            model.addAttribute("nombreUsuario", nombreUsuario);
            model.addAttribute("rolUsuario", rolUsuario);

            accionService.registrarAccion(
                "SISTEMA",
                "Visualizó ventas diarias",
                "Ventas",
                "Fecha: " + fecha + ", Usuario: " + nombreUsuario,
                request.getRemoteAddr()
            );
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("nombreUsuario", "Usuario");
            model.addAttribute("rolUsuario", "Rol");
        }

        LocalDate fechaFiltro = fecha != null ? LocalDate.parse(fecha) : LocalDate.now();
        
        List<Venta> ventas = ventaService.buscarPorFecha(fechaFiltro);
        
        List<Map<String, Object>> ventasDTO = ventas.stream().map(v -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", v.getId());
            map.put("clienteAnonimoNombre", v.getClienteAnonimoNombre() != null ? v.getClienteAnonimoNombre() : "Anónimo");
            map.put("total", v.getTotal());
            map.put("metodoPago", v.getMetodoPago());
            map.put("fechaHoraFormateada", v.getFechaVenta().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            
            int totalProductos = v.getDetalles().stream().mapToInt(DetalleVenta::getCantidad).sum();
            String productosResumen = v.getDetalles().stream()
                    .limit(2)
                    .map(d -> d.getMedicamento().getNombre())
                    .collect(Collectors.joining(", "));
            if (v.getDetalles().size() > 2) productosResumen += " +" + (v.getDetalles().size() - 2);
            
            map.put("totalProductos", totalProductos);
            map.put("productosResumen", productosResumen);
            
            return map;
        }).collect(Collectors.toList());
        
        model.addAttribute("ventas", ventasDTO);
        model.addAttribute("fechaSeleccionada", fechaFiltro.toString());
        
        return "ventasDiarias";
    }

    @GetMapping("/detalle/{id}")
    @ResponseBody
    public Map<String, Object> obtenerDetalleVenta(@PathVariable Long id) {
        Venta venta = ventaService.buscarPorId(id);
        
        List<Map<String, Object>> productos = venta.getDetalles().stream().map(d -> {
            Map<String, Object> p = new HashMap<>();
            p.put("nombre", d.getMedicamento().getNombre());
            p.put("cantidad", d.getCantidad());
            p.put("precio", d.getPrecioUnitario());
            p.put("subtotal", d.getSubtotal());
            return p;
        }).collect(Collectors.toList());
        
        BigDecimal subtotal = venta.getTotal().divide(new BigDecimal("1.18"), 2, RoundingMode.HALF_UP);
        BigDecimal igv = venta.getTotal().subtract(subtotal);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", venta.getId());
        response.put("clienteNombre", venta.getClienteAnonimoNombre() != null ? venta.getClienteAnonimoNombre() : "Anónimo");
        response.put("metodoPago", venta.getMetodoPago());
        response.put("productos", productos);
        response.put("subtotal", subtotal);
        response.put("igv", igv);
        response.put("total", venta.getTotal());
        response.put("fecha", venta.getFechaVenta().toString());
        
        return response;
    }

    // Métodos auxiliares para obtener el usuario autenticado
    private String obtenerNombreUsuario() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                String username = auth.getName();
                Usuario usuario = usuarioService.buscarPorUsername(username);
                return usuario != null ? usuario.getNombreCompleto() : username;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Usuario";
    }

    private String obtenerRolUsuario() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                String username = auth.getName();
                Usuario usuario = usuarioService.buscarPorUsername(username);
                return usuario != null ? usuario.getRol() : "Rol";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Rol";
    }
}