package com.example.mspcopilot.infra.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mspcopilot.domain.TicketAction;

public interface TicketActionRepository extends JpaRepository<TicketAction, UUID> {
  List<TicketAction> findByTicketIdOrderByCreatedAtAsc(UUID ticketId);
}
