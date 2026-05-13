# Design: add-shopflow-h5-post-purchase-capabilities

## Overview

本次变更采用“新增明确页面 + 接通现有入口 + 尽量复用现有请求层”的策略。

核心设计原则：

- 不改后端协议，优先复用现有 `shopflow-wx-api`
- 不把新能力继续塞进历史半成品页面中
- 以“交易后闭环完整”为目标，而不是只把按钮点亮
- 新逻辑优先集中在 H5 页面、请求封装和少量通用上传能力中

## Scope Boundary

本次范围固定为 `P1`：

- 售后申请
- 售后列表
- 售后详情
- 订单评价
- 商品评论列表

明确不纳入本次：

- 足迹
- 消息与聊天
- 动态
- 赏金
- 分享邀请
- 提现/余额
- 商家侧管理能力

这样可以保证本次 change 只聚焦“交易后链路”，避免把 H5 补齐变成一个无边界的大项目。

## Page Architecture

建议新增或补齐以下 H5 页面：

- `views/user/aftersale-list`
- `views/user/aftersale-detail`
- `views/order/aftersale-apply`
- `views/order/comment-post`
- `views/items/comment-list`

对应入口建议：

- 订单详情页：
  - 增加“申请售后”入口
  - 接通“去评价”入口
- 订单列表页：
  - 接通“去评价”入口
- 用户中心：
  - 新增“售后列表”入口
- 商品详情页：
  - 新增“查看全部评论”入口

## Data Flow

### 售后申请

1. 从订单详情进入售后申请页
2. 调用 `order/detail` 获取订单与商品信息
3. 用户选择售后类型、填写原因、说明、上传图片
4. 调用 `storage/upload` 上传凭证
5. 调用 `aftersale/submit` 提交申请
6. 成功后进入售后列表或售后详情

### 售后列表与详情

1. 个人中心进入售后列表
2. 调用 `aftersale/list` 分页拉取
3. 点击某一项进入详情
4. 调用 `aftersale/detail` 展示完整信息

### 订单评价与商品评论

1. 从订单列表或订单详情进入评价页
2. 调用 `order/goods` 获取待评价商品信息
3. 用户填写评分、评论内容、图片
4. 上传图片后调用 `order/comment`
5. 商品详情评论入口调用 `goods/comment/count` 与 `goods/comment/list`

## Request and Upload Strategy

请求层继续复用现有：

- `src/utils/request.js`
- `src/utils/shopflow-compat.js`

不在各页面自行处理：

- 租户头
- 用户头
- 登录态兼容

上传策略：

- 统一复用 `/wx/storage/upload`
- 页面层只负责选择文件、展示上传中状态和收集返回 URL
- 不把上传协议细节分散到多个页面

## UI and Interaction Strategy

H5 继续保持现有 `Vue 2 + Vant` 体系，不做额外视觉重构。

交互原则：

- 尽量沿用现有 H5 风格
- 表单类页面优先简单、清晰、低认知成本
- 重要失败信息必须明确展示，不能静默吞错

关键交互：

- 售后申请页：
  - 类型选择
  - 原因输入
  - 说明输入
  - 凭证上传
- 评论页：
  - 星级评分
  - 评论内容
  - 图片上传
- 评论列表页：
  - 全部/有图切换
  - 分页加载

## Legacy Page Handling

历史 `refund-list` 页面不作为本次核心复用基座。

原因：

- 当前页面没有真正接好接口
- 语义偏“退款记录”，与完整售后列表边界不够清晰
- 如果继续在旧壳子上硬补，会扩大历史耦合

建议做法：

- 新增明确的售后列表页
- 旧 `refund-list` 视实现阶段决定是下线、跳转还是收敛为兼容入口

## Error Handling

必须显式处理：

- 未登录态进入受保护页面
- 上传失败
- 提交失败
- 列表为空
- 评论与售后状态不满足当前操作条件

要求：

- 错误提示必须通过统一 UI 反馈展示
- 不得只在控制台打印错误

## Verification Strategy

本次验证分三层：

- 页面级手工链路验证
- 关键请求联调验证
- 冒烟构建验证

至少覆盖：

- 订单详情进入售后申请
- 提交售后成功
- 售后列表和详情可查看
- 订单评价可提交
- 商品评论列表可分页查看
- 上传失败和未登录场景有可见反馈

