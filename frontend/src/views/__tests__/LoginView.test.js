import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { createRouter, createMemoryHistory } from 'vue-router'
import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'
import LoginView from '../LoginView.vue'

const authStore = {
  token: '',
  username: '',
  login: vi.fn(),
  logout: vi.fn()
}

vi.mock('../../stores/auth', () => ({
  useAuthStore: () => authStore
}))

function buildRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/login', name: 'login', component: LoginView },
      { path: '/monitoring', name: 'monitoring', component: { template: '<div />' } }
    ]
  })
}

function mountLogin(router) {
  return mount(LoginView, {
    global: {
      plugins: [
        [PrimeVue, { theme: { preset: Aura, options: { darkModeSelector: false } } }],
        createPinia(),
        router
      ]
    }
  })
}

async function settle() {
  await flushPromises()
  await new Promise((resolve) => setTimeout(resolve, 20))
}

describe('LoginView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
    localStorage.clear()
  })

  it('shows validation errors when submitted empty', async () => {
    const router = buildRouter()
    router.push('/login')
    await router.isReady()
    const wrapper = mountLogin(router)

    await wrapper.find('form').trigger('submit')
    await settle()

    expect(wrapper.text()).toContain('Username is required')
    expect(wrapper.text()).toContain('Password is required')
    expect(authStore.login).not.toHaveBeenCalled()
  })

  it('logs in and routes to monitoring on success', async () => {
    authStore.login.mockResolvedValue()
    const router = buildRouter()
    router.push('/login')
    await router.isReady()
    const wrapper = mountLogin(router)

    const inputs = wrapper.findAll('input')
    await inputs[0].setValue('admin')
    await inputs[1].setValue('admin123')
    await wrapper.find('form').trigger('submit')
    await settle()

    expect(authStore.login).toHaveBeenCalledWith('admin', 'admin123')
    await router.isReady()
    expect(router.currentRoute.value.name).toBe('monitoring')
  })

  it('shows an error message on invalid credentials', async () => {
    authStore.login.mockRejectedValue(new Error('Unauthorized'))
    const router = buildRouter()
    router.push('/login')
    await router.isReady()
    const wrapper = mountLogin(router)

    const inputs = wrapper.findAll('input')
    await inputs[0].setValue('admin')
    await inputs[1].setValue('wrong')
    await wrapper.find('form').trigger('submit')
    await settle()

    expect(wrapper.text()).toContain('Invalid username or password')
  })
})