# OpenSpec × Superpowers 项目手册

本手册用于说明 ShopFlow 项目如何把 OpenSpec 作为规格骨架，把 Superpowers 作为设计、计划、开发、验证和审查能力接入进来。
默认前提是：根目录存在全局 `AGENTS.md`，关键模块目录各自维护更细粒度的 `AGENTS.md`。

## 1. 工作流总览

- 推荐流程：`brainstorming -> explore -> propose -> writing-plans -> apply -> review -> verify -> archive`
- 官方 OpenSpec 主骨架仍然有效：`explore -> propose -> apply -> archive`
- 本项目在官方骨架之上补充：
  - `brainstorming`：在新增能力、行为变更、方案设计或非平凡重构前先收敛设计
  - `writing-plans`：在多步骤变更前把 tasks 转化成可执行计划
  - `review`：`apply` 完成后自动进入独立审查
  - `verify`：归档前显式核对任务状态、代码状态和验证证据

## 2. 哪些场景必须走全流程

以下场景默认按七阶段流程执行，不得直接绕过 OpenSpec 进入编码：

- 业务能力新增
- 功能变更
- 主流程调整
- 接口、返回结构或数据模型变化
- 数据库表结构、SQL、mapper、service 变更
- 管理端页面行为、权限或提交流程变化
- 小程序页面流程、授权链路、支付或下单链路变化
- 鉴权、租户、存储、通知、任务调度等核心支撑能力改动
- 影响外部行为的较大修复
- 跨 2 个以上模块或 3 个以上任务的改动

以下情况可按复杂度判断是否简化：

- 单文件、小范围、无并行价值的缺陷修复
- 纯文案、注释、说明文档调整
- 不影响运行行为的配置说明更新

即使走简化路径，也应遵守最小改动、边界校验和验证取证原则。

## 3. 七阶段说明

### Phase 1：`brainstorming`

- 适用于新增能力、行为变化、方案调整或非平凡重构。
- 先理解项目上下文，再逐个问题确认目标、约束、成功标准和候选方案。
- 需要在进入 OpenSpec 前形成设计结论，并保存到：
  - `doc/superpowers/specs/YYYY-MM-DD-<topic>-design.md`
- 不允许带着关键未决问题直接进入建档。

### Phase 2：`explore`

- 目标是理解现有代码、影响范围、复用入口、兼容约束、风险点和 change 名称。
- 重点梳理：
  - 受影响模块
  - 上下游接口或页面
  - 现有复用入口
  - 回滚与兼容约束

### Phase 3：`propose`

- 生成并维护：
  - `proposal.md`
  - `design.md`
  - `specs/.../spec.md`
  - `tasks.md`
- proposal 讲 why、what、impact。
- design 讲关键决策、兼容性、边界和权衡。
- specs 用行为场景表达需求。
- tasks 用可执行任务驱动 apply。

### Phase 4：`writing-plans`

- 适用于多任务、跨模块、需要并行开发或需要明确检查点的变更。
- 计划文件保存到：
  - `doc/superpowers/plans/YYYY-MM-DD-<topic>-plan.md`
- plan 负责回答“怎么做”，至少说明：
  - 阶段划分
  - 每阶段任务归属
  - 哪些任务可并行
  - 哪些任务由主线串行协调
  - 验证命令与检查点
- `tasks.md` 仍然是完成状态的唯一准绳，plan 不替代 tasks。

### Phase 5：`apply`

- 实现前先确认 proposal、design、specs、tasks、plan 一致。
- 显式使用：
  - `superpowers:test-driven-development`
  - `superpowers:requesting-code-review`
  - `superpowers:verification-before-completion`
- 任务内部优先使用 TDD 红绿循环。
- 独立任务可并行，但子 agent 只提交代码和完成报告，不直接改 `tasks.md`。
- 主线统一同步任务状态和阶段结论，避免并发冲突。

### Phase 6：`review`

- `apply` 完成后自动进入独立 review。
- review 重点确认：
  - 需求与 specs 是否覆盖完整
  - 验证是否充分
  - 是否存在行为回归
  - 是否遗漏边界条件
  - 是否存在残余风险
  - 验收契约是否兑现
- review 建议沉淀到：
  - `openspec/changes/<change-name>/review.md`

### Phase 7：`verify` 与 `archive`

- archive 前先显式核对：
  - tasks 是否已同步勾选
  - specs 是否与代码一致
  - review 中的重要问题是否已处理
  - 测试、lint、构建、联调或人工验证证据是否完整
- verify 完成后先等待用户确认，再进入 archive。
- archive 时同步沉淀长期有效规则到合适的 `AGENTS.md` 或项目文档。

## 4. 两道确认闸门

### 第一确认闸门

完成 brainstorming、explore、方案比较后，先向用户汇总：

- 当前理解
- 变更范围
- 受影响模块
- 候选方案与推荐方案
- 拟定 change 名称

只有用户确认后，才创建或更新 proposal、design、specs、tasks。

### 第二确认闸门

完成 OpenSpec 文档和实施计划后，先向用户汇总：

- change 名称
- 关键假设
- 核心方案
- 任务拆分
- 并行策略
- 主要风险
- 验证方式

只有用户确认后，才进入 apply。

## 5. 工件要求

### 5.1 brainstorming 设计稿

- 保存路径：`doc/superpowers/specs/YYYY-MM-DD-<topic>-design.md`
- 用于沉淀设计方向、关键约束、候选方案和最终推荐方案。

### 5.2 OpenSpec change 工件

- 活跃 change 位于：`openspec/changes/<change-name>/`
- 历史归档位于：`openspec/changes/archive/`

每个 change 至少维护：

- `proposal.md`
- `design.md`
- `tasks.md`
- `specs/.../spec.md`

### 5.3 plan 工件

- 保存路径：`doc/superpowers/plans/YYYY-MM-DD-<topic>-plan.md`
- 用于描述阶段划分、执行顺序、并行策略和验证方式。

### 5.4 review 工件

- 推荐路径：`openspec/changes/<change-name>/review.md`
- 用于记录审查发现、重要问题、处理动作和残余风险。

## 6. tasks 验收契约要求

`tasks.md` 中每个任务都应附带 1 到 3 条验收契约，用于把需求验收条件前置到建档阶段。

要求如下：

- 优先使用 `MUST`、`SHALL`、`不得` 这类可验证措辞
- 只描述完成标准，不写大段实现细节
- 可以覆盖返回结构、异常行为、权限限制、分页条件、联调要求等
- 契约应能直接驱动测试编写

示例表达：

- MUST 返回 `{ list, total }` 结构
- SHALL 支持分页参数 `page`、`pageSize`
- 在权限不足时不得放宽校验

## 7. 测试、验证与证据

本项目采用三层嵌入式测试思路：

- 第一层：契约层。任务在 `tasks.md` 中先写清验收契约。
- 第二层：实现层。编码时先按契约写测试，再写实现，再跑通测试。
- 第三层：集成层。阶段收尾时补跑 lint、构建、接口联调、页面联调、人工链路验证等。

验证要求：

- Java 纯逻辑优先补单元测试
- Spring Boot 接口、权限、事务、数据访问和跨模块改动至少做集成验证
- 管理端改动至少验证页面行为、接口联调、权限和异常提示
- 小程序改动至少验证页面流程、授权链路和关键用户路径
- 数据库、配置、迁移和外部依赖改动要同步检查 SQL、文档和部署说明

未运行最新验证命令前，不得宣称“已完成”“测试通过”或“可以归档”。

## 8. 并行开发与状态同步

- 一个 change 默认共享同一个上下文，不为每个子 agent 单独维护一套 tasks 状态。
- 子 agent 负责提交实现代码和完成报告。
- 主线负责：
  - 审核子 agent 结果
  - 汇总 review 反馈
  - 统一更新 `tasks.md`
  - 统一推进 verify 和 archive

这样做的目的，是避免多个 agent 同时修改同一份 tasks 文档导致冲突或状态漂移。

## 9. review 与 archive 纪律

- review 不得省略，即使变更看起来简单，也至少做一次独立视角检查。
- review 中发现的重要问题，在修复前不应进入 archive。
- archive 前应回看本次 change，沉淀长期有效规则：
  - 实现边界
  - 复用入口
  - 易错点
  - 验证方式
  - 兼容约束
- 如果规则只影响某个模块，优先沉淀到对应模块的 `AGENTS.md`。

## 10. 维护要求

- 根 `AGENTS.md` 更新后，本手册需同步。
- `openspec/config.yaml` 更新后，本手册需同步。
- `doc/superpowers/specs/` 与 `doc/superpowers/plans/` 的产物应与实际流程保持一致。
- 多处描述冲突时，以更近一级且更严格的规则为准。
