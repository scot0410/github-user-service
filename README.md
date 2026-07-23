# GitHub User Service

## About
This Spring Boot application acts as an integration and transformation layer. It normalizes external API payloads from GitHub into a single, unified domain object.

## Prerequisites
Ensure the following tools are installed on your local machine before starting:
* **Java 21**
* **Docker & Docker Compose**

---

## Quick Start

### 1. Start Required Services
Spin up the necessary infrastructure dependencies (such as Redis) in the background:
```bash
docker-compose up -d
```

### 2. Build the Application
Compile the project and run the automated test suite to verify the setup:
```bash
./gradlew clean build
```

### 3. Run the Application
Start the Spring Boot application locally:
```bash
./gradlew bootRun
```

### 4. Stop Services
Tear down the background infrastructure dependencies when finished:
```bash
docker-compose down
```

---

## API Endpoints

| Method | Endpoint | Description  | Request Params | Payload                                             |
|:-------|:----------------------|:----------------------------------------------------------|:-------------------------------------------------|:----------------------------------------------------|
| `GET`  | `/actuator/health`    | Liveness and Readiness probe for container orchestration. |                                                  | [Health Response](#health-response-payload)         |
| `GET`  | `/v1/users`           | Returns a unified GitHub user profile.                    | **Request Param:** `username` (String, Required) | [GitHubUser Response](#githubuser-response-payload) |

### Health Response Payload
```json
{
  "groups": [
    "liveness",
    "readiness"
  ],
  "status": "UP"
}
```

### GitHubUser Response Payload
```json
{
  "userName": "octocat",
  "displayName": "The Octocat",
  "avatar": "https://githubusercontent.com",
  "geoLocation": "San Francisco",
  "email": null,
  "url": "https://github.com",
  "createdAt": "Tue, 25 Jan 2011 18:44:36 GMT",
  "repos": [
    {
      "name": "boysenberry-repo-1",
      "url": "https://github.com"
    }
  ]
}
```

---

## System Architecture & Request Flow

> ⚠️ **Implementation Note:** The sequence diagram below represents the **Target Architecture**. While Redis and infrastructure containers are fully provisioned via Docker Compose, application-level caching and circuit breakers are slotted for future implementation phases (detailed below).

```text
[ Client ]
    │
    ▼ (GET /v1/users?username=...)
[ User Controller ]
    │
    ▼
[ User Service ] <───► [ Redis Cache ] (Provisioned / Future Integration)
    │
    ├───► [ Circuit Breaker (Future Integration) ] ───► External API 1 (User Bio) ──┐
    │                                                           ├──► [ Domain Object ] ──► [ Client ]
    └───► [ Circuit Breaker (Future Integration) ] ───► External API 2 (User Repos) ─┘
```

---

## Architectural Decisions & Trade-offs

### 1. Structural Pattern: MVC & Package-by-Feature
* **Decision:** Implemented a practical MVC layout organized tightly by feature boundaries instead of pure Hexagonal Architecture layers.
* **Reasoning:** Driven by strict project time constraints and a small initial domain. Hexagonal boundaries would introduce unnecessary interface overhead for a straightforward API proxy.
* **Future Iteration:** If the domain expands or introduces heavy multi-database business logic, transition to pure Ports & Adapters to strictly isolate the business domain.

### 2. Containerized Microservice Infrastructure
* **Decision:** Configured a `Dockerfile` and multi-container `docker-compose.yml` environment containing a Redis dependency.
* **Reasoning:** Establishes a production-ready baseline on Day 1, ensuring the local setup perfectly mirrors real-world deployments.
* **Future Iteration:** Focus shift back to pure application logic. The container foundation ensures the next developer can easily plug in the Spring Cache Redis adapter.

### 3. Exception Handling Strategy
* **Decision:** Relied on localized `@ResponseStatus` markers directly on explicit custom exception classes.
* **Reasoning:** Allowed for fast delivery of distinct HTTP status codes without complex wiring.
* **Future Iteration:** Replace this approach with a centralized `@RestControllerAdvice` (Global Exception Handler) to return clean, standardized problem details to downstream clients.

### 4. Handling Missing Data (Email Nullability)
* **Decision:** The application leaves empty/hidden downstream fields as `null` in the payload instead of enforcing fallback strings.
* **Reasoning:** Matches upstream behavior where public emails are entirely optional on GitHub profiles.
* **Future Iteration:** To avoid exposing risks to downstream developers, expose fields safely through an un-serializable `Optional<String>` getter interface or clear mapping annotations rather than using raw nullable object fields.

---

## Production Considerations

* **Validation:** Leverages Spring Validation via `@RequestParam` constraints. Note that explicit null-checking for the literal value `"null"` was omitted, as it represents a valid, registered upstream username on the GitHub platform.
* **Testing Coverage:** Current suite achieves **87% Line Coverage** and **25% Branch Coverage**. Future focus will emphasize parameterized testing to capture deep edge cases and missing response paths.
* **DevOps Readiness:** Out-of-the-box support for Kubernetes liveness/readiness probes via Spring Boot Actuator endpoints. Next step is to export these configurations into localized Helm charts.
