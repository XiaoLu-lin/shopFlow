# 提案：extend-h5-next-suji-style-to-items-browse-pages

## 背景与原因

`shopflow-h5-next` 的首页、分类页、商品详情页，以及订单和个人中心相关页面已经逐步统一到用户确认的灰蓝 Suji / ShopFlow 方向，但剩余商品浏览类页面仍保留早期迁移期样式：

- 头部结构偏重，说明型文案偏多
- 商品流卡片语言分散，列表页和内容页不连续
- 品牌、专题、拼团、搜索结果等页面仍有“先接通、后补齐”的迁移态描述

用户已在本地 HTML 预览中确认本批页面继续采用 `A` 方向，并要求沿用之前的协作方式：

- 继续使用 `ShopFlow` 对外品牌
- 继续沿用灰蓝主题、`8px` 圆角、小 hero、紧凑间距
- 先通过本地预览确认方向，再落真实页面

## 变更内容

本次 change 将在 `shopflow-h5-next` 内完成以下页面的统一改造：

1. 搜索与分类结果：
   - `src/pages/items/search/index.vue`
   - `src/pages/items/category/index.vue`
2. 品牌与专题：
   - `src/pages/items/brand-list/index.vue`
   - `src/pages/items/brand/index.vue`
   - `src/pages/items/topic-list/index.vue`
   - `src/pages/items/topic/index.vue`
3. 商品流页面：
   - `src/pages/items/new/index.vue`
   - `src/pages/items/hot/index.vue`
   - `src/pages/items/groupon/index.vue`
4. 在必要时补充展示 helper、测试和共享样式基座，使这一批页面保持同一套视觉语言。

本次变更不包含：

- 登录 / 注册页
- 首页、分类主入口页和商品详情页二次重做
- 后端接口或数据结构变更
- 新增真实筛选、排序、拼团业务流程

## 影响范围

- `shopflow-h5-next/src/pages/items/*`
- `shopflow-h5-next/src/features/goods/*`
- 对应测试文件

主要风险：

- 搜索、分类、品牌、专题页的文案收紧时，容易误伤当前路由入口或数据降级逻辑。
- 9 个页面同时换肤，如果没有统一展示 helper，容易再次出现风格漂移。
- 拼团页与专题详情页带有特殊信息块，如果处理过重，会脱离前面已确认的轻量方向。

预期收益：

- 让 `shopflow-h5-next` 的商品浏览域从首页到结果页、品牌页、专题页形成连续的浏览体验。
- 消除迁移期临时文案，使商品域页面更适合真实演示和后续上线。
- 为剩余登录域页面继续统一风格提供更稳定的基座。
