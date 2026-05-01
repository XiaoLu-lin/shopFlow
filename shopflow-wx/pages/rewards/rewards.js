const util = require('../../utils/util.js');
const api = require('../../config/api.js');
var app = getApp()

Page({
 /**
   * 页面的初始数据
   */
  data: {
    balance: 0,
    earnings: 0,
    rewardList: [],
    page: 1,
    limit: 10,
    totalPages: 1
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      page: 1 ,
      rewardList: []
    })
    this.getRewardList();
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
      rewardList: []
    })
    this.getRewardList();
    wx.hideNavigationBarLoading() //完成停止加载
    wx.stopPullDownRefresh() //停止下拉刷新
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  },

  /**
   * 添加赏金分享
   * @param {} e 
   */
  rewardShare: function(e) {
    let rewardTaskId = e.currentTarget.dataset.id
    util.request(api.RewardCreate, {
      rewardTaskId: rewardTaskId
    },"POST").then(function(res) {
      if (res.errno === "success") {
        util.navigate("/pages/goodsDetail/goodsDetail?rewardId="+res.data.id);
      }
    });
  },

  getRewardList: function() {
    let that = this;
    wx.showLoading({
      title: '加载中...',
      mask: true,
      duration: 3000
    });
    util.request(api.RewardList, {
      page: that.data.page,
      limit: that.data.limit
    }).then(function(res) {
      if (res.errno === "success") {
        console.log(res)
        that.setData({
          rewardList: that.data.rewardList.concat(res.data.list),
          totalPages: res.data.pages,
          balance: res.data.balance,
          earnings: res.data.earnings,
        });
      }
      wx.hideLoading();
    });
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    if (this.data.totalPages > this.data.page) {
      this.setData({
        page: this.data.page + 1
      });
      this.getRewardList();
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