const util = require('../../../utils/util.js');
const api = require('../../../config/api.js');

var app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    valueId: 0,
    commentType: 0,
    brandLike: false,
    hasLogin: false,
    Administrator: false,
    userInfo: {},
    status: false,
    brandId: 0,
    brand: {},
    brandUser : {},
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

  //点赞
  likeSubmit(e){
    let that = this;
    let brand = that.data.brand;
    util.request(api.LikeSubmit, {
      likeType: 2,
      valueId: that.data.brandId,
    },'POST').then(function(res) {
      if (res.errno === "success") {
        brand.likeCount = !that.data.brandLike ? brand.likeCount + 1 : brand.likeCount - 1;
        if(brand.likeCount <= 0){
          brand.likeCount  = 0;
        }
        that.setData({
          brandLike: !that.data.brandLike,
          brand: brand
        });
      }
    });
  },

  getGoodsList: function() {
    var that = this;
    util.request(api.GoodsList, {
      brandId: that.data.brandId,
      page: that.data.page,
      limit: that.data.limit,
      order: that.data.currentSortOrder,
      sort: that.data.currentSort,
      categoryId: that.data.categoryId,
      isAdmin: that.data.status || that.data.Administrator
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
   * 评论弹窗
   */
  showCommentModal: function() {
    let that = this;
    that.commentModal.setData({
      valueId : that.data.brandId,
      commentType: 2
    })
    that.commentModal.togglePopup();
  },

  /**
   * 跳转到发布商品
   */
  issueGoods(){
    let that = this;
    let brand = that.data.brand;
    util.navigate("/pages/issue/issueGoods/issueGoods?brandId="+brand.id);
  },

  /**
   * 跳转订单详情
   */
  getBrandOrder(){
    util.navigate("/pages/brand/brandOrder/brandOrder");
  },

  /**
   * 店铺客服
   */
  openChat: function() {
    let brand = this.data.brand
    if (!brand.userId || !brand.name) {
      util.showErrorToast("该店铺暂无客服")
      return;
    }
    if (app.globalData.hasLogin) {
      util.navigate("/pages/chat/chat?receiveUserId="+brand.userId+"&chatName="+brand.name);
    } else {
      util.navigate("/pages/auth/login/login");
    }
  },

  getBrandDetail: function() {
    let that = this;
    util.request(api.BrandDetail, {
      brandId: that.data.brandId
    }).then(function(res) {
      if (res.errno === "success") {
        that.setData({
          brand: res.data.brand,
          brandLike: res.data.brandLike,
          brandUser : res.data.brandUser,
        });
      }
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    var that = this;
    that.setData({
      brandId: options.id,
      status: options.status === '1',
    });
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {
    this.commentModal = this.selectComponent("#commentModal");
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    let that = this;
    //获取用户的登录信息
    if (app.globalData.hasLogin) {
      let userInfo = wx.getStorageSync('userInfo');
      that.setData({
        userInfo: userInfo,
        hasLogin: true
      });
      //管理员登录
      if (app.globalData.Administrator) {
        that.setData({
          Administrator: true
        });
      }
    }
    that.getBrandDetail();
    that.clearGoods();
    that.getGoodsList();
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
