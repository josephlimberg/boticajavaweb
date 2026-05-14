package com.java.boticaintegrador.service;

import com.java.boticaintegrador.model.Medicamento;
import com.java.boticaintegrador.repository.MedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicamentoService {

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    public List<Medicamento> listarTodos() {
        return medicamentoRepository.findAll();
    }

    @Transactional
    public Medicamento guardar(Medicamento medicamento) {
        return medicamentoRepository.save(medicamento);
    }

    public Medicamento buscarPorId(Long id) {
        return medicamentoRepository.findById(id).orElse(null);
    }

    @Transactional
    public void eliminar(Long id) {
        medicamentoRepository.deleteById(id);
    }
}