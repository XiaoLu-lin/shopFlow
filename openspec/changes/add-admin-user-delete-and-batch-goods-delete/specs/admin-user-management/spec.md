## ADDED Requirements

### Requirement: Admin can delete a member from the member management list

后台管理员必须能够在会员管理列表中删除单个会员。

#### Scenario: delete a member from the list

- **Given** 管理员进入会员管理页面并看到某个会员记录
- **When** 管理员点击该记录的删除按钮并确认删除
- **Then** 系统应调用会员删除接口删除该会员
- **And** 删除成功后页面应提示成功并刷新列表

#### Scenario: cancel member deletion

- **Given** 管理员点击会员删除按钮
- **When** 管理员在确认框中取消
- **Then** 系统不应发送删除请求
- **And** 当前会员列表保持不变

#### Scenario: member delete request fails

- **Given** 管理员确认删除某个会员
- **When** 后端返回删除失败
- **Then** 页面不应误提示删除成功
- **And** 当前会员列表应保持可继续操作状态
