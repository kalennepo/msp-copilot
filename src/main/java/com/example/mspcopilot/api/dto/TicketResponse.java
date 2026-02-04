package com.example.mspcopilot.api.dto;

import java.time.Instant;
import java.util.UUID;

import com.example.mspcopilot.domain.enums.Category;
import com.example.mspcopilot.domain.enums.Priority;
import com.example.mspcopilot.domain.enums.TicketStatus;

public record TicketResponse(
    UUID id,
    String title,
    String description,
    String requesterEmail,
    Priority priority,
    TicketStatus status,
    Category category,
    Instant createdAt,
    Instant updatedAt
) {}
