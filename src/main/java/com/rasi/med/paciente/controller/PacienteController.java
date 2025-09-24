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
    @ResponseBody
    public ResponseEntity<Paciente> create(@RequestBody Paciente p) {
        Paciente saved = pacienteService.save(p);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable("id") UUID publicId) {
        Paciente p = repo.findById(publicId).orElse(null);
        if (p == null) return ResponseEntity.notFound().build();
        pacienteService.softDelete(p);                 // <<-- pasa por el SERVICE
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<Paciente>> list() {
        return ResponseEntity.ok(repo.findAll());
    }

//    @PutMapping
//    public  ResponseEntity<Paciente> update(@RequestBody Paciente p) {
//        Paciente existente = repo.findById(p.getPublicId())
//                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
//        existente.setNombre(p.getNombre());
//        existente.setApellidos(p.getApellidos());
//        return pacienteService.save(p);
//    }
}
