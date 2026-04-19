const util = require('../../utils/util.js');
const api = require('../../config/api.js');
const user = require('../../utils/user.js');
//获取应用实例
var app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    valueId: 0,
    commentType: 0,
    content: '',
    hasLogin: false,
    Administrator: false,
    userInfo: {},
    modalName:'',
    timeVo: {},
    dynamicList:[],//发布列表
    index: 0,//循环下标
    page: 1,
    limit: 10,
    totalPages: 1,
  },

  //点赞
  likeSubmit(e){
    let that = this;
    let index = e.target.dataset.index;
    let valueId = e.target.dataset.valueid;
    if(valueId){
      util.request(api.LikeSubmit, {
        likeType: 0,
        valueId: valueId,
      },'POST').then(function(res) {
        if (res.errno === "success") {
          let dynamicVo = that.data.dynamicList[index];
          dynamicVo.likeCount = !dynamicVo.dynamicLike ? dynamicVo.likeCount + 1 : dynamicVo.likeCount - 1;
          dynamicVo.dynamicLike = !dynamicVo.dynamicLike;
          if(dynamicVo.likeCount <= 0){
            dynamicVo.likeCount  = 0;
          }
          that.setData({
            dynamicList: that.data.dynamicList
          });
        }
      });
    }
  },

  /**
   * 用户聊天
   */
  openChat: function(e) {
    let timevo = e.target.dataset.timevo;
    if (this.data.hasLogin) {
      util.navigate("/pages/chat/chat?receiveUserId="+timevo.userId+"&chatName="+timevo.nickName);
    } else {
      util.navigate("/pages/auth/login/login");
    }
  },

  bindExchange: function (e) {
    this.setData({
      content: e.detail.value
    });
  },
  
  clearExchange: function () {
    this.setData({
      content: ''
    });
  },

  clearTimeLine: function(){
    let that = this;
    that.setData({
      page: 1,
      dynamicList : [],
    });
  },

  goSearch: function() {
    let that = this;
    that.clearTimeLine();
    that.getTimeLineVoList();
  },

  //打开弹窗
  showModal(e) {
    this.setData({
      timeVo: e.currentTarget.dataset.timevo,
      index: e.currentTarget.dataset.index,
      modalName: 'menuModal',
    })
  },

  /**
   * 评论弹窗
   */
  showCommentModal: function(e) {
    let that = this;
    let valueId = e.target.dataset.valueid;
    if(valueId){
      that.commentModal.setData({
        valueId : valueId,
        commentType: 0
      })
      that.commentModal.togglePopup();
    }
  },

  //关闭弹窗
  hideModal(e) {
    this.setData({
      modalName: null
    })
  },

  previewImage: function(e) {
    wx.previewImage({
      current: e.currentTarget.id, // 当前显示图片的http链接
      urls: [e.currentTarget.dataset.url] // 需要预览的图片http链接列表
    })
  },

  //编辑
  editTimeLine: function (e) {
    let that = this;
    util.showErrorModal('无权限');
    that.hideModal();
  },

  //删除
  deleteTimeLine: function (e) {
    let that = this;
    let timeVo = that.data.timeVo;
    util.request(api.TimeLineDelete, {
      timeVoId: timeVo.id,
    },'POST').then(function(res) {
      if (res.errno === "success") {
        wx.showToast({
          title: '删除成功',
          icon: 'success',
          duration: 2000//持续的时间
        })
        that.data.dynamicList.splice(that.data.index, 1);
        that.setData({
          dynamicList: that.data.dynamicList
        }); 
      }
    });
    that.hideModal();
  },

  //获取发布信息
  getTimeLineVoList(){
    let that = this;
    wx.showLoading({
      title: '加载中...',
      mask: true,
      duration: 3000
    });
    util.request(api.DynamicList, {
      page: that.data.page,
      limit: that.data.limit,
      content: that.data.content,
    }).then(function(res) {
      console.log(res);
      if (res.errno === "success") {
        that.setData({
          dynamicList: that.data.dynamicList.concat(res.data.list),
          totalPages: res.data.pages
        });
      }
      wx.hideLoading()
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this;
    that.clearTimeLine();
    that.getTimeLineVoList();
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
    this.commentModal = this.selectComponent("#commentModal");
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    let that = this;
    //获取用户的登录信息
    if (app.globalData.hasLogin) {
      let userInfo = wx.getStorageSync('userInfo');
      this.setData({
        userInfo: userInfo,
        hasLogin: true
      });
      //管理员登录
      if (app.globalData.Administrator) {
        this.setData({
          Administrator: true
        });
      }
    }
    //自定义底部导航栏高亮显示不加会导致高亮随机跳
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({
        selected: 1
      })
    }

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {
    wx.showNavigationBarLoading() //在标题栏中显示加载
    let that = this;
    that.clearTimeLine();
    that.getTimeLineVoList();
    wx.hideNavigationBarLoading() //完成停止加载
    wx.stopPullDownRefresh() //停止下拉刷新
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    let that = this;
    //获取发布信息
    if (that.data.totalPages > that.data.page) {
      that.setData({
        page: that.data.page + 1
      });
      that.getTimeLineVoList();
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
  onShareAppMessage: function () {

  },
})