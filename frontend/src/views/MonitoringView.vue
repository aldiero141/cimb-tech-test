<script setup>
import Button from 'primevue/button'
import Message from 'primevue/message'
import { useCallRecords } from '../composables/useCallRecords'
import FilterBar from '../components/FilterBar.vue'
import CallDataTable from '../components/CallDataTable.vue'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const { filters, data, totalElements, totalPages, isLoading, isFetching, isError, updateFilter, setPage, setSort } =
  useCallRecords()

function onSort(field, order) {
  setSort(field, order)
}

function onPageChange(page) {
  setPage(page)
}
</script>

<template>
  <div class="monitoring-page">
    <header class="page-header">
      <h1 class="page-title">Call Monitoring</h1>
      <div class="header-right">
        <span v-if="auth.username" class="welcome">{{ auth.username }}</span>
        <Button label="Sign out" severity="secondary" size="small" @click="auth.logout()" />
      </div>
    </header>

    <Message v-if="isError" severity="error" class="load-error">
      Failed to load call records. Please try again.
    </Message>

    <FilterBar
      :q="filters.q"
      :date-range="filters.dateRange"
      :sentiment="filters.sentiment"
      @update:q="updateFilter({ q: $event })"
      @update:date-range="updateFilter({ dateRange: $event })"
      @update:sentiment="updateFilter({ sentiment: $event })"
    />

    <div class="table-wrapper">
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

    <p v-if="!isLoading && data" class="record-count">
      Showing {{ totalElements }} record{{ totalElements === 1 ? '' : 's' }}
    </p>
  </div>
</template>

<style scoped>
.monitoring-page {
  max-width: 1100px;
  margin: 0 auto;
  padding: 2rem 1.5rem;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1.5rem;
}

.page-title {
  margin: 0;
  font-size: 1.5rem;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.welcome {
  color: #6b7280;
  font-size: 0.875rem;
}

.load-error {
  margin-bottom: 1rem;
}

.table-wrapper {
  margin-top: 1.5rem;
}

.record-count {
  margin-top: 1rem;
  color: #6b7280;
  font-size: 0.875rem;
}
</style>