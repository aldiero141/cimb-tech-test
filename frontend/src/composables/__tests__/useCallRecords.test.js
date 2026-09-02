import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { defineComponent } from 'vue'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import { useCallRecords } from '../useCallRecords'

vi.mock('../../services/api', () => ({
  api: {
    get: vi.fn()
  }
}))

import { api } from '../../services/api'

function makeClient() {
  return new QueryClient({
    defaultOptions: { queries: { retry: false } }
  })
}

describe('useCallRecords', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  afterEach(() => {
    document.body.innerHTML = ''
  })

  it('fetches paginated records with default params', async () => {
    api.get.mockResolvedValue({
      data: {
        content: [
          { id: 1, callId: 'aaa', csName: 'Siti Aminah', customerName: 'Budi Santoso', callTimestamp: '2026-07-01T10:00:00', sentimentScore: 85 }
        ],
        totalElements: 100,
        totalPages: 20
      }
    })

    const client = makeClient()
    const Wrapper = defineComponent({
      setup() {
        const { data, totalElements } = useCallRecords()
        return { data, totalElements }
      },
      template: '<div><span class="total">{{ totalElements }}</span></div>'
    })

    const wrapper = mount(Wrapper, {
      global: { plugins: [[VueQueryPlugin, { queryClient: client }]] }
    })

    await flushPromises()

    expect(api.get).toHaveBeenCalledTimes(1)
    expect(api.get).toHaveBeenCalledWith('/calls', {
      params: {
        q: undefined,
        startDate: undefined,
        endDate: undefined,
        sentiment: undefined,
        sort: 'callTimestamp',
        order: 'desc',
        page: 0,
        size: 5
      }
    })
    expect(wrapper.find('.total').text()).toBe('100')
  })

  it('updateFilter applies patch and resets page to 0', async () => {
    api.get.mockResolvedValue({
      data: { content: [], totalElements: 0, totalPages: 0 }
    })

    const client = makeClient()
    const Wrapper = defineComponent({
      setup() {
        const { updateFilter } = useCallRecords()
        return { updateFilter }
      },
      template: '<button data-test="filter" @click="updateFilter({ q: \'siti\', page: 3 })">filter</button>'
    })

    const wrapper = mount(Wrapper, {
      global: { plugins: [[VueQueryPlugin, { queryClient: client }]] }
    })

    await flushPromises()

    await wrapper.find('[data-test="filter"]').trigger('click')
    await flushPromises()

    expect(api.get).toHaveBeenLastCalledWith('/calls', {
      params: expect.objectContaining({
        q: 'siti',
        page: 0
      })
    })
  })
})