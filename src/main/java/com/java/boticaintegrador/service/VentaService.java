package com.java.boticaintegrador.service;

import com.java.boticaintegrador.model.Usuario;
import com.java.boticaintegrador.model.Venta;
import com.java.boticaintegrador.repository.UsuarioRepository;
import com.java.boticaintegrador.repository.VentaRepository;
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
        return ventaRepository.save(venta);
    }

    public Usuario obtenerUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username).orElse(null);
    }
}