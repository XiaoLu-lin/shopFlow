# Design: rebuild-shopflow-h5-next-as-uniapp-multiplatform

## Overview

本次设计采用“直接升级 `shopflow-h5-next` 为 uni-app 多端工程，旧小程序并行保留”的策略。

关键原则：

- 不修改后端 `/wx/*` 协议
- 不继续以 `Vant` 作为多端主组件库
- 不把 Tailwind 作为小程序端主样式体系
- 页面尽量共用，平台差异集中到适配层
- 旧 H5 兼容协议继续可读写
- 旧 `shopflow-wx` 在新多端工程验收前继续保留

## Architecture

新工程目标架构：

- 页面层：`pages/*`
- 通用组件层：`components/*`
- 业务功能层：`features/*`
- 领域实体层：`entities/*`
- 共享层：`shared/*`
- 状态层：`stores/*`

推荐目录职责：

- `pages`：页面编排、生命周期、导航参数读取
- `components`：跨端基础组件与商城业务组件
- `features`：登录、购物车、结算、支付、个人资料等业务动作
- `entities`：商品、订单、用户、地址、分类等模型与 service
- `shared/request`：统一请求、错误映射、tenant bootstrap
- `shared/compat`：旧 token、旧本地键、旧 redirect 语义兼容
- `shared/platform`：平台能力适配
- `stores`：会话、UI 壳、轻量业务引用状态

## Component Strategy

组件体系固定为：

- `uView Plus`：移动端基础交互组件
- `sf-*` 自定义业务组件：商城领域组件
- `uni-app` 内置组件：基础渲染和容器能力

组件职责分层：

- `uView Plus` 负责：
  - 表单
  - 搜索
  - Tabs
  - Popup
  - Toast
  - Navbar
  - Empty
  - Loading
- `sf-*` 负责：
  - 商品卡片
  - 价格展示
  - SKU 选择
  - 地址卡片
  - 订单卡片
  - 页面壳
  - 底部结算栏

这样可以避免业务页面深度耦合第三方组件 API，后续换肤、主题统一和局部替换会更稳。

## Routing

新工程不继续使用 `vue-router`。

固定路由策略：

- 页面注册走 `pages.json`
- 页面跳转走 `shared/platform/navigation`
- tabBar 页统一走 `switchTab`
- 普通详情页走 `navigateTo`
- 登录态回跳通过 redirect 参数和导航封装统一处理

H5 旧 hash 链接兼容策略：

- 提供 H5-only 兼容入口解析旧路径
- 将旧路径映射为新的 uni-app 页面路径
- 保持未登录访问受保护页面时的 redirect 语义

## Compatibility Layer

兼容层继续保留以下协议：

- `Authorization`
- `ShopFlowTenantToken`
- `avatar`
- `nickName`
- `X-ShopFlow-User-Token`
- `X-ShopFlow-TenantId`
- `/wx/home/auth`

兼容层职责：

- 统一读写历史 localStorage / storage 键
- 统一 tenant bootstrap
- 统一用户 token header 注入
- 统一匿名请求与受保护请求的差异处理
- 统一登录后 redirect 恢复

## Platform Adapters

必须单独抽象的适配能力：

- `storage`
- `navigation`
- `payment`
- `share`
- `upload`
- `rich-text`

约束：

- 页面中不得散落大量平台分支判断
- 条件编译优先收敛在适配层或极少量端专属组件中
- 支付和分享链路必须为 H5 和小程序分别留验证入口

## State Strategy

状态管理固定为：

- `Pinia` 只保存客户端状态
- 服务端数据通过 `service + composable` 读取
- 不将分页列表、接口 loading 和临时错误态长期堆入全局 store

Pinia 典型内容：

- session
- tenant
- cart reference
- filters
- ui shell state

## Styling Strategy

样式策略固定为：

- 主样式：`SCSS + CSS Variables`
- H5 增强：`Tailwind CSS 3.4`
- 小程序端：class + SCSS + design tokens

视觉约束保持现阶段已确认结论：

- 白色 + 蓝色主基调
- 紧凑字体、间距、行高
- 卡片轻阴影
- 圆角小于 `15px`
- 不使用厚黑边框
- 表单和搜索统一组件化

## Migration Strategy

迁移顺序：

1. OpenSpec 建档
2. uni-app 工程壳搭建
3. request / compat / platform adapters
4. design tokens 与基础组件
5. 浏览域
6. 鉴权域
7. 交易域
8. 用户域
9. H5 旧链接兼容
10. 双端验证与切流准备

原因：

- 基础适配层先稳定，后面的页面迁移不会反复返工
- 浏览域可以先形成多端 UI 基座
- 交易和用户域依赖登录态、地址、支付等高风险能力，应后置

## Verification

验证分层：

- 单元测试：session adapter、tenant bootstrap、request client、导航映射、校验工具
- 契约验证：双 header、redirect、旧键兼容、首页预热
- H5 冒烟：首页、分类、搜索、详情、购物车、结算、个人中心
- 小程序冒烟：首页、分类、详情、购物车、登录、个人中心
- 人工重点链路：微信内打开、支付回跳、分享打开

## Rollout

切流策略：

- 旧 `shopflow-wx` 继续保留，直到 uni-app 小程序完成验收
- 旧 `shopflow-h5` 继续保留，直到 uni-app H5 完成验收
- 新多端工程先并行联调，再评估逐步切流

## Risks

- uni-app 与现有 Vite SPA 的页面结构差异较大，迁移成本真实存在
- `uView Plus` 需要项目层二次封装，否则业务页容易直接依赖第三方实现细节
- H5 和小程序在支付、分享、上传、富文本上的差异必须提前预留测试成本
