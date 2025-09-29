package com.rasi.med.sync;

import com.rasi.med.config.SyncProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile("branch")
@Component
@RequiredArgsConstructor
public class BranchSyncScheduler {
    private final OutboxService outboxService;
    private final SyncProperties props;
    private final SyncClient syncClient;

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void push() {
        outboxService.pushPendingBatch(syncClient, props.getBranchId());
    }
}
