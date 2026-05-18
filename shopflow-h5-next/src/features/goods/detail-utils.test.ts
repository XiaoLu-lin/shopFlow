import { describe, expect, test } from 'vitest'
import { buildAttributePairs, buildSpecSummary } from './detail-utils'

const detail = {
  attribute: [
    { attribute: '材质', value: '棉' },
    { attribute: '适用', value: '四季' },
  ],
  specificationList: [
    {
      name: '颜色',
      valueList: [{ id: 1, value: '黑色' }, { id: 2, value: '奶白' }],
    },
  ],
} as never

describe('detail utils', () => {
  test('builds attribute pairs', () => {
    expect(buildAttributePairs(detail)).toEqual([
      ['材质', '棉'],
      ['适用', '四季'],
    ])
  })

  test('builds specification summary', () => {
    expect(buildSpecSummary(detail)).toEqual([
      {
        name: '颜色',
        values: ['黑色', '奶白'],
      },
    ])
  })
})
