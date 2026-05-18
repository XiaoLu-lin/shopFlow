# Tasks: rebuild-shopflow-h5-modern-spa-with-tailwind

## 1. OpenSpec 与设计底稿

- [ ] 创建本次重构 change 的 proposal、design、tasks、review 与相关 spec 更新。
- [ ] 补充 brainstorming 设计稿与 writing-plans 实施计划。
- [ ] 产出旧 H5 页面、路由、接口和兼容要求矩阵。

验收契约：

- 页面矩阵 MUST 覆盖所有旧路由与对应页面职责。
- tasks SHALL 明确兼容契约与测试策略。

## 2. 新工程初始化

- [ ] 创建 `shopflow-h5-next` 并接入 `Vue 3 + TypeScript + Vite 6`。
- [ ] 接入 `Vant 4`、`Tailwind 3.4`、`Pinia`、`Vue Query`、`Vitest`、`Playwright`。
- [ ] 固定 `hash` 路由和 `/h5/` 风格静态部署参数。

验收契约：

- 新工程 MUST 可以本地启动并完成生产构建。
- 不得引入 SSR 或实验性数据加载能力。

## 3. 兼容层与请求层

- [ ] 用 TDD 实现 `session-adapter`、`tenant bootstrap`、`request client`、`route guard`。
- [ ] 保留旧 localStorage 键与双 header 映射。
- [ ] 统一错误提示与登录跳转语义。

验收契约：

- 首次首页访问 MUST 可完成 `appid + tenant` 预热。
- 已登录请求 MUST 自动带双 header。
- 未登录受保护路由 SHALL 跳转登录并保留 redirect。

## 4. 应用壳与页面域迁移

- [ ] 实现 Header、Tabbar、品牌令牌、全局布局和页面容器。
- [ ] 迁移浏览域页面。
- [ ] 迁移鉴权域页面。
- [ ] 迁移交易域与用户域页面。

验收契约：

- 旧 hash 路由语义 MUST 继续可访问。
- 页面功能 SHALL 覆盖旧站已有行为，不得仅保留空壳。

## 5. 验证与 CI

- [ ] 补齐兼容层单元测试、关键组件测试和 E2E 基线。
- [ ] 更新 CI，使其支持 `shopflow-h5-next` 构建与测试。
- [ ] 补 review，记录已完成范围、残余风险和切流前置条件。

验收契约：

- 构建、单元测试和最小 E2E 冒烟 MUST 可执行。
- review SHALL 明确旧站与新站并行期间的残余风险。
