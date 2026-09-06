# Environment notes

- Windows host, PowerShell 5.1 shell — no `&&`/`||`; chain with
  `cmd1; if ($?) { cmd2 }`. Use `New-Item -ItemType Directory` and
  `Remove-Item -Recurse -Force` instead of Unix equivalents.
- No local Java/Maven — backend must be built/tested inside Docker
  (`docker compose build backend` / `docker compose run --rm backend mvn test`).
  Verify changes via `mvn test` inside the container, not by assuming a local toolchain.
- Docker daemon may not be running in a given session — verify before
  assuming `docker compose` commands will succeed.
- Frontend uses Node 22 + npm (not pnpm). Use `npm install`, `npm run dev`, `npm run build` locally.
- Register PrimeVue via `app.use(PrimeVue)` with the Aura theme in
  `main.js` — do not add a `vite-plugin-primevue` dependency, it does not
  exist.

_Source of truth for this section. Indexed from `AGENTS.md`. See also: `project-summary.md`._
