# 任务：extend-h5-next-shopflow-commerce-style-to-order-user-pages

## 1. 第二阶段设计与规格建档

- [x] 为第二阶段交易链路与个人中心换肤创建 proposal、design、tasks 和对应 spec 更新。
- [x] 补充第二阶段 brainstorming 设计稿与 writing-plans 实施计划。
- [x] 明确本次范围聚焦 `shopflow-h5-next` 的购物车、结算、订单、支付和个人中心页面。

验收契约：

- proposal MUST 明确本次不修改后端接口和交易协议。
- tasks SHALL 明确第二阶段页面范围与排除项，避免实现中继续扩张到全部用户页。

## 2. 购物车与结算页统一新风格

- [x] 改造 `src/pages/order/cart/index.vue`，统一到 ShopFlow 灰蓝交易风格。
- [x] 改造 `src/pages/order/checkout/index.vue`，统一到 ShopFlow 结算风格。
- [x] 如有新增购物车金额或视图归一化 helper，补对应测试。

验收契约：

- 用户在购物车页 MUST 继续可用勾选、全选、编辑删除、数量调整和去结算行为。
- 结算页 SHALL 继续使用现有 checkout flow context 和地址/优惠券/备注/提交订单逻辑。
- 页面不得继续展示“已接回旧站”“兼容路径”等迁移期文案。

## 3. 订单链路与支付结果统一新风格

- [x] 改造 `src/pages/user/order-list/index.vue`。
- [x] 改造 `src/pages/order/detail/index.vue`。
- [x] 改造 `src/pages/order/payment/index.vue` 与 `src/pages/order/payment-status/index.vue`。
- [x] 如有新增订单状态或支付结果 helper，补对应测试。

验收契约：

- 订单列表和详情页 MUST 保留取消、删除、确认收货、支付、售后等现有操作入口。
- 支付页和支付结果页 SHALL 保留现有微信内 JSAPI、H5 支付和状态页回跳行为。
- 缺失的参考稿装饰数据不得被前端伪造为真实业务状态。

## 4. 个人中心统一新风格

- [x] 改造 `src/pages/user/index.vue`。
- [x] 如个人中心快捷入口规则发生扩展，同步更新 `entry-utils` 与测试。
- [x] 统一个人中心中的订单状态入口、服务入口和空态/提示文案。

验收契约：

- 个人中心 MUST 保留现有订单、购物车、收藏、地址、优惠券、售后、帮助、服务和设置跳转能力。
- 个人中心 SHALL 以新风格呈现订单状态与快捷入口，不再保留“下一批补齐”式用户可见文案。

## 5. Review 与验证

- [x] 完成类型检查、单元测试和 H5 构建验证。
- [x] 对购物车、结算、订单列表、订单详情、支付页、支付结果页和个人中心做人工视觉冒烟检查。
- [x] 创建本次 change 的 `review.md`，记录覆盖范围、验证证据与残余风险。

验收契约：

- review MUST 记录本次已覆盖的第二阶段页面与仍未纳入的剩余用户页。
- 在未提供最新 `type-check / test / build:h5` 证据前，不得声称本次完成可归档。
