// pages/auth/qrauth/qrauth.js
var api = require('../../../config/api.js');
var util = require('../../../utils/util.js');
var user = require('../../../utils/user.js');
var app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    hasLogin: false,
    Administrator: false,
    userInfo: {},
    adminToken: '',
    authCode: '',
  },

  /**
   * 授权登陆
   */
  getSubject(){
    let that = this;
    if(!that.data.authCode){
      util.showErrorToast("无权限");
      return;
    }

    // 防多点击，这个不用配合 wx.hideLoading();
    wx.showLoading({
      title: '加载中...',
      mask: true,
      duration: 3000
    });

    util.request(api.AuthLoginByQr, {
      authCode: that.data.authCode,
    },'POST').then(function(res) {
      //退出小程序
      wx.exitMiniProgram({success: (res) => {}});
    });
  },

  /**
   * 取消授权
   */
  cancel(){
    wx.exitMiniProgram({success: (res) => {}})
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    let that = this;
    if (options.scene) {
      let authCode = decodeURIComponent(options.scene);
      console.log("authCode=",authCode)
      that.setData({
        authCode: authCode,
      })
    }
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