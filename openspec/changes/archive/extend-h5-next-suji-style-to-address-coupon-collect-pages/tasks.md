# 任务：extend-h5-next-suji-style-to-address-coupon-collect-pages

## 1. A 方案建档与约束固化

- [x] 在本地预览中确认本批用户页采用 `A. 轻生活感`。
- [x] 创建 proposal、design、tasks 和对应 spec 更新。
- [x] 明确主题色不变、圆角统一收敛到 `8px`、hero 缩小这一批次约束。

验收契约：

- proposal MUST 明确本次不修改后端接口和主题色 token。
- design SHALL 明确本次页面统一采用 `8px` 圆角、小 hero、轻字号和轻间距。

## 2. 地址列表与地址编辑页统一新风格

- [x] 改造 `src/pages/user/address/index.vue` 为紧凑地址簿式页面。
- [x] 改造 `src/pages/user/address-edit/index.vue` 为轻量表单式页面。
- [x] 如有新增展示辅助逻辑，补对应测试。

验收契约：

- 地址列表页 MUST 继续保留现有地址选择、编辑和新增能力。
- 地址编辑页 SHALL 继续保留现有校验、保存和删除逻辑。
- 页面不得再使用大圆角、厚重 hero 或过宽留白。

## 3. 优惠券与收藏页统一新风格

- [x] 改造 `src/pages/user/coupon/index.vue` 为紧凑灰蓝票夹式页面。
- [x] 改造 `src/pages/user/collect/index.vue` 为轻收藏清单式页面。
- [x] 如有新增展示辅助逻辑，补对应测试。

验收契约：

- 优惠券页 MUST 继续保留三种状态切换与现有列表数据流。
- 收藏页 SHALL 继续保留商品详情跳转与取消收藏能力。
- 页面风格必须与已确认个人中心 A 方案连续，且继续使用当前灰蓝主题。

## 4. 验证与 review

- [x] 运行与本批改动直接相关的测试。
- [x] 运行 `type-check`。
- [x] 对地址列表、地址编辑、优惠券、收藏页做本地 H5 视觉检查。
- [x] 创建 review 记录本次覆盖范围与残余风险。

验收契约：

- 在未提供最新测试和 `type-check` 证据前，不得声称本次完成。
- review MUST 记录本次只处理地址、地址编辑、优惠券和收藏，其他用户页留待后续批次处理。
