# shopflow-admin-api 目录说明

> 适用范围：`shopflow-admin-api` 及其子目录，除非被更深层 `AGENTS.md` 覆盖。

## 1. 本目录职责

- 目录名称：`shopflow-admin-api`
- 主要职责：提供管理后台后端接口，承载后台权限、运营、商品、订单、租户、系统配置等管理侧业务能力。
- 不负责内容：不承载通用基础设施实现，不直接替代 `shopflow-core`；不承载底层实体与 mapper 定义，不替代 `shopflow-db`。

## 2. 复用与依赖边界

- 优先复用入口：`shopflow-core` 中已有的鉴权、租户、存储、通知、任务与公共配置能力。
- 优先复用入口：`shopflow-db` 中已有的 service、mapper、entity、domain 和 validator。
- 对外依赖边界：允许依赖 `shopflow-core` 与 `shopflow-db`；避免在本目录重复实现底层通用能力。
- 对外暴露边界：对管理端前端暴露后台接口、后台业务流程和后台专用返回模型。

## 3. 编码约束

- 后台接口行为变更时，必须同步检查管理端联调影响。
- 尽量把业务编排放在 service 或明确的业务层，不把复杂判断堆进 controller / web 层。
- 输入边界必须校验，权限、租户、角色、菜单与数据范围相关逻辑不得静默放宽。
- 后台模型对象、VO、DTO 命名遵循现有风格，不随意混用小程序侧模型。

## 4. 测试与验证

- 当前目录修改后，优先验证：后台接口联调、权限校验、租户隔离、关键业务流。
- 高风险点：角色权限、订单状态流转、商品审核、租户与系统配置、后台文件存储与通知链路。
- 变更后需要同步检查：管理端页面、接口文档、SQL、配置项、异常提示。

## 5. OpenSpec 衔接

- 涉及当前目录的功能新增、行为变更、主流程调整时，默认先使用根规则中的 `openspec-superpowers-orchestrator` 作为统一入口，由它判断 `brainstorming` / `openspec-explore` 与后续阶段。
- 除已明确可按 `lightweight` 处理的极小改动外，不要绕过 `openspec-superpowers-orchestrator` 直接进入实现。
- `apply` 完成后自动进入 `review`。
- `review` 完成后停止等待用户确认，再决定是否 `archive`。
- 如果归档结论只影响当前目录，优先沉淀到当前 `AGENTS.md`。
