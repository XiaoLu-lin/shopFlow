var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');
var app = getApp();


Page({

  /**
   * 页面的初始数据
   */
  data: {
    couponList: [],
    cartId: 0,
    couponId: 0,
    userCouponId: 0,
    grouponLinkId: 0,
    scrollTop: 0
  },

  getCouponList: function () {
    let that = this;
    that.setData({
      couponList: []
    });
    // 页面渲染完成
    wx.showToast({
      title: '加载中...',
      icon: 'loading',
      duration: 2000
    });
    util.request(api.CouponSelectList, {
      cartId: that.data.cartId,
      grouponRulesId: that.data.grouponRulesId,
    }).then(function (res) {
      if (res.errno === "success") {
        let list = [];
        for (var i = 0; i < res.data.list.length; i++) {
          if (res.data.list[i].available) {
            list.push(res.data.list[i]);
          }
        }
        that.setData({
          couponList: list
        });
        if (list.length <= 0) {
          wx.showModal({
            title: "提示信息",
            confirmColor: '#b4282d',
            content: "没有可用优惠券",
            showCancel: false,
            success: res => {
              if (res.confirm) {
                try {
                  wx.setStorageSync('couponId', -1);
                  wx.setStorageSync('userCouponId', -1);
                } catch (error) {
                  console.log(error);
                }
                wx.navigateBack();
              }
            },
          });
        }
      }
      wx.hideToast();
    });
  },

  selectCoupon: function (e) {
    try {
      wx.setStorageSync('couponId', e.currentTarget.dataset.cid);
      wx.setStorageSync('userCouponId', e.currentTarget.dataset.id);
    } catch (error) {
      console.log(error);
    }
    wx.navigateBack();
  },

  unselectCoupon: function() {
    // 如果优惠券ID设置-1，则表示订单不使用优惠券
    try {
      wx.setStorageSync('couponId', -1);
      wx.setStorageSync('userCouponId', -1);
    } catch (error) {
      console.log(error)
    }
    wx.navigateBack();
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    wx.showLoading({
      title: '加载中...',
      mask: true,
      duration: 3000
    });

    try {
      var cartId = wx.getStorageSync('cartId');
      if (!cartId) {
        cartId = 0;
      }

      var couponId = wx.getStorageSync('couponId');
      if (!couponId) {
        couponId = 0;
      }

      var userCouponId = wx.getStorageSync('userCouponId');
      if (!userCouponId) {
        userCouponId = 0;
      }

      var grouponRulesId = wx.getStorageSync('grouponRulesId');
      if (!grouponRulesId) {
        grouponRulesId = 0;
      }

      this.setData({
        cartId: cartId,
        couponId: couponId,
        userCouponId: userCouponId,
        grouponRulesId: grouponRulesId
      });
    } catch (e) {
      console.log(e);
    }

    this.getCouponList();
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
