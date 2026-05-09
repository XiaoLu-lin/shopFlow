# shopflow-h5 目录说明

> 适用范围：`shopflow-h5` 及其子目录，除非被更深层 `AGENTS.md` 覆盖。

## 1. 本目录职责

- 目录名称：`shopflow-h5`
- 主要职责：提供 Vue 2 + Vant H5 前台，承载移动端网页页面、路由、状态管理、接口调用和用户侧登录态兼容。
- 历史来源：本目录由旧 `litemall-vue` 前台导入并更名，保留部分历史页面结构与 localStorage 登录态约定。
- 不负责内容：不承担后端业务规则落地，不替代 `shopflow-wx-api`；不承诺旧 litemall 全量接口差异都已完成迁移。

## 2. 复用与依赖边界

- 优先复用入口：`src/api`、`src/utils/request.js`、`src/utils/shopflow-compat.js`、`src/router`、`src/store`。
- 用户侧接口统一走 ShopFlow wx API，生产配置默认指向 `https://manager.enshipeixue.com/wx/`。
- 线上部署时 H5 静态页面不要占用 `/wx` 路径；`/wx/*` 必须保留给后端 API，H5 推荐部署到 `/h5/` 或独立域名。
- 租户上下文通过 `X-ShopFlow-TenantId` 传递，用户登录态通过 `X-ShopFlow-User-Token` 传递。
- 不要恢复旧 `X-Litemall-Token` 作为当前请求协议，只能在兼容说明中作为历史背景出现。

## 3. 编码约束

- 页面结构和 Vant 组件风格优先保持现状，避免在命名整理时夹带 UI 或业务重构。
- 登录、注册、验证码、找回密码等鉴权链路改动前，先确认是否影响 `shopflow-wx-api` 的 `/wx/auth/*` 语义。
- 新增兼容逻辑优先收敛到 `src/utils/shopflow-compat.js` 或统一请求层，避免散落到各页面生命周期。
- 本地专用配置放 `.env.local`，不要提交本地 token、端口覆盖或构建产物。

## 4. 测试与验证

- H5 兼容测试：`node scripts/shopflow-compat.test.js`。
- 生产构建：`VUE_APP_SHOPFLOW_APPID=1649067 npm run build`。
- 本地联调启动：`VUE_APP_BASE_API=http://localhost:6914/wx VUE_APP_SHOPFLOW_APPID=1649067 npm run serve -- --port 6256`。
- 重点验证：租户预热、普通用户登录、请求头映射、登录后跳转、注册和找回密码入口。

## 5. OpenSpec 衔接

- 涉及 H5 主流程、鉴权、请求协议、接口字段或后端联动时，默认先走 OpenSpec。
- 仅目录命名、文档路径、CI 工作目录等不改变运行行为的轻量整理，可按根规则说明理由后直接实施。
