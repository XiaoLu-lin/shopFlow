# ShopFlow H5 Modern Rebuild Design

## Goal

新增 `shopflow-h5-next`，以现代技术栈全量重建当前 H5 前台，同时保留现有 ShopFlow `/wx/*` 协议、旧 hash 路由语义和关键 localStorage 兼容契约。

## Confirmed Decisions

- 并行新目录，不直接升级旧 `shopflow-h5`
- `Vue 3 + TypeScript + Vite 6`
- `Vant 4 + Tailwind CSS 3.4 + CSS Variables`
- 全量 1:1 功能覆盖
- UI 全新设计
- 纯前端静态部署
- `hash` 路由兼容

## Recommended Aesthetic Direction

- 风格：轻奢编辑感移动商城
- 关键词：暖白、柔和渐层、圆角大卡片、轻质玻璃感、胭脂粉与琥珀高光
- 目标：比旧站更现代，但仍保留移动商城的操作直觉

## Architecture Summary

- `Pinia` 管理客户端状态
- `Vue Query` 管理接口状态
- 兼容层负责旧登录态键、租户预热、双 header 与错误归一
- 业务按浏览、鉴权、交易、用户四大域迁移
