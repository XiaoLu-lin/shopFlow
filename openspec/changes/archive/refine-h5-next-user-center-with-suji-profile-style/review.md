# Review: refine-h5-next-user-center-with-suji-profile-style

## 检查范围

- `shopflow-h5-next/src/pages/user/index.vue`
- `shopflow-h5-next/src/pages/user/dashboard-utils.ts`
- `shopflow-h5-next/src/pages/user/dashboard-utils.test.ts`
- `shopflow-h5-next/src/pages/user/entry-utils.test.ts`

## 核对结论

- 个人中心页已经切换为用户确认的 `A. 原版 Suji 感` 结构：
  - 渐变头部
  - 我的订单五宫格
  - 列表式服务入口
- 现有订单、购物车、收藏、地址、优惠券、售后、帮助、服务与设置入口均保留。
- 页面未引入伪造的用户 ID、等级、积分等参考稿装饰数据，仍只消费当前真实 session 与 `/user/index` 统计字段。

## 验证证据

2026-05-16 最新执行结果：

- `cd shopflow-h5-next && npm run test -- src/pages/user/dashboard-utils.test.ts src/pages/user/entry-utils.test.ts`
  - 结果：通过，`2` 个测试文件、`5` 条测试全部通过
- `cd shopflow-h5-next && npm run type-check`
  - 结果：通过
- H5 视觉检查
  - 结果：已在 `http://127.0.0.1:6257/#/pages/user/index` 打开并检查，页面已呈现 A 方案结构

## 发现与处理

- 发现：原个人中心页仍是说明卡 + 双列快捷入口，和用户选定的 Suji A 方案差距明显。
  - 处理：把头部、订单区和服务区分别重构成更贴近参考稿的三段式结构。
- 发现：原展示辅助逻辑不足以支撑 A 方案中的头部眉文、手机号脱敏和服务入口顺序。
  - 处理：在 `dashboard-utils.ts` 中补齐对应纯函数逻辑，并用单测守住。

## 残余风险

- 当前头部主标题仍依赖现有 session 中的 `nickName`；如果历史登录态把手机号当昵称写入，首屏会显示手机号而不是更友好的昵称。
- 本次只处理了个人中心首页，未继续同步地址、优惠券、收藏、帮助、服务等子页面的 Suji 风格。
