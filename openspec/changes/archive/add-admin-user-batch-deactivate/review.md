# Review: add-admin-user-batch-deactivate

## Scope Check

- 已覆盖会员管理列表当前页多选批量注销能力。
- 已覆盖“仅未注销会员可选、已注销会员禁选”的页面约束。
- 已覆盖前后端批量注销接口、汇总提示、列表刷新和选中状态清理。
- 已复用现有单条“注销会员”的权限口径，未新增独立权限点。

## Verification

- 执行 `./node_modules/.bin/eslint --no-ignore --ext .js,.vue src/views/user/user.vue src/api/user.js`，前端定点 lint 通过。
- 执行 `mvn -pl shopflow-admin-api -am -Dmaven.test.skip=false -Dtest=AdminUserControllerTest -Dsurefire.failIfNoSpecifiedTests=false test`，后端批量注销单测通过。
- 执行 `mvn -pl shopflow-admin-api -am -Dmaven.test.skip=true compile`，后端编译通过。
- 本地运行态已确认管理端 `9527` 与后端 `6914` 连通，`/admin/user/batch-delete` 返回不再为 404。

## Findings And Resolutions

- 初次联调时后端运行的是旧版 `shopflow-all` 可执行包，导致新接口路径返回 404。
- 已重新打包并启动最新后端，确认新接口已生效。
- 前端会员页是历史老风格文件，定点 lint 初次存在格式问题和 ESLint 规则笔误。
- 已仅针对本次变更相关文件完成整理，并移除无效的 ESLint 规则项，确保验证可执行。

## Residual Risk

- 当前仅完成本地启动联调与定点验证，未补充浏览器自动化或完整业务回归脚本。
- 后端启动会继续生成 smart-doc 静态文档，运行期间可能带来接口文档首页的时间戳型变更，这属于项目现有副作用。

## Archive Recommendation

- 当前实现、验证证据、review 结论和 OpenSpec 工件已对齐，适合归档。
