import { describe, it, expect, vi, beforeEach } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useAuthStore } from '../auth'
import { api } from '../../services/api'
import { queryClient } from '../../services/queryClient'

vi.mock('../../services/api', () => ({
  api: {
    post: vi.fn()
  }
}))

vi.mock('../../services/queryClient', () => ({
  queryClient: {
    clear: vi.fn()
  }
}))

describe('useAuthStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('initializes with token and username from localStorage', () => {
    localStorage.setItem('token', 'mock-token')
    localStorage.setItem('username', 'admin')
    const store = useAuthStore()

    expect(store.token).toBe('mock-token')
    expect(store.username).toBe('admin')
    expect(store.isAuthenticated).toBe(true)
  })

  it('login updates store state and localStorage', async () => {
    api.post.mockResolvedValueOnce({
      data: { token: 'jwt-123', username: 'admin' }
    })
    const store = useAuthStore()

    await store.login('admin', 'admin123')

    expect(api.post).toHaveBeenCalledWith('/auth/login', { username: 'admin', password: 'admin123' })
    expect(store.token).toBe('jwt-123')
    expect(store.username).toBe('admin')
    expect(localStorage.getItem('token')).toBe('jwt-123')
    expect(localStorage.getItem('username')).toBe('admin')
  })

  it('logout posts to backend, clears state/localStorage, and clears queryClient', async () => {
    api.post.mockResolvedValueOnce({ data: { message: 'Logged out' } })
    localStorage.setItem('token', 'jwt-123')
    localStorage.setItem('username', 'admin')
    const store = useAuthStore()

    await store.logout()

    expect(api.post).toHaveBeenCalledWith('/auth/logout')
    expect(store.token).toBe('')
    expect(store.username).toBe('')
    expect(localStorage.getItem('token')).toBeNull()
    expect(localStorage.getItem('username')).toBeNull()
    expect(queryClient.clear).toHaveBeenCalled()
  })
})
