# Design: align-litemall-vue-tenant-user-headers

## Overview

本次改动覆盖 `litemall-vue` 和 `shopflow-wx-api`，不修改数据库结构，也不调整 `shopflow-core` 现有租户过滤和 token 协议。

整体策略：

- 保持后端当前“用户 token 与租户 token 分离”的协议不变。
- 保持当前 `/wx/auth/login` 的管理员绑定语义不变，避免影响小程序现有流程。
- 在旧前台增加一层轻量级租户初始化与请求头映射层。
- 在用户侧后端补一个面向旧 H5 的普通用户账号登录兼容入口，而不是反向让现有绑定接口兼容旧前台。
- 对旧前台注册、验证码、找回密码页面只做最小协议对齐和必要页面补线，不顺手做无关重构。

## Tenant Bootstrap

旧前台直接请求 `/wx/home/index`，但当前后端首页链路要求前台提供 `appid` 并生成租户 token。

本次兼容方案：

- 为前台增加一个可配置的 `appid` 读取入口，优先从环境配置读取。
- 首页初始化时，调用 `/wx/home/index` 时显式带上 `appid`。
- 读取首页返回中的 `tenantId` 字段，将其视为当前租户 token 并保存到本地。
- 当首页尚未成功返回租户 token 时，请求层允许优先调用 `/wx/home/auth` 进行租户预热。
- 登录、注册、验证码、找回密码等旧 H5 鉴权请求也复用同一套租户预热逻辑，避免鉴权页首包直接被租户过滤器拦截。

这样可以复用后端现有 `WxHomeController.index` 与 `WxTenantService.getTenant(appid)` 行为，不新增额外的租户兼容接口。

## Request Header Compatibility

旧前台当前只会从 localStorage 的 `Authorization` 中读取 token，并以 `X-Litemall-Token` 发送。

本次兼容方案：

- 移除旧头 `X-Litemall-Token` 的默认注入逻辑。
- 请求层统一读取本地 `Authorization`，若存在则发送 `X-ShopFlow-User-Token`。
- 请求层统一读取本地租户 token，若存在则发送 `X-ShopFlow-TenantId`。
- 对首页初始化与租户预热接口允许不带租户头发起首包请求。
- 对登录成功后的 token 回写统一走兼容工具，避免页面各自拼装。

这样可以兼容当前后端的：

- `LoginUserArgumentResolver` 对 `X-ShopFlow-User-Token` 的解析；
- `TenantContextHolder` / `TenantFilter` 对 `X-ShopFlow-TenantId` 的解析。

## Legacy Account Login Compatibility

当前 `/wx/auth/login` 语义实际是“已登录用户绑定管理员账户”，并要求：

- `@LoginUser` 用户上下文；
- 请求体包含 `username`、`password`、`code`。

旧 H5 登录页实际需要的是“普通用户账号密码登录”，并期望成功后返回用户信息和用户 token。

本次兼容方案：

- 在 `shopflow-wx-api` 新增一个旧 H5 专用普通用户登录接口。
- 该接口只面向 C 端用户账号登录，不复用管理员绑定语义。
- 登录匹配优先支持当前后端稳定具备的数据维度：
  - `mobile`
  - `username`
- 明确不承诺恢复旧 H5 `email` 登录分支；前台若输入邮箱，将视为当前不支持的历史分支并返回可理解错误。
- 接口成功后返回结构对齐旧前台消费方式，同时保留与 ShopFlow 当前 `userToken` 语义一致的内容。

这样做的原因是：

- 可以避免破坏小程序管理员绑定链路；
- 可以把旧前台兼容范围隔离在单独入口，便于后续下线或继续迁移；
- 可以直接复用 `TokenManager.createUserToken`、`WxUserService` 和现有用户状态校验逻辑。

## Registration And Reset Alignment

旧前台注册、找回密码和验证码逻辑与当前后端存在三类偏差：

1. 接口路径偏差
   - 旧前台使用 `/auth/regCaptcha`、`/auth/captcha`
   - 当前后端实际提供的是 `/wx/auth/captcha/mobile` 和 `/wx/auth/captcha/mail`

2. 参数偏差
   - 当前短信验证码接口通过 `@JsonBody String mobile` 读取 body 中的 `mobile` 字段。
   - 当前注册接口实际只要求 `mobile`、`password`、`code`，不会消费旧前台额外的 `repeatPassword`、`username`。

3. 页面实现偏差
   - 旧前台 `forget` 页仅做路由跳转，没有真实验证码发送和参数传递。
   - `forget-reset` 页面 `submitCode()` 为空实现，无法完成密码重置。

本次兼容方案：

- 在 `litemall-vue` 统一把注册验证码、通用短信验证码适配到 `/auth/captcha/mobile`。
- 注册提交时仅发送后端实际需要的字段，并保留前端本地二次密码校验。
- 找回密码流程改为：
  - 第一步发送手机验证码并把 `mobile + code` 传给下一页；
  - 第二步提交 `mobile + code + password` 调用 `/auth/reset`。
- 用户中心里的改密/改手机号页面继续沿用统一验证码发送兼容入口，避免和登录页分叉。

## Response Compatibility

旧前台页面大量假设登录成功后拿到的是：

- `data.userInfo`
- `data.token`

而当前 ShopFlow 用户侧接口普遍使用：

- `data.userInfo`
- `data.userToken`

本次兼容方案：

- 登录兼容入口返回时优先对齐旧前台可消费结构。
- 前台兼容层同时兼容 `token` 与 `userToken` 两种字段读取，统一落到本地 `Authorization`。
- 请求层继续把本地 `Authorization` 映射为 `X-ShopFlow-User-Token`，减少页面级分支。

## AppId Source Strategy

为避免把环境差异写死在页面里，`appid` 采用前台配置优先策略：

- 优先从 `litemall-vue` 环境变量读取专用配置。
- 若未显式配置，则使用当前已验证可用的默认值作为开发联调兜底。

该策略只解决当前本地联调和最小兼容，不替代正式环境配置治理。正式部署时仍应提供真实的租户或默认 `appid` 配置。

## Failure Handling

- 当 `appid` 缺失或首页初始化返回失败时，前台应保留明确错误提示，不静默吞掉后端初始化失败信息。
- 当本地没有用户 token 时，请求层不发送空的 `X-ShopFlow-User-Token`。
- 当本地没有租户 token 时，请求层可自动做一次租户预热；若预热仍失败，应向页面返回原始失败信息。
- 当用户输入邮箱尝试旧 H5 登录时，前台或兼容接口应返回明确的“不支持当前登录方式”提示，而不是落到模糊的授权错误。
- 当登录成功返回字段与旧前台消费结构不一致时，由兼容层做统一归一，不把页面暴露给多套返回格式。

## Verification

- 首页请求应能够携带 `appid` 并成功返回首页数据。
- 首页成功后，本地应持久化租户 token。
- 后续兼容接口请求应自动带上 `X-ShopFlow-TenantId`。
- 已有本地用户 token 的情况下，请求应自动带上 `X-ShopFlow-User-Token`。
- 旧 H5 登录页应能够用兼容支持的账号类型完成普通用户登录。
- 旧 H5 注册页应能够发送短信验证码并完成注册。
- 旧 H5 找回密码页应能够发送验证码并完成密码重置。
- 当前“未找到授权租户”和 `A0223` 不应再出现在旧 H5 普通登录首链路中。
- 小程序原有 `/wx/auth/login` 管理员绑定流程不应因本次改动被破坏。
