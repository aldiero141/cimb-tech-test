# CodeGraph — finding context

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

_Source of truth for this section. Indexed from `AGENTS.md`. See also: `project-summary.md`, `hard-rules.md`._
