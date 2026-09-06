# Project summary — CIMB Tech Test: Call Monitoring

**Repo:** `cimb-tech-test` (monorepo) — a supervisor call-monitoring
dashboard: a JWT-protected Spring Boot API backed by PostgreSQL, with a
Vue 3 + PrimeVue frontend.

**Stack**
- Backend: Java 21, Spring Boot 3.5.x, Spring Data JPA, Spring Security, JJWT 0.12.5, PostgreSQL
- Frontend: Vue 3 + Vite, PrimeVue (Aura theme), vee-validate + Zod, TanStack Vue Query, Axios, Pinia, Vue Router
- Infra: Docker Compose (postgres:16, backend :8080, frontend/nginx :80)
- Testing: JUnit (backend), Vitest (frontend unit), ESLint + `vue-tsc` typecheck, Playwright (E2E)

**Monorepo layout**
```
cimb-tech-test/
├── backend/     # Java 21 + Spring Boot 3.5.x (Maven)
├── frontend/    # Vue 3 + Vite
├── compose.yml  # postgres + backend + frontend orchestration
├── .env         # environment / credential overrides
├── .agentic/
│   ├── agents/               # implementer definitions
│   ├── docs/rules/           # modular rule files (this folder)
│   └── docs/tickets/<code>/  # grilling-plan.md + to-spec.md per ticket
└── README.md
```

**No `any` in frontend** — TypeScript strict mode. Every frontend type must be explicit.

## Read next (summary-first order)

1. `AGENTS.md` (thin index) — routing to the workflow file you need.
2. `.agentic/docs/rules/hard-rules.md` — non-negotiable rules for every agent.
3. `.agentic/docs/rules/codegraph.md` — how to find context before reading files.
4. `.agentic/docs/rules/env-notes.md` — Windows/PowerShell, Docker, PrimeVue constraints.
5. `.agentic/docs/rules/design-system.md` — PrimeVue Aura design system. **Required before building any UI.**
6. Workflow files as needed: `branching.md`, `sub-agents.md`, `planning-flow.md`, `commits.md`, `precommit-checks.md`, `definition-of-done.md`.
7. Your implementer file in `.agentic/agents/` (`backend-`, `frontend-`, `devops-`, or `qa-implementer.md`).

_Source of truth for this section. Indexed from `AGENTS.md`. See also: `hard-rules.md`, `codegraph.md`._
