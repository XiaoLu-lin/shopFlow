# ShopFlow H5 Next 商品列表批次 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将商品列表批次 9 个页面统一到已确认的灰蓝 Suji A 方向，同时不破坏现有数据流和商品详情跳转。

**Architecture:** 先抽一个商品浏览批次展示 helper 和一份共享样式基座，再把搜索 / 分类、品牌 / 专题、新品 / 热卖 / 拼团三组页面统一切到同一套页面语言。页面保留现有 API 和路由，只重做展示结构、文案和样式。

**Tech Stack:** uni-app、Vue 3、TypeScript、SCSS、Vitest

---

### Task 1: 商品浏览批次展示 helper

**Files:**
- Create: `shopflow-h5-next/src/features/goods/browse-display-utils.ts`
- Create: `shopflow-h5-next/src/features/goods/browse-display-utils.test.ts`

- [ ] 定义页面类型、hero 文案、空态文案和搜索结果文案 helper。
- [ ] 先补测试，覆盖搜索结果标题、品牌 / 专题 / 拼团页默认文案和空态文案。
- [ ] 实现最小 helper，并跑对应测试。

### Task 2: 商品浏览批次共享样式基座

**Files:**
- Create: `shopflow-h5-next/src/pages/items/browse-page.scss`

- [ ] 提取轻 hero、面板、商品卡、品牌卡、专题卡、空态和标签的共享样式。
- [ ] 保证基座继续使用灰蓝 token、`8px` 圆角和轻阴影。

### Task 3: 搜索与分类结果页实现

**Files:**
- Modify: `shopflow-h5-next/src/pages/items/search/index.vue`
- Modify: `shopflow-h5-next/src/pages/items/category/index.vue`

- [ ] 搜索页切到搜索发现 + 结果态结构，保留热门搜索、历史搜索、结果和空态。
- [ ] 分类结果页切到轻 hero + 兄弟分类胶囊 + 单列商品流。
- [ ] 移除迁移期文案，保留现有分类与商品详情跳转。

### Task 4: 品牌与专题页实现

**Files:**
- Modify: `shopflow-h5-next/src/pages/items/brand-list/index.vue`
- Modify: `shopflow-h5-next/src/pages/items/brand/index.vue`
- Modify: `shopflow-h5-next/src/pages/items/topic-list/index.vue`
- Modify: `shopflow-h5-next/src/pages/items/topic/index.vue`

- [ ] 品牌馆切为列表式品牌卡。
- [ ] 品牌详情切为轻头部 + 简介卡 + 商品流。
- [ ] 专题列表切为内容流卡片。
- [ ] 专题详情切为轻 hero + 正文卡 + 商品流。

### Task 5: 新品 / 热卖 / 拼团页实现

**Files:**
- Modify: `shopflow-h5-next/src/pages/items/new/index.vue`
- Modify: `shopflow-h5-next/src/pages/items/hot/index.vue`
- Modify: `shopflow-h5-next/src/pages/items/groupon/index.vue`

- [ ] 新品、热卖和拼团三页统一到同一套商品流结构。
- [ ] 拼团页保留真实团购标签，但控制视觉重量。

### Task 6: 验证

**Files:**
- Verify only

- [ ] 运行 `cd shopflow-h5-next && pnpm vitest run src/features/goods/browse-display-utils.test.ts`
- [ ] 运行 `cd shopflow-h5-next && pnpm type-check`
- [ ] 必要时运行与商品域相关的现有测试。
- [ ] 在本地 H5 检查搜索、分类、品牌、专题和拼团页真实渲染。
