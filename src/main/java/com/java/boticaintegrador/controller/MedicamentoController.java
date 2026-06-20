package com.java.boticaintegrador.controller;

import com.java.boticaintegrador.dto.MedicamentoLoteDTO;
import com.java.boticaintegrador.model.Categoria;
import com.java.boticaintegrador.model.Lote;
import com.java.boticaintegrador.model.Medicamento;
import com.java.boticaintegrador.model.Usuario;
import com.java.boticaintegrador.service.AccionService;
import com.java.boticaintegrador.service.CategoriaService;
import com.java.boticaintegrador.service.LoteService;
import com.java.boticaintegrador.service.MedicamentoService;
import com.java.boticaintegrador.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/medicamentos")
public class MedicamentoController {

    @Autowired
    private MedicamentoService medicamentoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private LoteService loteService;

    @Autowired
    private AccionService accionService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listarInventario(Model model, HttpServletRequest request) {
        try {
            // Obtener usuario autenticado
            String nombreUsuario = obtenerNombreUsuario();
            model.addAttribute("nombreUsuario", nombreUsuario);
            model.addAttribute("rolUsuario", obtenerRolUsuario());

            // Registrar acción
            accionService.registrarAccion(
                "SISTEMA",
                "Visualizó el inventario",
                "Inventario",
                null,
                request.getRemoteAddr()
            );

            List<Medicamento> medicamentos = medicamentoService.listarTodos();
            LocalDate hoy = LocalDate.now();
            LocalDate fechaLimiteVencimiento = hoy.plusDays(30);
            
            // Calcular estadísticas
            int stockTotal = medicamentos.stream()
                    .filter(m -> m.getFechaVencimiento() != null && 
                                 m.getFechaVencimiento().isAfter(hoy))
                    .mapToInt(Medicamento::getStock)
                    .sum();
            
            long proximosAVencer = medicamentos.stream()
                    .filter(m -> m.getFechaVencimiento() != null && 
                                 m.getFechaVencimiento().isAfter(hoy) &&
                                 m.getFechaVencimiento().isBefore(fechaLimiteVencimiento))
                    .count();
            
            long stockBajo = medicamentos.stream()
                    .filter(m -> m.getFechaVencimiento() != null && 
                                 m.getFechaVencimiento().isAfter(hoy) &&
                                 m.getStock() < m.getNivelMinimo())
                    .count();
            
            long vencidos = medicamentos.stream()
                    .filter(m -> m.getFechaVencimiento() != null && 
                                 m.getFechaVencimiento().isBefore(hoy))
                    .count();
            
            for (Medicamento m : medicamentos) {
                List<Lote> lotes = loteService.listarPorMedicamento(m);
                if (!lotes.isEmpty()) {
                    Lote loteActivo = lotes.stream()
                            .filter(l -> l.getStock() > 0 && l.getFechaVencimiento().isAfter(LocalDate.now()))
                            .findFirst()
                            .orElse(lotes.get(0));
                    m.setLoteInfo(loteActivo.getCodigoLote() + " (Stock: " + loteActivo.getStock() + ")");
                } else {
                    m.setLoteInfo("Sin lote");
                }
            }
            
            model.addAttribute("listaMedicamentos", medicamentos);
            model.addAttribute("listaCategorias", categoriaService.listarTodas());
            model.addAttribute("stockTotal", stockTotal);
            model.addAttribute("proximosAVencer", proximosAVencer);
            model.addAttribute("stockBajo", stockBajo);
            model.addAttribute("vencidos", vencidos);

            return "inventario";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("nombreUsuario", "Usuario");
            model.addAttribute("rolUsuario", "Rol");
            model.addAttribute("listaMedicamentos", List.of());
            model.addAttribute("listaCategorias", List.of());
            model.addAttribute("stockTotal", 0);
            model.addAttribute("proximosAVencer", 0);
            model.addAttribute("stockBajo", 0);
            model.addAttribute("vencidos", 0);
            return "inventario";
        }
    }

    @PostMapping("/guardar")
    public String guardarMedicamentoConLote(@ModelAttribute MedicamentoLoteDTO dto, HttpServletRequest request) {
        System.out.println("=== DEBUG ===");
        System.out.println("ID: " + dto.getId());
        System.out.println("Nombre: " + dto.getNombre());
        System.out.println("CategoriaId: " + dto.getCategoriaId());
        System.out.println("CodigoLote: " + dto.getCodigoLote());
        System.out.println("============");
        
        if (dto.getCategoriaId() == null) {
            throw new RuntimeException("La categoría es obligatoria");
        }
        
        Medicamento medicamento;
        boolean esNuevo = false;
        if (dto.getId() != null && dto.getId() > 0) {
            medicamento = medicamentoService.buscarPorId(dto.getId());
            if (medicamento == null) {
                medicamento = new Medicamento();
                esNuevo = true;
            }
        } else {
            medicamento = new Medicamento();
            esNuevo = true;
        }
        
        medicamento.setNombre(dto.getNombre());
        
        Categoria categoria = categoriaService.buscarPorId(dto.getCategoriaId());
        if (categoria == null) {
            throw new RuntimeException("Categoría no encontrada con ID: " + dto.getCategoriaId());
        }
        medicamento.setCategoria(categoria);
        
        medicamento.setFormaFarmaceutica(dto.getFormaFarmaceutica());
        medicamento.setDescripcion(dto.getDescripcion());
        medicamento.setProveedor(dto.getProveedor());
        medicamento.setStock(dto.getStock() != null ? dto.getStock() : 0);
        medicamento.setNivelMinimo(dto.getNivelMinimo() != null ? dto.getNivelMinimo() : 20);
        medicamento.setPrecio(dto.getPrecio() != null ? dto.getPrecio() : BigDecimal.ZERO);
        medicamento.setUbicacionEstante(dto.getUbicacionEstante());
        medicamento.setFechaVencimiento(dto.getFechaVencimiento());
        
        Medicamento medicamentoGuardado = medicamentoService.guardar(medicamento);
        
        if (dto.getCodigoLote() != null && !dto.getCodigoLote().trim().isEmpty()) {
            Lote lote = new Lote();
            lote.setMedicamento(medicamentoGuardado);
            lote.setCodigoLote(dto.getCodigoLote());
            lote.setStock(dto.getStockLote() != null ? dto.getStockLote() : dto.getStock());
            lote.setFechaVencimiento(dto.getFechaVencimiento());
            lote.setFechaFabricacion(dto.getFechaFabricacion());
            lote.setPrecioCompra(dto.getPrecioCompra());
            
            loteService.guardarActualizando(lote);
        }
        
        // Registrar acción con el usuario autenticado
        String tipoAccion = esNuevo ? "CREACIÓN" : "MODIFICACIÓN";
        String descripcion = esNuevo ? 
            "Creó nuevo medicamento: " + dto.getNombre() : 
            "Modificó medicamento: " + dto.getNombre();
        String detalles = "ID: " + medicamentoGuardado.getId() + 
                          ", Categoría: " + categoria.getNombre() +
                          ", Stock: " + medicamentoGuardado.getStock() +
                          ", Precio: S/" + medicamentoGuardado.getPrecio();
        
        accionService.registrarAccion(
            tipoAccion,
            descripcion,
            "Inventario",
            detalles,
            request.getRemoteAddr()
        );
        
        return "redirect:/medicamentos";
    }

    @PostMapping("/eliminar")
    public String eliminarMedicamento(@RequestParam("id") Long id, HttpServletRequest request) {
        Medicamento medicamento = medicamentoService.buscarPorId(id);
        String nombreMedicamento = medicamento != null ? medicamento.getNombre() : "ID: " + id;
        
        List<Lote> lotes = loteService.listarPorMedicamentoId(id);
        for (Lote lote : lotes) {
            loteService.eliminar(lote.getId());
        }
        medicamentoService.eliminar(id);
        
        accionService.registrarAccion(
            "ELIMINACIÓN",
            "Eliminó medicamento: " + nombreMedicamento,
            "Inventario",
            "ID eliminado: " + id,
            request.getRemoteAddr()
        );
        
        return "redirect:/medicamentos";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Map<String, Object> obtenerMedicamentoConLotes(@PathVariable Long id) {
        Medicamento medicamento = medicamentoService.buscarPorId(id);
        List<Lote> lotes = loteService.listarPorMedicamento(medicamento);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", medicamento.getId());
        response.put("nombre", medicamento.getNombre());
        response.put("categoriaId", medicamento.getCategoria().getId());
        response.put("formaFarmaceutica", medicamento.getFormaFarmaceutica());
        response.put("descripcion", medicamento.getDescripcion());
        response.put("proveedor", medicamento.getProveedor());
        response.put("stock", medicamento.getStock());
        response.put("nivelMinimo", medicamento.getNivelMinimo());
        response.put("precio", medicamento.getPrecio());
        response.put("ubicacionEstante", medicamento.getUbicacionEstante());
        response.put("fechaVencimiento", medicamento.getFechaVencimiento().toString());
        
        if (!lotes.isEmpty()) {
            Lote loteActivo = lotes.stream()
                    .filter(l -> l.getStock() > 0 && l.getFechaVencimiento().isAfter(LocalDate.now()))
                    .findFirst()
                    .orElse(lotes.get(0));
            response.put("codigoLote", loteActivo.getCodigoLote());
            response.put("stockLote", loteActivo.getStock());
            response.put("fechaFabricacion", loteActivo.getFechaFabricacion() != null ? loteActivo.getFechaFabricacion().toString() : "");
            response.put("precioCompra", loteActivo.getPrecioCompra() != null ? loteActivo.getPrecioCompra() : 0);
        }
        
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