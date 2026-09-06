# Hard rules for every agent

- **No `any` in frontend** — TypeScript strict mode. Every frontend type must be explicit. Never use `any` as a type annotation, return type, or parameter type in `frontend/src/**`.
- Data displayed in the UI comes from PostgreSQL via the backend API — never hardcode data in the frontend.
- Backend API must be JWT-protected (access token in `Authorization: Bearer` header). `POST /api/auth/login` is the only public auth endpoint.
- The app must be runnable end-to-end per the README instructions (`docker compose up --build`).
- README must include an "AI Usage" section.
- Git history must be broken into clear, atomic commits (see `commits.md`).
- Every feature needs its own branch (see `branching.md`).
- Every commit needs tests/lint/typecheck passing first (see `precommit-checks.md`).
- Every feature needs backend unit tests (JUnit), frontend unit tests (Vitest), and
  Playwright E2E coverage before it's considered done.

_Source of truth for this section. Indexed from `AGENTS.md`. See also: `project-summary.md`, `definition-of-done.md`._
