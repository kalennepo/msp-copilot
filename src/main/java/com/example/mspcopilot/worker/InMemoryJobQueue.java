package com.example.mspcopilot.worker;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Component;

@Component
public class InMemoryJobQueue implements JobQueue {
  private final BlockingQueue<UUID> q = new LinkedBlockingQueue<>();

  @Override
  public void enqueue(UUID jobId) {
    q.offer(jobId);
  }

  @Override
  public UUID take() throws InterruptedException {
    return q.take();
  }
}
