package com.java.boticaintegrador.controller;

import com.java.boticaintegrador.model.Medicamento;
import com.java.boticaintegrador.model.Usuario;
import com.java.boticaintegrador.model.Venta;
import com.java.boticaintegrador.model.DetalleVenta;
import com.java.boticaintegrador.service.MedicamentoService;
import com.java.boticaintegrador.service.VentaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private ObjectMapper objectMapper;

    @GetMapping
    public String mostrarVenta(Model model) {
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

    @PostMapping("/registrar")
    public String registrarVenta(
            @RequestParam("detalles") String detallesJson,
            @RequestParam("total") BigDecimal total,
            @RequestParam("subtotal") BigDecimal subtotal,
            @RequestParam("igv") BigDecimal igv,
            @RequestParam("metodoPago") String metodoPago,
            @RequestParam("clienteNombre") String clienteNombre,
            Authentication authentication) {
        
        try {
            // Obtener usuario actual
            Usuario usuario = ventaService.obtenerUsuarioPorUsername(authentication.getName());
            
            // Crear venta
            Venta venta = new Venta();
            venta.setUsuario(usuario);
            venta.setTotal(total);
            venta.setMetodoPago(metodoPago);
            venta.setClienteAnonimoNombre(clienteNombre);
            venta.setFechaVenta(LocalDate.now());
            
            // Parsear detalles
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> detallesList = objectMapper.readValue(detallesJson, List.class);
            
            // Procesar cada detalle
            for (Map<String, Object> detalleMap : detallesList) {
                Long medicamentoId = Long.valueOf(detalleMap.get("medicamentoId").toString());
                Integer cantidad = Integer.valueOf(detalleMap.get("cantidad").toString());
                BigDecimal precioUnitario = new BigDecimal(detalleMap.get("precioUnitario").toString());
                BigDecimal subtotalDetalle = new BigDecimal(detalleMap.get("subtotal").toString());
                
                // Obtener medicamento
                Medicamento medicamento = medicamentoService.buscarPorId(medicamentoId);
                
                // Validar stock
                if (medicamento.getStock() < cantidad) {
                    throw new RuntimeException("Stock insuficiente para: " + medicamento.getNombre());
                }
                
                // Actualizar stock
                medicamento.setStock(medicamento.getStock() - cantidad);
                medicamentoService.guardar(medicamento);
                
                // Crear detalle
                DetalleVenta detalle = new DetalleVenta();
                detalle.setMedicamento(medicamento);
                detalle.setCantidad(cantidad);
                detalle.setPrecioUnitario(precioUnitario);
                detalle.setSubtotal(subtotalDetalle);
                detalle.setVenta(venta);
                
                venta.getDetalles().add(detalle);
            }
            
            // Guardar venta
            ventaService.guardar(venta);
            
            return "redirect:/ventas?success";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/ventas?error=" + e.getMessage();
        }
    }
}