package com.java.boticaintegrador.service;

import com.java.boticaintegrador.model.Usuario;
import com.java.boticaintegrador.model.Venta;
import com.java.boticaintegrador.repository.UsuarioRepository;
import com.java.boticaintegrador.repository.VentaRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Venta guardar(Venta venta) {
        try {
            return ventaRepository.save(venta);
        } catch (Exception e) {
            System.out.println("Error al guardar venta: " + e.getMessage());
            return null;
        }
    }

    public Usuario obtenerUsuarioPorUsername(String username) {
        try {
            return usuarioRepository.findByUsername(username).orElse(null);
        } catch (Exception e) {
            System.out.println("Error al obtener usuario: " + e.getMessage());
            return null;
        }
    }

    public List<Venta> buscarPorFecha(LocalDate fecha) {
        try {
            return ventaRepository.findByFechaVenta(fecha);
        } catch (Exception e) {
            System.out.println("Error al buscar ventas por fecha: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public List<Venta> buscarPorRangoFechas(LocalDate inicio, LocalDate fin) {
        try {
            return ventaRepository.findByFechaVentaBetween(inicio, fin);
        } catch (Exception e) {
            System.out.println("Error al buscar ventas por rango: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Venta buscarPorId(Long id) {
        try {
            return ventaRepository.findById(id).orElse(null);
        } catch (Exception e) {
            System.out.println("Error al buscar venta por ID: " + e.getMessage());
            return null;
        }
    }
}