const util = require('../../../utils/util.js');
const api = require('../../../config/api.js');
var app = getApp();


Page({

  /**
   * 页面的初始数据
   */
  data: {
    brandName: '',
    brandList: [],
    page: 1,
    limit: 10,
    totalPages: 1
  },

  bindExchange: function (e) {
    this.setData({
      brandName: e.detail.value
    });
  },
  clearExchange: function () {
    this.setData({
      brandName: ''
    });
  },
  goSearch: function() {
    let that = this;
    that.setData({
      page: 1,
      brandList : [],
    });
    that.getBrandList();
  },
  
  getBrandList: function() {
    wx.showLoading({
      title: '加载中...',
      mask: true,
      duration: 3000
    });
    let that = this;
    util.request(api.BrandList, {
      brandName: that.data.brandName,
      page: that.data.page,
      limit: that.data.limit
    }).then(function(res) {
      if (res.errno === "success") {
        that.setData({
          brandList: that.data.brandList.concat(res.data.list),
          totalPages: res.data.pages
        });
      }
      wx.hideLoading();
    });
  },

  toChild(e) {
    let id = e.currentTarget.dataset.id;
    util.navigate("../brandDetail/brandDetail?id="+id)
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      page : 1,
      brandList : [],
    })
    this.getBrandList();
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
      page : 1,
      brandList : [],
    })
    this.getBrandList();
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
      this.getBrandList();
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