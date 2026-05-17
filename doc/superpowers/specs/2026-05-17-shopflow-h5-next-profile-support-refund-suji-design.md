# ShopFlow H5 Next 资料 / 服务 / 售后 Batch B Suji 方案设计稿

## 背景

在个人中心首页和 Batch A 用户页落地完成后，`shopflow-h5-next` 仍剩下一批用户页没有统一到同一套风格下：

- 资料设置与资料编辑链路
- 帮助中心、服务中心、意见反馈
- 售后列表、售后详情、申请售后

用户已先通过本地 HTML 预览确认 Batch B 页面继续沿用同一方向，并明确：

- 风格继续参考 Suji，但保留 `ShopFlow` 品牌和项目灰蓝主题
- 卡片、输入框、按钮圆角统一收敛到 `8px`
- hero 缩轻、缩小，不再抢首屏
- 页面结构以已批准的 Batch B 预览为准，不再额外增加装饰性模块

## 已确认结论

- 本批方向固定为已批准的 Batch B 预览。
- 继续沿用 `ShopFlow` 灰蓝主题 token：
  - `--sf-color-brand: 74 111 165`
  - `--sf-color-brand-soft: 229 237 246`
  - `--sf-color-brand-light: 109 140 184`
- 统一沿用小 hero、轻量白卡、`8px` 圆角、小字号与收紧间距。
- 不把 “Suji / 设计稿 / 参考稿” 等内部命名写进真实页面文案。
- 不修改资料、反馈、售后等页面现有接口与关键行为。

## 范围

本次只覆盖：

- `src/pages/user/profile/index.vue`
- `src/pages/user/profile-edit-nickname/index.vue`
- `src/pages/user/profile-edit-mobile/index.vue`
- `src/pages/user/profile-edit-password/index.vue`
- `src/pages/user/help/index.vue`
- `src/pages/user/service/index.vue`
- `src/pages/user/feedback/index.vue`
- `src/pages/user/refund/index.vue`
- `src/pages/user/refund-detail/index.vue`
- `src/pages/user/refund-apply/index.vue`

本次不包含：

- 新接口、新字段或协议变更
- 个人中心首页重新出方案
- 扩展到更多未进入 Batch B 预览确认的用户页

## 页面结构结论

### 1. 资料设置链路

- 资料设置页采用小 hero + 三到四条轻服务行。
- 昵称、手机号编辑页采用小 hero + 单字段表单 + 固定底部按钮。
- 重置密码页采用小 hero + 分段表单，验证码按钮为次级按钮。

### 2. 帮助 / 服务 / 反馈

- 帮助中心采用小 hero + FAQ 轻卡列表。
- 服务中心采用小 hero + 服务行列表，联系客服信息保留在轻量底部卡片中。
- 意见反馈采用小 hero + 轻表单分组，类型为小 chip，底部按钮固定。

### 3. 售后链路

- 售后列表页采用小 hero + 状态 chip + 紧凑售后记录卡。
- 售后详情采用分组信息卡，把状态、商品、售后信息和凭证图片区分清楚。
- 申请售后页采用商品摘要卡 + 分段表单 + 凭证上传区 + 固定底部提交按钮。

## 实现约束

- 保留当前资料读取和更新方式：
  - `fetchUserProfile`
  - `updateUserProfile`
  - `updateAuthProfile`
  - `persistLegacyProfile`
- 保留当前帮助、反馈、售后数据流：
  - `fetchIssueList`
  - `submitUserFeedback`
  - `fetchAftersaleList`
  - `fetchAftersaleDetail`
  - `submitAftersale`
  - `uploadAftersaleProof`
- 页面展示文案与 hero 信息优先收敛到 `page-display-utils.ts`，避免分散硬编码。
- 售后状态视觉统一改成真实页面可用 class，不继续依赖无效类名。

## 建议 change 名称

`extend-h5-next-suji-style-to-profile-support-refund-pages`

## 测试与验证

- 先补 `page-display-utils.ts` 新增元信息的测试。
- 至少运行：
  - `cd shopflow-h5-next && pnpm vitest run src/pages/user/page-display-utils.test.ts`
  - `cd shopflow-h5-next && pnpm type-check`
- 之后再检查 H5 编译产物或真实页面，确认资料、帮助服务反馈和售后三页已经落地到对应路由。

## Gate 1 / Gate 2 结论

- 当前理解：继续沿 Batch B 已批准预览，把剩余资料、支持、售后页统一到灰蓝 Suji A 轻量方向。
- 范围：只做 Batch B 已批准页面，不扩到其他未确认用户页。
- 推荐方案：直接按已批准预览结构落真实页面，并把共享 hero / 文案 / 状态规则沉淀到辅助文件。
- 风险控制：不改接口，不改业务流程，先补共享测试，再分组改资料页、支持页、售后页，最后统一验证。
