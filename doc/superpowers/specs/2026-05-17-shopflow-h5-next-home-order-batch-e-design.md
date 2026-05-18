# ShopFlow H5 Next 首页与订单链路批次 E 设计稿

> 对应 change：`extend-h5-next-suji-style-to-home-and-order-pages`

## 页面范围

- 首页 `pages/home/index`
- 我的订单 `pages/user/order-list/index`
- 订单详情 `pages/order/detail/index`
- 确认订单 `pages/order/checkout/index`
- 支付订单 `pages/order/payment/index`
- 支付结果 `pages/order/payment-status/index`

## 已确认方向

- 沿用前面已确认的 ShopFlow / Suji A 方向
- 统一使用灰蓝主题、`8px` 圆角、轻 hero、紧凑卡片节奏
- 订单链路标题区不再保留冗余副文案
- 支付订单页 hero 改成与其他页面一致的浅色渐变块

## 结构摘要

### 首页

- 搜索入口前置
- 识别 hero 轻量化
- 分类/热卖/新品/拼团等入口用紧凑频道卡承接
- 推荐商品统一成更克制的商品卡

### 订单链路

- 我的订单：概览指标 + 状态切换 + 订单摘要卡
- 订单详情：状态 hero + 摘要卡 + 信息字段卡 + 底部动作区
- 确认订单：地址、商品、优惠券、备注、金额、提交区按顺序排列
- 支付订单：金额前置 + 支付方式卡 + 主动作
- 支付结果：结果 icon + 标题文案 + 查看订单主动作

