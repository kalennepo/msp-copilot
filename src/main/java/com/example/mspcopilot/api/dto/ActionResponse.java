package com.example.mspcopilot.api.dto;

import java.time.Instant;
import java.util.UUID;

import com.example.mspcopilot.domain.enums.ActionType;

public record ActionResponse(
    UUID id,
    UUID ticketId,
    ActionType type,
    String message,
    Instant createdAt
) {}
