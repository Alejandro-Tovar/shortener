# URL Shortener

A Spring Boot application that shortens URLs, supports redirection, analytics, Redis caching, and rate limiting.

---

## Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Endpoints](#endpoints)
- [Setup & Run Instructions](#setup--run-instructions)
- [Usage](#usage)
- [Testing](#testing)
- [Author](#author)

---

## Features

- URL shortening with deterministic hashes
- Redirection with total + unique click tracking
- Redis cache for performance + deduplication
- Rate limiting per shortened URL and client IP
- Analytics exposed via public endpoints
- Swagger documentation for all endpoints

---

## Requirements

- Java 17
- Maven
- Redis (running on `localhost:6379` or linked in Docker)
- Docker (optional, for container deployment)

---

## Endpoints

| Method | Path                                    | Description                                       |
|--------|-----------------------------------------|---------------------------------------------------|
| POST   | `/shorten`                              | Shortens a valid URL (rate-limited per IP)        |
| GET    | `/s/{code}`                             | Redirects to original URL (rate-limited per code) |
| GET    | `/analytics/{code}/clicks`              | Returns total clicks for the code                 |
| GET    | `/analytics/{code}/clicks/unique`       | Returns unique IP click count                     |
| GET    | `/analytics/{code}`                     | Returns full analytics for the shortened URL      |

---

## Setup & Run Instructions

### Local Setup
```
git clone https://github.com/Alejandro-Tovar/shortener.git
cd shortener
mvn clean install
mvn spring-boot:run
```
### Docker Setup
```
docker build -t url-shortener .
docker run -d --name redis redis:7
docker run -d -p 8080:8080 --link redis url-shortener
```

Access API via http://localhost:8080

## Usage
There are several ways to start using the URL Shortener API. One is by using the provided Swagger UI, or by making requests with tools like **Postman**, **curl**, etc.

The Swagger UI containing all available endpoints will be available as soon as the Spring Boot application starts, here:  
[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---
### Example: Create a Shortened URL

A valid POST /shorten curl request would look like this:
```
curl -X 'POST' \
'http://localhost:8080/shorten' \
-H 'Content-Type: application/json' \
-d '{
"url": "https://www.amazon.com/dp/B0CTSC3VS4"
}'
```
A successful request returns:
```
{
"shortenedUrl": "pf8GCm5A"
}
```
If the IP has made too many requests, you’ll receive a **429 Rate Limit error**.
```
{
  "error": "Too many requests from this IP"
}
```
---
### Example: Redirect to Original URL

This is an example of what a valid curl request looks like for the `GET /s/{code}` endpoint:
```
curl -X 'GET' \
  'http://localhost:8080/s/pf8GCm5A' \
  -H 'accept: */*'
```
If the shortened code exists, this request redirects (302) to the original URL, such as:
```
https://www.amazon.com/dp/B0CTSC3VS4
```
If too many requests are made for this code, you’ll receive a **429 Rate Limit error**.

---
### Example: Analytics URL
This is an example of what a valid curl request looks like for the `GET /analytics/{code}` endpoint:
```
curl -X 'GET' \
'http://localhost:8080/analytics/pf8GCm5A' \
-H 'accept: */*'
```
A successful request returns:
```
{
  "shortenedUrl": "pf8GCm5A",
  "originalUrl": "https://www.amazon.com/dp/B0CTSC3VS4",
  "createdAt": "2025-06-22T20:16:46.535015",
  "totalClicks": 3,
  "uniqueClicks": 1
}
```


---

## Testing
* Unit tests: mocked services for Redis and analytics
* Integration/E2E tests: REST endpoints
* Manual testing: curl or Postman; observe rate limit behavior

## Author
* Diego Alejandro Tovar Castañeda 
* github: Alejandro-Tovar
