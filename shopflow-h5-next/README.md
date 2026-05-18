# ShopFlow H5 Next

`shopflow-h5-next` 是当前用户侧前台主工程，基于 uni-app + Vue 3 + TypeScript。

## API Path

- 开发环境默认通过 `/wx` 代理到本地 `http://127.0.0.1:6914`
- 生产环境默认用户侧 API 根地址为 `https://manager.enshipeixue.com/wx-api/`

这样可以让线上：

- `https://manager.enshipeixue.com/` 继续承载后台页面
- `https://manager.enshipeixue.com/wx/` 承载 H5 前台页面
- `https://manager.enshipeixue.com/wx-api/` 独立承载用户侧接口

避免 H5 页面路由与 `/wx/*` 后端接口发生路径冲突。
