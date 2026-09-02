import { computed, reactive, readonly } from 'vue'
import { keepPreviousData, useQuery } from '@tanstack/vue-query'
import { api } from '../services/api'

function toIsoDate(date) {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

export function useCallRecords() {
  const filters = reactive({
    q: '',
    dateRange: null,
    sentiment: '',
    sortField: 'callTimestamp',
    sortOrder: -1,
    page: 0,
    size: 5
  })

  const params = computed(() => {
    const startDate = filters.dateRange?.[0] ? toIsoDate(filters.dateRange[0]) : null
    const endDate = filters.dateRange?.[1] ? toIsoDate(filters.dateRange[1]) : null
    return {
      q: filters.q || undefined,
      startDate: startDate || undefined,
      endDate: endDate || undefined,
      sentiment: filters.sentiment || undefined,
      sort: filters.sortField,
      order: filters.sortOrder === 1 ? 'asc' : 'desc',
      page: filters.page,
      size: filters.size
    }
  })

  const query = useQuery({
    queryKey: computed(() => [
      'calls',
      params.value.q,
      params.value.startDate,
      params.value.endDate,
      params.value.sentiment,
      params.value.sort,
      params.value.order,
      params.value.page,
      params.value.size
    ]),
    queryFn: async () => {
      const { data } = await api.get('/calls', { params: params.value })
      return data
    },
    placeholderData: keepPreviousData
  })

  function updateFilter(patch) {
    Object.assign(filters, patch, { page: 0 })
  }

  function setPage(page) {
    filters.page = page
  }

  function setSort(field, order) {
    filters.sortField = field
    filters.sortOrder = order
  }

  return {
    filters: readonly(filters),
    data: query.data,
    totalElements: computed(() => query.data.value?.totalElements ?? 0),
    totalPages: computed(() => query.data.value?.totalPages ?? 0),
    isLoading: query.isLoading,
    isFetching: query.isFetching,
    isError: query.isError,
    error: query.error,
    updateFilter,
    setPage,
    setSort
  }
}