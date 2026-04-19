const api = require('../../../config/api.js');
const check = require('../../../utils/check.js');
const util = require('../../../utils/util.js');

var app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    username: '',
    password: '',
    confirmPassword: '',
    mobile: '',
    code: ''
  },

  /**
   * 发送验证码
   */
  sendCode: function() {
    let that = this;
    if (that.data.mobile.length === 0) {
      util.showErrorModal('手机号不能为空');
      return false;
    }
    if (!check.isValidPhone(that.data.mobile)) {
      util.showErrorModal('手机号输入不正确');
      return false;
    }
    util.request(api.AuthMobileCaptcha, {
      mobile: that.data.mobile
    },'POST').then(function(res) {
      if (res.errno === "success") {
        util.showOkToast("发送成功");
      }
    });
  },

  /**
   * 注册账号
   * @param {*} wxCode 
   */
  requestRegister: function(wxCode) {
    let that = this;
    util.request(api.AuthRegister, {
      username: that.data.username,
      password: that.data.password,
      mobile: that.data.mobile,
      code: that.data.code,
      wxCode: wxCode
    },'POST').then(function(res) {
      console.log(res)
      if (res.errno === "success") {
        app.globalData.hasLogin = true;
        wx.setStorageSync('userInfo', res.data.data.userInfo);
        wx.setStorage({
          key: "token",
          data: res.data.data.token,
          success: function() {
            wx.switchTab({
              url: '/pages/ucenter/index/index'
            });
          }
        });
      }else{
        util.showErrorModal(res.errmsg)
      }
    });
  },

  startRegister: function() {
    let that = this;
    if (this.data.password.length < 6 || this.data.username.length < 6) {
      util.showErrorModal('用户名和密码不得少于6位');
      return false;
    }

    if (this.data.password !== this.data.confirmPassword) {
      util.showErrorModal('确认密码不一致');
      return false;
    }

    if (this.data.mobile.length === 0 || this.data.code.length === 0) {
      util.showErrorModal('手机号和验证码不能为空');
      return false;
    }

    if (!check.isValidPhone(this.data.mobile)) {
      util.showErrorModal('手机号输入不正确');
      return false;
    }

    wx.login({
      success: function(res) {
        if (!res.code) {
          util.showErrorModal('注册失败');
        }
        that.requestRegister(res.code);
      }
    });
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
  bindConfirmPasswordInput: function(e) {
    this.setData({
      confirmPassword: e.detail.value
    });
  },
  bindMobileInput: function(e) {
    this.setData({
      mobile: e.detail.value
    });
  },
  bindCodeInput: function(e) {
    this.setData({
      code: e.detail.value
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
      case 'clear-confirm-password':
        this.setData({
          confirmPassword: ''
        });
        break;
      case 'clear-mobile':
        this.setData({
          mobile: ''
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