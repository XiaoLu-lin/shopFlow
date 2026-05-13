## ADDED Requirements

### Requirement: ShopFlow H5 frontend must provide a complete aftersale flow using existing wx aftersale APIs

当前 `shopflow-h5` 在保持现有 ShopFlow 用户侧协议不变的前提下，MUST 基于已有 `wx` 售后接口提供完整的售后申请、售后列表和售后详情链路，而不能停留在仅有退款按钮或空壳列表页的状态。

#### Scenario: authenticated user can submit an aftersale request from order detail

- **Given** 用户已经登录，且订单状态允许售后
- **When** 用户从 H5 订单详情页进入售后申请页并提交完整信息
- **Then** 前台必须调用当前可用的售后提交接口完成申请
- **And** 提交前必须校验售后类型、原因、说明和必要凭证

#### Scenario: aftersale list is accessible from user center

- **Given** 用户已经登录
- **When** 用户从 H5 个人中心进入售后列表
- **Then** 前台必须展示当前用户的售后记录列表
- **And** 列表必须支持继续进入售后详情

#### Scenario: aftersale detail shows key order and aftersale information

- **Given** 用户已有一条售后记录
- **When** 用户进入 H5 售后详情页
- **Then** 前台必须展示订单、商品、金额、状态、原因、说明和凭证等关键信息
- **And** 页面不能只展示空白占位或未接线壳子

### Requirement: ShopFlow H5 frontend must complete the order comment submission flow using existing wx order comment APIs

当前 `shopflow-h5` 订单列表和订单详情中的“去评价”入口 MUST 形成可执行的评论发布闭环，而不能继续保留空实现。

#### Scenario: comment action from order list opens a usable comment page

- **Given** 用户在 H5 订单列表中看到可评价商品
- **When** 用户点击“去评价”
- **Then** 前台必须进入可用的评价发布页
- **And** 不得继续停留在空函数或无响应状态

#### Scenario: comment action from order detail submits a review successfully

- **Given** 用户在 H5 订单详情中进入评价页并填写评价内容
- **When** 用户提交评分、评论内容和可选图片
- **Then** 前台必须调用现有订单评价接口提交数据
- **And** 提交成功后必须给出明确反馈

### Requirement: ShopFlow H5 frontend must expose product comment browsing using existing wx goods comment APIs

当前 `shopflow-h5` 商品详情页 MUST 能基于现有商品评论接口提供评论浏览能力，而不能仅保留接口常量但没有页面消费。

#### Scenario: product detail opens the comment list page

- **Given** 商品存在评论数据
- **When** 用户从 H5 商品详情页进入评论列表
- **Then** 前台必须展示商品评论列表
- **And** 应能展示评论数量信息

#### Scenario: product comment list supports picture filtering

- **Given** 商品评论中同时存在普通评论与有图评论
- **When** 用户在 H5 评论列表中切换筛选
- **Then** 前台必须支持全部评论与有图评论视图切换
- **And** 列表分页行为必须保持可用

### Requirement: This H5 post-purchase change must remain scoped to frontend capability completion

本次 H5 交易后能力补齐 MUST 默认以现有 `shopflow-wx-api` 能力复用为前提，不得在没有明确确认的情况下把范围扩展为后端协议改造或非交易后模块补齐。

#### Scenario: implementation records backend reuse as the default path

- **Given** 本次 change 进入实现阶段
- **When** 团队执行售后与评论补齐任务
- **Then** 默认实现路径必须优先复用现有 `wx-api` 接口
- **And** 不应把消息、动态、赏金或其他后续能力混入同一次实现范围

