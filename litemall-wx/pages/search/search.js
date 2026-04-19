const util = require('../../utils/util.js');
const api = require('../../config/api.js');

var app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    keywrod: '',
    searchStatus: false,
    goodsList: [],
    helpKeyword: [],
    historyKeyword: [],
    categoryFilter: false,
    currentSort: 'name',
    currentSortType: 'default',
    currentSortOrder: 'desc',
    filterCategory: [],
    defaultKeyword: {},
    hotKeyword: [],
    page: 1,
    limit: 10,
    categoryId: 0
  },

   //事件处理函数
   closeSearch: function() {
    wx.navigateBack()
  },

  clearKeyword: function() {
    this.setData({
      keyword: '',
      searchStatus: false
    });
  },

  getSearchKeyword() {
    let that = this;
    util.request(api.SearchIndex).then(function(res) {
      if (res.errno === "success") {
        that.setData({
          historyKeyword: res.data.historyKeywordList,
          defaultKeyword: res.data.defaultKeyword,
          hotKeyword: res.data.hotKeywordList
        });
      }
    });
  },

  inputChange: function(e) {
    this.setData({
      keyword: e.detail.value,
      searchStatus: false
    });
    if (e.detail.value) {
      this.getHelpKeyword();
    }
  },

  getHelpKeyword: function() {
    let that = this;
    util.request(api.SearchHelper, {
      keyword: that.data.keyword
    }).then(function(res) {
      if (res.errno === "success") {
        that.setData({
          helpKeyword: res.data
        });
      }
    });
  },

  inputFocus: function() {
    this.setData({
      searchStatus: false,
      goodsList: []
    });
    if (this.data.keyword) {
      this.getHelpKeyword();
    }
  },

  clearHistory: function() {
    this.setData({
      historyKeyword: []
    });
    util.request(api.SearchClearHistory, {}, 'POST')
    .then(function(res) {
      console.log('清除成功');
    });
  },

  //清除商品信息
  clearGoods(e){
    this.setData({
      page: 1,
      goodsList: [],
    })
  },

  getGoodsList: function() {
    let that = this;
    util.request(api.GoodsList, {
      keyword: that.data.keyword,
      page: that.data.page,
      limit: that.data.limit,
      sort: that.data.currentSort,
      order: that.data.currentSortOrder,
      categoryId: that.data.categoryId
    }).then(function(res) {
      if (res.errno === "success") {
        console.log(res);
        that.setData({
          searchStatus: true,
          categoryFilter: false,
          goodsList: that.data.goodsList.concat(res.data.list),
          totalPages: res.data.pages,
          filterCategory: res.data.filterCategoryList
        });
      }
      //重新获取关键词
      that.getSearchKeyword();
    });
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

  onKeywordTap: function(event) {
    this.getSearchResult(event.target.dataset.keyword);
  },

  getSearchResult(keyword) {
    if (keyword === '') {
      keyword = this.data.defaultKeyword.keyword;
    }
    this.setData({
      keyword: keyword,
      page: 1,
      categoryId: 0,
      goodsList: []
    });
    this.getGoodsList();
  },

  selectCategory: function(event) {
    let currentIndex = event.target.dataset.categoryIndex;
    let filterCategory = this.data.filterCategory;
    let currentCategory = null;
    for (let key in filterCategory) {
      if (key == currentIndex) {
        filterCategory[key].selected = true;
        currentCategory = filterCategory[key];
      } else {
        filterCategory[key].selected = false;
      }
    }
    this.setData({
      filterCategory: filterCategory,
      categoryFilter: false,
      categoryId: currentCategory.id,
      page: 1,
      goodsList: []
    });
    this.getGoodsList();
  },

  onKeywordConfirm(event) {
    let value = event.target.dataset.value;
    this.getSearchResult(value);
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.getSearchKeyword();
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
