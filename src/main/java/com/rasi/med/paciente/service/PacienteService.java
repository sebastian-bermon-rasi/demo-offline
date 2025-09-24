package com.rasi.med.paciente.service;

import com.rasi.med.paciente.Paciente;
import org.springframework.context.annotation.Profile;

@Profile("sede")
public interface PacienteService {

    Paciente save(Paciente p);

    void softDelete(Paciente p);

    Paciente update(Paciente p);
}
