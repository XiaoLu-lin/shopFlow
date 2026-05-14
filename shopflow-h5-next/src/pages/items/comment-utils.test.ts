import { describe, expect, test } from 'vitest'
import { createGoodsCommentQuery, normalizeCommentGoodsId, shouldFinishCommentPaging } from './comment-utils'

describe('goods comment utils', () => {
  test('normalizes query goods id to numeric string when possible', () => {
    expect(normalizeCommentGoodsId('101')).toBe('101')
    expect(normalizeCommentGoodsId(undefined)).toBe('')
  })

  test('builds goods comment query with picture filter', () => {
    expect(createGoodsCommentQuery({
      goodsId: '101',
      page: 2,
      limit: 20,
      hasPicture: true,
    })).toEqual({
      goodsId: '101',
      page: 2,
      limit: 20,
      hasPicture: true,
    })
  })

  test('detects paging completion from returned page metadata', () => {
    expect(shouldFinishCommentPaging([], 1, 2)).toBe(true)
    expect(shouldFinishCommentPaging([{ id: 1 }], 2, 2)).toBe(true)
    expect(shouldFinishCommentPaging([{ id: 1 }], 1, 2)).toBe(false)
  })
})
