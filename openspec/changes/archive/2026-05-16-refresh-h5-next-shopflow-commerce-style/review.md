# Review: refresh-h5-next-shopflow-commerce-style

## 检查范围

- `shopflow-h5-next` 共享 UI token 与 Vant 基础变量映射。
- 首页第一阶段商城化布局。
- 分类页左右分栏浏览布局。
- 商品详情页购买导向布局。
- 首页 feed/区块显隐 helper 与详情展示 helper。

## 需求覆盖

- 已保留 `ShopFlow` 品牌，没有引入面向用户的 Suji 命名。
- 已将主视觉从亮蓝迁移到灰蓝克制方向，常规卡片/面板按约 `8px` 产品圆角落地。
- 首页已接入 `banner`、`channel`、`couponList`、`grouponList`、`newGoodsList`、`hotGoodsList`，并加入 `推荐 / 新品 / 热销` 商品流。
- 分类页保留现有分类数据流和子分类跳转契约。
- 商品详情页保留 SKU、收藏、加购、立即购买和 checkout flow context 写入行为。
- 缺失真实评价、销量、地区、优惠券时使用保守文案或隐藏，不展示伪造业务事实。

## 发现

- 未发现阻塞性问题。
- `npm run test` 初次失败在 `src/shared/platform/navigation.test.ts`：测试仍按旧的 `switchTab/redirectTo` 语义断言，而当前实现已统一 `reLaunch`。已将测试对齐当前实现契约，并重新验证通过。
- 根据 2026-05-16 视觉反馈，首页分类区曾因入口底色块过重、图标块过大而与整体不协调；已将分类入口调整为轻量导航样式，并移除推荐商品区对 `overflow: hidden` 的额外依赖。
- H5 构建存在 Sass/uView 既有 deprecation warning，来源于 `uview-plus/theme.scss` 和 Sass legacy API，不影响本次构建产物。

## 验证证据

- `npm run test -- home-display-utils`：通过，4 个测试通过。
- `npm run test -- detail-display-utils`：通过，3 个测试通过。
- `npm run type-check`：通过，退出码 0。
- `npm run test`：通过，18 个测试文件、86 个测试全部通过。
- `npm run build:h5`：通过，退出码 0，输出 `DONE Build complete.`。
- H5 dev server：`npm run dev:h5` 成功启动在 `http://localhost:6257/`。
- H5 应用壳冒烟：`curl -I http://localhost:6257/` 返回 `HTTP/1.1 200 OK`，页面 HTML 标题为 `ShopFlow`。
- 2026-05-16 10:07 CST，`npm run test -- home-display-utils`：通过，4 个测试通过。
- 2026-05-16 12:29 CST，`npm run test`：通过，18 个测试文件、86 个测试全部通过。
- 2026-05-16 12:30 CST，`npm run build:h5`：通过，输出 `DONE Build complete.`；仍有 uView/Sass 既有 deprecation warning。

## 残余风险

- 本阶段只覆盖首页、分类页和商品详情页，购物车、个人中心、结算、订单等页面仍保留旧视觉风格。
- 商品详情页的真实评价、销量、地区和优惠券能力受当前接口字段限制，后续如需完整设计稿效果，需要补接口或新增前端数据来源。
- 视觉冒烟当前以 dev server 可达和应用壳返回为证据，尚未用真实后端数据逐页人工截图验收。
