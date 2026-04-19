var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');
// var plugin = requirePlugin("logisticsPlugin")
var app = getApp();


Page({

  /**
   * 页面的初始数据
   */
  data: {
    status: 0,
    orderBasics: 0,
    grouponBasics: 0,
    orderId: 0,
    linkGrouponId: 0,
    brandUser:{},
    rules:{},
    groupon: {},
    joiners: [],
    orderInfo: {},
    orderGoods: {},
    expressInfo: {},
    expandFlag: false,
    handleOption: {},
    orderBasicsList: [
      {icon: 'usefullfill', name: '待付款', color: 'text-orange'}, 
      {icon: 'timefill', name: '待发货', color: 'text-orange'}, 
      {icon: 'deliver', name: '待收货', color: 'text-orange'}, 
      {icon: 'comment', name: '待评论', color: 'text-orange'}, 
    ],
    groupBasicsList: [
      {icon: 'usefullfill', name: '待开团', color: 'text-orange'}, 
      {icon: 'timefill', name: '团购中', color: 'text-orange'}, 
      {icon: 'roundclosefill', name: '团购失败', color: 'text-orange'}, 
      {icon: 'roundcheckfill', name: '团购成功', color: 'text-green'}, 
    ],
  },

  //团购分享组件
  showShare: function() {
    let that = this;
    that.sharePop.togglePopup();
  },

  /**
   * 用户聊天
   */
  openChat: function(e) {
    let that = this;
    let orderInfo = that.data.orderInfo;
    if (app.globalData.hasLogin) {
      util.navigate("/pages/chat/chat?receiveUserId="+orderInfo.userId+"&chatName="+orderInfo.consignee);
    } else {
      util.navigate("/pages/auth/login/login");
    }
  },

  //查询物流详情
  searchDetail(){
    let that = this;
    util.request(api.ExpressLogistics, {
      orderSn: that.data.orderInfo.orderSn
    }).then(function(res) {
      console.log(res)
      if (res.errno === "success") {
        // 在此通过调用 api 来查询微信快递服务详情
        // 必须用预览才能测试这个功能，无法在工具端模拟
        // plugin.openWaybillTracking({
        //   waybillToken: res.data.waybillToken
        // });
      } else {
        //通过快递鸟查询物流详情
        util.request(api.ExpressApiTrack, {
          orderSn: that.data.orderInfo.orderSn
        }).then(function(res) {
          if (res.errno === "success") {
            that.setData({
              expandFlag: true,
              expressInfo: res.data.expressInfo,
            })
          }
        });
      }
    });
  },

  //关闭物流上拉窗
  expandDetail: function() {
    let that = this;
    that.setData({
      expandFlag: false
    })
  },

  //发货通知订阅
  shipSubscribe: function(){
    util.requestSubscribe(api.ShipTmplIds);
  },

  //查询订单详情
  getOrderDetail: function() {
    wx.showLoading({
      title: '加载中...',
      duration: 2000
    });
    let that = this;
    util.request(api.OrderDetail, {
      orderId: that.data.orderId
    }).then(function(res) {
      console.log(res)
      if (res.errno === "success") {
        //如果是店主访问订单取消所有订单操作
        let handleOption = res.data.orderInfo.handleOption;
        if(that.data.status == 1){
          handleOption = {};
        }
        //渲染数据
        that.setData({
          rules: res.data.rules,
          joiners: res.data.joiners,
          groupon: res.data.groupon,
          brandUser: res.data.brandUser,
          linkGrouponId: res.data.linkGrouponId,
          orderId: res.data.orderInfo.id,
          orderInfo: res.data.orderInfo,
          orderBasics: res.data.orderBasics,
          orderGoods: res.data.orderGoods,
          handleOption: handleOption,
          grouponBasics: res.data.grouponBasics,
        });
      }
      wx.hideLoading();
    });
  },

  // “去付款”按钮点击效果
  payOrder: function() {
    let that = this;
    // 防多点击，这个不用配合 wx.hideLoading();
    wx.showLoading({
      title: '加载中...',
      mask: true,
      duration: 3000
    });
    let orderIds = [];
    orderIds.push(that.data.orderId);
    util.request(api.OrderPrepay, {
      orderIds: orderIds
    }, 'POST').then(function(res) {
      if (res.errno === "success") {
        console.log("支付过程开始");
        wx.requestPayment({
          'timeStamp': res.data.timeStamp,
          'nonceStr': res.data.nonceStr,
          'package': res.data.packageValue,
          'signType': res.data.signType,
          'paySign': res.data.paySign,
          'success': function(res) {
            console.log("支付过程成功");
            util.requestSubscribe(api.ShipTmplIds);
            util.redirect('/pages/ucenter/order/order?tab=2');
          },
          'fail': function(res) {
            console.log("支付过程失败");
            util.showErrorModal('支付失败');
          },
          'complete': function(res) {
            console.log("支付过程结束")
          }
        });
      }else{
        console.log("支付调用失败");
      }
    });
  },

  // “取消订单”点击效果
  cancelOrder: function() {
    let that = this;
    let orderInfo = that.data.orderInfo;
    wx.showModal({
      title: '取消订单',
      content: '确定要取消此订单？',
      success: function(res) {
        if (res.confirm) {
          util.request(api.OrderCancel, {
            orderId: orderInfo.id
          }, 'POST').then(function(res) {
            if (res.errno === "success") {
              wx.showToast({
                title: '取消订单成功'
              });
              util.redirect('/pages/ucenter/order/order?tab=0');
            } 
          });
        }
      }
    });
  },
  
  // “取消订单并退款”点击效果
  refundOrder: function() {
    let that = this;
    let orderInfo = that.data.orderInfo;
    wx.showModal({
      title: '申请退款',
      content: '确定要取消此订单？',
      success: function(res) {
        if (res.confirm) {
          util.request(api.OrderRefund, {
            orderId: orderInfo.id
          }, 'POST').then(function(res) {
            if (res.errno === "success") {
              wx.showToast({
                title: '取消订单成功'
              });
              util.requestSubscribe(api.RefundTmplIds);
              util.redirect('/pages/ucenter/order/order?tab=0');
            } 
          });
        }
      },
    });
  },

  // “删除”点击效果
  deleteOrder: function() {
    let that = this;
    let orderInfo = that.data.orderInfo;
    wx.showModal({
      title: '删除订单',
      content: '确定要删除此订单？',
      success: function(res) {
        if (res.confirm) {
          util.request(api.OrderDelete, {
            orderId: orderInfo.id
          }, 'POST').then(function(res) {
            if (res.errno === "success") {
              wx.showToast({
                title: '删除订单成功'
              });
              util.redirect('/pages/ucenter/order/order?tab=0');
            } 
          });
        }
      }
    });
  },

  // “确认收货”点击效果
  confirmOrder: function() {
    let that = this;
    let orderInfo = that.data.orderInfo;
    wx.showModal({
      title: '确认收货',
      content: '确认已收货？',
      success: function(res) {
        if (res.confirm) {
          util.request(api.OrderConfirm, {
            orderId: orderInfo.id
          }, 'POST').then(function(res) {
            if (res.errno === "success") {
              wx.showToast({
                title: '确认收货成功！'
              });
              util.redirect('/pages/ucenter/order/order?tab=4');
            } 
          });
        }
      }
    });
  },
  
  // “申请售后”点击效果
  aftersaleOrder: function () {
    // 防多点击，这个不用配合 wx.hideLoading();
    let that = this;
    wx.showLoading({
      title: '加载中...',
      mask: true,
      duration: 3000
    });
    util.redirect('/pages/ucenter/aftersale/aftersale?id=' + that.data.orderId );
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    let that = this;
    that.setData({
      status : options.status,
      orderId: options.orderId,
    });
    that.getOrderDetail();
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {
    let that = this;
    that.sharePop = that.selectComponent("#sharePop");
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    let that = this;
    that.getOrderDetail();
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {
    wx.showNavigationBarLoading() //在标题栏中显示加载
    this.getOrderDetail();
    wx.hideNavigationBarLoading() //完成停止加载
    wx.stopPullDownRefresh() //停止下拉刷新
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {
    let that = this;
    if(that.data.groupon){
      return {
        title: '邀请团购',
        desc: '唯爱与美食不可辜负',
        path: '/pages/index/index?grouponId=' + that.data.linkGrouponId
      }
    }
  }
})

