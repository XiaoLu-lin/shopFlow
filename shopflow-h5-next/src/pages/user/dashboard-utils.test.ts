import { describe, expect, test } from 'vitest'
import { buildOrderShortcutEntries, buildProfileServiceEntries, resolveUserHeroCopy } from './dashboard-utils'

describe('dashboard utils', () => {
  test('resolves suji-style hero copy from session state', () => {
    expect(resolveUserHeroCopy({
      nickName: '阿琳',
      token: 'token-1',
      mobile: '18367402199',
    })).toEqual({
      eyebrow: 'SHOPFLOW PROFILE',
      title: '阿琳',
      description: '手机号 183****2199',
    })

    expect(resolveUserHeroCopy({
      nickName: '',
      token: '',
      mobile: '',
    })).toEqual({
      eyebrow: 'SHOPFLOW PROFILE',
      title: '欢迎来到 ShopFlow',
      description: '登录后可同步查看订单、购物车和个人服务入口。',
    })
  })

  test('builds order shortcut entries with fixed routes and icon text', () => {
    expect(buildOrderShortcutEntries({
      unpaid: 2,
      unship: 1,
      unrecv: 0,
      uncomment: 5,
    })).toEqual([
      { label: '待付款', value: '2', iconText: '付', url: '/pages/user/order-list/index?active=1' },
      { label: '待发货', value: '1', iconText: '发', url: '/pages/user/order-list/index?active=2' },
      { label: '待收货', value: '0', iconText: '收', url: '/pages/user/order-list/index?active=3' },
      { label: '待评价', value: '5', iconText: '评', url: '/pages/user/order-list/index?active=4' },
      { label: '售后', value: '查看', iconText: '退', url: '/pages/user/refund/index?active=0' },
    ])
  })

  test('builds profile service entries in suji list order', () => {
    expect(buildProfileServiceEntries()).toEqual([
      { label: '购物车', type: 'cart', copy: '继续结算已选商品' },
      { label: '优惠券', type: 'coupon', copy: '查看可用优惠与历史记录' },
      { label: '我的收藏', type: 'collect', copy: '回看已收藏的商品与品牌' },
      { label: '收货地址', type: 'address', copy: '管理联系人、电话和默认地址' },
      { label: '退款/售后', type: 'refund', copy: '跟进退款进度和售后处理' },
      { label: '帮助中心', type: 'help', copy: '查看常见问题与使用说明' },
      { label: '服务中心', type: 'service', copy: '反馈问题或获取平台支持' },
    ])
  })
})
