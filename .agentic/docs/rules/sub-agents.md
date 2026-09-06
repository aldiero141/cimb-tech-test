# Sub-agents

Work is split into four implementer agents, each with its own file under
`.agentic/agents/`.
Dispatch to the agent whose surface owns the change; cross-cutting
changes (e.g. an API contract change) should touch backend first, then
frontend, in separate commits.

| Agent | File | Owns |
|---|---|---|
| Backend Implementer | `.agentic/agents/backend-implementer.md` | `backend/**` — entities, repositories, specifications, controllers, security/JWT, seed data, unit tests |
| Frontend Implementer | `.agentic/agents/frontend-implementer.md` | `frontend/**` — views, components, stores, composables, schemas, Vitest/Playwright tests |
| DevOps Implementer | `.agentic/agents/devops-implementer.md` | `compose.yml`, `**/Dockerfile`, `.env`, `nginx.conf`, README, CI concerns |
| QA Implementer | `.agentic/agents/qa-implementer.md` | `e2e/**`, `frontend/e2e/**` — Playwright E2E tests, fixtures, page objects |

Each agent file assumes `.agentic/docs/rules/project-summary.md` and `AGENTS.md` have already been read
(read summary first, then the index) and does not
repeat the project rules — only its own scope and general
responsibilities.

Read order for implementers: `.agentic/docs/rules/project-summary.md` → `AGENTS.md` → the workflow file needed for the task.

## Dispatch rules
- Backend and QA are **separate agents**: backend writes JUnit unit tests, QA writes Playwright E2E.
- QA only starts work after the feature implementer declares the feature complete.
- Cross-cutting changes (API contract): backend first, then frontend, in separate commits.

_Source of truth for this section. Indexed from `AGENTS.md`. See also: `project-summary.md`, `planning-flow.md`._
