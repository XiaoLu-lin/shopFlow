# Shopflow H5 Next Post-Purchase Alignment Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 补齐 `shopflow-h5-next` 的订单评价与商品评论闭环，并将售后申请规则、售后状态分组与订单页退款/售后入口语义对齐到 `shopflow-wx`。

**Architecture:** 在现有 `shopflow-h5-next` `uni-app + Vue 3 + TypeScript` 架构上增量补齐页面与 API 封装，不重写已有售后链路。评论相关能力新增到 `entities/order`、`entities/goods` 与新页面中，售后对齐则基于现有 `pages/user/refund*` 与 `entities/user` 做定向修正。

**Tech Stack:** `uni-app`, `Vue 3`, `TypeScript`, `Vitest`, `Playwright`, 现有 `shopflow-wx-api` `/wx/*` 接口

---

## File Map

**Create:**

- `shopflow-h5-next/src/pages/order/comment-post/index.vue`
- `shopflow-h5-next/src/pages/items/comment-list/index.vue`
- `shopflow-h5-next/src/pages/items/comment-utils.ts`
- `shopflow-h5-next/src/pages/items/comment-utils.test.ts`
- `shopflow-h5-next/src/pages/order/comment-utils.ts`
- `shopflow-h5-next/src/pages/order/comment-utils.test.ts`

**Modify:**

- `shopflow-h5-next/src/pages.json`
- `shopflow-h5-next/src/entities/order/api.ts`
- `shopflow-h5-next/src/entities/order/api.test.ts`
- `shopflow-h5-next/src/entities/goods/api.ts`
- `shopflow-h5-next/src/entities/goods/api.test.ts`
- `shopflow-h5-next/src/entities/user/api.ts`
- `shopflow-h5-next/src/entities/user/api.test.ts`
- `shopflow-h5-next/src/pages/order/detail/index.vue`
- `shopflow-h5-next/src/pages/user/order-list/index.vue`
- `shopflow-h5-next/src/pages/items/detail/index.vue`
- `shopflow-h5-next/src/pages/user/refund-apply/index.vue`
- `shopflow-h5-next/src/pages/user/refund/index.vue`
- `shopflow-h5-next/src/pages/user/aftersale-utils.ts`
- `shopflow-h5-next/tests/e2e/smoke.spec.ts`

**Verification Targets:**

- `shopflow-h5-next/src/entities/order/api.test.ts`
- `shopflow-h5-next/src/entities/goods/api.test.ts`
- `shopflow-h5-next/src/entities/user/api.test.ts`
- `shopflow-h5-next/src/pages/items/comment-utils.test.ts`
- `shopflow-h5-next/src/pages/order/comment-utils.test.ts`
- `shopflow-h5-next/tests/e2e/smoke.spec.ts`

## Implementation Notes

- `shopflow-wx` 的行为基线来自：
  - 订单详情评价入口：`shopflow-wx/pages/ucenter/orderDetail/orderDetail.wxml`
  - 订单评价页：`shopflow-wx/pages/comment/commentPost/commentPost.js`
  - 商品评论列表：`shopflow-wx/pages/comment/commentList/commentList.js`
  - 售后申请校验：`shopflow-wx/pages/ucenter/aftersale/aftersale.js`
- 旧 `shopflow-h5` 已经补过一版评价页和评论列表，可作为接口口径参考：
  - `shopflow-h5/src/views/order/comment-post/index.vue`
  - `shopflow-h5/src/views/items/comment-list/index.vue`
- 现有 `shopflow-h5-next` 已经具备售后 API 封装与页面骨架，优先复用：
  - `shopflow-h5-next/src/entities/user/api.ts`
  - `shopflow-h5-next/src/pages/user/refund/index.vue`
  - `shopflow-h5-next/src/pages/user/refund-detail/index.vue`
  - `shopflow-h5-next/src/pages/user/refund-apply/index.vue`
- 当前 `shopflow-h5-next` 的 `OrderDetailHandleOption` 仅显式声明了 `refund` / `comment` 等字段；实现时要先确认后端是否还会返回 `aftersale` 字段，并据此决定类型与按钮展示逻辑。

### Task 1: 补齐评论与订单操作的 API 契约

**Files:**

- Modify: `shopflow-h5-next/src/entities/order/api.ts`
- Modify: `shopflow-h5-next/src/entities/order/api.test.ts`
- Modify: `shopflow-h5-next/src/entities/goods/api.ts`
- Modify: `shopflow-h5-next/src/entities/goods/api.test.ts`

- [ ] **Step 1: 先补订单与商品评论相关的 failing tests**

在 `shopflow-h5-next/src/entities/order/api.test.ts` 中新增用例，覆盖：

```ts
test('requests order goods by legacy goodsId query and submits order comment payload', async () => {
  client.get.mockResolvedValueOnce({
    data: {
      errno: 0,
      data: {
        id: 11,
        goodsId: 101,
        goodsName: '轻软跑鞋',
        number: 1,
        price: 88,
        picUrl: 'shoe.png',
        specifications: ['白色', '42'],
      },
    },
  })
  client.post.mockResolvedValueOnce({
    data: {
      errno: 0,
      data: null,
    },
  })

  const { fetchOrderCommentGoods, submitOrderComment } = await import('./api')
  await fetchOrderCommentGoods(101)
  await submitOrderComment({
    goodsId: 101,
    content: '穿着舒服',
    star: 5,
    hasPicture: true,
    picUrls: ['https://cdn.shopflow.test/comment-1.png'],
  })

  expect(client.get).toHaveBeenLastCalledWith('/order/goods', {
    params: {
      goodsId: 101,
    },
  })
  expect(client.post).toHaveBeenLastCalledWith('/order/comment', {
    goodsId: 101,
    content: '穿着舒服',
    star: 5,
    hasPicture: true,
    picUrls: ['https://cdn.shopflow.test/comment-1.png'],
  })
})
```

在 `shopflow-h5-next/src/entities/goods/api.test.ts` 中新增用例，覆盖：

```ts
test('requests goods comment count and paged comment list with picture filter', async () => {
  client.get
    .mockResolvedValueOnce({
      data: {
        errno: 0,
        data: {
          allCount: 12,
          hasPicCount: 5,
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

  const { fetchGoodsCommentCount, fetchGoodsCommentList } = await import('./api')
  await fetchGoodsCommentCount(101)
  await fetchGoodsCommentList({
    goodsId: 101,
    page: 1,
    limit: 20,
    hasPicture: true,
  })

  expect(client.get).toHaveBeenNthCalledWith(1, '/goods/comment/count', {
    params: { goodsId: 101 },
  })
  expect(client.get).toHaveBeenNthCalledWith(2, '/goods/comment/list', {
    params: {
      goodsId: 101,
      page: 1,
      limit: 20,
      hasPicture: true,
    },
  })
})
```

- [ ] **Step 2: 运行评论 API 测试，确认当前失败**

Run:

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npx vitest run src/entities/order/api.test.ts src/entities/goods/api.test.ts
```

Expected:

- `order api` 中缺少 `fetchOrderCommentGoods` / `submitOrderComment` 导出导致 FAIL
- `goods api` 中缺少 `fetchGoodsCommentCount` / `fetchGoodsCommentList` 导出导致 FAIL

- [ ] **Step 3: 在 order/goods entities 中补最小实现**

在 `shopflow-h5-next/src/entities/order/api.ts` 中新增类型与方法：

```ts
export interface OrderCommentGoodsItem {
  id: number
  goodsId?: number
  goodsName: string
  number: number
  price: number
  picUrl: string
  specifications: string[]
  comment?: number
}

export interface OrderCommentPayload {
  goodsId: number
  content: string
  star: number
  hasPicture: boolean
  picUrls: string[]
}

export async function fetchOrderCommentGoods(goodsId: number | string) {
  const response = await getApiClient().get<ApiEnvelope<OrderCommentGoodsItem>>('/order/goods', {
    params: {
      goodsId,
    },
  })
  return response.data.data
}

export async function submitOrderComment(payload: OrderCommentPayload) {
  await getApiClient().post('/order/comment', payload)
}
```

在 `shopflow-h5-next/src/entities/goods/api.ts` 中新增评论统计与列表类型：

```ts
export interface GoodsCommentCountPayload {
  allCount: number
  hasPicCount: number
}

export interface GoodsCommentItem {
  id: number | string
  nickName?: string
  avatarUrl?: string
  addTime?: string
  content?: string
  star?: number
  picUrls?: string[]
  adminContent?: string
}

export interface GoodsCommentListPayload {
  list: GoodsCommentItem[]
  page: number
  pages: number
}

export async function fetchGoodsCommentCount(goodsId: number | string) {
  const response = await getApiClient().get<ApiEnvelope<GoodsCommentCountPayload>>('/goods/comment/count', {
    params: { goodsId },
  })
  return response.data.data
}

export async function fetchGoodsCommentList(params: {
  goodsId: number | string
  page: number
  limit: number
  hasPicture: boolean
}) {
  const response = await getApiClient().get<ApiEnvelope<GoodsCommentListPayload>>('/goods/comment/list', {
    params,
  })
  return response.data.data
}
```

- [ ] **Step 4: 重新运行评论 API 测试，确认通过**

Run:

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npx vitest run src/entities/order/api.test.ts src/entities/goods/api.test.ts
```

Expected:

- PASS

- [ ] **Step 5: 提交一个小 checkpoint**

```bash
git add shopflow-h5-next/src/entities/order/api.ts shopflow-h5-next/src/entities/order/api.test.ts shopflow-h5-next/src/entities/goods/api.ts shopflow-h5-next/src/entities/goods/api.test.ts
git commit -m "feat: add post-purchase comment api contracts"
```

### Task 2: 抽评论页面的纯逻辑工具并用 TDD 固定

**Files:**

- Create: `shopflow-h5-next/src/pages/order/comment-utils.ts`
- Create: `shopflow-h5-next/src/pages/order/comment-utils.test.ts`
- Create: `shopflow-h5-next/src/pages/items/comment-utils.ts`
- Create: `shopflow-h5-next/src/pages/items/comment-utils.test.ts`

- [ ] **Step 1: 先写评论页与评论列表页的 failing tests**

在 `shopflow-h5-next/src/pages/order/comment-utils.test.ts` 中新增：

```ts
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
```

在 `shopflow-h5-next/src/pages/items/comment-utils.test.ts` 中新增：

```ts
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
```

- [ ] **Step 2: 运行工具测试，确认当前失败**

Run:

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npx vitest run src/pages/order/comment-utils.test.ts src/pages/items/comment-utils.test.ts
```

Expected:

- FAIL，提示找不到对应模块文件

- [ ] **Step 3: 写最小工具实现**

在 `shopflow-h5-next/src/pages/order/comment-utils.ts` 中新增：

```ts
export function getCommentStarText(star: number) {
  if (star <= 1) return '很差'
  if (star === 2) return '不太满意'
  if (star === 3) return '满意'
  if (star === 4) return '比较满意'
  return '十分满意'
}

export function validateOrderCommentForm(input: {
  star: number
  content: string
  picUrls: string[]
}) {
  if (!input.star || input.star < 1) {
    return '请选择评分'
  }

  if (!input.content.trim()) {
    return '请填写评论'
  }

  return ''
}

export function buildOrderCommentPayload(input: {
  goodsId: number
  star: number
  content: string
  picUrls: string[]
}) {
  return {
    goodsId: input.goodsId,
    star: input.star,
    content: input.content.trim(),
    hasPicture: input.picUrls.length > 0,
    picUrls: input.picUrls,
  }
}
```

在 `shopflow-h5-next/src/pages/items/comment-utils.ts` 中新增：

```ts
export function normalizeCommentGoodsId(value: string | number | undefined) {
  if (value === undefined || value === null || value === '') {
    return ''
  }
  return String(value)
}

export function createGoodsCommentQuery(input: {
  goodsId: string
  page: number
  limit: number
  hasPicture: boolean
}) {
  return {
    goodsId: input.goodsId,
    page: input.page,
    limit: input.limit,
    hasPicture: input.hasPicture,
  }
}

export function shouldFinishCommentPaging(list: unknown[], page: number, pages: number) {
  return list.length === 0 || page >= pages
}
```

- [ ] **Step 4: 重新运行工具测试，确认通过**

Run:

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npx vitest run src/pages/order/comment-utils.test.ts src/pages/items/comment-utils.test.ts
```

Expected:

- PASS

- [ ] **Step 5: 提交一个小 checkpoint**

```bash
git add shopflow-h5-next/src/pages/order/comment-utils.ts shopflow-h5-next/src/pages/order/comment-utils.test.ts shopflow-h5-next/src/pages/items/comment-utils.ts shopflow-h5-next/src/pages/items/comment-utils.test.ts
git commit -m "test: pin post-purchase comment utils"
```

### Task 3: 新增订单评价页并接通评价提交流程

**Files:**

- Create: `shopflow-h5-next/src/pages/order/comment-post/index.vue`
- Modify: `shopflow-h5-next/src/pages.json`
- Test: `shopflow-h5-next/tests/e2e/smoke.spec.ts`

- [ ] **Step 1: 先写 e2e failing case，描述评价页最小闭环**

在 `shopflow-h5-next/tests/e2e/smoke.spec.ts` 中补 mock：

```ts
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
  await route.fulfill({
    status: 200,
    contentType: 'application/json',
    body: JSON.stringify({
      errno: 0,
      data: null,
    }),
  })
})
```

并新增用例：

```ts
test('navigates to comment page and submits review from order detail', async ({ page }) => {
  await page.goto('/#/order/order-detail?orderId=702')

  await expect(page.getByRole('main').getByRole('heading', { name: '订单详情' })).toBeVisible()
  await page.getByRole('button', { name: '去评价' }).click()

  await expect(page.getByRole('main').getByRole('heading', { name: '订单评价' })).toBeVisible()
  await page.getByPlaceholder('留言经过筛选后，对所有人可见').fill('穿着舒服')
  await page.setInputFiles('input[type="file"]', {
    name: 'comment.png',
    mimeType: 'image/png',
    buffer: Buffer.from('comment'),
  })
  await page.getByRole('button', { name: '发表评价' }).click()

  await expect(page.getByText('评价成功')).toBeVisible()
})
```

- [ ] **Step 2: 运行 e2e 或定向 smoke，确认当前失败**

Run:

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npx playwright test tests/e2e/smoke.spec.ts --grep "submits review"
```

Expected:

- FAIL，因无评价页路由或无“去评价”入口

- [ ] **Step 3: 新增评价页并注册路由**

在 `shopflow-h5-next/src/pages.json` 中追加：

```json
{
  "path": "pages/order/comment-post/index",
  "style": {
    "navigationBarTitleText": "订单评价"
  }
}
```

在 `shopflow-h5-next/src/pages/order/comment-post/index.vue` 中实现最小闭环页面，关键结构包括：

```vue
<template>
  <view class="page">
    <view class="hero-card">
      <text class="title">订单评价</text>
      <text class="desc">分享你的真实使用感受，帮助其他用户做决定。</text>
    </view>

    <view v-if="goodsInfo" class="section-list">
      <view class="section-card">
        <view class="goods-row">
          <image class="goods-image" :src="goodsInfo.picUrl" mode="aspectFill" />
          <view class="goods-body">
            <text class="goods-title">{{ goodsInfo.goodsName }}</text>
            <text class="goods-spec">{{ goodsInfo.specifications?.join(' / ') || '默认规格' }}</text>
            <text class="goods-meta">x {{ goodsInfo.number }} / ¥ {{ goodsInfo.price }}</text>
          </view>
        </view>
      </view>

      <view class="section-card">
        <text class="section-title">评分</text>
        <view class="star-row">
          <view
            v-for="value in [1, 2, 3, 4, 5]"
            :key="value"
            class="star-chip"
            :class="{ 'star-chip--active': star >= value }"
            @click="star = value"
          >
            ★
          </view>
        </view>
        <text class="hint">{{ starText }}</text>
      </view>

      <view class="section-card">
        <text class="field-label">评价内容</text>
        <textarea v-model="content" class="field-textarea" maxlength="140" placeholder="留言经过筛选后，对所有人可见" />
      </view>

      <view class="section-card">
        <view class="upload-head">
          <text class="section-title">评价图片</text>
          <view class="ghost-btn" @click="chooseCommentImage">{{ uploading ? '上传中...' : '上传图片' }}</view>
        </view>
        <view v-if="pictures.length" class="proof-grid">
          <view v-for="(picture, index) in pictures" :key="picture" class="proof-card" @click="removePicture(index)">
            <image class="proof-image" :src="picture" mode="aspectFill" />
            <text class="proof-remove">删除</text>
          </view>
        </view>
      </view>

      <view class="footer">
        <view class="primary-btn" :class="{ 'primary-btn--disabled': submitting || uploading }" @click="submit">发表评价</view>
      </view>
    </view>
  </view>
</template>
```

脚本核心逻辑：

```ts
import { computed, ref } from 'vue'
import { fetchOrderCommentGoods, submitOrderComment } from '@/entities/order/api'
import { uploadAftersaleProof } from '@/entities/user/api'
import { buildOrderCommentPayload, getCommentStarText, validateOrderCommentForm } from '../comment-utils'
```

注意：

- 若当前目录相对导入不顺，优先使用 `@/pages/order/comment-utils`
- 可先复用 `uploadAftersaleProof` 做图片上传，后续如需要再抽通用 upload helper

- [ ] **Step 4: 跑定向 e2e，确认评价页闭环通过**

Run:

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npx playwright test tests/e2e/smoke.spec.ts --grep "submits review"
```

Expected:

- PASS

- [ ] **Step 5: 提交一个小 checkpoint**

```bash
git add shopflow-h5-next/src/pages.json shopflow-h5-next/src/pages/order/comment-post/index.vue shopflow-h5-next/tests/e2e/smoke.spec.ts
git commit -m "feat: add order comment post page"
```

### Task 4: 补订单列表与订单详情的“去评价”入口

**Files:**

- Modify: `shopflow-h5-next/src/pages/order/detail/index.vue`
- Modify: `shopflow-h5-next/src/pages/user/order-list/index.vue`
- Modify: `shopflow-h5-next/src/entities/order/api.ts`
- Test: `shopflow-h5-next/tests/e2e/smoke.spec.ts`

- [ ] **Step 1: 为入口行为补 failing e2e 场景**

在 `shopflow-h5-next/tests/e2e/smoke.spec.ts` 调整 `order/detail` mock，针对特定订单返回可评论态：

```ts
const isCommentOrder = orderId === '702'

handleOption: {
  cancel: false,
  pay: false,
  refund: !isPaymentOrder && !isCommentOrder,
  confirm: false,
  delete: false,
  comment: isCommentOrder,
}
```

并为 `order/list` mock 增加可评论订单数据，新增用例：

```ts
test('opens comment page from user order list', async ({ page }) => {
  await page.goto('/#/user/order/list?active=4')

  await expect(page.getByRole('main').getByRole('heading', { name: '我的订单' })).toBeVisible()
  await page.getByRole('button', { name: '去评价' }).first().click()
  await expect(page.getByRole('main').getByRole('heading', { name: '订单评价' })).toBeVisible()
})
```

- [ ] **Step 2: 运行 smoke，确认当前入口仍失败**

Run:

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npx playwright test tests/e2e/smoke.spec.ts --grep "comment page"
```

Expected:

- FAIL，订单列表或订单详情没有“去评价”按钮

- [ ] **Step 3: 在订单页补评价入口与导航**

在 `shopflow-h5-next/src/pages/order/detail/index.vue` 中：

- 保留现有售后与支付按钮
- 在 `orderGoods` 中找到第一个可评价商品 id
- 当 `orderInfo.handleOption.comment` 为真时展示“去评价”

建议新增：

```ts
const commentGoodsId = computed(() => {
  const firstGoods = orderGoods.value[0]
  if (!firstGoods) {
    return 0
  }
  return Number(firstGoods.id || 0)
})

function goCommentPost(goodsId: number) {
  if (!goodsId) return
  uni.navigateTo({
    url: `/pages/order/comment-post/index?goodsId=${goodsId}`,
  })
}
```

按钮可加在 action row：

```vue
<view v-if="orderInfo.handleOption.comment && commentGoodsId" class="ghost-btn" @click="goCommentPost(commentGoodsId)">去评价</view>
```

在 `shopflow-h5-next/src/pages/user/order-list/index.vue` 中：

- 每个订单基于 `order.handleOption.comment` 与订单内首个商品 id 展示“去评价”
- 避免和取消/确认/支付按钮互相覆盖

- [ ] **Step 4: 跑定向 smoke，确认两个入口都通**

Run:

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npx playwright test tests/e2e/smoke.spec.ts --grep "comment page|去评价"
```

Expected:

- PASS

- [ ] **Step 5: 提交一个小 checkpoint**

```bash
git add shopflow-h5-next/src/pages/order/detail/index.vue shopflow-h5-next/src/pages/user/order-list/index.vue shopflow-h5-next/tests/e2e/smoke.spec.ts
git commit -m "feat: connect post-purchase comment entry points"
```

### Task 5: 新增商品评论列表页并在商品详情补评论入口

**Files:**

- Create: `shopflow-h5-next/src/pages/items/comment-list/index.vue`
- Modify: `shopflow-h5-next/src/pages/items/detail/index.vue`
- Modify: `shopflow-h5-next/src/pages.json`
- Test: `shopflow-h5-next/tests/e2e/smoke.spec.ts`

- [ ] **Step 1: 为商品评论入口和评论列表页补 failing e2e**

在 `shopflow-h5-next/tests/e2e/smoke.spec.ts` 中补：

```ts
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
```

再补 `goods/comment/count` 与 `goods/comment/list` mock，并新增用例：

```ts
test('opens goods comment list from goods detail and toggles picture filter', async ({ page }) => {
  await page.goto('/#/items/detail?id=101')

  await expect(page.getByText('查看全部评论')).toBeVisible()
  await page.getByText('查看全部评论').click()

  await expect(page.getByRole('main').getByRole('heading', { name: '商品评价' })).toBeVisible()
  await page.getByText(/有图/).click()
  await expect(page.getByText('当前没有带图评价')).toBeVisible()
})
```

- [ ] **Step 2: 跑定向 smoke，确认当前失败**

Run:

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npx playwright test tests/e2e/smoke.spec.ts --grep "comment list"
```

Expected:

- FAIL，商品详情中还没有评论入口

- [ ] **Step 3: 实现评论列表页与商品详情轻量评论摘要**

在 `shopflow-h5-next/src/pages.json` 中新增：

```json
{
  "path": "pages/items/comment-list/index",
  "style": {
    "navigationBarTitleText": "商品评价"
  }
}
```

在 `shopflow-h5-next/src/pages/items/comment-list/index.vue` 中实现：

- 顶部统计信息
- 全部 / 有图切换
- 评论内容、评分、图片、规格展示
- 空状态与分页

在 `shopflow-h5-next/src/pages/items/detail/index.vue` 中：

- 新增评论摘要卡片
- 文案类似：

```vue
<view class="panel" @click="goCommentList">
  <view class="comment-head">
    <text class="panel-title">商品评价</text>
    <text class="panel-link">查看全部评论</text>
  </view>
  <text class="comment-copy">共 {{ commentCount }} 条评价，{{ hasPicCount }} 条带图</text>
</view>
```

脚本层补：

```ts
const commentCount = computed(() => detail.value?.comment?.allCount || 0)
const hasPicCount = computed(() => detail.value?.comment?.hasPicCount || 0)

function goCommentList() {
  if (!id || typeof id !== 'string') return
  uni.navigateTo({
    url: `/pages/items/comment-list/index?goodsId=${id}`,
  })
}
```

若 `GoodsDetailPayload` 当前不含 `comment` 字段，则在 `entities/goods/api.ts` 中同步补类型：

```ts
comment?: {
  allCount: number
  hasPicCount: number
}
```

- [ ] **Step 4: 运行商品评论 smoke，确认通过**

Run:

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npx playwright test tests/e2e/smoke.spec.ts --grep "comment list"
```

Expected:

- PASS

- [ ] **Step 5: 提交一个小 checkpoint**

```bash
git add shopflow-h5-next/src/pages.json shopflow-h5-next/src/pages/items/detail/index.vue shopflow-h5-next/src/pages/items/comment-list/index.vue shopflow-h5-next/tests/e2e/smoke.spec.ts shopflow-h5-next/src/entities/goods/api.ts
git commit -m "feat: add goods comment browsing flow"
```

### Task 6: 对齐售后申请校验与售后列表状态语义

**Files:**

- Modify: `shopflow-h5-next/src/pages/user/refund-apply/index.vue`
- Modify: `shopflow-h5-next/src/pages/user/refund/index.vue`
- Modify: `shopflow-h5-next/src/pages/user/aftersale-utils.ts`
- Modify: `shopflow-h5-next/src/entities/user/api.test.ts`
- Modify: `shopflow-h5-next/tests/e2e/smoke.spec.ts`

- [ ] **Step 1: 先补售后规则对齐的 failing 测试**

在 `shopflow-h5-next/tests/e2e/smoke.spec.ts` 中新增或修改售后用例，覆盖：

```ts
test('blocks aftersale submit when non-undelivered type has no proof image', async ({ page }) => {
  await page.goto('/#/user/refund-apply?orderId=701')

  await expect(page.getByRole('main').getByRole('heading', { name: '申请售后' })).toBeVisible()
  await page.getByText('退货退款').click()
  await page.getByPlaceholder('请填写退款原因').fill('尺码不合适')
  await page.getByPlaceholder('可补充描述问题，便于售后处理').fill('鞋码偏小')
  await page.getByRole('button', { name: '提交售后申请' }).click()

  await expect(page.getByText('请上传凭证')).toBeVisible()
})
```

同时在 `shopflow-h5-next/src/entities/user/api.test.ts` 中补 aftersale list 状态分组相关断言，确保 `status` 字段不被改坏。

- [ ] **Step 2: 运行定向验证，确认当前失败**

Run:

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npx playwright test tests/e2e/smoke.spec.ts --grep "aftersale submit"
```

Expected:

- FAIL，因为当前 `comment` 和凭证校验不完整

- [ ] **Step 3: 修正售后申请页校验与 tab 语义**

在 `shopflow-h5-next/src/pages/user/refund-apply/index.vue` 中：

- 去掉默认 `type = 1`，改成未选择态
- 提交前新增：

```ts
if (type.value === null) {
  toast('请选择退款类型')
  return
}

if (!reason.value.trim()) {
  toast('请输入退款原因')
  return
}

if (!comment.value.trim()) {
  toast('请输入退款说明')
  return
}

if (type.value !== 0 && pictures.value.length === 0) {
  toast('请上传凭证')
  return
}
```

在 `shopflow-h5-next/src/pages/user/aftersale-utils.ts` 中将 tab 改为与 `shopflow-wx` 用户认知一致，例如：

```ts
export const AFTERSALE_TABS = [
  { label: '申请中', status: 1 },
  { label: '处理中', status: 2 },
  { label: '已完成', status: 3 },
  { label: '已拒绝', status: 4 },
] as const
```

如需保留“全部”视图，先与现有 UX 再核一次；若不保留，记得同步修正 `normalizeListTab` 使用方式与跳转参数。

在 `shopflow-h5-next/src/pages/user/refund/index.vue` 中：

- 对齐默认 tab
- 修正空状态、状态样式与跳转参数

- [ ] **Step 4: 跑售后定向测试与全量 user api 测试**

Run:

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npx vitest run src/entities/user/api.test.ts
npx playwright test tests/e2e/smoke.spec.ts --grep "aftersale"
```

Expected:

- PASS

- [ ] **Step 5: 提交一个小 checkpoint**

```bash
git add shopflow-h5-next/src/pages/user/refund-apply/index.vue shopflow-h5-next/src/pages/user/refund/index.vue shopflow-h5-next/src/pages/user/aftersale-utils.ts shopflow-h5-next/tests/e2e/smoke.spec.ts shopflow-h5-next/src/entities/user/api.test.ts
git commit -m "feat: align aftersale rules with wx behavior"
```

### Task 7: 全量验证、构建、OpenSpec 回填与本地启动

**Files:**

- Modify: `openspec/changes/align-shopflow-h5-next-post-purchase-with-wx/tasks.md`
- Create: `openspec/changes/align-shopflow-h5-next-post-purchase-with-wx/review.md`

- [ ] **Step 1: 运行单测与 e2e 冒烟**

Run:

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npx vitest run
npx playwright test tests/e2e/smoke.spec.ts
```

Expected:

- 全部 PASS

- [ ] **Step 2: 运行 H5 构建验证**

Run:

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run build:h5
```

Expected:

- build 成功

- [ ] **Step 3: 更新 OpenSpec tasks 与 review**

在 `openspec/changes/align-shopflow-h5-next-post-purchase-with-wx/tasks.md`：

- 把已完成任务勾为 `[x]`
- 若中途对 scope 做了微调，补一句理由

新建 `openspec/changes/align-shopflow-h5-next-post-purchase-with-wx/review.md`，至少包含：

```md
# Review: align-shopflow-h5-next-post-purchase-with-wx

## Findings

- 已补齐订单评价发布页、商品评论列表页、订单页评价入口
- 已对齐售后申请校验与售后状态分组

## Verification

- `npx vitest run`
- `npx playwright test tests/e2e/smoke.spec.ts`
- `npm run build:h5`

## Residual Risks

- 当前仍未纳入消息、动态、聊天等非交易后能力
- 如后端 `handleOption` 字段语义未来变化，需要同步收口类型定义
```

- [ ] **Step 4: 启动本地联调服务**

Run:

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master
java -jar shopflow-all/target/shopflow-all-0.1.0-exec.jar
```

另开一个终端：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run dev:h5
```

Expected:

- 后端监听 `6914`
- `shopflow-h5-next` dev server 监听 `6257`

- [ ] **Step 5: 手工联调关键路径并提交收尾 commit**

手工验证：

- `http://localhost:6257/#/user/order/list?active=4`
- `http://localhost:6257/#/order/order-detail?orderId=<可评价订单>`
- `http://localhost:6257/#/items/detail?id=<有评论商品>`
- `http://localhost:6257/#/user/refund/index`

收尾提交：

```bash
git add openspec/changes/align-shopflow-h5-next-post-purchase-with-wx/tasks.md openspec/changes/align-shopflow-h5-next-post-purchase-with-wx/review.md shopflow-h5-next
git commit -m "feat: align shopflow-h5-next post-purchase flow with wx"
```

## Self-Review

- Spec coverage:
  - 评价发布闭环：Task 1-4 覆盖
  - 商品评论浏览：Task 1、2、5 覆盖
  - 售后申请规则对齐：Task 6 覆盖
  - 订单页、商品页、售后页入口语义对齐：Task 4、5、6 覆盖
- Placeholder scan:
  - 无 `TODO` / `TBD`
  - 每个验证步骤都给了具体命令
- Type consistency:
  - 评论相关 payload 统一落在 `entities/order` 与 `entities/goods`
  - 售后状态调整集中在 `aftersale-utils.ts` 与对应页面消费
