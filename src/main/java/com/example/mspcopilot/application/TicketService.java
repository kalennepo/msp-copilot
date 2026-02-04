// src/main/java/com/example/mspcopilot/application/TicketService.java
package com.example.mspcopilot.application;

import com.example.mspcopilot.domain.Job;
import com.example.mspcopilot.domain.Ticket;
import com.example.mspcopilot.domain.enums.*;
import com.example.mspcopilot.infra.repo.JobRepository;
import com.example.mspcopilot.infra.repo.TicketRepository;
import com.example.mspcopilot.worker.JobQueue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TicketService {
  private final TicketRepository ticketRepo;
  private final JobRepository jobRepo;
  private final AuditService audit;
  private final JobQueue queue;

  public TicketService(TicketRepository ticketRepo, JobRepository jobRepo, AuditService audit, JobQueue queue) {
    this.ticketRepo = ticketRepo;
    this.jobRepo = jobRepo;
    this.audit = audit;
    this.queue = queue;
  }

  @Transactional
  public UUID createTicketAndEnqueueTriage(String title, String description, String requesterEmail, Priority priority) {
    Ticket ticket = Ticket.newTicket(title, description, requesterEmail, priority);
    ticketRepo.save(ticket);

    audit.log(ticket.getId(), ActionType.STATE_CHANGE, "Ticket created with status NEW.");

    Job job = Job.newJob(ticket.getId(), JobType.TRIAGE);
    jobRepo.save(job);

    ticket.setStatus(TicketStatus.QUEUED);
    ticketRepo.save(ticket);
    audit.log(ticket.getId(), ActionType.STATE_CHANGE, "Ticket moved to QUEUED. Triage job created: " + job.getId());

    queue.enqueue(job.getId());
    return job.getId();
  }
}
