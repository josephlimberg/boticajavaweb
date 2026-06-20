package com.java.boticaintegrador.controller;

import com.java.boticaintegrador.dto.ErrorResponse;
import com.java.boticaintegrador.model.Medicamento;
import com.java.boticaintegrador.model.Venta;
import com.java.boticaintegrador.model.DetalleVenta;
import com.java.boticaintegrador.service.MedicamentoService;
import com.java.boticaintegrador.service.PacienteService;
import com.java.boticaintegrador.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reportes")
public class ReportesController {

    @Autowired
    private MedicamentoService medicamentoService;

    @Autowired
    private VentaService ventaService;

    @Autowired
    private PacienteService pacienteService;

    @GetMapping
    public String mostrarReportes(
            @RequestParam(required = false) String mes,
            @RequestParam(required = false) String anio,
            HttpServletRequest request,
            Model model) {
        
        System.out.println("=== INICIANDO REPORTES ===");
        System.out.println("Mes seleccionado: " + mes);
        System.out.println("Año seleccionado: " + anio);
        
        try {
            // Procesar fecha seleccionada
            LocalDate fechaSeleccionada;
            if (mes != null && !mes.isEmpty()) {
                try {
                    fechaSeleccionada = LocalDate.parse(mes + "-01");
                } catch (Exception e) {
                    fechaSeleccionada = LocalDate.now().withDayOfMonth(1);
                }
            } else {
                fechaSeleccionada = LocalDate.now().withDayOfMonth(1);
            }
            
            System.out.println("Fecha procesada: " + fechaSeleccionada);
            
            // Obtener el año seleccionado
            Integer anioSeleccionado;
            try {
                anioSeleccionado = anio != null ? Integer.parseInt(anio) : LocalDate.now().getYear();
            } catch (Exception e) {
                anioSeleccionado = LocalDate.now().getYear();
            }
            System.out.println("Año seleccionado: " + anioSeleccionado);
            model.addAttribute("anioSeleccionado", anioSeleccionado);
            
            // 1. Obtener ventas del mes
            LocalDate inicio = fechaSeleccionada.withDayOfMonth(1);
            LocalDate fin = fechaSeleccionada.withDayOfMonth(fechaSeleccionada.lengthOfMonth());
            System.out.println("Rango: " + inicio + " - " + fin);
            
            List<Venta> ventasMes = new ArrayList<>();
            try {
                ventasMes = ventaService.buscarPorRangoFechas(inicio, fin);
                System.out.println("Ventas encontradas: " + ventasMes.size());
            } catch (Exception e) {
                System.out.println("Error al buscar ventas: " + e.getMessage());
                ventasMes = new ArrayList<>();
            }
            
            // 2. Calcular ventas totales
            BigDecimal ventasTotalesMes = BigDecimal.ZERO;
            for (Venta v : ventasMes) {
                if (v.getTotal() != null) {
                    ventasTotalesMes = ventasTotalesMes.add(v.getTotal());
                }
            }
            System.out.println("ventasTotalesMes: " + ventasTotalesMes);
            model.addAttribute("ventasTotalesMes", ventasTotalesMes);

            // 3. Calcular crecimiento
            Double crecimiento = 0.0;
            try {
                LocalDate mesAnterior = fechaSeleccionada.minusMonths(1);
                LocalDate inicioAnt = mesAnterior.withDayOfMonth(1);
                LocalDate finAnt = mesAnterior.withDayOfMonth(mesAnterior.lengthOfMonth());
                List<Venta> ventasAnt = ventaService.buscarPorRangoFechas(inicioAnt, finAnt);
                
                BigDecimal totalAnt = BigDecimal.ZERO;
                for (Venta v : ventasAnt) {
                    if (v.getTotal() != null) {
                        totalAnt = totalAnt.add(v.getTotal());
                    }
                }
                
                if (totalAnt.compareTo(BigDecimal.ZERO) > 0) {
                    crecimiento = ventasTotalesMes.subtract(totalAnt)
                            .divide(totalAnt, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .doubleValue();
                }
                System.out.println("crecimiento: " + crecimiento);
            } catch (Exception e) {
                System.out.println("Error al calcular crecimiento: " + e.getMessage());
            }
            model.addAttribute("crecimiento", crecimiento);

            // 4. Clientes Frecuentes
            long clientesFrecuentes = 0;
            try {
                clientesFrecuentes = pacienteService.contarFrecuentes();
            } catch (Exception e) {
                System.out.println("Error al contar clientes frecuentes: " + e.getMessage());
            }
            System.out.println("clientesFrecuentes: " + clientesFrecuentes);
            model.addAttribute("clientesFrecuentes", clientesFrecuentes);

            // 5. Stock Crítico
            long stockCritico = 0;
            try {
                stockCritico = contarStockCritico();
            } catch (Exception e) {
                System.out.println("Error al contar stock crítico: " + e.getMessage());
            }
            System.out.println("stockCritico: " + stockCritico);
            model.addAttribute("stockCritico", stockCritico);

            // 6. Ticket Promedio
            BigDecimal ticketPromedio = BigDecimal.ZERO;
            try {
                if (!ventasMes.isEmpty()) {
                    BigDecimal total = BigDecimal.ZERO;
                    for (Venta v : ventasMes) {
                        if (v.getTotal() != null) {
                            total = total.add(v.getTotal());
                        }
                    }
                    ticketPromedio = total.divide(BigDecimal.valueOf(ventasMes.size()), 2, RoundingMode.HALF_UP);
                }
            } catch (Exception e) {
                System.out.println("Error al calcular ticket promedio: " + e.getMessage());
            }
            System.out.println("ticketPromedio: " + ticketPromedio);
            model.addAttribute("ticketPromedio", ticketPromedio);

            // 7. Productos Más Vendidos
            List<Map<String, Object>> productosMasVendidos = new ArrayList<>();
            try {
                productosMasVendidos = obtenerProductosMasVendidos(ventasMes);
            } catch (Exception e) {
                System.out.println("Error al obtener productos más vendidos: " + e.getMessage());
                e.printStackTrace();
            }
            System.out.println("productosMasVendidos size: " + productosMasVendidos.size());
            model.addAttribute("productosMasVendidos", productosMasVendidos);

            // 8. Resumen Trimestral
            Map<String, BigDecimal> resumenTrimestral = new LinkedHashMap<>();
            try {
                resumenTrimestral = obtenerResumenTrimestral(fechaSeleccionada);
            } catch (Exception e) {
                System.out.println("Error al obtener resumen trimestral: " + e.getMessage());
            }
            model.addAttribute("resumenTrimestral", resumenTrimestral);

            // 9. Resumen Mensual del Año (todos los meses)
            Map<String, BigDecimal> resumenMensual = new LinkedHashMap<>();
            BigDecimal maxVentaAnual = BigDecimal.ZERO;
            try {
                Locale locale = new Locale("es", "ES");
                for (int i = 1; i <= 12; i++) {
                    LocalDate fecha = LocalDate.of(anioSeleccionado, i, 1);
                    String nombreMes = fecha.getMonth().getDisplayName(TextStyle.SHORT, locale);
                    nombreMes = nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1);
                    
                    LocalDate inicioMes = fecha.withDayOfMonth(1);
                    LocalDate finMes = fecha.withDayOfMonth(fecha.lengthOfMonth());
                    List<Venta> ventas = ventaService.buscarPorRangoFechas(inicioMes, finMes);
                    
                    BigDecimal total = BigDecimal.ZERO;
                    for (Venta v : ventas) {
                        if (v.getTotal() != null) {
                            total = total.add(v.getTotal());
                        }
                    }
                    resumenMensual.put(nombreMes, total);
                    if (total.compareTo(maxVentaAnual) > 0) {
                        maxVentaAnual = total;
                    }
                }
                System.out.println("resumenMensual: " + resumenMensual.size() + " meses");
                System.out.println("maxVentaAnual: " + maxVentaAnual);
            } catch (Exception e) {
                System.out.println("Error al obtener resumen mensual: " + e.getMessage());
            }
            model.addAttribute("resumenMensual", resumenMensual);
            model.addAttribute("maxVentaAnual", maxVentaAnual);

            // 10. Proyección
            BigDecimal proyeccion = BigDecimal.ZERO;
            try {
                if (!resumenTrimestral.isEmpty()) {
                    List<BigDecimal> valores = new ArrayList<>(resumenTrimestral.values());
                    if (!valores.isEmpty()) {
                        BigDecimal ultimo = valores.get(valores.size() - 1);
                        proyeccion = ultimo.multiply(BigDecimal.valueOf(1.05)).setScale(2, RoundingMode.HALF_UP);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error al calcular proyección: " + e.getMessage());
            }
            System.out.println("proyeccion: " + proyeccion);
            model.addAttribute("proyeccion", proyeccion);

            // 11. Categoría más vendida
            String categoriaDestacada = "Sin datos";
            Double porcentajeCategoria = 0.0;
            try {
                categoriaDestacada = obtenerCategoriaMasVendida(ventasMes);
                porcentajeCategoria = obtenerPorcentajeCategoria(categoriaDestacada, ventasMes);
            } catch (Exception e) {
                System.out.println("Error al obtener categoría destacada: " + e.getMessage());
            }
            model.addAttribute("categoriaDestacada", categoriaDestacada);
            model.addAttribute("porcentajeCategoria", porcentajeCategoria);

            // 12. Datos del mes
            String mesSeleccionado = fechaSeleccionada.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            model.addAttribute("mesSeleccionado", mesSeleccionado);
            
            String nombreMes = fechaSeleccionada.getMonth().getDisplayName(TextStyle.FULL, new Locale("es"));
            nombreMes = nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1);
            model.addAttribute("nombreMes", nombreMes + " " + fechaSeleccionada.getYear());

            System.out.println("=== REPORTES COMPLETADO EXITOSAMENTE ===");

        } catch (Exception e) {
            System.out.println("=== ERROR EN REPORTES ===");
            e.printStackTrace();
            
            // Crear ErrorResponse
            ErrorResponse error = new ErrorResponse(
                "Error al cargar los reportes: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getStackTrace().length > 0 ? e.getStackTrace()[0].toString() : "Sin detalles",
                System.currentTimeMillis()
            );
            
            // Valores por defecto
            model.addAttribute("ventasTotalesMes", BigDecimal.ZERO);
            model.addAttribute("crecimiento", 0.0);
            model.addAttribute("clientesFrecuentes", 0L);
            model.addAttribute("stockCritico", 0L);
            model.addAttribute("ticketPromedio", BigDecimal.ZERO);
            model.addAttribute("productosMasVendidos", new ArrayList<>());
            model.addAttribute("resumenTrimestral", new LinkedHashMap<>());
            model.addAttribute("resumenMensual", new LinkedHashMap<>());
            model.addAttribute("maxVentaAnual", BigDecimal.ZERO);
            model.addAttribute("proyeccion", BigDecimal.ZERO);
            model.addAttribute("categoriaDestacada", "Sin datos");
            model.addAttribute("porcentajeCategoria", 0.0);
            model.addAttribute("mesSeleccionado", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
            model.addAttribute("nombreMes", "Mes actual");
            model.addAttribute("anioSeleccionado", LocalDate.now().getYear());
            model.addAttribute("error", error);
        }

        return "reportes";
    }

    private long contarStockCritico() {
        try {
            List<Medicamento> medicamentos = medicamentoService.listarTodos();
            if (medicamentos == null || medicamentos.isEmpty()) {
                return 0L;
            }
            LocalDate hoy = LocalDate.now();
            return medicamentos.stream()
                    .filter(m -> m.getFechaVencimiento() != null && 
                                m.getFechaVencimiento().isAfter(hoy) &&
                                m.getStock() != null &&
                                m.getNivelMinimo() != null &&
                                m.getStock() < m.getNivelMinimo())
                    .count();
        } catch (Exception e) {
            System.out.println("Error en contarStockCritico: " + e.getMessage());
            return 0L;
        }
    }

    private List<Map<String, Object>> obtenerProductosMasVendidos(List<Venta> ventas) {
        Map<String, Map<String, Object>> productoStats = new HashMap<>();
        
        if (ventas == null || ventas.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            for (Venta venta : ventas) {
                if (venta.getDetalles() == null) continue;
                for (DetalleVenta detalle : venta.getDetalles()) {
                    if (detalle.getMedicamento() == null) continue;
                    String nombre = detalle.getMedicamento().getNombre();
                    if (nombre == null) continue;
                    
                    if (!productoStats.containsKey(nombre)) {
                        Map<String, Object> stats = new HashMap<>();
                        stats.put("nombre", nombre);
                        stats.put("categoria", detalle.getMedicamento().getCategoria() != null ? 
                                detalle.getMedicamento().getCategoria().getNombre() : "Sin categoría");
                        stats.put("proveedor", detalle.getMedicamento().getProveedor() != null ? 
                                detalle.getMedicamento().getProveedor() : "N/A");
                        stats.put("unidades", 0);
                        stats.put("ingresos", BigDecimal.ZERO);
                        productoStats.put(nombre, stats);
                    }
                    Map<String, Object> stats = productoStats.get(nombre);
                    stats.put("unidades", (Integer) stats.get("unidades") + detalle.getCantidad());
                    BigDecimal ingresos = (BigDecimal) stats.get("ingresos");
                    if (detalle.getSubtotal() != null) {
                        stats.put("ingresos", ingresos.add(detalle.getSubtotal()));
                    }
                }
            }

            return productoStats.values().stream()
                    .sorted((a, b) -> ((Integer) b.get("unidades")).compareTo((Integer) a.get("unidades")))
                    .limit(5)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Error en obtenerProductosMasVendidos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private Map<String, BigDecimal> obtenerResumenTrimestral(LocalDate fecha) {
        Map<String, BigDecimal> resumen = new LinkedHashMap<>();
        try {
            Locale locale = new Locale("es", "ES");
            
            for (int i = 2; i >= 0; i--) {
                LocalDate mes = fecha.minusMonths(i);
                String nombreMes = mes.getMonth().getDisplayName(TextStyle.SHORT, locale);
                nombreMes = nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1);
                
                LocalDate inicio = mes.withDayOfMonth(1);
                LocalDate fin = mes.withDayOfMonth(mes.lengthOfMonth());
                List<Venta> ventas = ventaService.buscarPorRangoFechas(inicio, fin);
                
                BigDecimal total = BigDecimal.ZERO;
                for (Venta v : ventas) {
                    if (v.getTotal() != null) {
                        total = total.add(v.getTotal());
                    }
                }
                resumen.put(nombreMes, total);
            }
        } catch (Exception e) {
            System.out.println("Error en obtenerResumenTrimestral: " + e.getMessage());
            resumen.put("Ene", BigDecimal.ZERO);
            resumen.put("Feb", BigDecimal.ZERO);
            resumen.put("Mar", BigDecimal.ZERO);
        }
        return resumen;
    }

    private String obtenerCategoriaMasVendida(List<Venta> ventas) {
        try {
            if (ventas == null || ventas.isEmpty()) {
                return "Sin datos";
            }
            
            Map<String, Integer> categoriaCount = new HashMap<>();
            for (Venta venta : ventas) {
                if (venta.getDetalles() == null) continue;
                for (DetalleVenta detalle : venta.getDetalles()) {
                    if (detalle.getMedicamento() == null || detalle.getMedicamento().getCategoria() == null) continue;
                    String categoria = detalle.getMedicamento().getCategoria().getNombre();
                    if (categoria != null) {
                        categoriaCount.put(categoria, categoriaCount.getOrDefault(categoria, 0) + detalle.getCantidad());
                    }
                }
            }

            return categoriaCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("Sin datos");
        } catch (Exception e) {
            System.out.println("Error en obtenerCategoriaMasVendida: " + e.getMessage());
            return "Sin datos";
        }
    }

    private Double obtenerPorcentajeCategoria(String categoria, List<Venta> ventas) {
        try {
            if (categoria == null || categoria.equals("Sin datos") || ventas == null || ventas.isEmpty()) {
                return 0.0;
            }
            
            int totalUnidades = 0;
            int categoriaUnidades = 0;

            for (Venta venta : ventas) {
                if (venta.getDetalles() == null) continue;
                for (DetalleVenta detalle : venta.getDetalles()) {
                    if (detalle.getMedicamento() == null || detalle.getMedicamento().getCategoria() == null) continue;
                    totalUnidades += detalle.getCantidad();
                    if (detalle.getMedicamento().getCategoria().getNombre().equals(categoria)) {
                        categoriaUnidades += detalle.getCantidad();
                    }
                }
            }

            if (totalUnidades == 0) return 0.0;
            double porcentaje = (categoriaUnidades * 100.0) / totalUnidades;
            return Math.round(porcentaje * 10) / 10.0;
        } catch (Exception e) {
            System.out.println("Error en obtenerPorcentajeCategoria: " + e.getMessage());
            return 0.0;
        }
    }
}