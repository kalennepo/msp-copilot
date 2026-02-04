package com.example.mspcopilot.domain;

import java.time.Instant;
import java.util.UUID;

import com.example.mspcopilot.domain.enums.ActionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ticket_actions")
public class TicketAction {
  @Id
  private UUID id;

  @Column(name = "ticket_id", nullable = false)
  private UUID ticketId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ActionType type;

  @Column(nullable = false, columnDefinition = "text")
  private String message;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  public static TicketAction of(UUID ticketId, ActionType type, String message) {
    TicketAction a = new TicketAction();
    a.id = UUID.randomUUID();
    a.ticketId = ticketId;
    a.type = type;
    a.message = message;
    a.createdAt = Instant.now();
    return a;
  }

  public UUID getId() { return id; }
  public UUID getTicketId() { return ticketId; }
  public ActionType getType() { return type; }
  public String getMessage() { return message; }
  public Instant getCreatedAt() { return createdAt; }
}
