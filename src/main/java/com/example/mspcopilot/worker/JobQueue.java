package com.example.mspcopilot.worker;

import java.util.UUID;

public interface JobQueue {
  void enqueue(UUID jobId);
  UUID take() throws InterruptedException;
}
