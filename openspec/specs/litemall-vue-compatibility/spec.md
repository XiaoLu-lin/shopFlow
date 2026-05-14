# shopflow-h5 Legacy Compatibility Specification

## Purpose
Define the compatibility contract that lets the `shopflow-h5` frontend, imported from the legacy `litemall-vue` H5 project, work with the current ShopFlow wx APIs.
## Requirements
### Requirement: ShopFlow H5 frontend must bootstrap a ShopFlow tenant context before protected wx requests

由旧 `litemall-vue` 导入并更名后的 `shopflow-h5` 前台在访问当前 ShopFlow 用户侧接口时，MUST 先基于 `appid` 建立可用的租户上下文，不能继续假设所有 `/wx/*` 请求只依赖单一登录 token。

#### Scenario: homepage bootstrap carries appid and receives tenant token

- **Given** `shopflow-h5` 前台首次进入首页且本地尚未保存租户 token
- **When** 前台请求首页初始化接口
- **Then** 请求必须显式携带当前环境可用的 `appid`
- **And** 前台必须接收首页返回中的租户 token 并保存到本地

#### Scenario: protected wx request reuses stored tenant token

- **Given** 前台已经完成首页初始化并保存了租户 token
- **When** 前台继续请求其他需要租户上下文的 `/wx/*` 接口
- **Then** 请求必须自动携带 `X-ShopFlow-TenantId`
- **And** 后续请求不应因为缺少租户授权而直接被过滤器拦截

#### Scenario: auth request can bootstrap tenant token before login-related calls

- **Given** 用户从旧 H5 登录、注册或找回密码页直接进入，且本地尚未保存租户 token
- **When** 前台发起短信验证码、注册、登录或密码重置请求
- **Then** 前台必须先复用现有租户预热机制建立有效租户上下文
- **And** 请求不应因为缺少 `X-ShopFlow-TenantId` 而直接失败

#### Scenario: bootstrap failure remains visible

- **Given** 当前环境没有提供有效的 `appid`
- **When** 前台发起首页初始化或租户预热请求
- **Then** 前台不应伪造或写入无效租户 token
- **And** 初始化失败信息应保持可见，便于继续排查配置问题

### Requirement: ShopFlow H5 frontend must map stored user token to the current ShopFlow user header

`shopflow-h5` 前台如果本地已经保存用户 token，MUST 按当前 ShopFlow 用户侧协议发送 `X-ShopFlow-User-Token`，而不是继续默认发送旧协议的 `X-Litemall-Token`。

#### Scenario: logged-in request sends ShopFlow user token header

- **Given** 前台本地已经保存用户 token
- **When** 前台发起需要登录态的 `/wx/*` 请求
- **Then** 请求必须携带 `X-ShopFlow-User-Token`
- **And** 用户 token 的来源应与现有前台 localStorage 登录态保持兼容

#### Scenario: anonymous request does not send empty user token header

- **Given** 前台本地不存在用户 token
- **When** 前台发起无需登录的 `/wx/*` 请求
- **Then** 请求不应发送空值的 `X-ShopFlow-User-Token`
- **And** 页面仍可按现有匿名访问路径继续运行

### Requirement: ShopFlow H5 frontend must restore ordinary user account login without changing the current admin binding flow

`shopflow-h5` 登录页 MUST 恢复普通用户账号密码登录能力，但不能通过改写当前 `/wx/auth/login` 的管理员绑定语义来实现。

#### Scenario: legacy login does not call the admin binding flow

- **Given** 旧 H5 登录页使用普通账号密码登录
- **When** 用户提交登录表单
- **Then** 前台不应继续依赖当前 `/wx/auth/login` 的管理员绑定语义
- **And** 登录链路不应再因为缺少第三方授权上下文而返回 `A0223`

#### Scenario: successful login returns a usable user token for legacy frontend

- **Given** 用户使用兼容支持的账号类型和正确密码登录
- **When** 后端完成普通用户登录
- **Then** 响应必须包含可用于当前 ShopFlow 用户侧请求的用户 token
- **And** 前台必须将其写入现有登录态存储位置，供后续请求头映射复用

#### Scenario: unsupported legacy account type fails clearly

- **Given** 旧 H5 登录页提交了当前兼容范围外的历史账号类型
- **When** 后端无法按当前用户模型完成登录匹配
- **Then** 系统必须返回明确可理解的错误信息
- **And** 不能继续落入误导性的第三方授权错误

### Requirement: ShopFlow H5 frontend must align registration, captcha, and password reset with the current wx auth APIs

`shopflow-h5` 前台的注册、短信验证码和找回密码流程 MUST 对齐当前 ShopFlow 用户侧真实可用的 `/wx/auth/*` 接口，而不是继续调用历史路径或停留在未实现状态。

#### Scenario: registration captcha uses the current mobile captcha endpoint

- **Given** 用户在旧 H5 注册页输入手机号
- **When** 用户申请短信验证码
- **Then** 前台必须调用当前可用的手机验证码接口
- **And** 请求体必须符合当前后端的参数读取方式

#### Scenario: registration submits only the fields the backend actually consumes

- **Given** 用户已经完成旧 H5 注册页的前端输入校验
- **When** 前台提交注册请求
- **Then** 请求必须至少包含 `mobile`、`password` 和 `code`
- **And** 不应依赖当前后端未消费的历史字段才能完成注册

#### Scenario: forgot-password flow becomes executable end-to-end

- **Given** 用户从旧 H5 找回密码入口进入
- **When** 用户依次完成验证码申请、验证码输入和新密码提交
- **Then** 前台必须把 `mobile`、`code` 和 `password` 传给当前密码重置接口
- **And** 找回密码链路不能再停留在空实现或仅路由跳转状态

### Requirement: This compatibility change must distinguish completed compatibility from remaining historical gaps

本次兼容修复 MUST 覆盖旧前台的租户初始化和基础鉴权流程，但不能误宣称所有历史接口行为都已完全恢复。

#### Scenario: review records completed and remaining gaps separately

- **Given** 本次兼容修复完成并进入 review
- **When** 产出 review 结论
- **Then** review 必须分别记录已完成的首页、双 header、登录、注册、找回密码兼容项
- **And** 仍未纳入本次范围的旧接口路径差异、历史邮箱分支或其他残余差异必须被单独标注

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
