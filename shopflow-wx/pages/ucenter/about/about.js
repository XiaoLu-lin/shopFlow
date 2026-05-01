const util = require('../../../utils/util.js');
const api = require('../../../config/api.js');

//获取应用实例
var app = getApp();


Page({

  /**
   * 页面的初始数据
   */
  data: {
    name: '闲置物品交易市场',
    address: '贵州贵阳',
    latitude: '31.201900',
    longitude: '121.587839',
    phone: '18585675204',
    qq: '777173476'
  },

  getAbout: function () {
    let that = this;
    util.request(api.AboutUrl).then(function (res) {
      if (res.errno === "success") {
        that.setData({
          name: res.data.name,
          address: res.data.address,
          phone: res.data.phone,
          qq: res.data.qq,
          latitude: res.data.latitude,
          longitude: res.data.longitude
        });
      }
    });
  },

  showLocation: function (e) {
    var that = this
    wx.openLocation({
      latitude: parseFloat(that.data.latitude),  //纬度
      longitude: parseFloat(that.data.longitude),//经度
      name: that.data.name,    //地址名称
      address: that.data.address, //地址名称
    })
  },

  callPhone: function (e) {
    var that = this
    wx.makePhoneCall({
      phoneNumber: that.data.phone,
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.getAbout()
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
