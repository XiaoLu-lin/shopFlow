import { beforeEach, describe, expect, test, vi } from 'vitest'

const client = {
  get: vi.fn(),
  post: vi.fn(),
}

vi.mock('@/shared/request', () => ({
  getApiClient: () => client,
}))

describe('order api', () => {
  beforeEach(() => {
    client.get.mockReset()
    client.post.mockReset()
  })

  test('submits cancel order request with legacy payload field', async () => {
    client.post.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: null,
      },
    })

    const { cancelUserOrder } = await import('./api')
    await cancelUserOrder(12)

    expect(client.post).toHaveBeenLastCalledWith('/order/cancel', {
      orderId: 12,
    })
  })

  test('submits confirm order request with legacy payload field', async () => {
    client.post.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: null,
      },
    })

    const { confirmUserOrder } = await import('./api')
    await confirmUserOrder(18)

    expect(client.post).toHaveBeenLastCalledWith('/order/confirm', {
      orderId: 18,
    })
  })

  test('submits delete and refund order requests with legacy payload field', async () => {
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
          data: null,
        },
      })

    const { deleteUserOrder, refundUserOrder } = await import('./api')
    await deleteUserOrder(20)
    await refundUserOrder(21)

    expect(client.post).toHaveBeenNthCalledWith(1, '/order/delete', {
      orderId: 20,
    })
    expect(client.post).toHaveBeenNthCalledWith(2, '/order/refund', {
      orderId: 21,
    })
  })

  test('requests order detail by legacy query id', async () => {
    client.get.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: {
          orderInfo: {
            id: '88',
            orderSn: 'SO20260510001',
            consignee: '张三',
            mobile: '13800000000',
            address: '上海市浦东新区世纪大道 1 号',
            goodsPrice: 99,
            freightPrice: 8,
            actualPrice: 107,
            orderStatusText: '待付款',
            handleOption: {
              cancel: true,
              pay: true,
              refund: false,
              confirm: false,
              delete: false,
              comment: false,
            },
          },
          orderGoods: [
            {
              id: 1,
              goodsName: '测试商品',
              number: 2,
              price: 49.5,
              picUrl: 'demo.png',
              specifications: ['默认'],
            },
          ],
        },
      },
    })

    const { fetchOrderDetail } = await import('./api')
    const result = await fetchOrderDetail('88')

    expect(client.get).toHaveBeenLastCalledWith('/order/detail', {
      params: {
        orderId: '88',
      },
    })
    expect(result.orderInfo.orderSn).toBe('SO20260510001')
    expect(result.orderGoods).toHaveLength(1)
  })

  test('submits prepay request as order id list for current backend contract', async () => {
    client.post.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: {
          timeStamp: '1',
        },
      },
    })

    const { submitOrderPrepay } = await import('./api')
    await submitOrderPrepay(['66', '67'])

    expect(client.post).toHaveBeenLastCalledWith('/order/prepay', ['66', '67'])
  })

  test('submits h5 pay request with legacy orderId payload', async () => {
    client.post.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: {
          mwebUrl: 'https://wx.tenpay.com/h5pay',
        },
      },
    })

    const { submitOrderH5Pay } = await import('./api')
    await submitOrderH5Pay(['77', '78'])

    expect(client.post).toHaveBeenLastCalledWith('/order/h5pay', {
      orderId: '77',
    })
  })

  test('normalizes order submit result from legacy single order field', async () => {
    client.post.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: {
          isPay: false,
          orderId: 99,
        },
      },
    })

    const { submitOrder } = await import('./api')
    const result = await submitOrder({
      addressId: '1',
      cartId: '0',
      couponId: '0',
      userCouponId: '0',
      message: '',
    })

    expect(client.post).toHaveBeenLastCalledWith('/order/submit', {
      addressId: '1',
      cartId: '0',
      couponId: '0',
      userCouponId: '0',
      message: '',
      grouponLinkId: 0,
      grouponRulesId: 0,
    })
    expect(result.orderIds).toEqual(['99'])
    expect(result.isPay).toBe(false)
  })
})
