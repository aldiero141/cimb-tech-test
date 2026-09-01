# AGENTS.md — CIMB Tech Test: Call Monitoring

This file is the entry point for any agent (human or AI) working in this repo.
It describes the project, how work is organized across sub-agents, and the
branching, testing, and commit conventions every agent must follow for every
feature — not just the one currently in progress.

## 1. Project summary

**Repo:** `cimb-tech-test` (monorepo) — a supervisor call-monitoring
dashboard: a JWT-protected Spring Boot API backed by PostgreSQL, with a
Vue 3 + PrimeVue frontend.

**Stack**
- Backend: Java 21, Spring Boot 3.5.x, Spring Data JPA, Spring Security, JJWT 0.12.5, PostgreSQL
- Frontend: Vue 3 + Vite, PrimeVue, vee-validate + Zod, TanStack Vue Query, Axios, Pinia, Vue Router
- Infra: Docker Compose (postgres:16, backend :8080, frontend/nginx :80)
- Testing: JUnit (backend), Vitest (frontend unit), ESLint + TypeScript typecheck (frontend), Playwright (E2E)

## Finding context with CodeGraph

Before reaching for grep/find or reading files to understand or locate code,
use the CodeGraph index (`.codegraph/` at the repo root) — it's the fastest,
most accurate way to get context:

- **`codegraph_explore "<question or symbol names>"`** (MCP tool) — returns the
  relevant symbols' verbatim source plus call paths in one call. Use this first
  for most questions: "how does X work", "where/what is X", architecture,
  a bug, or the symbols you're about to change.
- **`codegraph explore "<query>"`** (shell) — same output when the MCP tool isn't
  available.
- **`codegraph status`** — confirm the index is current; run **`codegraph sync`**
  (or `codegraph index`) after adding or changing source files so lookups reflect
  the latest code. Rebuilds are cheap for this repo.

Notes on this repo's index:
- CodeGraph is language-aware — it won't list scaffolding/config assets you
  still read directly (e.g. `application.yml`, `compose.yml`, `Dockerfile`).
- There is currently **no Java source** under `backend/src/main` (only
  `application.yml`), so backend queries return nothing until entity/service/
  controller classes exist. Once Java code lands, run `codegraph sync` and
  re-query.
- If the index is stale or missing a symbol, fall back to Grep/Glob — don't
  silently trust an outdated graph.

**Hard rules for every agent**
- Data displayed in the UI comes from PostgreSQL via the backend API — never
  hardcode data in the frontend.
- Backend API must be JWT-protected.
- The app must be runnable end-to-end per the README instructions.
- README must include an "AI Usage" section.
- Git history must be broken into clear, atomic commits (see §4).
- Every feature needs its own branch (see §2).
- Every commit needs tests/lint/typecheck passing first (see §5).
- Every feature needs backend unit tests, frontend (Vitest) unit tests, and
  Playwright E2E coverage before it's considered done.

**Environment notes**
- Windows host, PowerShell 5.1 shell — no `&&`/`||`; chain with
  `cmd1; if ($?) { cmd2 }`. Use `New-Item -ItemType Directory` and
  `Remove-Item -Recurse -Force` instead of Unix equivalents.
- No local Java/Maven — backend must be built/tested inside Docker.
- Docker daemon may not be running in a given session — verify before
  assuming `docker compose` commands will succeed.
- Register PrimeVue via `app.use(PrimeVue)` with the Aura theme in
  `main.js` — do not add a `vite-plugin-primevue` dependency, it does not
  exist.

## 2. Branching — Conventional Branch 1.1.0

This repo follows [Conventional Branch 1.1.0](https://conventionalbranch.org/).
Every branch name MUST be either a trunk branch or a prefixed branch matching
`<type>/<description>`.

- Every new feature (a user story, ticket, or otherwise scoped unit of
  work) gets its own branch off `master` (the repo's default branch — the
  trunk branch for this repo, alongside `main`/`develop` per the spec)
  before any code is written.

**Format:** `<type>/<description>`

**Allowed `type` prefixes**

| Type | Use for | Example |
|---|---|---|
| `feature` / `feat` | new feature | `feature/add-login-page` |
| `bugfix` / `fix` | bug fix | `fix/header-bug` |
| `hotfix` | urgent production fix | `hotfix/security-patch` |
| `release` | release preparation | `release/v1.2.0` |
| `chore` | non-code tasks (deps, docs, infra) | `chore/update-dependencies` |
| `ai` | any AI agent (generic) | `ai/refactor-auth-flow` |
| `claude` | Claude Code (Anthropic) | `claude/security-patch` |
| `codex` | OpenAI Codex | `codex/optimize-query` |
| `copilot` | GitHub Copilot | `copilot/add-login-page` |
| `cursor` | Cursor | `cursor/fix-header-bug` |

Trunk branches (`main`, `master`, `develop`) do not use a prefix. Custom types
beyond this list are allowed but MUST be documented here before use.

**Branch naming rules** (per spec):

1. Use only lowercase alphanumerics (`a-z`, `0-9`), hyphens (`-`), and dots (`.`
   for version numbers in `release/` branches, e.g. `release/v1.2.0`).
2. No consecutive, leading, or trailing hyphens or dots — e.g. `feature/new--login`,
   `feature/-new-login`, `feature/new-login-`, `release/v1.-2.0` are all invalid.
3. No spaces or underscores — `fix/header bug` and `fix/header_bug` are invalid.
4. Keep it clear and concise — descriptive yet short.
5. Include ticket/issue number in lowercase when one exists —
   e.g. `feature/issue-123-new-login`, `feature/tht-mon-us-001-call-table`
   (lowercase the ticket ID; `feature/THT-MON-US-001-call-table` is invalid).

Formal ABNF (spec): `branch-name = trunk-branch / prefixed-branch` where
`prefixed-branch = type "/" description`, `description = desc-segment *("-" desc-segment)`,
`desc-segment = 1*(ALPHA / DIGIT) *("." 1*(ALPHA / DIGIT))`.

**Repo workflow rules**

- Do not commit feature work directly to `master`. Merge (or open a PR
  against) `master` only once the feature's definition of done (§6) is met.
- One feature branch may span multiple agents (backend + frontend +
  devops) when the feature touches all three layers — coordinate on the
  same branch rather than each agent branching independently.
- **Always push and open PRs through the `no-mistakes` gate** — never
  `git push origin <branch>`. Push with `git push no-mistakes <branch>`
  (or run `no-mistakes`/`no-mistakes -y` for the TUI). The gate runs the
  validation pipeline in a disposable worktree and only forwards the branch
  to `origin` and opens the PR once every check passes. This is the
  mandatory path to any PR for this repo. The gate can be configured to
  validate branch names against this spec (via `commit-check`).

## 3. Sub-agents

Work is split into three implementer agents, each with its own file under
`agents/`. Dispatch to the agent whose surface owns the change; cross-cutting
changes (e.g. an API contract change) should touch backend first, then
frontend, in separate commits.

| Agent | File | Owns |
|---|---|---|
| Backend Implementer | `agents/backend-implementer.md` | `backend/**` — entities, repositories, specifications, controllers, security/JWT, seed data, unit tests |
| Frontend Implementer | `agents/frontend-implementer.md` | `frontend/**` — views, components, stores, composables, schemas, Vitest/Playwright tests |
| DevOps Implementer | `agents/devops-implementer.md` | `compose.yml`, `**/Dockerfile`, `.env`, `nginx.conf`, README, CI concerns |

Each agent file assumes this AGENTS.md has already been read and does not
repeat the project rules above — only its own scope and general
responsibilities.

## 4. Git commit convention — Conventional Commits v1.0.0

Every commit, from every agent, MUST follow
[Conventional Commits 1.0.0](https://www.conventionalcommits.org/en/v1.0.0/):

```
<type>[optional scope][optional !]: <description>

[optional body]

[optional footer(s)]
```

**Types**
| Type | Use for |
|---|---|
| `feat` | a new feature visible to the user or API consumer |
| `fix` | a bug fix |
| `docs` | documentation only (README, comments-as-docs) |
| `style` | formatting, whitespace, lint fixes — no logic change |
| `refactor` | code change that neither fixes a bug nor adds a feature |
| `perf` | a change that improves performance |
| `test` | adding or correcting tests |
| `build` | build system or dependency changes (Maven, npm, Dockerfile) |
| `ci` | CI/CD pipeline configuration |
| `chore` | maintenance that doesn't fit the above (e.g. .gitignore) |
| `revert` | reverting a previous commit |

**Scopes** (recommended, keep them consistent with the agent that owns the area):
- `backend`, `frontend`, `infra` (compose/docker/nginx), `docs`, `security`, `db`

**Rules**
- Description: imperative mood, lowercase, no trailing period —
  e.g. `feat(backend): add server-side filtering to /api/calls`.
- Breaking changes: append `!` after the type/scope AND/OR add a
  `BREAKING CHANGE: <explanation>` footer.
  e.g. `feat(backend)!: require JWT on all /api/calls routes`.
- Reference the ticket/user story in the footer when one exists:
  `Refs: <TICKET-ID>`.
- One logical change per commit — do not mix backend and frontend changes,
  or feature code and formatting, in the same commit.
- `fix` and `feat` commits are what drive semantic versioning, so never use
  them for non-functional changes.

**Examples**
```
feat(db): add new entity and JPA repository
feat(backend): implement specification-based search/filter/sort
feat(backend): add paginated, JWT-protected list endpoint
feat(security): add JWT auth with register/login endpoints
feat(backend): seed sample data via CommandLineRunner
test(backend): add unit tests for the new specification
feat(frontend): scaffold list view with PrimeVue DataTable
feat(frontend): add data-fetching composable via TanStack Vue Query
feat(frontend): add auth store with Pinia and router guard
fix(frontend): register PrimeVue via app.use instead of removed plugin
test(frontend): add Vitest coverage for list view filters
test(e2e): add Playwright flow for the completed feature
build(infra): add multi-stage Dockerfiles for backend and frontend
ci(infra): wire postgres healthcheck into compose.yml
docs: add AI Usage section to README
```

## 5. Pre-commit checks

Before **every** commit, in every agent, run and pass:
1. **Tests** — the relevant unit test suite for whatever was changed
   (`mvn test` for backend, `npm run test` / Vitest for frontend).
2. **Lint** — `npm run lint` (ESLint) for any frontend change.
3. **Typecheck** — `npm run typecheck` (or `vue-tsc --noEmit`) for any
   frontend change involving TypeScript/Zod schemas/typed composables.

Rules:
- If any of the three fail, fix the failure and re-run before committing —
  do not commit broken tests, lint errors, or type errors with the
  intention of "fixing it in a follow-up commit."
- If a fix isn't immediately obvious, keep iterating on the same
  uncommitted change rather than committing a known-broken state.
- These checks are per-commit and scoped to what changed — they are
  separate from, and lighter-weight than, E2E testing (see below).
- **Playwright E2E tests only run once a full feature is implemented** —
  i.e. after the last commit of a feature branch, not after every
  intermediate commit. Do not gate individual commits on E2E passing.

## 6. Definition of done (per feature)

Before a feature branch is considered complete:
1. Code compiles/builds (backend via Docker, frontend via `npm run build`).
2. Unit tests, lint, and typecheck all pass on every commit (§5).
3. Playwright E2E coverage of the full feature passes (run once, after the
   feature is complete — see §5).
4. Commits follow §4 and are already split logically — no squash-and-fix-later.
5. README reflects any new setup/run steps.
6. No hardcoded data leaked into the frontend.
7. The feature branch was pushed through the `no-mistakes` gate
   (`git push no-mistakes <branch>`) and its PR was opened once the gate
   reported all checks green — never pushed straight to `origin` (§2).
