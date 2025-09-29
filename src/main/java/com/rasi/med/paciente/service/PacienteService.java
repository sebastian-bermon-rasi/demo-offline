package com.rasi.med.paciente.service;

import com.rasi.med.paciente.Paciente;
import org.springframework.context.annotation.Profile;

import java.util.UUID;

@Profile("sede")
public interface PacienteService {

    Paciente save(Paciente p);

    void softDelete(Paciente p);

    void softDeleteById(UUID id);

    Paciente update(Paciente p);
}
