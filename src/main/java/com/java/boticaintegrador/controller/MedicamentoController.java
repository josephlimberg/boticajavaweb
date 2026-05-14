package com.java.boticaintegrador.controller;

import com.java.boticaintegrador.dto.MedicamentoLoteDTO;
import com.java.boticaintegrador.model.Categoria;
import com.java.boticaintegrador.model.Lote;
import com.java.boticaintegrador.model.Medicamento;
import com.java.boticaintegrador.service.CategoriaService;
import com.java.boticaintegrador.service.LoteService;
import com.java.boticaintegrador.service.MedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping
public String listarInventario(Model model) {
    List<Medicamento> medicamentos = medicamentoService.listarTodos();
    LocalDate hoy = LocalDate.now();
    LocalDate fechaLimiteVencimiento = hoy.plusDays(30);
    
    // Calcular estadísticas
    // Stock total SOLO de medicamentos NO vencidos
    int stockTotal = medicamentos.stream()
            .filter(m -> m.getFechaVencimiento() != null && 
                         m.getFechaVencimiento().isAfter(hoy))
            .mapToInt(Medicamento::getStock)
            .sum();
    
    // Próximos a vencer (30 días) y que NO estén vencidos
    long proximosAVencer = medicamentos.stream()
            .filter(m -> m.getFechaVencimiento() != null && 
                         m.getFechaVencimiento().isAfter(hoy) &&
                         m.getFechaVencimiento().isBefore(fechaLimiteVencimiento))
            .count();
    
    // Stock bajo (menor al nivel mínimo) y NO vencidos
    long stockBajo = medicamentos.stream()
            .filter(m -> m.getFechaVencimiento() != null && 
                         m.getFechaVencimiento().isAfter(hoy) &&
                         m.getStock() < m.getNivelMinimo())
            .count();
    
    // Vencidos (solo para mostrar en tarjeta)
    long vencidos = medicamentos.stream()
            .filter(m -> m.getFechaVencimiento() != null && 
                         m.getFechaVencimiento().isBefore(hoy))
            .count();
    
    // Para cada medicamento, obtener información del lote (primer lote activo)
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
    model.addAttribute("vencidos", vencidos); // Agregar vencidos para mostrar

    return "inventario";
}

    @PostMapping("/guardar")
    public String guardarMedicamentoConLote(@ModelAttribute MedicamentoLoteDTO dto) {
        System.out.println("=== DEBUG ===");
        System.out.println("ID: " + dto.getId());
        System.out.println("Nombre: " + dto.getNombre());
        System.out.println("CategoriaId: " + dto.getCategoriaId());
        System.out.println("CodigoLote: " + dto.getCodigoLote());
        System.out.println("============");
        
        // Validar que la categoría no sea null
        if (dto.getCategoriaId() == null) {
            throw new RuntimeException("La categoría es obligatoria");
        }
        
        // 1. Obtener o crear medicamento
        Medicamento medicamento;
        if (dto.getId() != null && dto.getId() > 0) {
            medicamento = medicamentoService.buscarPorId(dto.getId());
            if (medicamento == null) {
                medicamento = new Medicamento();
            }
        } else {
            medicamento = new Medicamento();
        }
        
        // 2. Llenar datos del medicamento
        medicamento.setNombre(dto.getNombre());
        
        // Obtener la categoría por ID
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
        
        // 3. Guardar medicamento primero
        Medicamento medicamentoGuardado = medicamentoService.guardar(medicamento);
        
        // 4. Crear o actualizar lote si se proporcionó código
        if (dto.getCodigoLote() != null && !dto.getCodigoLote().trim().isEmpty()) {
            Lote lote = new Lote();
            lote.setMedicamento(medicamentoGuardado);
            lote.setCodigoLote(dto.getCodigoLote());
            lote.setStock(dto.getStockLote() != null ? dto.getStockLote() : dto.getStock());
            lote.setFechaVencimiento(dto.getFechaVencimiento());
            lote.setFechaFabricacion(dto.getFechaFabricacion());
            lote.setPrecioCompra(dto.getPrecioCompra());
            
            // Usar el método que maneja actualización en lugar de guardar directamente
            loteService.guardarActualizando(lote);
        }
        
        return "redirect:/medicamentos";
    }

    @PostMapping("/eliminar")
    public String eliminarMedicamento(@RequestParam("id") Long id) {
        // Primero eliminar los lotes asociados
        List<Lote> lotes = loteService.listarPorMedicamentoId(id);
        for (Lote lote : lotes) {
            loteService.eliminar(lote.getId());
        }
        // Luego eliminar el medicamento
        medicamentoService.eliminar(id);
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
    
    // Información del primer lote activo
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
}