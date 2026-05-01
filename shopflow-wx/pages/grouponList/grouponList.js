// pages/groupon/grouponList/grouponList.js
const util = require('../../utils/util.js');
const api = require('../../config/api.js');
var app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    grouponList: [],
    page: 1,
    limit: 10,
    totalPages: 1,
  },

  getGrouponList: function() {
    let that = this;
    // 页面渲染完成
    wx.showToast({
      title: '加载中...',
      icon: 'loading',
      duration: 2000
    });
    util.request(api.GroupOnList, {
      page: that.data.page,
      limit: that.data.limit
    }).then(function(res) {
      console.log(res)
      if (res.errno === "success") {
        that.setData({
          grouponList: that.data.grouponList.concat(res.data.list),
          totalPages: res.data.pages
        });
      }
      wx.hideToast();
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function() {
    this.setData({
      page: 1 ,
      grouponList: []
    })
    this.getGrouponList();
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function() {
    wx.showNavigationBarLoading() //在标题栏中显示加载
    this.setData({
      page: 1 ,
      grouponList: []
    })
    this.getGrouponList();
    wx.hideNavigationBarLoading() //完成停止加载
    wx.stopPullDownRefresh() //停止下拉刷新
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function() {

  },
  
  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    if (this.data.totalPages > this.data.page) {
      this.setData({
        page: this.data.page + 1
      });
      this.getGrouponList();
    } else {
      wx.showToast({
        title: '没有更多信息了',
        icon: 'none',
        duration: 2000
      });
      return false;
    }
  },


})