const util = require('../../../utils/util.js');
const api = require('../../../config/api.js');
var app = getApp();


Page({

  /**
   * 页面的初始数据
   */
  data: {
    bannerInfo: {
      'imgUrl': 'http://yanxuan.nosdn.127.net/8976116db321744084774643a933c5ce.png',
      'name': '新品首发'
    },
    categoryFilter: false,
    filterCategory: [],
    goodsList: [],
    categoryId: 0,
    currentSortType: 'default',
    currentSort: 'add_time',
    currentSortOrder: 'desc',
    page: 1,
    limit: 10,
    totalPages: 1,
  },

  //清除商品信息
  clearGoods(e){
    this.setData({
      page: 1,
      goodsList: [],
    })
  },

  getGoodsList: function() {
    var that = this;
    util.request(api.GoodsList, {
      isNew: true,
      page: that.data.page,
      limit: that.data.limit,
      order: that.data.currentSortOrder,
      sort: that.data.currentSort,
      categoryId: that.data.categoryId
    }).then(function(res) {
      console.log(res);
      if (res.errno === "success") {
        that.setData({
          totalPages: res.data.pages,
          filterCategory: res.data.filterCategoryList,
          goodsList: that.data.goodsList.concat(res.data.list),
        });
      }
    });
  },

  switchCate: function(event) {
    let categoryId = event.currentTarget.dataset.categoryid;
    if (this.data.categoryId == categoryId) {
      return false;
    }
    let that = this;
    let clientX = event.detail.x;
    let currentTarget = event.currentTarget;
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
      categoryId: categoryId,
    });

    this.clearGoods();
    this.getGoodsList();
  },

  openSortFilter: function(event) {
    let that = this;
    let currentId = event.currentTarget.id;
    let filterCategory = that.data.filterCategory;
    
    switch (currentId) {
      case 'categoryFilter':
        this.setData({
          categoryFilter: !that.data.categoryFilter,
          currentSortType: 'category',
          categoryId: filterCategory.length > 0 && !that.data.categoryFilter ? filterCategory[0].id : 0,
        });
        break;
      case 'priceSort':
        let tmpSortOrder = 'asc';
        if (this.data.currentSortOrder == 'asc') {
          tmpSortOrder = 'desc';
        }
        this.setData({
          currentSortType: 'price',
          currentSort: 'retail_price',
          currentSortOrder: tmpSortOrder,
        });
        break;
      default:
        //综合排序
        this.setData({
          currentSortType: 'default',
          currentSort: 'add_time',
          currentSortOrder: 'desc',
          categoryFilter: false,
          categoryId: 0,
        });
    }

    this.clearGoods();
    this.getGoodsList();
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.getGoodsList();
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
