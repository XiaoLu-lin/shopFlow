# Proposal: rebuild-shopflow-h5-modern-spa-with-tailwind

## Why

当前 `shopflow-h5` 仍基于 `Vue 2 + Vue CLI 3 + Vant 2 + Vuex`，并保留大量从旧 `litemall-vue` 迁入的历史结构。虽然租户头、用户头和基础鉴权兼容已经补齐，但现状仍有几类长期问题：

- 工程栈老旧，构建和依赖治理成本高。
- 页面状态、接口状态和本地兼容逻辑耦合严重，难以继续演进。
- 样式体系以历史 `scss` 页面样式为主，难以支撑全新 UI 设计。
- 页面、组件、请求层和兼容层缺少清晰边界，不利于全量迁移与持续维护。

用户已确认本次目标不是继续在旧目录上修修补补，而是：

- 新建并行目录 `shopflow-h5-next`
- 使用 `Vue 3 + TypeScript + Vite 6 + Vant 4 + Tailwind CSS 3.4`
- 保持旧 H5 功能全量 1:1 覆盖
- 保留现有 `/wx/*` 协议、hash 路由语义和 localStorage 兼容契约
- 在重构同时做 UI 全新设计

## What

本次 change 将新增一个并行运行的新 H5 前端，并逐步完成旧站能力迁移：

1. 创建 `shopflow-h5-next` 新工程与静态部署链路。
2. 先产出旧站页面、路由、接口和兼容要求矩阵，作为迁移底稿。
3. 抽离新站基础兼容层：
   - `Authorization`
   - `ShopFlowTenantToken`
   - `avatar`
   - `nickName`
   - `X-ShopFlow-User-Token`
   - `X-ShopFlow-TenantId`
   - `/wx/home/auth` 预热
4. 用 `Pinia + Vue Query` 重构状态分层。
5. 用 `Vant 4 + Tailwind 3.4 + CSS Variables` 重建 UI 基座。
6. 迁移全部页面域：
   - 浏览
   - 鉴权
   - 交易
   - 用户中心
7. 新增测试基线、E2E 基线和 CI 构建入口。

本次变更不包含：

- 修改后端接口结构
- 引入 SSR、BFF、微前端
- 切断旧 H5 运行能力
- 在首版彻底清零全部 SCSS

## Impact

受影响模块：

- `shopflow-h5-next`
- `.github/workflows/deploy.yml`
- `openspec/specs/litemall-vue-compatibility`

受影响能力：

- H5 前端构建与部署
- H5 页面路由与静态资源
- H5 用户态和租户兼容层
- H5 测试与验证链路

主要风险：

- 旧站存在部分空实现、隐式 localStorage 依赖和历史文案假设，迁移时容易遗漏行为细节。
- Vant 4 与 Tailwind 并用时若不限制基础样式，容易互相污染。
- 若不先把兼容层写成独立模块，页面迁移会重新散落协议细节。

预期收益：

- 获得一个可以继续长期演进的现代 H5 前端。
- 在保留现有 ShopFlow 协议的前提下，降低页面迁移与维护复杂度。
- 为后续逐步下线旧 `shopflow-h5` 提供平滑切换路径。
