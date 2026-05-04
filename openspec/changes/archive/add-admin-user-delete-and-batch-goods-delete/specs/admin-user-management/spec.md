## ADDED Requirements

### Requirement: Admin can delete a member from the member management list

后台管理员必须能够在会员管理列表中注销单个会员，且不能通过该操作物理删除会员数据。

#### Scenario: deactivate a member from the list

- **Given** 管理员进入会员管理页面并看到某个会员记录
- **When** 管理员点击该记录的注销按钮并确认操作
- **Then** 系统应调用会员注销接口将该会员置为注销状态
- **And** 注销成功后页面应提示成功并刷新列表

#### Scenario: cancel member deactivation

- **Given** 管理员点击会员注销按钮
- **When** 管理员在确认框中取消
- **Then** 系统不应发送删除请求
- **And** 当前会员列表保持不变

#### Scenario: member deactivation request fails

- **Given** 管理员确认注销某个会员
- **When** 后端返回删除失败
- **Then** 页面不应误提示删除成功
- **And** 当前会员列表应保持可继续操作状态

#### Scenario: keep dependent history after deactivation

- **Given** 某会员已经关联历史订单、通知或其他业务记录
- **When** 管理员执行会员注销
- **Then** 系统不应物理删除该会员数据
- **And** 既有依赖该会员的历史业务记录仍应可继续查询和处理
