# ShopFlow H5 Next 资料 / 服务 / 售后 Batch B 实施计划

关联设计稿：

- `doc/superpowers/specs/2026-05-17-shopflow-h5-next-profile-support-refund-suji-design.md`

关联 change：

- `openspec/changes/extend-h5-next-suji-style-to-profile-support-refund-pages/`

## 目标

把资料设置链路、帮助 / 服务 / 反馈页和售后链路统一到已批准的 Batch B 灰蓝轻量 Suji 方向，同时不改变现有接口和关键交互。

## Phase 1：共享规则与工件补齐

涉及文件：

- `openspec/changes/extend-h5-next-suji-style-to-profile-support-refund-pages/*`
- `doc/superpowers/specs/2026-05-17-shopflow-h5-next-profile-support-refund-suji-design.md`
- `doc/superpowers/plans/2026-05-17-shopflow-h5-next-profile-support-refund-suji-plan.md`
- `src/pages/user/page-display-utils.ts`
- `src/pages/user/page-display-utils.test.ts`
- `src/pages/user/aftersale-utils.ts`

执行要点：

- 新建 Batch B change 工件和设计稿、计划。
- 为资料页、帮助页、服务页、反馈页、售后三页补共享 hero / 元信息配置。
- 调整售后状态 class 返回值，确保真实样式可接住。

检查点：

- `page-display-utils.test.ts` 新增 case 覆盖 Batch B 配置

## Phase 2：资料设置链路

涉及文件：

- `src/pages/user/profile/index.vue`
- `src/pages/user/profile-edit-nickname/index.vue`
- `src/pages/user/profile-edit-mobile/index.vue`
- `src/pages/user/profile-edit-password/index.vue`

执行要点：

- 资料设置页改成小 hero + 轻服务行结构。
- 三个编辑页统一到与地址编辑页同一套轻表单结构。
- 保留性别切换、退出登录、验证码倒计时和提交逻辑。

验证重点：

- 资料项跳转正常
- 性别切换仍可用
- 退出登录仍可触发

## Phase 3：帮助 / 服务 / 反馈

涉及文件：

- `src/pages/user/help/index.vue`
- `src/pages/user/service/index.vue`
- `src/pages/user/feedback/index.vue`

执行要点：

- 帮助页改成小 hero + FAQ 轻卡
- 服务中心改成服务行清单 + 轻量联系客服弹层
- 反馈页改成 chip + 表单卡 + 固定底部按钮

验证重点：

- FAQ 展开和收起
- 服务中心跳转与弹层
- 反馈页校验与提交按钮状态

## Phase 4：售后链路

涉及文件：

- `src/pages/user/refund/index.vue`
- `src/pages/user/refund-detail/index.vue`
- `src/pages/user/refund-apply/index.vue`

执行要点：

- 售后列表收紧为轻量记录卡
- 售后详情分组展示状态、商品、售后信息和图片
- 申请售后改成分段表单，底部固定提交按钮

验证重点：

- 状态切换
- 查看订单 / 查看售后
- 撤销售后
- 凭证上传区和图片预览入口

## Phase 5：统一验证与 review

执行命令：

1. `cd shopflow-h5-next && pnpm vitest run src/pages/user/page-display-utils.test.ts`
2. `cd shopflow-h5-next && pnpm type-check`

补充检查：

- 用 `curl` 查看 `http://127.0.0.1:6257/src/pages/user/.../index.vue` 编译产物，确认改动已进入 dev server
- 如需要，再看对应 H5 路由页面视觉结果

收尾要求：

- 同步更新 `tasks.md` 完成状态
- 新建 `review.md`，记录覆盖范围、验证证据与残余风险
