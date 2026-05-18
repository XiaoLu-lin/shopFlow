# Review: rebuild-shopflow-h5-next-as-uniapp-multiplatform

更新日期：2026-05-13

## 结论

本次 `shopflow-h5-next` 从单端 H5 工程切换为 `uni-app` 多端主工程的主线目标已基本达成：

- H5 主链路可运行
- 微信小程序开发版可运行
- 浏览、鉴权、交易、用户域主页面均已迁移
- 旧登录态、租户预热、双 header、redirect 等兼容协议已保留

当前状态适合进入“带少量非阻塞遗留说明的归档准备”，主链路高风险问题已完成收口。

## 已完成范围

### 工程与栈

- `shopflow-h5-next` 已切换为 `uni-app + Vue 3 + TypeScript + Pinia + uView Plus + SCSS`
- H5 与 `mp-weixin` 双端输出链路已具备
- H5 仍保留 `Tailwind CSS 3.4` 作为增强，小程序端不依赖 Tailwind 主样式

### 兼容层

- 旧 `Authorization`、`avatar`、`nickName`、`mobile` 等兼容键可继续读写
- `X-ShopFlow-User-Token`、`X-ShopFlow-TenantId` 已由统一请求层自动注入
- `/wx/home/auth` 租户预热链路已保留
- 登录后 redirect 语义与受保护页面跳转已保留

### 页面域

- 浏览域：首页、分类、搜索、商品详情、热卖、新品、拼团、品牌、专题
- 鉴权域：登录、注册、找回密码
- 交易域：购物车、结算、支付、支付结果、订单详情
- 用户域：个人中心、订单列表、地址、收藏、优惠券、售后列表、售后详情、售后申请、资料设置、帮助、服务、反馈

### 小程序专项修复

- 修复 `app.wxss` 编译失败问题，避免 H5 专用样式进入小程序产物
- 修复 `axios` 在 `mp-weixin` 环境无适配器的问题，改为统一 `uni.request`
- 修复 `URLSearchParams is not defined`
- 修复 header 编码不兼容导致的 `ISO-8859-1` 报错
- 修复开发版小程序读取相对 `/wx` 导致的 `invalid url`
- 对齐旧小程序的 `develop / trial / release` baseURL 选择逻辑
- 支付页已补齐 `mp-weixin` 分支，按旧站兼容协议接通 `/order/prepay + requestPayment`
- 售后凭证上传已改为统一 `uni.uploadFile`，不再走失真的 `FormData + uni.request`

## 验证证据

已执行并通过的验证包括：

- `npm run type-check`
- `npm run build:h5`
- `npm run build:mp-weixin`
- `npm run dev:mp-weixin`
- `npm test -- src/shared/request/client.test.ts`
- 历史阶段性单测：
  - `src/shared/compat/session-adapter.test.ts`
  - `src/entities/auth/api.test.ts`
  - `src/entities/user/api.test.ts`

补充验证结论：

- 小程序开发版目录已可产出到 `dist/dev/mp-weixin`
- 本地 `http://127.0.0.1:6914/wx/home/auth` 已确认可返回成功结果
- 小程序开发版请求配置已改为优先走本地 `6914` 联调地址

## 已知遗留风险

### 功能遗留

- 手机号编辑仍偏向兼容写回，尚未完全压实为真实后端资料更新
- 头像编辑未完成迁移
- 售后图片上传仍可继续优化为更适合小程序端的多图与预览交互体验

### 验证遗留

- 支付回跳、图片预览、文件上传等小程序专项能力仍需人工集中冒烟
- 用户域部分页面的空态、异常态、成功态文案仍可进一步统一
- 个别跨页刷新与状态同步仍有优化空间

### 非阻塞技术债

- `uView Plus` 相关 Sass deprecation warning 仍存在，但当前不阻塞构建与运行

## 建议归档口径

建议本次 change 以如下口径归档：

- `shopflow-h5-next` 的 uni-app 多端主链路迁移已完成
- H5 与微信小程序开发版已具备联调能力
- 少量资料编辑、图片预览/多图交互、支付回跳手工回归与文案统一项作为后续增量优化继续追踪
