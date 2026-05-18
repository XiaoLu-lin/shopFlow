import type { CartItem } from '@/entities/cart/api'

export function collectCheckedIds(items: CartItem[]): number[] {
  return items.filter((item) => item.checked).map((item) => item.id)
}

export function collectAllIds(items: CartItem[]): number[] {
  return items.map((item) => item.id)
}

export function calculateCartTotal(items: CartItem[], checkedIds: number[]) {
  return items.reduce((total, item) => {
    if (!checkedIds.includes(item.id)) {
      return total
    }
    return total + item.price * item.number
  }, 0)
}

export function collectCheckedProductIds(items: CartItem[], checkedIds: number[]) {
  return items
    .filter((item) => checkedIds.includes(item.id))
    .map((item) => item.productId)
}

export function isAllChecked(items: CartItem[], checkedIds: number[]) {
  return items.length > 0 && checkedIds.length === items.length
}
