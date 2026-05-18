# Tasks: Extend H5 Next Suji Style To Home And Order Pages

- [x] 1. 补齐首页与订单链路批次 E 的设计与计划工件
  - 验收契约：本批 MUST 明确包含 `home`、`user/order-list`、`order/detail`、`order/checkout`、`order/payment`、`order/payment-status`
  - 验收契约：本批 SHALL 明确延续灰蓝主题、`8px` 圆角、轻 hero 的已确认方向

- [x] 2. 实现首页与订单列表新样式
  - 验收契约：首页 MUST 保留搜索入口、频道入口、优惠区块和商品详情跳转
  - 验收契约：订单列表 SHALL 保留状态切换、支付、取消、删除、确认收货与详情跳转

- [x] 3. 实现订单详情、确认订单、支付订单、支付结果新样式
  - 验收契约：订单详情不得丢失售后、支付、确认收货等真实操作入口
  - 验收契约：确认订单、支付订单、支付结果 MUST 保持现有地址、优惠券、支付流程和跳转语义

- [x] 4. review 与验证
  - 验收契约：必须运行相关 vitest 与 type-check
  - 验收契约：必须补本地 H5 页面验证说明
  - 验收说明：已运行 `pnpm exec vitest run src/pages/home/home-display-utils.test.ts src/features/order/order-display-utils.test.ts src/features/order/payment-utils.test.ts` 与 `pnpm type-check`
  - 验收说明：已补首页首屏重复请求与支付结果页定时器清理的回归测试，并通过 `17` 条相关测试
  - 验收说明：已用本地 H5 路由检查首页命中 `今日精选`；订单链路页在无登录态自动化环境下均被真实鉴权重定向到登录页，需在已登录状态下继续做视觉验收
