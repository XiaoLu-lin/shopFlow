# Tasks: simplify-github-actions-build-pipeline

## 1. OpenSpec

- [x] 明确现有 workflow 中前端构建、后端打包和自动部署职责。
- [x] 完成 proposal、design、tasks 文档，明确只保留构建链路。

## 2. Workflow 精简

- [x] 更新 `.github/workflows/deploy.yml`，删除自动部署 job。
- [x] 保留后端 Maven 打包与 jar artifact 上传。
- [x] 保留前端 Vue 构建与 dist artifact 上传。
- [x] 检查 workflow 触发条件与 job 依赖，确保删除 deploy 后 YAML 仍清晰可用。

## 3. 复核

- [x] 复核 workflow 中不再依赖服务器 secrets、scp 或 ssh 部署动作。
- [x] 运行最小必要校验，确认 YAML 结构和关键命令符合预期。
- [x] 在 review 中记录“当前流程为纯构建 CI，不负责自动部署”的结论。
