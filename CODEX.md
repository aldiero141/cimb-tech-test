# CODEX.md — Instructions for OpenAI Codex Agents

Before performing any action, planning, coding, or executing commands in this repository, you MUST read and strictly adhere to all instructions, workflows, conventions, and rules defined in [AGENTS.md](./AGENTS.md).

## Skills Fallback

If a skill referenced in a task (e.g. `/grilling`, `/implement`, `/tdd`, `/to-spec`, `/code-review`) is not found in the default location (`~/.opencode/skills/`), check the project-local skills folder: [.agents/skills/](./.agents/skills/). All project-specific skills live there. Read the skill's `SKILL.md` to load its instructions.

## Core Directives

1. **Primary Source of Truth**: Read [AGENTS.md](./AGENTS.md) for the thin index, then [.agentic/docs/rules/project-summary.md](./.agentic/docs/rules/project-summary.md) for stack and repo identity. Open only the section file you need from the index.
2. **CodeGraph Exploration**: Always use CodeGraph (`codegraph_explore` tool or `codegraph explore` CLI) before using grep/find or reading files when seeking to understand or locate code symbols. See [.agentic/docs/rules/codegraph.md](./.agentic/docs/rules/codegraph.md).
3. **Conventional Branching**: Ensure work is done on dedicated feature branches following [Conventional Branching 1.1.0](https://conventionalbranch.org/) (e.g. `feature/<description>`, `fix/<description>`). Never commit feature work directly to `master`. See [.agentic/docs/rules/branching.md](./.agentic/docs/rules/branching.md).
4. **Conventional Commits**: Every commit must follow [Conventional Commits 1.0.0](https://www.conventionalcommits.org/en/v1.0.0/) (`feat(...)`, `fix(...)`, `chore(...)`, etc.). See [.agentic/docs/rules/commits.md](./.agentic/docs/rules/commits.md).
5. **Data Layer Rules**: Never hardcode data in the frontend UI. All data displayed must originate from the JWT-protected Spring Boot backend API connected to PostgreSQL. See [.agentic/docs/rules/hard-rules.md](./.agentic/docs/rules/hard-rules.md).
6. **Pre-commit Checks**: Verify unit tests, linting, and typechecking pass before declaring a task complete. See [.agentic/docs/rules/precommit-checks.md](./.agentic/docs/rules/precommit-checks.md).
