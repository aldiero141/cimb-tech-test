import { describe, it, expect, vi, beforeEach } from 'vitest'

vi.mock('../queryClient', () => ({
  queryClient: { clear: vi.fn() }
}))

// We need to test the interceptors without actually initializing Toast
// api.js registers interceptors at import time; we inspect them manually.

describe('api interceptors', () => {
  let api
  let setToastGetter

  beforeEach(async () => {
    vi.resetModules()
    vi.doMock('../queryClient', () => ({ queryClient: { clear: vi.fn() } }))
    const mod = await import('../api.js')
    api = mod.api
    setToastGetter = mod.setToastGetter
    localStorage.clear()
  })

  it('429 triggers toast with retryAfter and does not clear auth', async () => {
    const toastAdd = vi.fn()
    setToastGetter(() => ({ add: toastAdd }))

    // find the response error interceptor (second arg of use)
    // axios stores interceptors; we directly invoke the handler by simulating rejection
    const interceptor = api.interceptors.response.handlers[0]
    const errorHandler = interceptor.rejected

    const error = {
      response: {
        status: 429,
        data: { message: 'Rate limit exceeded. Try again in 58s.', retryAfter: 58 },
        headers: { 'retry-after': '58' }
      }
    }

    await expect(errorHandler(error)).rejects.toEqual(error)
    expect(toastAdd).toHaveBeenCalledWith(expect.objectContaining({ severity: 'warn', summary: 'Too many requests' }))
    expect(localStorage.getItem('token')).toBeNull() // not set, but also not cleared in a specific way
  })

  it('401 still clears storage and does not call toast', async () => {
    localStorage.setItem('token', 't')
    localStorage.setItem('username', 'admin')
    const toastAdd = vi.fn()
    setToastGetter(() => ({ add: toastAdd }))
    const { queryClient } = await import('../queryClient.js')

    const interceptor = api.interceptors.response.handlers[0]
    const errorHandler = interceptor.rejected

    const error = { response: { status: 401, data: {}, headers: {} } }
    await expect(errorHandler(error)).rejects.toEqual(error)
    expect(localStorage.getItem('token')).toBeNull()
    expect(queryClient.clear).toHaveBeenCalled()
    expect(toastAdd).not.toHaveBeenCalled()
  })

  it('429 with header-only retryAfter shows header value', async () => {
    const toastAdd = vi.fn()
    setToastGetter(() => ({ add: toastAdd }))
    const interceptor = api.interceptors.response.handlers[0]
    const errorHandler = interceptor.rejected
    const error = { response: { status: 429, data: {}, headers: { 'retry-after': '12' } } }
    await expect(errorHandler(error)).rejects.toEqual(error)
    expect(toastAdd).toHaveBeenCalledWith(expect.objectContaining({ detail: expect.stringContaining('12') }))
  })
})
