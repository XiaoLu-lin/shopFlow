# 提案：refine-h5-next-user-center-with-suji-profile-style

## 背景与原因

`shopflow-h5-next` 的个人中心页已经纳入第二阶段 UI refresh，但当前实现仍更像一组交易型卡片，不够贴近用户已确认的 `docs-suji` Profile 页面气质。

在本地视觉预览中，用户已经明确选择 `A. 原版 Suji 感` 作为个人中心的目标方向，并确认这轮先只改个人中心，不回改购物车、结算、订单与支付页。

## 变更内容

本次 change 将只在 `shopflow-h5-next` 的个人中心页内完成以下工作：

1. 把当前个人中心重写成更接近 `docs-suji` Profile 的三段式结构：
   - 渐变用户头部
   - 我的订单五宫格
   - 列表式服务入口
2. 保留现有数据来源、登录态兼容、路由和服务入口能力。
3. 适度补充展示辅助逻辑与测试，收敛头部文案、手机号脱敏和服务入口排序。

本次变更不包含：

- 回改购物车、结算、订单、支付页
- 新增后端接口或修改协议
- 新增用户画像字段、会员等级、积分等业务数据

## 影响范围

- `shopflow-h5-next/src/pages/user/index.vue`
- `shopflow-h5-next/src/pages/user/dashboard-utils.ts`
- `shopflow-h5-next/src/pages/user/dashboard-utils.test.ts`
- 视需要影响 `shopflow-h5-next/src/pages/user/entry-utils.ts`

主要风险：

- 服务入口布局调整可能误伤现有跳转
- 顶部头部重写若处理不当，可能造成长昵称换行或安全区问题

预期收益：

- 让个人中心页明显更接近用户选定的 Suji 风格
- 为后续其他页面沿用“先预览选 UI，再落真实页面”的方式建立样板
