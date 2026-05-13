# ShopFlow H5 Modern Rebuild Plan

## Phase 1: Inventory

- 盘点旧 H5 路由、页面、接口和兼容要求
- 形成页面矩阵
- 确认每个页面的验收要点

## Phase 2: Foundation

- 初始化 `shopflow-h5-next`
- 配置 Vite、Tailwind、Vant、Pinia、Vue Query、Vitest、Playwright
- 建立设计令牌、路由、应用壳和兼容层

## Phase 3: Domain Migration

- 浏览域
- 鉴权域
- 交易域
- 用户域

## Phase 4: Verification

- 单元测试
- 组件测试
- E2E 冒烟
- CI 构建

## Checkpoints

- Checkpoint A：基础层可启动、可构建、兼容层测试通过
- Checkpoint B：浏览域和鉴权域可运行
- Checkpoint C：交易域和用户域可运行
- Checkpoint D：CI 与验证证据齐全
