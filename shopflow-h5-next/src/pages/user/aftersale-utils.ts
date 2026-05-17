export const AFTERSALE_TABS = [
  { label: '全部', status: 0 },
  { label: '申请中', status: 1 },
  { label: '已退款', status: 3 },
  { label: '已关闭', status: 5 },
] as const

export function resolveAftersaleStatusText(status: number) {
  if (status === 0) {
    return '可申请'
  }

  if (status === 1) {
    return '已申请'
  }

  if (status === 2) {
    return '审核通过'
  }

  if (status === 3) {
    return '退款成功'
  }

  if (status === 4) {
    return '审核拒绝'
  }

  if (status === 5) {
    return '用户已取消'
  }

  return '处理中'
}

export function resolveAftersaleStatusClass(statusText: string) {
  if (statusText.includes('拒绝') || statusText.includes('取消') || statusText.includes('关闭')) {
    return 'status-tag--muted'
  }

  if (statusText.includes('申请') || statusText.includes('审核')) {
    return 'status-tag--brand'
  }

  if (statusText.includes('成功')) {
    return 'status-tag--success'
  }

  return 'status-tag--plain'
}

export function canCancelAftersale(status: number) {
  return status === 1
}
