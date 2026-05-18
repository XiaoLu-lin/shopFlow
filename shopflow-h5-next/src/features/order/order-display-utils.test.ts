import { describe, expect, test } from 'vitest'
import {
  resolveOrderEmptyState,
  resolveOrderOverviewStats,
  resolvePaymentMethodOptions,
} from './order-display-utils'

describe('order display utils', () => {
  test('maps order tabs into neutral empty state copy', () => {
    expect(resolveOrderEmptyState(0)).toEqual({
      title: '暂时还没有订单',
      description: '挑几件喜欢的商品，下单后就会展示在这里。',
    })

    expect(resolveOrderEmptyState(1)).toEqual({
      title: '还没有待付款订单',
      description: '待付款订单会在你提交订单后出现在这里。',
    })

    expect(resolveOrderEmptyState(4)).toEqual({
      title: '还没有待评价订单',
      description: '确认收货后的订单会在这里等待评价。',
    })
  })

  test('builds payment method options from runtime environment', () => {
    expect(resolvePaymentMethodOptions(true)).toEqual([
      {
        key: 'wx',
        title: '微信支付',
        description: '当前在微信内，将优先发起 JSAPI 支付。',
        stateLabel: '推荐',
        enabled: true,
      },
      {
        key: 'ali',
        title: '支付宝',
        description: '入口保留，当前版本暂未开放。',
        stateLabel: '暂未开放',
        enabled: false,
      },
    ])

    expect(resolvePaymentMethodOptions(false)[0]).toEqual({
      key: 'wx',
      title: '微信支付',
      description: '当前环境将优先尝试 H5 支付跳转。',
      stateLabel: '推荐',
      enabled: true,
    })
  })

  test('builds compact overview stats for order list header', () => {
    expect(resolveOrderOverviewStats([
      { label: '待付款', count: 0 },
      { label: '待收货', count: 3 },
      { label: '售后中', count: 1 },
    ])).toEqual([
      { label: '待付款', count: 0, value: '暂无' },
      { label: '待收货', count: 3, value: '3 笔' },
      { label: '售后中', count: 1, value: '1 笔' },
    ])
  })
})
