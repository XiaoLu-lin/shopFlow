import type { QuickLinkItem } from './entry-utils'

export function resolveUserHeroCopy(input: {
  nickName: string
  token: string
  mobile: string
}) {
  if (input.token.trim()) {
    return {
      eyebrow: 'SHOPFLOW PROFILE',
      title: input.nickName.trim() || 'ShopFlow 用户',
      description: input.mobile.trim()
        ? `手机号 ${maskMobile(input.mobile)}`
        : '继续查看订单进度、支付状态和常用服务。',
    }
  }

  return {
    eyebrow: 'SHOPFLOW PROFILE',
    title: '欢迎来到 ShopFlow',
    description: '登录后可同步查看订单、购物车和个人服务入口。',
  }
}

export function buildOrderShortcutEntries(input: {
  unpaid: number
  unship: number
  unrecv: number
  uncomment: number
}) {
  return [
    { label: '待付款', value: String(input.unpaid), iconText: '付', url: '/pages/user/order-list/index?active=1' },
    { label: '待发货', value: String(input.unship), iconText: '发', url: '/pages/user/order-list/index?active=2' },
    { label: '待收货', value: String(input.unrecv), iconText: '收', url: '/pages/user/order-list/index?active=3' },
    { label: '待评价', value: String(input.uncomment), iconText: '评', url: '/pages/user/order-list/index?active=4' },
    { label: '售后', value: '查看', iconText: '退', url: '/pages/user/refund/index?active=0' },
  ]
}

export function buildProfileServiceEntries(): QuickLinkItem[] {
  return [
    { label: '购物车', type: 'cart', copy: '继续结算已选商品' },
    { label: '优惠券', type: 'coupon', copy: '查看可用优惠与历史记录' },
    { label: '我的收藏', type: 'collect', copy: '回看已收藏的商品与品牌' },
    { label: '收货地址', type: 'address', copy: '管理联系人、电话和默认地址' },
    { label: '退款/售后', type: 'refund', copy: '跟进退款进度和售后处理' },
    { label: '帮助中心', type: 'help', copy: '查看常见问题与使用说明' },
    { label: '服务中心', type: 'service', copy: '反馈问题或获取平台支持' },
  ]
}

function maskMobile(mobile: string) {
  const normalized = mobile.trim()
  if (normalized.length < 7) {
    return normalized
  }

  return `${normalized.slice(0, 3)}****${normalized.slice(-4)}`
}
