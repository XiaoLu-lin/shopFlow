var app = getApp();
var WxParse = require('../../../lib/wxParse/wxParse.js');
var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');


Page({

  /**
   * 页面的初始数据
   */
  data: {
    id: 0,
    modalName:'',
    topic: {},
    topicList: [],
    commentList: [],
    topicGoods: [],
    collect: false,
    topicLike: false,
  },

  //点赞
  likeSubmit(e){
    let that = this;
    let topic = that.data.topic;
    util.request(api.LikeSubmit, {
      likeType: 1,
      valueId: that.data.id,
    },'POST').then(function(res) {
      if (res.errno === "success") {
        topic.likeCount = !that.data.topicLike ? topic.likeCount + 1 : topic.likeCount - 1;
        if(topic.likeCount <= 0){
          topic.likeCount  = 0;
        }
        that.setData({
          topicLike: !that.data.topicLike,
          topic: topic
        });
      }
    });
  },

  /**
   * 评论弹窗
   */
  showCommentModal: function() {
    let that = this;
    that.commentModal.setData({
      valueId : that.data.id,
      commentType: 1
    })
    that.commentModal.togglePopup();
  },

  // 页面分享
  onShareAppMessage: function() {
    let that = this;
    return {
      title: that.data.topic.title,
      desc: that.data.topic.subtitle,
      path: '/pages/topic/topicDetail/topicDetail?id=' + that.data.id
    }
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

  //添加或是取消收藏
  addCollectOrNot: function() {
    let that = this;
    util.request(api.CollectAddOrDelete, {
      type: 1,
      valueId: this.data.id
    }, "POST").then(function(res) {
      if (res.errno === "success") {
        that.setData({
          collect: !that.data.collect,
        });
      }
    });
  },

  postComment() {
    if (!app.globalData.hasLogin) {
      wx.navigateTo({
        url: "/pages/auth/login/login"
      });
    } else {
      wx.navigateTo({
        url: '/pages/comment/commentPost/commentPost?valueId=' + this.data.id + '&type=1',
      })
    }
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    let that = this;
    that.setData({
      id: options.id
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
    util.request(api.TopicDetail, {
      id: that.data.id
    }).then(function(res) {
      if (res.errno === "success") {
        if (!res.data.topic) {
          wx.navigateBack({delta: 0})
          util.showErrorModal('文章已删除');
          return;
        }
        that.setData({
          topic: res.data.topic,
          topicGoods: res.data.goods,
          topicLike: res.data.topicLike,
          collect: res.data.userHasCollect,
        });
        if (!that.data.topicDetail) {
          WxParse.wxParse('topicDetail', 'html', res.data.topic.content, that);
        }
      } else {
        wx.navigateBack({delta: 0})
      }
    });
    util.request(api.TopicRelated, {
      id: that.data.id
    }).then(function(res) {
      if (res.errno === "success") {
        that.setData({
          topicList: res.data.list
        });
      }
    });
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
