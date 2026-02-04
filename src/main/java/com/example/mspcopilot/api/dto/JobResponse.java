package com.example.mspcopilot.api.dto;

import java.time.Instant;
import java.util.UUID;

import com.example.mspcopilot.domain.enums.JobStatus;
import com.example.mspcopilot.domain.enums.JobType;

public record JobResponse(
    UUID id,
    UUID ticketId,
    JobType type,
    JobStatus status,
    int progress,
    String error,
    Instant createdAt,
    Instant updatedAt
) {}
