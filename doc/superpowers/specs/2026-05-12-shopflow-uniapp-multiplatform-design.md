# ShopFlow uni-app 多端改造技术方案

## 1. 背景

当前前台存在三条相关代码线：

- `shopflow-h5`：旧版 Vue 2 + Vant H5 前台，现已完成下线，仅保留历史归档记录。
- `shopflow-h5-next`：正在重建的 Vue 3 + Vite 6 + Vant 4 + Tailwind H5 工程。
- `shopflow-wx`：现有微信小程序工程，使用原生小程序结构，页面、请求、样式与 H5 独立维护。

新的目标是将 `shopflow-h5-next` 调整为 `uni-app` 多端主工程，使同一套前台业务可以同时发布为 H5 和微信小程序。后端接口、租户预热、登录态兼容和核心业务语义保持不变。

## 2. 目标

- 直接改造 `shopflow-h5-next`，使其成为新的 `uni-app` 多端工程。
- 首版同时支持 `H5` 和 `mp-weixin`。
- 保留当前 ShopFlow `/wx/*` 后端协议。
- 保留旧 H5 的关键兼容契约，包括登录态 key、租户 token、受保护路由 redirect 语义。
- 尽量复用当前 `shopflow-h5-next` 已沉淀的业务模型、API 类型、兼容层思路和页面矩阵。
- 后续继续以 `shopflow-h5-next` 承接 H5 主前台，并逐步吸收旧 `shopflow-wx` 的剩余能力。

## 3. 非目标

- 不改造后端 `/wx/*` 接口结构。
- 不引入 SSR、BFF、微前端或文件路由。
- 不继续以 `Vant` 作为多端主组件库。
- 不把 Tailwind 作为小程序端主样式体系。
- 不在首版追求所有页面 100% 视觉同构；优先保证业务同构和交互可用。
- 本方案不处理旧 `shopflow-wx` 的删除；旧 `shopflow-h5` 已在后续清理中下线。

## 4. 技术栈

推荐技术栈：

- `uni-app`
- `Vue 3`
- `TypeScript`
- `Vite`
- `Pinia`
- `uView Plus`
- `SCSS`
- `Tailwind CSS 3.4`，仅作为 H5 端增强，不作为小程序端主依赖
- `Vitest`，用于纯函数、兼容层、请求层测试
- 微信开发者工具，用于 `mp-weixin` 真机和模拟器验证

暂不建议继续保留：

- `Vue Router`：uni-app 页面路由由 `pages.json` 与 `uni.navigateTo` 等 API 管理。
- `Vant`：Vant 适合 H5，不适合作为微信小程序主组件体系。
- `@tanstack/vue-query` 作为基础依赖：多端兼容和运行时体积需要额外验证，首版用 `service + composable + Pinia` 更稳。

组件库选择结论：

- `uView Plus` 作为首版主组件库，承担表单、弹窗、搜索、列表、导航、加载、空状态等移动端基础能力。
- `uni-ui` 仅作为备选补充，不作为主组件体系，避免两个组件库同时主导页面风格。
- 商城强业务组件统一封装为 `sf-*`，页面优先依赖项目组件，而不是直接堆满三方组件 API。

## 5. 架构结论

采用“单工程多端，同业务层，轻平台适配”的架构。

页面尽量共用，但将平台差异收敛到 `platform adapters`：

- H5 与小程序共用页面主体、业务服务、状态、校验和领域模型。
- 登录、支付、分享、上传、存储、跳转等能力通过平台适配层隔离。
- 少数差异较大的页面允许使用条件编译或端专属组件，但必须先评估复用成本。

推荐分层：

- 页面层：负责页面布局、交互编排、生命周期调用。
- 业务层：负责登录、购物车、下单、用户资料等业务动作。
- 实体层：负责商品、订单、用户、地址等领域类型与格式化。
- 共享层：负责请求、兼容、平台适配、配置、工具函数。
- 样式层：负责设计令牌、跨端基础样式、H5 增强样式。

## 6. 目录结构

`shopflow-h5-next` 改造后的建议结构：

```text
shopflow-h5-next/
  package.json
  pages.json
  manifest.json
  src/
    App.vue
    main.ts
    pages/
      home/
      items/
      login/
      order/
      user/
    components/
      sf-button/
      sf-card/
      sf-empty/
      sf-goods-card/
      sf-page/
      sf-search/
      sf-sku/
    features/
      auth/
      cart/
      checkout/
      goods/
      payment/
      profile/
    entities/
      address/
      auth/
      cart/
      catalog/
      goods/
      order/
      user/
    shared/
      compat/
      config/
      platform/
      request/
      styles/
      utils/
    stores/
      session.ts
      cart.ts
      ui.ts
  tests/
    unit/
    contract/
```

说明：

- `pages.json`、`manifest.json` 的实际落位以 uni-app CLI 模板为准；若模板要求根目录配置，则不要强行放入 `src`。
- `pages.json` 成为页面注册、导航栏、分包和 tabBar 的主配置。
- `components` 使用 `easycom` 风格组织，优先做跨端基础组件。
- `shared/platform` 统一封装平台差异，不允许页面里散落大量 `#ifdef`。
- `shared/request` 继续承载租户预热、双 header、错误映射。
- `shared/compat` 继续承载旧 H5 登录态和缓存 key 兼容。

## 7. 路由设计

uni-app 不继续使用 `vue-router`。页面通过 `pages.json` 注册，跳转统一走封装后的 `navigate` 工具。

推荐映射示例：

| 当前 H5 路由 | uni-app 页面 |
| --- | --- |
| `/` | `/pages/home/index` |
| `/items` | `/pages/items/catalog/index` |
| `/items/search` | `/pages/items/search/index` |
| `/items/detail/:itemId` | `/pages/items/detail/index?id=:itemId` |
| `/login` | `/pages/login/index` |
| `/order` | `/pages/order/cart/index` |
| `/order/checkout` | `/pages/order/checkout/index` |
| `/user` | `/pages/user/index` |
| `/user/address/edit` | `/pages/user/address-edit/index` |

H5 旧链接兼容策略：

- 新增 H5-only 兼容入口，解析旧 `hash` 路径。
- 将旧路径转换为 uni-app 页面路径和 query。
- 未登录访问受保护页面时，继续进入登录页并保留 `redirect`。
- 微信小程序不承诺支持旧 hash 链接，但分享参数需要映射到同一业务页面。

## 8. 请求与兼容层

请求层必须保留以下协议：

- `Authorization`
- `ShopFlowTenantToken`
- `avatar`
- `nickName`
- `X-ShopFlow-User-Token`
- `X-ShopFlow-TenantId`
- `/wx/home/auth` 租户预热链路

推荐模块：

- `shared/request/client.ts`：封装 `uni.request`，统一 baseURL、header、错误提示和响应解析。
- `shared/request/bootstrap-tenant.ts`：处理 `appid + tenant` 预热。
- `shared/compat/session-adapter.ts`：统一读写旧登录态 key。
- `shared/platform/storage.ts`：H5 使用 `localStorage`，小程序使用 `uni.setStorageSync`。
- `shared/platform/navigation.ts`：封装 `navigateTo`、`redirectTo`、`switchTab`、`reLaunch`。
- `shared/platform/payment.ts`：隔离 H5 支付和微信小程序支付。
- `shared/platform/share.ts`：隔离 H5 分享链接和小程序分享卡片。

## 9. 状态管理

`Pinia` 只保存客户端状态：

- 用户会话
- 租户上下文
- UI 壳状态
- 购物车轻量引用
- 筛选条件
- 页面间临时参数

服务端数据通过业务 service 获取，不把接口 loading、列表结果和分页状态长期堆进 store。

推荐模式：

```text
pages -> features composables -> entities api/service -> shared request
```

例如：

- `features/auth/useLogin.ts`
- `features/cart/useCartActions.ts`
- `entities/goods/goods.service.ts`
- `entities/order/order.service.ts`

## 10. UI 与样式策略

多端主 UI 体系：

- 组件基础：`uView Plus + 自定义 sf-* 组件`
- 样式基础：`SCSS + CSS Variables`
- H5 增强：允许 Tailwind 参与布局和细节优化
- 小程序端：以 class + SCSS + 设计令牌为主

组件分工：

- `uView Plus`：负责通用移动端交互组件，如输入框、搜索框、表单校验、弹窗、Toast、Loading、Tabs、Navbar、Badge、Empty。
- `sf-*`：负责商城业务组件，如商品卡片、订单卡片、地址卡片、价格展示、SKU 选择、页面容器、结算栏。
- `uni-app` 内置组件：负责基础渲染能力，如 `view`、`text`、`image`、`swiper`、`scroll-view`、`button`。
- `uni-ui`：仅在 uView Plus 缺失或不适合时按需评估，不作为默认选择。

设计约束继续沿用当前用户反馈：

- 页面保持简洁，不做花哨装饰。
- 主风格为白色 + 蓝色。
- 商品卡片可以有轻阴影。
- 卡片圆角控制在 `15px` 以下，推荐 `8px` 到 `12px`。
- 字号、行高、边距要紧凑，避免大卡片和大留白。
- 表单组件优先使用统一组件，不使用散落原生输入框。
- 不使用厚黑边框。
- 搜索、输入、地址、反馈、下单备注等表单能力统一组件化。

推荐设计令牌：

```text
color.primary: #1677ff
color.primarySoft: #eaf3ff
color.textMain: #172033
color.textMuted: #6f7a8a
color.border: #e8edf5
color.background: #f6f8fb
radius.sm: 6px
radius.md: 10px
radius.lg: 14px
shadow.card: 0 6px 18px rgba(23, 32, 51, 0.08)
font.size.body: 14px
font.size.caption: 12px
lineHeight.body: 1.45
space.pageX: 12px
space.card: 12px
```

## 11. 页面迁移范围

首版必须覆盖以下页面域：

- 浏览域：首页、分类、搜索、商品详情、品牌、专题、拼团、热卖、新品。
- 鉴权域：登录、注册、验证码、找回密码。
- 交易域：购物车、结算、支付、支付结果、订单详情。
- 用户域：个人中心、订单列表、优惠券、退款、收藏、地址、资料设置、反馈、帮助、服务页。

迁移顺序：

1. 工程壳层和编译配置。
2. 兼容层、请求层、平台适配层。
3. 设计令牌和基础组件。
4. 浏览域页面。
5. 鉴权域页面。
6. 交易域页面。
7. 用户域页面。
8. H5 旧链接兼容和小程序分享参数。
9. 双端验证和切流准备。

## 12. 平台差异处理

必须平台适配的能力：

| 能力 | H5 | 微信小程序 | 处理方式 |
| --- | --- | --- | --- |
| 存储 | `localStorage` | `uni.*StorageSync` | `platform/storage` |
| 路由 | H5 URL / hash 兼容 | 小程序页面栈 | `platform/navigation` |
| 登录 | 账号密码/手机号 | 账号体系 + 微信能力可扩展 | `features/auth` |
| 支付 | H5 pay / 跳转回调 | `uni.requestPayment` | `platform/payment` |
| 分享 | URL 分享 | `onShareAppMessage` | `platform/share` |
| 图片上传 | file input / H5 upload | `uni.chooseImage` + upload | `platform/upload` |
| 富文本 | H5 HTML | 小程序 rich-text 限制 | `components/sf-rich-text` |
| 安全区域 | CSS env | 小程序 safe area | `components/sf-page` |

## 13. 测试与验证

单元测试：

- session adapter
- tenant bootstrap
- request client
- route redirect mapper
- 商品价格、SKU、订单金额、表单校验等纯函数

契约测试：

- 首次访问首页触发租户预热。
- 已登录请求自动带 `X-ShopFlow-User-Token` 和 `X-ShopFlow-TenantId`。
- 未登录访问受保护页面跳登录并保留 redirect。
- 登录成功后旧缓存 key 仍可读写。

H5 验证：

- 构建 H5 产物。
- 首页、分类、搜索、详情、购物车、结算、个人中心主链路可访问。
- 旧 hash 链接进入兼容入口后能跳转新页面。

微信小程序验证：

- 微信开发者工具可编译。
- 首页、分类、详情、购物车、登录、个人中心可访问。
- 图片、tabBar、页面标题、分享入口正常。
- 支付链路先做模拟和接口可达验证，真支付在联调环境回归。

建议命令：

```bash
npm run dev:h5
npm run build:h5
npm run dev:mp-weixin
npm run build:mp-weixin
npm run test
```

## 14. 迁移阶段

### Phase 1：OpenSpec 变更建档

- 创建新 change，建议名称：`rebuild-shopflow-h5-next-as-uniapp-multiplatform`
- 写 proposal、design、tasks、spec delta。
- 将本方案作为上游设计依据。
- 已停止旧 `shopflow-h5` 重建线的后续 UI 投入。

### Phase 2：工程切换

- 将 `shopflow-h5-next` 的 Vite SPA 配置替换为 uni-app CLI 配置。
- 按 uni-app CLI 模板新增或调整 `pages.json`、`manifest.json`、编译脚本和平台配置。
- 保留可复用的 `entities`、`features`、`shared/compat`、`shared/request` 思路。
- 移除或隔离 `vue-router`、Vant、Vue Query 相关代码。

### Phase 3：基础能力

- 完成 platform adapters。
- 完成 request client。
- 完成 session adapter。
- 完成基础组件和设计令牌。
- 完成 tabBar、页面壳、登录守卫。

### Phase 4：页面迁移

- 先迁移浏览域，保证匿名浏览体验。
- 再迁移鉴权域，保证登录和注册。
- 再迁移交易域，保证购物车、结算、支付结果。
- 最后迁移用户域，保证订单、地址、收藏、反馈等能力。

### Phase 5：双端验证与切流

- H5 与微信小程序分别构建。
- 对主链路做双端冒烟。
- 对微信内 H5 和微信小程序分别验证登录、支付回跳、分享打开。
- 验收通过后继续评估旧 `shopflow-wx` 的下线节奏。

## 15. 风险

- uni-app 路由模型和当前 `vue-router` 差异较大，页面跳转和 redirect 需要重新设计。
- Vant 页面不能直接作为多端页面复用，需要迁移到 `uView Plus` 或自定义跨端组件。
- Tailwind 在小程序端兼容成本较高，首版只能作为 H5 增强。
- 小程序支付、分享、上传和富文本能力与 H5 不同，必须端内验证。
- 当前 `shopflow-h5-next` 已投入的 UI 代码会有一部分被重写，应优先复用业务逻辑和接口封装。
- 原生 `shopflow-wx` 页面较多，切流前要确认页面是否全部被新 uni-app 覆盖。

## 16. 推荐结论

可以将 `shopflow-h5-next` 改造为 `uni-app` 多端工程，技术路线建议固定为：

```text
uni-app + Vue 3 + TypeScript + Vite + Pinia + uView Plus + SCSS + H5 Tailwind enhancement
```

这条路线比继续维护独立 H5 和原生小程序更适合长期演进。核心取舍是接受一次组件体系迁移，换取后续 H5 与小程序主业务的一套代码维护。

## 17. 参考

- uni-app Vue 3 说明：<https://en.uniapp.dcloud.io/tutorial/vue3-basics.html>
- uni-app CLI 创建工程：<https://uniapp.dcloud.net.cn/quickstart-cli.html>
- uni-app TypeScript 支持：<https://en.uniapp.dcloud.io/tutorial/typescript-subject.html>
- 旧 H5 重建相关设计稿与页面矩阵已随旧工程下线一并移除。
