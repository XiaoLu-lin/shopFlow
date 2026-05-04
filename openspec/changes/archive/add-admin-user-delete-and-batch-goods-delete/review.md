# Review: add-admin-user-delete-and-batch-goods-delete

## Scope Check

- 已覆盖会员管理单条会员治理能力，最终实现为“注销会员”而非物理删除。
- 已覆盖商品列表多选与批量删除能力。
- 前后端接口、权限点、页面提示和刷新链路均已联动调整。

## Verification

- 在干净 worktree 中执行 `mvn -pl shopflow-all -am package -DskipTests`，构建通过。
- 在干净 worktree 中执行 `NODE_OPTIONS=--openssl-legacy-provider npm run build`，前端生产构建通过。
- 本地运行态已确认 `6914` 后端和 `9527` 管理端端口可用。

## Findings And Resolutions

- 初版会员删除实现采用物理删除，存在破坏订单、通知等历史依赖链路的风险。
- 已调整为状态注销实现，避免直接移除会员主数据。
- 商品批量删除沿用 `goodsCoreService.goodsDelete(id)`，保留现有商品级联清理行为。
- 商品批量删除对不存在的商品 id 采用跳过策略，避免重复删除或历史脏数据导致整批失败。

## Residual Risk

- 当前前端与接口命名仍沿用“delete”语义，实际行为为注销，后续如果要进一步统一语义，可在独立 change 中再做接口命名治理。
- 项目现有前端构建存在若干历史 warning，但本次未新增阻塞性错误。

## Archive Recommendation

- 当前实现、验证和文档已同步，适合归档。
