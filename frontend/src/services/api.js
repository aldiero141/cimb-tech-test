import axios from 'axios'
import { queryClient } from './queryClient'

export const api = axios.create({
  baseURL: '/api'
})

let toastGetter = null
export function setToastGetter(getter) {
  toastGetter = getter
}

function showRateLimitToast(status, data, headers) {
  const retryAfter = data?.retryAfter ?? headers?.['retry-after'] ?? headers?.['Retry-After']
  const seconds = retryAfter != null ? String(retryAfter) : null
  const message = data?.message ?? (seconds ? `Rate limit exceeded. Try again in ${seconds}s.` : 'Too many requests. Please try again later.')
  if (toastGetter) {
    try {
      const toast = toastGetter()
      if (toast?.add) {
        toast.add({ severity: 'warn', summary: 'Too many requests', detail: message, life: 4000 })
      }
    } catch {
      // toast not yet available — ignore
    }
  }
}

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
    const status = error.response?.status
    if (status === 429) {
      showRateLimitToast(status, error.response?.data, error.response?.headers)
    } else if (status === 401) {
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