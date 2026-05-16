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
