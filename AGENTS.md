# AGENTS.md — CIMB Tech Test: Call Monitoring

This file is the entry point for any agent (human or AI) working in this repo.
It is a thin index — the single source of truth for each section lives in
`.agentic/`. Do not duplicate rule text here; follow the links.

## Start here

1. Read `.agentic/docs/rules/project-summary.md` first (stack, repo identity, read-next list).
2. Then use this index to open only the section file you need.

## Index

| Section | File |
|---|---|
| Project summary | `.agentic/docs/rules/project-summary.md` |
| CodeGraph — finding context | `.agentic/docs/rules/codegraph.md` |
| Hard rules for every agent | `.agentic/docs/rules/hard-rules.md` |
| Environment notes | `.agentic/docs/rules/env-notes.md` |
| Branching — Conventional Branch 1.1.0 | `.agentic/docs/rules/branching.md` |
| Sub-agents | `.agentic/docs/rules/sub-agents.md` |
| Feature planning flow (grilling → spec → implementation) | `.agentic/docs/rules/planning-flow.md` |
| Git commit convention — Conventional Commits v1.0.0 | `.agentic/docs/rules/commits.md` |
| Pre-commit checks | `.agentic/docs/rules/precommit-checks.md` |
| Definition of done (per feature) | `.agentic/docs/rules/definition-of-done.md` |
| Pull request / merge request template | `.agentic/docs/rules/pull_request_template.md` |
| Design system — PrimeVue Aura | `.agentic/docs/rules/design-system.md` |

## Implementers

| Agent | File |
|---|---|
| Backend Implementer | `.agentic/agents/backend-implementer.md` |
| Frontend Implementer | `.agentic/agents/frontend-implementer.md` |
| DevOps Implementer | `.agentic/agents/devops-implementer.md` |
| QA Implementer | `.agentic/agents/qa-implementer.md` |

## Skills

Project-specific skills live in `.agents/skills/` (and `.claude/skills/` — kept in sync). If a skill (e.g. `/grilling`, `/implement`, `/tdd`, `/to-spec`, `/code-review`) is not found in the default location (`~/.opencode/skills/` or `~/.config/opencode/skills/`), check `.agents/skills/` and read its `SKILL.md`.

| Skill | File |
|---|---|
| Grill with Docs | `.agents/skills/grill-with-docs/SKILL.md` |
| Implement | `.agents/skills/implement/SKILL.md` |
| To-Spec | `.agents/skills/to-spec/SKILL.md` |
| To-Tickets | `.agents/skills/to-tickets/SKILL.md` |
| TDD | `.agents/skills/tdd/SKILL.md` |
| Code Review | `.agents/skills/code-review/SKILL.md` |
| Improve Codebase Architecture | `.agents/skills/improve-codebase-architecture/SKILL.md` |
| Resolve Merge Conflicts | `.agents/skills/resolving-merge-conflicts/SKILL.md` |

Read order for implementers: `.agentic/docs/rules/project-summary.md` → `AGENTS.md` → the workflow file needed for the task.

## Domain modeling lens

This repo applies the **codebase-design** vocabulary (deep modules, seams, adapters) when shaping new code.
A module is deep when a small interface hides a lot of behaviour — e.g. `CallRecordSpecification` (one method, all predicate
composition hidden) or `useCallRecords` (five return values, all query-key and pagination logic hidden).
Place seams where behaviour actually varies (repository boundary, API client, query layer) and verify that
deleting the module would scatter complexity across callers. See `C:\Users\Aldo\.opencode\skills\codebase-design`.
