let api = [];
const apiDocListSize = 1
api.push({
    name: 'default',
    order: '1',
    list: []
})
api[0].list.push({
    alias: 'WxCouponController',
    order: '1',
    link: '优惠券服务',
    desc: '优惠券服务',
    list: []
})
api[0].list[0].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/coupon/list',
    desc: '优惠券列表',
});
api[0].list[0].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/coupon/user',
    desc: '个人优惠券列表',
});
api[0].list[0].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/coupon/select',
    desc: '当前购物车下单商品订单可用优惠券',
});
api[0].list[0].list.push({
    order: '4',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/coupon/receive',
    desc: '优惠券领取',
});
api[0].list[0].list.push({
    order: '5',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/coupon/exchange',
    desc: '优惠券兑换',
});
api[0].list.push({
    alias: 'WxSearchController',
    order: '2',
    link: '商品搜索服务',
    desc: '商品搜索服务',
    list: []
})
api[0].list[1].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/search/index',
    desc: '搜索页面信息',
});
api[0].list[1].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/search/helper',
    desc: '关键字提醒 &lt;p&gt; 当用户输入关键字一部分时，可以推荐系统中合适的关键字。',
});
api[0].list[1].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/search/clear/history',
    desc: '清除用户搜索历史',
});
api[0].list.push({
    alias: 'WxAftersaleController',
    order: '3',
    link: '售后服务',
    desc: '售后服务',
    list: []
})
api[0].list[2].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/aftersale/list',
    desc: '售后列表',
});
api[0].list[2].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/aftersale/detail',
    desc: '售后详情',
});
api[0].list[2].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/aftersale/submit',
    desc: '申请售后',
});
api[0].list[2].list.push({
    order: '4',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/aftersale/cancel',
    desc: '取消售后',
});
api[0].list[2].list.push({
    order: '5',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/aftersale/recept',
    desc: '审核通过',
});
api[0].list[2].list.push({
    order: '6',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/aftersale/reject',
    desc: '审核驳回',
});
api[0].list[2].list.push({
    order: '7',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/aftersale/refund',
    desc: '售后退款',
});
api[0].list.push({
    alias: 'WxAuthController',
    order: '4',
    link: '鉴权服务',
    desc: '鉴权服务',
    list: []
})
api[0].list[3].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/auth/login_by_qr',
    desc: '扫码授权登陆',
});
api[0].list[3].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/auth/login',
    desc: '账号登录',
});
api[0].list[3].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/auth/captcha/mobile',
    desc: '请求手机验证码 TODO 这里需要一定机制防止短信验证码被滥用',
});
api[0].list[3].list.push({
    order: '4',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/auth/captcha/mail',
    desc: '请求邮箱验证码 TODO 这里需要一定机制防止短信验证码被滥用',
});
api[0].list[3].list.push({
    order: '5',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/auth/login_by_weixin',
    desc: '微信登录',
});
api[0].list[3].list.push({
    order: '6',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/auth/register',
    desc: '账号注册',
});
api[0].list[3].list.push({
    order: '7',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/auth/reset',
    desc: '账号密码重置',
});
api[0].list[3].list.push({
    order: '8',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/auth/resetPhone',
    desc: '账号手机号码重置',
});
api[0].list[3].list.push({
    order: '9',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/auth/profile',
    desc: '账号信息更新',
});
api[0].list[3].list.push({
    order: '10',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/auth/bindPhone',
    desc: '微信手机号码绑定',
});
api[0].list[3].list.push({
    order: '11',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/auth/logout',
    desc: '退出登陆',
});
api[0].list.push({
    alias: 'WxOrderController',
    order: '5',
    link: '订单服务',
    desc: '订单服务',
    list: []
})
api[0].list[4].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/order/list',
    desc: '订单列表',
});
api[0].list[4].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/order/detail',
    desc: '订单详情',
});
api[0].list[4].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/order/submit',
    desc: '提交订单',
});
api[0].list[4].list.push({
    order: '4',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/order/cancel',
    desc: '取消订单',
});
api[0].list[4].list.push({
    order: '5',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/order/prepay',
    desc: '付款订单的预支付会话标识',
});
api[0].list[4].list.push({
    order: '6',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/order/pay-notify',
    desc: '微信付款成功或失败回调接口 &lt;p&gt;  TODO  注意，这里pay-notify是示例地址，建议开发者应该设立一个隐蔽的回调地址',
});
api[0].list[4].list.push({
    order: '7',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/order/refund',
    desc: '订单申请退款',
});
api[0].list[4].list.push({
    order: '8',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/order/confirm',
    desc: '确认收货',
});
api[0].list[4].list.push({
    order: '9',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/order/delete',
    desc: '删除订单',
});
api[0].list[4].list.push({
    order: '10',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/order/goods',
    desc: '待评价订单商品信息',
});
api[0].list[4].list.push({
    order: '11',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/order/comment',
    desc: '评价订单商品',
});
api[0].list[4].list.push({
    order: '12',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/order/admin/refund',
    desc: '订单退款',
});
api[0].list[4].list.push({
    order: '13',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/order/admin/cancel',
    desc: '商家取消订单',
});
api[0].list[4].list.push({
    order: '14',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/order/admin/ship',
    desc: '发货',
});
api[0].list.push({
    alias: 'WxCatalogController',
    order: '6',
    link: '类目服务',
    desc: '类目服务',
    list: []
})
api[0].list[5].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/catalog/index',
    desc: '分类详情',
});
api[0].list[5].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/catalog/all',
    desc: '所有分类数据',
});
api[0].list[5].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/catalog/current',
    desc: '当前分类栏目',
});
api[0].list[5].list.push({
    order: '4',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/catalog/first',
    desc: '所有一级分类目录',
});
api[0].list[5].list.push({
    order: '5',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/catalog/second',
    desc: '所有二级分类目录',
});
api[0].list.push({
    alias: 'WxBrandController',
    order: '7',
    link: '店铺信息',
    desc: '店铺信息',
    list: []
})
api[0].list[6].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/brand/list',
    desc: '店铺列表',
});
api[0].list[6].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/brand/read',
    desc: '店铺详情',
});
api[0].list[6].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/brand/detail',
    desc: '店铺详情',
});
api[0].list[6].list.push({
    order: '4',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/brand/save',
    desc: '添加或修改店铺',
});
api[0].list[6].list.push({
    order: '5',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/brand/order',
    desc: '店铺订单列表',
});
api[0].list[6].list.push({
    order: '6',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/brand/goods/init',
    desc: '商品上传参数初始化',
});
api[0].list[6].list.push({
    order: '7',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/brand/goods/category',
    desc: '分类列表',
});
api[0].list[6].list.push({
    order: '8',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/brand/goods/list',
    desc: '店铺商品列表',
});
api[0].list[6].list.push({
    order: '9',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/brand/goods/detail',
    desc: '店铺商品详情',
});
api[0].list[6].list.push({
    order: '10',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/brand/goods/update',
    desc: '编辑店铺商品',
});
api[0].list[6].list.push({
    order: '11',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/brand/goods/delete',
    desc: '删除店铺商品',
});
api[0].list[6].list.push({
    order: '12',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/brand/goods/create',
    desc: '添加店铺商品',
});
api[0].list.push({
    alias: 'WxHomeController',
    order: '8',
    link: '首页服务',
    desc: '首页服务',
    list: []
})
api[0].list[7].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/home/auth',
    desc: '小程序授权',
});
api[0].list[7].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/home/index',
    desc: '首页数据',
});
api[0].list[7].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/home/navigate',
    desc: '判断首页初始化参数是否支持跳转',
});
api[0].list[7].list.push({
    order: '4',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/home/about',
    desc: '商城介绍信息',
});
api[0].list.push({
    alias: 'WxGoodsController',
    order: '9',
    link: '商品服务',
    desc: '商品服务',
    list: []
})
api[0].list[8].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/goods/detail',
    desc: '商品详情',
});
api[0].list[8].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/goods/category',
    desc: '商品分类类目',
});
api[0].list[8].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/goods/list',
    desc: '根据条件搜素商品',
});
api[0].list[8].list.push({
    order: '4',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/goods/related',
    desc: '商品详情页面“大家都在看”推荐商品',
});
api[0].list[8].list.push({
    order: '5',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/goods/count',
    desc: '在售的商品总数',
});
api[0].list[8].list.push({
    order: '6',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/goods/comment/count',
    desc: '评论数量',
});
api[0].list[8].list.push({
    order: '7',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/goods/comment/list',
    desc: '评论列表',
});
api[0].list.push({
    alias: 'WxUserController',
    order: '10',
    link: '用户服务',
    desc: '用户服务',
    list: []
})
api[0].list[9].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/user/index',
    desc: '用户个人页面数据',
});
api[0].list[9].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/user/info',
    desc: '用户基本信息',
});
api[0].list[9].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/user/share',
    desc: '用户个人分享记录',
});
api[0].list[9].list.push({
    order: '4',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/user/integral',
    desc: '获取用户余额',
});
api[0].list[9].list.push({
    order: '5',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/user/trading-record',
    desc: '获取用户交易记录',
});
api[0].list[9].list.push({
    order: '6',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/user/withdraw-deposit',
    desc: '余额提现',
});
api[0].list.push({
    alias: 'WxIndexController',
    order: '11',
    link: '测试服务',
    desc: '测试服务',
    list: []
})
api[0].list[10].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/index/index',
    desc: '测试数据',
});
api[0].list.push({
    alias: 'WxMsgController',
    order: '12',
    link: '微信消息推送配置',
    desc: '微信消息推送配置',
    list: []
})
api[0].list[11].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/msg/welcome',
    desc: '消息校验，确定是微信发送的消息',
});
api[0].list[11].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/msg/welcome',
    desc: '微信小程序事件推送',
});
api[0].list.push({
    alias: 'WxGrouponController',
    order: '13',
    link: '团购服务',
    desc: '团购服务',
    list: []
})
api[0].list[12].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/groupon/list',
    desc: '团购规则列表',
});
api[0].list[12].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/groupon/join',
    desc: '参加团购',
});
api[0].list.push({
    alias: 'WxLikeController',
    order: '14',
    link: '用户点赞服务',
    desc: '用户点赞服务',
    list: []
})
api[0].list[13].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/like/submit',
    desc: '点赞',
});
api[0].list.push({
    alias: 'WxTopicController',
    order: '15',
    link: '专题服务',
    desc: '专题服务',
    list: []
})
api[0].list[14].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/topic/list',
    desc: '专题列表',
});
api[0].list[14].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/topic/detail',
    desc: '专题详情',
});
api[0].list[14].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/topic/related',
    desc: '相关专题',
});
api[0].list.push({
    alias: 'WxAddressController',
    order: '16',
    link: '用户收货地址服务',
    desc: '用户收货地址服务',
    list: []
})
api[0].list[15].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/address/list',
    desc: '用户收货地址列表',
});
api[0].list[15].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/address/tenant',
    desc: '获取租户地址',
});
api[0].list[15].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/address/detail',
    desc: '收货地址详情',
});
api[0].list[15].list.push({
    order: '4',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/address/save',
    desc: '添加或更新收货地址',
});
api[0].list[15].list.push({
    order: '5',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/address/delete',
    desc: '删除收货地址',
});
api[0].list.push({
    alias: 'WxExpressController',
    order: '17',
    link: '物流查询接口',
    desc: '物流查询接口',
    list: []
})
api[0].list[16].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/express/channel',
    desc: '查询物流公司',
});
api[0].list[16].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/express/api-track',
    desc: '通过快递鸟查询物流',
});
api[0].list[16].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/express/logistics',
    desc: '获取微信物流查询插件token',
});
api[0].list.push({
    alias: 'WxFeedbackController',
    order: '18',
    link: '意见反馈服务',
    desc: '意见反馈服务',
    list: []
})
api[0].list[17].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/feedback/submit',
    desc: '添加意见反馈',
});
api[0].list.push({
    alias: 'WxMessageController',
    order: '19',
    link: '用户聊天列表',
    desc: '用户聊天列表',
    list: []
})
api[0].list[18].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/message/list',
    desc: '获取聊天记录',
});
api[0].list[18].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/message/history',
    desc: '获取历史聊天记录',
});
api[0].list[18].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/message/delete',
    desc: '删除聊天记录',
});
api[0].list.push({
    alias: 'WxFootprintController',
    order: '20',
    link: '用户访问足迹服务',
    desc: '用户访问足迹服务',
    list: []
})
api[0].list[19].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/footprint/list',
    desc: '用户足迹列表',
});
api[0].list[19].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/footprint/delete',
    desc: '删除用户足迹',
});
api[0].list.push({
    alias: 'WxStorageController',
    order: '21',
    link: '对象存储服务',
    desc: '对象存储服务',
    list: []
})
api[0].list[20].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/storage/upload',
    desc: '获取微信小程序上传的图片',
});
api[0].list[20].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/storage/uploads',
    desc: '获取微信小程序上传的图片',
});
api[0].list[20].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/storage/fetch/{key}',
    desc: '访问存储对象',
});
api[0].list[20].list.push({
    order: '4',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/storage/download/{key}',
    desc: '访问存储对象',
});
api[0].list.push({
    alias: 'WxCollectController',
    order: '22',
    link: '用户收藏服务',
    desc: '用户收藏服务',
    list: []
})
api[0].list[21].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/collect/list',
    desc: '用户收藏列表',
});
api[0].list[21].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/collect/update',
    desc: '用户收藏添加和取消',
});
api[0].list.push({
    alias: 'WxIssueController',
    order: '23',
    link: '常见问题服务',
    desc: '常见问题服务',
    list: []
})
api[0].list[22].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/issue/list',
    desc: '帮助中心',
});
api[0].list.push({
    alias: 'WxRewardController',
    order: '24',
    link: '赏金接口',
    desc: '赏金接口',
    list: []
})
api[0].list[23].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/reward/list',
    desc: '赏金列表',
});
api[0].list[23].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/reward/join',
    desc: '参加赏金',
});
api[0].list[23].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/reward/create',
    desc: '添加赏金参与信息',
});
api[0].list.push({
    alias: 'WxCommentController',
    order: '25',
    link: '用户评论服务',
    desc: '用户评论服务',
    list: []
})
api[0].list[24].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/comment/list',
    desc: '评论列表',
});
api[0].list[24].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/comment/reply-list',
    desc: '回复评论列表',
});
api[0].list[24].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/comment/count',
    desc: '评论数量',
});
api[0].list[24].list.push({
    order: '4',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/comment/submit',
    desc: '评论回复',
});
api[0].list.push({
    alias: 'WxDynamicController',
    order: '26',
    link: '动态信息',
    desc: '动态信息',
    list: []
})
api[0].list[25].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/dynamic/list',
    desc: '动态列表',
});
api[0].list[25].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/dynamic/submit',
    desc: '发布日常',
});
api[0].list[25].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/dynamic/delete',
    desc: '删除日常',
});
api[0].list.push({
    alias: 'WxCartController',
    order: '27',
    link: '用户购物车服务',
    desc: '用户购物车服务',
    list: []
})
api[0].list[26].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/cart/index',
    desc: '用户购物车信息',
});
api[0].list[26].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/cart/add',
    desc: '加入商品到购物车 &lt;p&gt; 如果已经存在购物车货品，则增加数量； 否则添加新的购物车货品项。',
});
api[0].list[26].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/cart/fast/add',
    desc: '立即购买 &lt;p&gt; 和add方法的区别在于： 1. 如果购物车内已经存在购物车货品，前者的逻辑是数量添加，这里的逻辑是数量覆盖 2. 添加成功以后，前者的逻辑是返回当前购物车商品数量，这里的逻辑是返回对应购物车项的ID',
});
api[0].list[26].list.push({
    order: '4',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/cart/update',
    desc: '修改购物车商品货品数量',
});
api[0].list[26].list.push({
    order: '5',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/cart/checked',
    desc: '购物车商品货品勾选状态',
});
api[0].list[26].list.push({
    order: '6',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/cart/delete',
    desc: '购物车商品删除',
});
api[0].list[26].list.push({
    order: '7',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/cart/count',
    desc: '购物车商品货品数量',
});
api[0].list[26].list.push({
    order: '8',
    deprecated: 'false',
    url: 'https://www.ysling.com.cn/wx/cart/checkout',
    desc: '购物车下单',
});
document.onkeydown = keyDownSearch;
function keyDownSearch(e) {
    const theEvent = e;
    const code = theEvent.keyCode || theEvent.which || theEvent.charCode;
    if (code === 13) {
        const search = document.getElementById('search');
        const searchValue = search.value.toLocaleLowerCase();

        let searchGroup = [];
        for (let i = 0; i < api.length; i++) {

            let apiGroup = api[i];

            let searchArr = [];
            for (let i = 0; i < apiGroup.list.length; i++) {
                let apiData = apiGroup.list[i];
                const desc = apiData.desc;
                if (desc.toLocaleLowerCase().indexOf(searchValue) > -1) {
                    searchArr.push({
                        order: apiData.order,
                        desc: apiData.desc,
                        link: apiData.link,
                        list: apiData.list
                    });
                } else {
                    let methodList = apiData.list || [];
                    let methodListTemp = [];
                    for (let j = 0; j < methodList.length; j++) {
                        const methodData = methodList[j];
                        const methodDesc = methodData.desc;
                        if (methodDesc.toLocaleLowerCase().indexOf(searchValue) > -1) {
                            methodListTemp.push(methodData);
                            break;
                        }
                    }
                    if (methodListTemp.length > 0) {
                        const data = {
                            order: apiData.order,
                            desc: apiData.desc,
                            link: apiData.link,
                            list: methodListTemp
                        };
                        searchArr.push(data);
                    }
                }
            }
            if (apiGroup.name.toLocaleLowerCase().indexOf(searchValue) > -1) {
                searchGroup.push({
                    name: apiGroup.name,
                    order: apiGroup.order,
                    list: searchArr
                });
                continue;
            }
            if (searchArr.length === 0) {
                continue;
            }
            searchGroup.push({
                name: apiGroup.name,
                order: apiGroup.order,
                list: searchArr
            });
        }
        let html;
        if (searchValue === '') {
            const liClass = "";
            const display = "display: none";
            html = buildAccordion(api,liClass,display);
            document.getElementById('accordion').innerHTML = html;
        } else {
            const liClass = "open";
            const display = "display: block";
            html = buildAccordion(searchGroup,liClass,display);
            document.getElementById('accordion').innerHTML = html;
        }
        const Accordion = function (el, multiple) {
            this.el = el || {};
            this.multiple = multiple || false;
            const links = this.el.find('.dd');
            links.on('click', {el: this.el, multiple: this.multiple}, this.dropdown);
        };
        Accordion.prototype.dropdown = function (e) {
            const $el = e.data.el;
            let $this = $(this), $next = $this.next();
            $next.slideToggle();
            $this.parent().toggleClass('open');
            if (!e.data.multiple) {
                $el.find('.submenu').not($next).slideUp("20").parent().removeClass('open');
            }
        };
        new Accordion($('#accordion'), false);
    }
}

function buildAccordion(apiGroups, liClass, display) {
    let html = "";
    if (apiGroups.length > 0) {
        if (apiDocListSize === 1) {
            let apiData = apiGroups[0].list;
            let order = apiGroups[0].order;
            for (let j = 0; j < apiData.length; j++) {
                html += '<li class="'+liClass+'">';
                html += '<a class="dd" href="#_'+order+'_'+apiData[j].order+'_' + apiData[j].link + '">' + apiData[j].order + '.&nbsp;' + apiData[j].desc + '</a>';
                html += '<ul class="sectlevel2" style="'+display+'">';
                let doc = apiData[j].list;
                for (let m = 0; m < doc.length; m++) {
                    let spanString;
                    if (doc[m].deprecated === 'true') {
                        spanString='<span class="line-through">';
                    } else {
                        spanString='<span>';
                    }
                    html += '<li><a href="#_'+order+'_' + apiData[j].order + '_' + doc[m].order + '_' + doc[m].desc + '">' + apiData[j].order + '.' + doc[m].order + '.&nbsp;' + spanString + doc[m].desc + '<span></a> </li>';
                }
                html += '</ul>';
                html += '</li>';
            }
        } else {
            for (let i = 0; i < apiGroups.length; i++) {
                let apiGroup = apiGroups[i];
                html += '<li class="'+liClass+'">';
                html += '<a class="dd" href="#_'+apiGroup.order+'_' + apiGroup.name + '">' + apiGroup.order + '.&nbsp;' + apiGroup.name + '</a>';
                html += '<ul class="sectlevel1">';

                let apiData = apiGroup.list;
                for (let j = 0; j < apiData.length; j++) {
                    html += '<li class="'+liClass+'">';
                    html += '<a class="dd" href="#_'+apiGroup.order+'_'+ apiData[j].order + '_'+ apiData[j].link + '">' +apiGroup.order+'.'+ apiData[j].order + '.&nbsp;' + apiData[j].desc + '</a>';
                    html += '<ul class="sectlevel2" style="'+display+'">';
                    let doc = apiData[j].list;
                    for (let m = 0; m < doc.length; m++) {
                       let spanString;
                       if (doc[m].deprecated === 'true') {
                           spanString='<span class="line-through">';
                       } else {
                           spanString='<span>';
                       }
                       html += '<li><a href="#_'+apiGroup.order+'_' + apiData[j].order + '_' + doc[m].order + '_' + doc[m].desc + '">'+apiGroup.order+'.' + apiData[j].order + '.' + doc[m].order + '.&nbsp;' + spanString + doc[m].desc + '<span></a> </li>';
                   }
                    html += '</ul>';
                    html += '</li>';
                }

                html += '</ul>';
                html += '</li>';
            }
        }
    }
    return html;
}