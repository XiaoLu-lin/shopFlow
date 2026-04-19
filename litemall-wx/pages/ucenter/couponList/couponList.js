var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');
var app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    tableName:['待使用','已使用','已过期'],
    couponList: [],
    code: '',
    status: 0,
    page: 1,
    limit: 10,
    totalPages: 1
  },

  getCouponList: function() {
    let that = this;
    wx.showLoading({
      title: '加载中...',
      mask: true,
      duration: 3000
    });
    util.request(api.CouponMyList, {
      status: that.data.status,
      page: that.data.page,
      limit: that.data.limit
    }).then(function(res) {
      console.log(res)
      if (res.errno === "success") {
        that.setData({
          couponList: that.data.couponList.concat(res.data.list),
          totalPages: res.data.pages,
        });
      }
      wx.hideLoading();
    });
  },

  bindExchange: function (e) {
    this.setData({
      code: e.detail.value
    });
  },

  clearExchange: function () {
    this.setData({
      code: ''
    });
  },

  goExchange: function() {
    if (this.data.code.length === 0) {
      util.showErrorModal("请输入兑换码");
      return;
    }
    let that = this;
    util.request(api.CouponExchange, {
      code: that.data.code
    }, 'POST').then(function (res) {
      if (res.errno === "success") {
        this.setData({
          page : 1,
          couponList: [],
        })
        that.getCouponList();
        that.clearExchange();
        wx.showToast({
          title: "领取成功",
          duration: 2000
        })
      } 
    });
  },

  switchTab: function(e) {
    this.setData({
      status: e.currentTarget.dataset.index,
      couponList: [],
      page: 1,
      limit: 10,
      totalPages: 1
    });
    this.getCouponList();
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      page : 1,
      couponList: [],
    })
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
    wx.showNavigationBarLoading() //在标题栏中显示加载
    this.setData({
      page : 1,
      couponList: [],
    })
    this.getCouponList();
    wx.hideNavigationBarLoading() //完成停止加载
    wx.stopPullDownRefresh() //停止下拉刷新
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {
    if (this.data.totalPages > this.data.page) {
      this.setData({
        page: this.data.page + 1
      });
      this.getCouponList();
    } else {
      wx.showToast({
        title: '没有更多信息了',
        icon: 'none',
        duration: 2000
      });
      return false;
    }
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  }
})
