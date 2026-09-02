import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount } from '@vue/test-utils'
import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'
import FilterBar from '../FilterBar.vue'

function mountFilterBar(props = {}) {
  return mount(FilterBar, {
    props: { q: '', dateRange: null, sentiment: '', ...props },
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura, options: { darkModeSelector: false } } }]]
    }
  })
}

describe('FilterBar', () => {
  beforeEach(() => {
    vi.useFakeTimers()
  })

  afterEach(() => {
    vi.useRealTimers()
  })

  it('renders the search input and sentiment filter', () => {
    const wrapper = mountFilterBar()
    expect(wrapper.find('input').exists()).toBe(true)
    // PrimeVue 4 (MIT, free) renders the placeholder until the overlay opens;
    // PrimeVue 5 rendered options inline. Check for placeholder which is stable
    // across both majors and confirms the Select is mounted.
    expect(wrapper.text()).toContain('Sentiment')
  })

  it('emits update:q after typing with debounce', async () => {
    const wrapper = mountFilterBar()
    const input = wrapper.find('input')

    input.setValue('siti')
    await vi.advanceTimersByTimeAsync(400)
    await vi.runAllTimersAsync()

    expect(wrapper.emitted('update:q')).toBeTruthy()
    expect(wrapper.emitted('update:q')[0]).toEqual(['siti'])
  })
})