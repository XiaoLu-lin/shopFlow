# 任务：extend-h5-next-suji-style-to-profile-support-refund-pages

## 1. Batch B 建档与约束固化

- [x] 基于已批准的 Batch B HTML 预览确认页面方向。
- [x] 创建 proposal、design、tasks、spec 和对应设计稿、实施计划。
- [x] 明确本批继续沿用灰蓝主题、`8px` 圆角、小 hero、轻间距，不改业务接口。

验收契约：

- proposal MUST 明确本次不修改后端接口和主题色 token。
- design SHALL 明确资料、支持、售后页面统一采用轻 hero 和紧凑卡片结构。

## 2. 资料设置链路统一新风格

- [x] 改造 `src/pages/user/profile/index.vue`。
- [x] 改造 `src/pages/user/profile-edit-nickname/index.vue`。
- [x] 改造 `src/pages/user/profile-edit-mobile/index.vue`。
- [x] 改造 `src/pages/user/profile-edit-password/index.vue`。
- [x] 如有新增展示辅助逻辑，补对应测试。

验收契约：

- 资料设置页 MUST 保留昵称、性别、手机号、密码设置和退出登录能力。
- 三个编辑页 SHALL 保留现有提交、校验和返回逻辑。
- 页面不得再使用大圆角、重头部或高饱和蓝色旧样式。

## 3. 帮助 / 服务 / 反馈页统一新风格

- [x] 改造 `src/pages/user/help/index.vue`。
- [x] 改造 `src/pages/user/service/index.vue`。
- [x] 改造 `src/pages/user/feedback/index.vue`。

验收契约：

- 帮助中心 MUST 继续保留 FAQ 展开与收起逻辑。
- 服务中心 SHALL 继续保留联系客服弹层、帮助中心和意见反馈入口。
- 反馈页 MUST 保留反馈类型、内容、联系方式和提交逻辑。

## 4. 售后链路统一新风格

- [x] 改造 `src/pages/user/refund/index.vue`。
- [x] 改造 `src/pages/user/refund-detail/index.vue`。
- [x] 改造 `src/pages/user/refund-apply/index.vue`。
- [x] 调整售后状态展示 helper 到真实可用的样式规则。

验收契约：

- 售后列表页 MUST 保留状态切换、查看售后、查看订单和撤销售后能力。
- 售后详情页 SHALL 保留凭证图片预览、查看订单和撤销售后能力。
- 申请售后页 MUST 保留商品信息读取、凭证上传和提交售后申请逻辑。

## 5. 验证与 review

- [x] 运行本批次直接相关的测试。
- [x] 运行 `type-check`。
- [x] 对资料设置、帮助 / 服务 / 反馈和售后链路做本地 H5 或编译产物检查。
- [x] 创建 review 记录本次覆盖范围、验证证据与残余风险。

验收契约：

- 在未提供最新测试和 `type-check` 证据前，不得声称本次完成。
- review MUST 记录本次仅覆盖 Batch B 已批准页面，不自动扩展到更多剩余用户页。
