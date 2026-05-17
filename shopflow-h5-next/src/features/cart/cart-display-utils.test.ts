import { describe, expect, test } from 'vitest'
import { resolveCartSelectionCopy, resolveCartSubmitMeta } from './cart-display-utils'

describe('cart display utils', () => {
  test('resolves submit meta for checkout and editing states', () => {
    expect(resolveCartSubmitMeta({
      editing: false,
      checkedCount: 0,
    })).toEqual({
      label: '去结算',
      disabled: true,
    })

    expect(resolveCartSubmitMeta({
      editing: false,
      checkedCount: 2,
    })).toEqual({
      label: '去结算',
      disabled: false,
    })

    expect(resolveCartSubmitMeta({
      editing: true,
      checkedCount: 0,
    })).toEqual({
      label: '删除所选商品',
      disabled: true,
    })
  })

  test('formats cart selection copy for footer summary', () => {
    expect(resolveCartSelectionCopy(0)).toBe('未选择商品')
    expect(resolveCartSelectionCopy(1)).toBe('已选 1 件')
    expect(resolveCartSelectionCopy(3)).toBe('已选 3 件')
  })
})
