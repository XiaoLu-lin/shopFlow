import { beforeEach, describe, expect, test, vi } from 'vitest'

const client = {
  get: vi.fn(),
  post: vi.fn(),
}

vi.mock('@/shared/request', () => ({
  getApiClient: () => client,
}))

describe('cart api', () => {
  beforeEach(() => {
    client.get.mockReset()
    client.post.mockReset()
  })

  test('requests cart list from legacy endpoint', async () => {
    client.get.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: {
          cartList: [],
        },
      },
    })

    const { fetchCartList } = await import('./api')
    await fetchCartList()

    expect(client.get).toHaveBeenLastCalledWith('/cart/index')
  })

  test('updates cart item with legacy payload shape', async () => {
    client.post.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: null,
      },
    })

    const { updateCartItem } = await import('./api')
    await updateCartItem({
      id: 1,
      goodsId: 2,
      productId: 3,
      number: 4,
    })

    expect(client.post).toHaveBeenLastCalledWith('/cart/update', {
      id: 1,
      goodsId: 2,
      productId: 3,
      number: 4,
    })
  })

  test('toggles cart checked state by product ids', async () => {
    client.post.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: null,
      },
    })

    const { toggleCartItems } = await import('./api')
    await toggleCartItems([3, 4], true)

    expect(client.post).toHaveBeenLastCalledWith('/cart/checked', {
      productIds: [3, 4],
      isChecked: 1,
    })
  })

  test('removes cart items by product ids and returns latest cart list', async () => {
    client.post.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: {
          cartList: [],
        },
      },
    })

    const { removeCartItems } = await import('./api')
    await removeCartItems([9])

    expect(client.post).toHaveBeenLastCalledWith('/cart/delete', {
      productIds: [9],
    })
  })

  test('adds normal cart item and fast-buy cart item with shared payload fields', async () => {
    client.post
      .mockResolvedValueOnce({
        data: {
          errno: 0,
          data: null,
        },
      })
      .mockResolvedValueOnce({
        data: {
          errno: 0,
          data: 66,
        },
      })

    const { addCartItem, fastAddCartItem } = await import('./api')
    await addCartItem({
      goodsId: 10,
      productId: 11,
      number: 2,
    })
    const cartId = await fastAddCartItem({
      goodsId: 12,
      productId: 13,
      number: 1,
    })

    expect(client.post).toHaveBeenNthCalledWith(1, '/cart/add', {
      goodsId: 10,
      productId: 11,
      number: 2,
    })
    expect(client.post).toHaveBeenNthCalledWith(2, '/cart/fast/add', {
      goodsId: 12,
      productId: 13,
      number: 1,
    })
    expect(cartId).toBe(66)
  })

  test('requests cart goods count from legacy endpoint', async () => {
    client.get.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: 7,
      },
    })

    const { fetchCartGoodsCount } = await import('./api')
    const count = await fetchCartGoodsCount()

    expect(client.get).toHaveBeenLastCalledWith('/cart/count')
    expect(count).toBe(7)
  })
})
