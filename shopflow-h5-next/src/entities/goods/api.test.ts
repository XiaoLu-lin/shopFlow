import { beforeEach, describe, expect, test, vi } from 'vitest'

const client = {
  get: vi.fn(),
  post: vi.fn(),
}

vi.mock('@/shared/request', () => ({
  getApiClient: () => client,
}))

describe('goods api', () => {
  beforeEach(() => {
    client.get.mockReset()
    client.post.mockReset()
  })

  test('requests hot goods list with paging params', async () => {
    client.get.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: {
          list: [],
          page: 1,
          pages: 1,
        },
      },
    })

    const { fetchHotGoodsList } = await import('./api')
    await fetchHotGoodsList({ page: 1, limit: 10 })

    expect(client.get).toHaveBeenLastCalledWith('/goods/list', {
      params: {
        isHot: true,
        page: 1,
        limit: 10,
      },
    })
  })

  test('requests brand detail and related goods by brand id', async () => {
    client.get
      .mockResolvedValueOnce({
        data: {
          errno: 0,
          data: {
            id: 7,
            name: '品牌 A',
            desc: '品牌描述',
            picUrl: 'brand.png',
          },
        },
      })
      .mockResolvedValueOnce({
        data: {
          errno: 0,
          data: {
            list: [],
            page: 1,
            pages: 1,
          },
        },
      })

    const { fetchBrandDetail, fetchBrandGoodsList } = await import('./api')
    await fetchBrandDetail(7)
    await fetchBrandGoodsList(7)

    expect(client.get).toHaveBeenNthCalledWith(1, '/brand/detail', {
      params: { id: 7 },
    })
    expect(client.get).toHaveBeenNthCalledWith(2, '/goods/list', {
      params: { brandId: 7, page: 1, limit: 20 },
    })
  })

  test('requests topic detail and topic list with legacy query names', async () => {
    client.get
      .mockResolvedValueOnce({
        data: {
          errno: 0,
          data: {
            topic: {
              id: 3,
              title: '专题',
              content: '<p>内容</p>',
            },
            goods: [],
          },
        },
      })
      .mockResolvedValueOnce({
        data: {
          errno: 0,
          data: {
            list: [],
            page: 1,
            pages: 1,
          },
        },
      })

    const { fetchTopicDetail, fetchTopicList } = await import('./api')
    await fetchTopicDetail(3)
    await fetchTopicList({ page: 1, limit: 10 })

    expect(client.get).toHaveBeenNthCalledWith(1, '/topic/detail', {
      params: { id: 3 },
    })
    expect(client.get).toHaveBeenNthCalledWith(2, '/topic/list', {
      params: { page: 1, limit: 10 },
    })
  })

  test('requests groupon list with paging params', async () => {
    client.get.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: {
          list: [],
          page: 1,
          pages: 1,
        },
      },
    })

    const { fetchGrouponList } = await import('./api')
    await fetchGrouponList({ page: 1, limit: 10 })

    expect(client.get).toHaveBeenLastCalledWith('/groupon/list', {
      params: { page: 1, limit: 10 },
    })
  })

  test('requests search index, helper and result with legacy query names', async () => {
    client.get
      .mockResolvedValueOnce({
        data: {
          errno: 0,
          data: {
            defaultKeyword: { keyword: '咖啡' },
            historyKeywordList: [],
            hotKeywordList: [],
          },
        },
      })
      .mockResolvedValueOnce({
        data: {
          errno: 0,
          data: [{ keyword: '电器' }],
        },
      })
      .mockResolvedValueOnce({
        data: {
          errno: 0,
          data: {
            list: [],
            page: 1,
            pages: 1,
            limit: 10,
          },
        },
      })

    const { fetchSearchHelper, fetchSearchIndex, fetchSearchResult } = await import('./api')
    await fetchSearchIndex()
    await fetchSearchHelper('电')
    await fetchSearchResult({
      keyword: '电',
      page: 1,
      limit: 10,
    })

    expect(client.get).toHaveBeenNthCalledWith(1, '/search/index')
    expect(client.get).toHaveBeenNthCalledWith(2, '/search/helper', {
      params: { keyword: '电' },
    })
    expect(client.get).toHaveBeenNthCalledWith(3, '/goods/list', {
      params: {
        categoryId: 0,
        keyword: '电',
        page: 1,
        limit: 10,
      },
    })
  })

  test('clears search history by legacy endpoint', async () => {
    client.post.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: null,
      },
    })

    const { clearSearchHistory } = await import('./api')
    await clearSearchHistory()

    expect(client.post).toHaveBeenLastCalledWith('/search/clearhistory')
  })
})
