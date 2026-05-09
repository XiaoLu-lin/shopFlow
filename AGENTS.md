# 项目根目录 Codex / OpenSpec 说明

> 适用范围：项目根目录及未被更深层 `AGENTS.md` 覆盖的子目录。

本文件只沉淀长期有效的协作规则，不记录一次性任务背景或短期调试结论。
本文件只负责全局规则；每个关键模块目录都应补自己的 `AGENTS.md` 承载局部规则。

## 1. OpenSpec 默认规则

- 涉及功能新增、功能修改、主流程调整、接口或数据结构变化、较大修复时，默认先走 OpenSpec。
- 涉及管理端页面行为、小程序交互流程、后端接口返回结构、数据库模型与 SQL 变更时，也必须先走 OpenSpec，不要绕过 `explore / propose` 直接改实现。
- 默认遵循官方 OpenSpec 主流程：`explore -> propose -> apply -> archive`。
- 本项目采用增强治理：`apply` 完成后自动进入 `review`，`review` 完成后停止并等待用户确认，再决定是否 `archive`。
- `openspec/changes/` 用于活跃 change，`openspec/changes/archive/` 仅作历史保留。
- proposal、design、specs、tasks 应与代码状态保持同步，避免长期漂移。
- 进入 coding / apply 阶段后，显式使用 `superpowers:test-driven-development`、`superpowers:requesting-code-review`、`superpowers:verification-before-completion`。

## 2. 确认闸门

- 第一确认闸门：完成代码调查、上下游影响分析、方案比较后，先向用户汇总理解、范围、候选方案和 change 名称；用户确认后再创建或更新 proposal/design/specs/tasks。
- 第二确认闸门：OpenSpec 文档准备完成后，先向用户汇总关键假设、方案、任务拆分和风险；用户确认后再进入 apply。

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

- Java 纯逻辑改动优先补单元测试。
- 涉及 Spring Boot 接口、权限、数据访问、事务或跨模块联动时，至少做集成验证。
- 管理端改动至少验证对应页面、接口联调、权限与异常提示。
- 小程序改动至少验证页面流程、授权路径、支付或下单相关上下游联调。
- 如果改动数据库、配置、迁移、静态资源或外部依赖，同时检查 SQL、文档、部署说明和初始化脚本是否需要同步。

## 6. review 与 archive

- `apply` 完成后，自动进入独立 review。
- review 重点确认：需求覆盖、验证充分性、行为回归、边界遗漏、残余风险。
- review 完成后，先向用户汇总结论并等待确认，再决定是否 archive。
- archive 前先把长期有效规则沉淀到最合适层级的 `AGENTS.md` 或项目文档。

## 7. 输出与文风

- 默认输出语言：中文。
- 新增说明文档、日志文案、提示信息优先保持清晰可读。
- 注释只写必要信息，重点说明意图、边界和副作用。

## 8. 本地启动速记

- 本地后端统一启动聚合模块 `shopflow-all`，默认端口为 `6914`。
- 后端依赖本机 MySQL 与 Redis：MySQL 配置见 `shopflow-db/src/main/resources/application-db.yml`，Redis 默认为 `127.0.0.1:6379`、db `0`。
- 后端打包命令：`mvn -pl shopflow-all -am -DskipTests package`。
- 后端启动命令：`java -jar shopflow-all/target/shopflow-all-0.1.0-exec.jar`。
- 后端启动后可用 `curl -s -X POST http://localhost:6914/wx/home/auth -H 'Content-Type: application/json' --data '{"appid":"1649067"}'` 验证，正常应返回 `errno: success` 且 `data` 为租户 token 字符串。
- H5 前台目录为 `litemall-vue`，推荐本地端口为 `6256`。
- H5 前台启动命令：`cd litemall-vue && VUE_APP_BASE_API=http://localhost:6914/wx VUE_APP_SHOPFLOW_APPID=1649067 npm run serve -- --port 6256`。
- H5 访问地址：`http://localhost:6256/#/`，登录页通常为 `http://localhost:6256/#/login?redirect=user`。
- `litemall-vue/vue.config.js` 里历史默认端口 `6255`、代理目标 `http://localhost:8080`，本地联调优先使用上面的显式 `VUE_APP_BASE_API=http://localhost:6914/wx`，不要默认依赖 `8080`。
