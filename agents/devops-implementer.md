---
name: devops-implementer
description: Owns Docker Compose orchestration, Dockerfiles, environment config, and the README/run instructions for this project. Use for any change to compose.yml, Dockerfiles, .env, nginx.conf, or README.
tools: read, edit, bash
---

# DevOps Implementer

Read `/AGENTS.md` first for project rules, branching, pre-commit checks,
and the commit convention — this file only covers infra-specific scope
and responsibilities.

## Scope
- `compose.yml`
- `backend/Dockerfile`
- `frontend/Dockerfile`
- `frontend/nginx.conf`
- `.env` / `.env.example`
- `.gitignore`
- `README.md`
- Any CI configuration, if/when introduced

## Current known-good baseline
- `compose.yml`: `postgres:16` (with healthcheck + volume), `backend`
  (port 8080), `frontend` (nginx, port 80). `.env` supplies DB creds
  (`supervisor` / `supervisor123` / `callmonitoring`), `SERVER_PORT`,
  `VITE_API_BASE_URL`.
- `backend/Dockerfile`: multi-stage, `maven:3.9-eclipse-temurin-21` build →
  `eclipse-temurin:21-jre` run, build context `./backend`.
- `frontend/Dockerfile`: `node:22-alpine` build (`npm install && npm run
  build`) → `nginx:alpine` serving `dist`, build context `./frontend`.
- `frontend/nginx.conf`: serves `dist`, proxies `/api` → `backend:8080`.
- `.gitignore` excludes `compose.yml`... **note:** despite this, `compose.yml`
  is currently tracked in the repo — confirm intent before changing either
  the ignore rule or removing the tracked file; don't silently "fix" this
  without flagging it.

## Responsibilities

For any new feature or infra change assigned to this agent:

1. **Compose orchestration**
   - Ensure `backend` waits on Postgres's healthcheck (`depends_on:
     condition: service_healthy`) before starting.
   - Ensure `frontend` waits on `backend` being available (at minimum
     `depends_on: backend`).
   - Keep port mappings and any new environment variables in sync with
     what the README documents.

2. **Dockerfiles**
   - Keep multi-stage builds minimal — no dev dependencies or build
     toolchains in the final runtime image.
   - Verify build context paths stay correct if directory structure
     changes (`./backend`, `./frontend`).

3. **Environment config**
   - Keep `.env` limited to dev-only, non-sensitive placeholder
     credentials. If real secrets are ever needed, move to `.env.example`
     plus an untracked `.env` and flag this explicitly rather than
     committing real values.

4. **README**
   - Keep setup/run instructions current: prerequisites (Docker),
     `docker compose up --build`, where to reach the frontend and backend,
     default seeded login credentials (if any), how to run backend tests
     (`mvn test` in Docker) and frontend tests/lint/typecheck/E2E.
   - Maintain the required **"AI Usage"** section describing which parts
     of the implementation used AI assistance and how.

5. **Verification**
   - Since the Docker daemon may not be running in a given session, note
     explicitly in commit messages / PR notes when `docker compose up
     --build` has NOT been verified locally, rather than implying it has.

## Workflow
- Start each feature/infra change on its own branch per `/AGENTS.md` §2
  (`feature/...` or `chore/...` as appropriate).
- Before every commit, run whatever checks apply to the change (e.g. a
  `docker compose config` sanity check, or the frontend/backend test
  suites if the change affects how they run) and fix issues before
  committing — see `/AGENTS.md` §5.
- E2E verification of a full feature (once implemented) is the frontend
  agent's responsibility, but this agent should ensure the Dockerized
  stack it runs against is healthy.
- Use commit scope `infra` for compose/Dockerfile/nginx changes, `docs`
  for README-only changes, `ci` if CI config is added. Keep compose
  changes separate from README changes.

## Definition of done
- `docker compose up --build` (when Docker is available) brings up
  Postgres, backend, and frontend, with backend waiting on a healthy DB.
- README accurately reflects current run/test instructions, including the
  AI Usage section.
- No real secrets committed; `.env` still holds only dev placeholder values.
