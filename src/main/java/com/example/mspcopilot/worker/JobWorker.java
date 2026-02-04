package com.example.mspcopilot.worker;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.mspcopilot.application.AuditService;
import com.example.mspcopilot.application.TriageService;
import com.example.mspcopilot.domain.Job;
import com.example.mspcopilot.domain.Ticket;
import com.example.mspcopilot.domain.enums.ActionType;
import com.example.mspcopilot.domain.enums.JobStatus;
import com.example.mspcopilot.domain.enums.TicketStatus;
import com.example.mspcopilot.infra.repo.JobRepository;
import com.example.mspcopilot.infra.repo.TicketRepository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class JobWorker {
  private static final Logger log = LoggerFactory.getLogger(JobWorker.class);

  private final JobQueue queue;
  private final JobRepository jobRepo;
  private final TicketRepository ticketRepo;
  private final TriageService triageService;
  private final AuditService audit;

  private final ExecutorService executor = Executors.newSingleThreadExecutor();

  public JobWorker(JobQueue queue, JobRepository jobRepo, TicketRepository ticketRepo,
                   TriageService triageService, AuditService audit) {
    this.queue = queue;
    this.jobRepo = jobRepo;
    this.ticketRepo = ticketRepo;
    this.triageService = triageService;
    this.audit = audit;
  }

  @PostConstruct
  public void start() {
    executor.submit(() -> {
      log.info("Job worker started.");
      while (!Thread.currentThread().isInterrupted()) {
        try {
          UUID jobId = queue.take();
          processJob(jobId);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
        } catch (Exception e) {
          log.error("Worker loop error", e);
        }
      }
      log.info("Job worker stopped.");
    });
  }

  @PreDestroy
  public void stop() {
    executor.shutdownNow();
  }

  @Transactional
  public void processJob(UUID jobId) {
    Job job = jobRepo.findById(jobId).orElseThrow();
    if (job.getStatus() != JobStatus.PENDING) return;

    job.setStatus(JobStatus.RUNNING);
    job.updateProgress(10);
    jobRepo.save(job);

    Ticket ticket = ticketRepo.findById(job.getTicketId()).orElseThrow();
    ticket.setStatus(TicketStatus.IN_PROGRESS);
    ticketRepo.save(ticket);
    audit.log(ticket.getId(), ActionType.STATE_CHANGE, "Triage started.");

    // “Work”
    job.updateProgress(40);
    jobRepo.save(job);

    TriageService.TriageResult result = triageService.triage(ticket.getTitle(), ticket.getDescription());
    ticket.setCategory(result.category());
    ticket.setStatus(TicketStatus.TRIAGED);
    ticketRepo.save(ticket);

    audit.log(ticket.getId(), ActionType.TRIAGE_RESULT, "Category=" + result.category());
    audit.log(ticket.getId(), ActionType.RUNBOOK_SUGGESTED, result.runbookSuggestion());

    job.updateProgress(100);
    job.setStatus(JobStatus.SUCCEEDED);
    jobRepo.save(job);

    audit.log(ticket.getId(), ActionType.STATE_CHANGE, "Triage completed. Ticket moved to TRIAGED.");
  }
}
