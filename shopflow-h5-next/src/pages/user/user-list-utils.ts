export const ORDER_TABS = ['全部', '待付款', '待发货', '待收货', '待评价'] as const
export const COUPON_TABS = ['未使用', '已使用', '已过期'] as const

export function normalizeListTab(value: string | undefined, length: number) {
  const parsed = Number(value)

  if (!Number.isInteger(parsed) || parsed < 0 || parsed >= length) {
    return 0
  }

  return parsed
}

export function resolveOrderStatusClass(statusText: string) {
  if (statusText.includes('待')) {
    return 'order-status--pending'
  }

  if (statusText.includes('已') || statusText.includes('完成')) {
    return 'order-status--done'
  }

  return 'order-status--muted'
}
