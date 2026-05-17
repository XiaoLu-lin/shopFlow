# Review: extend-h5-next-suji-style-to-profile-support-refund-pages

## 检查范围

- `shopflow-h5-next/src/pages/user/profile/index.vue`
- `shopflow-h5-next/src/pages/user/profile-edit-nickname/index.vue`
- `shopflow-h5-next/src/pages/user/profile-edit-mobile/index.vue`
- `shopflow-h5-next/src/pages/user/profile-edit-password/index.vue`
- `shopflow-h5-next/src/pages/user/help/index.vue`
- `shopflow-h5-next/src/pages/user/service/index.vue`
- `shopflow-h5-next/src/pages/user/feedback/index.vue`
- `shopflow-h5-next/src/pages/user/refund/index.vue`
- `shopflow-h5-next/src/pages/user/refund-detail/index.vue`
- `shopflow-h5-next/src/pages/user/refund-apply/index.vue`
- `shopflow-h5-next/src/pages/user/page-display-utils.ts`
- `shopflow-h5-next/src/pages/user/page-display-utils.test.ts`
- `shopflow-h5-next/src/pages/user/aftersale-utils.ts`

## 结论

- Batch B 已批准页面已经统一到同一套灰蓝轻量 Suji 方向。
- 资料设置链路、帮助 / 服务 / 反馈页和售后链路都已换成小 hero、轻卡片、`8px` 圆角和更收紧的间距体系。
- 页面继续保留现有资料更新、FAQ 展开、联系客服、反馈提交、售后查看 / 撤销 / 提交等关键行为。
- 售后状态样式已从无效 class 返回值改为页面可直接接住的状态标签类。
- 售后详情页原先依赖详情接口中的 `statusText`，这次已补成基于 `status` 的稳定前端映射，不再误回退为“处理中”。

## 验证证据

2026-05-17 最新执行结果：

- `cd shopflow-h5-next && pnpm vitest run src/pages/user/page-display-utils.test.ts`
  - 结果：通过，`1` 个测试文件、`4` 条测试全部通过
- `cd shopflow-h5-next && pnpm vitest run src/pages/user/page-display-utils.test.ts src/pages/user/dashboard-utils.test.ts src/pages/user/entry-utils.test.ts src/pages/user/user-list-utils.test.ts`
  - 结果：通过，`4` 个测试文件、`12` 条测试全部通过
- `cd shopflow-h5-next && pnpm vitest run src/pages/user/aftersale-utils.test.ts`
  - 结果：通过，`1` 个测试文件、`3` 条测试全部通过
- `cd shopflow-h5-next && pnpm vitest run src/pages/user/page-display-utils.test.ts src/pages/user/dashboard-utils.test.ts src/pages/user/entry-utils.test.ts src/pages/user/user-list-utils.test.ts src/pages/user/aftersale-utils.test.ts`
  - 结果：通过，`5` 个测试文件、`15` 条测试全部通过
- `cd shopflow-h5-next && pnpm type-check`
  - 结果：通过，`vue-tsc --noEmit` 退出码 `0`
- 编译产物检查
  - `http://127.0.0.1:6257/src/pages/user/profile/index.vue`
  - `http://127.0.0.1:6257/src/pages/user/help/index.vue`
  - `http://127.0.0.1:6257/src/pages/user/service/index.vue`
  - `http://127.0.0.1:6257/src/pages/user/refund/index.vue`
  - `http://127.0.0.1:6257/src/pages/user/refund-detail/index.vue`
  - `http://127.0.0.1:6257/src/pages/user/refund-apply/index.vue`
  - 结果：已能检索到本轮新文案与结构关键字，说明 dev server 已编译到最新页面代码

## 发现与处理

- 发现：售后状态 helper 原先返回的是页面内并不存在的 class 名，真实状态色无法可靠生效。
  - 处理：改为统一返回 `status-tag--brand / success / muted / plain`，由售后列表和详情页样式直接接住。
- 发现：资料页、支持页和售后页仍保留上一批 `12rpx` 旧圆角和偏厚表单 / 卡片结构。
  - 处理：全部收敛回当前 Batch B 的 `8px` 轻量结构和灰蓝 token。
- 发现：售后详情页读取 `detail.aftersale.statusText`，但详情接口返回的 `aftersale` 实体并不补 `statusText`，页面会错误回退到“处理中”。
  - 处理：在 `aftersale-utils.ts` 中新增 `resolveAftersaleStatusText(status)`，并让详情页优先使用接口文案、缺失时回退到基于 `status` 的前端映射；同时补 `aftersale-utils.test.ts` 覆盖状态文案和状态色规则。

## 残余风险

- 当前验证以类型检查、现有用户页测试和编译产物检查为主，尚未追加新的页面级 E2E。
- 售后详情和申请页需要真实订单数据才能完整走到所有交互分支，当前仍主要保证页面结构、类型和现有逻辑未被改断。
- 本次仅覆盖 Batch B 已批准页面，地址 / 优惠券 / 收藏之外的其他剩余用户页仍需按同一流程继续推进时再单独确认范围。

## 归档建议

- 当前 review 阶段未再发现阻塞归档的问题。
- 如果你要继续走 archive，下一步可以进入 verify / archive 收尾。
