# ShopFlow H5 Next 入口与登录链路批次 D 设计稿

## 背景

`shopflow-h5-next` 当前商品流页面、订单页和大部分用户子页已经统一到已确认的灰蓝 ShopFlow / Suji-like 方向，但仍有一组高频入口页面停留在早期迁移状态：

- `pages/items/catalog`
- `pages/items/detail`
- `pages/login`
- `pages/login/forget`
- `pages/login/register-get-code`

其中分类入口页和商品详情页虽然功能可用，但视觉层级与新批次商品流不连续；找回密码页与注册页则仍是迁移占位状态，已经影响整套 H5 Next 的完成度。

用户已确认继续沿用单一 `A` 方向，并要求沿用既有协作方式：先出本地预览，确认后再实现真实页面。

## 已确认结论

- 对外品牌继续保持 `ShopFlow`。
- 继续沿用灰蓝主色、白底、`8px` 圆角、小 hero、紧凑间距。
- 不再提供多套主题方案，本批直接沿用已确认的单一 `A` 方向。
- 不新增登录、注册、找回密码的额外路由页；在当前 `login / forget / register-get-code` 路由内收成正式单页流程。
- 不改现有接口、登录态写入方式、redirect 语义、分类跳转和商品详情购买链路。

## 页面范围

本批范围固定为：

- 分类入口页 `pages/items/catalog`
- 商品详情页 `pages/items/detail`
- 登录页 `pages/login`
- 找回密码页 `pages/login/forget`
- 注册页 `pages/login/register-get-code`

本批不纳入：

- 首页 `pages/home`
- 支付、订单、个人中心等已经统一的页面
- 登录成功 / 注册成功 / 重置成功的独立结果页
- 后端鉴权协议、接口参数、tenant 预热逻辑

## 结构方案

### 分类入口页

- 保留左侧分类导航 + 右侧内容区的基本交互模型。
- 顶部搜索入口收紧成与商品流页一致的轻搜索条。
- 右侧当前分类头图缩轻成小 banner，不再让 hero 占据过重面积。
- 子分类宫格继续保留，但统一到 `8px` 圆角、小卡片、轻阴影体系。

### 商品详情页

- 保留顶部 `商品 / 评价 / 详情` 的语义，但整体头部更轻，避免与旧 H5 详情页风格混杂。
- 轮播、价格、标题、规格、服务说明、底部购买栏继续存在，但统一成当前商品流批次的卡片语言。
- 不新增伪造的销量、评价数、会员标识等字段。
- 规格选择仍沿用当前 `sku-utils`、`detail-display-utils` 与加购 / 立即购买逻辑。

### 登录页

- 保留“账号 + 密码 + redirect”结构。
- 从“早期基线页”升级为更正式的轻表单页，但保持克制，不做大块重色 Hero。
- 登录成功仍沿用 `persistLegacyLoginSession` 和 `redirectAfterLogin`。

### 找回密码页

- 不再沿用旧 H5 的“两步页面”拆分。
- 当前路由页直接收成单页流程：
  - 手机号
  - 验证码
  - 新密码
  - 确认密码
- 沿用现有 `requestResetCaptcha` 和 `submitPasswordReset` 接口。

### 注册页

- 同样不再拆分旧 H5 的 `register-getCode -> register-submit -> register-status` 多页流程。
- 当前路由页直接收成单页流程：
  - 手机号
  - 验证码
  - 用户名
  - 密码
  - 确认密码
- 沿用现有 `requestRegisterCaptcha` 和 `submitRegister` 接口。

## 复用策略

- 分类入口页优先复用 `entities/catalog/api.ts`，不改数据结构。
- 商品详情页继续复用：
  - `entities/goods/api.ts`
  - `features/goods/detail-display-utils.ts`
  - `features/goods/sku-utils.ts`
- 登录链路继续复用：
  - `entities/auth/api.ts`
  - `features/auth/use-uni-login-form.ts`
  - `shared/compat/session-adapter.ts`
  - `shared/platform/navigation.ts`

## 数据与降级

- 分类页若无子分类图，继续使用占位块，不新增运营态图片文案。
- 商品详情页若无图文详情，保持当前“详情整理中”降级语义，但风格统一。
- 注册与找回密码流程中不引入独立成功页，成功后直接 toast + 跳回登录页或完成后跳转登录。
- 表单错误提示统一使用当前 `uni.showToast` 语义，不新增复杂弹窗。

## 验证目标

- `cd shopflow-h5-next && pnpm vitest run src/entities/auth/api.test.ts src/features/goods/detail-display-utils.test.ts src/features/goods/sku-utils.test.ts`
- `cd shopflow-h5-next && pnpm type-check`
- H5 本地检查：
  - `#/pages/items/catalog/index`
  - `#/pages/items/detail/index?id=<id>`
  - `#/pages/login/index`
  - `#/pages/login/forget/index`
  - `#/pages/login/register-get-code/index`
