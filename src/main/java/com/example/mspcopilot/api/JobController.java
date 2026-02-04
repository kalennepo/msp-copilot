package com.example.mspcopilot.api;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mspcopilot.api.dto.JobResponse;
import com.example.mspcopilot.domain.Job;
import com.example.mspcopilot.infra.repo.JobRepository;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
  private final JobRepository jobRepo;

  public JobController(JobRepository jobRepo) {
    this.jobRepo = jobRepo;
  }

  @GetMapping("/{id}")
  public JobResponse get(@PathVariable UUID id) {
    Job j = jobRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Job not found: " + id));
    return new JobResponse(
        j.getId(), j.getTicketId(), j.getType(), j.getStatus(),
        j.getProgress(), j.getError(), j.getCreatedAt(), j.getUpdatedAt()
    );
  }
}
