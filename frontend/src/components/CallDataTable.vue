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
    striped-rows
    class="call-table"
    @page="onPage"
    @sort="onSort"
    empty-message=" "
  >
    <template #empty>
      <EmptyState />
    </template>

    <Column field="no" header="No." :sortable="false">
      <template #body="slotProps">{{ slotProps.index + 1 + page * size }}</template>
    </Column>

    <Column field="callId" header="Call ID" sortable />
    <Column field="callTimestamp" header="Call Timestamp" sortable>
      <template #body="slotProps">{{ formatTimestamp(slotProps.data.callTimestamp) }}</template>
    </Column>
    <Column field="csName" header="CS Name" sortable />
    <Column field="customerName" header="Customer Name" sortable />
    <Column field="sentimentScore" header="Sentiment Score" sortable>
      <template #body="slotProps">
        <span :class="['score', slotProps.data.sentimentScore >= 70 ? 'score-ok' : 'score-warn']">
          {{ slotProps.data.sentimentScore }}%
        </span>
      </template>
    </Column>
  </DataTable>
</template>

<style scoped>
.score-ok {
  color: #16a34a;
  font-weight: 600;
}

.score-warn {
  color: #dc2626;
  font-weight: 600;
}
</style>