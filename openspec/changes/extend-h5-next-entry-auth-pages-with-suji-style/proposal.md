# Proposal: Extend H5 Next Suji Style To Entry And Auth Pages

## Why

`shopflow-h5-next` 已经完成商品流、订单页和大部分用户子页的风格统一，但分类入口、商品详情和登录链路仍停留在不同阶段的迁移实现中，导致整套 H5 Next 在关键入口处仍存在明显割裂。

尤其是：

- `pages/items/catalog` 与当前商品流页面层级不一致
- `pages/items/detail` 仍保留较强的早期迁移痕迹
- `pages/login/forget` 与 `pages/login/register-get-code` 仍是占位页

这些页面都处于高频入口位置，继续保留会影响用户对 H5 Next 完整度的判断。

## What Changes

- 统一 `catalog` 与 `detail` 到已确认的 ShopFlow / Suji-like A 方向
- 把 `login`、`forget`、`register-get-code` 收成正式单页流程
- 保持接口、路由、redirect 和购买链路不变
- 为 auth 表单新增可复用展示与状态辅助

## Impact

- 影响模块：`shopflow-h5-next`
- 影响页面：5 个
- 不影响后端接口契约
- 不引入新路由和新依赖
