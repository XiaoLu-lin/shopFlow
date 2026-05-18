import { expect, test } from '@playwright/test'

test.beforeEach(async ({ page }) => {
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

    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        errno: 0,
        data: {
          orderInfo: {
            id: isPaymentOrder ? '801' : '701',
            orderSn: isPaymentOrder ? 'SO20260510002' : 'SO20260510001',
            consignee: '张三',
            mobile: '13800000000',
            address: '上海市浦东新区世纪大道 1 号',
            goodsPrice: 88,
            freightPrice: 0,
            actualPrice: 88,
            orderStatusText: isPaymentOrder ? '待发货' : '待售后',
            addTime: '2026-05-10 10:00:00',
            handleOption: {
              cancel: false,
              pay: false,
              refund: !isPaymentOrder,
              confirm: false,
              delete: false,
              comment: false,
            },
          },
          orderGoods: [
            {
              id: 1,
              goodsName: '轻软跑鞋',
              number: 1,
              price: 88,
              picUrl: 'shoe.png',
              specifications: ['白色', '42'],
            },
          ],
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

  await expect(page.getByText('ShopFlow')).toBeVisible()
  await expect(page.getByText('新人券')).toBeVisible()
  await expect(page.getByText('运动鞋')).toBeVisible()
})

test('navigates from order detail to aftersale apply and submits request', async ({ page }) => {
  await page.goto('/#/order/order-detail?orderId=701')

  await expect(page.locator('.summary-card .title')).toHaveText('待售后')
  await expect(page.getByText('订单编号 SO20260510001')).toBeVisible()
  await page.getByText('申请售后').click()

  await expect(page.getByText('申请售后')).toBeVisible()
  await page.getByRole('textbox').first().fill('尺码不合适')
  await page.getByRole('spinbutton').fill('88')
  await page.getByRole('textbox').nth(1).fill('鞋码偏小')
  await page.getByText('提交售后申请').click()

  await expect(page.getByText('退款 / 售后')).toBeVisible()
})

test('opens aftersale detail and cancels request inline', async ({ page }) => {
  await page.goto('/#/user/refund/list')

  await expect(page.getByText('售后单 AS20260510001')).toBeVisible()
  await page.getByText('查看售后').click()

  await expect(page.getByText('售后详情')).toBeVisible()
  await expect(page.getByText('凭证图片')).toBeVisible()
  await page.getByText('撤销售后').click()

  await expect(page.getByText('退款 / 售后')).toBeVisible()
})

test('navigates cart to checkout and reaches payment result via direct success callback', async ({ page }) => {
  await page.goto('/#/order')

  await expect(page.locator('.toolbar-title')).toHaveText('共 1 件商品')
  await expect(page.getByText('轻软跑鞋')).toBeVisible()
  await page.locator('.footer-bar .submit-btn').dispatchEvent('click')

  await expect(page.getByText('ShopFlow Checkout')).toBeVisible()
  await page.locator('.footer-bar .submit-btn').dispatchEvent('click')

  await expect(page).toHaveURL(/#\/pages\/order\/payment\/index\?orderId=801&orderIds=801/)
  await expect(page.getByText('ShopFlow Pay')).toBeVisible()

  await page.goto('/#/pages/order/payment-status/index?status=success&orderId=801&orderIds=801')
  await expect(page.getByText('支付成功')).toBeVisible()
  await expect(page.getByText('订单编号：SO20260510002')).toBeVisible()
})

test('renders user dashboard and navigates to order list through order status entry', async ({ page }) => {
  await page.route('**/wx/user/index**', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        errno: 0,
        data: {
          order: {
            unpaid: 2,
            unship: 1,
            unrecv: 0,
            uncomment: 5,
          },
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
              id: 701,
              orderSn: 'SO20260510001',
              actualPrice: 88,
              freightPrice: 0,
              orderStatusText: '待发货',
              goodsList: [
                {
                  goodsName: '轻软跑鞋',
                  number: 1,
                  picUrl: 'shoe.png',
                  specifications: ['白色', '42'],
                },
              ],
              handleOption: {
                cancel: false,
                pay: true,
                refund: false,
                confirm: false,
                delete: false,
                comment: false,
              },
            },
          ],
          page: 1,
          pages: 1,
        },
      }),
    })
  })

  await page.goto('/#/user')

  await expect(page.getByText('E2E 用户')).toBeVisible()
  await expect(page.getByText('待付款')).toBeVisible()
  await expect(page.getByText('常用服务', { exact: true })).toBeVisible()
  await page.getByText('全部订单', { exact: true }).click()

  await expect(page).toHaveURL(/#\/pages\/user\/order-list\/index\?active=0/)
  await expect(page.getByText('ShopFlow Orders')).toBeVisible()
  await expect(page.getByText('SO20260510001')).toBeVisible()
})

test('keeps user dashboard scrolling isolated from header and tabbar', async ({ page }) => {
  await page.route('**/wx/user/index**', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        errno: 0,
        data: {
          order: {
            unpaid: 2,
            unship: 1,
            unrecv: 0,
            uncomment: 5,
          },
        },
      }),
    })
  })

  await page.goto('/#/user')

  const metrics = await page.locator('.page').evaluate((node) => {
    const pageRect = node.getBoundingClientRect()
    const scroll = node.querySelector('.page-scroll')

    if (!scroll) {
      return null
    }

    const scrollRect = scroll.getBoundingClientRect()

    return {
      pageHeight: pageRect.height,
      pageBottom: pageRect.bottom,
      scrollTop: scrollRect.top,
      scrollBottom: scrollRect.bottom,
      bodyScrollable: document.documentElement.scrollHeight > document.documentElement.clientHeight + 2,
      scrollMatchesPage: Math.abs(scrollRect.height - pageRect.height) < 2,
    }
  })

  expect(metrics).not.toBeNull()
  expect(metrics?.bodyScrollable).toBe(false)
  expect(metrics?.scrollTop).toBeGreaterThanOrEqual(0)
  expect(metrics?.scrollBottom).toBeLessThanOrEqual((metrics?.pageBottom ?? 0) + 1)
  expect(metrics?.scrollMatchesPage).toBe(true)
})
