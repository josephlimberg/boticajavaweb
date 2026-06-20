package com.java.boticaintegrador.controller;

import com.java.boticaintegrador.model.Medicamento;
import com.java.boticaintegrador.model.Usuario;
import com.java.boticaintegrador.model.Venta;
import com.java.boticaintegrador.service.AccionService;
import com.java.boticaintegrador.service.MedicamentoService;
import com.java.boticaintegrador.service.UsuarioService;
import com.java.boticaintegrador.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class InicioController {

    @Autowired
    private MedicamentoService medicamentoService;

    @Autowired
    private VentaService ventaService;

    @Autowired
    private AccionService accionService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String inicio(Model model, HttpServletRequest request) {
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

            // Registrar acción de visualización
            accionService.registrarAccion(
                "SISTEMA",
                "Visualizó el panel de inicio",
                "Dashboard",
                "Usuario: " + nombreUsuario,
                request.getRemoteAddr()
            );

            // 1. Total de medicamentos
            List<Medicamento> medicamentos = medicamentoService.listarTodos();
            long totalMedicamentos = medicamentos.size();
            model.addAttribute("totalMedicamentos", totalMedicamentos);

            // 2. Alertas y stock crítico
            LocalDate hoy = LocalDate.now();
            LocalDate fechaLimite = hoy.plusDays(30);

            long alertasCriticas = medicamentos.stream()
                    .filter(m -> m.getFechaVencimiento() != null && 
                                m.getFechaVencimiento().isAfter(hoy) &&
                                m.getStock() < m.getNivelMinimo())
                    .count();

            long proximosAVencer = medicamentos.stream()
                    .filter(m -> m.getFechaVencimiento() != null && 
                                m.getFechaVencimiento().isAfter(hoy) &&
                                m.getFechaVencimiento().isBefore(fechaLimite))
                    .count();

            long totalAlertas = alertasCriticas + proximosAVencer;

            model.addAttribute("alertasCriticas", alertasCriticas);
            model.addAttribute("proximosAVencer", proximosAVencer);
            model.addAttribute("totalAlertas", totalAlertas);

            // 3. Ventas del día
            List<Venta> ventasHoy = ventaService.buscarPorFecha(hoy);
            BigDecimal ventasDia = ventasHoy.stream()
                    .map(Venta::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            model.addAttribute("ventasDia", ventasDia);

            // 4. Producto más vendido
            Map<String, Integer> ventasPorMedicamento = new HashMap<>();
            for (Venta venta : ventasHoy) {
                if (venta.getDetalles() != null) {
                    venta.getDetalles().forEach(detalle -> {
                        String nombre = detalle.getMedicamento().getNombre();
                        ventasPorMedicamento.put(nombre, 
                            ventasPorMedicamento.getOrDefault(nombre, 0) + detalle.getCantidad());
                    });
                }
            }

            String productoMasVendido = "-";
            int unidadesMasVendidas = 0;
            if (!ventasPorMedicamento.isEmpty()) {
                Map.Entry<String, Integer> maxEntry = ventasPorMedicamento.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .orElse(null);
                if (maxEntry != null) {
                    productoMasVendido = maxEntry.getKey();
                    unidadesMasVendidas = maxEntry.getValue();
                }
            }
            model.addAttribute("productoMasVendido", productoMasVendido);
            model.addAttribute("productoMasVendidoUnidades", unidadesMasVendidas);

            // 5. Alertas para la tabla
            List<Map<String, Object>> alertas = new ArrayList<>();
            medicamentos.stream()
                    .filter(m -> m.getFechaVencimiento() != null && 
                                m.getFechaVencimiento().isAfter(hoy) &&
                                m.getStock() < m.getNivelMinimo())
                    .limit(5)
                    .forEach(m -> {
                        Map<String, Object> alerta = new HashMap<>();
                        alerta.put("nombre", m.getNombre());
                        alerta.put("categoria", m.getCategoria().getNombre());
                        alerta.put("stock", m.getStock());
                        alerta.put("nivelMinimo", m.getNivelMinimo());
                        alertas.add(alerta);
                    });
            model.addAttribute("alertas", alertas);

            // 6. Capacidad utilizada (simulada)
            int capacidadUtilizada = 78;
            model.addAttribute("capacidadUtilizada", capacidadUtilizada);

        } catch (Exception e) {
            e.printStackTrace();
            
            // Valores por defecto en caso de error
            model.addAttribute("nombreUsuario", "Usuario");
            model.addAttribute("rolUsuario", "Rol");
            model.addAttribute("totalMedicamentos", 0);
            model.addAttribute("alertasCriticas", 0);
            model.addAttribute("proximosAVencer", 0);
            model.addAttribute("totalAlertas", 0);
            model.addAttribute("ventasDia", BigDecimal.ZERO);
            model.addAttribute("productoMasVendido", "-");
            model.addAttribute("productoMasVendidoUnidades", 0);
            model.addAttribute("alertas", new ArrayList<>());
            model.addAttribute("capacidadUtilizada", 0);
        }

        return "inicio";
    }
}