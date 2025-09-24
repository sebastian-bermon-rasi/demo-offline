package com.rasi.med.paciente.repository;

import com.rasi.med.paciente.Paciente;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface PacienteRepository extends JpaRepository<Paciente, UUID> {

    // Por documento (para conflictos)
    Optional<Paciente> findByTipoDocAndNumDoc(String tipoDoc, String numDoc);

}
