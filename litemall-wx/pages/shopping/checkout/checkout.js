var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');

var app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    isSubmit: false,
    message: '',
    isDeduction: true,
    checkedGoodsList: [],
    checkedAddress: {},
    checkedCoupon: [],
    goodsTotalPrice: 0.00, //商品总价
    freightPrice: 0.00, //快递费
    couponPrice: 0.00, //优惠券的价格
    grouponPrice: 0.00, //团购优惠价格
    orderTotalPrice: 0.00, //订单总价
    integralPrice: 0.00,//余额优惠
    actualPrice: 0.00, //实际需要支付的总价
    cartId: 0,
    couponId: 0,
    addressId: 0,
    rewardLinkId: 0,
    userCouponId: 0,
    grouponLinkId: 0, //参与的团购，如果是发起则为0
    grouponRulesId: 0, //团购规则ID
    availableCouponLength: 0, // 可用的优惠券数量
  },

  //是否使用余额减免订单金额
  buidDeduction(e){
    let that = this;
    that.setData({
      isDeduction: !that.data.isDeduction,
    })
    that.getCheckoutInfo();
  },

  //获取checkou信息
  getCheckoutInfo: function() {
    let that = this;
    wx.showLoading({
      title: '加载中...',
      mask: true,
      duration: 3000
    });
    util.request(api.CartCheckout, {
      cartId: that.data.cartId,
      addressId: that.data.addressId,
      userCouponId: that.data.userCouponId,
      couponId: that.data.couponId,
      isDeduction: that.data.isDeduction,
      grouponRulesId: that.data.grouponRulesId
    }).then(function(res) {
      if (res.errno === "success") {
        console.log(res)
        that.setData({
          checkedGoodsList: res.data.checkedGoodsList,
          checkedAddress: res.data.checkedAddress,
          actualPrice: res.data.actualPrice,
          checkedCoupon: res.data.checkedCoupon,
          availableCouponLength: res.data.availableCouponLength,
          couponPrice: res.data.couponPrice,
          grouponPrice: res.data.grouponPrice,
          freightPrice: res.data.freightPrice,
          goodsTotalPrice: res.data.goodsTotalPrice,
          orderTotalPrice: res.data.orderTotalPrice,
          addressId: res.data.addressId,
          couponId: res.data.couponId,
          userCouponId: res.data.userCouponId,
          grouponRulesId: res.data.grouponRulesId,
          integralPrice: res.data.integralPrice,
        });
      }
      wx.hideLoading();
    });
  },
  
  selectAddress() {
    wx.navigateTo({
      url: '/pages/ucenter/address/address',
    })
  },

  selectCoupon() {
    wx.navigateTo({
      url: '/pages/ucenter/couponSelect/couponSelect',
    })
  },

  bindMessageInput: function(e) {
    this.setData({
      message: e.detail.value
    });
  },

  /**
   * 地址详情
   */
  getAddressDetail(addressId) {
    let that = this;
    util.request(api.AddressDetail, {
      id: addressId
    }).then(function (res) {
      if (res.errno === "success") {
        that.setData({
          addressId : addressId,
          checkedAddress: res.data
        });
      } else{
        wx.removeStorageSync('addressId')
        that.setData({
          addressId : 0,
          checkedAddress: {id : 0}
        })
      }
    });
  },

  submitOrder: function() {
    var that = this;
    if(that.data.addressId <= 0){
      util.showErrorModal('请选择收货地址');
      return false;
    }
    // 这个不用配合 wx.hideLoading();
    wx.showLoading({
      title: '加载中...',
      mask: true,
      duration: 3000
    });
    //提交订单后不刷新页面
    that.setData({
      isSubmit: true,
    })

    //提交订单
    util.request(api.OrderSubmit, {
      cartId: that.data.cartId,
      message: that.data.message,
      couponId: that.data.couponId,
      addressId: that.data.addressId,
      isDeduction: that.data.isDeduction,
      userCouponId: that.data.userCouponId,
      rewardLinkId: that.data.rewardLinkId,
      grouponLinkId: that.data.grouponLinkId,
      grouponRulesId: that.data.grouponRulesId,
    }, 'POST').then(res => {
      if (res.errno === "success") {
        //判断是否需要调用微信支付
        if (res.data.isPay) {
          wx.redirectTo({
            url: '/pages/shopping/payResult/payResult?status=1'
          });
          return
        }
        //调用微信支付
        wx.setStorageSync('orderIds', res.data.orderIds);
        util.request(api.OrderPrepay, {
          orderIds: res.data.orderIds
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
                //发起发货消息订阅
                util.requestSubscribe(api.ShipTmplIds);
                wx.redirectTo({
                  url: '/pages/shopping/payResult/payResult?status=1'
                });
              },
              'fail': function(res) {
                console.log("支付过程失败");
                wx.redirectTo({
                  url: '/pages/shopping/payResult/payResult?status=0'
                });
              },
              'complete': function(res) {
                console.log("支付过程结束")
              }
            });
          } else {
            wx.redirectTo({
              url: '/pages/shopping/payResult/payResult?status=0'
            });
          }
        });
      }
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    let that = this;
    try {
      var cartId = wx.getStorageSync('cartId');
      if (cartId === "") {
        cartId = 0;
      }
      var couponId = wx.getStorageSync('couponId');
      if (couponId === "") {
        couponId = 0;
      }
      let addressId = wx.getStorageSync('addressId');
      if (!addressId || addressId === "" || addressId == 0) {
        that.setData({
          addressId : 0,
          checkedAddress: {id : 0}
        })
      }else{
        that.getAddressDetail(addressId);
      }
      var userCouponId = wx.getStorageSync('userCouponId');
      if (userCouponId === "") {
        userCouponId = 0;
      }
      var rewardLinkId = wx.getStorageSync('rewardLinkId');
      if (rewardLinkId === "") {
        rewardLinkId = 0;
      }
      var grouponLinkId = wx.getStorageSync('grouponLinkId');
      if (grouponLinkId === "") {
        grouponLinkId = 0;
      }
      var grouponRulesId = wx.getStorageSync('grouponRulesId');
      if (grouponRulesId === "") {
        grouponRulesId = 0;
      }
      
      that.setData({
        cartId: cartId,
        couponId: couponId,
        addressId: addressId,
        rewardLinkId: rewardLinkId,
        userCouponId: userCouponId,
        grouponLinkId: grouponLinkId,
        grouponRulesId: grouponRulesId,
      });
    } catch (e) {
      console.log(e);
    }

    if(!that.data.isSubmit){
      that.getCheckoutInfo();
    }
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

  }
})
