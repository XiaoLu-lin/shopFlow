# ShopFlow H5 Next Entry And Auth Batch D Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 把分类入口、商品详情和登录链路 5 个页面统一到已确认的 ShopFlow / Suji-like A 方向，并补齐注册/找回密码占位页的真实单页流程。

**Architecture:** 保持当前路由与接口契约不变，只重做页面结构与展示辅助逻辑。商品域继续复用 catalog / goods / sku 现有封装；登录链路在现有 auth API 之上补单页表单状态管理和轻量校验。

**Tech Stack:** uni-app、Vue 3、TypeScript、SCSS、Vitest

---

### Task 1: 补齐批次 D 工件与页面元信息

**Files:**
- Modify: `openspec/changes/extend-h5-next-suji-style-to-items-browse-pages/tasks.md`
- Create: `openspec/changes/extend-h5-next-entry-auth-pages-with-suji-style/proposal.md`
- Create: `openspec/changes/extend-h5-next-entry-auth-pages-with-suji-style/design.md`
- Create: `openspec/changes/extend-h5-next-entry-auth-pages-with-suji-style/tasks.md`

- [ ] 把批次 D 范围、复用入口、验证命令和验收契约同步到新 change 工件。
- [ ] 明确说明本批注册/找回密码采用“单页流程”而非旧 H5 多页拆分。
- [ ] 确认 tasks 中覆盖 5 个页面与 1 组 auth 表单逻辑。

### Task 2: 抽取登录链路展示与表单辅助

**Files:**
- Create: `shopflow-h5-next/src/features/auth/auth-form-display-utils.ts`
- Create: `shopflow-h5-next/src/features/auth/auth-form-display-utils.test.ts`
- Create: `shopflow-h5-next/src/features/auth/use-uni-auth-action-form.ts`
- Modify: `shopflow-h5-next/src/features/auth/use-uni-login-form.ts`

- [ ] 为登录 / 注册 / 找回密码整理统一的 hero copy、按钮文案、成功跳转文案和基础字段校验逻辑。
- [ ] 为注册与找回密码补一个可复用的单页 action form composable，负责验证码倒计时、提交中状态和基础校验。
- [ ] 保持登录页现有 `redirectAfterLogin` 行为不变。

### Task 3: 重写登录链路三页

**Files:**
- Modify: `shopflow-h5-next/src/pages/login/index.vue`
- Modify: `shopflow-h5-next/src/pages/login/forget/index.vue`
- Modify: `shopflow-h5-next/src/pages/login/register-get-code/index.vue`
- Create: `shopflow-h5-next/src/pages/login/auth-page.scss`

- [ ] 登录页改为统一的轻表单页，保留账号登录真实能力。
- [ ] 找回密码页从占位页改为正式单页流程，接通短信验证码和密码重置提交。
- [ ] 注册页从占位页改为正式单页流程，接通短信验证码和注册提交。
- [ ] 三页统一使用 `auth-page.scss` 样式基座，延续灰蓝轻量表单风格。

### Task 4: 重写分类入口与商品详情页

**Files:**
- Modify: `shopflow-h5-next/src/pages/items/catalog/index.vue`
- Modify: `shopflow-h5-next/src/pages/items/detail/index.vue`
- Create: `shopflow-h5-next/src/pages/items/catalog-page.scss`

- [ ] 分类入口页统一到更轻的小 banner + 子分类宫格结构。
- [ ] 商品详情页收成与商品流一致的卡片层级，保留原有规格、加购、购买行为。
- [ ] 若需要新 helper，优先收敛到已有 `detail-display-utils` / `sku-utils` 邻近文件，不散落到页面里。

### Task 5: 测试、review、验证

**Files:**
- Modify: `openspec/changes/extend-h5-next-entry-auth-pages-with-suji-style/review.md`
- Modify: `openspec/changes/extend-h5-next-entry-auth-pages-with-suji-style/tasks.md`

- [ ] 运行 `pnpm vitest run src/entities/auth/api.test.ts src/features/goods/detail-display-utils.test.ts src/features/goods/sku-utils.test.ts src/features/auth/auth-form-display-utils.test.ts`
- [ ] 运行 `pnpm type-check`
- [ ] 补一次 H5 本地页面验证说明，覆盖 catalog/detail/login/forget/register 五个路由。
- [ ] 在 review.md 中记录视觉统一、链路保持、剩余风险和验证证据。
