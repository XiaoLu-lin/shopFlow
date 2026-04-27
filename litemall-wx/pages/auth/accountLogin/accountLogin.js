var api = require('../../../config/api.js');
var util = require('../../../utils/util.js');
var user = require('../../../utils/user.js');

var app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    username: '',
    password: '',
    loginErrorCount: 0,
    canIUseGetUserProfile: false,
  },

  //管理员登陆
  accountLogin: function() {
    var that = this;
    let username = that.data.username;
    let password = that.data.password;
    if (password.length < 1 || username.length < 1) {
      util.showErrorModal('请输入用户名和密码');
      return false;
    }
    user.loginByAccount(username,password).then(res => {
      app.globalData.hasLogin = true;
      app.globalData.Administrator = true;
      wx.navigateBack({
        delta: 2
      })
    })
  },

  bindUsernameInput: function(e) {
    this.setData({
      username: e.detail.value
    });
  },

  bindPasswordInput: function(e) {
    this.setData({
      password: e.detail.value
    });
  },

  clearInput: function(e) {
    switch (e.currentTarget.id) {
      case 'clear-username':
        this.setData({
          username: ''
        });
        break;
      case 'clear-password':
        this.setData({
          password: ''
        });
        break;
    }
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    if (wx.getUserProfile) {
      this.setData({
        canIUseGetUserProfile: true
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
