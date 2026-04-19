const util = require('../../../utils/util.js');
const api = require('../../../config/api.js');


Page({

  /**
   * 页面的初始数据
   */
  data: {
    aftersaleList: [],
    showType: 1,
    page: 1,
    limit: 10,
    totalPages: 1
  },

  switchTab: function (event) {
    let showType = event.currentTarget.dataset.index;
    this.setData({
      aftersaleList: [],
      showType: showType,
      page: 1,
      limit: 10,
      totalPages: 1
    });
    this.getAftersaleList();
  },

  getAftersaleList() {
    let that = this;
    util.request(api.AftersaleList, {
      status: that.data.showType,
      page: that.data.page,
      limit: that.data.limit
    }).then(function (res) {
      if (res.errno === "success") {
        console.log(res.data);
        that.setData({
          aftersaleList: that.data.aftersaleList.concat(res.data.list),
          totalPages: res.data.pages
        });
      }
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.getAftersaleList();
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
    if (this.data.totalPages > this.data.page) {
      this.setData({
        page: this.data.page + 1
      });
      this.getAftersaleList();
    } else {
      wx.showToast({
        title: '没有更多售后了',
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
