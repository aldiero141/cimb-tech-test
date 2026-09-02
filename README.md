# CIMB Tech Test — Call Monitoring Dashboard

A supervisor call-monitoring dashboard: a JWT-protected Spring Boot API backed
by PostgreSQL, with a Vue 3 + PrimeVue frontend.

**Feature (US-01):** supervisors sign in, then monitor customer-service calls —
search, filter by period and sentiment, sort, and page through a server-side
paginated table. Sample data (100 calls) is seeded automatically on startup.

## Stack

- **Backend:** Java 21, Spring Boot 3.5.x, Spring Data JPA, Spring Security, JJWT 0.12.5, PostgreSQL
- **Frontend:** Vue 3 + Vite, PrimeVue (Aura theme), vee-validate + Zod, TanStack Vue Query, Axios, Pinia, Vue Router
- **Infra:** Docker Compose (postgres:16, backend :8080, frontend/nginx :80)
- **Testing:** JUnit (backend), Vitest (frontend unit), ESLint + `vue-tsc` typecheck, Playwright (E2E)

## Structure

```
cimb-tech-test/
├── backend/     # Java 21 + Spring Boot 3.5.x (Maven)
├── frontend/    # Vue 3 + Vite
├── compose.yml  # postgres + backend + frontend orchestration
├── .env         # environment / credential overrides
├── .agentic/    # grilling plan + spec for this feature (THT-MON-US-001)
└── README.md
```

## Prerequisites

- Docker + Docker Compose
- Node.js (v22) + npm (frontend unit tests / local dev)
- No local Java/Maven required — the backend is built and tested inside Docker.

## Getting Started

### Docker (Recommended)

```bash
# From the project root
docker compose up --build
```

- PostgreSQL on port `5432`
- Spring Boot backend on port `8080`
- Vue (nginx) frontend on port `80` — open http://localhost

```bash
docker compose down
```

### Backend (local)

The backend is built and tested inside Docker; if you have Java 21 + Maven
locally you can also run it directly:

```bash
cd backend
mvn -DskipTests package
java -jar target/call-monitoring-1.0.0.jar
```

Backend runs on `http://localhost:8080`. Points at the `postgres` service by
default; adjust `application.yml` for a local Postgres.

### Frontend (local)

```bash
cd frontend
npm install
npm run dev
```

Vite dev server on `http://localhost:5173`. The `/api` proxy forwards to
`http://localhost:8080`.

## Seeded data & credentials

- Default supervisor login: **Username** `admin` — **Password** `admin123`
  (BCrypt-hashed in `backend/.../seed/SeedDataRunner`).
- 100 sample call records across the last 6 months, with random Indonesian
  names and weighted sentiment scores (~60% below 70%, ~40% at/above 70%).
- The seed runs automatically on startup via a `CommandLineRunner`.

## API overview

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/auth/login` | Exchange username/password for a JWT |
| `GET` | `/api/calls` | Paginated, filtered, sorted call records (requires `Authorization: Bearer <token>`) |

`GET /api/calls` query parameters:

- `q` — free-text search (matches Call ID or CS name)
- `startDate` / `endDate` — period filter (`yyyy-MM-dd`, inclusive)
- `sentiment` — `below70` or `above70`
- `sort` — `callId`, `callTimestamp`, `csName`, `customerName`, `sentimentScore` (default `callTimestamp`)
- `order` — `asc` / `desc` (default `desc`)
- `page` (0-based, default 0), `size` (default 5)

## Testing

Backend unit tests (spec, JWT, controller) run during the Docker Maven build
stage; any failure stops the build:

```bash
docker compose build backend   # or docker compose up --build
```

Frontend unit tests, lint, and typecheck:

```bash
cd frontend
npm run test        # Vitest unit tests
npm run lint        # ESLint
npm run typecheck   # vue-tsc --noEmit
```

End-to-end (Playwright) — requires the full stack running:

```bash
docker compose up --build   # stack on :80
cd frontend
npm run test:e2e            # playwright test (e2e/monitoring.spec.js)
```

## Dockerfiles

- `backend/Dockerfile` — multi-stage Maven build (unit tests included) → Eclipse Temurin JRE runtime
- `frontend/Dockerfile` — Node build → nginx (with `/api` proxy to backend)

## AI Usage

This project was planned, implemented, and debugged with AI assistance
throughout. The following describes the tools used, the workflow followed,
and the concrete contributions AI made across each feature.

### Tools

| Tool | Role |
|---|---|
| **Google Antigravity IDE** (Gemini / Claude models) | Primary pair-programming agent — architecture decisions, code generation, test authoring, debugging |
| **Antigravity slash commands** (`/grill-me`, `/to-spec`, `/implement`) | Structured feature-planning workflow |
| **`no-mistakes` gate** | Pre-push validation (lint, typecheck, unit tests) before every PR |
| **CodeGraph** | Semantic code search used by agents to locate symbols before editing |

### Workflow

Every feature followed the same planning-to-implementation cycle mandated by
`AGENTS.md`:

1. **Grill the feature** (`/grill-me`) — an interactive interview resolved scope
   questions (API shape, auth rules, data semantics, testing seams). Decisions
   were recorded as ADRs and a domain glossary.
2. **Write the spec** (`/to-spec`) — the interview was synthesised into a spec
   (problem statement, user stories, API contract, implementation decisions,
   out-of-scope notes) stored under `.agentic/doc/<ticket>/`.
3. **Implement** (`/implement`) — the agent read the grilling plan and spec,
   then dispatched backend, frontend, and devops changes in separate, atomic
   commits following Conventional Commits v1.0.0.
4. **Review** — a code-review sub-agent checked every PR against the repo's
   coding standards and the originating spec before merge.

### Features built with AI

#### US-01 — Call Monitoring Dashboard (`feature/US-01-call-monitoring`)

The core feature, built end-to-end by the AI agent:

- **Backend**: `CallRecord` JPA entity, repositories, `CallRecordSpecification`
  (dynamic JPA Criteria API filtering/sorting/pagination), JWT auth
  (`/api/auth/login`), `SeedDataRunner` (100 call records + admin user).
- **Frontend**: `LoginView` (vee-validate + Zod), `MonitoringView` with
  `FilterBar` and `CallDataTable` (PrimeVue), `useCallRecords` composable
  (TanStack Vue Query), Pinia auth store, Axios JWT interceptor, Vue Router
  guard.
- **Infra**: Multi-stage Dockerfiles for backend (Maven → JRE) and frontend
  (Node → nginx), Docker Compose orchestration with postgres healthcheck,
  nginx `/api` proxy.
- **Tests**: JUnit unit tests (specification, JWT, controller), Vitest unit
  tests (composable, components), Playwright E2E (login → monitoring flow).

Spec: `.agentic/doc/THT-MON-US-001/`

#### US-02 — Sign-Out Flow (`feat/sign-out-flow`)

- **Backend**: `POST /api/auth/logout` endpoint with JUnit coverage.
- **Frontend**: Sign-out button in the dashboard header, Pinia store `logout`
  action (clears token + TanStack query cache), 401 Axios response interceptor
  (auto-redirects to login on expired token).
- **Tests**: Vitest unit tests for the updated auth store and header component;
  Playwright E2E covering the full sign-out flow and 401 redirect.

Spec: `.agentic/doc/THT-MON-US-002/`

#### Bug fixes

- **`fix/monitoring-table-filter`** — Fixed HTTP 403 on search (`/api/calls?q=`).
  Root cause: PostgreSQL has no `lower(uuid)` function; Hibernate 6 did not
  emit a SQL `CAST` when calling `cb.lower()` on a UUID field. Fix: replaced
  with `cb.concat(root.get("callId").as(String.class), "")` to force
  text conversion before the `lower()` call. Also scoped the search to
  **Call ID and CS Name** only (previously attempted to search all columns).

### Human oversight

All AI-generated code was reviewed by the developer before merge:

- Every PR was checked against the repo's coding standards (via the
  code-review sub-agent) and the originating spec.
- No secrets, hardcoded data, or frontend-side data fabrication were
  introduced; all displayed data originates from PostgreSQL via the
  JWT-protected API.
- Git history is broken into logical, atomic commits — reviewable
  independently.