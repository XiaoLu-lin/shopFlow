const util = require('../../../utils/util.js');
const api = require('../../../config/api.js');
var app = getApp()


Page({

  /**
   * 页面的初始数据
   */
  data: {
    title: '',
    topicList: [],
    page: 1,
    limit: 10,
    totalPages: 1
  },

  bindExchange: function (e) {
    this.setData({
      title: e.detail.value
    });
  },

  clearExchange: function () {
    this.setData({
      title: ''
    });
  },

  goSearch: function() {
    let that = this;
    that.setData({
      page: 1,
      topicList : [],
    });
    that.getTopicList();
  },

  getTopicList: function() {
    let that = this;
    wx.showLoading({
      title: '加载中...',
      mask: true,
      duration: 3000
    });
    util.request(api.TopicList, {
      page: that.data.page,
      limit: that.data.limit,
      title: that.data.title,
    }).then(function(res) {
      if (res.errno === "success") {
        console.log(res)
        that.setData({
          topicList: that.data.topicList.concat(res.data.list),
          totalPages: res.data.pages
        });
      }
      wx.hideLoading();
    });
  },


  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      page: 1,
      topicList : [],
    });
    this.getTopicList();
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
      page: 1 ,
      topicList: []
    })
    this.getTopicList();
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
      this.getTopicList();
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
