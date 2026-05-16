import { describe, expect, it } from 'vitest'
import { formatDiscountLabel, hasRenderableDetail } from './detail-display-utils'

describe('detail-display-utils', () => {
  it('根据现价和原价格式化折扣标签', () => {
    expect(formatDiscountLabel(149, 299)).toBe('5折')
    expect(formatDiscountLabel(89, 299)).toBe('3折')
  })

  it('缺少有效原价时不展示折扣标签', () => {
    expect(formatDiscountLabel(149, undefined)).toBe('')
    expect(formatDiscountLabel(149, 0)).toBe('')
    expect(formatDiscountLabel(149, 149)).toBe('')
  })

  it('识别可渲染的商品详情内容', () => {
    expect(hasRenderableDetail('<p>商品详情</p>')).toBe(true)
    expect(hasRenderableDetail('  商品参数  ')).toBe(true)
    expect(hasRenderableDetail('')).toBe(false)
    expect(hasRenderableDetail('   ')).toBe(false)
    expect(hasRenderableDetail(undefined)).toBe(false)
  })
})
