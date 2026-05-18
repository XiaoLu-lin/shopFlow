# Review：extend-h5-next-shopflow-commerce-style-to-order-user-pages

## 覆盖范围

本次 review 覆盖以下第二阶段页面与配套兼容能力：

- `shopflow-h5-next/src/pages/order/cart/index.vue`
- `shopflow-h5-next/src/pages/order/checkout/index.vue`
- `shopflow-h5-next/src/pages/user/order-list/index.vue`
- `shopflow-h5-next/src/pages/order/detail/index.vue`
- `shopflow-h5-next/src/pages/order/payment/index.vue`
- `shopflow-h5-next/src/pages/order/payment-status/index.vue`
- `shopflow-h5-next/src/pages/user/index.vue`
- `shopflow-h5-next/src/shared/utils/legacy-route-map.ts`
- `shopflow-h5-next/tests/e2e/smoke.spec.ts`

同步检查的配套 helper 与测试：

- `src/features/cart/cart-display-utils.ts`
- `src/features/order/order-display-utils.ts`
- `src/pages/user/dashboard-utils.ts`
- `src/pages/user/entry-utils.ts`
- `src/pages/user/user-list-utils.ts`

## 核对结论

- 第二阶段目标页已经统一到 ShopFlow 灰蓝风格，并去除了迁移期面向用户文案。
- 购物车页保留了勾选、全选、编辑删除、数量调整与去结算行为。
- 结算页继续复用既有 `AddressId / CartId / CouponId / UserCouponId` flow context，并恢复下单后进入支付页的交易链路。
- 订单列表、订单详情、支付页、支付结果页与个人中心页面均保留原有关键跳转或操作语义。
- H5 旧 `hash` 路由兼容层已在应用创建前接入，`/#/order`、`/#/order/order-detail`、`/#/order/payment/:status`、`/#/user/order/list/:active` 等旧链接可在当前 uni-app H5 运行时进入对应 `pages/*` 页面。

## 验证证据

2026-05-16 最新执行结果：

- `cd shopflow-h5-next && npm run type-check`
  - 结果：通过
- `cd shopflow-h5-next && npm run test`
  - 结果：通过，`21` 个测试文件、`92` 条测试全部通过
- `cd shopflow-h5-next && npm run build:h5`
  - 结果：通过
  - 备注：仍存在既有 `Sass legacy-js-api` 与 `@import` 弃用告警，未新增构建失败
- `cd shopflow-h5-next && npm run test:e2e`
  - 结果：通过，`10` 条 Playwright smoke 用例全部通过
  - 覆盖：首页、订单详情到售后申请、售后详情与撤销、购物车到结算到支付结果、个人中心到订单列表

## 发现与处理

- 发现：旧 H5 `hash` 链接在 uni-app H5 运行时未自动映射到 `/#/pages/...`，导致旧 `/#/order`、`/#/user`、`/#/order/order-detail` 等入口直接 no-match。
  - 处理：将 `legacy-route-map` 的 H5 兼容重写前移到 `src/main.ts` 顶层，在应用创建前执行。
- 发现：结算页下单成功后只弹 toast，未继续进入支付页。
  - 处理：恢复下单后按 `orderIds` 跳转 `pages/order/payment/index`。
- 发现：现有 uni-app H5 fixed 底部交易栏在自动化点击中会被系统 tabbar 命中拦截。
  - 处理：页面补充 `z-index`，同时 smoke 改为更贴近 uni-app DOM 的事件触发方式。

## 残余风险

- 当前 E2E 已对第二阶段目标页完成 smoke 覆盖，但仍主要是 mocked `/wx/*` 接口场景，不等于真实后端全链路联调。
- 本次未继续统一的用户页仍包括地址、优惠券、收藏、帮助、服务、反馈、资料设置、售后申请/详情之外的剩余用户侧页面；这些页面视觉风格仍可能与第二阶段目标页存在落差。
- `legacy-route-map` 目前已补齐本次交易链路和常用用户页映射，但若后续再接入更多旧 H5 深链接，仍需要继续扩充映射测试。

## 归档建议

- review 与 verify 已完成。
- 按仓库规则，当前应等待用户确认后再决定是否 `archive`。
