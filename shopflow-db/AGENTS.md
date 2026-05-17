# shopflow-db 目录说明

> 适用范围：`shopflow-db` 及其子目录，除非被更深层 `AGENTS.md` 覆盖。

## 1. 本目录职责

- 目录名称：`shopflow-db`
- 主要职责：承载实体、领域对象、枚举、mapper、service、validator 与 SQL 映射，是项目的数据访问层与持久化边界。
- 不负责内容：不承担页面交互、接口编排或通用基础设施实现；不把复杂上层业务流程直接堆进数据访问层。

## 2. 复用与依赖边界

- 优先复用入口：已有 `service`、`mapper`、`validator`、`domain`、`entity` 与 `mybatis` 封装。
- 优先复用入口：已有排序、订单等校验器，避免在上层重复实现相同规则。
- 对外依赖边界：对上层提供稳定的数据访问接口；避免为了单次需求随意打破实体、领域对象和 mapper 的既有分层。
- 对外暴露边界：为后台与小程序业务模块提供统一的数据读写与查询能力。

## 3. 编码约束

- 新增表字段、实体属性、mapper SQL 或 service 方法时，必须同步检查上下游调用与兼容影响。
- SQL 变更要尽量可读、可验证，避免把复杂业务规则直接塞进难维护的语句中。
- 数据访问异常不要静默吞掉，必要时向上抛出可定位的信息。
- 校验规则优先收敛到 validator 或 service 边界，不在多个调用点散落重复逻辑。

## 4. 测试与验证

- 当前目录修改后，优先验证：mapper 查询、写入更新、分页排序、事务联动和关键 service 行为。
- 高风险点：订单、库存、结算、租户隔离、多条件查询、SQL 兼容与历史数据兼容。
- 变更后需要同步检查：SQL 脚本、实体映射、上下游接口、文档和初始化数据。

## 5. OpenSpec 衔接

- 涉及当前目录的功能新增、行为变更、主流程调整时，默认先使用根规则中的 `openspec-superpowers-orchestrator` 作为统一入口，由它判断 `brainstorming` / `openspec-explore` 与后续阶段。
- 除已明确可按 `lightweight` 处理的极小改动外，不要绕过 `openspec-superpowers-orchestrator` 直接进入实现。
- `apply` 完成后自动进入 `review`。
- `review` 完成后停止等待用户确认，再决定是否 `archive`。
- 如果归档结论只影响当前目录，优先沉淀到当前 `AGENTS.md`。
