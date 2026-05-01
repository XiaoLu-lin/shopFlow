const util = require('../../../utils/util.js');
const api = require('../../../config/api.js');


Page({

  /**
   * 页面的初始数据
   */
  data: {
    orderId: 0,
    orderInfo: {},
    orderGoods: [],
    aftersale: {
      pictures: []
    },
    column: '请选择退款类型',
    columns: ['未收货退款', '不退货退款', '退货退款'],
  },

  //保存图片
  getPictures(e){
    this.setData({
      'aftersale.pictures': e.detail.picUrls,
    })
  },

  getOrderDetail: function () {
    wx.showLoading({
      title: '加载中...',
      duration: 2000
    });
    let that = this;
    util.request(api.OrderDetail, {
      orderId: that.data.orderId
    }).then(function (res) {
      if (res.errno === "success") {
        console.log(res.data);
        that.setData({
          orderInfo: res.data.orderInfo,
          orderGoods: res.data.orderGoods,
          'aftersale.orderId': that.data.orderId,
          'aftersale.amount': res.data.orderInfo.actualPrice - res.data.orderInfo.freightPrice
        });
      }
      wx.hideLoading();
    });
  },
  
  commentInput: function (e) {
    this.setData({
      'aftersale.comment': e.detail.value,
    });
  },

  reasonInput: function (e) {
    this.setData({
      'aftersale.reason': e.detail.value,
    });
  },

  bindMultiPickerChange: function(e) {
    this.setData({
      column: this.data.columns[e.detail.value],
      'aftersale.type': e.detail.value,
    })
  },
  
  submit: function () {
    let that = this;
    if (that.data.aftersale.type == undefined) {
      util.showErrorModal('请选择退款类型');
      return false;
    }

    if (!that.data.aftersale.reason) {
      util.showErrorModal('请输入退款原因');
      return false;
    }
    
    if(!that.data.aftersale.comment){
      util.showErrorModal('请输入退款说明');
      return false;
    }

    if(that.data.aftersale.pictures.length <= 0 && that.data.aftersale.type !== "0"){
      util.showErrorModal('请上传凭证');
      return false;
    }

    // 防多点击，这个不用配合 wx.hideLoading();
    wx.showLoading({
      title: '加载中...',
      mask: true,
      duration: 3000
    });

    util.request(api.AftersaleSubmit, 
      that.data.aftersale,
    'POST').then(function (res) {
      wx.hideLoading();
      if (res.errno === "success") {
        wx.showToast({
          title: '申请售后成功',
          icon: 'success',
          duration: 2000,
          complete: function () {
            wx.switchTab({
              url: '/pages/ucenter/index/index'
            });
          }
        });
      }
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      orderId: options.id
    });
    this.getOrderDetail();
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
