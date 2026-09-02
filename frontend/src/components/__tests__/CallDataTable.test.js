import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'
import CallDataTable from '../CallDataTable.vue'

const records = [
  { id: 1, callId: 'aaa-111', callTimestamp: '2026-07-01T10:30:00', csName: 'Siti Aminah', customerName: 'Budi Santoso', sentimentScore: 85 },
  { id: 2, callId: 'bbb-222', callTimestamp: '2026-07-02T11:00:00', csName: 'Rina Kartika', customerName: 'Dewi Lestari', sentimentScore: 40 }
]

function mountTable(props = {}) {
  return mount(CallDataTable, {
    props: {
      records,
      loading: false,
      totalRecords: 2,
      page: 0,
      size: 5,
      sortField: 'callTimestamp',
      sortOrder: -1,
      ...props
    },
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }]]
    }
  })
}

describe('CallDataTable', () => {
  beforeEach(() => {
    document.body.innerHTML = ''
  })

  it('renders a row per record with sentiment percentage', () => {
    const wrapper = mountTable()
    expect(wrapper.text()).toContain('Siti Aminah')
    expect(wrapper.text()).toContain('Budi Santoso')
    expect(wrapper.text()).toContain('85%')
    expect(wrapper.text()).toContain('40%')
  })

  it('computes the No. column relative to page offset', () => {
    const wrapper = mountTable({ page: 2, size: 5 })
    const rows = wrapper.findAll('tbody tr')
    expect(rows.length).toBe(2)
    // page 2 x 5 = offset 10 => first row No. = 11
    expect(rows[0].text()).toContain('11')
    expect(rows[1].text()).toContain('12')
  })

  it('emits page-change when the paginator advances', async () => {
    const wrapper = mountTable({ totalRecords: 20, records: [records[0]] })
    const nextButton = wrapper.find('button.p-paginator-next')
    expect(nextButton.exists()).toBe(true)
    await nextButton.trigger('click')
    const emitted = wrapper.emitted('page-change')
    expect(emitted).toBeTruthy()
    expect(emitted[0][0]).toBe(1)
  })

  it('emits sort-change when the column sort is requested', async () => {
    const wrapper = mountTable()
    const dataTable = wrapper.findComponent({ name: 'DataTable' })
    dataTable.vm.$emit('sort', { sortField: 'csName', sortOrder: 1 })
    const emitted = wrapper.emitted('sort-change')
    expect(emitted).toBeTruthy()
    expect(emitted[0]).toEqual(['csName', 1])
  })
})