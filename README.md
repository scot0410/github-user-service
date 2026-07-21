# GitHub User Service

## About
This Spring Boot application normalizes and transforms external API payloads into a single, unified domain object.

## Prerequisites
Ensure the following tools are installed on your local machine:
* **Java 21**
* **Docker & Docker Compose**

## API Endpoints

| Method | Endpoint              | Description                                                | Payload / Params  |
|:-------|:----------------------|:-----------------------------------------------------------|:------------------|
| `GET`  | `/actuator/health`    | Liveness and Readiness check                               | Raw API JSON body |
| `GET`  | `/v1/user/{username}` | Accepts a username and returns a normalized domain object. | Raw API JSON body |

## Quick Start

### 1. Start Required Services
Spin up the necessary infrastructure dependencies in the background:
```bash
docker-compose up -d
```

### 2. Build the Application
Compile the project and run the test suite to verify the setup:
```bash
./gradlew clean build
```

### 3. Run the Application
Start the Spring Boot application locally:
```bash
./gradlew bootRun
```

### 4. Stop Required Services
Spin up the necessary infrastructure dependencies in the background:
```bash
docker-compose down
```

## System Architecture & Request Flow

## Request Flow

```text
[ Client ]
    │
    ▼ (GET /api/v1/users/{username})
[ User Controller ]
    │
    ▼
[ User Service ] <───► [ Redis Cache ] (Check first)
    │
    ├───► [ Circuit Breaker ] ───► External API 1 (User Bio)  ──┐
    │                                                           ├──► [ Data Transformer ] ──► [ Domain Object ] ──► [ Client ]
    └───► [ Circuit Breaker ] ───► External API 2 (User Repos) ─┘
```


