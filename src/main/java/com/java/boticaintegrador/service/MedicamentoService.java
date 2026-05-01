package com.java.boticaintegrador.service;

import com.java.boticaintegrador.model.Medicamento;
import com.java.boticaintegrador.repository.MedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicamentoService {

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    public List<Medicamento> listarTodos() {
        return medicamentoRepository.findAll();
    }

    public void guardar(Medicamento medicamento) {
        // Aquí en el futuro (Sprint 3) pondremos la lógica para validar alertas
        medicamentoRepository.save(medicamento);
    }
}