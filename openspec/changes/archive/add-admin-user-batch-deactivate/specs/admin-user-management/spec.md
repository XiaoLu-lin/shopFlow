## ADDED Requirements

### Requirement: Admin can batch deactivate members from the member management list

后台管理员必须能够在会员管理列表中批量注销当前页已勾选的未注销会员，且不能通过该操作物理删除会员数据。

#### Scenario: batch deactivate selected active members

- **Given** 管理员进入会员管理页面并勾选了当前页多个未注销会员
- **When** 管理员点击批量注销并确认操作
- **Then** 系统应调用会员批量注销接口处理所选会员
- **And** 系统应将成功处理的会员状态更新为注销
- **And** 页面应展示成功数量与失败数量的汇总结果
- **And** 操作完成后页面应刷新当前列表并清空当前选中状态

#### Scenario: block selection for already deactivated members

- **Given** 会员管理列表中存在状态为注销的会员
- **When** 管理员查看列表选择列
- **Then** 已注销会员的选择框应为禁用状态
- **And** 管理员不能将其选入批量注销操作

#### Scenario: no selection for batch deactivation

- **Given** 管理员进入会员管理页面
- **When** 管理员未选择任何会员就点击批量注销
- **Then** 系统不应发送批量注销请求
- **And** 页面应提示至少选择一条未注销会员记录

#### Scenario: partially successful batch deactivation

- **Given** 管理员勾选了多条未注销会员
- **When** 批量注销过程中部分会员处理成功、部分会员处理失败
- **Then** 系统不应因为单条失败中断整个批次
- **And** 接口应返回成功数量和失败数量
- **And** 页面应按汇总结果提示管理员

#### Scenario: keep dependent history after batch deactivation

- **Given** 被批量注销的会员已关联历史订单、通知、分享或其他业务记录
- **When** 管理员执行批量注销
- **Then** 系统不应物理删除这些会员数据
- **And** 既有依赖该会员的历史业务记录仍应可继续查询和处理
