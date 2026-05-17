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
    expect(resolveLegacyHashRoute('#/user/server')).toBe('/pages/user/service/index')
    expect(resolveLegacyHashRoute('#/user/information')).toBe('/pages/user/profile/index')
  })

  test('maps dynamic detail routes into uni pages', () => {
    expect(resolveLegacyHashRoute('#/items/detail/88')).toBe('/pages/items/detail/index?id=88')
    expect(resolveLegacyHashRoute('#/items/brand/6')).toBe('/pages/items/brand/index?brandId=6')
    expect(resolveLegacyHashRoute('#/order/payment/success?orderId=10')).toBe(
      '/pages/order/payment-status/index?status=success&orderId=10',
    )
    expect(resolveLegacyHashRoute('#/order/order-detail?orderId=701')).toBe(
      '/pages/order/detail/index?orderId=701',
    )
    expect(resolveLegacyHashRoute('#/order/payment?orderId=801&orderIds=801')).toBe(
      '/pages/order/payment/index?orderId=801&orderIds=801',
    )
    expect(resolveLegacyHashRoute('#/user/order/list/3')).toBe('/pages/user/order-list/index?active=3')
    expect(resolveLegacyHashRoute('#/user/coupon/list/2')).toBe('/pages/user/coupon/index?active=2')
    expect(resolveLegacyHashRoute('#/user/refund/list')).toBe('/pages/user/refund/index?active=0')
    expect(resolveLegacyHashRoute('#/user/information/setMobile')).toBe('/pages/user/profile-edit-mobile/index')
    expect(resolveLegacyHashRoute('#/user/information/setNickname')).toBe('/pages/user/profile-edit-nickname/index')
    expect(resolveLegacyHashRoute('#/user/information/setPassword')).toBe('/pages/user/profile-edit-password/index')
  })
})
