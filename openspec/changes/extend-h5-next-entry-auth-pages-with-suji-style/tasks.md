# Tasks: Extend H5 Next Suji Style To Entry And Auth Pages

- [x] 1. 补齐批次 D 的设计与计划工件
  - 验收契约：本批 MUST 明确包含 `catalog`、`detail`、`login`、`forget`、`register-get-code`
  - 验收契约：本批 SHALL 明确说明注册与找回密码采用单页流程

- [x] 2. 重构登录链路表单辅助
  - 验收契约：登录、注册、找回密码不得各自复制倒计时与基础校验逻辑
  - 验收契约：登录成功后的 redirect 语义 MUST 保持不变

- [x] 3. 实现登录链路三页新样式与真实流程
  - 验收契约：`forget` 和 `register-get-code` 不得再显示迁移占位文案
  - 验收契约：注册和找回密码 SHALL 能调用现有 auth API 完成提交流程

- [x] 4. 实现分类入口与商品详情页新样式
  - 验收契约：`catalog` 和 `detail` MUST 延续灰蓝、小 hero、8px 圆角的既有方向
  - 验收契约：商品详情的规格选择、加购、购买入口不得回退

- [x] 5. review 与验证
  - 验收契约：必须运行相关 vitest 与 type-check
  - 验收契约：必须补本地 H5 页面验证说明
