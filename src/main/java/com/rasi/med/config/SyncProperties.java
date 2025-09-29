package com.rasi.med.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sync")
@Data
public class SyncProperties {
    private String role;
    private String branchId;
    private String centralBaseUrl;
    private String authToken;
    private String acceptAuthToken;
}
