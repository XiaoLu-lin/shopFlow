import { describe, expect, test, vi } from 'vitest'
import { createMemoryStorage } from '@/shared/platform/storage'
import { createApiClient } from './client'

describe('request client', () => {
  test('injects shopflow headers from legacy session storage', async () => {
    const storage = createMemoryStorage({
      Authorization: 'user-token',
      ShopFlowTenantToken: 'tenant-token',
    })

    const requestMock = vi.fn().mockResolvedValue({
      data: {
        errno: 0,
        data: {},
      },
      statusCode: 200,
      header: {},
    })
    vi.stubGlobal('uni', {
      request: requestMock,
    })

    const client = createApiClient({ storage })
    await client.get('/cart/index')

    expect(requestMock).toHaveBeenCalledTimes(1)
    expect(requestMock.mock.calls[0][0].header['X-ShopFlow-User-Token']).toBe('user-token')
    expect(requestMock.mock.calls[0][0].header['X-ShopFlow-TenantId']).toBe('tenant-token')
  })

  test('drops non latin1 header values for mini program request compatibility', async () => {
    const storage = createMemoryStorage({
      Authorization: '测试token',
      ShopFlowTenantToken: 'tenant-token',
    })

    const requestMock = vi.fn().mockResolvedValue({
      data: {
        errno: 0,
        data: {},
      },
      statusCode: 200,
      header: {},
    })
    vi.stubGlobal('uni', {
      request: requestMock,
    })

    const client = createApiClient({ storage })
    await client.get('/cart/index')

    expect(requestMock.mock.calls[0][0].header['X-ShopFlow-User-Token']).toBeUndefined()
    expect(requestMock.mock.calls[0][0].header['X-ShopFlow-TenantId']).toBe('tenant-token')
  })

  test('reboots tenant auth when persisted tenant token is invalid html', async () => {
    const storage = createMemoryStorage({
      Authorization: 'user-token',
      ShopFlowTenantToken: '<!doctype html><html lang="zh-CN"><body>vite</body></html>',
    })

    const requestMock = vi.fn()
      .mockResolvedValueOnce({
        data: {
          errno: 'success',
          data: 'tenant-from-auth',
        },
        statusCode: 200,
        header: {},
      })
      .mockResolvedValueOnce({
        data: {
          errno: 0,
          data: {},
        },
        statusCode: 200,
        header: {},
      })

    vi.stubGlobal('uni', {
      request: requestMock,
    })

    const client = createApiClient({ storage })
    await client.get('/cart/index')

    expect(requestMock).toHaveBeenCalledTimes(2)
    expect(requestMock.mock.calls[0][0].url).toContain('/wx/home/auth')
    expect(requestMock.mock.calls[0][0].header['X-ShopFlow-TenantId']).toBeUndefined()
    expect(requestMock.mock.calls[1][0].header['X-ShopFlow-TenantId']).toBe('tenant-from-auth')
    expect(storage.getItem('ShopFlowTenantToken')).toBe('tenant-from-auth')
  })
})
