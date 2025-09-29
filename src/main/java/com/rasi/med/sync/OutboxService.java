package com.rasi.med.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rasi.med.config.SyncProperties;
import com.rasi.med.paciente.Paciente;
import com.rasi.med.sync.dto.*;
import com.rasi.med.sync.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OutboxService {
    private final OutboxRepository outboxRepo;
    private final SyncProperties props;
    private final ObjectMapper om;

    @Transactional
    public void publishPatientUpsert(Paciente p) {
        OutboxEvent ev = OutboxEvent.builder()
                .id(UUID.randomUUID())
                .aggregateType("PATIENT")
                .aggregateId(p.getPublicId())
                .op("UPSERT")
                .payload(om.valueToTree(p))
                .branchId(props.getBranchId())
                .occurredAt(Instant.now())
                .status("PENDING")
                .attempts(0)
                .build();
        outboxRepo.save(ev);
    }

    @Transactional
    public void publishPatientDelete(UUID patientId) {
        Map<String,Object> body = new HashMap<>();
        body.put("idGlobal", patientId.toString());
        OutboxEvent ev = OutboxEvent.builder()
                .id(UUID.randomUUID())
                .aggregateType("PATIENT")
                .aggregateId(patientId)
                .op("DELETE")
                .payload(om.valueToTree(body))
                .branchId(props.getBranchId())
                .occurredAt(Instant.now())
                .status("PENDING")
                .attempts(0)
                .build();
        outboxRepo.save(ev);
    }

    @Transactional
    public void pushPendingBatch(SyncClient client, String branchId) {
        List<OutboxEvent> batch = outboxRepo.findTop100Pending((Pageable) PageRequest.of(0,100));
        if (batch.isEmpty()) return;
        SyncRequest req = SyncRequest.builder()
                .branchId(branchId)
                .events(batch.stream().map(this::toDto).collect(Collectors.toList()))
                .build();
        SyncResponse res = client.push(req);
        res.getAck().forEach((id, ok) -> {
            UUID uid = UUID.fromString(id);
            batch.stream().filter(e -> e.getId().equals(uid)).findFirst().ifPresent(e -> {
                if (Boolean.TRUE.equals(ok)) e.setStatus("ACK");
                else e.setAttempts(e.getAttempts()+1);
            });
        });
        outboxRepo.saveAll(batch);
    }

    private SyncEventDTO toDto(OutboxEvent e) {
        return SyncEventDTO.builder()
                .id(e.getId())
                .aggregateType(e.getAggregateType())
                .aggregateId(e.getAggregateId())
                .op(e.getOp())
                .payload(String.valueOf(e.getPayload()))
                .branchId(e.getBranchId())
                .occurredAt(e.getOccurredAt())
                .build();
    }

    private String writeJson(Object o) {
        try { return om.writeValueAsString(o); }
        catch (Exception ex) { throw new RuntimeException(ex); }
    }
}
