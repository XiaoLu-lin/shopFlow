# ShopFlow H5 Next Home And Order Batch E Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将首页与订单链路 6 个页面统一到已确认的 Suji / ShopFlow A 方向，同时保持现有购物、下单、支付和订单操作行为不变。

**Architecture:** 继续复用 `home-display-utils`、`order-display-utils` 与 `payment-utils` 承接展示文案和状态映射，页面层只做结构与样式重组。实现按首页/订单列表/订单详情与支付链路三段推进，每一段完成后立即做相关测试与类型检查。

**Tech Stack:** uni-app、Vue 3、TypeScript、SCSS、Vitest

---

### Task 1: 补齐本批工件

**Files:**
- Create: `openspec/changes/extend-h5-next-suji-style-to-home-and-order-pages/proposal.md`
- Create: `openspec/changes/extend-h5-next-suji-style-to-home-and-order-pages/design.md`
- Create: `openspec/changes/extend-h5-next-suji-style-to-home-and-order-pages/tasks.md`
- Create: `doc/superpowers/specs/2026-05-17-shopflow-h5-next-home-order-batch-e-design.md`
- Create: `doc/superpowers/plans/2026-05-17-shopflow-h5-next-home-order-batch-e-plan.md`

- [ ] Step 1: 写入 proposal、design、tasks 与设计稿摘要
- [ ] Step 2: 核对页面范围和方向描述与预览稿一致

### Task 2: 首页与订单列表落地

**Files:**
- Modify: `shopflow-h5-next/src/pages/home/index.vue`
- Modify: `shopflow-h5-next/src/pages/user/order-list/index.vue`
- Test: `shopflow-h5-next/src/pages/home/home-display-utils.test.ts`
- Test: `shopflow-h5-next/src/features/order/order-display-utils.test.ts`

- [ ] Step 1: 先补展示层需要的 helper / 文案调整测试
- [ ] Step 2: 改首页结构与样式，保持频道和商品跳转不变
- [ ] Step 3: 改订单列表结构与样式，保持状态切换和订单动作不变
- [ ] Step 4: 运行首页与订单列表相关测试

### Task 3: 订单详情与支付链路落地

**Files:**
- Modify: `shopflow-h5-next/src/pages/order/detail/index.vue`
- Modify: `shopflow-h5-next/src/pages/order/checkout/index.vue`
- Modify: `shopflow-h5-next/src/pages/order/payment/index.vue`
- Modify: `shopflow-h5-next/src/pages/order/payment-status/index.vue`
- Test: `shopflow-h5-next/src/features/order/payment-utils.test.ts`

- [ ] Step 1: 如需调整支付状态文案或支付方式文案，先补测试
- [ ] Step 2: 改订单详情结构与样式
- [ ] Step 3: 改确认订单、支付订单、支付结果结构与样式
- [ ] Step 4: 运行支付与订单链路相关测试

### Task 4: 验证与收尾

**Files:**
- Modify: `openspec/changes/extend-h5-next-suji-style-to-home-and-order-pages/tasks.md`
- Create: `openspec/changes/extend-h5-next-suji-style-to-home-and-order-pages/review.md`

- [ ] Step 1: 运行 `pnpm exec vitest run src/pages/home/home-display-utils.test.ts src/features/order/order-display-utils.test.ts src/features/order/payment-utils.test.ts`
- [ ] Step 2: 运行 `pnpm type-check`
- [ ] Step 3: 记录页面验证入口与残余风险
- [ ] Step 4: 更新 tasks 状态并写 review 记录
