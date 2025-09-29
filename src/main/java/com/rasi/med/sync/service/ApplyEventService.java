package com.rasi.med.sync.service;

import com.rasi.med.paciente.Paciente;
import com.rasi.med.paciente.repository.PacienteRepository;
import com.rasi.med.sync.dto.SyncEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplyEventService {
    private final PacienteRepository pacienteRepo;

    @Transactional
    public void apply(SyncEventDTO ev) {
        if ("PATIENT".equals(ev.getAggregateType())) {
            if ("UPSERT".equals(ev.getOp())) applyPatientUpsert(ev);
            else if ("DELETE".equals(ev.getOp())) applyPatientDelete(ev);
        }
    }

    private void applyPatientUpsert(SyncEventDTO ev) {
        Paciente incoming = readPatient(ev.getPayload());
        Paciente current = pacienteRepo.findById(incoming.getPublicId()).orElse(null);
        if (current == null) {
            pacienteRepo.save(incoming);
        } else {
            // LWW simple por updatedAt
            if (incoming.getUpdatedAt()!=null && (current.getUpdatedAt()==null
                    || incoming.getUpdatedAt().isAfter(current.getUpdatedAt()))) {
                current.setNombre(incoming.getNombre());
                current.setNumDoc(incoming.getNumDoc());
                current.setUpdatedAt(incoming.getUpdatedAt());
                current.setUpdatedBy(incoming.getUpdatedBy());
                current.setVersion(incoming.getVersion());
                current.setDeleted(incoming.isDeleted());
                current.setBranchId(incoming.getBranchId());
                pacienteRepo.save(current);
            }
        }
    }

    private void applyPatientDelete(SyncEventDTO ev) {
        UUID id = ev.getAggregateId();
        Paciente current = pacienteRepo.findById(id).orElse(null);
        if (current != null) {
//            current.setDeleted(true);
            pacienteRepo.save(current);
        }
    }

    private Paciente readPatient(String json) {
        try { return new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, Paciente.class); }
        catch(Exception e){ throw new RuntimeException(e); }
    }
}
