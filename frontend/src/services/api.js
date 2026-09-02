import axios from 'axios'
import { queryClient } from './queryClient'

export const api = axios.create({
  baseURL: '/api'
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      if (queryClient) {
        queryClient.clear()
      }
      if (!window.location.pathname.startsWith('/login')) {
        window.location.href = '/login?expired=true'
      }
    }
    return Promise.reject(error)
  }
)