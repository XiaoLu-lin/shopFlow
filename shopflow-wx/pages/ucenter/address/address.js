var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');
var app = getApp();


Page({

  /**
   * 页面的初始数据
   */
  data: {
    addressList: [],
  },

  getAddressList() {
    let that = this;
    util.request(api.AddressList).then(function(res) {
      if (res.errno === "success") {
        that.setData({
          addressList: res.data.list
        });
      }
    });
  },

  addressAddOrUpdate(event) {
    //返回之前，先取出上一页对象，并设置addressId
    var pages = getCurrentPages();
    var prevPage = pages[pages.length - 2];
    let addressId = event.currentTarget.dataset.addressId;
    if (prevPage.route == "pages/shopping/checkout/checkout") {
      try {
        wx.setStorageSync('addressId', addressId);
      } catch (e) {
        console.log(e);
      }
      if (addressId && addressId != 0) {
        wx.navigateBack();
      } else {
        wx.navigateTo({
          url: '/pages/ucenter/addressAdd/addressAdd?id=' + addressId
        })
      }
    } else {
      wx.navigateTo({
        url: '/pages/ucenter/addressAdd/addressAdd?id=' + addressId
      })
    }
  },
  
  deleteAddress(event) {
    console.log(event.target);
    let that = this;
    wx.showModal({
      title: '地址删除',
      content: '确定要删除地址？',
      success: function(res) {
        if (res.confirm) {
          let addressId = event.target.dataset.addressId;
          util.request(api.AddressDelete, {
            id: addressId
          }, 'POST').then(function(res) {
            if (res.errno === "success") {
              that.getAddressList();
              wx.removeStorage({
                key: 'addressId',
                success: function(res) {},
              })
            }
          });
        }
      }
    });
    return false;
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
    this.getAddressList();
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
    this.getAddressList();
    wx.hideNavigationBarLoading() //完成停止加载
    wx.stopPullDownRefresh() //停止下拉刷新
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
