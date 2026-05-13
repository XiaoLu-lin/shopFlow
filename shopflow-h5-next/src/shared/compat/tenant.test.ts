import { describe, expect, test } from 'vitest'
import { createMemoryStorage } from '@/shared/platform/storage'
import { extractTenantToken, handleTenantPayload, shouldBootstrapTenant, withAppIdParam } from './tenant'

describe('tenant compat', () => {
  test('adds appid when request params are missing it', () => {
    const params = withAppIdParam({ page: 1 })

    expect(params.page).toBe(1)
    expect(params.appid).toBe('1649067')
  })

  test('requires tenant bootstrap for protected requests without a tenant token', () => {
    expect(shouldBootstrapTenant('/wx/cart/index', '')).toBe(true)
    expect(shouldBootstrapTenant('/wx/home/auth', '')).toBe(false)
  })

  test('extracts and persists tenant token from nested payload', () => {
    const storage = createMemoryStorage()
    expect(
      extractTenantToken({
        data: {
          tenantId: 'tenant-2',
        },
      }),
    ).toBe('tenant-2')

    expect(handleTenantPayload({ data: 'tenant-3' }, storage)).toBe('tenant-3')
    expect(storage.getItem('ShopFlowTenantToken')).toBe('tenant-3')
  })
})
