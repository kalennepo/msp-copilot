package com.example.mspcopilot.domain;

import java.time.Instant;
import java.util.UUID;

import com.example.mspcopilot.domain.enums.Category;
import com.example.mspcopilot.domain.enums.Priority;
import com.example.mspcopilot.domain.enums.TicketStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tickets")
public class Ticket {
  @Id
  private UUID id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "text")
  private String description;

  @Column(name = "requester_email", nullable = false)
  private String requesterEmail;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Priority priority;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TicketStatus status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Category category;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public static Ticket newTicket(String title, String description, String requesterEmail, Priority priority) {
    Ticket t = new Ticket();
    t.id = UUID.randomUUID();
    t.title = title;
    t.description = description;
    t.requesterEmail = requesterEmail;
    t.priority = priority;
    t.status = TicketStatus.NEW;
    t.category = Category.OTHER;
    t.createdAt = Instant.now();
    t.updatedAt = Instant.now();
    return t;
  }

  public void setStatus(TicketStatus status) {
    this.status = status;
    this.updatedAt = Instant.now();
  }

  public void setCategory(Category category) {
    this.category = category;
    this.updatedAt = Instant.now();
  }

  public UUID getId() { return id; }
  public String getTitle() { return title; }
  public String getDescription() { return description; }
  public String getRequesterEmail() { return requesterEmail; }
  public Priority getPriority() { return priority; }
  public TicketStatus getStatus() { return status; }
  public Category getCategory() { return category; }
  public Instant getCreatedAt() { return createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }
}
