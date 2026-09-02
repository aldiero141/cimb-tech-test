<script setup>
import { computed, ref, watch } from 'vue'
import InputText from 'primevue/inputtext'
import DatePicker from 'primevue/datepicker'
import Select from 'primevue/select'

const props = defineProps({
  q: { type: String, default: '' },
  dateRange: { type: Array, default: null },
  sentiment: { type: String, default: '' }
})

const emit = defineEmits(['update:q', 'update:date-range', 'update:sentiment'])

const now = new Date()
const maxDate = new Date(now.getFullYear(), now.getMonth(), now.getDate())
const minDate = new Date(now.getFullYear(), now.getMonth() - 3, now.getDate())

const sentimentOptions = [
  { label: 'All Sentiment', value: '' },
  { label: 'Below 70%', value: 'below70' },
  { label: '70% or above', value: 'above70' }
]

const search = ref(props.q)
let debounceTimer

watch(search, (value) => {
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    emit('update:q', value)
  }, 400)
})

const dateRangeModel = computed({
  get: () => props.dateRange,
  set: (value) => emit('update:date-range', value)
})

const sentimentModel = computed({
  get: () => props.sentiment,
  set: (value) => emit('update:sentiment', value)
})
</script>

<template>
  <div class="filter-bar">
    <!-- Search Input -->
    <div class="filter-item search-item">
      <div class="input-wrapper">
        <span class="material-symbols-outlined input-icon">search</span>
        <InputText
          v-model="search"
          placeholder="Search Call ID or Name..."
          class="custom-filter-input"
          data-testid="search-input"
        />
      </div>
    </div>

    <!-- Date Range Picker -->
    <div class="filter-item period-item">
      <div class="input-wrapper">
        <DatePicker
          v-model="dateRangeModel"
          selection-mode="range"
          :min-date="minDate"
          :max-date="maxDate"
          date-format="dd/mm/yy"
          placeholder="Select period (last 3 months)"
          class="custom-datepicker"
          data-testid="period-picker"
        />
      </div>
    </div>

    <!-- Sentiment Filter -->
    <div class="filter-item sentiment-item">
      <Select
        v-model="sentimentModel"
        :options="sentimentOptions"
        option-label="label"
        option-value="value"
        placeholder="Sentiment"
        class="custom-select"
        data-testid="sentiment-select"
      />
    </div>
  </div>
</template>

<style scoped>
.filter-bar {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  width: 100%;
}

@media (min-width: 640px) {
  .filter-bar {
    flex-direction: row;
    flex-wrap: wrap;
    align-items: center;
  }
}

.filter-item {
  width: 100%;
}

@media (min-width: 640px) {
  .search-item {
    width: 16rem;
  }
  .period-item {
    width: 13rem;
  }
  .sentiment-item {
    width: 12rem;
  }
}

.input-wrapper {
  position: relative;
  width: 100%;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 0.75rem;
  color: var(--text-muted, #64748B);
  font-size: 1.25rem !important;
  pointer-events: none;
  z-index: 2;
}

.custom-filter-input {
  width: 100%;
  padding-left: 2.5rem !important;
  padding-right: 0.75rem !important;
  padding-top: 0.5rem !important;
  padding-bottom: 0.5rem !important;
  font-size: 0.875rem !important;
  border: 1px solid var(--surface-border, #E2E8F0) !important;
  border-radius: var(--radius-sm, 0.25rem) !important;
  background-color: var(--surface-card, #ffffff) !important;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.custom-datepicker {
  width: 100%;
}

:deep(.p-datepicker) {
  font-family: var(--font-sans);
}

.custom-select {
  width: 100%;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}
</style>