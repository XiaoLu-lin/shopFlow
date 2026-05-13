# shopflow-h5 与 shopflow-wx 业务差异整理

## 1. 文档目的

这份文档用于梳理 `shopflow-h5` 和 `shopflow-wx` 两个前台项目的业务覆盖范围、实现差异和后续补齐方向。

适用场景：

- 做前台能力盘点
- 评估 H5 与小程序的功能差距
- 规划 H5 补齐范围
- 帮助开发快速定位两端页面和能力差异

## 2. 结论先看

从业务域来看，`shopflow-h5` 和 `shopflow-wx` 都属于同一套 C 端商城体系。

但从当前代码落地情况看：

- `shopflow-wx` 是更完整的小程序前台版本
- `shopflow-h5` 是覆盖核心商城交易主链路的 H5 子集

可以简单理解为：

- `shopflow-wx` = 商城主链路 + 微信生态能力 + 社交互动能力 + 运营扩展能力
- `shopflow-h5` = 商城主链路 + 部分个人中心能力

## 3. 三列表对照

| 业务模块 | H5 已有 | WX 已有 | H5 缺失或差异 |
|---|---|---|---|
| 首页 | 是 | 是 | 无明显差异 |
| 分类 | 是 | 是 | 无明显差异 |
| 搜索 | 是 | 是 | 无明显差异 |
| 商品详情 | 是 | 是 | 小程序额外带分享、聊天、赏金入口 |
| 品牌/店铺列表 | 是 | 是 | 小程序更偏店铺经营场景 |
| 品牌/店铺详情 | 是 | 是 | 小程序支持评论、聊天 |
| 品牌入驻/创建 | 否 | 是 | H5 缺 |
| 品牌订单管理 | 否 | 是 | H5 缺 |
| 专题列表 | 是 | 是 | 无明显差异 |
| 专题详情 | 是 | 是 | 小程序评论、分享更完整 |
| 拼团列表 | 是 | 是 | 无明显差异 |
| 购物车 | 是 | 是 | 无明显差异 |
| 结算页 | 是 | 是 | 小程序多余额抵扣、赏金链路 |
| 支付 | 是 | 是 | H5 走 H5/JSBridge，小程序走 `wx.requestPayment` |
| 订单列表 | 是 | 是 | 小程序订单后置动作更完整 |
| 订单详情 | 是 | 是 | 小程序多聊天、评论、售后入口 |
| 地址管理 | 是 | 是 | 无明显差异 |
| 收藏 | 是 | 是 | 无明显差异 |
| 优惠券列表 | 是 | 是 | 无明显差异 |
| 结算选券 | 是 | 是 | 无明显差异 |
| 登录 | 是 | 是 | H5 偏账号登录兼容；小程序多微信授权登录 |
| 注册/重置密码 | 是 | 是 | 无明显差异 |
| 用户资料修改 | 是 | 是 | 小程序更贴近微信环境，支持头像上传等能力 |
| 反馈 | 是 | 是 | 无明显差异 |
| 帮助/服务说明 | 是 | 是 | 无明显差异 |
| 退款 | 是 | 是 | H5 有退款；小程序更完整 |
| 售后申请 | 否 | 是 | H5 缺 |
| 售后列表 | 否 | 是 | H5 缺 |
| 售后详情 | 否 | 是 | H5 缺 |
| 商品评论列表 | 否 | 是 | H5 缺 |
| 商品发表评论 | 否 | 是 | H5 缺 |
| 足迹 | 否 | 是 | H5 缺 |
| 动态 | 否 | 是 | H5 缺 |
| 消息列表 | 否 | 是 | H5 缺 |
| 聊天 | 否 | 是 | H5 缺 |
| 赏金活动 | 否 | 是 | H5 缺 |
| 分享海报/邀请 | 否 | 是 | H5 缺 |
| 提现/余额 | 否 | 是 | H5 缺 |
| 管理员入口 | 否 | 是 | H5 缺 |
| 二维码授权登录 | 否 | 是 | H5 缺 |

## 4. H5 当前已覆盖的核心页面

`shopflow-h5` 当前主要覆盖的是商城交易主链路和部分个人中心能力。

### 4.1 首页与商品浏览

- `/`
- `/items`
- `/items/search`
- `/items/detail/:itemId`
- `/items/category`
- `/items/hot`
- `/items/new`
- `/items/groupon`

### 4.2 品牌与专题

- `/items/brand/:brandId`
- `/items/brand-list`
- `/items/topic/:topicId`
- `/items/topic-list`

### 4.3 用户中心

- `/user`
- `/user/collect`
- `/user/address`
- `/user/address/edit`
- `/user/help`
- `/user/feedback`
- `/user/information`
- `/user/information/setMobile`
- `/user/information/setNickname`
- `/user/information/setPassword`
- `/user/order/list/:active`
- `/user/coupon/list/:active`
- `/user/refund/list`

### 4.4 交易链路

- `/order`
- `/order/checkout`
- `/order/order-detail`
- `/order/payment`
- `/order/payment/:status`

### 4.5 登录注册

- `/login`
- `/login/registerGetCode`
- `/login/registerSubmit/:phone`
- `/login/registerStatus/:status`
- `/login/forget`
- `/login/forget/reset`
- `/login/forget/reset/:status`

## 5. 小程序端额外具备的页面与能力

`shopflow-wx` 除了商城主链路，还多了大量互动、社交、运营和微信生态相关能力。

### 5.1 互动与社交

- `pages/dynamic/dynamic`
- `pages/messageList/messageList`
- `pages/chat/chat`

### 5.2 评论闭环

- `pages/comment/commentList/commentList`
- `pages/comment/commentPost/commentPost`

### 5.3 售后闭环

- `pages/ucenter/aftersale/aftersale`
- `pages/ucenter/aftersaleList/aftersaleList`
- `pages/ucenter/aftersaleDetail/aftersaleDetail`

### 5.4 运营与增长

- `pages/rewards/rewards`
- `pages/ucenter/shareMe/shareMe`
- `pages/ucenter/withdraw/withdraw`

### 5.5 其他小程序专属能力

- `pages/ucenter/footprint/footprint`
- `pages/auth/qrauth/qrauth`
- `pages/ucenter/admin/admin`
- `pages/brand/brandAdd/brandAdd`
- `pages/brand/brandOrder/brandOrder`

## 6. 关键差异总结

### 6.1 底部导航不同

H5 底部 tab 只有 4 个：

- 精选
- 分类
- 购物车
- 我的

小程序底部 tab 有 5 个：

- 首页
- 动态
- 消息
- 购物车
- 我的

这意味着小程序把“动态”和“消息”作为一级入口，而 H5 没有这两个主入口。

### 6.2 H5 更偏浏览器兼容，小程序更偏微信生态

H5 技术形态主要是：

- Vue 2
- Vant
- vue-router
- axios
- localStorage

小程序技术形态主要是微信原生能力：

- `wx.login`
- `wx.requestPayment`
- `wx.uploadFile`
- `wx.requestSubscribeMessage`
- WebSocket

所以两边虽然业务主题相同，但产品能力边界并不一样。

### 6.3 支付链路实现不同

H5 支付需要兼容两类环境：

- 微信内置浏览器支付
- 外部浏览器 H5 支付跳转

小程序支付则直接走：

- `wx.requestPayment`

因此支付体验和实现方式天然不同，不能简单一套页面直接复用。

### 6.4 小程序端后置交易能力更完整

在订单完成后的延伸链路上，小程序明显比 H5 更完整，主要包括：

- 评论
- 售后
- 消息触达
- 聊天沟通
- 订单相关分享

而 H5 目前更聚焦在“下单完成”和“订单查看”。

### 6.5 H5 有部分接口定义，但页面未完整闭环

H5 代码里仍然保留了不少接口定义，例如：

- 评论相关
- 足迹相关
- 退款相关

但从页面路由和页面落地情况看，这些能力并没有像小程序那样形成完整业务闭环。

因此评估功能时，应该以“页面和流程是否真正落地”为准，而不是只看 API 常量是否存在。

## 7. H5 补齐建议优先级

如果后续要把 H5 往小程序能力靠拢，建议按下面顺序推进。

### 优先级 1：补交易后链路

- 售后申请
- 售后列表
- 售后详情
- 订单评价
- 商品评论列表/发表评论

原因：

- 这部分和交易闭环直接相关
- 用户投诉、售后处理、复购体验都依赖它
- 优先级通常高于社交和运营能力

### 优先级 2：补用户留存辅助能力

- 足迹
- 消息列表
- 基础会话提醒

原因：

- 能提升用户回访和订单跟进体验
- 复杂度低于完整聊天体系

### 优先级 3：补互动与运营能力

- 动态
- 聊天
- 赏金活动
- 分享海报/邀请
- 提现/余额

原因：

- 这部分更偏增长和运营
- 依赖微信生态能力较多
- 在 H5 上实现时往往需要重新设计交互和技术方案

## 8. 适合直接给团队的结论

如果只用一句话概括当前状态，可以写成：

`shopflow-wx` 是更完整的小程序前台，覆盖商城、互动、运营和微信生态能力；`shopflow-h5` 目前主要覆盖商城核心交易主链路，仍缺少评论、售后、消息、聊天、动态、赏金、分享、提现等一批小程序侧已具备的业务能力。

## 9. 参考代码位置

### H5 路由与页面入口

- `shopflow-h5/src/router/home.js`
- `shopflow-h5/src/router/items.js`
- `shopflow-h5/src/router/user.js`
- `shopflow-h5/src/router/order.js`
- `shopflow-h5/src/router/login.js`
- `shopflow-h5/src/components/Tabbar/index.vue`

### H5 请求与兼容层

- `shopflow-h5/src/api/api.js`
- `shopflow-h5/src/utils/request.js`
- `shopflow-h5/src/utils/shopflow-compat.js`

### 小程序页面与配置

- `shopflow-wx/app.json`
- `shopflow-wx/custom-tab-bar/index.js`
- `shopflow-wx/config/api.js`
- `shopflow-wx/utils/user.js`
- `shopflow-wx/utils/util.js`

