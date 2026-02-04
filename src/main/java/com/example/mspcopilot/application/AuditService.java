// src/main/java/com/example/mspcopilot/application/AuditService.java
package com.example.mspcopilot.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.mspcopilot.domain.TicketAction;
import com.example.mspcopilot.domain.enums.ActionType;
import com.example.mspcopilot.infra.repo.TicketActionRepository;

@Service
public class AuditService {
  private final TicketActionRepository repo;

  public AuditService(TicketActionRepository repo) {
    this.repo = repo;
  }

  public void log(UUID ticketId, ActionType type, String message) {
    repo.save(TicketAction.of(ticketId, type, message));
  }
}
