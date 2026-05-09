# Admin User Batch Deactivate Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为后台会员管理增加当前页勾选批量注销能力，支持部分成功汇总提示，并禁止选择已注销会员。

**Architecture:** 后端在 `AdminUserController` 新增批量注销接口，入参复用 `IdsBody`，按现有单条注销语义逐个更新会员状态并返回成功数、失败数。前端在会员列表中复用现有批量删除交互模式，增加多选列、批量注销按钮、已注销禁选、确认框和汇总提示。

**Tech Stack:** Java 8, Spring Boot 2.6.x, Sa-Token, Vue 2, Element UI, Axios request 封装

---

### Task 1: 后端批量注销结果结构与接口

**Files:**
- Create: `/Users/lhl/Desktop/code/litemall-plus-master/shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/model/user/result/UserBatchDeleteResult.java`
- Modify: `/Users/lhl/Desktop/code/litemall-plus-master/shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminUserController.java`
- Test: 手工接口验证，当前仓库未配置可直接运行的 `shopflow-admin-api` 单元测试基线

- [ ] **Step 1: 先为批量结果定义最小返回结构**

新增一个仅包含 `successCount` 和 `failCount` 的结果对象，避免把汇总逻辑硬编码进字符串。

- [ ] **Step 2: 在 `AdminUserController` 增加批量注销接口**

接口建议：

```java
@SaCheckPermission("admin:user:delete")
@RequiresPermissionsDesc(menu = {"用户管理", "会员管理"}, button = "删除")
@PostMapping("/batch-delete")
public Object batchDelete(@Valid @RequestBody IdsBody body)
```

- [ ] **Step 3: 以现有单条注销语义实现逐条处理**

循环 `ids`，对每个会员执行：

- 查询用户是否存在
- 若不存在则失败数加一
- 若状态已是 `UserStatus.STATUS_OUT` 则失败数加一
- 否则设置状态为注销并调用 `userService.updateVersionSelective(user)`
- 更新成功则成功数加一，失败则失败数加一

- [ ] **Step 4: 返回汇总结果**

使用：

```java
return ResponseUtil.ok(result);
```

其中 `result` 带 `successCount`、`failCount`。

- [ ] **Step 5: 运行最小后端编译验证**

Run:

```bash
mvn -pl shopflow-admin-api -am -Dmaven.test.skip=true compile
```

Expected: 编译通过，无当前改动导致的 Java 编译错误

### Task 2: 管理端用户 API 封装

**Files:**
- Modify: `/Users/lhl/Desktop/code/litemall-plus-master/shopflow-admin/src/api/user.js`
- Test: 通过页面联调验证请求路径和请求体

- [ ] **Step 1: 增加批量注销请求封装**

新增：

```js
export function batchDeleteUser(data) {
  return request({
    url: '/user/batch-delete',
    method: 'post',
    data
  })
}
```

- [ ] **Step 2: 保持现有单条注销封装不变**

不替换 `deleteUser`，避免影响现有单条注销调用点。

### Task 3: 会员列表多选、禁选与批量交互

**Files:**
- Modify: `/Users/lhl/Desktop/code/litemall-plus-master/shopflow-admin/src/views/user/user.vue`
- Test: 页面联调验证

- [ ] **Step 1: 增加批量操作状态字段**

参考商品、通知页面，新增：

- `multiple`
- `multipleSelection`

- [ ] **Step 2: 在会员表格增加选择列和禁选逻辑**

增加 `type="selection"` 列，并通过 `:selectable="isSelectable"` 控制已注销会员不可选。

建议方法：

```js
isSelectable(row) {
  return row.status !== 2
}
```

- [ ] **Step 3: 在筛选区增加“批量注销”按钮**

按钮要求：

- 使用 `type="danger"` / `plain` 风格与现有批量删除按钮保持一致
- 复用当前单条注销权限口径
- 未选中时禁用

- [ ] **Step 4: 增加选择变化处理逻辑**

新增：

```js
handleSelectionChange(val) {
  this.multipleSelection = val
  this.multiple = !val.length
}
```

- [ ] **Step 5: 增加批量注销交互逻辑**

逻辑要求：

- 未选择任何记录时提示“请至少选择一条未注销会员记录”
- 选中后弹确认框
- 组装 `ids`
- 调 `batchDeleteUser({ ids })`
- 成功后按返回结果提示
- 清空选中状态并刷新列表

提示文案建议：

```js
message: `批量注销完成，成功 ${successCount} 条，失败 ${failCount} 条`
```

- [ ] **Step 6: 保持现有单条注销能力不变**

`handleDelete(row)` 继续可用，只做必要的非冲突调整。

### Task 4: 联调与验证

**Files:**
- Verify: `/Users/lhl/Desktop/code/litemall-plus-master/shopflow-admin/src/views/user/user.vue`
- Verify: `/Users/lhl/Desktop/code/litemall-plus-master/shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminUserController.java`

- [ ] **Step 1: 运行前端 lint**

Run:

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-admin && npm run lint -- src/views/user/user.vue src/api/user.js
```

Expected: lint 通过

- [ ] **Step 2: 再跑后端编译**

Run:

```bash
mvn -pl shopflow-admin-api -am -Dmaven.test.skip=true compile
```

Expected: 编译通过

- [ ] **Step 3: 手工核对需求清单**

检查：

- 已注销会员勾选框禁用
- 当前页多选可用
- 未选中时不给发请求
- 返回后展示成功/失败汇总
- 单条注销仍可用
