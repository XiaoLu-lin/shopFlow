const util = require('../../utils/util.js');
const api = require('../../config/api.js');

Page({

  /**
   * 页面的初始数据
   */
  data: {
    navList: [],
    goodsList: [],
    id: 0,
    currentCategory: {},
    scrollLeft: 0,
    scrollTop: 0,
    scrollHeight: 0,
    page: 1,
    limit: 10,
  },

  getCategoryInfo: function() {
    let that = this;
    util.request(api.GoodsCategory, {
      id: this.data.id
    }).then(function(res) {
      if (res.errno === "success") {
        console.log(res);
        that.setData({
          navList: res.data.brotherCategory,
          currentCategory: res.data.currentCategory
        });
        wx.setNavigationBarTitle({
          title: res.data.parentCategory.name
        })
        //nav位置
        let currentIndex = 0;
        let navListCount = that.data.navList.length;
        for (let i = 0; i < navListCount; i++) {
          currentIndex += 1;
          if (that.data.navList[i].id == that.data.id) {
            break;
          }
        }
        if (currentIndex > navListCount / 2 && navListCount > 5) {
          that.setData({
            scrollLeft: currentIndex * 60
          });
        }
        that.clearGoods();
        that.getGoodsList();
      }
    });
  },

  getGoodsList: function() {
    wx.showLoading({
      title: '加载中...',
      mask: true,
      duration: 3000
    });
    setTimeout(function() {
      wx.hideLoading()
    }, 2000);

    var that = this;
    util.request(api.GoodsList, {
      categoryId: that.data.currentCategory.id,
      page: that.data.page,
      limit: that.data.limit
    }).then(function(res) {
      that.setData({
        totalPages: res.data.pages,
        goodsList: that.data.goodsList.concat(res.data.list),
      });
      wx.hideLoading();
    });
  },

  //清除商品信息
  clearGoods(e){
    this.setData({
      page: 1,
      goodsList: [],
    })
  },

  switchCate: function(event) {
    if (this.data.id == event.currentTarget.dataset.id) {
      return false;
    }
    var that = this;
    var clientX = event.detail.x;
    var currentTarget = event.currentTarget;
    if (clientX < 60) {
      that.setData({
        scrollLeft: currentTarget.offsetLeft - 60
      });
    } else if (clientX > 330) {
      that.setData({
        scrollLeft: currentTarget.offsetLeft
      });
    }
    this.setData({
      id: event.currentTarget.dataset.id,
    });
    this.getCategoryInfo();
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    var that = this;
    if (options.id) {
      that.setData({
        id: options.id
      });
    }
    wx.getSystemInfo({
      success: function(res) {
        that.setData({
          scrollHeight: res.windowHeight
        });
      }
    });
    this.getCategoryInfo();
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
    this.clearGoods();
    this.getGoodsList();
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
      this.getGoodsList();
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