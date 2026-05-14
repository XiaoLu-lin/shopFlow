# Review: align-shopflow-h5-next-post-purchase-with-wx

## Review Scope

- proposal: `openspec/changes/align-shopflow-h5-next-post-purchase-with-wx/proposal.md`
- design: `openspec/changes/align-shopflow-h5-next-post-purchase-with-wx/design.md`
- spec: `openspec/changes/align-shopflow-h5-next-post-purchase-with-wx/specs/litemall-vue-compatibility/spec.md`
- plan: `doc/superpowers/plans/2026-05-14-shopflow-h5-next-post-purchase-alignment-plan.md`

本次 review 只覆盖 `shopflow-h5-next` 交易后链路对齐，不包含已废弃的 `shopflow-h5`，也不包含消息、动态、赏金、商家侧售后审核等非交易后能力。

## Completed Scope

### 1. 评价闭环补齐

- 新增订单评价发布页，接通 `order/goods`、`order/comment` 与图片上传
- 在订单详情、订单列表中补齐“去评价”入口，并按后端可操作态控制展示
- 在商品详情补齐评论摘要与“查看全部评论”入口
- 新增商品评论列表页，支持全部评论 / 有图评论切换与分页加载

### 2. 售后规则与语义对齐

- 售后申请页对齐 `shopflow-wx` 校验规则：类型必选、原因必填、说明必填、非未收货退款必须上传凭证
- 售后列表 tab 语义对齐为 `申请中(1)`、`处理中(2)`、`已完成(3)`、`已拒绝(4)`
- 订单详情区分“申请退款”与“申请售后”语义，并保持申请页、列表页、详情页跳转闭环
- 售后详情补齐页内确认交互，H5 文件上传支持浏览器 `File` 流程

### 3. 为收口 smoke 补齐的兼容项

- 新增旧 H5 hash 路由到 `shopflow-h5-next` 页面路由的入口级重写，兼容 `/#/order` 等旧地址
- 首页补齐优惠券模块，匹配既有首页 smoke
- 登录后跳转改为 tab 页 `switchTab`、普通页 `redirectTo`
- 支付页 `uni.requestPayment` 补 `orderInfo`
- 购物车底部结算栏抬高，避免被 tabbar 遮挡

## Verification Evidence

以下命令均于本次变更实现完成后重新执行：

- `cd shopflow-h5-next && npx vitest run`
  - 结果：`19 passed / 91 passed`
- `cd shopflow-h5-next && npm run type-check`
  - 结果：`vue-tsc --noEmit` 退出码 `0`
- `cd shopflow-h5-next && npx playwright test tests/e2e/smoke.spec.ts`
  - 结果：`20 passed`
- `cd shopflow-h5-next && npm run build:h5`
  - 结果：构建完成，退出码 `0`

额外关键链路复核：

- 支付成功回跳 smoke 已按 `uni-app` 实际 URL 形态放宽断言后重新执行通过

## Findings

本轮 review 未发现阻断本次 proposal 范围交付的新增缺陷。

## Residual Risks

- `build:h5` 仍存在大量 Sass `@import` 与 legacy JS API 弃用告警，当前不阻断构建，但后续升级 Sass / `uview-plus` 时需要专项收敛
- 本轮 Playwright 验证以前端集成 smoke 为主，覆盖了关键页面跳转与提交流程，但不等同于真实后端联调环境的完整验收

## Out Of Scope Reminder

以下差异未纳入本次 change，后续如需继续对齐应另开 change：

- 聊天 / 消息
- 动态 / 足迹 / 赏金
- 商家侧售后审核
- 其他非交易后用户侧能力
