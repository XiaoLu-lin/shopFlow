# Design: Extend H5 Next Suji Style To Entry And Auth Pages

## Scope

本次只处理：

- `pages/items/catalog`
- `pages/items/detail`
- `pages/login`
- `pages/login/forget`
- `pages/login/register-get-code`

## Decisions

### 1. 登录链路不补旧 H5 多页路由

旧 H5 的注册与找回密码采用多页拆分，但新 H5 Next 当前只暴露单页路由。为了保持最小改动和当前工程结构一致，本次直接在现有路由内收成单页流程，不新增 `register-submit`、`register-status`、`forget-reset`、`forget-status` 页面。

### 2. 商品入口页与商品详情页沿用现有数据层

`catalog` 继续使用 `entities/catalog/api.ts`，`detail` 继续使用 `entities/goods/api.ts` 与现有 `sku-utils`。本次只调整层级、文案和样式，不改数据协议。

### 3. 新增 auth 表单辅助层

登录、注册、找回密码都属于轻表单场景，适合抽出一层展示和行为辅助，统一：

- hero 文案
- 字段文案
- 验证码倒计时
- 提交中状态
- 成功后的保守跳转策略

这样可以避免三个页面各自散落校验和倒计时代码。

## Risks

- 登录链路属于真实入口，若重构时处理不好，会影响 redirect 和登录成功落地。
- 商品详情页涉及加购和立即购买，不能因样式改造破坏规格选择和底部操作。
- 注册与重置密码接口虽然已存在，但之前页面未完整接通，需要重点验证 toast、倒计时与成功跳转。
