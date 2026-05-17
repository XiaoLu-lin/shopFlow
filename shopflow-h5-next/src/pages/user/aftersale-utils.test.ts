import { describe, expect, test } from 'vitest'
import { canCancelAftersale, resolveAftersaleStatusClass, resolveAftersaleStatusText } from './aftersale-utils'

describe('aftersale utils', () => {
  test('resolves aftersale status text from numeric status', () => {
    expect(resolveAftersaleStatusText(0)).toBe('可申请')
    expect(resolveAftersaleStatusText(1)).toBe('已申请')
    expect(resolveAftersaleStatusText(2)).toBe('审核通过')
    expect(resolveAftersaleStatusText(3)).toBe('退款成功')
    expect(resolveAftersaleStatusText(4)).toBe('审核拒绝')
    expect(resolveAftersaleStatusText(5)).toBe('用户已取消')
    expect(resolveAftersaleStatusText(99)).toBe('处理中')
  })

  test('resolves aftersale status class from status text', () => {
    expect(resolveAftersaleStatusClass('已申请')).toBe('status-tag--brand')
    expect(resolveAftersaleStatusClass('审核通过')).toBe('status-tag--brand')
    expect(resolveAftersaleStatusClass('退款成功')).toBe('status-tag--success')
    expect(resolveAftersaleStatusClass('审核拒绝')).toBe('status-tag--muted')
    expect(resolveAftersaleStatusClass('用户已取消')).toBe('status-tag--muted')
    expect(resolveAftersaleStatusClass('处理中')).toBe('status-tag--plain')
  })

  test('only request status can be canceled', () => {
    expect(canCancelAftersale(1)).toBe(true)
    expect(canCancelAftersale(0)).toBe(false)
    expect(canCancelAftersale(2)).toBe(false)
    expect(canCancelAftersale(3)).toBe(false)
  })
})
