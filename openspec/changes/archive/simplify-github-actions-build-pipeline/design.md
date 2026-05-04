# Design: simplify-github-actions-build-pipeline

## Overview

本次改动只涉及 GitHub Actions workflow 配置，不修改业务代码，也不改变现有服务器脚本。

整体策略：

- 保留 `build-backend` job 作为后端构建与 jar 产物归档节点。
- 保留 `build-frontend` job 作为前端依赖安装、生产构建与 dist 产物归档节点。
- 删除 `deploy` job 及其所有服务器上传、脚本分发和远程执行步骤。

## Workflow Simplification

保留部分：

- `workflow_dispatch`
- `push` 到 `main` / `master`
- Java 8 + Maven 构建
- Node 18 + Vue 构建
- `upload-artifact` 产物上传

删除部分：

- `deploy` job
- `download-artifact` 步骤
- `appleboy/scp-action` 上传服务器步骤
- `appleboy/ssh-action` 远程执行部署脚本步骤
- deploy job 对服务器 secrets 的依赖

## Compatibility

- 保持 workflow 文件名不变，避免影响仓库已有入口与使用习惯。
- 保持前后端 job 名称与 artifact 名称不变，避免影响后续人工下载构建产物。
- 不改动 `docker/deploy.sh` 等已有部署脚本，避免与当前服务器运维方式耦合。

## Verification

- workflow YAML 结构应保持合法。
- workflow 至少保留两个独立 job：后端打包、前端构建。
- workflow 中不应再包含服务器连接、上传或远程执行步骤。
