# Design system — PrimeVue Aura

## Overview

This repo uses **PrimeVue** with the **Aura** preset. All UI must be built with PrimeVue components
where they exist (DataTable, DatePicker, Select, InputText, Button, Dialog, etc.) rather than
reinventing primitives. Tailwind CSS is available for layout/spacing, but component styling is owned
by PrimeVue's Aura theme.

## Stack

- **PrimeVue** `^4.x` with `Aura` theme — registered via `app.use(PrimeVue, { theme: { preset: Aura } })` in `frontend/src/main.js` (or `main.ts`).
- Do **not** add `vite-plugin-primevue` — it does not exist.
- Icons: **PrimeIcons** (`primeicons`).

## Colors (Aura tokens)

| Token | Value | Usage |
|---|---|---|
| Primary | `#10b981` (emerald) | Primary buttons, active pagination, focus rings |
| Surface | `#ffffff` / `#1e1e1e` | Card and page backgrounds |
| Border | `#e5e7eb` | Table borders, dividers |
| Text Primary | `#111827` | Titles, table cells |
| Text Secondary | `#6b7280` | Metadata, helper text |
| Success | `#10b981` | Sentiment >= 70% |
| Warning | `#f59e0b` | Sentiment 40–69% |
| Danger | `#ef4444` | Sentiment < 40%, errors |

Aura's CSS variables are injected by PrimeVue; override via `primevue.config` or Tailwind, not via ad-hoc inline styles that duplicate tokens.

## Typography

- **Display / Body**: Inter (or system sans) — loaded via PrimeVue/Aura defaults.
- Sizes: 12px metadata, 14px body/table, 16px input, 20–24px page title.
- Weights: 400 body, 500–600 headings and table headers, 700 page titles.

## Elevation & Radius

| Level | Shadow | Usage |
|---|---|---|
| 0 | none | Tables, cards on white |
| 1 | `0 1px 2px rgba(0,0,0,0.06)` | Header, sticky filters |
| 2 | `0 4px 12px rgba(0,0,0,0.08)` | Dropdowns, overlays |

Border radius: 6px inputs/dropdowns, 8px cards/dialogs, 12px large panels.

## Spacing

- Base unit: 4px. Scale: 4 / 8 / 12 / 16 / 24 / 32.
- Page padding: 24px desktop, 16px mobile.
- Section gap: 24px between filter bar and table.

## Components

### Buttons
- Primary: `severity="primary"`, label + optional icon, 36px height.
- Secondary: `severity="secondary"` or `outlined`.
- Icon-only buttons must have `aria-label`.

### DataTable (Call Monitoring)
- `paginator`, `rows=5`, `lazy` server-side mode, `sortMode="single"`.
- Columns: No. (global index), Call ID, Call Timestamp, CS Name, Customer Name, Sentiment Score.
- Sentiment renders as integer `%` with severity tag.
- Empty state via `#empty` template — not a blank table.

### Inputs
- Search: `InputText` with debounce (300ms), `placeholder="Search Call ID or CS Name"`.
- Period: `DatePicker selectionMode="range"` with `:minDate` / `:maxDate` clamped to last 3 months.
- Sentiment: `Select` with options All / Below 70% / 70% or above.

### Navigation
- Top bar 56px, app title left, user + sign-out right.
- Authenticated routes guarded via Vue Router `beforeEach`.

## Do's and Don'ts

- **Do** use PrimeVue components for every form control, table, and overlay.
- **Do** respect Aura's color tokens — don't hardcode hex values that duplicate the theme.
- **Don't** add `vite-plugin-primevue` or import Aura incorrectly (see `env-notes.md`).
- **Do** keep table server-side (`lazy`) — frontend never paginates locally.
- **Don't** hardcode call data — every row comes from `GET /api/calls`.

_Source of truth for this section. See also: `project-summary.md`, `env-notes.md`._
