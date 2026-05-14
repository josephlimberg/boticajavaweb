package com.java.boticaintegrador.repository;

import com.java.boticaintegrador.model.Lote;
import com.java.boticaintegrador.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {
    List<Lote> findByMedicamento(Medicamento medicamento);
    List<Lote> findByMedicamentoId(Long medicamentoId);
}