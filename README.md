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
| **Google Antigravity IDE** (Gemini / Claude models) | Primary pair-programming agent in early features — architecture decisions, code generation, test authoring, debugging |
| **OpenCode + Muse Spark** (`opencode.ai`, `muse-spark-1.2`) | Current primary agent runtime — same slash-command workflow (`/grill-me`, `/to-spec`, `/implement`), CodeGraph MCP, `no-mistakes` gate integration |
| **Antigravity / OpenCode slash commands** (`/grill-me`, `/to-spec`, `/implement`) | Structured feature-planning workflow (grill → spec → implement → review) |
| **`no-mistakes` gate** | Pre-push validation (lint, typecheck, unit tests) before every PR |
| **CodeGraph** | Semantic code search used by agents to locate symbols before editing (MCP `codegraph_explore` + `codegraph explore` CLI) |

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

### Agent instruction files (`AGENTS.md` & `GEMINI.md`)

Both files are hand-authored and committed — not auto-generated — and together define how any AI agent (Antigravity, OpenCode, Claude, etc.) must work in this repo. `AGENTS.md` is the single source of truth; `GEMINI.md` is a thin adapter for Gemini/Antigravity.

**`AGENTS.md` — canonical entry point** (`AGENTS.md:1`):

Created at repo init and evolved via `docs:` commits (branching rules in `5bb94cf`, sub-agent + planning flow in `f1868c8`). Every agent must read it before any action. Structure:

| § | Heading | What it defines |
|---|---|---|
| 1 | Project summary | Stack, hard rules (no hardcoded data, JWT, runnable via README), environment notes (PowerShell 5.1, Docker, PrimeVue registration), and CodeGraph usage (`codegraph_explore` / `codegraph explore` / `codegraph sync`) |
| 2 | Branching — Conventional Branch 1.1.0 | Branch format `<type>/<description>`, allowed `type` prefixes, naming rules, ticket-code placement, trunk branches (`master`/`main`/`develop`), `no-mistakes` push gate |
| 3 | Sub-agents | Three implementers under `agents/` — `backend-implementer.md`, `frontend-implementer.md`, `devops-implementer.md` — and dispatch order (backend first for cross-cutting changes) |
| 3.1 | Feature planning flow | `grill-me` → `to-spec` → `implement` cycle; outputs stored in `.agentic/doc/<ticket>/grilling-plan.md` + `to-spec.md` with ADRs and glossary |
| 4 | Git commit convention | Conventional Commits v1.0.0 — types, scopes (`backend`/`frontend`/`infra`/…), breaking-change and `Refs:` footer rules, atomic-commit examples |
| 5 | Pre-commit checks | Tests + `npm run lint` + `npm run typecheck` must pass before every commit; Playwright E2E only after a feature is complete |
| 6 | Definition of done | Build, tests/lint/typecheck, E2E, commit style, README, no hardcoded data, `no-mistakes` gate |

**`GEMINI.md` — Gemini/Antigravity adapter** (`GEMINI.md:1`):

Added in `eb3dbbb` (`doc: add gemini.md`). 12 lines, intentionally minimal — it does not duplicate `AGENTS.md`. It instructs Gemini/Antigravity to read `AGENTS.md` as the primary source of truth and restates the six core directives: (1) AGENTS.md is authoritative, (2) use CodeGraph before grep/find, (3) Conventional Branch 1.1.0 on feature branches, (4) Conventional Commits 1.0.0, (5) no hardcoded data / JWT-protected API, (6) pre-commit checks. This keeps Antigravity aligned with OpenCode and other agents without drift between files.

**OpenCode integration:**

OpenCode (`~/.config/opencode/opencode.jsonc:1`, `~/.config/opencode/AGENTS.md:1`) reads the same `AGENTS.md` at the repo root via its AGENTS.md convention and loads the CodeGraph MCP (`codegraph serve --mcp`) plus the project skills under `.claude/skills/` / `.agents/skills/` (synced via `skills-lock.json`). No separate `OPENCODE.md` is needed — extending the repo means editing `AGENTS.md` (add a section or ADR) and, only if the change is Gemini-specific, updating the 6-line directive list in `GEMINI.md`.

### Human oversight

All AI-generated code was reviewed by the developer before merge:

- Every PR was checked against the repo's coding standards (via the
  code-review sub-agent) and the originating spec.
- No secrets, hardcoded data, or frontend-side data fabrication were
  introduced; all displayed data originates from PostgreSQL via the
  JWT-protected API.
- Git history is broken into logical, atomic commits — reviewable
  independently.