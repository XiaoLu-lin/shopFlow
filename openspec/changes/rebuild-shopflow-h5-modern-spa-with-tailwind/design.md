# Design: rebuild-shopflow-h5-modern-spa-with-tailwind

## Overview

本次重构采用“并行新目录 + 协议兼容不变 + 页面全量迁移”的策略。

关键设计原则：

- 不在旧 `shopflow-h5` 上直接升级 Vue 版本。
- 不改后端 `/wx/*` 协议，只重做前端消费方式。
- 把旧站协议兼容抽到独立层，不再散落到页面中。
- 用 `Tailwind 3.4` 做主样式层，保留少量 `scss` 作为过渡。
- 首版固定 `hash` 路由，保证旧链接可用。

## Application Architecture

新工程目录固定为：

- `src/app`：应用壳、providers、router
- `src/pages`：页面
- `src/widgets`：页面级复合块
- `src/features`：登录、购物车、下单等业务特征
- `src/entities`：商品、订单、地址、用户等领域对象
- `src/shared`：request、compat、config、ui、utils

状态分层固定为：

- `Pinia` 只负责客户端状态与跨页面 UI 状态。
- `Vue Query` 负责服务端数据、缓存、异步状态。
- 页面不直接操作 `window.localStorage`，统一经过 `session-adapter`。

## Compatibility Layer

兼容层是本次重构的第一优先级，必须先于业务页面稳定。

职责包括：

- 统一读取与写入历史键：
  - `Authorization`
  - `ShopFlowTenantToken`
  - `avatar`
  - `nickName`
- 统一完成租户预热与租户 token 持久化。
- 统一完成用户 token 到 `X-ShopFlow-User-Token` 的映射。
- 统一处理首页白名单、匿名请求和登录态缺失时的 header 行为。
- 统一兼容 `token` / `userToken` 返回格式。

## UI Foundation

新站 UI 基座采用：

- `Vant 4`：承接移动端成熟交互组件
- `Tailwind 3.4`：承接布局、栅格、间距、响应式与视觉原子类
- `CSS Variables`：承接品牌令牌

固定策略：

- 关闭 Tailwind `preflight`
- 不把 `@apply` 当主要写法
- 只为复杂品牌块保留 scoped `scss`
- Header、Tabbar、Hero、卡片、表单区块、空状态、Skeleton 做成项目级组件

视觉方向采用“轻奢编辑感移动商城”，以暖白底、胭脂粉品牌色、琥珀高光和大圆角容器为主。

## Migration Order

迁移顺序固定为：

1. 页面与接口矩阵
2. 兼容层与请求层
3. 应用壳、Header、Tabbar、路由
4. 浏览域
5. 鉴权域
6. 交易域
7. 用户域
8. CI、验证与切流准备

这样做的原因是：

- 浏览域最容易先形成新站视觉基调。
- 鉴权与交易域依赖兼容层，必须排在兼容层之后。
- 用户中心依赖登录态和多个下游模块，适合后置。

## Verification Strategy

验证分三层：

- 单元测试：兼容层、request、guard、校验工具
- 页面/组件测试：登录、地址、购物车、订单筛选、空态
- E2E：匿名浏览、登录注册找回、加购下单、订单查询、资料修改、退出登录

额外要求：

- 至少覆盖 Chromium 和 WebKit 手机视口
- 手工复核一次微信内打开、支付回跳、旧 hash 链接
