# Design: align-shopflow-h5-next-post-purchase-with-wx

## Overview

本次变更采用“以现有 `shopflow-h5-next` 页面为基座，补齐评价闭环并校正售后行为”的策略。

核心设计原则：

- 不改后端协议，优先复用现有 `shopflow-wx-api`
- 不推翻 `shopflow-h5-next` 已有售后实现，只在其基础上做对齐
- 评价链路按 `shopflow-wx` 的业务语义补齐，但保持 `shopflow-h5-next` 当前 `uni-app + Vue 3 + TypeScript` 工程组织
- 新增逻辑优先收敛到 `entities`、`pages` 和少量 `utils`，避免分散到多个页面临时判断中

## Scope Boundary

本次范围固定为：

- 订单评价发布页
- 商品评论列表页
- 订单列表与订单详情的评价入口
- 商品详情评论入口与评论摘要
- 售后申请校验规则对齐
- 售后列表状态分组对齐
- 订单详情退款/售后入口语义对齐

明确不纳入本次：

- 消息与聊天
- 动态
- 足迹
- 赏金
- 分享邀请
- 店主或商家侧售后审核

## Page Architecture

本次页面设计以 `shopflow-h5-next/src/pages` 现有结构为基础，建议新增或修改以下页面：

- 新增 `pages/order/comment-post/index`
- 新增 `pages/items/comment-list/index`
- 修改 `pages/order/detail/index`
- 修改 `pages/user/order-list/index`
- 修改 `pages/items/detail/index`
- 修改 `pages/user/refund-apply/index`
- 修改 `pages/user/refund/index`

对应入口策略：

- 订单详情页：
  - 若商品可评价，展示“去评价”
  - 若订单允许退款，保持“申请退款”语义
  - 若订单允许售后，展示“申请售后”语义
- 订单列表页：
  - 在可评价订单上补“去评价”入口
- 商品详情页：
  - 展示评论数量摘要
  - 提供“查看全部评论”入口
- 用户中心：
  - 继续复用已有“退款/售后”快捷入口，不新增额外重复入口

## Domain and Data Flow

### 订单评价发布

1. 用户从订单列表或订单详情进入评价页
2. 页面通过 `order/goods` 拉取待评价商品信息
3. 用户填写评分、评价内容并可选上传图片
4. 图片统一经 `storage/upload` 转成 URL
5. 页面调用 `order/comment` 提交评价
6. 提交成功后返回上一页或订单页，并给出明确反馈

### 商品评论浏览

1. 商品详情拉取评论统计信息
2. 商品详情展示评论数量、可选首条摘要或入口文案
3. 用户进入评论列表页
4. 页面通过 `goods/comment/count` 获取总数与有图数
5. 页面通过 `goods/comment/list` 获取评论列表
6. 列表支持全部 / 有图切换与分页加载

### 售后申请对齐

1. 用户从订单详情进入售后申请页
2. 页面调用 `order/detail` 获取订单与商品信息
3. 用户选择售后类型、填写原因、说明、上传凭证
4. 页面按 `shopflow-wx` 语义校验：
   - 售后类型必选
   - 原因必填
   - 说明必填
   - 非“未收货退款”场景要求凭证
5. 页面调用 `aftersale/submit`
6. 成功后进入售后列表并展示明确成功反馈

### 售后列表分组对齐

1. 售后列表以 `shopflow-wx` 的用户认知分组为基线
2. 页面切换 tab 时仍调用同一后端 `aftersale/list`
3. 前端按已确认的状态分组映射展示，避免保留当前与小程序不一致的 tab 文案

## API and Compatibility Strategy

继续复用现有：

- `src/shared/request/client.ts`
- `src/shared/config/env.ts`
- `src/shared/compat/*`

新增或补齐的 API 封装集中在：

- `src/entities/order/api.ts`
- `src/entities/goods/api.ts`
- `src/entities/user/api.ts`

兼容约束：

- 保持当前 `/wx/*` 接口路径与请求头契约不变
- 不恢复旧 `litemall` 历史 token 协议
- 不在页面层自行处理租户预热或头部拼接

## UI and Interaction Strategy

继续沿用 `shopflow-h5-next` 当前的卡片式页面基调，不引入与现有页面割裂的新视觉体系。

交互约束：

- 评价页优先清晰直给：
  - 商品信息
  - 星级评分
  - 文本评价
  - 图片上传
  - 提交按钮
- 评论列表页优先突出：
  - 总评论数
  - 有图筛选
  - 评论内容、评分、图片与规格
- 商品详情评论入口只做轻量摘要，不压制购买主路径
- 售后页沿用当前样式，但将文案、占位与校验反馈收敛为一致语义

## State and Validation Design

评价页状态：

- `rating`
- `content`
- `pictures`
- `submitting`
- `uploading`

售后申请页状态：

- `type`
- `reason`
- `comment`
- `pictures`
- `saving`
- `uploading`

校验要求：

- 所有失败都必须通过 `uni.showToast` 或明确 UI 提示反馈
- 不允许只打印控制台日志而页面无感知
- 提交按钮在提交中 / 上传中状态需要防重复触发

## Error Handling

必须显式处理：

- 未登录访问受保护页面
- `order/goods` 拉取失败
- 评论提交失败
- 评论图片上传失败
- 售后提交失败
- 售后凭证上传失败
- 评论列表为空
- 售后列表为空

要求：

- 空状态页面需要可读文案，而不是空白页
- 失败提示需要贴合当前动作语义
- 评价态或售后态不满足条件时，不得展示误导性操作按钮

## Verification Strategy

本次验证至少分三层：

- API / 纯逻辑测试
- 页面流程级测试
- H5 构建与联调验证

至少覆盖：

- 订单列表进入评价页
- 订单详情进入评价页
- 评价内容提交成功
- 商品详情进入评论列表
- 评论列表支持全部 / 有图切换
- 售后申请必填校验生效
- 非未收货退款要求凭证
- 售后列表状态切换与展示可用
- H5 构建通过
