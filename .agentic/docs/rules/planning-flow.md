# Feature planning flow (grilling → spec → implementation)

Every feature/ticket follows the same planning-to-implementation flow so that
decisions are captured once and reused by all implementers:

1. **Grill the feature** — run a `/grilling` session (grill-with-docs /
   grill-me) against the ticket before writing code. Resolve scope questions
   (auth in/out, API shape, data semantics, schema, UI language, seed data,
   testing seams) and record every decision as an ADR plus a glossary.
2. **Write the spec** — run `/to-spec` to synthesize the conversation into a
   spec (problem statement, solution, user stories, implementation decisions,
   testing decisions, out of scope, further notes).
3. **Store both in `.agentic/docs/tickets/<ticket-code>/`** — for every ticket, create a
   folder under `.agentic/docs/tickets/` named by the ticket code and save two files
   there:
   - `grilling-plan.md` — the interview decisions, ADRs, glossary, and the
     ordered implementation plan (rather than asking implementers to re-derive
     them).
   - `to-spec.md` — the synthesized spec.
   - Ticket code matches the story ID (e.g. `.agentic/docs/tickets/THT-MON-US-001/`).
4. **Implement against the docs** — run `/implement` to drive the work. It
   reads `grilling-plan.md` and `to-spec.md` and dispatches to the backend,
   frontend, and devops implementers, following the ADRs, glossary (use the
   domain vocabulary), and ordered commit plan they contain.
5. **E2E after implementation** — after the feature implementers declare
   complete and all unit tests pass, dispatch to QA Implementer to write
   and run Playwright E2E tests.

Rules:
- Do not skip the grilling/spec step for a new ticket; the docs are the
  source of truth for implementation decisions, not a live conversation.
- Keep `.agentic/docs/tickets/<ticket-code>/` scoped to that ticket — one folder per
  ticket, no cross-ticket docs.
- Respect ADRs in the area you're touching; add a new ADR when a decision
  of lasting significance is made rather than overwriting an existing one.
- QA Implementer only starts work after backend/frontend implementers
  declare the feature complete.

_Source of truth for this section. Indexed from `AGENTS.md`. See also: `sub-agents.md`._
