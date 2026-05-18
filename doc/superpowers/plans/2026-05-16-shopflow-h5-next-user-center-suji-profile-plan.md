# ShopFlow H5 Next 个人中心 Suji Profile A 方案 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 把 `shopflow-h5-next` 个人中心页重写成更贴近 `docs-suji` Profile 的 A 方案，同时保持现有入口与跳转行为不回退。

**Architecture:** 保留现有 `/user/index` 数据来源、session snapshot 和 `entry-utils` 路由分发，仅重做页面结构与少量展示辅助逻辑。优先把头部文案和服务入口排序收敛到 `dashboard-utils.ts`，让模板保持清晰。

**Tech Stack:** uni-app、Vue 3、TypeScript、SCSS、Vitest

---

## 文件结构

- 修改 `shopflow-h5-next/src/pages/user/index.vue`：重写个人中心模板与样式。
- 修改 `shopflow-h5-next/src/pages/user/dashboard-utils.ts`：沉淀头部文案、服务入口排序等展示辅助逻辑。
- 修改 `shopflow-h5-next/src/pages/user/dashboard-utils.test.ts`：先写失败测试，再补最小实现。
- 视需要修改 `shopflow-h5-next/src/pages/user/entry-utils.ts`：仅当服务入口映射需要补充时调整。

## 任务 1：测试先行补个人中心展示逻辑

- [ ] 在 `dashboard-utils.test.ts` 中新增失败测试，覆盖：
  - 已登录用户头部文案包含昵称、品牌眉文和脱敏手机号
  - 未登录用户回退文案正确
  - 服务入口排序与展示列表符合 A 方案

- [ ] 运行测试并确认先失败：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run test -- src/pages/user/dashboard-utils.test.ts
```

预期：测试失败，失败原因是当前展示结构尚未支持 A 方案。

- [ ] 在 `dashboard-utils.ts` 中补最小实现，让新增测试通过。

- [ ] 再次运行：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run test -- src/pages/user/dashboard-utils.test.ts
```

预期：测试通过。

## 任务 2：重写个人中心页面

- [ ] 改造 `src/pages/user/index.vue` 的模板：
  - 顶部改为渐变头部
  - 中部改为订单五宫格卡片
  - 底部改为列表式服务入口

- [ ] 保留现有行为：
  - `全部订单` 跳转
  - 设置跳转
  - 购物车 `switchTab`
  - 其他服务 `navigateTo`

- [ ] 检查页面文案，不再保留说明型大卡片文案。

## 任务 3：验证

- [ ] 运行页面相关测试：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run test -- src/pages/user/dashboard-utils.test.ts src/pages/user/entry-utils.test.ts
```

预期：测试通过。

- [ ] 运行类型检查：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run type-check
```

预期：退出码为 0。

- [ ] 在本地 H5 中检查：
  - `http://127.0.0.1:6257/#/pages/user/index`

预期：个人中心页视觉结构已切到 A 方案，入口仍可点击。
