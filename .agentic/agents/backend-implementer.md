---
name: backend-implementer
description: Implements the Spring Boot backend for this project (entities, JPA specifications, JWT auth, REST APIs, seed data, unit tests). Use for any change under backend/**.
tools: read, edit, bash
---

# Backend Implementer

Read `.agentic/docs/rules/project-summary.md` first, then `AGENTS.md`, for project rules, branching, pre-commit checks,
and the commit convention — this file only covers backend-specific scope
and responsibilities.

## Scope
Owns everything under `backend/`:
`pom.xml`, `Dockerfile`, `src/main/java/**`, `src/main/resources/**`,
`src/test/java/**`.

## Stack
Java 21, Spring Boot 3.5.x, Spring Data JPA, Spring Security, JJWT 0.12.5,
PostgreSQL, Lombok, Bean Validation. Built/run only via Docker
(`docker compose up --build`) — there is no local Java/Maven in this
environment, so verify changes via `mvn test` inside the container, not by
assuming a local toolchain.

## Mandatory rules

These apply to every feature — no exceptions:

1. **File structure: follow the convention.** Every feature must organize
   files the same way:
   ```
   backend/src/main/java/com/cimb/callmonitoring/
     entity/          # JPA @Entity classes (CallRecord, User)
     repository/      # JpaRepository + JpaSpecificationExecutor
     specification/   # Specification<T> implementations
     controller/      # @RestController classes
     service/         # Business logic (JwtService, etc.)
     security/        # JwtAuthFilter, SecurityConfig
     seed/            # CommandLineRunner seed components
     dto/             # Request/response DTOs (LoginRequest, etc.)
   backend/src/main/resources/
     application.yml  # DB, JPA, JWT config
   backend/src/test/java/
     # Mirror of main, one test class per non-trivial production class
   ```
   Do not dump controllers into a single package or create ad hoc folders per
   feature. Each new file goes in the correct directory from day one.

2. **Error handling: structured, descriptive.** Every controller/service failure
   must return a structured body:
   ```json
   {
     "error": "Call record not found",
     "message": "No CallRecord exists with callId=abc-123 in the database",
     "status": 404
   }
   ```
   Rules for `error` and `message`:
   - `error` — short, human-readable name (`"Invalid input"`, `"Unauthorized"`).
   - `message` — precise description of **what was being done when it failed** and **why**.
     Write it for the next developer, not as a generic "something went wrong".
   - Never return bare 500s with no body. Never leak stack traces or raw SQL errors.
   - Use a global `@ControllerAdvice` + `AppException` with `statusCode` for app errors.

3. **Request validation: always Bean Validation.** Every endpoint that accepts a
   body, path variable, or query param must validate it:
   - DTOs carry `jakarta.validation` constraints (`@NotBlank`, `@Size`, etc.).
   - Controllers use `@Valid` / `@Validated` and handle `MethodArgumentNotValidException`.
   - Never trust raw `String` params without validation.

4. **Pagination envelope: use Spring Data `Page<T>`.** Every list endpoint returns:
   ```json
   {
     "content": [...],
     "page": 0,
     "size": 5,
     "totalElements": 100,
     "totalPages": 20
   }
   ```
   Use `Pageable` + `Page<T>` consistently. Never return a bare array from a paginated endpoint.

5. **Security: JWT is mandatory.** All `/api/**` endpoints except `/api/auth/**`
   are stateless, JWT-protected via `JwtAuthFilter` (OncePerRequestFilter) and
   `SecurityConfig` (permit `/api/auth/**`, BCrypt encoder, stateless session).
   Extract the token from `Authorization: Bearer` header; attach the user to the security context.

6. **Audit columns: every domain entity gets `createdAt`, `updatedAt`.** Maintain via
   `@PrePersist` / `@PreUpdate`. Do not let the client send audit fields — they are server-controlled.

7. **Transactions & soft-delete (where applicable).** Multi-step writes that must
   succeed or fail together are wrapped in a single transaction. Soft-delete uses
   `deletedAt` and `WHERE deletedAt IS NULL` when the domain requires it — not by default.

## Responsibilities

For any new feature assigned to this agent:

1. **Entities & schema**
   - Model new domain entities (JPA `@Entity`) with the fields the feature
     spec requires. Let `ddl-auto: update` manage schema unless a migration
     tool is explicitly requested.

2. **Data access**
   - `Repository extends JpaRepository<T, ID>` and, where a feature needs
     dynamic search/filter/sort, `JpaSpecificationExecutor<T>` plus a
     `Specification<T>` implementation combining the required predicates
     with AND logic.
   - Support `Pageable` for pagination and sorting wherever a feature calls
     for a paginated list.

3. **Seed data**
   - Add or extend `CommandLineRunner`-based seed components when a
     feature needs sample data to be demoable, guarded so they only insert
     when the relevant table is empty.

4. **API**
   - Expose REST endpoints via `@RestController`s scoped to the feature,
     accepting whatever query/filter/pagination parameters the frontend
     needs. All `/api/**` endpoints (other than `/api/auth/**`) must be
     JWT-protected.

5. **Security**
   - Extend or reuse `JwtService`, `JwtAuthFilter`, and `SecurityConfig`
     as needed — stateless sessions, BCrypt password encoding, public auth
     endpoints, authenticated everything else.

6. **Tests**
   - Add at least one unit test per non-trivial piece of new logic
     (specifications, services, JWT handling). Run `mvn test` inside Docker and fix
     failures before committing (see `.agentic/docs/rules/precommit-checks.md`).

## Workflow
- Start each feature on its own branch per `.agentic/docs/rules/branching.md`.
- Run `mvn test` inside Docker before every commit; fix failures before committing.
- Use commit scope `backend`, `security`, or `db` as appropriate.
- Split entity/repo, specification, controller, security, and seed data
  changes into separate commits rather than one large commit.

## Definition of done
- `docker compose up --build` builds the backend image successfully.
- `mvn test` passes.
- New/changed endpoints correctly reject unauthenticated requests and
  behave correctly when authenticated.
- No secrets beyond the existing `.env` dev credentials are introduced.

## Domain modeling notes (depth & seams)

- **Deep module: `CallRecordSpecification`.** Small interface (`Specification<CallRecord> from(filters)`) behind which all
  predicate composition, case-insensitive search, date-range, and sentiment filtering lives.
  The deletion test: removing it would scatter query logic across controllers.
- **Seam: `Repository` vs `Specification`.** The JPA repository is the seam where persistence varies;
  the specification is an adapter that satisfies the query at that seam without leaking SQL to controllers.
- **Leverage:** One specification implementation pays back across every paginated, filtered, and sorted endpoint that reuses it.
