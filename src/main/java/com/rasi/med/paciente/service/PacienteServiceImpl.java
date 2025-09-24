package com.rasi.med.paciente.service;

import com.rasi.med.paciente.Paciente;
import com.rasi.med.paciente.repository.PacienteRepository;
import org.springframework.stereotype.Service;

@Service
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteServiceImpl(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public Paciente save(Paciente p) {
        return pacienteRepository.save(p);
    }

    @Override
    public void softDelete(Paciente p) {
        pacienteRepository.delete(p);
    }

    public Paciente update(Paciente p) {
        Paciente existente = pacienteRepository.findById(p.getPublicId())
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        existente.setNombre(p.getNombre());
        existente.setApellidos(p.getApellidos());
        return pacienteRepository.save(existente);
    }
}

