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

| Method | Endpoint | Description  | Request Params                | Payload                                             |
|:-------|:----------------------|:----------------------------------------------------------|:------------------------------|:----------------------------------------------------|
| `GET`  | `/actuator/health`    | Liveness and Readiness probe for container orchestration. |                               | [Health Response](#health-response-payload)         |
| `GET`  | `/v1/users`           | Returns a unified GitHub user profile.                    | `username` (String, Required) | [GitHubUser Response](#githubuser-response-payload) |

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
  "user_name": "octocat",
  "display_name": "The Octocat",
  "avatar": "https://avatars.githubusercontent.com/u/583231?v=4",
  "geo_location": "San Francisco",
  "email": null,
  "url": "https://api.github.com/users/octocat",
  "created_at": "Tue, 25 Jan 2011 18:44:36 GMT",
  "repos": [
    {
      "name": "boysenberry-repo-1",
      "url": "https://api.github.com/repos/octocat/boysenberry-repo-1"
    }
  ]
}
```

---

## System Architecture & Request Flow

> ⚠️ **Implementation Note:** The sequence diagram below represents the **Target Architecture**. While Redis is fully provisioned via Docker Compose, application-level caching and circuit breakers are slotted for future implementation phases (detailed below).

```text
[ Client ]
    │
    ▼ (GET /v1/users?username=...)
[ User Controller ]
    │
    ▼
[ User Service ] <───► [ Redis Cache ] (Provisioned / Future Integration)
    │
    ├───► [ Circuit Breaker (Future Integration) ] ───► External API 1 (User Bio)  ──┐
    │                                                                                ├──► [ Domain Object ] ──► [ Client ]
    └───► [ Circuit Breaker (Future Integration) ] ───► External API 2 (User Repos) ─┘
```

---

## Architectural Decisions & Trade-offs

### 1. Structural Pattern: MVC & Package-by-Feature
* **Decision:** Implemented a practical MVC layout with Package by Feature, instead of pure Hexagonal Architecture layers.
* **Reasoning:** Time constraints and a small initial domain.
* **Future Iteration:** If the domain expands or introduces heavy business logic, transition to pure Ports & Adapters.

### 2. Containerized Microservice Infrastructure
* **Decision:** Configured a `Dockerfile` and multi-container `docker-compose.yml` environment containing a Redis dependency.
* **Reasoning:** Establishes a production-ready setup, and ensures app deployment reliability.
* **Future Iteration:** Focus shift back to pure application logic. The container foundation enables quick Spring Cache Redis integration.

### 3. Exception Handling Strategy
* **Decision:** Relied on `@ResponseStatus` annotations directly on exception classes.
* **Reasoning:** Time constraints.
* **Future Iteration:** Replace this approach with `@RestControllerAdvice` (Global Exception Handler) to return clean, standardized responses with error details.

### 4. Handling Missing Data (Email Nullability)
* **Decision:** The application leaves the `email` field as `null` when it's returned from the GitHub User API.
* **Reasoning:** Matches GitHub API behavior, where public emails are optional on GitHub profiles.
* **Future Iteration:** To avoid potential risks (eg. `NullPointerException`), use `Optional<String>` getters or annotations rather than using raw nullable object fields.

---

## Production Considerations

### Current State
* **Validation:** Performs manual validation on supplied `@RequestParam`. Explicit null-checking for the string literal `"null"` is intentionally omitted, as it represents a valid, registered upstream username on GitHub.
* **Testing Coverage:** The current suite achieves **87% Line Coverage** and **25% Branch Coverage**.
* **DevOps Readiness:** Includes out-of-the-box support for Kubernetes liveness and readiness probes via Spring Boot Actuator endpoints.

### Future Roadmap
* **Testing Expansion:** Implement parameterized testing for edge cases and branch coverage.
* **Caching:** Implement service-layer caching to prevent upstream rate-limiting errors.
* **Resilience:** Integrate a Circuit Breaker pattern at external client call for fault tolerance.
* **Infrastructure as Code:** Configure K8s manifests with Helm charts for continuous deployment.
