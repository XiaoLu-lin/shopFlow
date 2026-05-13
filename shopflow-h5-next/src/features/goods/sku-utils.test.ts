import { describe, expect, test } from 'vitest'
import type { GoodsDetailPayload } from '@/entities/goods/api'
import { buildCartPayload, buildSkuOptionGroups, clampSkuQuantity, hasCompletedSkuSelection, resolveMatchedProduct, resolveSelectedValueNames } from './sku-utils'

const detail: GoodsDetailPayload = {
  info: {
    id: 1001,
    name: '测试商品',
    brief: '测试简介',
    detail: '<p>detail</p>',
    picUrl: 'demo.png',
    retailPrice: 99,
    counterPrice: 109,
    gallery: ['demo.png'],
  },
  attribute: [],
  specificationList: [
    {
      name: '颜色',
      valueList: [
        { id: 1, value: '黑色' },
        { id: 2, value: '白色' },
      ],
    },
    {
      name: '尺码',
      valueList: [
        { id: 3, value: 'S' },
        { id: 4, value: 'M' },
      ],
    },
  ],
  productList: [
    { id: 11, specifications: ['黑色', 'S'], price: 99, number: 3 },
    { id: 12, specifications: ['白色', 'M'], price: 109, number: 5 },
  ],
  userHasCollect: 0,
}

describe('sku utils', () => {
  test('builds sku option groups from detail payload', () => {
    expect(buildSkuOptionGroups(detail)).toEqual([
      {
        name: '颜色',
        values: [
          { id: 1, value: '黑色', picUrl: undefined },
          { id: 2, value: '白色', picUrl: undefined },
        ],
      },
      {
        name: '尺码',
        values: [
          { id: 3, value: 'S', picUrl: undefined },
          { id: 4, value: 'M', picUrl: undefined },
        ],
      },
    ])
  })

  test('resolves selected value names and matched product', () => {
    expect(resolveSelectedValueNames(detail.specificationList, [1, 3])).toEqual(['黑色', 'S'])
    expect(resolveMatchedProduct(detail, [1, 3])?.id).toBe(11)
  })

  test('checks whether sku selection is complete', () => {
    expect(hasCompletedSkuSelection(detail, [1])).toBe(false)
    expect(hasCompletedSkuSelection(detail, [1, 3])).toBe(true)
  })

  test('builds cart payload from selected sku and quantity', () => {
    expect(buildCartPayload(detail, { quantity: 2, selectedValueIds: [2, 4] })).toEqual({
      goodsId: 1001,
      productId: 12,
      number: 2,
    })
  })

  test('clamps quantity inside stock bounds', () => {
    expect(clampSkuQuantity(0, 5)).toBe(1)
    expect(clampSkuQuantity(9, 3)).toBe(3)
  })
})
