# Tasks: rebuild-shopflow-h5-next-as-uniapp-multiplatform

更新日期：2026-05-13

## 1. OpenSpec 与工程基线

- [x] 创建本次多端改造 change 的 proposal、design、tasks。
- [x] 固定 `shopflow-h5-next` 的目标栈为 `uni-app + Vue 3 + TypeScript + Pinia + uView Plus + SCSS`。
- [x] 固定 `Tailwind CSS 3.4` 仅用于 H5 端增强，小程序端不作为主样式体系。
- [x] 明确旧 `shopflow-h5`、旧 `shopflow-wx` 在切流前继续并行保留。

验收契约：

- 多端主工程 MUST 以 `shopflow-h5-next` 为承载目录。
- 组件体系 MUST 不再以 `Vant` 作为多端主基座。
- 切流前不得默认删除旧 H5 或旧小程序实现。

## 2. 兼容层与平台适配

- [x] 落地 `session-adapter`，兼容旧登录态、本地存储键和租户 token。
- [x] 落地 `tenant bootstrap` 与双 header 注入逻辑。
- [x] 落地 `storage`、`navigation` 等平台适配基础能力。
- [x] 完成小程序请求层适配，替换掉运行时不兼容的 `axios adapter` 依赖。
- [x] 修复小程序端 `WXSS`、`URLSearchParams`、header 编码、相对 baseURL 等运行时兼容问题。

验收契约：

- 首次首页访问 MUST 能完成 `appid + tenant` 预热。
- 已登录请求 MUST 自动带 `X-ShopFlow-User-Token` 与 `X-ShopFlow-TenantId`。
- 小程序开发版 SHALL 使用可执行的完整请求地址，不得保留相对 `/wx` 导致 `invalid url`。

## 3. 页面迁移

- [x] 浏览域：首页、分类、搜索、商品详情、热卖、新品、拼团、品牌、专题完成迁移。
- [x] 鉴权域：登录、注册、找回密码完成迁移。
- [x] 交易域：购物车、结算、支付、支付结果、订单详情完成迁移。
- [x] 用户域：个人中心、订单列表、地址、收藏、优惠券、售后列表、售后详情、售后申请、资料设置、帮助、服务、反馈完成迁移。
- [x] H5 与小程序共用页面主壳已经可运行。

验收契约：

- 浏览、鉴权、交易、用户四大域 MUST 具备主链路可访问页面。
- 用户中心 MUST 可进入地址、订单、资料与售后相关页面。
- 购物车到结算页的交易链路 SHALL 可完成页面跳转和接口调用。

## 4. 验证与联调

- [x] `type-check` 已持续通过。
- [x] H5 构建已通过。
- [x] 小程序 `build:mp-weixin` 已通过。
- [x] 小程序 `dev:mp-weixin` 已可正常产出 `dist/dev/mp-weixin`。
- [x] 请求层关键单测已补齐并通过。

验收契约：

- 小程序开发版 MUST 能从 `dist/dev/mp-weixin` 导入运行。
- 小程序构建产物 MUST 不再包含会导致 `app.wxss` 编译失败的 H5 专用样式语法。
- 请求层关键兼容逻辑 SHALL 有最小化测试覆盖。

## 5. 已知未闭环项

- [ ] 手机号编辑仍需进一步压实为真实后端资料更新链路。
- [ ] 头像编辑尚未迁移完成。
- [ ] 售后图片上传的小程序端体验仍需专项优化与验证。
- [ ] 支付回跳、图片预览、上传等小程序专项能力仍需手工集中冒烟。
- [ ] 用户域空态、异常态、成功态文案还可进一步统一。
- [ ] `uView Plus` Sass 警告尚未治理，但当前不阻塞运行。

验收契约：

- 这些遗留项 MUST 在归档说明中明确记录，不得被误报为已完成。
- 现阶段归档 SHALL 以“主链路完成、遗留项可追踪”为前提。
