package com.rasi.med.sync;

import com.rasi.med.config.SyncProperties;
import com.rasi.med.sync.dto.SyncEventDTO;
import com.rasi.med.sync.dto.SyncRequest;
import com.rasi.med.sync.dto.SyncResponse;
import com.rasi.med.sync.repository.InboxRepository;
import com.rasi.med.sync.service.ApplyEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@Profile("central")
@RestController
@RequestMapping("/sync")
@RequiredArgsConstructor
public class CentralSyncController {
    private final InboxRepository inboxRepo;
    private final ApplyEventService applyEventService;
    private final SyncProperties props;

    @PostMapping("/batch")
    @Transactional
    public SyncResponse batch(@RequestBody SyncRequest req,
                              @RequestHeader(value="Authorization", required=false) String auth) {

        // (Opcional) validar token simple:
        if (props.getAcceptAuthToken()!=null && !props.getAcceptAuthToken().isEmpty()) {
            if (auth==null || !auth.equals("Bearer " + props.getAcceptAuthToken()))
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Map<String, Boolean> ack = new HashMap<>();
        for (SyncEventDTO ev : req.getEvents()) {
            if (inboxRepo.existsById(ev.getId())) { ack.put(ev.getId().toString(), true); continue; }
            Inbox in = Inbox.builder().id(ev.getId()).branchId(req.getBranchId()).processed(false).build();
            inboxRepo.save(in);
            try {
                applyEventService.apply(ev);
                in.setProcessed(true);
                inboxRepo.save(in);
                ack.put(ev.getId().toString(), true);
            } catch (Exception ex) {
                in.setError(ex.getMessage());
                inboxRepo.save(in);
                ack.put(ev.getId().toString(), false); // reintento en branch
            }
        }
        return SyncResponse.builder().ack(ack).build();
    }
}

