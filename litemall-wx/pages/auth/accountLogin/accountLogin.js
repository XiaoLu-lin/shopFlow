var api = require('../../../config/api.js');
var util = require('../../../utils/util.js');
var user = require('../../../utils/user.js');

var app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    code: '',
    username: '',
    password: '',
    loginErrorCount: 0,
    canIUseGetUserProfile: false,
  },

  //获取登陆授权
  bindPhoneNumber: function(e) {
    let that = this;
    // 拒绝授权
    if (e.detail.errMsg !== "getPhoneNumber:ok") {
      return;
    }
    user.loginByWeixin(e).then(res => {
      app.globalData.hasLogin = true;
      that.accountLogin();
    })
  },


  //管理员登陆
  accountLogin: function() {
    var that = this;
    let code = that.data.code;
    let username = that.data.username;
    let password = that.data.password;
    if (password.length < 1 || username.length < 1) {
      util.showErrorModal('请输入用户名和密码');
      return false;
    }
    if (code.length < 1) {
      util.showErrorModal('请输入验证码');
      return false;
    }
    user.loginByAccount(username,password,code).then(res => {
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

  bindCodeInput: function(e) {
    this.setData({
      code: e.detail.value
    });
  },

  sendCode: function() {
    let that = this;
    if (this.data.password.length < 1 || this.data.username.length < 1) {
      util.showErrorModal('请输入用户名和密码');
      return false;
    }
    util.request(api.AuthMailCaptcha, {
      username: that.data.username
    },'POST').then(function(res) {
      if (res.errno === "success") {
        util.showErrorModal(res.data);
      }
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
      case 'clear-code':
        this.setData({
          code: ''
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