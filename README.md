# CIMB Tech Test — Monorepo

Monorepo setup with **Java Spring Boot 3.5.x** (backend), **Vue 3 + Vite** (frontend), and **PostgreSQL**, orchestrated with **Docker Compose**.

## Structure

```
cimb-tech-test/
├── backend/     # Java 21 + Spring Boot 3.5.x (Maven)
├── frontend/    # Vue 3 + Vite
├── compose.yml  # postgres + backend + frontend orchestration
├── .env         # environment / credential overrides
└── README.md
```

## Prerequisites

- Docker + Docker Compose
- Node.js (v22) + npm (for local frontend dev)
- Java 21 + Maven (for local backend dev)

## Getting Started

### Docker (Recommended)

```bash
# From the project root
docker compose up --build
```

- PostgreSQL on port `5432`
- Spring Boot backend on port `8080`
- Vue (nginx) frontend on port `80`

```bash
docker compose down
```

### Backend (local)

```bash
cd backend
mvn -DskipTests package
java -jar target/call-monitoring-1.0.0.jar
```

Backend runs on `http://localhost:8080`. Points at the `postgres` service by default; adjust `application.yml` for a local Postgres.

### Frontend (local)

```bash
cd frontend
npm install
npm run dev
```

Vite dev server on `http://localhost:5173`. The `/api` proxy forwards to `http://localhost:8080`.

## Dockerfiles

- `backend/Dockerfile` — multi-stage Maven build → Eclipse Temurin JRE runtime
- `frontend/Dockerfile` — Node build → nginx (with `/api` proxy to backend)
