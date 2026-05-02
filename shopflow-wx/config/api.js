// 以下是业务服务器API地址
function getEnvVersion() {
  try {
    if (typeof wx !== 'undefined' && wx.getAccountInfoSync) {
      return wx.getAccountInfoSync().miniProgram.envVersion || 'develop';
    }
  } catch (e) {
  }
  return 'develop';
}

const LOCAL_DEV_HOST = 'http://127.0.0.1:6914';
const LOCAL_DEV_WS_HOST = 'ws://127.0.0.1:6914';
const ONLINE_HOST = 'https://manager.enshipeixue.com';
const ONLINE_WS_HOST = 'wss://manager.enshipeixue.com';

const ENV_CONFIG = {
  develop: {
    apiRoot: LOCAL_DEV_HOST + '/wx/',
    wsRoot: LOCAL_DEV_WS_HOST + '/websocket',
    useWxLoginForAccountLogin: false,
    adminUrl: LOCAL_DEV_HOST + '/#/login',
  },
  trial: {
    apiRoot: ONLINE_HOST + '/wx/',
    wsRoot: ONLINE_WS_HOST + '/websocket',
    useWxLoginForAccountLogin: true,
    adminUrl: ONLINE_HOST + '/#/login',
  },
  release: {
    apiRoot: ONLINE_HOST + '/wx/',
    wsRoot: ONLINE_WS_HOST + '/websocket',
    useWxLoginForAccountLogin: true,
    adminUrl: ONLINE_HOST + '/#/login',
  },
};

const currentEnvVersion = getEnvVersion();
const currentEnvConfig = ENV_CONFIG[currentEnvVersion] || ENV_CONFIG.develop;
var WxApiRoot = currentEnvConfig.apiRoot;

module.exports = {
  EnvVersion: currentEnvVersion,
  // 管理员账号登录前是否先调用 wx.login，开发版联调默认关闭
  UseWxLoginForAccountLogin: currentEnvConfig.useWxLoginForAccountLogin,
  // Socket链接
  WSS_SERVER_URL: currentEnvConfig.wsRoot,

  adminUrl: currentEnvConfig.adminUrl,//管理后台地址
  ShipTmplIds: 'Uy7q5hPRyiL2IRZo22HQ5Je8quwKoL7kzpX3S0SC5q4',//发货订阅模板id
  RefundTmplIds: 'vjTL2TZURvShCeFnjo7Shu0v6D_r9kS3GsvBHcd4tJM',//退款订阅模板id
  NewOrderTmplIds: '9cgvWe9phZfc4_AgAfFGBJWie0FfrVe3Rae-puLzL2s',//新订单订阅模板id
  newMessageTmplId: "e_rfKqJ3Da3q8jvABCGsolnQh4b6W-IdIbhgj2369nA",//新消息订阅通知

  CommentList: WxApiRoot + 'comment/list', //评论列表
  CommentReplyList: WxApiRoot + 'comment/reply-list', //回复评论列表
  CommentCount: WxApiRoot + 'comment/count', //评论总数
  CommentSubmit: WxApiRoot + 'comment/submit', //发表评论

  LikeSubmit: WxApiRoot + 'like/submit', //点赞，取消点赞

  MessageList: WxApiRoot + 'message/list', //消息列表,
  MessageHistory: WxApiRoot + 'message/history', //历史记录,
  MessageDelete: WxApiRoot + 'message/delete', //删除消息列表,

  UserIndex: WxApiRoot + 'user/index', //个人页面用户相关信息
  UserInfo: WxApiRoot + 'user/info', //个人信息
  UserShare: WxApiRoot + 'user/share', //个人分享信息
  UserIntegral: WxApiRoot + 'user/integral', //用户余额
  UserTradingRecord: WxApiRoot + 'user/trading-record', //交易记录
  UserWithdrawDeposit: WxApiRoot + 'user/withdraw-deposit', //余额提现

  StorageUpload: WxApiRoot + 'storage/upload', //图片上传,
  IssueList: WxApiRoot + 'issue/list', //帮助信息
  IndexUrl: WxApiRoot + 'home/index', //首页数据接口
  AboutUrl: WxApiRoot + 'home/about', //介绍信息
  AuthUrl: WxApiRoot + 'home/auth', //微信授权
  NavigateUrl: WxApiRoot + 'home/navigate', //首页跳转判断

  DynamicSubmit: WxApiRoot + 'dynamic/submit',//发布日常
  DynamicList: WxApiRoot + 'dynamic/list',//获取布列表
  TimeLineDelete: WxApiRoot + 'dynamic/delete',//获取布列表

  RewardJoin: WxApiRoot + 'reward/join',//获取赏金详情
  RewardList: WxApiRoot + 'reward/list',//获取赏金列表
  RewardCreate: WxApiRoot + 'reward/create',//添加赏金

  CatalogList: WxApiRoot + 'catalog/index', //分类目录全部分类数据接口
  CatalogCurrent: WxApiRoot + 'catalog/current', //分类目录当前分类数据接口
  CatalogAll: WxApiRoot + 'catalog/all', //分类目录当前分类数据接口

  AuthLoginByQr: WxApiRoot + 'auth/login_by_qr', //扫码登陆
  AuthLoginByWeixin: WxApiRoot + 'auth/login_by_weixin', //微信登录
  AuthLoginByAccount: WxApiRoot + 'auth/login', //账号登录
  AuthLogout: WxApiRoot + 'auth/logout', //账号登出
  AuthRegister: WxApiRoot + 'auth/register', //账号注册
  AuthReset: WxApiRoot + 'auth/reset', //账号密码重置
  AuthProfile: WxApiRoot + 'auth/profile', //账号信息修改
  AuthMobileCaptcha: WxApiRoot + 'auth/captcha/mobile', //手机验证码
  AuthMailCaptcha: WxApiRoot + 'auth/captcha/mail', //邮箱验证码
  AuthBindPhone: WxApiRoot + 'auth/bindPhone', //绑定微信手机号

  GoodsCount: WxApiRoot + 'goods/count', //统计商品总数
  GoodsList: WxApiRoot + 'goods/list', //获得商品列表
  GoodsCategory: WxApiRoot + 'goods/category', //获得分类数据
  GoodsDetail: WxApiRoot + 'goods/detail', //获得商品的详情
  GoodsRelated: WxApiRoot + 'goods/related', //商品详情页的关联商品（大家都在看）

  GoodsCommentList: WxApiRoot + 'goods/comment/list', //评论列表
  GoodsCommentCount: WxApiRoot + 'goods/comment/count', //评论总数
  GoodsCommentPost: WxApiRoot + 'goods/comment/post', //发表评论

  BrandList: WxApiRoot + 'brand/list', //店铺列表
  BrandRead: WxApiRoot + 'brand/read', //店铺信息
  BrandDetail: WxApiRoot + 'brand/detail', //店铺详情
  BrandOrder: WxApiRoot + 'brand/order', //店铺订单
  BrandSave: WxApiRoot + 'brand/save', //创建店铺或修改店铺

  BrandGoodsInit: WxApiRoot + 'brand/goods/init', //商品上传初始化参数
  BrandGoodsList: WxApiRoot + 'brand/goods/list', //获取店铺商品列表
  BrandGoodsUpdate: WxApiRoot + 'brand/goods/update', //修改店铺商品
  BrandGoodsCreate: WxApiRoot + 'brand/goods/create', //添加店铺商品
  BrandGoodsDetail: WxApiRoot + 'brand/goods/detail', //店铺商品详情
  BrandGoodsDelete: WxApiRoot + 'brand/goods/delete', //删除店铺商品
  BrandGoodsCategory: WxApiRoot + 'brand/goods/category', //获取商品分类

  CartList: WxApiRoot + 'cart/index', //获取购物车的数据
  CartAdd: WxApiRoot + 'cart/add', // 添加商品到购物车
  CartFastAdd: WxApiRoot + 'cart/fast/add', // 立即购买商品
  CartUpdate: WxApiRoot + 'cart/update', // 更新购物车的商品
  CartDelete: WxApiRoot + 'cart/delete', // 删除购物车的商品
  CartChecked: WxApiRoot + 'cart/checked', // 选择或取消选择商品
  CartGoodsCount: WxApiRoot + 'cart/count', // 获取购物车商品件数
  CartCheckout: WxApiRoot + 'cart/checkout', // 下单前信息确认

  CollectList: WxApiRoot + 'collect/list', //收藏列表
  CollectAddOrDelete: WxApiRoot + 'collect/update', //添加或取消收藏

  TopicList: WxApiRoot + 'topic/list', //专题列表
  TopicDetail: WxApiRoot + 'topic/detail', //专题详情
  TopicRelated: WxApiRoot + 'topic/related', //相关专题
  TopicComment: WxApiRoot + 'topic/comment', //上传专题评论

  SearchIndex: WxApiRoot + 'search/index', //搜索关键字
  SearchResult: WxApiRoot + 'search/result', //搜索结果
  SearchHelper: WxApiRoot + 'search/helper', //搜索帮助
  SearchClearHistory: WxApiRoot + 'search/clear/history', //搜索历史清楚

  AddressList: WxApiRoot + 'address/list', //收货地址列表
  AddressDetail: WxApiRoot + 'address/detail', //收货地址详情
  AddressSave: WxApiRoot + 'address/save', //保存收货地址
  AddressTenant: WxApiRoot + 'address/tenant', //保存收货地址
  AddressDelete: WxApiRoot + 'address/delete', //保存收货地址

  RegionList: WxApiRoot + 'region/list', //获取区域列表

  OrderSubmit: WxApiRoot + 'order/submit', // 提交订单
  OrderPrepay: WxApiRoot + 'order/prepay', // 订单的预支付会话
  OrderList: WxApiRoot + 'order/list', //订单列表
  OrderDetail: WxApiRoot + 'order/detail', //订单详情
  OrderCancel: WxApiRoot + 'order/cancel', //取消订单
  OrderRefund: WxApiRoot + 'order/refund', //退款取消订单
  OrderDelete: WxApiRoot + 'order/delete', //删除订单
  OrderConfirm: WxApiRoot + 'order/confirm', //确认收货
  OrderGoods: WxApiRoot + 'order/goods', // 代评价商品信息
  OrderComment: WxApiRoot + 'order/comment', // 评价订单商品信息
  
  OrderAdminShip:WxApiRoot+'order/admin/ship',//商家发货
  OrderAdminCancel:WxApiRoot+'order/admin/cancel',//商家取消订单
  OrderAdminRefund:WxApiRoot+'order/admin/refund',//商家退款

  ExpressChannel: WxApiRoot + 'express/channel', // 查询物流公司
  ExpressLogistics: WxApiRoot + 'express/logistics', // 查询物流插件token
  ExpressApiTrack: WxApiRoot + 'express/api-track', // 通过快递鸟查询物流

  AftersaleList: WxApiRoot + 'aftersale/list', // 售后列表
  AftersaleSubmit: WxApiRoot + 'aftersale/submit', // 提交售后申请
  AftersaleDetail: WxApiRoot + 'aftersale/detail', // 售后详情
  AftersaleRecept: WxApiRoot + 'aftersale/recept', // 售后通过
  AftersaleReject: WxApiRoot + 'aftersale/reject', // 售后拒绝
  AftersaleRefund: WxApiRoot + 'aftersale/refund', // 售后退款
  
  FeedbackAdd: WxApiRoot + 'feedback/submit', //添加反馈
  FootprintList: WxApiRoot + 'footprint/list', //足迹列表
  FootprintDelete: WxApiRoot + 'footprint/delete', //删除足迹

  GroupOnList: WxApiRoot + 'groupon/list', //团购列表
  GroupOnJoin: WxApiRoot + 'groupon/join', //团购API-详情

  CouponList: WxApiRoot + 'coupon/list', //优惠券列表
  CouponMyList: WxApiRoot + 'coupon/user', //我的优惠券列表
  CouponSelectList: WxApiRoot + 'coupon/select', //当前订单可用优惠券列表
  CouponReceive: WxApiRoot + 'coupon/receive', //优惠券领取
  CouponExchange: WxApiRoot + 'coupon/exchange', //优惠券兑换

 
};
