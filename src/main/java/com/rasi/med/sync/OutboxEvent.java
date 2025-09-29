package com.rasi.med.sync;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class OutboxEvent {
    @Id
    private UUID id;                    // evento

    @Column(name="aggregate_type", nullable=false)
    private String aggregateType;       // PATIENT

    @Column(name="aggregate_id", nullable=false)
    private UUID aggregateId;           // idGlobal del paciente

    @Column(nullable=false)
    private String op;                  // UPSERT | DELETE

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private JsonNode payload;             // JSON del estado paciente

    @Column(name="branch_id", nullable=false)
    private String branchId;

    @Column(name="occurred_at", nullable=false)
    private Instant occurredAt;

    @Column(nullable=false)
    private String status;              // PENDING | ACK

    @Column(nullable=false)
    private Integer attempts;
}
