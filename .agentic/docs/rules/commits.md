# Git commit convention — Conventional Commits v1.0.0

Every commit, from every agent, MUST follow
[Conventional Commits 1.0.0](https://www.conventionalcommits.org/en/v1.0.0/):

```
<type>[optional scope][optional !]: <description>

[optional body]

[optional footer(s)]
```

**Types**
| Type | Use for |
|---|---|
| `feat` | a new feature visible to the user or API consumer |
| `fix` | a bug fix |
| `docs` | documentation only (README, comments-as-docs) |
| `style` | formatting, whitespace, lint fixes — no logic change |
| `refactor` | code change that neither fixes a bug nor adds a feature |
| `perf` | a change that improves performance |
| `test` | adding or correcting tests |
| `build` | build system or dependency changes (Maven, npm, Dockerfile) |
| `ci` | CI/CD pipeline configuration |
| `chore` | maintenance that doesn't fit the above (e.g. .gitignore) |
| `revert` | reverting a previous commit |

**Scopes** (recommended, keep them consistent with the agent that owns the area):
- `backend`, `frontend`, `infra` (compose/docker/nginx), `docs`, `security`, `db`, `e2e`

**Rules**
- Description: imperative mood, lowercase, no trailing period —
  e.g. `feat(backend): add server-side filtering to /api/calls`.
- Breaking changes: append `!` after the type/scope AND/OR add a
  `BREAKING CHANGE: <explanation>` footer.
  e.g. `feat(backend)!: require JWT on all /api/calls routes`.
- Reference the ticket/user story in the footer when one exists:
  `Refs: <TICKET-ID>`.
- One logical change per commit — do not mix backend and frontend changes,
  or feature code and formatting, in the same commit.
- `fix` and `feat` commits are what drive semantic versioning, so never use
  them for non-functional changes.

**Examples**
```
feat(db): add new entity and JPA repository
feat(backend): implement specification-based search/filter/sort
feat(backend): add paginated, JWT-protected list endpoint
feat(security): add JWT auth with register/login endpoints
feat(backend): seed sample data via CommandLineRunner
test(backend): add unit tests for the new specification
feat(frontend): scaffold list view with PrimeVue DataTable
feat(frontend): add data-fetching composable via TanStack Vue Query
feat(frontend): add auth store with Pinia and router guard
fix(frontend): register PrimeVue via app.use instead of removed plugin
test(frontend): add Vitest coverage for list view filters
test(e2e): add Playwright flow for the completed feature
build(infra): add multi-stage Dockerfiles for backend and frontend
ci(infra): wire postgres healthcheck into compose.yml
docs: add AI Usage section to README
```

_Source of truth for this section. Indexed from `AGENTS.md`. See also: `branching.md`, `precommit-checks.md`._
