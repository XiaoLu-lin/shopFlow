const util = require('../../../utils/util.js');
const api = require('../../../config/api.js');
var app = getApp();


Page({

  /**
   * 页面的初始数据
   */
  data: {
    status: false,
    hideModal: true,
  },

  clearSubmit: function(){
    // 提交订单重置id
    try {
      wx.setStorageSync('couponId', 0);
      wx.setStorageSync('rewardLinkId', 0);
      wx.setStorageSync('grouponLinkId', 0);
      wx.setStorageSync('grouponRulesId', 0);
    } catch (error) {
      console.log(error)
    }
  },

  //打开或关闭拟态窗
  hideModal(e) {
    let that = this;
    that.setData({
      hideModal: !that.data.hideModal
    })
  },

  //查看订单
  payResultConfirm:function(){
    wx.switchTab({
      url: '/pages/ucenter/index/index',
    })
    this.hideModal();
  },

  //继续逛
  payResultCancel:function(){
    wx.switchTab({
      url: '/pages/index/index',
    })
    this.hideModal();
  },

  //去支付
  payResultPayOrder:function(){
    let that = this;
    //关闭拟态窗
    that.hideModal();
    //调用支付
    util.request(api.OrderPrepay, {
      orderIds: wx.getStorageSync('orderIds')
    }, 'POST').then(function(res) {
      if (res.errno === "success") {
        console.log("支付过程开始")
        wx.requestPayment({
          'timeStamp': res.data.timeStamp,
          'nonceStr': res.data.nonceStr,
          'package': res.data.packageValue,
          'signType': res.data.signType,
          'paySign': res.data.paySign,
          'success': function(res) {
            that.setData({
              status: true
            });
            console.log("支付过程成功");
          },
          'fail': function(res) {
            that.setData({
              status: false
            });
            console.log("支付过程失败");
          },
          'complete': function(res) {
            console.log("支付过程结束");
            that.hideModal();
          }
        });
      }else{
        console.log("支付调用失败");
      }
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      status: options.status === '1' ? true : false,
    })
    // 提交订单重置id
    this.clearSubmit();
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
