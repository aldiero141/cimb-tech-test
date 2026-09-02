<script setup>
import Message from 'primevue/message'
import { useRouter } from 'vue-router'
import { useCallRecords } from '../composables/useCallRecords'
import FilterBar from '../components/FilterBar.vue'
import CallDataTable from '../components/CallDataTable.vue'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const router = useRouter()
const { filters, data, totalElements, isLoading, isFetching, isError, updateFilter, setPage, setSort } =
  useCallRecords()

function onSort(field, order) {
  setSort(field, order)
}

function onPageChange(page) {
  setPage(page)
}

async function handleSignOut() {
  await auth.logout()
  router.push({ name: 'login', query: { signedOut: 'true' } })
}
</script>

<template>
  <div class="monitoring-layout">
    <!-- Top Navigation Bar (Stitch Design) -->
    <header class="top-navbar">
      <div class="nav-container">
        <div class="nav-left">
          <a class="brand-logo" href="#">Call Monitoring</a>
          <nav class="nav-links">
            <a href="#" class="nav-link active" @click.prevent>Live Monitor</a>
            <a href="#" class="nav-link" @click.prevent>Dashboard</a>
            <a href="#" class="nav-link" @click.prevent>History</a>
          </nav>
        </div>

        <div class="nav-right">
          <button class="icon-btn" title="Notifications">
            <span class="material-symbols-outlined">notifications</span>
          </button>
          <button class="icon-btn" title="Help">
            <span class="material-symbols-outlined">help</span>
          </button>
          <div class="nav-divider" />
          <div class="profile-section">
            <div class="avatar-circle">
              <span class="material-symbols-outlined avatar-icon">person</span>
            </div>
            <span v-if="auth.username" class="user-name">{{ auth.username }}</span>
            <button class="sign-out-btn" @click="handleSignOut">Sign Out</button>
          </div>
        </div>
      </div>
    </header>

    <!-- Main Content Area -->
    <main class="main-content">
      <div class="page-intro-row">
        <div class="page-intro">
          <h1 class="page-title">Live Monitoring</h1>
          <p class="page-subtitle">Real-time overview of customer service calls and sentiment scores.</p>
        </div>

        <div class="filters-container">
          <FilterBar
            :q="filters.q"
            :date-range="filters.dateRange"
            :sentiment="filters.sentiment"
            @update:q="updateFilter({ q: $event })"
            @update:date-range="updateFilter({ dateRange: $event })"
            @update:sentiment="updateFilter({ sentiment: $event })"
          />
        </div>
      </div>

      <Message v-if="isError" severity="error" class="load-error">
        Failed to load call records. Please try again.
      </Message>

      <div class="table-container">
        <CallDataTable
          :records="data?.content ?? []"
          :loading="isLoading || isFetching"
          :total-records="totalElements"
          :page="filters.page"
          :size="filters.size"
          :sort-field="filters.sortField"
          :sort-order="filters.sortOrder"
          @sort-change="onSort"
          @page-change="onPageChange"
        />
      </div>
    </main>
  </div>
</template>

<style scoped>
.monitoring-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--surface-bg, #F8FAFC);
}

.top-navbar {
  background-color: var(--surface-card, #ffffff);
  border-bottom: 1px solid var(--surface-border, #E2E8F0);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
  position: sticky;
  top: 0;
  z-index: 50;
}

.nav-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0.75rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.brand-logo {
  font-size: 1.5rem;
  font-weight: 700;
  letter-spacing: -0.01em;
  color: var(--primary-brand, #b80035);
  text-decoration: none;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.nav-link {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-muted, #64748B);
  text-decoration: none;
  padding: 0.375rem 0.75rem;
  border-radius: var(--radius-sm, 0.25rem);
  transition: all 0.15s ease;
}

.nav-link:hover {
  background-color: var(--surface-container, #ffe9e9);
  color: var(--primary-brand, #b80035);
}

.nav-link.active {
  color: var(--primary-brand, #b80035);
  border-bottom: 2px solid var(--primary-brand, #b80035);
  border-radius: 0;
  padding-bottom: 0.25rem;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.icon-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 9999px;
  color: var(--text-muted, #64748B);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.15s ease;
}

.icon-btn:hover {
  background-color: var(--surface-header, #F1F5F9);
  color: var(--text-main, #0F172A);
}

.nav-divider {
  width: 1px;
  height: 1.5rem;
  background-color: var(--surface-border, #E2E8F0);
  margin: 0 0.5rem;
}

.profile-section {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.avatar-circle {
  width: 2rem;
  height: 2rem;
  border-radius: 9999px;
  background-color: var(--surface-header, #F1F5F9);
  border: 1px solid var(--surface-border, #E2E8F0);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted, #64748B);
}

.avatar-icon {
  font-size: 1.25rem !important;
}

.user-name {
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--text-main, #0F172A);
}

.sign-out-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-muted, #64748B);
  transition: color 0.15s ease;
  padding: 0.25rem 0.5rem;
}

.sign-out-btn:hover {
  color: var(--primary, #E11D48);
}

.main-content {
  flex-grow: 1;
  width: 100%;
  max-width: 1280px;
  margin: 0 auto;
  padding: 2rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.page-intro-row {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

@media (min-width: 1024px) {
  .page-intro-row {
    flex-direction: row;
    justify-content: space-between;
    align-items: flex-end;
  }
}

.page-intro {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.page-title {
  margin: 0;
  font-size: 1.875rem;
  line-height: 2.375rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: var(--text-main, #0F172A);
}

.page-subtitle {
  margin: 0;
  font-size: 0.875rem;
  line-height: 1.25rem;
  color: var(--text-muted, #64748B);
}

.filters-container {
  width: 100%;
}

@media (min-width: 1024px) {
  .filters-container {
    width: auto;
  }
}

.load-error {
  margin: 0;
}

.table-container {
  width: 100%;
}

@media (max-width: 768px) {
  .nav-links {
    display: none;
  }
}
</style>