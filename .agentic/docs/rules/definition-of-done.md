# Definition of done (per feature)

Before a feature branch is considered complete:
1. Code compiles/builds (backend via Docker `docker compose build backend`, frontend via `npm run build`).
2. Unit tests, lint, and typecheck all pass on every commit (`precommit-checks.md`).
3. Playwright E2E coverage of the full feature passes (run once, after the
   feature is complete — by QA Implementer, see `precommit-checks.md`).
4. Commits follow `commits.md` and are already split logically — no squash-and-fix-later.
5. README reflects any new setup/run steps.
6. No hardcoded data leaked into the frontend.
7. No `any` types in any frontend source file.
8. The feature branch was pushed through the `no-mistakes` gate
   (`git push no-mistakes <branch>`) and its PR was opened once the gate
   reported all checks green — never pushed straight to `origin` (`branching.md`).

_Source of truth for this section. Indexed from `AGENTS.md`. See also: `precommit-checks.md`, `branching.md`, `commits.md`._
