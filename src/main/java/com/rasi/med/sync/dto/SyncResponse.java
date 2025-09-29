package com.rasi.med.sync.dto;

import lombok.*;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncResponse {
    private Map<String, Boolean> ack; // eventId -> ok
}
