# Pull Request / Merge Request Template

> **Usage**: Copy this template into `.github/pull_request_template.md` (GitHub)
> or `.gitlab/merge_request_templates/Feature.md` (GitLab) when opening a PR/MR.
> Agents must fill every section before opening the PR/MR.

---

## Summary

<!-- One sentence: what does this PR do? Reference the ticket if one exists. -->
<!-- e.g. "Implements call monitoring list view with search, filters, and pagination. Refs: THT-MON-US-001" -->

**Ticket**: <!-- e.g. THT-MON-US-001, or "N/A" -->
**Branch**: <!-- e.g. feature/tht-mon-us-001-call-monitoring -->
**Type**: <!-- feat / fix / chore / docs / test / build / ci / refactor -->

## What changed

<!-- Bullet list of concrete changes — files added/modified, endpoints, components, schemas. -->
-
-
-

## Agent ownership

<!-- Check all that apply. The owning agent's review is required. -->

- [ ] **Backend** (`backend/**`) — entities, JPA specifications, controllers, security/JWT, seed data
- [ ] **Frontend** (`frontend/**`) — Vue views, components, Pinia stores, composables, PrimeVue, Vitest
- [ ] **DevOps** (`compose.yml`, Dockerfiles, nginx, `.env`, README)
- [ ] **QA** (`e2e/**`, `frontend/e2e/**`) — Playwright E2E tests
- [ ] **Security** (`backend/src/main/java/**/security/**`) — JWT, filters

## Pre-flight checklist

> Every item must be checked before the PR/MR is opened. No exceptions.

### Code quality
- [ ] `mvn test` passes inside Docker (backend unit tests — `docker compose run --rm backend mvn test`)
- [ ] `npm run test` passes (frontend Vitest unit tests)
- [ ] `npm run lint` passes (ESLint — frontend changes only)
- [ ] `npm run typecheck` passes (`vue-tsc --noEmit` — frontend TS if changed)
- [ ] Zero `any` types in all changed frontend files

### Branching & commits
- [ ] Branch name follows Conventional Branch 1.1.0 (`<type>/<description>`)
- [ ] All commits follow Conventional Commits 1.0.0 (`type(scope): description`)
- [ ] One logical change per commit — no mixed backend/frontend commits
- [ ] Ticket ID referenced in commit footers where applicable (`Refs: <TICKET-ID>`)

### Security & data
- [ ] No hardcoded data in frontend — all data comes from backend API
- [ ] Backend API endpoints (except `/api/auth/**`) are JWT-protected
- [ ] No secrets, keys, or credentials in source code or commit messages

### Functionality
- [ ] App builds successfully: `npm run build` (frontend) + backend via Docker
- [ ] README reflects any new setup/run/test instructions
- [ ] App is runnable end-to-end via `docker compose up --build` per README

### E2E (feature-complete PRs only)
- [ ] Playwright E2E tests pass for the feature (run by QA Implementer)
- [ ] E2E covers: happy path, error states, edge cases per spec

## How to test

<!-- Step-by-step instructions for a reviewer to verify this PR locally. -->
1.
2.
3.

## Screenshots / recordings

<!-- If UI changed, paste screenshots or terminal output. Remove this section if not applicable. -->

## Breaking changes

<!-- List any breaking API changes, schema migrations, or env var additions. -->
<!-- If none, write "None." -->

## Follow-up work

<!-- Any known gaps, tech debt, or future tasks this PR enables. -->
<!-- If none, write "None." -->
