// pages/messageList/myssageList.js
var app = getApp();
const util = require('../../utils/util.js');
const api = require('../../config/api.js');

Page({

  /**
   * 页面的初始数据
   */
  data: {
    hasLogin: false,
    Administrator: false,
    userInfo: {},
    messageList: [],
    modalName: ''
  },

  goLogin() {
    wx.navigateTo({
      url: "/pages/auth/login/login"
    });
  },

  messageSubscribe(){
    if(app.globalData.hasLogin){
      util.requestSubscribe(api.newMessageTmplId);
    }
  },

  deleteMessage(e){
    let that = this;
    let index = e.target.dataset.index;
    let message = that.data.messageList[index];
    wx.showModal({
      title: '确定要删除吗？',
      content: '删除后会清除你与对方的全部聊天记录！',
      cancelText: '再看看',
      confirmText: '删除',
      success: res => {
        if (res.confirm) {
          util.request(api.MessageDelete , {
            sendUserId : message.sendUserId,
          },'POST').then(function(res) {
            if (res.errno === "success") {
              that.getMessageList();
            }
          });
        }
      }
    })
  },

  /**
   * 打开聊天界面
   */
  openChat: function(e) {
    let that = this;
    let index = parseInt(e.currentTarget.dataset.index);
    let message = that.data.messageList[index];
    if (this.data.hasLogin) {
      util.navigate("/pages/chat/chat?receiveUserId="+message.sendUserId+"&chatName="+message.nickName);
    } else {
      util.navigate("/pages/auth/login/login");
    }
  },

  showModal(e) {
    this.setData({
      modalName: e.currentTarget.dataset.target
    })
  },
  hideModal(e) {
    this.setData({
      modalName: null
    })
  },

  // ListTouch触摸开始
  ListTouchStart(e) {
    this.setData({
      ListTouchStart: e.touches[0].pageX
    })
  },

  // ListTouch计算方向
  ListTouchMove(e) {
    this.setData({
      ListTouchDirection: e.touches[0].pageX - this.data.ListTouchStart > 0 ? 'right' : 'left'
    })
  },

  // ListTouch计算滚动
  ListTouchEnd(e) {
    if (this.data.ListTouchDirection == 'left'){
      this.setData({
        modalName: e.currentTarget.dataset.target
      })
    } else {
      this.setData({
        modalName: null
      })
    }
    this.setData({
      ListTouchDirection: null
    })
  },

  //获取信息列表
  getMessageList: function() {
    let that = this;
    if(!app.globalData.hasLogin){
      return;
    }
    wx.showLoading({
      title: '加载中...',
      mask: true,
      duration: 3000
    });
    util.request(api.MessageList).then(function(res) {
      wx.hideLoading();
      if (res.errno === "success") {
        that.setData({
          messageList: res.data.list
        })
      }
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    
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

    //自定义底部导航栏高亮显示不加会导致高亮随机跳
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({
        selected: 2
      })
    }

    this.getMessageList();
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
    this.getMessageList();
    wx.hideNavigationBarLoading() //完成停止加载
    wx.stopPullDownRefresh() //停止下拉刷新
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