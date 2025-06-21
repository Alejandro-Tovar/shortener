# URL Shortener

A URL shortening service built with Java, Spring Boot, Redis, and Docker. Designed to showcase a classic system design scenario, 
with caching strategies, and Dockerized deployment.

## Features

- Shorten long URLs via REST API
- Redirect to original URLs using short codes
- Caching layer with Redis for performance
- H2 in-memory database (for dev)
- Docker + Docker Compose setup

## Tech Stack

- Java 21
- Spring Boot
- Redis
- Docker / Docker Compose
- H2 Database
- JUnit 5

## How to Run

```bash
docker-compose up --build
