import { beforeEach, describe, expect, test, vi } from 'vitest'

const client = {
  get: vi.fn(),
  post: vi.fn(),
  upload: vi.fn(),
}

vi.mock('@/shared/request', () => ({
  getApiClient: () => client,
}))

describe('user api', () => {
  beforeEach(() => {
    client.get.mockReset()
    client.post.mockReset()
    client.upload.mockReset()
  })

  test('maps address list payload into ui-friendly fields', async () => {
    client.get.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: {
          list: [
            {
              id: 1,
              name: '张三',
              tel: '13800000000',
              province: '上海市',
              city: '上海市',
              county: '浦东新区',
              addressDetail: '世纪大道 1 号',
              isDefault: true,
            },
          ],
        },
      },
    })

    const { fetchAddressList } = await import('./api')
    const result = await fetchAddressList()

    expect(result.list[0]).toMatchObject({
      id: 1,
      name: '张三',
      tel: '13800000000',
      address: '上海市上海市浦东新区 世纪大道 1 号',
      isDefault: true,
    })
  })

  test('requests order list by active tab and paging', async () => {
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

    const { fetchUserOrderList } = await import('./api')
    await fetchUserOrderList({
      showType: 2,
      page: 1,
      limit: 10,
    })

    expect(client.get).toHaveBeenLastCalledWith('/order/list', {
      params: {
        showType: 2,
        page: 1,
        limit: 10,
      },
    })
  })

  test('requests coupon list by status and paging', async () => {
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

    const { fetchUserCouponList } = await import('./api')
    await fetchUserCouponList({
      status: 0,
      page: 1,
      limit: 10,
    })

    expect(client.get).toHaveBeenLastCalledWith('/coupon/mylist', {
      params: {
        status: 0,
        page: 1,
        limit: 10,
      },
    })
  })

  test('requests address detail by id and keeps raw form fields', async () => {
    client.get.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: {
          id: 9,
          name: '李四',
          mobile: '13900000000',
          province: '浙江省',
          city: '杭州市',
          county: '西湖区',
          addressDetail: '学院路 88 号',
          areaCode: '330106',
          postalCode: '310000',
          addressAll: '浙江省杭州市西湖区 学院路 88 号',
          isDefault: false,
        },
      },
    })

    const { fetchAddressDetail } = await import('./api')
    const result = await fetchAddressDetail(9)

    expect(client.get).toHaveBeenLastCalledWith('/address/detail', {
      params: {
        id: 9,
      },
    })
    expect(result.mobile).toBe('13900000000')
    expect(result.areaCode).toBe('330106')
  })

  test('saves address with legacy payload shape', async () => {
    client.post.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: 9,
      },
    })

    const { saveAddress } = await import('./api')
    await saveAddress({
      id: 9,
      name: '李四',
      mobile: '13900000000',
      province: '浙江省',
      city: '杭州市',
      county: '西湖区',
      addressDetail: '学院路 88 号',
      areaCode: '330106',
      postalCode: '310000',
      addressAll: '浙江省杭州市西湖区 学院路 88 号',
      isDefault: true,
    })

    expect(client.post).toHaveBeenLastCalledWith('/address/save', {
      id: 9,
      name: '李四',
      mobile: '13900000000',
      province: '浙江省',
      city: '杭州市',
      county: '西湖区',
      addressDetail: '学院路 88 号',
      areaCode: '330106',
      postalCode: '310000',
      addressAll: '浙江省杭州市西湖区 学院路 88 号',
      isDefault: true,
    })
  })

  test('deletes address by legacy id payload', async () => {
    client.post.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: null,
      },
    })

    const { deleteAddress } = await import('./api')
    await deleteAddress(12)

    expect(client.post).toHaveBeenLastCalledWith('/address/delete', {
      id: 12,
    })
  })

  test('requests collect list and toggles collect by legacy payload', async () => {
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
    client.post.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: null,
      },
    })

    const { fetchCollectList, toggleCollect } = await import('./api')
    await fetchCollectList({
      type: 0,
      page: 1,
      limit: 10,
    })
    await toggleCollect(9)

    expect(client.get).toHaveBeenLastCalledWith('/collect/list', {
      params: {
        type: 0,
        page: 1,
        limit: 10,
      },
    })
    expect(client.post).toHaveBeenLastCalledWith('/collect/addordelete', {
      valueId: 9,
      type: 0,
    })
  })

  test('requests issue list and user profile with legacy endpoints', async () => {
    client.get
      .mockResolvedValueOnce({
        data: {
          errno: 0,
          data: {
            list: [
              {
                id: 1,
                question: '如何退款？',
                answer: '在订单页提交退款申请。',
              },
            ],
          },
        },
      })
      .mockResolvedValueOnce({
        data: {
          errno: 0,
          data: {
            nickName: '测试用户',
            avatar: 'avatar.png',
            gender: 1,
            mobile: '13800000000',
          },
        },
      })

    const { fetchIssueList, fetchUserProfile } = await import('./api')
    const issueList = await fetchIssueList()
    const profile = await fetchUserProfile()

    expect(client.get).toHaveBeenNthCalledWith(1, '/issue/list')
    expect(client.get).toHaveBeenNthCalledWith(2, '/user/info')
    expect(issueList[0].question).toBe('如何退款？')
    expect(profile.mobile).toBe('13800000000')
  })

  test('requests aftersale list by legacy status paging and maps compact cards', async () => {
    client.get.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: {
          list: [
            {
              statusText: '退款成功',
              aftersale: {
                id: '9001',
                orderId: '701',
                aftersaleSn: 'AS20260510001',
                status: 3,
                type: 1,
                reason: '尺码不合适',
                amount: 88,
                comment: '申请尽快处理',
                addTime: '2026-05-10 10:00:00',
              },
              orderGoods: {
                goodsName: '轻软跑鞋',
                number: 1,
                price: 88,
                picUrl: 'shoe.png',
                specifications: ['白色', '42'],
              },
            },
          ],
          page: 1,
          pages: 1,
        },
      },
    })

    const { fetchAftersaleList } = await import('./api')
    const result = await fetchAftersaleList({
      status: 3,
      page: 1,
      limit: 10,
    })

    expect(client.get).toHaveBeenLastCalledWith('/aftersale/list', {
      params: {
        status: 3,
        page: 1,
        limit: 10,
      },
    })
    expect(result.list[0]).toMatchObject({
      id: '9001',
      orderId: '701',
      aftersaleSn: 'AS20260510001',
      statusText: '退款成功',
      goodsName: '轻软跑鞋',
      specificationsText: '白色 / 42',
    })
  })

  test('requests aftersale detail by legacy order id payload', async () => {
    client.get.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: {
          order: {
            id: '701',
            orderSn: 'SO20260510001',
            actualPrice: 88,
          },
          aftersale: {
            id: '9001',
            orderId: '701',
            status: 1,
            reason: '尺码不合适',
            amount: 88,
          },
          orderGoods: {
            goodsName: '轻软跑鞋',
            number: 1,
            price: 88,
            picUrl: 'shoe.png',
            specifications: ['白色', '42'],
          },
        },
      },
    })

    const { fetchAftersaleDetail } = await import('./api')
    const result = await fetchAftersaleDetail('701')

    expect(client.get).toHaveBeenLastCalledWith('/aftersale/detail', {
      params: {
        orderId: '701',
      },
    })
    expect(result.aftersale.id).toBe('9001')
    expect(result.orderGoods.goodsName).toBe('轻软跑鞋')
  })

  test('cancels aftersale by legacy id payload', async () => {
    client.post.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: null,
      },
    })

    const { cancelAftersale } = await import('./api')
    await cancelAftersale('9001')

    expect(client.post).toHaveBeenLastCalledWith('/aftersale/cancel', {
      id: '9001',
    })
  })

  test('submits aftersale request with legacy payload shape', async () => {
    client.post.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: null,
      },
    })

    const { submitAftersale } = await import('./api')
    await submitAftersale({
      orderId: '701',
      type: 1,
      amount: 88,
      reason: '尺码不合适',
      comment: '请尽快处理',
      pictures: ['proof-1.png'],
    })

    expect(client.post).toHaveBeenLastCalledWith('/aftersale/submit', {
      orderId: '701',
      type: 1,
      amount: 88,
      reason: '尺码不合适',
      comment: '请尽快处理',
      pictures: ['proof-1.png'],
    })
  })

  test('uploads aftersale proof image with multipart form data', async () => {
    client.upload.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: {
          url: 'https://cdn.shopflow.test/proof-1.png',
        },
      },
    })

    const { uploadAftersaleProof } = await import('./api')
    const result = await uploadAftersaleProof('/tmp/proof.png')

    expect(client.upload).toHaveBeenCalledTimes(1)
    expect(client.upload).toHaveBeenLastCalledWith('/storage/upload', {
      filePath: '/tmp/proof.png',
      name: 'file',
    })
    expect(result.url).toBe('https://cdn.shopflow.test/proof-1.png')
  })

  test('submits feedback and profile updates with legacy payload shape', async () => {
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

    const { submitUserFeedback, updateUserProfile } = await import('./api')
    await submitUserFeedback({
      mobile: '13800000000',
      feedType: '功能异常',
      content: '详情页偶发白屏',
    })
    await updateUserProfile({
      nickname: '新昵称',
      gender: 2,
    })

    expect(client.post).toHaveBeenNthCalledWith(1, '/feedback/submit', {
      mobile: '13800000000',
      feedType: '功能异常',
      content: '详情页偶发白屏',
    })
    expect(client.post).toHaveBeenNthCalledWith(2, '/auth/profile', {
      nickname: '新昵称',
      gender: 2,
    })
  })

  test('uploads aftersale proof through cross-platform upload api', async () => {
    client.upload.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: {
          url: 'https://img.example.com/proof.png',
        },
      },
    })

    const { uploadAftersaleProof } = await import('./api')
    const result = await uploadAftersaleProof('/tmp/proof.png')

    expect(client.upload).toHaveBeenLastCalledWith('/storage/upload', {
      filePath: '/tmp/proof.png',
      name: 'file',
    })
    expect(result.url).toBe('https://img.example.com/proof.png')
  })
})
