# GEMINI.md — Instructions for Gemini / Antigravity Agents

Before performing any action, planning, coding, or executing commands in this repository, you MUST read and strictly adhere to all instructions, workflows, conventions, and rules defined in [AGENTS.md](file:///e:/Documents/Git/cimb-tech-test/AGENTS.md).

## Core Directives

1. **Primary Source of Truth**: Read [AGENTS.md](file:///e:/Documents/Git/cimb-tech-test/AGENTS.md) for full context, project architecture, sub-agent responsibilities, git branching guidelines, commit conventions, and definition of done.
2. **CodeGraph Exploration**: Always use CodeGraph (`codegraph_explore` tool or `codegraph explore` CLI) before using grep/find or reading files when seeking to understand or locate code symbols.
3. **Conventional Branching**: Ensure work is done on dedicated feature branches following [Conventional Branching 1.1.0](https://conventionalbranch.org/) (e.g. `feature/<description>`, `fix/<description>`). Never commit feature work directly to `master`.
4. **Conventional Commits**: Every commit must follow [Conventional Commits 1.0.0](https://www.conventionalcommits.org/en/v1.0.0/) (`feat(...)`, `fix(...)`, `chore(...)`, etc.).
5. **Data Layer Rules**: Never hardcode data in the frontend UI. All data displayed must originate from the JWT-protected Spring Boot backend API connected to PostgreSQL.
6. **Pre-commit Checks**: Verify unit tests, linting (`npm run lint`), and typechecking (`npm run typecheck`) pass before declaring a task complete.
