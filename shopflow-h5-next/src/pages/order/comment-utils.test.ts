import { describe, expect, test } from 'vitest'
import { buildOrderCommentPayload, getCommentStarText, validateOrderCommentForm } from './comment-utils'

describe('order comment utils', () => {
  test('maps star score into readable text', () => {
    expect(getCommentStarText(1)).toBe('很差')
    expect(getCommentStarText(5)).toBe('十分满意')
  })

  test('validates content and star before submit', () => {
    expect(validateOrderCommentForm({ star: 0, content: '', picUrls: [] })).toBe('请选择评分')
    expect(validateOrderCommentForm({ star: 5, content: '  ', picUrls: [] })).toBe('请填写评论')
  })

  test('builds payload with hasPicture flag', () => {
    expect(buildOrderCommentPayload({
      goodsId: 101,
      star: 5,
      content: '穿着舒服',
      picUrls: ['https://cdn.shopflow.test/comment-1.png'],
    })).toEqual({
      goodsId: 101,
      star: 5,
      content: '穿着舒服',
      hasPicture: true,
      picUrls: ['https://cdn.shopflow.test/comment-1.png'],
    })
  })
})
