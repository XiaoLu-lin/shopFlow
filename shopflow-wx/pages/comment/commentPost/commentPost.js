// 上传组件 基于https://github.com/Tencent/weui-wxss/tree/master/src/example/uploader
var app = getApp();
const util = require('../../../utils/util.js');
const api = require('../../../config/api.js');

Page({

  /**
   * 页面的初始数据
   */
  data: {
    goodsId: 0,//待评论商品id
    orderGoods: {}, //订单商品详情
    content: '',
    stars: [0, 1, 2, 3, 4],
    star: 5,
    starText: '十分满意',
    hasPicture: false,
    picUrls: [],
  },


  //保存图片
  getContentImage(e){
    this.setData({
      picUrls: e.detail.picUrls,
    })
  },

  selectRater: function(e) {
    var star = e.currentTarget.dataset.star + 1;
    var starText;
    if (star == 1) {
      starText = '很差';
    } else if (star == 2) {
      starText = '不太满意';
    } else if (star == 3) {
      starText = '满意';
    } else if (star == 4) {
      starText = '比较满意';
    } else {
      starText = '十分满意'
    }
    this.setData({
      star: star,
      starText: starText
    })
  },

  getOrderGoods: function() {
    let that = this;
    util.request(api.OrderGoods, {
      goodsId: that.data.goodsId
    }).then(function(res) {
      console.log(res);
      if (res.errno === "success") {
        that.setData({
          orderGoods: res.data,
        });
      }
    });
  },

  getTopic: function() {
    let that = this;
    util.request(api.TopicDetail, {
      id: that.data.goodsId
    }).then(function(res) {
      console.log(res);
      if (res.errno === "success") {
        that.setData({
          topic: res.data.topic
        });
      }
    });
  },

  onClose: function() {
    wx.navigateBack();
  },

  onPost: function() {
    let that = this;
    if (!this.data.content) {
      util.showErrorModal('请填写评论')
      return false;
    }

    wx.showModal({
      title: '发布评论',
      content: '确定要发布吗？',
      cancelText: '再看看',
      confirmText: '发布',
      success: res => {
        if (res.confirm) {
          // 防多点击，这个不用配合 wx.hideLoading();
          wx.showLoading({
            title: '加载中...',
            mask: true,
            duration: 3000
          });
          util.request(api.OrderComment, {
            goodsId: that.data.goodsId,
            content: that.data.content,
            star: that.data.star,
            hasPicture: that.data.hasPicture,
            picUrls: that.data.picUrls
          }, 'POST').then(function(res) {
            if (res.errno === "success") {
              wx.showToast({
                title: '评论成功',
                complete: function() {
                  wx.navigateBack();
                }
              })
            }
          });
        }
      }
    })
  },

  bindInputValue(event) {
    let value = event.detail.value;
    //判断是否超过140个字符
    if (value && value.length > 140) {
      return false;
    }
    this.setData({
      content: event.detail.value,
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    var that = this;
    that.setData({
      goodsId: options.goodsId
    });
    that.getOrderGoods();
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
