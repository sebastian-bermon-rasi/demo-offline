package com.rasi.med.sync.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncRequest {
    private String branchId;
    private List<SyncEventDTO> events;
}
