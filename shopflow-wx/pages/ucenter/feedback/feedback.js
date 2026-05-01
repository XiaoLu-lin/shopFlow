var util = require('../../../utils/util.js');
var check = require('../../../utils/check.js');
var api = require('../../../config/api.js');
var app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    array: ['请选择反馈类型', '商品相关', '功能异常', '优化建议', '其他'],
    index: 0,
    content: '',
    mobile: '',
    picUrls: [],
  },

  //保存图片
  getContentImage(e){
    this.setData({
      picUrls: e.detail.picUrls,
    })
  },

  bindPickerChange: function(e) {
    this.setData({
      index: e.detail.value
    });
  },

  mobileInput: function(e) {
    this.setData({
      mobile: e.detail.value
    });
  },

  contentInput: function(e) {
    this.setData({
      content: e.detail.value,
    });
  },

  clearMobile: function(e) {
    this.setData({
      mobile: ''
    });
  },

  submitFeedback: function(e) {
    if (!app.globalData.hasLogin) {
      wx.navigateTo({
        url: "/pages/auth/login/login"
      });
      return false;
    }

    let that = this;
    if (that.data.index == 0) {
      util.showErrorModal('请选择反馈类型');
      return false;
    }

    if (that.data.content == '') {
      util.showErrorModal('请输入反馈内容');
      return false;
    }

    if (that.data.mobile == '') {
      util.showErrorModal('请输入手机号码');
      return false;
    }

    if(!(/(^1(3\d|4[5-9]|5[0-35-9]|6[567]|7[0-8]|8\d|9[0-35-9])\d{8}$)/.test(that.data.mobile))){
      util.showErrorModal('手机号不正确');
      return false;
    }

    wx.showModal({
      title: '意见反馈',
      content: '确定要提交此意见吗？',
      success: function(res) {
        if (res.confirm) {
          util.request(api.FeedbackAdd, {
            mobile: that.data.mobile,
            feedType: that.data.array[that.data.index],
            content: that.data.content,
            hasPicture: that.data.picUrls.length > 0,
            picUrls: that.data.picUrls,
          }, 'POST').then(function(res) {
            wx.hideLoading();
            if (res.errno === "success") {
              wx.showToast({
                title: '感谢您的反馈！',
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
