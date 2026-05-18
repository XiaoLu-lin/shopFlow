import { describe, expect, it } from 'vitest'
import {
  formatDiscountLabel,
  formatSavingsCopy,
  hasRenderableDetail,
  resolveDetailSelectionCopy,
} from './detail-display-utils'

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

  it('根据原价和现价生成节省文案', () => {
    expect(formatSavingsCopy(239, 299)).toBe('比原价省 ¥60')
    expect(formatSavingsCopy(149.5, 199)).toBe('比原价省 ¥49.5')
  })

  it('没有有效差价时不展示节省文案', () => {
    expect(formatSavingsCopy(149, undefined)).toBe('')
    expect(formatSavingsCopy(149, 149)).toBe('')
    expect(formatSavingsCopy(199, 149)).toBe('')
  })

  it('根据规格完成度生成库存提示', () => {
    expect(resolveDetailSelectionCopy({
      completedSelection: false,
      stock: 8,
      selectedValues: ['米白', '37'],
    })).toBe('请选择完整规格后再购买')

    expect(resolveDetailSelectionCopy({
      completedSelection: true,
      stock: 8,
      selectedValues: ['米白', '37'],
    })).toBe('已选 米白 / 37 · 库存 8')

    expect(resolveDetailSelectionCopy({
      completedSelection: true,
      stock: 2,
      selectedValues: [],
    })).toBe('库存 2')
  })
})
