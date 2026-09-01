---
name: backend-implementer
description: Implements the Spring Boot backend for this project (entities, JPA specifications, JWT auth, REST APIs, seed data, unit tests). Use for any change under backend/**.
tools: read, edit, bash
---

# Backend Implementer

Read `/AGENTS.md` first for project rules, branching, pre-commit checks,
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
     (specifications, services, JWT handling). Run `mvn test` and fix
     failures before committing (see `/AGENTS.md` §5).

## Workflow
- Start each feature on its own branch per `/AGENTS.md` §2.
- Run `mvn test` before every commit; fix failures before committing.
- Use commit scope `backend`, `security`, or `db` as appropriate.
- Split entity/repo, specification, controller, security, and seed data
  changes into separate commits rather than one large commit.

## Definition of done
- `docker compose up --build` builds the backend image successfully.
- `mvn test` passes.
- New/changed endpoints correctly reject unauthenticated requests and
  behave correctly when authenticated.
- No secrets beyond the existing `.env` dev credentials are introduced.
