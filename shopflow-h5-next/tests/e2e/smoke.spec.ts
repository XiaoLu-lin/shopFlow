import { expect, test } from '@playwright/test'

let submittedOrderCommentPayload: Record<string, unknown> | null = null

test.beforeEach(async ({ page }) => {
  submittedOrderCommentPayload = null

  await page.addInitScript(() => {
    window.localStorage.setItem('Authorization', 'e2e-token')
    window.localStorage.setItem('ShopFlowTenantToken', 'e2e-tenant')
    window.localStorage.setItem('nickName', 'E2E 用户')
    window.localStorage.setItem('avatar', 'avatar.png')
  })

  await page.route('**/wx/home/index**', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        errno: 0,
        data: {
          banner: [{ url: 'banner-1.png' }],
          channel: [{ id: 101, name: '运动鞋' }],
          couponList: [{ id: 1, discount: 30, name: '新人券', desc: '满 199 可用', tag: '新人', days: 7 }],
          grouponList: [],
          brandList: [],
          newGoodsList: [],
          hotGoodsList: [],
          topicList: [],
        },
      }),
    })
  })

  await page.route('**/wx/order/detail**', async (route) => {
    const url = new URL(route.request().url())
    const orderId = url.searchParams.get('orderId')
    const isPaymentOrder = orderId === '801'
    const isCommentOrder = orderId === '702'
    const isRefundOrder = orderId === '703'
    const isAftersaleOrder = orderId === '701'

    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        errno: 0,
        data: {
          orderInfo: {
            id: isPaymentOrder ? '801' : isCommentOrder ? '702' : isRefundOrder ? '703' : '701',
            orderSn: isPaymentOrder ? 'SO20260510002' : isCommentOrder ? 'SO20260510003' : isRefundOrder ? 'SO20260510004' : 'SO20260510001',
            consignee: '张三',
            mobile: '13800000000',
            address: '上海市浦东新区世纪大道 1 号',
            goodsPrice: 88,
            freightPrice: 0,
            actualPrice: 88,
            orderStatusText: isPaymentOrder ? '待发货' : isCommentOrder ? '待评价' : isRefundOrder ? '待发货' : '待售后',
            addTime: '2026-05-10 10:00:00',
            handleOption: {
              cancel: false,
              pay: false,
              refund: isRefundOrder,
              aftersale: isAftersaleOrder,
              confirm: isAftersaleOrder,
              delete: false,
              comment: isCommentOrder,
            },
          },
          orderGoods: [
            {
              id: isCommentOrder ? 11 : 1,
              goodsName: '轻软跑鞋',
              number: 1,
              price: 88,
              picUrl: 'shoe.png',
              specifications: ['白色', '42'],
              comment: 0,
            },
          ],
        },
      }),
    })
  })

  await page.route('**/wx/order/list**', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        errno: 0,
        data: {
          list: [
            {
              id: 702,
              orderSn: 'SO20260510003',
              actualPrice: 88,
              freightPrice: 0,
              orderStatusText: '待评价',
              goodsList: [
                {
                  id: 11,
                  goodsName: '轻软跑鞋',
                  number: 1,
                  picUrl: 'shoe.png',
                  specifications: ['白色', '42'],
                  comment: 0,
                },
              ],
              handleOption: {
                cancel: false,
                pay: false,
                refund: false,
                confirm: false,
                delete: false,
                comment: true,
              },
            },
          ],
          page: 1,
          pages: 1,
        },
      }),
    })
  })

  await page.route('**/wx/order/goods**', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        errno: 0,
        data: {
          id: 1,
          goodsId: 101,
          goodsName: '轻软跑鞋',
          number: 1,
          price: 88,
          picUrl: 'shoe.png',
          specifications: ['白色', '42'],
        },
      }),
    })
  })

  await page.route('**/wx/order/comment', async (route) => {
    submittedOrderCommentPayload = route.request().postDataJSON() as Record<string, unknown>

    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        errno: 0,
        data: null,
      }),
    })
  })

  await page.route('**/wx/goods/detail**', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        errno: 0,
        data: {
          info: {
            id: 101,
            name: '轻软跑鞋',
            brief: '轻便透气',
            detail: '<p>detail</p>',
            picUrl: 'shoe.png',
            retailPrice: 88,
            counterPrice: 108,
            gallery: ['shoe.png'],
          },
          attribute: [],
          specificationList: [],
          productList: [],
          userHasCollect: 0,
          comment: {
            allCount: 12,
            hasPicCount: 5,
          },
        },
      }),
    })
  })

  await page.route('**/wx/goods/comment/count**', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        errno: 0,
        data: {
          allCount: 12,
          hasPicCount: 5,
        },
      }),
    })
  })

  await page.route('**/wx/goods/comment/list**', async (route) => {
    const url = new URL(route.request().url())
    const hasPicture = url.searchParams.get('hasPicture') === 'true'

    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        errno: 0,
        data: {
          list: hasPicture
            ? []
            : [
                {
                  id: 901,
                  nickName: 'E2E 用户',
                  avatarUrl: 'avatar.png',
                  addTime: '2026-05-10 10:00:00',
                  content: '鞋底很软，通勤舒服',
                  star: 5,
                  picUrls: ['https://cdn.shopflow.test/comment-1.png'],
                  adminContent: '感谢支持',
                },
              ],
          page: 1,
          pages: 1,
        },
      }),
    })
  })

  await page.route('**/wx/aftersale/list**', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        errno: 0,
        data: {
          list: [
            {
              statusText: '已申请',
              aftersale: {
                id: '9001',
                orderId: '701',
                aftersaleSn: 'AS20260510001',
                status: 1,
                type: 1,
                reason: '尺码不合适',
                amount: 88,
                comment: '请尽快处理',
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
      }),
    })
  })

  await page.route('**/wx/aftersale/detail**', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
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
            aftersaleSn: 'AS20260510001',
            status: 1,
            statusText: '已申请',
            type: 1,
            reason: '尺码不合适',
            amount: 88,
            comment: '请尽快处理',
            addTime: '2026-05-10 10:00:00',
            pictures: ['https://cdn.shopflow.test/proof-1.png'],
          },
          orderGoods: {
            goodsName: '轻软跑鞋',
            number: 1,
            price: 88,
            picUrl: 'shoe.png',
            specifications: ['白色', '42'],
          },
        },
      }),
    })
  })

  await page.route('**/wx/aftersale/cancel', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        errno: 0,
        data: null,
      }),
    })
  })

  await page.route('**/wx/aftersale/submit', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        errno: 0,
        data: null,
      }),
    })
  })

  await page.route('**/wx/storage/upload', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        errno: 0,
        data: {
          url: 'https://cdn.shopflow.test/proof-1.png',
        },
      }),
    })
  })

  await page.route('**/wx/cart/index**', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        errno: 0,
        data: {
          cartList: [
            {
              id: 11,
              goodsId: 101,
              productId: 1001,
              goodsName: '轻软跑鞋',
              picUrl: 'shoe.png',
              price: 88,
              number: 1,
              checked: true,
              specifications: ['白色', '42'],
            },
          ],
        },
      }),
    })
  })

  await page.route('**/wx/cart/checked', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ errno: 0, data: null }),
    })
  })

  await page.route('**/wx/cart/update', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ errno: 0, data: null }),
    })
  })

  await page.route('**/wx/cart/delete', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ errno: 0, data: null }),
    })
  })

  await page.route('**/wx/cart/checkout**', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        errno: 0,
        data: {
          checkedGoodsList: [
            {
              id: 101,
              goodsName: '轻软跑鞋',
              number: 1,
              price: 88,
              picUrl: 'shoe.png',
              specifications: ['白色', '42'],
            },
          ],
          checkedAddress: {
            id: 9,
            name: '张三',
            tel: '13800000000',
            addressDetail: '上海市浦东新区世纪大道 1 号',
          },
          availableCouponLength: 1,
          goodsTotalPrice: 88,
          freightPrice: 0,
          couponPrice: 0,
          grouponPrice: 0,
          orderTotalPrice: 88,
          actualPrice: 88,
          addressId: 9,
          cartId: 11,
          couponId: 0,
          userCouponId: 0,
        },
      }),
    })
  })

  await page.route('**/wx/coupon/selectlist**', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        errno: 0,
        data: {
          list: [
            {
              id: 1,
              cid: 1,
              name: '新人券',
              min: 199,
              discount: 20,
              desc: '满 199 可用',
              tag: '新人',
              startTime: '2026-05-10 00:00:00',
              endTime: '2026-05-17 00:00:00',
              available: true,
            },
          ],
        },
      }),
    })
  })

  await page.route('**/wx/order/submit', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        errno: 0,
        data: {
          isPay: false,
          orderIds: ['801'],
        },
      }),
    })
  })

  await page.route('**/wx/order/h5pay', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        errno: 0,
        data: {
          mwebUrl: '',
        },
      }),
    })
  })
})

test('renders real home modules with mocked payload', async ({ page }) => {
  await page.goto('/#/')

  await expect(page.getByText('优惠券')).toBeVisible()
  await expect(page.getByText('新人券')).toBeVisible()
  await expect(page.getByText('运动鞋')).toBeVisible()
})

test('navigates from order detail to aftersale apply and submits request', async ({ page }) => {
  await page.goto('/#/pages/order/detail/index?orderId=701')

  await expect(page.getByRole('main').getByRole('heading', { name: '订单详情' })).toBeVisible()
  await page.getByRole('button', { name: '申请售后' }).click()

  await expect(page.getByRole('main').getByRole('heading', { name: '申请售后' })).toBeVisible()
  await page.getByText('退货退款').click()
  await page.locator('input:not([type="file"])').nth(0).fill('尺码不合适')
  await page.locator('input:not([type="file"])').nth(1).fill('88')
  await page.locator('textarea').first().fill('鞋码偏小')
  await page.setInputFiles('input[type="file"]', {
    name: 'proof.png',
    mimeType: 'image/png',
    buffer: Buffer.from('proof'),
  })

  await expect(page.getByText('已上传凭证')).toBeVisible()
  await page.getByRole('button', { name: '提交售后申请' }).click()

  await expect(page.getByRole('main').getByRole('heading', { name: '退款 / 售后' })).toBeVisible()
})

test('blocks aftersale submit when non-undelivered type has no proof image', async ({ page }) => {
  await page.goto('/#/pages/user/refund-apply/index?orderId=701')

  await expect(page.getByRole('main').getByRole('heading', { name: '申请售后' })).toBeVisible()
  await page.getByText('退货退款').click()
  await page.locator('input:not([type="file"])').nth(0).fill('尺码不合适')
  await page.locator('textarea').first().fill('鞋码偏小')
  await page.getByRole('button', { name: '提交售后申请' }).click()

  await expect(page.getByText('请上传凭证')).toBeVisible()
})

test('renders refund and aftersale actions with backend semantics', async ({ page }) => {
  await page.goto('/#/pages/order/detail/index?orderId=703')

  await expect(page.getByRole('button', { name: '申请退款' })).toBeVisible()
  await expect(page.getByRole('button', { name: '申请售后' })).toHaveCount(0)

  await page.goto('/#/pages/order/detail/index?orderId=701')

  await expect(page.getByRole('button', { name: '申请售后' })).toBeVisible()
  await expect(page.getByRole('button', { name: '申请退款' })).toHaveCount(0)
})

test('opens order comment page directly and submits review', async ({ page }) => {
  await page.goto('/#/pages/order/comment-post/index?goodsId=101')

  await expect(page.getByRole('main').getByRole('heading', { name: '订单评价' })).toBeVisible()
  await page.getByRole('textbox').fill('穿着舒服')
  await page.getByRole('button', { name: '发表评价' }).click()

  await expect(page.getByText('评价成功')).toBeVisible()
  expect(submittedOrderCommentPayload).toEqual({
    goodsId: 101,
    content: '穿着舒服',
    star: 5,
    hasPicture: false,
    picUrls: [],
  })
})

test('opens comment page from order detail action', async ({ page }) => {
  await page.goto('/#/pages/order/detail/index?orderId=702')

  await expect(page.getByRole('main').getByRole('heading', { name: '订单详情' })).toBeVisible()
  await page.getByRole('button', { name: '去评价' }).click()

  await expect(page.getByRole('main').getByRole('heading', { name: '订单评价' })).toBeVisible()
})

test('opens comment page from user order list', async ({ page }) => {
  await page.goto('/#/pages/user/order-list/index?active=4')

  await expect(page.getByText('我的订单')).toBeVisible()
  await page.getByRole('button', { name: '去评价' }).first().click()

  await expect(page.getByRole('main').getByRole('heading', { name: '订单评价' })).toBeVisible()
})

test('opens goods comment list from goods detail and toggles picture filter', async ({ page }) => {
  await page.goto('/#/pages/items/detail/index?id=101')

  await expect(page.getByText('查看全部评论')).toBeVisible()
  await page.getByText('查看全部评论').click()

  await expect(page.getByRole('main').getByRole('heading', { name: '商品评价' })).toBeVisible()
  await page.getByText(/有图/).click()
  await expect(page.getByText('当前没有带图评价')).toBeVisible()
})

test('opens aftersale detail and cancels request inline', async ({ page }) => {
  await page.goto('/#/pages/user/refund/index')

  await expect(page.getByText('售后单 AS20260510001')).toBeVisible()
  await page.getByRole('button', { name: '查看售后' }).click()

  await expect(page.getByRole('main').getByRole('heading', { name: '售后详情' })).toBeVisible()
  await expect(page.getByText('凭证图片')).toBeVisible()
  await page.getByRole('button', { name: '撤销售后' }).click()
  await page.getByRole('button', { name: '确认' }).click()

  await expect(page.getByRole('main').getByRole('heading', { name: '退款 / 售后' })).toBeVisible()
})

test('navigates cart to checkout and reaches payment result via direct success callback', async ({ page }) => {
  await page.goto('/#/order')

  await expect(page.getByRole('main').getByRole('heading', { name: '购物车' })).toBeVisible()
  await expect(page.getByText('轻软跑鞋')).toBeVisible()
  await page.getByRole('button', { name: '去结算' }).click()

  await expect(page.getByRole('main').getByRole('heading', { name: '确认订单' })).toBeVisible()
  await page.getByRole('button', { name: '提交订单' }).click()

  await expect(page).toHaveURL(/#\/(?:order\/payment|pages\/order\/payment\/index)\?orderId=801&orderIds=801/)
  await expect(page.getByRole('main').getByRole('heading', { name: '支付订单' })).toBeVisible()

  await page.goto('/#/order/payment/success?orderId=801&orderIds=801')
  await expect(page.getByRole('main').getByRole('heading', { name: '支付成功' })).toBeVisible()
  await expect(page.getByText('订单编号：SO20260510002')).toBeVisible()
})
