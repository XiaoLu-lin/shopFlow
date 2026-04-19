var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');
var app = getApp()


Page({

  /**
   * 页面的初始数据
   */
  data: {
    userShare: {},
    inviterList: [],
    page: 1,
    limit: 10,
    totalPages: 1
  },

  /**
   * 获取用户分享信息
   * @param {} e 
   */
  userShare: function(e) {
    let that = this;
    util.request(api.UserShare).then(function(res) {
      console.log(res)
      if (res.errno === "success") {
        that.setData({
          userShare: res.data.userShare,
          inviterList: res.data.inviterList,
        })
      }
    });
  },

  showShare: function() {
    this.sharePop.togglePopup();
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    let that = this;
    that.userShare();
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {
    this.sharePop = this.selectComponent("#sharePop");
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
      // this.getRewardList();
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
    let that = this;
    return {
      title: '分享得好礼',
      desc: '唯爱与生活不可辜负',
      path: '/pages/index/index?inviter=' + that.data.userShare.id
    }
  }
})
