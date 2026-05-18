# Proposal: Extend H5 Next Suji Style To Home And Order Pages

## Why

`shopflow-h5-next` 已经完成商品浏览域、登录域和大部分用户页的 Suji 风格统一，但首页与订单主链路仍停留在上一阶段的实现语言里：

- 首页仍是较早的首页布局，和已确认的轻量灰蓝方向不完全一致
- 我的订单、订单详情、确认订单、支付订单、支付结果仍保留旧版 `page-head + section-card` 结构
- 订单链路的标题区、副文案、按钮语义和卡片节奏没有和购物车、用户中心、商品页形成统一感

这几个页面构成了用户从浏览到下单、支付、查看订单的高频主路径。如果继续保留旧样式，会让 H5 Next 在演示和提审阶段看起来仍像“完成了一半”。

## What Changes

- 统一首页到已确认的 Suji / ShopFlow A 方向
- 统一 `pages/user/order-list` 与 `pages/order/detail`
- 统一 `pages/order/checkout`、`pages/order/payment`、`pages/order/payment-status`
- 保持现有订单、支付、地址、优惠券、售后相关接口与路由行为不变
- 在必要时补充 `home` / `order` 展示 helper 与测试，使这批页面保持同一套文案和视觉节奏

## Impact

- 影响模块：`shopflow-h5-next`
- 影响页面：6 个
- 不影响后端接口契约
- 不新增新路由和新依赖

