import { describe, expect, test } from 'vitest'
import { resolveQuickLinkAction, type QuickLinkItem } from './entry-utils'

describe('user entry utils', () => {
  test('routes order, cart and address links to real pages', () => {
    expect(resolveQuickLinkAction(makeLink('我的订单', 'order'))).toEqual({
      action: 'navigateTo',
      url: '/pages/user/order-list/index?active=0',
    })

    expect(resolveQuickLinkAction(makeLink('购物车', 'cart'))).toEqual({
      action: 'switchTab',
      url: '/pages/order/cart/index',
    })

    expect(resolveQuickLinkAction(makeLink('收货地址', 'address'))).toEqual({
      action: 'navigateTo',
      url: '/pages/user/address/index',
    })

    expect(resolveQuickLinkAction(makeLink('我的收藏', 'collect'))).toEqual({
      action: 'navigateTo',
      url: '/pages/user/collect/index',
    })

    expect(resolveQuickLinkAction(makeLink('设置', 'profile'))).toEqual({
      action: 'navigateTo',
      url: '/pages/user/profile/index',
    })

    expect(resolveQuickLinkAction(makeLink('帮助中心', 'help'))).toEqual({
      action: 'navigateTo',
      url: '/pages/user/help/index',
    })

    expect(resolveQuickLinkAction(makeLink('服务中心', 'service'))).toEqual({
      action: 'navigateTo',
      url: '/pages/user/service/index',
    })

    expect(resolveQuickLinkAction(makeLink('意见反馈', 'feedback'))).toEqual({
      action: 'navigateTo',
      url: '/pages/user/feedback/index',
    })

    expect(resolveQuickLinkAction(makeLink('优惠券', 'coupon'))).toEqual({
      action: 'navigateTo',
      url: '/pages/user/coupon/index?active=0',
    })

    expect(resolveQuickLinkAction(makeLink('退款/售后', 'refund'))).toEqual({
      action: 'navigateTo',
      url: '/pages/user/refund/index?active=0',
    })
  })

  test('keeps placeholder links as toast actions', () => {
    expect(resolveQuickLinkAction(makeLink('帮助中心', 'placeholder'))).toEqual({
      action: 'toast',
      title: '帮助中心下一批补齐',
    })
  })
})

function makeLink(label: string, type: QuickLinkItem['type']): QuickLinkItem {
  return {
    label,
    type,
    copy: '',
  }
}
