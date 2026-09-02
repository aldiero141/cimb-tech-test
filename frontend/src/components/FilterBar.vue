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
  { label: 'All', value: '' },
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
    <div class="filter-item search">
      <span class="p-input-icon-left w-full">
        <i class="pi pi-search" />
        <InputText v-model="search" placeholder="Search..." class="w-full" data-testid="search-input" />
      </span>
    </div>

    <div class="filter-item">
      <DatePicker
        v-model="dateRangeModel"
        selection-mode="range"
        :min-date="minDate"
        :max-date="maxDate"
        date-format="dd/mm/yy"
        placeholder="Select period (last 3 months)"
        class="w-full"
        data-testid="period-picker"
      />
    </div>

    <div class="filter-item">
      <Select
        v-model="sentimentModel"
        :options="sentimentOptions"
        option-label="label"
        option-value="value"
        placeholder="Sentiment"
        class="w-full"
        data-testid="sentiment-select"
      />
    </div>
  </div>
</template>

<style scoped>
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}

.filter-item {
  flex: 1 1 15rem;
}

.search {
  flex: 2 1 20rem;
}
</style>