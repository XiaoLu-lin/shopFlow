# Proposal: rebuild-shopflow-h5-next-as-uniapp-multiplatform

## Why

当前前台存在两条长期分裂的用户侧实现：

- `shopflow-h5` / `shopflow-h5-next`：面向 H5 的独立前台实现
- `shopflow-wx`：面向微信小程序的原生小程序实现

虽然 `shopflow-h5-next` 已经完成一轮现代化 H5 重建，但它仍然是单端架构，无法直接产出微信小程序。继续沿着 “H5 一套、小程序一套” 的方式推进，会长期带来以下问题：

- 页面、业务流程、接口封装和兼容逻辑双线维护
- 登录、租户预热、支付、分享、上传等主链路容易出现端间漂移
- 视觉和交互规范难以统一
- 后续每次新增业务都要重复做 H5 和小程序两套落地

用户已确认下一阶段目标是将 `shopflow-h5-next` 直接升级为一个可同时产出 `H5` 与 `微信小程序` 的多端主工程，并采用：

- `uni-app`
- `Vue 3`
- `TypeScript`
- `Pinia`
- `uView Plus`
- `SCSS`
- `Tailwind CSS 3.4` 仅用于 H5 端增强

## What

本次 change 将把下一阶段技术路线从“Vite 单端 H5 SPA”切换为“uni-app 多端前台工程”，并完成对应的规格建档：

1. 将 `shopflow-h5-next` 的目标形态调整为 `uni-app` 多端工程。
2. 规定新前台同时支持 `H5` 和 `mp-weixin`。
3. 保留现有 `/wx/*` 接口结构、租户预热协议、用户 token 映射和旧 H5 登录态兼容键。
4. 将通用组件体系固定为 `uView Plus + sf-* 业务组件`。
5. 将平台差异收敛到独立适配层，包括：
   - storage
   - navigation
   - payment
   - share
   - upload
6. 明确旧 H5 hash 链接兼容策略和旧小程序并行保留策略。
7. 补齐后续实施所需的 design、tasks 与多端行为 spec。

本次变更不包含：

- 立即删除旧 `shopflow-h5` 或旧 `shopflow-wx`
- 立即启动 `shopflow-h5-next` 的 uni-app 代码迁移
- 修改后端 `/wx/*` 返回结构
- 引入 SSR、BFF、微前端或双后端协议

## Impact

受影响模块：

- `shopflow-h5-next`
- `shopflow-wx`
- `openspec/changes/rebuild-shopflow-h5-next-as-uniapp-multiplatform`
- `openspec/specs`
- `doc/superpowers/specs/2026-05-12-shopflow-uniapp-multiplatform-design.md`

受影响能力：

- H5 与微信小程序前台技术栈
- 前台组件体系
- 前台路由模型
- 前台平台适配层
- 前台兼容层与验证策略

主要风险：

- `shopflow-h5-next` 现有 Vite SPA 页面不能直接平移到 uni-app，需要重组页面壳和组件体系。
- `Vant` 与 `vue-router` 相关实现会被逐步替换。
- 小程序支付、分享、上传和富文本能力必须单独端内验证。

预期收益：

- 后续主业务页面尽量以一套代码同时服务 H5 与微信小程序。
- 登录、租户、购物车、下单、用户资料等链路可以统一收敛。
- 前端后续新增需求的双端开发成本显著下降。
