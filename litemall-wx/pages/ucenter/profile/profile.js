// pages/ucenter/profile/profile.js
var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');
var user = require('../../../utils/user.js');
var app = getApp();


Page({

  /**
   * 页面的初始数据
   */
  data: {
    hasLogin: false,
    userInfo: {},
    nickName: '',
    avatarUrl: '',
    gender: '',
    mobile: '',
    birthday: '2018-12-25',
  },

  /**
   * 获取微信头像
   */
  onChooseAvatar(e) {
    let that = this;
    wx.uploadFile({
      filePath: e.detail.avatarUrl,
      name: 'file',
      url: api.StorageUpload,//服务器端接收图片的路径
      header: {
        'Content-Type': 'application/json',
        'X-Litemall-TenantId': wx.getStorageSync('tenantId'),
        'X-Litemall-User-Token': wx.getStorageSync('userToken'),
        'X-Litemall-Admin-Token': wx.getStorageSync('adminToken')
      },
      success: function(res) {
        let data = JSON.parse(res.data);
        if (data.errno === "success") {
          that.setData({
            avatarUrl: data.data.url,
          })
        }
      },
      fail: function(e) {
        wx.showModal({
          title: '错误',
          content: '上传失败',
          showCancel: false
        })
      },
    })
  },

  //用户性别
  bindIsGender(e) {
    this.setData({
      gender: e.target.dataset.gender,
    });
  },

  //用户生日
  BirthdayChange(e) {
    this.setData({
      birthday: e.detail.value
    })
  },

  submit(e){
    console.log(e);
    let that = this;
    let mobile = e.detail.value.mobile;
    let nickName = e.detail.value.nickName;
    let avatarUrl = that.data.avatarUrl;
    let gender = that.data.gender;
    let birthday = that.data.birthday;
    util.request(api.AuthProfile,{
      avatarUrl: avatarUrl,
      mobile: mobile,
      nickName: nickName,
      gender: gender,
      birthday: birthday,
    },'POST').then(function(res) {
      if (res.errno === "success") {
        let userInfo = wx.getStorageSync('userInfo');
        userInfo.nickName = nickName;
        userInfo.avatarUrl = avatarUrl;
        userInfo.gender = gender;
        wx.setStorageSync('userInfo', userInfo)
        wx.navigateBack({
          delta: 1
        })
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
    if (app.globalData.hasLogin) {
      let userInfo = wx.getStorageSync('userInfo');
      this.setData({
        userInfo: userInfo,
        hasLogin: true
      });
      util.request(api.UserInfo).then(function(res) {
        console.log(res);
        if (res.errno === "success") {
          that.setData({
            nickName: res.data.nickName,
            avatarUrl: res.data.avatarUrl,
            gender: res.data.gender,
            mobile: res.data.mobile,
            birthday: res.data.birthday,
            genderText: res.data.genderText,
          });
        }
      });
    } else {
      wx.navigateBack({delta: 0})
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
