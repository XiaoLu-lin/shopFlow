# ShopFlow H5 Next 地址 / 优惠券 / 收藏 Suji A 方案 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 把 `shopflow-h5-next` 的地址列表、地址编辑、优惠券和收藏页统一到已确认的 Suji A 方向，同时保持现有行为和灰蓝主题不回退。

**Architecture:** 继续复用现有 `ShopFlow` 灰蓝 token、现有用户侧 API 和现有路由/flow context 契约。实现以页面内结构和样式重做为主，必要时只抽取少量展示辅助逻辑，不新增依赖、不改后端协议。

**Tech Stack:** uni-app、Vue 3、TypeScript、SCSS、Vitest

---

## 文件结构

- 修改 `shopflow-h5-next/src/pages/user/address/index.vue`：重做地址列表布局与样式。
- 修改 `shopflow-h5-next/src/pages/user/address-edit/index.vue`：重做地址编辑页布局与样式。
- 修改 `shopflow-h5-next/src/pages/user/coupon/index.vue`：重做优惠券页布局与样式。
- 修改 `shopflow-h5-next/src/pages/user/collect/index.vue`：重做收藏页布局与样式。
- 视需要修改对应页面的展示辅助文件与测试。
- 新增 `openspec/changes/extend-h5-next-suji-style-to-address-coupon-collect-pages/review.md`。

## 任务 1：地址列表与地址编辑页

**文件：**

- 修改：`shopflow-h5-next/src/pages/user/address/index.vue`
- 修改：`shopflow-h5-next/src/pages/user/address-edit/index.vue`
- 可选修改：与地址展示相关的聚焦测试文件

- [ ] 扫描两个页面中的旧视觉值与较重结构：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master
rg -n "#1677ff|#172033|#f6f8fb|border-radius: 12|border-radius: 24|继续沿用旧站|轻量表单|常用地址" \
  shopflow-h5-next/src/pages/user/address/index.vue \
  shopflow-h5-next/src/pages/user/address-edit/index.vue
```

预期：命令返回旧视觉值或旧结构文案命中。

- [ ] 将地址列表页改造成“小 hero + 紧凑地址卡 + 底部新增按钮”结构。

- [ ] 将地址编辑页改造成“小 hero + 轻量分组表单 + 紧凑双按钮底栏”结构。

- [ ] 保留现有行为：
  - 地址选择写入 `AddressId`
  - 新增 / 编辑路由不变
  - 保存 / 删除逻辑不变

## 任务 2：优惠券与收藏页

**文件：**

- 修改：`shopflow-h5-next/src/pages/user/coupon/index.vue`
- 修改：`shopflow-h5-next/src/pages/user/collect/index.vue`
- 可选修改：与用户页展示相关的聚焦测试文件

- [ ] 扫描两个页面中的旧视觉值与较重结构：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master
rg -n "#1677ff|#172033|#f6f8fb|保持旧站|已接回旧站|border-radius: 12|border-radius: 24" \
  shopflow-h5-next/src/pages/user/coupon/index.vue \
  shopflow-h5-next/src/pages/user/collect/index.vue
```

预期：命令返回旧视觉值或旧结构文案命中。

- [ ] 将优惠券页改造成“小 hero + tabs + 紧凑灰蓝票券列表”结构。

- [ ] 将收藏页改造成“小 hero + 轻收藏清单”结构。

- [ ] 保留现有行为：
  - 优惠券三态切换不变
  - `/coupon/user` 数据流不变
  - 收藏商品详情跳转不变
  - 取消收藏和刷新逻辑不变

## 任务 3：验证

- [ ] 运行与本批页面直接相关的测试；若没有新增聚焦测试，记录原因。

- [ ] 运行类型检查：

```bash
cd /Users/lhl/Desktop/code/litemall-plus-master/shopflow-h5-next
npm run type-check
```

预期：退出码为 0。

- [ ] 在本地 H5 中检查：
  - `http://127.0.0.1:6257/#/pages/user/address/index`
  - `http://127.0.0.1:6257/#/pages/user/address-edit/index?addressId=-1`
  - `http://127.0.0.1:6257/#/pages/user/coupon/index?active=0`
  - `http://127.0.0.1:6257/#/pages/user/collect/index`

预期：四个页面都已切到已确认的 A 方向，圆角统一为 `8px` 基线，hero 更轻更小，行为不回退。

## 任务 4：Review

**文件：**

- 新增：`openspec/changes/extend-h5-next-suji-style-to-address-coupon-collect-pages/review.md`

- [ ] 写入 review，至少包含：

```markdown
# Review: extend-h5-next-suji-style-to-address-coupon-collect-pages

## 检查范围

- 地址列表页
- 地址编辑页
- 优惠券页
- 收藏页

## 发现

- 记录行为回归、视觉缺口和测试缺口。

## 验证证据

- `npm run type-check`：结果
- 页面相关测试：结果
- H5 视觉检查：结果

## 残余风险

- 帮助、服务、反馈、资料设置、售后等剩余用户页仍待后续批次统一。
```

预期：review 明确记录本批范围、验证结果和剩余页面。
