# Proposal: align-shopflow-h5-next-post-purchase-with-wx

## Why

`shopflow-h5-next` 已经完成从旧 H5 向 `uni-app` 多端主工程的重建，并且接入了首页、商品、购物车、下单、支付、订单、个人中心等主交易链路，也已经具备一版售后页面与接口封装。

但与 `shopflow-wx` 对照后，当前 `shopflow-h5-next` 的交易后链路仍存在两类明显差异：

1. 评价闭环仍未补齐
   - 缺少订单评价发布页
   - 缺少商品评论列表页
   - 订单列表、订单详情中的“去评价”入口尚未接通
   - 商品详情中没有评论摘要与评论列表入口

2. 售后能力虽已存在，但规则与语义尚未完全对齐
   - 售后申请页当前未完整复刻 `shopflow-wx` 的表单校验约束
   - 售后 tab 状态分组与 `shopflow-wx` 口径不一致
   - 订单详情中的退款/售后按钮语义尚未完全与小程序保持一致

这会带来三个直接问题：

1. 新 H5 主工程仍未形成完整交易后闭环
   - 用户可以完成下单与支付，但评价路径仍断裂
   - 售后虽能提交，但前后行为与小程序不完全一致，容易造成用户认知偏差

2. 多端主工程目标没有兑现到关键业务面
   - `shopflow-h5-next` 作为后续主前台，如果交易后链路与 `shopflow-wx` 持续漂移，会削弱“统一主工程”的价值

3. 已有售后实现容易造成“功能已齐”的误判
   - 当前并不是完全缺失，而是“已做一部分但还没对齐”
   - 如果没有单独变更收口，很容易在后续迭代中继续遗留半对齐状态

用户已确认，本次 change 的目标是：

- 以 `shopflow-wx` 作为交易后链路的行为基线
- 优先补齐 `shopflow-h5-next` 的评价闭环
- 同步把售后申请校验、状态分组和相关入口语义拉齐
- 默认复用现有 `shopflow-wx-api` 能力，不主动扩展后端协议

## What

本次 change 将为 `shopflow-h5-next` 补齐并对齐以下能力：

1. 新增 `shopflow-h5-next` 订单评价发布页
2. 新增 `shopflow-h5-next` 商品评论列表页
3. 接通订单列表与订单详情中的“去评价”入口与“已评价”状态表现
4. 在商品详情中补齐评论摘要与“查看全部评论”入口
5. 接通 `order/goods`、`order/comment`、`goods/comment/count`、`goods/comment/list` 与图片上传流程
6. 对齐售后申请页的校验规则、表单行为与结果反馈
7. 对齐售后列表 tab 语义与订单详情中退款/售后入口语义

本次默认复用以下后端能力：

- `/wx/order/goods`
- `/wx/order/comment`
- `/wx/goods/comment/count`
- `/wx/goods/comment/list`
- `/wx/storage/upload`
- `/wx/aftersale/list`
- `/wx/aftersale/detail`
- `/wx/aftersale/submit`

本次变更不包含：

- 聊天、消息、动态、赏金、提现、分享等非交易后能力
- 商家侧订单审核或售后审核流程
- `shopflow-wx-api` 新接口设计或协议调整
- 数据库结构调整
- 把 `shopflow-h5-next` 全量页面完全逐一复刻为 `shopflow-wx` 外观

## Impact

受影响模块：

- `shopflow-h5-next`
- `openspec/specs/litemall-vue-compatibility`

受影响能力：

- `shopflow-h5-next` 订单详情
- `shopflow-h5-next` 订单列表
- `shopflow-h5-next` 商品详情
- `shopflow-h5-next` 个人中心
- `shopflow-h5-next` 售后申请 / 列表 / 详情
- `shopflow-h5-next` 订单评价与商品评论浏览

关键假设：

- `shopflow-wx-api` 中评论、售后与上传接口仍维持当前可复用语义
- `shopflow-h5-next` 现有请求层、租户预热和登录态兼容层可直接承接新增交易后页面
- 本次实现可以建立在现有 `shopflow-h5-next` 售后基础上做补齐，而不是重写整段交易后链路

主要风险：

- `shopflow-h5-next` 当前订单详情和订单列表的数据模型可能缺少评价态需要的字段消费，需要实现期显式消化兼容差异
- 商品详情页目前未布局评论模块，补入口时需要控制好信息密度，避免干扰既有购买主路径
- 如果直接照搬旧 `shopflow-h5` 页面结构，会和当前 `uni-app` 页面组织方式冲突，因此需要按 `shopflow-h5-next` 现有架构重组

预期收益：

- 让 `shopflow-h5-next` 具备完整可用的交易后闭环
- 把新 H5 主工程与 `shopflow-wx` 的核心用户侧能力差距收敛到更小范围
- 为后续继续推进多端统一页面语义建立清晰基线
