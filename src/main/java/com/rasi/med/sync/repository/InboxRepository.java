package com.rasi.med.sync.repository;

import com.rasi.med.sync.Inbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InboxRepository extends JpaRepository<Inbox, UUID> {}
