package com.rasi.med.paciente.controller;

import com.rasi.med.paciente.Paciente;
import com.rasi.med.paciente.repository.PacienteRepository;
import com.rasi.med.paciente.service.PacienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;
    private final PacienteRepository repo;

    @PostMapping
    public ResponseEntity<Paciente> create(@RequestBody Paciente p) {
        // Para el MVP, pasa por save() que publica evento
        Paciente saved = pacienteService.save(p);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID publicId) {
        // Usa el flujo del service (soft delete + outbox)
        pacienteService.softDeleteById(publicId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Paciente>> list() {
        return ResponseEntity.ok(repo.findAll());
    }
}
