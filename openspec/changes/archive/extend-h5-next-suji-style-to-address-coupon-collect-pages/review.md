# Review: extend-h5-next-suji-style-to-address-coupon-collect-pages

## 检查范围

- `shopflow-h5-next/src/pages/user/address/index.vue`
- `shopflow-h5-next/src/pages/user/address-edit/index.vue`
- `shopflow-h5-next/src/pages/user/coupon/index.vue`
- `shopflow-h5-next/src/pages/user/collect/index.vue`
- `shopflow-h5-next/src/pages/user/page-display-utils.ts`
- `shopflow-h5-next/src/pages/user/page-display-utils.test.ts`

## 结论

- 地址列表、地址编辑、优惠券和收藏页已经统一到本轮确认的 A 方向。
- 页面继续保持 `ShopFlow` 现有灰蓝主题，没有再引入新的暖色主题。
- hero 头部、字号和间距已整体收紧，页面不再保留早期预览中的重头部方向。
- 现有关键行为仍保留：
  - 地址选择 / 编辑 / 新增
  - 地址保存 / 删除
  - 优惠券状态切换
  - 收藏详情跳转 / 取消收藏

## 验证证据

2026-05-17 最新执行结果：

- `cd shopflow-h5-next && pnpm vitest run src/pages/user/page-display-utils.test.ts src/pages/user/dashboard-utils.test.ts src/pages/user/entry-utils.test.ts src/pages/user/user-list-utils.test.ts src/pages/user/aftersale-utils.test.ts`
  - 结果：通过，`5` 个测试文件、`15` 条测试全部通过
- `cd shopflow-h5-next && pnpm type-check`
  - 结果：通过，`vue-tsc --noEmit` 退出码 `0`
- H5 页面检查
  - `http://127.0.0.1:6257/#/pages/user/address/index`
  - `http://127.0.0.1:6257/#/pages/user/address-edit/index?addressId=-1`
  - `http://127.0.0.1:6257/#/pages/user/coupon/index?active=0`
  - `http://127.0.0.1:6257/#/pages/user/collect/index`
  - 结果：四个页面均可打开，未出现白屏

## 发现与处理

- 发现：预览阶段曾偏离已确认灰蓝主题，出现暖杏偏色。
  - 处理：在实现阶段统一回到 `ShopFlow` 现有灰蓝 token。
- 发现：真实页面引用展示 utils 时，因页面位于各自子目录下导致相对路径写错。
  - 处理：统一修正为 `../page-display-utils`，恢复类型检查通过路径。

## 残余风险

- 当前浏览器验页已确认四个页面可以正常打开，但本轮还没有补更深的页面级交互自动化测试。
- 本次只处理了地址、地址编辑、优惠券和收藏；帮助、服务、反馈、资料设置、售后等剩余用户页仍待后续批次统一。
