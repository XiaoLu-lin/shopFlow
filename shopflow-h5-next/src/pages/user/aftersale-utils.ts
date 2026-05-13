export const AFTERSALE_TABS = [
  { label: '全部', status: 0 },
  { label: '申请中', status: 1 },
  { label: '已退款', status: 3 },
  { label: '已关闭', status: 5 },
] as const

export function resolveAftersaleStatusClass(statusText: string) {
  if (statusText.includes('申请') || statusText.includes('审核')) {
    return 'text-brand-deep'
  }

  if (statusText.includes('成功')) {
    return 'text-[#1f9d55]'
  }

  if (statusText.includes('拒绝') || statusText.includes('取消') || statusText.includes('关闭')) {
    return 'text-ink/45'
  }

  return 'text-ink/60'
}

export function canCancelAftersale(status: number) {
  return status === 1
}
