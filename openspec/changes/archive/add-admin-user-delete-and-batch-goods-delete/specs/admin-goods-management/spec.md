## ADDED Requirements

### Requirement: Admin can batch delete goods from the goods list

后台管理员必须能够在商品列表中批量删除多个商品。

#### Scenario: batch delete selected goods

- **Given** 管理员在商品列表中选择了多个商品
- **When** 管理员点击批量删除并确认
- **Then** 系统应调用商品批量删除接口删除所选商品
- **And** 删除成功后页面应提示成功并刷新列表

#### Scenario: no goods selected

- **Given** 管理员进入商品列表但未选择任何商品
- **When** 管理员点击批量删除
- **Then** 系统应提示先选择要删除的商品
- **And** 不应发送批量删除请求

#### Scenario: cancel batch delete

- **Given** 管理员在商品列表中选择了多个商品
- **When** 管理员点击批量删除后又取消确认
- **Then** 系统不应发送批量删除请求
- **And** 当前选择状态和商品列表保持不变

#### Scenario: batch delete reuses existing single-delete behavior

- **Given** 管理员批量删除的商品中包含已关联团购或赏金任务的商品
- **When** 系统执行批量删除
- **Then** 每个商品都应复用现有单个商品删除逻辑
- **And** 相关联的清理行为应与单个删除保持一致
