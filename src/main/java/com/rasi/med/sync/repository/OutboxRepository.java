package com.rasi.med.sync.repository;

import com.rasi.med.sync.OutboxEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {
    @Query("select e from OutboxEvent e where e.status='PENDING' order by e.occurredAt asc")
    List<OutboxEvent> findTop100Pending(Pageable pageable);
}

