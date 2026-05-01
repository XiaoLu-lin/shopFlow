// pages/announce/announce.js
const util = require('../../../utils/util');
const check = require('../../../utils/check.js');
const api = require('../../../config/api.js');

var app = getApp();


Page({

  /**
   * 页面的初始数据
   */
  data: {
    hasLogin: false,
    Administrator: false,
    userInfo: {},
    content: '',
    picUrls: [],
  },


  //保存图片
  getContentImage(e){
    this.setData({
      picUrls: e.detail.picUrls,
    })
  },
  
  contentInput: function(e) {
    this.setData({
      content: e.detail.value,
    });
  },
  
  submitIssue: function(e) {
    if (!app.globalData.hasLogin) {
      wx.navigateTo({
        url: "/pages/auth/login/login"
      });
    }

    let that = this;
    
    if (that.data.content == '') {
      util.showErrorModal('请输入发布内容');
      return false;
    }

    wx.showModal({
      title: '发布日常',
      content: '确定要发布吗？',
      cancelText: '再看看',
      confirmText: '发布',
      success: res => {
        if (res.confirm) {
          util.request(api.DynamicSubmit, {
            content: that.data.content,
            picUrls: that.data.picUrls,
            isAdmin: that.data.Administrator,
          }, 'POST').then(function(res) {
            if (res.errno === "success") {
              wx.showToast({
                title: '发布成功！',
                icon: 'success',
                duration: 2000,
                complete: function() {
                  wx.navigateBack({
                    delta: 1
                  })
                }
              });
            } 
          });
        }
      }
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    //获取用户的登录信息
    if (app.globalData.hasLogin) {
      let userInfo = wx.getStorageSync('userInfo');
      this.setData({
        userInfo: userInfo,
        hasLogin: true
      });
      //管理员登录
      if (app.globalData.Administrator) {
        this.setData({
          Administrator: true
        });
      }
    }
    
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})