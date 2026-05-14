package com.java.boticaintegrador.service;

import com.java.boticaintegrador.model.Lote;
import com.java.boticaintegrador.model.Medicamento;
import com.java.boticaintegrador.repository.LoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LoteService {

    @Autowired
    private LoteRepository loteRepository;

    public List<Lote> listarPorMedicamento(Medicamento medicamento) {
        return loteRepository.findByMedicamento(medicamento);
    }

    public List<Lote> listarPorMedicamentoId(Long medicamentoId) {
        return loteRepository.findByMedicamentoId(medicamentoId);
    }

    @Transactional
    public Lote guardar(Lote lote) {
        return loteRepository.save(lote);
    }
    
    @Transactional
    public Lote guardarActualizando(Lote lote) {
        // Buscar si ya existe un lote con el mismo código para este medicamento
        List<Lote> existentes = loteRepository.findByMedicamentoId(lote.getMedicamento().getId());
        Lote loteExistente = existentes.stream()
                .filter(l -> l.getCodigoLote().equals(lote.getCodigoLote()))
                .findFirst()
                .orElse(null);
        
        if (loteExistente != null) {
            // Actualizar el lote existente
            loteExistente.setStock(lote.getStock());
            loteExistente.setFechaVencimiento(lote.getFechaVencimiento());
            loteExistente.setFechaFabricacion(lote.getFechaFabricacion());
            loteExistente.setPrecioCompra(lote.getPrecioCompra());
            return loteRepository.save(loteExistente);
        } else {
            // Crear nuevo lote
            return loteRepository.save(lote);
        }
    }

    @Transactional
    public void eliminar(Long id) {
        loteRepository.deleteById(id);
    }

    public Lote buscarPorId(Long id) {
        return loteRepository.findById(id).orElse(null);
    }
}