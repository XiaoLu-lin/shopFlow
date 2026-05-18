import { describe, expect, test } from 'vitest'
import { resolveBrowsePageState } from './browse-pagination'

describe('browse-pagination', () => {
  test('第一页结果会替换旧列表并推进下一页', () => {
    expect(resolveBrowsePageState([{ id: 99 }], {
      list: [{ id: 1 }, { id: 2 }],
      page: 1,
      pages: 3,
    })).toEqual({
      list: [{ id: 1 }, { id: 2 }],
      nextPage: 2,
      hasMore: true,
    })
  })

  test('后续页结果会追加到已有列表末尾', () => {
    expect(resolveBrowsePageState([{ id: 1 }, { id: 2 }], {
      list: [{ id: 3 }],
      page: 2,
      pages: 3,
    })).toEqual({
      list: [{ id: 1 }, { id: 2 }, { id: 3 }],
      nextPage: 3,
      hasMore: true,
    })
  })

  test('最后一页会关闭继续加载状态', () => {
    expect(resolveBrowsePageState([{ id: 1 }], {
      list: [{ id: 2 }],
      page: 2,
      pages: 2,
    })).toEqual({
      list: [{ id: 1 }, { id: 2 }],
      nextPage: 3,
      hasMore: false,
    })
  })
})
