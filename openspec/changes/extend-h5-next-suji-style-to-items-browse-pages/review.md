# Review：extend-h5-next-suji-style-to-items-browse-pages

## 1. 覆盖结论

- 本次已覆盖 `shopflow-h5-next` 商品浏览批次的 9 个页面：
  - `search`
  - `category`
  - `brand-list`
  - `brand`
  - `new`
  - `hot`
  - `topic-list`
  - `topic`
  - `groupon`
- 页面已统一到同一套灰蓝 ShopFlow 浏览基座：
  - `8px` 圆角
  - 小 hero
  - 紧凑单列商品流 / 内容卡
  - 无迁移态“后续补齐”文案

## 2. 本次确认点

- 搜索页继续保留热门搜索、最近浏览、搜索结果和商品详情跳转。
- 分类结果页继续保留分类商品流和同级分类切换，当前分类已纳入胶囊区高亮。
- 品牌馆、品牌详情、专题列表、专题详情继续保留原有详情跳转与专题正文渲染。
- 新品、热卖、拼团页继续使用原有数据流；拼团页仅展示真实成团人数与优惠字段，没有新增伪造运营数据。

## 3. 验证证据

- 展示 helper 与相关路由 / 接口测试：
  - `cd shopflow-h5-next && pnpm vitest run src/entities/goods/api.test.ts src/shared/utils/legacy-route-map.test.ts src/features/goods/browse-display-utils.test.ts`
- 类型检查：
  - `cd shopflow-h5-next && pnpm type-check`
- H5 构建验证：
  - `cd shopflow-h5-next && pnpm build:h5`
- 本地 H5 首屏检查：
  - 使用 Playwright 访问 `http://127.0.0.1:6257/#/pages/items/...`
  - 已确认 `search / category / brand-list / topic-list / new / hot / groupon` 命中新文案和新结构

## 4. 残余风险

- 本轮 H5 自动检查聚焦首屏关键文案和结构，没有覆盖每个页面的深入点击路径。
- `brand` 与 `topic` 详情页的真实线上数据组合仍建议后续再做一次人工浏览复核，重点看空态、图片缺失和长正文场景。
- `pnpm build:h5` 通过，但项目仍存在来自 `uview-plus` 和历史 `@import` 的 Sass 废弃告警；这不是本次改动引入的问题。
