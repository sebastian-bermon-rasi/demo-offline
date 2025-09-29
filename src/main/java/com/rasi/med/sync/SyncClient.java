package com.rasi.med.sync;

import com.rasi.med.config.SyncProperties;
import com.rasi.med.sync.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class SyncClient {
    private final SyncProperties props;
    private final RestTemplate restTemplate = new RestTemplate();

    public SyncResponse push(SyncRequest req) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (props.getAuthToken()!=null && !props.getAuthToken().isEmpty()) {
            headers.setBearerAuth(props.getAuthToken());
        }
        HttpEntity<SyncRequest> entity = new HttpEntity<>(req, headers);
        ResponseEntity<SyncResponse> res = restTemplate.exchange(
                props.getCentralBaseUrl() + "/sync/batch",
                HttpMethod.POST, entity, SyncResponse.class);
        return res.getBody();
    }
}
