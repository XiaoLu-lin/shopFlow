# ShopFlow H5 Next 第二阶段交易链路与个人中心风格 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 `shopflow-h5-next` 的购物车、结算、订单、支付和个人中心页面统一为第二阶段 ShopFlow 灰蓝商城风格，同时保持现有交易行为不回退。

**Architecture:** 继续复用第一阶段共享 token 与现有 API/路由契约。实现时按页面分组改造，必要时只抽取少量展示 helper 或共享块，不新增依赖、不改后端协议，优先用测试守住订单/支付/入口行为。

**Tech Stack:** uni-app、Vue 3、TypeScript、SCSS、Vite、Vitest、现有 ShopFlow wx API client。

---

## 文件结构

- 修改 `shopflow-h5-next/src/pages/order/cart/index.vue`：统一购物车布局、底部结算栏和编辑态样式。
- 可选修改 `shopflow-h5-next/src/features/cart/cart-utils.ts`：如需要补视图摘要 helper。
- 可选新增 `shopflow-h5-next/src/features/cart/cart-display-utils.test.ts`：若新增 helper，则补聚焦测试。
- 修改 `shopflow-h5-next/src/pages/order/checkout/index.vue`：统一结算页结构、地址/商品/金额层级和提交区。
- 修改 `shopflow-h5-next/src/pages/user/order-list/index.vue`：统一订单列表 tabs、订单卡与操作按钮。
- 修改 `shopflow-h5-next/src/pages/order/detail/index.vue`：统一订单详情状态卡、信息卡和操作区。
- 修改 `shopflow-h5-next/src/pages/order/payment/index.vue`：统一支付摘要和支付方式区。
- 修改 `shopflow-h5-next/src/pages/order/payment-status/index.vue`：统一支付结果页状态展示。
- 修改 `shopflow-h5-next/src/pages/user/index.vue`：统一个人中心头部、订单入口和快捷服务区。
- 视需要修改 `shopflow-h5-next/src/pages/user/entry-utils.ts` 与 `entry-utils.test.ts`：同步调整个人中心入口行为与文案。
- 视需要修改 `shopflow-h5-next/src/pages/user/user-list-utils.ts` 与 `user-list-utils.test.ts`：同步订单状态视觉映射。
- 新增 `openspec/changes/extend-h5-next-shopflow-commerce-style-to-order-user-pages/review.md`。

## 任务 1：购物车与结算页

**文件：**

- 修改：`shopflow-h5-next/src/pages/order/cart/index.vue`
- 修改：`shopflow-h5-next/src/pages/order/checkout/index.vue`
- 可选修改：`shopflow-h5-next/src/features/cart/cart-utils.ts`
- 可选新增：`shopflow-h5-next/src/features/cart/cart-display-utils.test.ts`

- [ ] 扫描两个页面中的旧视觉值与迁移文案：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master
rg -n "#1677ff|#172033|#f6f8fb|已接回旧站|兼容路径|下一批补齐" \
  shopflow-h5-next/src/pages/order/cart/index.vue \
  shopflow-h5-next/src/pages/order/checkout/index.vue
```

预期：命令返回旧色值或迁移文案命中。

- [ ] 如需要新增购物车摘要 helper，先补测试，覆盖已选件数、合计金额或底部 CTA 文案。

运行：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run test -- cart-utils
```

预期：相关测试通过。

- [ ] 将购物车页改造成“标题区 + 单列商品卡 + 固定底部结算栏”结构，保留勾选、编辑、删除、数量调整和去结算逻辑。

- [ ] 将结算页改造成“地址卡 + 商品清单 + 优惠券区 + 备注区 + 金额摘要/提交区”结构，保留地址跳转、优惠券选择和提交订单逻辑。

- [ ] 重新运行聚焦测试或页面相关测试：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run test -- cart-utils
```

预期：聚焦测试通过；若未新增 helper，则记录跳过原因。

## 任务 2：订单链路与支付页

**文件：**

- 修改：`shopflow-h5-next/src/pages/user/order-list/index.vue`
- 修改：`shopflow-h5-next/src/pages/order/detail/index.vue`
- 修改：`shopflow-h5-next/src/pages/order/payment/index.vue`
- 修改：`shopflow-h5-next/src/pages/order/payment-status/index.vue`
- 可选修改：`shopflow-h5-next/src/pages/user/user-list-utils.ts`
- 可选修改：`shopflow-h5-next/src/pages/user/user-list-utils.test.ts`
- 可选修改：`shopflow-h5-next/src/features/order/payment-utils.ts`
- 可选修改：`shopflow-h5-next/src/features/order/payment-utils.test.ts`

- [ ] 扫描四个页面中的旧视觉值与迁移文案：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master
rg -n "#1677ff|#172033|#f6f8fb|已接回旧站|兼容路径|下一批补齐" \
  shopflow-h5-next/src/pages/user/order-list/index.vue \
  shopflow-h5-next/src/pages/order/detail/index.vue \
  shopflow-h5-next/src/pages/order/payment/index.vue \
  shopflow-h5-next/src/pages/order/payment-status/index.vue
```

预期：命令返回旧色值或迁移文案命中。

- [ ] 若订单状态样式映射或支付结果拷贝逻辑需要扩展，先补对应测试。

运行：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run test -- user-list-utils
npm run test -- payment-utils
```

预期：相关测试通过。

- [ ] 重构订单列表页，使状态 chips、订单卡、金额区和操作按钮对齐第二阶段样式，同时保留切 tab、进详情与订单操作行为。

- [ ] 重构订单详情页，拆出状态摘要、商品信息、收货信息、订单信息、金额明细和物流信息卡，保留取消/删除/售后/确认/支付操作。

- [ ] 重构支付页与支付结果页，统一支付摘要和状态视觉，但不改变 JSAPI/H5 支付逻辑及结果页路由契约。

- [ ] 重新运行聚焦测试：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run test -- user-list-utils
npm run test -- payment-utils
```

预期：聚焦测试通过。

## 任务 3：个人中心页

**文件：**

- 修改：`shopflow-h5-next/src/pages/user/index.vue`
- 视需要修改：`shopflow-h5-next/src/pages/user/entry-utils.ts`
- 视需要修改：`shopflow-h5-next/src/pages/user/entry-utils.test.ts`

- [ ] 扫描个人中心页中的旧视觉值与迁移文案：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master
rg -n "#1677ff|#172033|#f6f8fb|已接回旧站|兼容路径|下一批补齐" \
  shopflow-h5-next/src/pages/user/index.vue \
  shopflow-h5-next/src/pages/user/entry-utils.ts
```

预期：命令返回旧色值或迁移文案命中。

- [ ] 若快捷入口映射或 toast 逻辑有变更，先补 `entry-utils` 测试。

运行：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run test -- entry-utils
```

预期：测试通过。

- [ ] 重构个人中心页为“用户头部 + 订单状态入口区 + 服务入口区”结构，保留订单、购物车、收藏、地址、优惠券、售后、帮助、服务、设置跳转。

- [ ] 将用户可见的迁移占位文案替换为正式产品文案；仅对暂未完成能力保留保守、可理解的提示。

- [ ] 重新运行聚焦测试：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run test -- entry-utils
```

预期：测试通过。

## 任务 4：全量验证与 review

**文件：**

- 新增：`openspec/changes/extend-h5-next-shopflow-commerce-style-to-order-user-pages/review.md`

- [ ] 运行类型检查：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run type-check
```

预期：退出码为 0。

- [ ] 运行单元测试：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run test
```

预期：退出码为 0。

- [ ] 运行 H5 构建：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run build:h5
```

预期：退出码为 0，并输出 H5 构建产物。

- [ ] 启动 H5 本地开发环境并做人工视觉冒烟：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run dev:h5
```

预期：购物车、结算、订单列表、订单详情、支付页、支付结果页和个人中心页在移动视口下可打开且不白屏。

- [ ] 写入 `review.md`，至少包含：

```markdown
# Review: extend-h5-next-shopflow-commerce-style-to-order-user-pages

## 检查范围

- 购物车页
- 结算页
- 订单列表页
- 订单详情页
- 支付页
- 支付结果页
- 个人中心页

## 发现

- 记录实现后的行为回归、视觉缺口或测试缺口。

## 验证证据

- `npm run type-check`：结果
- `npm run test`：结果
- `npm run build:h5`：结果
- H5 视觉冒烟检查：结果

## 残余风险

- 地址、收藏、优惠券、售后等剩余用户页尚未统一到第二阶段风格。
```

预期：review 明确记录覆盖范围、验证结果和后续剩余页面。
