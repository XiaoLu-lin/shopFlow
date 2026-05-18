# ShopFlow H5 页面矩阵

| 路由 | 页面 | 领域 | 登录要求 | 关键接口 | 关键状态 | 异常态/边界 | 旧键/兼容点 | 验收要点 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `/` | 首页 | 浏览 | 否 | `/home/index` | banner、频道、品牌、专题、团购、优惠券 | 首页首包需租户预热 | `ShopFlowTenantToken`、`appid` | 能匿名进入并写入租户 token |
| `/items` | 分类页 | 浏览 | 否 | `/catalog/index`、`/catalog/current` | 当前分类、分类商品 | 切分类跳转商品列表 | tenant header | 分类可浏览 |
| `/items/search` | 搜索页 | 浏览 | 否 | `/search/helper`、`/search/result` | 搜索词、历史关键字 | 本地关键字历史 | `keyword` | 搜索与历史记录可用 |
| `/items/detail/:itemId` | 商品详情 | 浏览/交易 | 否 | `/goods/detail`、`/cart/count`、`/collect/addordelete`、`/cart/add`、`/cart/fast/add` | SKU、收藏、购物车角标 | 多规格、未登录收藏/加购 | `Authorization`、`CartId` | 能选规格、加购、立即购买 |
| `/items/category` | 分类商品列表 | 浏览 | 否 | `/goods/list`、`/goods/category` | 分类筛选、排序 | query 参数驱动 | tenant header | 可按分类查看商品 |
| `/items/hot` | 人气推荐 | 浏览 | 否 | `/goods/list` | 列表、分页 | 空列表 | tenant header | 可浏览 |
| `/items/new` | 新品首发 | 浏览 | 否 | `/goods/list` | 列表、分页 | 空列表 | tenant header | 可浏览 |
| `/items/groupon` | 团购专区 | 浏览 | 否 | `/groupon/list` | 团购列表 | 空列表 | tenant header | 可浏览 |
| `/items/brand/:brandId` | 品牌详情 | 浏览 | 否 | `/brand/detail` | 品牌信息、商品 | 不存在品牌 | tenant header | 可浏览 |
| `/items/brand-list` | 品牌列表 | 浏览 | 否 | `/brand/list` | 品牌列表 | 空列表 | tenant header | 可浏览 |
| `/items/topic/:topicId` | 专题详情 | 浏览 | 否 | `/topic/detail`、`/topic/related` | 专题内容、关联专题 | 空详情 | tenant header | 可浏览 |
| `/items/topic-list` | 专题列表 | 浏览 | 否 | `/topic/list` | 列表 | 空列表 | tenant header | 可浏览 |
| `/login` | 登录 | 鉴权 | 否 | `/auth/login_legacy` | account、password | 邮箱分支不支持 | `Authorization`、`avatar`、`nickName` | 登录成功后跳转并写入旧键 |
| `/login/registerGetCode` | 注册手机号录入 | 鉴权 | 否 | 无 | mobile | 手机号校验 | 无 | 能进入下一步 |
| `/login/registerSubmit/:phone` | 注册提交 | 鉴权 | 否 | `/auth/captcha/mobile`、`/auth/register` | code、password | 密码二次确认 | tenant header | 可发送验证码并注册 |
| `/login/registerStatus/:status` | 注册结果 | 鉴权 | 否 | 无 | status | 失败态 | 无 | 成功/失败文案可见 |
| `/login/forget` | 找回密码验证码 | 鉴权 | 否 | `/auth/captcha/mobile` | mobile、code | 手机号校验 | tenant header | 可发验证码并进入重置 |
| `/login/forget/reset` | 找回密码重置 | 鉴权 | 否 | `/auth/reset` | mobile、code、password | 缺参数、二次密码不一致 | tenant header | 可完成重置 |
| `/login/forget/reset/:status` | 找回密码结果 | 鉴权 | 否 | 无 | status | 失败态 | 无 | 成功/失败文案可见 |
| `/order` | 购物车 | 交易 | 是 | `/cart/index`、`/cart/update`、`/cart/checked`、`/cart/delete` | 勾选、编辑态、总价 | 空购物车 | `Authorization` | 可勾选、改数量、删除、去结算 |
| `/order/checkout` | 结算 | 交易 | 是 | `/cart/checkout`、`/coupon/selectlist`、`/order/submit` | 地址、优惠券、备注 | 缺地址、下单失败 | `AddressId`、`CartId`、`CouponId`、`UserCouponId` | 可提交订单 |
| `/order/order-detail` | 订单详情 | 交易 | 是 | `/order/detail` | 订单状态、商品、地址 | 不存在订单 | `Authorization` | 可查看详情 |
| `/order/payment` | 支付页 | 交易 | 是 | `/order/prepay`、`/order/h5pay` | orderId、支付方式 | 支付失败 | `Authorization` | 可进入支付链路 |
| `/order/payment/:status` | 支付结果 | 交易 | 是 | 无 | status | 失败态 | 无 | 能正确回跳 |
| `/user` | 个人中心 | 用户 | 是 | 本地登录态、`/user/index` | 登录状态、快捷入口 | 未登录头图态 | `Authorization`、`avatar`、`nickName` | 可看到个人中心主入口 |
| `/user/collect` | 收藏 | 用户 | 是 | `/collect/list`、`/collect/addordelete` | 列表 | 空列表 | `Authorization` | 可查看和取消收藏 |
| `/user/address` | 地址列表 | 用户 | 是 | `/address/list`、`/address/delete` | 地址列表、默认地址 | 空列表 | `Authorization`、`AddressId` | 可查看、进入编辑、删除 |
| `/user/address/edit` | 地址编辑 | 用户 | 是 | `/address/detail`、`/address/save` | 表单、地区选择 | 参数缺失、校验失败 | `Authorization` | 可新增和编辑 |
| `/user/server` | 服务页 | 用户 | 否 | 无 | 静态入口 | 无 | 无 | 页面可访问 |
| `/user/help` | 帮助 | 用户 | 否 | `/issue/list` | FAQ 列表 | 空列表 | tenant header | 可浏览帮助 |
| `/user/feedback` | 意见反馈 | 用户 | 否 | `/feedback/submit` | 文本、联系方式 | 提交失败 | `Authorization` | 可提交反馈 |
| `/user/information` | 资料设置 | 用户 | 是 | `/user/info`、`/auth/profile` | 用户资料 | 未登录 | `Authorization`、`nickName` | 可查看入口 |
| `/user/information/setMobile` | 修改手机号 | 用户 | 是 | `/auth/captcha/mobile`、`/auth/profile` | mobile、code | 手机校验 | tenant header | 可更新手机号 |
| `/user/information/setNickname` | 修改昵称 | 用户 | 是 | `/auth/profile` | nickName | 空昵称 | `nickName` | 可更新昵称 |
| `/user/information/setPassword` | 修改密码 | 用户 | 是 | `/auth/captcha/mobile`、`/auth/reset` | code、password | 校验失败 | tenant header | 可更新密码 |
| `/user/order/list/:active` | 订单列表 | 用户/交易 | 是 | `/order/list`、`/order/cancel`、`/order/confirm`、`/order/delete` | active tab、列表 | 空列表 | `Authorization` | 可查看和操作订单 |
| `/user/coupon/list/:active` | 优惠券列表 | 用户 | 是 | `/coupon/mylist` | active tab、列表 | 空列表 | `Authorization` | 可查看优惠券 |
| `/user/refund/list` | 退款列表 | 用户/交易 | 是 | 暂无明确接口 | 列表占位 | 历史空实现 | `Authorization` | 至少保留兼容页面与说明 |
