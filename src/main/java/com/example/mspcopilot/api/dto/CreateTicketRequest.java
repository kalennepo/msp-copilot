package com.example.mspcopilot.api.dto;

import com.example.mspcopilot.domain.enums.Priority;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTicketRequest(
    @NotBlank String title,
    @NotBlank String description,
    @Email @NotBlank String requesterEmail,
    @NotNull Priority priority
) {}
