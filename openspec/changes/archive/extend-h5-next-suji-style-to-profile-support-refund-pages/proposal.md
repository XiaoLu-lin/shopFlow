# 提案：extend-h5-next-suji-style-to-profile-support-refund-pages

## 背景与原因

`shopflow-h5-next` 的个人中心首页，以及地址、地址编辑、优惠券、收藏页已经完成用户确认的 Suji A 灰蓝轻量方向，但剩余用户页仍保留旧一批较重、较工具化的列表和表单样式。

用户已在本地 HTML 预览中确认本批次继续沿用同一套方向，并明确要求：

- 继续使用现有 `ShopFlow` 灰蓝主题，不改主品牌色
- 继续保持 `8px` 左右圆角、小 hero、轻字号、轻间距
- 更贴近已确认的 Suji 预览结构，不再加入额外增强信息块或偏离预览的装饰

本批次将把剩余核心用户页统一到这套风格下，补齐个人资料、帮助服务反馈和售后链路的视觉一致性。

## 变更内容

本次 change 只在 `shopflow-h5-next` 内完成以下工作：

1. 改造资料设置链路页面：
   - `src/pages/user/profile/index.vue`
   - `src/pages/user/profile-edit-nickname/index.vue`
   - `src/pages/user/profile-edit-mobile/index.vue`
   - `src/pages/user/profile-edit-password/index.vue`
2. 改造帮助 / 服务 / 反馈页面：
   - `src/pages/user/help/index.vue`
   - `src/pages/user/service/index.vue`
   - `src/pages/user/feedback/index.vue`
3. 改造售后链路页面：
   - `src/pages/user/refund/index.vue`
   - `src/pages/user/refund-detail/index.vue`
   - `src/pages/user/refund-apply/index.vue`
4. 在必要时补充或调整页面展示辅助逻辑与测试，使本批页面统一沿用同一套 hero、文案和状态视觉规则。

本次变更不包含：

- 新增或修改后端接口
- 修改资料、反馈、售后的业务行为与路由契约
- 修改 `ShopFlow` 灰蓝主题 token
- 扩展到更多未纳入预览确认的用户页

## 影响范围

- `shopflow-h5-next/src/pages/user/profile/*`
- `shopflow-h5-next/src/pages/user/help/index.vue`
- `shopflow-h5-next/src/pages/user/service/index.vue`
- `shopflow-h5-next/src/pages/user/feedback/index.vue`
- `shopflow-h5-next/src/pages/user/refund*.vue`
- `shopflow-h5-next/src/pages/user/page-display-utils.ts`
- `shopflow-h5-next/src/pages/user/aftersale-utils.ts`
- 对应测试文件

主要风险：

- 资料页和售后页有真实编辑、提交、撤销等交互，样式重构时可能误伤按钮层级与点击路径。
- 售后列表和详情页的状态色、金额与操作按钮如果处理不当，容易影响信息可读性。
- 服务中心和帮助中心本身信息密度较低，过度装饰或过度收缩都可能让页面显得空或弱。

预期收益：

- 让 `shopflow-h5-next` 的个人中心及其主要子页面形成连续统一的用户体验。
- 为后续剩余用户页继续沿同一方式扩展提供稳定模板。
- 在不改接口和业务行为的前提下，明显提升页面完成度与对外可展示性。
