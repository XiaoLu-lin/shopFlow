# ShopFlow H5 Next 商城风格实施计划

> **给执行 agent 的要求：** 实施本计划时必须使用 `superpowers:subagent-driven-development`（推荐）或 `superpowers:executing-plans`，按任务逐项执行。步骤使用 checkbox（`- [ ]`）语法跟踪。

**目标：** 将 `shopflow-h5-next` 的首页、分类页和商品详情页刷新为已确认的 ShopFlow 灰蓝商城风格。

**架构：** 先更新共享 UI token，再基于现有 API 流程重构三个页面。只有在展示逻辑需要稳定测试时才抽取小型纯函数，不新增依赖，不改变后端契约。

**技术栈：** uni-app、Vue 3、TypeScript、SCSS、Vite、Vitest、现有 ShopFlow wx API client。

---

## 文件结构

- 修改 `shopflow-h5-next/src/shared/ui/tokens.css`：共享颜色、圆角、阴影、间距和商城视觉 token。
- 修改 `shopflow-h5-next/src/shared/ui/base.css`：将 Vant 和全局变量映射到刷新后的 token。
- 修改 `shopflow-h5-next/src/pages/home/index.vue`：第一阶段商城首页布局和数据渲染。
- 可选新增 `shopflow-h5-next/src/pages/home/home-display-utils.ts`：首页商品流和区块显隐纯函数。
- 可选新增 `shopflow-h5-next/src/pages/home/home-display-utils.test.ts`：首页展示 helper 聚焦测试。
- 修改 `shopflow-h5-next/src/pages/items/catalog/index.vue`：左右分栏分类布局。
- 修改 `shopflow-h5-next/src/pages/items/detail/index.vue`：商品详情布局和操作栏刷新。
- 可选新增 `shopflow-h5-next/src/features/goods/detail-display-utils.ts`：详情展示纯函数。
- 可选新增 `shopflow-h5-next/src/features/goods/detail-display-utils.test.ts`：详情展示 helper 聚焦测试。
- 实现后新增 `openspec/changes/refresh-h5-next-shopflow-commerce-style/review.md`。

## 任务 1：视觉基座

**文件：**

- 修改：`shopflow-h5-next/src/shared/ui/tokens.css`
- 修改：`shopflow-h5-next/src/shared/ui/base.css`

- [ ] 将共享 token 更新为已确认的 ShopFlow 商城色板。

以下值作为实现来源：

```css
:root {
  --sf-color-ink: 44 62 80;
  --sf-color-page: 248 249 251;
  --sf-color-shell: 255 255 255;
  --sf-color-mist: 240 242 245;
  --sf-color-brand: 74 111 165;
  --sf-color-brand-soft: 229 237 246;
  --sf-color-brand-deep: 51 83 122;
  --sf-color-brand-light: 109 140 184;
  --sf-color-price: 232 85 61;
  --sf-color-price-soft: 255 241 238;
  --sf-color-badge: 255 71 87;
  --sf-color-success: 39 174 96;
  --sf-color-warning: 243 156 18;
  --sf-color-line: 232 236 241;
  --sf-color-divider: 240 242 245;
  --sf-color-text-secondary: 123 139 163;
  --sf-color-text-hint: 176 190 197;
  --sf-radius-card: 8px;
  --sf-radius-panel: 8px;
  --sf-radius-image: 8px;
  --sf-radius-chip: 999px;
  --sf-shadow-card: 0 1px 2px rgba(15, 23, 42, 0.04);
  --sf-shadow-soft: 0 2px 8px rgba(15, 23, 42, 0.06);
}
```

- [ ] 更新 `base.css`，让 body、Vant 主色、Vant danger 色、边框色和 tabbar 色映射到刷新后的变量。

- [ ] 编辑前扫描三个目标页面中的旧视觉值：

```bash
rg -n "#1677ff|#172033|#f6f8fb|12rpx|22rpx|24rpx" shopflow-h5-next/src/pages/home shopflow-h5-next/src/pages/items/catalog shopflow-h5-next/src/pages/items/detail shopflow-h5-next/src/shared/ui
```

预期：重构前存在匹配；完成页面任务后匹配数量应明显减少。

## 任务 2：首页商城化

**文件：**

- 修改：`shopflow-h5-next/src/pages/home/index.vue`
- 可选新增：`shopflow-h5-next/src/pages/home/home-display-utils.ts`
- 可选新增：`shopflow-h5-next/src/pages/home/home-display-utils.test.ts`

- [ ] 如果商品流选择或区块显隐逻辑变得非平凡，先写 helper 测试。

目标行为：

```ts
expect(resolveHomeFeed('new', payload)).toEqual(payload.newGoodsList)
expect(resolveHomeFeed('hot', payload)).toEqual(payload.hotGoodsList)
expect(shouldShowCoupons(payload)).toBe(payload.couponList.length > 0)
expect(shouldShowFlashSale(payload)).toBe(payload.grouponList.length > 0)
```

- [ ] 将模板重构为以下结构：

```text
页面
  ShopFlow 品牌搜索头
  Banner 轮播
  分类宫格
  优惠券 Banner，有 couponList 时展示
  秒杀横向栏，有 grouponList 时展示
  商品流 tabs
  商品双列列表
  选中 feed 为空时展示空态
```

- [ ] 复用现有搜索、分类跳转和详情跳转函数。

- [ ] 移除面向用户的迁移期文案。

- [ ] 如果新增 helper，运行聚焦测试：

```bash
cd shopflow-h5-next && npm run test -- home-display-utils
```

预期：helper 测试通过。

## 任务 3：分类页布局

**文件：**

- 修改：`shopflow-h5-next/src/pages/items/catalog/index.vue`

- [ ] 保留现有 `catalog`、`currentCatalog`、`currentId`、`categories`、`currentCategory` 和 `currentSubCategories` 数据流。

- [ ] 将模板重构为以下结构：

```text
页面
  搜索头
  分类布局
    左侧侧栏
    右侧滚动区
      当前分类 banner/上下文
      3 列子分类宫格
      无子分类时展示空态
```

- [ ] 保留以下路由行为：

```ts
uni.navigateTo({
  url: `/pages/items/category/index?categoryId=${id}&title=${encodeURIComponent(name)}`,
})
```

- [ ] 在 scoped SCSS 中使用共享颜色和 8px 圆角。

## 任务 4：商品详情页布局

**文件：**

- 修改：`shopflow-h5-next/src/pages/items/detail/index.vue`
- 可选新增：`shopflow-h5-next/src/features/goods/detail-display-utils.ts`
- 可选新增：`shopflow-h5-next/src/features/goods/detail-display-utils.test.ts`

- [ ] 如果折扣或详情内容判断逻辑变得非平凡，先写 helper 测试。

目标行为：

```ts
expect(formatDiscountLabel(149, 299)).toBe('5折')
expect(formatDiscountLabel(149, undefined)).toBe('')
expect(hasRenderableDetail('<p>content</p>')).toBe(true)
expect(hasRenderableDetail('')).toBe(false)
```

- [ ] 保留现有 `addToCart`、`buyNow`、`toggleCollectState`、`selectSku` 和 `changeQuantity` 行为。

- [ ] 将模板重构为以下结构：

```text
页面
  详情头部
  商品图轮播
  商品摘要卡片
  促销/服务卡片
  SKU/规格卡片
  属性/详情卡片
  数量卡片
  固定底部操作栏
```

- [ ] 缺失的设计稿专用数据必须隐藏或使用保守文案。

- [ ] 如果新增 helper，运行聚焦测试：

```bash
cd shopflow-h5-next && npm run test -- detail-display-utils
```

预期：helper 测试通过。

## 任务 5：验证与 review

**文件：**

- 新增：`openspec/changes/refresh-h5-next-shopflow-commerce-style/review.md`

- [ ] 运行类型检查：

```bash
cd shopflow-h5-next && npm run type-check
```

预期：命令退出码为 0。

- [ ] 运行测试：

```bash
cd shopflow-h5-next && npm run test
```

预期：命令退出码为 0。

- [ ] 运行 H5 构建：

```bash
cd shopflow-h5-next && npm run build:h5
```

预期：命令退出码为 0，并产出 H5 构建结果。

- [ ] 启动或复用本地 H5 dev server，视觉检查：

```bash
cd shopflow-h5-next && npm run dev:h5
```

预期：首页、分类页、商品详情页在移动视口下渲染且不白屏。

- [ ] 写入 `review.md`：

```markdown
# Review: refresh-h5-next-shopflow-commerce-style

## 检查范围

- 首页
- 分类页
- 商品详情页
- 共享 UI token

## 发现

- 记录实现后发现的行为回归、视觉缺口或测试缺口。

## 验证证据

- `npm run type-check`：结果
- `npm run test`：结果
- `npm run build:h5`：结果
- H5 视觉冒烟检查：结果

## 残余风险

- 购物车、个人中心、结算、订单等页面不在第一阶段范围内。
```

## 自查清单

- [ ] 每条 OpenSpec 需求都能映射到上面的至少一个任务。
- [ ] 计划保留现有 API 和路由契约。
- [ ] 计划不引入新依赖。
- [ ] 计划包含类型检查、测试、构建和视觉冒烟验证命令。

