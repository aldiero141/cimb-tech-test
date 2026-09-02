<script setup>
import { computed } from 'vue'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import EmptyState from './EmptyState.vue'

const props = defineProps({
  records: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  totalRecords: { type: Number, default: 0 },
  page: { type: Number, default: 0 },
  size: { type: Number, default: 5 },
  sortField: { type: String, default: 'callTimestamp' },
  sortOrder: { type: Number, default: -1 }
})

const emit = defineEmits(['page-change', 'sort-change'])

const first = computed(() => props.page * props.size)

function formatTimestamp(value) {
  if (!value) return ''
  return value.replace('T', ' ').slice(0, 19)
}

function onPage(event) {
  const nextPage = Math.floor(event.first / props.size)
  emit('page-change', nextPage)
}

function onSort(event) {
  emit('sort-change', event.sortField, event.sortOrder)
}
</script>

<template>
  <DataTable
    :value="records"
    :loading="loading"
    lazy
    :total-records="totalRecords"
    :first="first"
    :rows="size"
    :sort-field="sortField"
    :sort-order="sortOrder"
    sort-mode="single"
    paginator
    :rows-per-page-options="[5]"
    paginator-template="CurrentPageReport PrevPageLink NextPageLink"
    current-page-report-template="Page {currentPage} of {totalPages}"
    class="call-table"
    @page="onPage"
    @sort="onSort"
    empty-message=" "
  >
    <template #empty>
      <EmptyState />
    </template>

    <Column field="no" header="No." :sortable="false" header-style="width: 4rem;">
      <template #body="slotProps">
        <span class="cell-muted">{{ slotProps.index + 1 + page * size }}</span>
      </template>
    </Column>

    <Column field="callId" header="Call ID" sortable>
      <template #body="slotProps">
        <span class="cell-call-id">{{ slotProps.data.callId }}</span>
      </template>
    </Column>

    <Column field="callTimestamp" header="Call Timestamp" sortable>
      <template #body="slotProps">
        <span class="cell-muted">{{ formatTimestamp(slotProps.data.callTimestamp) }}</span>
      </template>
    </Column>

    <Column field="csName" header="CS Name" sortable>
      <template #body="slotProps">
        <span class="cell-main">{{ slotProps.data.csName }}</span>
      </template>
    </Column>

    <Column field="customerName" header="Customer Name" sortable>
      <template #body="slotProps">
        <span class="cell-main">{{ slotProps.data.customerName }}</span>
      </template>
    </Column>

    <Column field="sentimentScore" header="Sentiment Score" sortable header-style="text-align: right;">
      <template #body="slotProps">
        <div class="score-container">
          <span
            :class="[
              'sentiment-pill',
              slotProps.data.sentimentScore >= 70 ? 'score-positive' : 'score-negative'
            ]"
          >
            {{ slotProps.data.sentimentScore }}%
          </span>
        </div>
      </template>
    </Column>

    <Column header="Actions" :sortable="false" header-style="width: 5rem; text-align: right;">
      <template #body>
        <div class="action-container">
          <button class="action-btn" title="View details">
            <span class="material-symbols-outlined action-icon">visibility</span>
          </button>
        </div>
      </template>
    </Column>
  </DataTable>
</template>

<style scoped>
.cell-muted {
  color: var(--text-muted, #64748B);
  font-size: 0.875rem;
}

.cell-call-id {
  font-weight: 500;
  color: var(--text-main, #0F172A);
}

.cell-main {
  color: var(--text-main, #0F172A);
}

.score-container {
  display: flex;
  justify-content: flex-end;
}

.sentiment-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.125rem 0.625rem;
  border-radius: 9999px;
  font-size: 0.875rem;
  font-weight: 600;
  line-height: 1.25rem;
}

.score-positive {
  background-color: var(--sentiment-pos-bg, #ecfdf5);
  color: var(--sentiment-pos, #10B981);
  border: 1px solid var(--sentiment-pos-border, #a7f3d0);
}

.score-negative {
  background-color: var(--sentiment-neg-bg, #fef2f2);
  color: var(--sentiment-neg, #E11D48);
  border: 1px solid var(--sentiment-neg-border, #fecaca);
}

.action-container {
  display: flex;
  justify-content: flex-end;
}

.action-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.375rem;
  border-radius: var(--radius-sm, 0.25rem);
  color: var(--text-muted, #64748B);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.15s ease;
}

.action-btn:hover {
  background-color: var(--surface-container, #ffe9e9);
  color: var(--primary, #E11D48);
}

.action-icon {
  font-size: 1.25rem !important;
}
</style>