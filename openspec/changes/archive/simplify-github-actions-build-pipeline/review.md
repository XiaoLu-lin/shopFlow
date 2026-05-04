# Review: simplify-github-actions-build-pipeline

## Scope Check

- 已将 GitHub Actions 工作流从 “构建 + 自动部署” 精简为纯构建 CI。
- 已保留后端 Maven 打包与 jar artifact 上传。
- 已保留前端 Vue 构建与 dist artifact 上传。
- 已移除未使用的自动部署 job。

## Verification

- 检查了 [`.github/workflows/deploy.yml`](/Users/lhl/Desktop/code/litemall-plus-master/.github/workflows/deploy.yml) 最终结构，确认只保留 `build-backend` 和 `build-frontend` 两个 job。
- 使用文本检索确认 workflow 中不再包含 `scp-action`、`ssh-action`、`download-artifact`、服务器 secrets 或 `Deploy to Server` 节点。
- 触发条件 `workflow_dispatch` 与 `push -> main/master` 保持不变。

## Findings And Resolutions

- 原 workflow 中 `deploy` job 同时承担上传构建产物、上传部署脚本和远程重启服务职责，但当前并未实际使用。
- 已将这些远程部署步骤整体移除，避免 CI 继续依赖未使用的服务器连接配置。

## Residual Risk

- 如果后续仍需要 GitHub Actions 自动发版，需要在独立 change 中重新设计并引入新的部署流程，而不是直接恢复旧 job。

## Archive Recommendation

- 当前变更范围清晰、文档已同步，适合在用户确认后归档。
