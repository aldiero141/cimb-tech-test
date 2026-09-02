import { defineStore } from 'pinia'
import { api } from '../services/api'
import { queryClient } from '../services/queryClient'

const TOKEN_KEY = 'token'
const USERNAME_KEY = 'username'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    username: localStorage.getItem(USERNAME_KEY) || ''
  }),

  getters: {
    isAuthenticated: (state) => Boolean(state.token)
  },

  actions: {
    async login(username, password) {
      const { data } = await api.post('/auth/login', { username, password })
      this.token = data.token
      this.username = data.username
      localStorage.setItem(TOKEN_KEY, data.token)
      localStorage.setItem(USERNAME_KEY, data.username)
    },

    async logout() {
      try {
        if (this.token) {
          await api.post('/auth/logout')
        }
      } catch {
        // Ignore network errors to guarantee client cleanup always proceeds
      } finally {
        this.token = ''
        this.username = ''
        localStorage.removeItem(TOKEN_KEY)
        localStorage.removeItem(USERNAME_KEY)
        if (queryClient) {
          queryClient.clear()
        }
      }
    }
  }
})