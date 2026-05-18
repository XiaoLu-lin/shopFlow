import { describe, expect, test } from 'vitest'
import { calculateCartTotal, collectAllIds, collectCheckedIds, collectCheckedProductIds, isAllChecked } from './cart-utils'

const items = [
  { id: 1, productId: 101, checked: true, price: 12, number: 2 },
  { id: 2, productId: 102, checked: false, price: 6, number: 1 },
  { id: 3, productId: 103, checked: true, price: 8, number: 3 },
] as const

describe('cart utils', () => {
  test('collects all cart ids', () => {
    expect(collectAllIds(items as never)).toEqual([1, 2, 3])
  })

  test('collects checked cart ids', () => {
    expect(collectCheckedIds(items as never)).toEqual([1, 3])
  })

  test('calculates checked total price', () => {
    expect(calculateCartTotal(items as never, [1, 3])).toBe(48)
  })

  test('collects checked product ids for cart actions', () => {
    expect(collectCheckedProductIds(items as never, [1, 3])).toEqual([101, 103])
  })

  test('detects all checked state', () => {
    expect(isAllChecked(items as never, [1, 2, 3])).toBe(true)
    expect(isAllChecked(items as never, [1, 3])).toBe(false)
  })
})
