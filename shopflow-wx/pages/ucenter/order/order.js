var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');

Page({

  /**
   * 页面的初始数据
   */
  data: {
    showType: 0,
    tableName:['全部','待付款','待发货','待收货','待评价'],
    orderList: [],
    page: 1,
    limit: 10,
    totalPages: 1,
  },

  switchTab: function(e) {
    let showType = e.currentTarget.dataset.index;
    this.setData({
      orderList: [],
      showType: showType,
      page: 1,
      limit: 10,
      totalPages: 1
    });
    this.getOrderList();
  },

  orderToDetail:function(e){
    let order = e.currentTarget.dataset.order;
    util.navigate("/pages/ucenter/orderDetail/orderDetail?orderId="+order.id);
  },

  getOrderList() {
    let that = this;
    // 防多点击，这个不用配合 wx.hideLoading();
    wx.showLoading({
      title: '加载中...',
      mask: true,
      duration: 3000
    });
    util.request(api.OrderList, {
      showType: that.data.showType,
      page: that.data.page,
      limit: that.data.limit
    }).then(function(res) {
      if (res.errno === "success") {
        console.log(res)
        that.setData({
          orderList: that.data.orderList.concat(res.data.list),
          totalPages: res.data.pages
        });
        wx.hideLoading();
      }
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    let that = this
    if (options.tab) {
      this.setData({
        showType: parseInt(options.tab)
      });
    }else{
      try {
        var tab = wx.getStorageSync('tab');
        that.setData({
          showType: tab
        });
      } catch (e) {}
    }
    this.setData({
      page: 1,
      orderList: [],
    })
    this.getOrderList();
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
      orderList: []
    })
    this.getOrderList();
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
      this.getOrderList();
    } else {
      wx.showToast({
        title: '没有更多订单了',
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
