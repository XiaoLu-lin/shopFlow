# ShopFlow uni-app Multiplatform Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 `shopflow-h5-next` 从当前 Vite 单端 H5 工程升级为同时支持 H5 与微信小程序的 `uni-app` 多端主工程，并保留现有 ShopFlow `/wx/*` 协议与关键兼容契约。

**Architecture:** 我们先固定多端工程壳和平台适配层，再迁移共享业务层与组件体系，最后分域迁移页面并完成双端验证。旧 `shopflow-h5` 与旧 `shopflow-wx` 在新工程验收前继续并行保留，不在本计划中直接下线。

**Tech Stack:** `uni-app`、`Vue 3`、`TypeScript`、`Pinia`、`uView Plus`、`SCSS`、`Tailwind CSS 3.4`（H5 only enhancement）、`Vitest`

---

## Scope

本计划覆盖：

- `shopflow-h5-next` 工程壳切换
- 多端平台适配层
- 组件体系切换
- 共享业务层迁移
- 浏览域、鉴权域、交易域、用户域页面迁移
- H5 旧 hash 链接兼容
- H5 / 微信小程序双端验证

本计划不覆盖：

- 旧 `shopflow-h5` 删除
- 旧 `shopflow-wx` 删除
- 后端 `/wx/*` 接口结构调整

## File Map

核心将触达的文件与目录：

- Modify: `shopflow-h5-next/package.json`
- Modify or Replace: `shopflow-h5-next/src/main.ts`
- Modify or Replace: `shopflow-h5-next/src/App.vue`
- Create or Replace by uni-app template: `shopflow-h5-next/pages.json`
- Create or Replace by uni-app template: `shopflow-h5-next/manifest.json`
- Create or Replace by uni-app template: `shopflow-h5-next/vite.config.ts`
- Create: `shopflow-h5-next/src/shared/platform/storage.ts`
- Create: `shopflow-h5-next/src/shared/platform/navigation.ts`
- Create: `shopflow-h5-next/src/shared/platform/payment.ts`
- Create: `shopflow-h5-next/src/shared/platform/share.ts`
- Create: `shopflow-h5-next/src/shared/platform/upload.ts`
- Modify: `shopflow-h5-next/src/shared/compat/session-adapter.ts`
- Modify: `shopflow-h5-next/src/shared/compat/tenant.ts`
- Modify: `shopflow-h5-next/src/shared/request/client.ts`
- Create: `shopflow-h5-next/src/components/sf-page/`
- Create: `shopflow-h5-next/src/components/sf-goods-card/`
- Create: `shopflow-h5-next/src/components/sf-search/`
- Create: `shopflow-h5-next/src/components/sf-price/`
- Create: `shopflow-h5-next/src/components/sf-empty/`
- Modify or Replace: `shopflow-h5-next/src/pages/**`
- Create: `shopflow-h5-next/src/stores/session.ts`
- Create: `shopflow-h5-next/src/stores/tenant.ts`
- Create: `shopflow-h5-next/src/stores/ui.ts`
- Modify: `shopflow-h5-next/src/entities/**`
- Modify: `shopflow-h5-next/src/features/**`
- Modify: `shopflow-h5-next/src/shared/ui/tokens.css`
- Modify: `shopflow-h5-next/src/shared/ui/tokens.ts`
- Create: `shopflow-h5-next/src/shared/utils/legacy-route-map.ts`
- Modify: `shopflow-h5-next/src/test/**`

## Phase Breakdown

### Phase 1: 工程壳切换

目标：

- 把 `shopflow-h5-next` 从 Vite H5 SPA 调整为可编译 H5 与微信小程序的 uni-app 工程
- 保留现有可复用的 `entities`、`features`、`shared/compat`、`shared/request` 思路

任务：

- [ ] 审核 `shopflow-h5-next` 当前目录中需要保留的共享业务文件
- [ ] 引入 uni-app CLI 所需依赖和脚本
- [ ] 新增或替换 `pages.json`、`manifest.json`
- [ ] 调整入口文件为 uni-app 启动方式
- [ ] 清理 `vue-router` 入口依赖，改由 `pages.json` 管理页面

建议修改文件：

- `shopflow-h5-next/package.json`
- `shopflow-h5-next/vite.config.ts`
- `shopflow-h5-next/src/main.ts`
- `shopflow-h5-next/src/App.vue`
- `shopflow-h5-next/pages.json`
- `shopflow-h5-next/manifest.json`

验证命令：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm install
npm run build:h5
npm run build:mp-weixin
```

检查点：

- H5 构建入口存在
- 微信小程序构建入口存在
- 工程能输出基础空壳页面

### Phase 2: 平台适配层与兼容层

目标：

- 固定 storage、navigation、payment、share、upload 等平台差异边界
- 保留旧 H5 登录态键、租户预热、双 header、redirect 语义

任务：

- [ ] 抽出 `shared/platform/storage.ts`
- [ ] 抽出 `shared/platform/navigation.ts`
- [ ] 抽出 `shared/platform/payment.ts`
- [ ] 抽出 `shared/platform/share.ts`
- [ ] 抽出 `shared/platform/upload.ts`
- [ ] 迁移 `session-adapter` 到多端 storage
- [ ] 迁移 `tenant bootstrap` 到 uni-app 请求模型
- [ ] 调整 `request client` 走统一请求封装
- [ ] 新增 H5 旧 hash 路由映射工具

建议修改文件：

- `shopflow-h5-next/src/shared/platform/*.ts`
- `shopflow-h5-next/src/shared/compat/session-adapter.ts`
- `shopflow-h5-next/src/shared/compat/tenant.ts`
- `shopflow-h5-next/src/shared/request/client.ts`
- `shopflow-h5-next/src/shared/utils/legacy-route-map.ts`

验证命令：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run test -- session-adapter
npm run test -- tenant
npm run test -- client
```

检查点：

- 首页预热能写入 tenant token
- 已登录请求自动带 `X-ShopFlow-User-Token`
- 已登录请求自动带 `X-ShopFlow-TenantId`
- 未登录访问受保护页面保留 redirect

### Phase 3: 组件基座与设计令牌

目标：

- 用 `uView Plus + sf-*` 建立统一多端组件基座
- 保留紧凑白蓝风格和 H5 端 Tailwind 增强能力

任务：

- [ ] 接入 `uView Plus`
- [ ] 固定全局 design tokens
- [ ] 建立页面壳 `sf-page`
- [ ] 建立业务商品卡 `sf-goods-card`
- [ ] 建立搜索组件 `sf-search`
- [ ] 建立价格组件 `sf-price`
- [ ] 建立空状态组件 `sf-empty`
- [ ] 统一表单组件策略，避免业务页面散落原生输入框

建议修改文件：

- `shopflow-h5-next/src/shared/ui/tokens.css`
- `shopflow-h5-next/src/shared/ui/tokens.ts`
- `shopflow-h5-next/src/components/sf-page/*`
- `shopflow-h5-next/src/components/sf-goods-card/*`
- `shopflow-h5-next/src/components/sf-search/*`
- `shopflow-h5-next/src/components/sf-price/*`
- `shopflow-h5-next/src/components/sf-empty/*`

验证命令：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run build:h5
npm run build:mp-weixin
```

检查点：

- 商品卡、搜索框、空状态、页面壳双端均可渲染
- 圆角、字号、间距符合现有 UI 约束

### Phase 4: 浏览域迁移

目标：

- 先跑通匿名浏览主链路

范围：

- 首页
- 分类
- 搜索
- 商品详情
- 品牌
- 专题
- 拼团
- 热卖
- 新品

任务：

- [ ] 迁移首页页面结构与 banner / swiper
- [ ] 迁移分类页和分类商品列表
- [ ] 迁移搜索页和搜索历史
- [ ] 迁移商品详情、SKU、收藏、加购
- [ ] 迁移品牌、专题、拼团、热卖、新品列表页

建议修改文件：

- `shopflow-h5-next/src/pages/home/**`
- `shopflow-h5-next/src/pages/items/**`
- `shopflow-h5-next/src/entities/goods/**`
- `shopflow-h5-next/src/entities/catalog/**`
- `shopflow-h5-next/src/features/goods/**`

验证命令：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run build:h5
npm run build:mp-weixin
```

人工验证：

- H5：`/`、分类、搜索、详情可访问
- 小程序：首页、分类、详情可访问

### Phase 5: 鉴权域迁移

目标：

- 跑通登录、注册、验证码、找回密码链路

任务：

- [ ] 迁移登录页
- [ ] 迁移注册手机号录入、提交、结果页
- [ ] 迁移找回密码验证码、重置、结果页
- [ ] 接入 redirect 恢复逻辑

建议修改文件：

- `shopflow-h5-next/src/pages/login/**`
- `shopflow-h5-next/src/features/auth/**`
- `shopflow-h5-next/src/entities/auth/**`

验证命令：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run test
npm run build:h5
npm run build:mp-weixin
```

人工验证：

- H5 登录成功后跳回原目标页
- 注册和找回密码全链路可执行
- 小程序登录页和找回密码页能正常走接口

### Phase 6: 交易域迁移

目标：

- 跑通购物车、结算、支付结果主链路

任务：

- [ ] 迁移购物车
- [ ] 迁移结算页
- [ ] 迁移支付页
- [ ] 迁移支付结果页
- [ ] 对接平台 payment adapter

建议修改文件：

- `shopflow-h5-next/src/pages/order/**`
- `shopflow-h5-next/src/entities/cart/**`
- `shopflow-h5-next/src/entities/order/**`
- `shopflow-h5-next/src/features/cart/**`
- `shopflow-h5-next/src/features/order/**`
- `shopflow-h5-next/src/shared/platform/payment.ts`

验证命令：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run test
npm run build:h5
npm run build:mp-weixin
```

人工验证：

- 购物车数量与勾选逻辑正常
- 结算页地址、备注、金额展示正常
- 支付结果页回跳正确

### Phase 7: 用户域迁移

目标：

- 跑通用户资料与订单后链路

范围：

- 个人中心
- 订单列表与详情
- 优惠券
- 收藏
- 地址
- 资料设置
- 反馈
- 帮助
- 服务页
- 退款页兼容占位

任务：

- [ ] 迁移个人中心入口页
- [ ] 迁移订单列表和详情
- [ ] 迁移地址列表与地址编辑
- [ ] 迁移资料设置、昵称、手机号、密码修改
- [ ] 迁移收藏、优惠券、反馈、帮助、服务页

建议修改文件：

- `shopflow-h5-next/src/pages/user/**`
- `shopflow-h5-next/src/entities/user/**`
- `shopflow-h5-next/src/features/profile/**`

验证命令：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run test
npm run build:h5
npm run build:mp-weixin
```

人工验证：

- 个人中心主入口可访问
- 地址新增编辑可用
- 昵称、手机号、密码修改可用

### Phase 8: H5 旧链接兼容与双端收尾

目标：

- 完成 H5 历史链接映射和双端收尾验证

任务：

- [ ] 接入旧 hash 路由兼容入口
- [ ] 完成关键旧链接映射表
- [ ] 完成 H5 和小程序双端冒烟
- [ ] 产出 review 与切流前置条件

建议修改文件：

- `shopflow-h5-next/src/shared/utils/legacy-route-map.ts`
- `openspec/changes/rebuild-shopflow-h5-next-as-uniapp-multiplatform/review.md`

验证命令：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run test
npm run build:h5
npm run build:mp-weixin
```

人工验证：

- 旧 H5 收藏链接与分享链接可映射
- 微信内 H5 登录、支付回跳、分享打开链路回归一次
- 微信小程序首页、详情、购物车、个人中心回归一次

## Parallelization Strategy

可以并行：

- Phase 2 中 `storage`、`navigation`、`share`、`upload` 适配层
- Phase 3 中 `sf-page`、`sf-goods-card`、`sf-search`、`sf-empty`
- Phase 4 中品牌、专题、热卖、新品、拼团页面
- Phase 7 中帮助、服务页、反馈、优惠券、收藏等低耦合页面

必须串行：

- 工程壳切换
- request / compat 核心链路
- 登录 redirect 语义
- payment adapter
- H5 旧链接映射总入口

## Checkpoints

Checkpoint A：

- uni-app 工程壳可构建 H5 与小程序
- 平台适配层基础文件齐全

Checkpoint B：

- 浏览域和鉴权域可运行
- 登录态与 tenant 兼容通过

Checkpoint C：

- 交易域和用户域主链路可运行
- 支付结果和地址能力稳定

Checkpoint D：

- H5 与小程序主链路冒烟完成
- OpenSpec review 与切流条件齐全

## Verification Matrix

- 单元测试：`session-adapter`、`tenant`、`request client`、校验工具、金额与 SKU 纯函数
- H5 构建：`npm run build:h5`
- 小程序构建：`npm run build:mp-weixin`
- H5 冒烟：首页、分类、搜索、详情、购物车、结算、用户中心
- 小程序冒烟：首页、分类、详情、购物车、登录、用户中心
- 手工重点验证：微信内 H5 登录、支付回跳、分享打开

## Self-Review

- 覆盖检查：已覆盖工程切换、组件体系、兼容层、平台适配、页面域迁移、H5 旧链接兼容与双端验证。
- 占位检查：未使用 `TODO`、`TBD`、`后续补充` 这类占位词。
- 一致性检查：组件体系、样式策略、并行保留旧 H5 / 旧小程序、`uView Plus` 选型均与当前设计稿和 OpenSpec change 一致。
