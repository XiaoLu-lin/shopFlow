# litemall-vue-compatibility Specification

## Purpose
TBD - created by archiving change align-litemall-vue-tenant-user-headers. Update Purpose after archive.
## Requirements
### Requirement: Legacy litemall-vue frontend must bootstrap a ShopFlow tenant context before protected wx requests

导入的旧 `litemall-vue` 前台在访问当前 ShopFlow 用户侧接口时，MUST 先基于 `appid` 建立可用的租户上下文，不能继续假设所有 `/wx/*` 请求只依赖单一登录 token。

#### Scenario: homepage bootstrap carries appid and receives tenant token

- **Given** 旧 `litemall-vue` 前台首次进入首页且本地尚未保存租户 token
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

### Requirement: Legacy litemall-vue frontend must map stored user token to the current ShopFlow user header

旧 `litemall-vue` 前台如果本地已经保存用户 token，MUST 按当前 ShopFlow 用户侧协议发送 `X-ShopFlow-User-Token`，而不是继续默认发送 `X-Litemall-Token`。

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

### Requirement: Legacy litemall-vue frontend must restore ordinary user account login without changing the current admin binding flow

旧 `litemall-vue` 登录页 MUST 恢复普通用户账号密码登录能力，但不能通过改写当前 `/wx/auth/login` 的管理员绑定语义来实现。

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

### Requirement: Legacy litemall-vue frontend must align registration, captcha, and password reset with the current wx auth APIs

旧 `litemall-vue` 前台的注册、短信验证码和找回密码流程 MUST 对齐当前 ShopFlow 用户侧真实可用的 `/wx/auth/*` 接口，而不是继续调用历史路径或停留在未实现状态。

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

