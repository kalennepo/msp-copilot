package com.example.mspcopilot.infra.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mspcopilot.domain.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {}
