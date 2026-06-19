package com.java.boticaintegrador.service;

import com.java.boticaintegrador.model.Paciente;
import com.java.boticaintegrador.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    public List<Paciente> listarTodos() {
        try {
            return pacienteRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<Paciente> buscarPorNombre(String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return List.of();
            }
            return pacienteRepository.buscarPorNombreApellidoODni(query.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public Paciente buscarPorId(Long id) {
        try {
            return pacienteRepository.findById(id).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Paciente buscarPorDni(String dni) {
        try {
            return pacienteRepository.findByDni(dni).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public Paciente guardar(Paciente paciente) {
        try {
            if (paciente.getId() == null) {
                paciente.setComprasRealizadas(0);
            }
            return pacienteRepository.save(paciente);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    public void eliminar(Long id) {
        try {
            pacienteRepository.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    public void incrementarCompras(Long id) {
        try {
            Paciente paciente = buscarPorId(id);
            if (paciente != null) {
                paciente.setComprasRealizadas(paciente.getComprasRealizadas() + 1);
                pacienteRepository.save(paciente);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public long contarTotal() {
        try {
            return pacienteRepository.count();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public long contarActivos() {
        try {
            return pacienteRepository.countByEstado("ACTIVO");
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public long contarFrecuentes() {
        try {
            return pacienteRepository.countByComprasRealizadasGreaterThanEqual(5);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public long contarNuevosHoy() {
        try {
            LocalDateTime inicio = LocalDate.now().atStartOfDay();
            LocalDateTime fin = LocalDate.now().atTime(23, 59, 59);
            return pacienteRepository.countByCreatedAtBetween(inicio, fin);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
}