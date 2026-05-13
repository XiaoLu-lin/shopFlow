import { describe, expect, test, vi } from 'vitest'
import { buildLoginUrl, redirectAfterLogin, resolveNamedPage } from './navigation'

describe('navigation platform adapter', () => {
  test('builds login url with legacy redirect name', () => {
    expect(buildLoginUrl({ redirect: 'orderCheckout' })).toBe('/pages/login/index?redirect=orderCheckout')
  })

  test('maps legacy route names to uni pages', () => {
    expect(resolveNamedPage('user')).toBe('/pages/user/index')
    expect(resolveNamedPage('orderCheckout')).toBe('/pages/order/checkout/index')
    expect(resolveNamedPage('unknown')).toBe('/pages/home/index')
  })

  test('redirects tab pages with switchTab and content pages with redirectTo', () => {
    const switchTab = vi.fn()
    const redirectTo = vi.fn()
    vi.stubGlobal('uni', {
      switchTab,
      redirectTo,
      navigateTo: vi.fn(),
      navigateBack: vi.fn(),
    })

    redirectAfterLogin('user')
    redirectAfterLogin('orderCheckout')

    expect(switchTab).toHaveBeenCalledWith({ url: '/pages/user/index' })
    expect(redirectTo).toHaveBeenCalledWith({ url: '/pages/order/checkout/index' })
  })
})
