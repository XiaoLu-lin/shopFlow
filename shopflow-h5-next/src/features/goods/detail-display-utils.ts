export function formatDiscountLabel(price?: number | string, originalPrice?: number | string): string {
  const current = Number(price)
  const original = Number(originalPrice)

  if (!Number.isFinite(current) || !Number.isFinite(original) || original <= 0 || current >= original) {
    return ''
  }

  const discount = Math.max(1, Math.round((current / original) * 10))
  return `${discount}折`
}

export function hasRenderableDetail(detail?: string | null): boolean {
  return Boolean(detail?.trim())
}

export function formatSavingsCopy(price?: number | string, originalPrice?: number | string): string {
  const current = Number(price)
  const original = Number(originalPrice)

  if (!Number.isFinite(current) || !Number.isFinite(original) || original <= current) {
    return ''
  }

  const savings = Number((original - current).toFixed(2))
  return `比原价省 ¥${savings}`
}

export function resolveDetailSelectionCopy(options: {
  completedSelection: boolean
  stock: number
  selectedValues?: string[]
}) {
  if (!options.completedSelection) {
    return '请选择完整规格后再购买'
  }

  const selected = (options.selectedValues || []).filter(Boolean)
  if (!selected.length) {
    return `库存 ${options.stock}`
  }

  return `已选 ${selected.join(' / ')} · 库存 ${options.stock}`
}
