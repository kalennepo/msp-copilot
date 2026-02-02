# MSP Ops Copilot

MSP Ops Copilot is a Spring Boot backend that models how an IT or MSP tool could
ingest support tickets, triage them, and suggest resolution 
steps (“runbooks”), while keeping a full audit trail.

## Features
- REST API for ticket ingestion
- Asynchronous triage processing
- Rules-based ticket classification
- Runbook suggestions
- Audit log of ticket actions

## Tech Stack
- Java 21
- Spring Boot 3
- PostgreSQL
- Flyway
- Docker

## How to Run
docker compose up -d
mvn spring-boot:run
