# 项目根目录 Codex / OpenSpec 说明

> 适用范围：项目根目录及未被更深层 `AGENTS.md` 覆盖的子目录。

本文件只沉淀长期有效的协作规则，不记录一次性任务背景或短期调试结论。
本文件只负责全局规则；每个关键模块目录都应补自己的 `AGENTS.md` 承载局部规则。

## 1. OpenSpec 默认规则

- 涉及功能新增、功能修改、主流程调整、接口或数据结构变化、较大修复时，默认先走 OpenSpec。
- 涉及管理端页面行为、小程序交互流程、后端接口返回结构、数据库模型与 SQL 变更时，也必须先走 OpenSpec，不要绕过 `explore / propose` 直接改实现。
- 涉及新增能力、行为变更、方案设计或非平凡重构时，先显式使用 `superpowers:brainstorming`，形成设计结论后再进入 OpenSpec 建档。
- 默认遵循官方 OpenSpec 主流程：`explore -> propose -> apply -> archive`。
- 本项目采用增强治理，推荐执行链路为：`brainstorming -> explore -> propose -> writing-plans -> apply -> review -> verify -> archive`。
- 本项目采用增强治理：`apply` 完成后自动进入 `review`，`review` 与 `verify` 完成后停止并等待用户确认，再决定是否 `archive`。
- `openspec/changes/` 用于活跃 change，`openspec/changes/archive/` 仅作历史保留。
- proposal、design、specs、tasks 应与代码状态保持同步，避免长期漂移。
- `doc/superpowers/specs/` 用于沉淀 brainstorming 产出的设计稿，`doc/superpowers/plans/` 用于沉淀 writing-plans 产出的实施计划。
- 进入 coding / apply 阶段后，显式使用 `superpowers:test-driven-development`、`superpowers:requesting-code-review`、`superpowers:verification-before-completion`。
- 多步骤变更默认补 `writing-plans` 形成 plan；仅单文件、小范围、无并行价值的轻量改动，才允许跳过 plan。
- 并行开发时，子 agent 只提交代码和完成报告，不直接修改 `openspec/changes/<change>/tasks.md`，由主线统一勾选任务状态，避免冲突。

## 1.1 七阶段工作流

- Phase 1 `brainstorming`：用于澄清目标、约束、候选方案和推荐方案，逐个问题确认，不带着未决假设进入建档。
- Phase 2 `explore`：用于调查现有代码、上下游影响、复用入口、兼容约束和 change 命名。
- Phase 3 `propose`：生成 proposal、design、specs、tasks，并保证四份工件互相引用、与设计结论一致。
- Phase 4 `writing-plans`：把 tasks 拆成可执行计划，明确阶段划分、可并行项、文件范围、验证命令和检查点。
- Phase 5 `apply`：按计划执行实现；任务内部优先走 TDD 红绿循环；独立任务可并行，跨模块高耦合任务由主线串行协调。
- Phase 6 `review`：独立审查需求覆盖、行为回归、边界遗漏、残余风险和验证充分性；重要问题修复前不进入归档。
- Phase 7 `verify/archive`：核对 tasks、specs、代码、测试证据和 review 结论一致，再等待用户确认归档。

## 2. 确认闸门

- 第一确认闸门：完成 brainstorming、代码调查、上下游影响分析、方案比较后，先向用户汇总理解、范围、候选方案、推荐方案和 change 名称；用户确认后再创建或更新 proposal/design/specs/tasks。
- 第二确认闸门：OpenSpec 文档与实施计划准备完成后，先向用户汇总关键假设、核心方案、任务拆分、并行策略、风险和验证方式；用户确认后再进入 apply。
- 任一闸门之前都不应留下 `⏳` 状态的关键待确认问题；用户已拍板事项应明确标记为 `✅ [已确认]`。

## 3. 代码改动原则

- 先复用现有模块、服务、工具类与前端公共封装，再新增实现。
- 保持最小改动，避免顺手清理无关代码或做大范围重构。
- 影响主入口、主流程、对外接口、鉴权、支付、租户、存储、消息推送时，必须先明确兼容约束和回滚方案。
- 输入边界必须校验，异常不能静默吞错。
- 新逻辑优先拆成可测试的服务、工具或纯函数，避免把业务判断散落到控制器、页面生命周期或 mapper XML 中。

## 4. 目录职责

- `shopflow-admin-api`：管理后台后端接口层，负责后台管理能力、权限控制、运营配置和后台业务流程。
- `shopflow-wx-api`：小程序后端接口层，负责 C 端用户、小程序页面对应能力和面向用户侧的业务流程。
- `shopflow-core`：通用基础设施与跨模块能力，承载配置、鉴权、租户、存储、通知、任务、事务等核心支撑。
- `shopflow-db`：数据访问层，承载实体、领域对象、mapper、service 与 SQL 映射。
- `shopflow-all`：聚合启动模块与整体装配入口。
- `shopflow-admin`：Vue 2 + Element UI 管理端前端。
- `shopflow-wx`：微信小程序前端。
- `doc`：项目文档、FAQ、部署和说明材料。
- `docker`：容器化部署相关文件。

## 4.1 包级 `AGENTS.md` 要求

- `shopflow-admin-api`、`shopflow-wx-api`、`shopflow-core`、`shopflow-db`、`shopflow-admin`、`shopflow-wx` 默认都应有自己的 `AGENTS.md`。
- 根 `AGENTS.md` 只写全局约束，不代替包内规则。
- 包级 `AGENTS.md` 应补目录职责、局部边界、复用入口、命名与测试要求。
- 如果包级规则与根规则冲突，以更近一级且更严格的规则为准。

## 5. 测试与验证

- 测试采用三层嵌入思路：tasks 验收契约层、任务内 TDD 实现层、阶段收尾集成验证层。
- Java 纯逻辑改动优先补单元测试。
- 涉及 Spring Boot 接口、权限、数据访问、事务或跨模块联动时，至少做集成验证。
- 管理端改动至少验证对应页面、接口联调、权限与异常提示。
- 小程序改动至少验证页面流程、授权路径、支付或下单相关上下游联调。
- 如果改动数据库、配置、迁移、静态资源或外部依赖，同时检查 SQL、文档、部署说明和初始化脚本是否需要同步。
- 未执行最新验证命令前，不得声称“已完成”“测试通过”或“可以归档”；结论必须附验证证据。

## 6. review 与 archive

- `apply` 完成后，自动进入独立 review。
- review 重点确认：需求覆盖、验证充分性、行为回归、边界遗漏、残余风险、并行任务整合质量和验收契约是否兑现。
- review 建议写入 `openspec/changes/<change-name>/review.md`，至少记录发现、结论、遗留风险和处理动作。
- archive 前显式执行一次“verify”检查，确认 tasks 已同步、specs 与代码一致、验证证据完整、review 重要问题已处理。
- review 与 verify 完成后，先向用户汇总结论并等待确认，再决定是否 archive。
- archive 前先把长期有效规则沉淀到最合适层级的 `AGENTS.md` 或项目文档。

## 7. 输出与文风

- 默认输出语言：中文。
- 新增说明文档、日志文案、提示信息优先保持清晰可读。
- 注释只写必要信息，重点说明意图、边界和副作用。

## 8. 工件要求

- brainstorming 产出的设计稿保存到 `doc/superpowers/specs/YYYY-MM-DD-<topic>-design.md`。
- writing-plans 产出的计划保存到 `doc/superpowers/plans/YYYY-MM-DD-<topic>-plan.md`。
- `openspec/changes/<change>/tasks.md` 中每个任务都应补 1 到 3 条“验收契约”，优先使用 `MUST`、`SHALL`、`不得` 这类可验证措辞。
- proposal、design、specs、tasks、plan、review 之间要互相引用上游工件，避免信息断层。
- 轻量改动如果跳过 plan，需要在 tasks 或 apply 说明里写明理由，例如“单文件修复、无并行价值、实现路径明确”。
