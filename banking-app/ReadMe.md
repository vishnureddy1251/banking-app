# Banking Application - REST API & GraphQL

An enterprise-style banking backend built with Spring Boot. Includes REST (v1/v2), GraphQL, JWT authentication, rate limiting, audit logging, and resilience patterns (Resilience4j).

## Highlights

* REST API for accounts, customers, transactions, loans, bills, notifications
* GraphQL endpoint with GraphiQL UI
* JWT authentication and role-based access (`ROLE_USER`, `ROLE_ADMIN`)
* Rate limiting for all `/api/**` endpoints (Bucket4j token bucket)
* Audit logging via an interceptor (`AuditInterceptor`)
* Circuit breaker / retry / bulkhead / time limiter demos (Resilience4j)
* Caching with Caffeine
* SQLite persistence (configured as `bankingdb.db`)
* Swagger/OpenAPI documentation
* WebSocket notifications via SockJS + STOMP (`/ws`)

## Quick Start

### Prerequisites

* Java 17+
* Maven (use the Maven wrapper)
* Optional: Redis running at `localhost:6379` (see `application.properties`)

### Run the application

```bash
# Windows
.\mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

### Open these URLs

| Area | URL |
|---|---|
| Application | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/api-docs |
| GraphiQL | http://localhost:8080/graphiql |
| Actuator | http://localhost:8080/actuator |
| Rate limit policy | http://localhost:8080/api/v1/rate-limit/info |
| WebSocket endpoint | http://localhost:8080/ws |

## Configuration

Key settings live in `src/main/resources/application.properties`:

* SQLite: `spring.datasource.url=jdbc:sqlite:bankingdb.db`
* Rate limiting: 20 requests/min per IP
* Redis cache: `spring.data.redis.host=localhost`, `spring.data.redis.port=6379`
* Swagger paths: `/swagger-ui.html` and `/api-docs`
* GraphQL paths: `/graphql` and `/graphiql`

## Authentication (JWT)

Public endpoints:

* `POST /api/auth/register`
* `POST /api/auth/login`

Example:

### Register

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123","role":"ROLE_ADMIN"}'
```

### Login (get token)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Use token

```bash
curl -X GET http://localhost:8080/api/v1/accounts \
  -H "Authorization: Bearer <your-token>"
```

## REST API (Quick Map)

Swagger is the best place to explore the full API surface:

* http://localhost:8080/swagger-ui.html

Main entry points:

* Accounts: `/api/v1/accounts`, `/api/v2/accounts`
* Customers: `/api/v1/customers`
* Transactions: `/api/v1/transactions/{accountId}`
* Bills: `/api/v1/bills/pay`, `/api/v1/bills/account/{accountId}`, `/api/v1/bills/track/{referenceNumber}`
* Notifications: `/api/v1/notifications/**`
* Rate limit info: `/api/v1/rate-limit/info`

## Rate Limiting

Implemented in `RateLimitFilter`:

* Scope: any request where the URI starts with `/api/`
* Limit: 20 requests per minute per IP
* Response headers:
  * `X-Rate-Limit-Limit`
  * `X-Rate-Limit-Remaining`
* When exceeded:
  * HTTP 429
  * JSON body with `error`, `message`, `retryAfterSeconds`

## GraphQL

Open:

* http://localhost:8080/graphiql

GraphQL endpoint:

* POST http://localhost:8080/graphql

## WebSocket Notifications

WebSocket is configured at `/ws` (SockJS).

The application publishes messages to the simple broker destination `/topic`.

## Testing & Coverage

### Run tests

```bash
./mvnw test
```

### Coverage report (JaCoCo)

After tests complete, open:

* `target/site/jacoco/index.html`

## Development Notes

* `src/test/java` contains unit tests (Mockito) and MockMvc contract tests.
* The project uses a standard Spring Boot layered structure: controller -> service -> repository.

