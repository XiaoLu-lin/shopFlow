# 任务：refresh-h5-next-shopflow-commerce-style

## 1. 视觉基座

- [x] 将 `shopflow-h5-next/src/shared/ui/tokens.css` 和 `base.css` 更新为已确认的 ShopFlow 灰蓝风格。
- [x] 将 Vant 变量和页面局部颜色对齐到共享 token。
- [x] 常规组件圆角保持在 `8px` 左右，pill 控件保留全圆角。

验收契约：

- 共享 token 必须保留 `ShopFlow` 品牌语义，不得引入面向用户的 Suji 命名。
- 常规卡片和面板样式应收敛到 `8px` 左右圆角。
- 价格、品牌、边框、背景和辅助文字颜色必须来自共享 token 或映射后的 CSS 变量。

## 2. 首页改造

- [x] 将 `src/pages/home/index.vue` 重构为第一阶段商城首页布局。
- [x] 在数据可用时渲染现有 `banner`、`channel`、`couponList`、`grouponList`、`newGoodsList` 和 `hotGoodsList`。
- [x] 增加第一阶段商品流 tabs：`推荐 / 新品 / 热销`。
- [x] 移除页面渲染中的迁移期文案。
- [x] 为抽取出的首页 feed 或区块显隐逻辑补聚焦测试。

验收契约：

- 数据存在时，首页必须展示品牌/搜索头、Banner、分类宫格和商品流。
- 优惠券和秒杀区块应仅在匹配数据存在时渲染。
- 首页不得渲染“持续迁移中”“后续补齐”或等价迁移占位文案。

## 3. 分类页改造

- [x] 将 `src/pages/items/catalog/index.vue` 重构为已确认的分栏分类布局。
- [x] 保留现有 `fetchCatalogList` 和 `fetchCurrentCatalog` 数据流。
- [x] 保留到 `pages/items/category/index` 的子分类导航。
- [x] 为没有子分类的分类增加空态处理。

验收契约：

- 分类页必须展示左侧侧栏和右侧当前分类内容区。
- 选中分类应使用共享灰蓝选中态。
- 点击子分类必须保留现有 `categoryId` 和 `title` 路由契约。

## 4. 商品详情页改造

- [x] 将 `src/pages/items/detail/index.vue` 重构为已确认的详情布局。
- [x] 保留 SKU 选择、数量控制、收藏切换、加入购物车和立即购买行为。
- [x] 增加详情头部、促销/服务区块、安全详情内容处理和五段式底部操作栏。
- [x] 为抽取出的详情展示 helper 补聚焦测试。

验收契约：

- 详情页必须保留现有加入购物车和立即购买 API 行为。
- 立即购买必须继续在跳转确认订单前写入 checkout flow context。
- 缺失的评价、销量、地区或优惠券数据不得被渲染成伪造事实。

## 5. 验证与 review

- [x] 在 `shopflow-h5-next` 中运行 `npm run type-check`。
- [x] 在 `shopflow-h5-next` 中运行 `npm run test`。
- [x] 在 `shopflow-h5-next` 中运行 `npm run build:h5`。
- [x] 对首页、分类页、详情页执行 H5 浏览器视觉冒烟检查。
- [x] 实现后创建 `openspec/changes/refresh-h5-next-shopflow-commerce-style/review.md`。

验收契约：

- 验证证据必须包含类型检查、单元测试和 H5 构建结果。
- Review 应记录需求覆盖、视觉风险、行为回归风险，以及后续阶段剩余页面。
