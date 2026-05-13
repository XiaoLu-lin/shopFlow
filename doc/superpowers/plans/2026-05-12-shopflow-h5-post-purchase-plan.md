# shopflow-h5 交易后链路补齐实施计划

## 1. 范围

本计划对应 change：

- `add-shopflow-h5-post-purchase-capabilities`

实施范围固定为：

- 售后申请
- 售后列表
- 售后详情
- 订单评价
- 商品评论列表

## 2. 阶段划分

### Phase A：页面与接口对齐

- 核对 H5 当前订单、商品详情、个人中心入口
- 明确每个新页面对应的接口
- 明确旧 `refund-list` 的处理策略

### Phase B：售后闭环实现

- 新增售后申请页
- 新增售后列表页
- 新增售后详情页
- 接通订单详情和个人中心入口
- 接通上传与提交逻辑

### Phase C：评论闭环实现

- 新增评论发布页
- 接通订单列表和订单详情中的“去评价”
- 新增商品评论列表页
- 接通商品详情评论入口

### Phase D：验证与收尾

- 手工验证售后与评论链路
- 检查未登录、失败、空列表、上传失败等边界
- 补 review
- 更新 tasks 状态

## 3. 可并行项

可并行：

- 售后页面开发
- 评论页面开发

需主线串行协调：

- 路由注册
- 上传能力接法
- 入口位置与旧页面去留策略
- 最终联调与 review

## 4. 文件范围

高概率涉及：

- `shopflow-h5/src/router/user.js`
- `shopflow-h5/src/router/order.js`
- `shopflow-h5/src/router/items.js`
- `shopflow-h5/src/views/order/*`
- `shopflow-h5/src/views/user/*`
- `shopflow-h5/src/views/items/*`
- `shopflow-h5/src/api/api.js`

## 5. 验证命令与检查点

建议检查点：

1. 新路由可访问
2. 售后申请可提交
3. 售后列表和详情可查看
4. 订单评价可提交
5. 商品评论列表可查看与切换

建议验证命令：

- `node scripts/shopflow-compat.test.js`
- `VUE_APP_SHOPFLOW_APPID=1649067 npm run build`

如需联调：

- 启动后端 `shopflow-all`
- 启动 H5 前台并手工走订单、售后、评论链路

## 6. 风险控制

- 先避免改造历史空壳页面，优先新增职责清晰的新页面
- 上传能力优先收敛成统一接法，不要在多个页面各写一套
- 未联调确认前，不宣称后端字段 100% 与 H5 页面直接匹配

