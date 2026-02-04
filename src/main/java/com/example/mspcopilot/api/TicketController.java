package com.example.mspcopilot.api;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mspcopilot.api.dto.ActionResponse;
import com.example.mspcopilot.api.dto.CreateTicketRequest;
import com.example.mspcopilot.api.dto.CreateTicketResponse;
import com.example.mspcopilot.api.dto.TicketResponse;
import com.example.mspcopilot.application.TicketService;
import com.example.mspcopilot.domain.Ticket;
import com.example.mspcopilot.infra.repo.TicketActionRepository;
import com.example.mspcopilot.infra.repo.TicketRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
  private final TicketService ticketService;
  private final TicketRepository ticketRepo;
  private final TicketActionRepository actionRepo;

  public TicketController(TicketService ticketService, TicketRepository ticketRepo, TicketActionRepository actionRepo) {
    this.ticketService = ticketService;
    this.ticketRepo = ticketRepo;
    this.actionRepo = actionRepo;
  }

  @PostMapping
  public CreateTicketResponse create(@Valid @RequestBody CreateTicketRequest req) {
    var res = ticketService.createTicketAndEnqueueTriage(
      req.title(), req.description(), req.requesterEmail(), req.priority()
    );
    return new CreateTicketResponse(res.ticketId(), res.jobId());
  }

  @GetMapping("/{id}")
  public TicketResponse get(@PathVariable("id") UUID id) {
    Ticket t = ticketRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + id));
    return new TicketResponse(
        t.getId(), t.getTitle(), t.getDescription(), t.getRequesterEmail(),
        t.getPriority(), t.getStatus(), t.getCategory(), t.getCreatedAt(), t.getUpdatedAt()
    );
  }

  @GetMapping
  public List<TicketResponse> list() {
    return ticketRepo.findAll().stream().map(t -> new TicketResponse(
        t.getId(), t.getTitle(), t.getDescription(), t.getRequesterEmail(),
        t.getPriority(), t.getStatus(), t.getCategory(), t.getCreatedAt(), t.getUpdatedAt()
    )).toList();
  }

  @GetMapping("/{id}/actions")
  public List<ActionResponse> actions(@PathVariable UUID id) {
    return actionRepo.findByTicketIdOrderByCreatedAtAsc(id).stream()
        .map(a -> new ActionResponse(a.getId(), a.getTicketId(), a.getType(), a.getMessage(), a.getCreatedAt()))
        .toList();
  }
}
