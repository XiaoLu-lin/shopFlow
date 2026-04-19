const util = require('../../../utils/util.js');
const api = require('../../../config/api.js');

Page({

  /**
   * 页面的初始数据
   */
  data: {
    orderId: 0,
    order: {},
    orderGoods: {},
    aftersale: {},
    statusColumns: ['未申请', '已申请，待审核', '审核通过，待退款', '退款成功', '审核不通过，已拒绝'],
    typeColumns: ['未收货退款', '不退货退款', '退货退款'],
  },

  getAftersaleDetail: function () {
    wx.showLoading({
      title: '加载中...',
      duration: 2000
    });
    let that = this;
    util.request(api.AftersaleDetail, {
      orderId: that.data.orderId
    }).then(function (res) {
      if (res.errno === "success") {
        console.log(res)
        that.setData({
          order: res.data.order,
          orderGoods: res.data.orderGoods,
          aftersale: res.data.aftersale,
        });
      }
      wx.hideLoading();
    });
  },

   // “申请售后”点击效果
   aftersaleOrder: function () {
    util.redirect('/pages/ucenter/aftersale/aftersale?id=' + this.data.orderId );
  },

  previewImage: function(e) {
    wx.previewImage({
      current: e.currentTarget.id, // 当前显示图片的http链接
      urls: [e.currentTarget.dataset.url] // 需要预览的图片http链接列表
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      orderId: options.id
    });
    this.getAftersaleDetail();
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
