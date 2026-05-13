import { describe, expect, test } from 'vitest'
import { resolveLegacyHashRoute, splitLegacyHashRoute } from './legacy-route-map'

describe('legacy route map', () => {
  test('splits legacy hash path and query', () => {
    expect(splitLegacyHashRoute('#/order/payment/success?orderId=10')).toEqual({
      path: '/order/payment/success',
      query: 'orderId=10',
    })
  })

  test('maps static routes into uni pages', () => {
    expect(resolveLegacyHashRoute('#/items/search')).toBe('/pages/items/search/index')
    expect(resolveLegacyHashRoute('http://localhost:6257/#/user')).toBe('/pages/user/index')
  })

  test('maps dynamic detail routes into uni pages', () => {
    expect(resolveLegacyHashRoute('#/items/detail/88')).toBe('/pages/items/detail/index?id=88')
    expect(resolveLegacyHashRoute('#/items/brand/6')).toBe('/pages/items/brand/index?brandId=6')
    expect(resolveLegacyHashRoute('#/order/payment/success?orderId=10')).toBe(
      '/pages/order/payment-status/index?status=success&orderId=10',
    )
  })
})
