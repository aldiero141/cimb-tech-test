# Branching — Conventional Branch 1.1.0

This repo follows [Conventional Branch 1.1.0](https://conventionalbranch.org/).
Every branch name MUST be either a trunk branch or a prefixed branch matching
`<type>/<description>`.

- Every new feature (a user story, ticket, or otherwise scoped unit of
  work) gets its own branch off `master` (the repo's default branch — the
  trunk branch for this repo, alongside `main`/`develop` per the spec)
  before any code is written.

**Format:** `<type>/<description>`

**Allowed `type` prefixes**

| Type | Use for | Example |
|---|---|---|
| `feature` / `feat` | new feature | `feature/add-login-page` |
| `bugfix` / `fix` | bug fix | `fix/header-bug` |
| `hotfix` | urgent production fix | `hotfix/security-patch` |
| `release` | release preparation | `release/v1.2.0` |
| `chore` | non-code tasks (deps, docs, infra) | `chore/update-dependencies` |
| `ai` | any AI agent (generic) | `ai/refactor-auth-flow` |
| `claude` | Claude Code (Anthropic) | `claude/security-patch` |
| `codex` | OpenAI Codex | `codex/optimize-query` |
| `copilot` | GitHub Copilot | `copilot/add-login-page` |
| `cursor` | Cursor | `cursor/fix-header-bug` |
| `opencode` | OpenCode (Muse Spark) | `opencode/add-call-table` |

Trunk branches (`main`, `master`, `develop`) do not use a prefix. Custom types
beyond this list are allowed but MUST be documented here before use.

**Branch naming rules** (per spec):

1. Use only lowercase alphanumerics (`a-z`, `0-9`), hyphens (`-`), and dots (`.`
   for version numbers in `release/` branches, e.g. `release/v1.2.0`).
2. No consecutive, leading, or trailing hyphens or dots — e.g. `feature/new--login`,
   `feature/-new-login`, `feature/new-login-`, `release/v1.-2.0` are all invalid.
3. No spaces or underscores — `fix/header bug` and `fix/header_bug` are invalid.
4. Keep it clear and concise — descriptive yet short.
5. Include the ticket/issue code when one exists, placed right after the type
   prefix — format: `<type>/<ticket-code>-<description>`.
   - Lowercase the ticket ID — `feature/tht-mon-us-001-call-table` is valid;
     `feature/THT-MON-US-001-call-table` is invalid.
   - Keep the description short, hyphen-separated.
   - Examples:
     `feature/US-01-call-monitoring`,
     `feature/issue-123-new-login`,
     `feature/tht-mon-us-001-call-table`,
     `fix/JIRA-456-null-pointer-on-login`.

Formal ABNF (spec): `branch-name = trunk-branch / prefixed-branch` where
`prefixed-branch = type "/" description`, `description = desc-segment *("-" desc-segment)`,
`desc-segment = 1*(ALPHA / DIGIT) *("." 1*(ALPHA / DIGIT))`.

**Repo workflow rules**

- Do not commit feature work directly to `master`. Merge (or open a PR
  against) `master` only once the feature's definition of done (`definition-of-done.md`) is met.
- One feature branch may span multiple agents (backend + frontend +
  devops) when the feature touches all three layers — coordinate on the
  same branch rather than each agent branching independently.
- **Always push and open PRs through the `no-mistakes` gate** — never
  `git push origin <branch>`. Push with `git push no-mistakes <branch>`
  (or run `no-mistakes`/`no-mistakes -y` for the TUI). The gate runs the
  validation pipeline in a disposable worktree and only forwards the branch
  to `origin` and opens the PR once every check passes. This is the
  mandatory path to any PR for this repo. The gate can be configured to
  validate branch names against this spec (via `commit-check`).
- **Use the PR/MR template** — every PR/MR must be opened using
  `.agentic/docs/rules/pull_request_template.md`. Copy it into
  `.github/pull_request_template.md` (GitHub) or
  `.gitlab/merge_request_templates/Feature.md` (GitLab) and fill every
  section before opening.

_Source of truth for this section. Indexed from `AGENTS.md`. See also: `commits.md`, `definition-of-done.md`._
