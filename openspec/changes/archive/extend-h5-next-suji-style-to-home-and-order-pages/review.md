# Review: Extend H5 Next Suji Style To Home And Order Pages

## 结论

- 本轮自检未发现新的阻塞级实现问题。
- 首页与订单链路六个页面已按批次 E 的设计稿统一为灰蓝主题、`8px` 圆角、轻 hero 结构。
- 真实下单、支付、订单操作、地址/优惠券流转语义保持沿用原有 API 和路由。
- review 阶段新增的两个回归问题已修复：首页首屏不再重复请求，支付结果页手动跳转时也不会再被旧定时器带回订单列表。

## 已覆盖内容

- 首页恢复并重构为 `搜索 + 轻 hero + 频道入口 + 优惠区块 + 拼团卡 + 推荐商品流`
- 我的订单重构为 `轻 hero + 概览指标 + 状态切换 + 订单卡 + 真实操作入口`
- 订单详情、确认订单、支付订单、支付结果统一为轻量卡片层级和固定底部动作区
- 首页 helper 与订单 helper 测试已补齐并保持通过

## 验证证据

- `cd shopflow-h5-next && pnpm exec vitest run src/pages/home/home-display-utils.test.ts src/features/order/order-display-utils.test.ts src/features/order/payment-utils.test.ts`
  - 结果：`3` 个测试文件，`17` 个测试全部通过
- `cd shopflow-h5-next && pnpm type-check`
  - 结果：命令退出码 `0`
- 本地 H5 路由检查
  - `/#/pages/home/index` 在无登录态环境下可正常命中 `今日精选`
  - `/#/pages/user/order-list/index`
  - `/#/pages/order/detail/index?orderId=1`
  - `/#/pages/order/checkout/index`
  - `/#/pages/order/payment/index?orderId=1`
  - `/#/pages/order/payment-status/index?status=success&orderId=1`
  - 上述订单链路页在无登录态自动化环境下均被真实鉴权重定向到 `/pages/login/index?redirect=home`

## 发现与处理

- 发现：首页在页面创建时先直接调用 `bootstrap()`，首次 `onShow` 又会再触发一次，导致 `/home/index` 首次进入重复请求。
  - 处理：把“跳过首次 `onShow`”收敛为 `createFirstTriggerSkipper()`，保留首屏初始化请求，同时继续支持后续回显刷新；并补首页 helper 测试覆盖该行为。
- 发现：支付成功页的 3 秒自动跳转定时器在用户手动进入订单详情或订单列表时不会清理，可能产生二次强制跳转。
  - 处理：新增 `createTimeoutController()` 托管延时跳转，并在手动跳转与 `onUnload` 时统一清理；同时补支付工具测试覆盖定时器清理行为。

## 残余风险

- 订单链路的最终视觉效果仍需在已登录状态下由应用内浏览器人工验收一次，因为自动化环境无法复用当前登录态。
- 支付结果页在成功态下会继续尝试读取真实订单详情；如果后端鉴权或订单状态同步异常，仍会沿用既有登录拦截与结果文案逻辑。

## 归档建议

- 当前 review 与 verify 已完成，未再发现阻塞归档的问题。
- 可进入 `archive` 收尾。
