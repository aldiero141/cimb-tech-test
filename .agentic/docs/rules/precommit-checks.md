# Pre-commit checks

Before **every** commit, in every agent, run and pass:
1. **Tests** — the relevant unit test suite for whatever was changed
   (`mvn test` inside Docker for backend, `npm run test` / Vitest for frontend).
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
- **No `any` types** — run `npm run typecheck` to verify strict
  TypeScript compliance before committing.

_Source of truth for this section. Indexed from `AGENTS.md`. See also: `definition-of-done.md`, `commits.md`._
