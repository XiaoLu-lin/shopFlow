## ADDED Requirements

### Requirement: ShopFlow H5 next frontend must complete the order comment submission flow using existing wx order comment APIs

当前 `shopflow-h5-next` 作为新的多端前台主工程，MUST 基于已有 `wx` 订单评价接口补齐可执行的评价发布闭环，而不能继续停留在只有订单主链路、没有交易后评价能力的状态。

#### Scenario: comment action from order list opens a usable comment page

- **Given** 用户在 `shopflow-h5-next` 订单列表中看到可评价商品
- **When** 用户点击“去评价”
- **Then** 前台必须进入可用的评价发布页
- **And** 不得继续停留在无入口、无响应或空实现状态

#### Scenario: comment action from order detail submits a review successfully

- **Given** 用户在 `shopflow-h5-next` 订单详情中进入评价页并填写评价内容
- **When** 用户提交评分、评论内容和可选图片
- **Then** 前台必须调用现有订单评价接口提交数据
- **And** 提交成功后必须给出明确反馈

### Requirement: ShopFlow H5 next frontend must expose product comment browsing using existing wx goods comment APIs

当前 `shopflow-h5-next` 商品详情页 MUST 能基于现有商品评论接口提供评论浏览能力，而不能只保留商品浏览与购买路径。

#### Scenario: product detail opens the comment list page

- **Given** 商品存在评论数据
- **When** 用户从 `shopflow-h5-next` 商品详情页进入评论列表
- **Then** 前台必须展示商品评论列表
- **And** 商品详情中应能展示评论数量或评论入口信息

#### Scenario: product comment list supports picture filtering

- **Given** 商品评论中同时存在普通评论与有图评论
- **When** 用户在评论列表中切换筛选
- **Then** 前台必须支持全部评论与有图评论视图切换
- **And** 列表分页行为必须保持可用

### Requirement: ShopFlow H5 next frontend must align aftersale submission rules with wx user-side behavior

当前 `shopflow-h5-next` 已有售后申请页，但 MUST 与 `shopflow-wx` 用户侧售后申请规则保持一致，避免出现 H5 与小程序在同一业务上的表单行为漂移。

#### Scenario: aftersale submission enforces wx-required fields

- **Given** 用户在 `shopflow-h5-next` 进入售后申请页
- **When** 用户未填写必要字段直接提交
- **Then** 前台必须校验售后类型、原因和说明
- **And** 不得直接发起提交请求

#### Scenario: aftersale proof is required for non-undelivered refund types

- **Given** 用户选择的售后类型不是“未收货退款”
- **When** 用户未上传凭证直接提交
- **Then** 前台必须提示补充凭证
- **And** 不得绕过前端校验直接提交

### Requirement: ShopFlow H5 next frontend must align post-purchase entry semantics with wx where capability already exists

当 `shopflow-wx` 已具备对应交易后能力时，`shopflow-h5-next` MUST 在订单页、商品页和售后页上提供一致的用户侧能力入口语义，而不能长期处于“已有一半能力但入口不一致”的状态。

#### Scenario: order pages expose comment and refund-related actions only when allowed

- **Given** 后端返回订单当前允许的操作集合
- **When** `shopflow-h5-next` 渲染订单列表或订单详情
- **Then** 前台必须只展示当前可执行的评价、退款或售后入口
- **And** 不得展示与当前状态不符的误导性按钮

#### Scenario: aftersale list status grouping matches wx user recognition

- **Given** 用户从 `shopflow-h5-next` 进入售后列表
- **When** 页面展示售后状态筛选
- **Then** 前台必须使用与 `shopflow-wx` 用户侧一致的状态分组认知
- **And** 不应继续保留与小程序明显不一致的 tab 语义
