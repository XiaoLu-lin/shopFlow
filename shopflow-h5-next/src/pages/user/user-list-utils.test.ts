import { describe, expect, test } from 'vitest'
import {
  COUPON_TABS,
  ORDER_TABS,
  normalizeListTab,
  resolveOrderStatusClass,
} from './user-list-utils'

describe('user list utils', () => {
  test('keeps tab labels stable for legacy routes', () => {
    expect(ORDER_TABS).toEqual(['全部', '待付款', '待发货', '待收货', '待评价'])
    expect(COUPON_TABS).toEqual(['未使用', '已使用', '已过期'])
  })

  test('maps order status text into compact accent styles', () => {
    expect(resolveOrderStatusClass('待付款')).toBe('text-brand-deep')
    expect(resolveOrderStatusClass('已完成')).toBe('text-ink/45')
    expect(resolveOrderStatusClass('关闭')).toBe('text-ink/60')
  })

  test('normalizes route tab params into safe indexes', () => {
    expect(normalizeListTab('2', ORDER_TABS.length)).toBe(2)
    expect(normalizeListTab('999', ORDER_TABS.length)).toBe(0)
    expect(normalizeListTab('-1', COUPON_TABS.length)).toBe(0)
    expect(normalizeListTab(undefined, COUPON_TABS.length)).toBe(0)
    expect(normalizeListTab('abc', COUPON_TABS.length)).toBe(0)
  })
})
