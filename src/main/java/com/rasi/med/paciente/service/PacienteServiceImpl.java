package com.rasi.med.paciente.service;

import com.rasi.med.paciente.Paciente;
import com.rasi.med.paciente.PacienteDto;
import com.rasi.med.paciente.repository.PacienteRepository;
import com.rasi.med.sync.OutboxService;
import com.rasi.med.config.SyncProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor // <-- inyecta todos los final automáticamente
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final OutboxService outboxService;   // <-- ahora sí se inyecta
    private final SyncProperties props;          // para tomar branchId por defecto

    @Override
    @Transactional
    public Paciente save(Paciente p) {
        if (p.getPublicId() == null) p.setPublicId(java.util.UUID.randomUUID());

        if (p.getTipoDoc() == null || p.getTipoDoc().trim().isEmpty()) {
            p.setTipoDoc("CC"); // o el tipo por defecto que uses
        }

        p.setDeleted(false);
        if (p.getBranchId() == null) p.setBranchId(props.getBranchId());
        if (p.getUpdatedBy() == null) p.setUpdatedBy("system");
        p.setVersion(p.getVersion() == null ? 0 : p.getVersion());

        Paciente saved = pacienteRepository.save(p);
        outboxService.publishPatientUpsert(saved);
        return saved;
    }

    @Override
    @Transactional
    public void softDelete(Paciente p) {
        if (p.isDeleted()) return; // idempotencia
        p.setDeleted(true);
        pacienteRepository.save(p);
        outboxService.publishPatientDelete(p.getPublicId());
    }

    @Override
    @Transactional
    public void softDeleteById(UUID id) {
        Paciente p = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        softDelete(p); // reusa la lógica e imprime en outbox
    }

    @Transactional
    public Paciente update(Paciente p) {
        Paciente existente = pacienteRepository.findById(p.getPublicId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        existente.setNombre(p.getNombre());
        existente.setApellidos(p.getApellidos());
        existente.setUpdatedBy(p.getUpdatedBy() != null ? p.getUpdatedBy() : "system");
        Paciente saved = pacienteRepository.save(existente);
        outboxService.publishPatientUpsert(saved);          // publicar cambio
        return saved;
    }

    @Transactional
    public Paciente create(PacienteDto dto, String usuario, String branchId) {
        Paciente p = Paciente.builder()
                .publicId(UUID.randomUUID())
                .numDoc(dto.getNumDoc())
                .nombre(dto.getNombres())
                .apellidos(dto.getApellidos())
                .updatedBy(usuario != null ? usuario : "system")
                .branchId(branchId != null ? branchId : props.getBranchId())
                .deleted(false)
                .version(0)
                .build();
        Paciente saved = pacienteRepository.save(p);
        outboxService.publishPatientUpsert(saved);
        return saved;
    }

    @Transactional
    public void eliminar(UUID id) {
        Paciente p = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        p.setDeleted(true);
        pacienteRepository.save(p);
        outboxService.publishPatientDelete(id);
    }
}
