package com.rasi.med.cita.controller;

import com.rasi.med.cita.Cita;
import com.rasi.med.cita.CitaDto;
import com.rasi.med.cita.repo.CitaRepository;
import com.rasi.med.paciente.Paciente;
import com.rasi.med.paciente.repository.PacienteRepository;
import lombok.var;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Profile("sede")
@RestController
@RequestMapping("/api/citas")
public class CitaController {

}
