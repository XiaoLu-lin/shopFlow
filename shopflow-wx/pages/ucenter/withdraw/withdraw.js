// pages/ucenter/withdraw/withdraw.js
var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');
var app = getApp()

Page({

  /**
   * 页面的初始数据
   */
  data: {
    showPop: false,
    integral: 0,
    dealList: [],
    page: 1,
    limit: 10,
    totalPages: 1
  },

  showModal(){
    let that = this;
    that.setData({
      showPop: !that.data.showPop,
    })
  },

  //微信提现到零钱
  cashAdvance(e){
    let that = this;
    let cashAmount = parseFloat(e.detail.value.cashAmount);
    if(!cashAmount){
      util.showErrorToast("金额不正确");
      return;
    }
    if(cashAmount > that.data.integral){
      util.showErrorToast("余额不足");
      return;
    }
    if(cashAmount > 1000 || cashAmount < 1){
      util.showErrorModal("微信提现限额 1~1000 元");
      return;
    }
    //关闭弹窗
    that.showModal();
    //发送请求
    util.request(api.UserWithdrawDeposit,{
      cashAmount: cashAmount,
    },'POST').then(function(res) {
      if (res.errno === "success") {
        //成功提示
        util.showOkToast("提现成功");
        //刷新余额
        that.userWithdraw();
        //刷新交易记录
        that.setData({
          page : 1,
          dealList : [],
        })
        that.getDealList();
      }
    });
  },

  /**
   * 获取用户余额
   * @param {} e 
   */
  userWithdraw: function(e) {
    let that = this;
    util.request(api.UserIntegral).then(function(res) {
      if (res.errno === "success") {
        that.setData({
          integral: res.data.integral,
        })
      }
    });
  },

  //获取交易记录
  getDealList(){
    let that = this;
    util.request(api.UserTradingRecord,{
      page: that.data.page,
      limit: that.data.limit
    }).then(function(res) {
      if (res.errno === "success") {
        console.log(res);
        that.setData({
          dealList: that.data.dealList.concat(res.data.list),
          totalPages: res.data.pages
        });
      }
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    let that = this;
    that.getDealList();
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
    that.userWithdraw();
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
      dealList : [],
    })
    this.getDealList();
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
      this.getDealList();
    } else {
      wx.showToast({
        title: '没有更多信息',
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