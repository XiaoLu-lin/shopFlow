var app = getApp();
const util = require('../../../utils/util.js');
const api = require('../../../config/api.js');

Page({

  /**
   * 页面的初始数据
   */
  data: {
    comments: [],
    allCommentList: [],
    picCommentList: [],
    goodsId: 0,
    showType: 0,
    allCount: 0,
    hasPicCount: 0,
    allPage: 1,
    picPage: 1,
    limit: 20
  },

  getCommentCount: function() {
    let that = this;
    util.request(api.GoodsCommentCount, {
      goodsId: that.data.goodsId,
    }).then(function(res) {
      if (res.errno === "success") {
        that.setData({
          allCount: res.data.allCount,
          hasPicCount: res.data.hasPicCount
        });
      }
    });
  },
  getCommentList: function() {
    let that = this;
    util.request(api.GoodsCommentList, {
      goodsId: that.data.goodsId,
      limit: that.data.limit,
      page: (that.data.showType == 0 ? that.data.allPage : that.data.picPage),
      hasPicture: that.data.showType == 1
    }).then(function(res) {
      if (res.errno === "success") {
        if (that.data.showType == 0) {
          that.setData({
            allCommentList: that.data.allCommentList.concat(res.data.list),
            allPage: res.data.page,
            comments: that.data.allCommentList.concat(res.data.list)
          });
        } else {
          that.setData({
            picCommentList: that.data.picCommentList.concat(res.data.list),
            picPage: res.data.page,
            comments: that.data.picCommentList.concat(res.data.list)
          });
        }
      }
    });
  },

  /**
   * 图片查看
   * @param {*} e 
   */
  previewImage: function(e) {
    wx.previewImage({
      current: e.currentTarget.id, // 当前显示图片的http链接
      urls: [e.currentTarget.dataset.url] // 需要预览的图片http链接列表
    })
  },

  switchTab: function() {
    let that = this;
    if (that.data.showType == 0) {
      that.setData({
        allCommentList: [],
        allPage: 1,
        comments: [],
        showType: 1
      });
    } else {
      that.setData({
        picCommentList: [],
        picPage: 1,
        comments: [],
        showType: 0
      });
    }
    this.getCommentList();
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      goodsId: options.goodsId
    });
    this.getCommentCount();
    this.getCommentList();
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
    this.getCommentCount();
    this.getCommentList();
    wx.hideNavigationBarLoading() //完成停止加载
    wx.stopPullDownRefresh() //停止下拉刷新
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {
    if (this.data.showType == 0) {
      if (this.data.allCount / this.data.limit < this.data.allPage) {
        return false;
      }
      this.setData({
        'allPage': this.data.allPage + 1
      });
    } else {
      if (this.data.hasPicCount / this.data.limit < this.data.picPage) {
        return false;
      }
      this.setData({
        'picPage': this.data.picPage + 1
      });
    }
    this.getCommentList();
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  }
})