# Review: align-litemall-vue-tenant-user-headers

## Conclusion

本次 change 已完成，可以归档。

已完成内容：

- `litemall-vue` 已从旧 `X-Litemall-Token` 协议切换为当前 ShopFlow 的 `X-ShopFlow-User-Token` 与 `X-ShopFlow-TenantId`。
- 前台已支持通过 `appid` 初始化租户 token，并在登录、注册、验证码、找回密码等鉴权链路前复用租户预热。
- `shopflow-wx-api` 已新增旧 H5 普通用户账号密码登录兼容入口，避免改写现有 `/wx/auth/login` 管理员绑定语义。
- 登录成功后的 `token/userToken` 已统一归一到前台本地 `Authorization`，供请求层继续映射。
- 注册验证码、注册提交、找回密码和密码重置流程已对齐当前 `/wx/auth/*` 接口。
- README 已留存 H5 登录流程图，方便后续交接。

## Verification

已完成的验证：

- `node litemall-vue/scripts/shopflow-compat.test.js` 通过。
- `npm run build` 曾完成前台构建，仅保留项目原有 Sass 与 chunk warning。
- `mvn -pl shopflow-wx-api -am -DskipTests compile` 曾通过。
- `mvn -pl shopflow-all -am -DskipTests package` 通过。
- 本地运行态验证 `/wx/home/auth` 可返回租户 token。
- 本地运行态验证 `/wx/home/index?appid=1649067` 可访问。
- 本地运行态验证 `/wx/auth/login_legacy` 对普通 H5 用户可登录并返回 `userToken`。
- 本地运行态验证 `/wx/auth/captcha/mobile` 在携带租户头时可匿名访问。

## Residual Risks

- 当前 H5 兼容范围覆盖基础租户与鉴权链路，不代表旧 `litemall-vue` 所有商品、购物车、订单等历史接口差异都已完成迁移。
- 邮箱登录属于旧前台历史分支，当前兼容入口明确不恢复该分支。
- 正式部署仍需要配置真实 `VUE_APP_BASE_API` 与 `VUE_APP_SHOPFLOW_APPID`；当前 `1649067` 是本地联调可用值。
- 后端测试存在现有 surefire 发现配置问题，相关新增测试可编译，但测试运行报告曾显示 `Tests run: 0`。
