# 任务：extend-h5-next-suji-style-to-items-browse-pages

## 1. 建档与方向固化

- [x] 基于本地 HTML 预览确认本批商品列表页采用 `A` 方向。
- [x] 创建 proposal、design、tasks 和对应 spec 更新。
- [x] 明确本批继续沿用灰蓝主题、`8px` 圆角、小 hero、紧凑间距。

验收契约：

- proposal MUST 明确本次不修改后端接口和主题色 token。
- design SHALL 明确本批页面统一采用轻 hero 和紧凑商品流结构。

## 2. 搜索与分类结果页统一新风格

- [x] 改造 `src/pages/items/search/index.vue`。
- [x] 改造 `src/pages/items/category/index.vue`。
- [x] 如有新增展示 helper，补对应测试。

验收契约：

- 搜索页 MUST 继续保留热门搜索、历史搜索、搜索结果和商品详情跳转。
- 分类结果页 SHALL 继续保留分类结果加载与商品详情跳转能力。
- 页面不得再保留“后续补齐”类迁移期文案。

## 3. 品牌与专题页统一新风格

- [x] 改造 `src/pages/items/brand-list/index.vue`。
- [x] 改造 `src/pages/items/brand/index.vue`。
- [x] 改造 `src/pages/items/topic-list/index.vue`。
- [x] 改造 `src/pages/items/topic/index.vue`。

验收契约：

- 品牌馆 MUST 继续保留品牌详情跳转。
- 品牌详情页 SHALL 继续保留品牌商品详情跳转。
- 专题列表和专题详情 MUST 继续保留专题与商品详情跳转，以及现有正文渲染能力。

## 4. 新品 / 热卖 / 拼团页统一新风格

- [x] 改造 `src/pages/items/new/index.vue`。
- [x] 改造 `src/pages/items/hot/index.vue`。
- [x] 改造 `src/pages/items/groupon/index.vue`。

验收契约：

- 新品、热卖和拼团页 MUST 继续使用现有数据流和商品详情跳转。
- 拼团页 SHALL 继续展示真实成团人数与优惠字段，不得伪造额外运营数据。
- 三个页面风格必须与搜索、品牌、专题页保持同一套灰蓝 Suji 方向。

## 5. 验证与 review

- [x] 运行与本批改动直接相关的测试。
- [x] 运行 `type-check`。
- [x] 对本批商品页做本地 H5 视觉检查。
- [x] 创建 review 记录本次覆盖范围与残余风险。

验收契约：

- 在未提供最新测试和 `type-check` 证据前，不得声称本次完成。
- review MUST 记录本次仅覆盖商品列表批次，不自动扩展到登录页或更多页面。
