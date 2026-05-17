# 提案：extend-h5-next-suji-style-to-address-coupon-collect-pages

## 背景与原因

`shopflow-h5-next` 的个人中心首页已经完成 `A. 原版 Suji 感` 方向确认与落地，但其下游用户页仍主要停留在较重、较工具化的交易页样式中，与当前用户已确认的个人中心气质存在断层。

在本地视觉预览中，用户已明确确认本批次优先处理：

- 地址列表页
- 地址编辑页
- 优惠券页
- 收藏页

并最终确认以下视觉方向：

- 采用 `A. 轻生活感`
- 继续沿用 `ShopFlow` 当前灰蓝主题，不再修改主题色
- 常规卡片、按钮、输入框和票券主体统一收敛到 `8px` 左右圆角
- hero 头部缩小、减轻，不再占过大首屏比重
- 字号和留白整体收一档

## 变更内容

本次 change 将只在 `shopflow-h5-next` 内完成以下工作：

1. 将 `src/pages/user/address/index.vue` 改造为更轻量的地址簿式页面。
2. 将 `src/pages/user/address-edit/index.vue` 改造成轻量表单风格，同时保留现有地址编辑行为。
3. 将 `src/pages/user/coupon/index.vue` 调整为灰蓝主题下更紧凑的用户票夹式布局。
4. 将 `src/pages/user/collect/index.vue` 调整为更轻的收藏清单式页面。
5. 在必要时补充对应页面的展示辅助逻辑与测试，确保风格改动不破坏现有行为。

本次变更不包含：

- 资料设置、帮助、服务、反馈页面
- 售后相关页面
- 新增后端接口或修改协议
- 修改品牌色 token

## 影响范围

- `shopflow-h5-next/src/pages/user/address/index.vue`
- `shopflow-h5-next/src/pages/user/address-edit/index.vue`
- `shopflow-h5-next/src/pages/user/coupon/index.vue`
- `shopflow-h5-next/src/pages/user/collect/index.vue`
- 视需要影响少量对应页面的展示辅助文件与测试

主要风险：

- 地址列表与编辑页视觉收紧时，可能误伤现有点击选择、编辑、保存和删除入口。
- 优惠券票券结构收紧后，若层级处理不当，可能影响状态信息可读性。
- 收藏页变轻后，若信息密度控制不好，可能损失商品信息扫描效率。

预期收益：

- 让个人中心首页与核心下游用户页形成一套连续的用户体验。
- 建立 `shopflow-h5-next` 用户页后续批次的稳定视觉模板。
- 在不改主题色与后端行为的前提下，提升页面一致性与完成度。
