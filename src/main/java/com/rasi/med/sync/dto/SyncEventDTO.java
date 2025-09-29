package com.rasi.med.sync.dto;

import lombok.*;
import java.time.Instant;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncEventDTO {
    private UUID id;
    private String aggregateType;
    private UUID aggregateId;
    private String op;
    private String payload;
    private String branchId;
    private Instant occurredAt;
}

