# Design: Extend H5 Next Suji Style To Home And Order Pages

## Overview

本次变更延续用户已经确认的 A 方向，不再为首页与订单链路重新发散新的主题分支。整体策略：

- 保持 `ShopFlow` 品牌与现有灰蓝 token
- 常规卡片、hero、底部动作条统一保持 `8px` 圆角基线
- 标题区只保留必要识别信息，不再保留冗余副标题说明
- 首页与订单链路在结构上对齐到“轻 hero + 信息卡片 + 固定动作区”的统一语言

## Shared Visual Rules

### 1. 颜色与层级

- 继续使用 `--sf-color-brand`、`--sf-color-brand-light`、`--sf-color-brand-soft`
- 页面背景统一采用浅灰蓝渐变底，不重新引入高饱和块
- 价格和支付金额继续使用现有价格强调色

### 2. 圆角与阴影

- hero、信息卡、底部按钮区、支付结果卡统一使用 `8px` 圆角
- 阴影继续限制在 `var(--sf-shadow-soft)` 与 `var(--sf-shadow-card)`

### 3. Hero 结构

- 首页 hero 收成轻量识别区，不堆叠厚营销文案
- 我的订单、订单详情、确认订单、支付订单只保留 `ShopFlow + 标题`
- 支付结果页保留结果 icon + 标题 + 描述，但不延续旧版大圆角结果卡语言

## Page Architecture

### 首页

- 顶部保留搜索入口
- 首屏保留轻量识别 hero
- 分类 / 热卖 / 新品 / 拼团等入口用紧凑功能卡承接
- 推荐商品采用更统一的商品卡，和商品域页面语言接近

### 我的订单

- 顶部使用轻 hero
- 状态概览用简短指标卡承接
- 标签切换区轻量化
- 订单卡统一为：
  - 订单摘要
  - 商品摘要
  - 金额与动作区

### 订单详情

- 顶部轻 hero 只表达当前状态
- 订单编号、时间、金额收为摘要卡
- 收货信息、订单信息、金额明细、物流信息统一为轻字段卡
- 底部动作区保留支付、确认收货、售后等真实入口

### 确认订单

- 顶部轻 hero
- 地址、商品、优惠券、备注、金额明细按提交流程顺序排列
- 底部提交区固定为“应付合计 + 提交订单”

### 支付订单

- 顶部 light hero，不再使用深色支付头块
- 订单编号与应付金额前置
- 支付方式卡保持清晰可选 / 不可选状态
- 底部单一主动作继续支付

### 支付结果

- 结果 icon、标题、说明、订单编号集中在结果卡
- 成功 / 取消 / 失败只切换 icon、强调色和文案
- 底部只保留“查看订单”主动作，必要时可提供订单详情次动作

## Helper Strategy

- 首页继续复用 `home-display-utils.ts`，必要时只补轻量 helper，不做过度抽象
- 订单链路优先复用 `features/order/order-display-utils.ts`、`payment-utils.ts`
- 如果空态、状态文案、支付方法说明需要调整，优先收敛到 helper 而不是散在页面模板内

## Testing Strategy

- 先补或复用 `home-display-utils`、`order-display-utils`、`payment-utils` 的测试
- 至少运行：
  - `pnpm exec vitest run src/pages/home/home-display-utils.test.ts src/features/order/order-display-utils.test.ts src/features/order/payment-utils.test.ts`
  - `pnpm type-check`
- 页面落地后补一次 H5 真实页面检查：
  - `/#/pages/home/index`
  - `/#/pages/user/order-list/index`
  - `/#/pages/order/detail/index?...`
  - `/#/pages/order/checkout/index`
  - `/#/pages/order/payment/index?...`
  - `/#/pages/order/payment-status/index?...`

## Risks And Mitigations

- 风险：首页入口块过多时容易重新回到“堆功能”的重界面。
  - 处理：保持频道入口紧凑，识别区只做轻量承接。
- 风险：订单链路改样式时误伤支付、地址、售后入口。
  - 处理：只重做结构和文案，不改 API、路由和按钮职责。
- 风险：支付结果页如果只改视觉不校对状态文案，容易和真实支付状态脱节。
  - 处理：继续复用并测试 `resolvePaymentResultCopy`，不把状态逻辑写死在模板里。

