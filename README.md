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

- `q` — free-text search (matches Call ID, CS name, customer name, sentiment)
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

This feature was planned and implemented with AI assistance, following the
repo's `AGENTS.md` planning flow:

1. **Grilling to spec** — the ticket was grilling-interviewed and the decisions
   (auth scope, server-side search/filter/sort/pagination, seed data
   semantics, UI language, component decomposition) recorded as ADRs and a
   glossary in `.agentic/doc/THT-MON-US-001/grilling-plan.md`.
2. **Spec** — synthesized into `.agentic/doc/THT-MON-US-001/to-spec.md`
   (problem statement, user stories, API contract, testing decisions).
3. **Implementation** — an AI agent (opencode + Claude Code) implemented the
   backend, frontend, and devops changes against those docs on branch
   `feature/US-01-call-monitoring`, with tests and a code review run as part
   of the branch workflow.

All code is reviewed against the repo's coding standards and the originating
spec before merging; no secrets or hardcoded data are introduced, and data
always flows from PostgreSQL → backend API → UI.