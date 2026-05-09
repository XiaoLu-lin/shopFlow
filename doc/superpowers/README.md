# Superpowers 工件说明

本目录用于承接 ShopFlow 项目在 OpenSpec 主流程之外，由 Superpowers 产出的长期可追溯工件。

## 目录用途

- `specs/`：保存 `superpowers:brainstorming` 产出的设计稿。
- `plans/`：保存 `superpowers:writing-plans` 产出的实施计划。

## 命名约定

- 设计稿：`specs/YYYY-MM-DD-<topic>-design.md`
- 计划：`plans/YYYY-MM-DD-<topic>-plan.md`

`<topic>` 应与 OpenSpec change 名称或需求主题保持一致，便于互相引用和追踪。

## 使用要求

- 设计稿用于沉淀设计方向、约束、候选方案、推荐方案和关键决策。
- 计划用于说明阶段划分、执行顺序、可并行任务、串行协调点和验证方式。
- `tasks.md` 仍然是 OpenSpec change 的完成状态来源，plan 不替代 tasks。
- proposal、design、specs、tasks、plan、review 之间应互相引用，避免规格与实现脱节。
