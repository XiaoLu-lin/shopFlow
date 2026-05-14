import { describe, expect, test } from 'vitest'
import { AFTERSALE_TABS, canCancelAftersale, resolveAftersaleStatusClass } from './aftersale-utils'

describe('aftersale utils', () => {
  test('keeps wx-aligned aftersale tab order', () => {
    expect(AFTERSALE_TABS).toEqual([
      { label: '申请中', status: 1 },
      { label: '处理中', status: 2 },
      { label: '已完成', status: 3 },
      { label: '已拒绝', status: 4 },
    ])
  })

  test('keeps cancel action only for requested records and resolves reject status class first', () => {
    expect(canCancelAftersale(1)).toBe(true)
    expect(canCancelAftersale(2)).toBe(false)
    expect(resolveAftersaleStatusClass('审核不通过，已拒绝')).toBe('text-ink/45')
    expect(resolveAftersaleStatusClass('退款成功')).toBe('text-[#1f9d55]')
    expect(resolveAftersaleStatusClass('审核通过，待退款')).toBe('text-brand-deep')
  })
})
