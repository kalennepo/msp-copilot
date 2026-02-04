package com.example.mspcopilot.domain;

import java.time.Instant;
import java.util.UUID;

import com.example.mspcopilot.domain.enums.JobStatus;
import com.example.mspcopilot.domain.enums.JobType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "jobs")
public class Job {
  @Id
  private UUID id;

  @Column(name = "ticket_id", nullable = false)
  private UUID ticketId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private JobType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private JobStatus status;

  @Column(nullable = false)
  private int progress;

  @Column(columnDefinition = "text")
  private String error;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public static Job newJob(UUID ticketId, JobType type) {
    Job j = new Job();
    j.id = UUID.randomUUID();
    j.ticketId = ticketId;
    j.type = type;
    j.status = JobStatus.PENDING;
    j.progress = 0;
    j.createdAt = Instant.now();
    j.updatedAt = Instant.now();
    return j;
  }

  public UUID getId() { return id; }
  public UUID getTicketId() { return ticketId; }
  public JobType getType() { return type; }
  public JobStatus getStatus() { return status; }
  public int getProgress() { return progress; }
  public String getError() { return error; }
  public Instant getCreatedAt() { return createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }

  public void setStatus(JobStatus status) {
    this.status = status;
    this.updatedAt = Instant.now();
  }

  public void updateProgress(int progress) {
    this.progress = Math.max(0, Math.min(100, progress));
    this.updatedAt = Instant.now();
  }

  public void fail(String error) {
    this.status = JobStatus.FAILED;
    this.error = error;
    this.updatedAt = Instant.now();
  }
}
