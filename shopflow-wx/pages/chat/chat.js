const api = require('../../config/api');
const util = require('../../utils/util.js');
const webSocket = require('../../utils/webSocket.js');

//获取应用实例
var app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    picUrls: [],
    textarea: false,
    addImage: false,
    scrollTop: 0, // 内容底部与顶部的距离
    hasLogin: false,
    Administrator: false,
    userInfo: {},
    chatName: '聊天', //聊天室名称
    receiveUserId: 0, //接收消息的用户
    InputBottom: 0,
    messageList: [],
    content: '',
    page: 1,
    limit: 10,
    totalPages: 1,
  },

  /**
   * 用户点击输入框，用于限制系统自动获取输入框焦点
   * @param {*} e 
   */
  textarea(e){
    let that = this;
    that.setData({
      textarea: true
    })
  },

  /**
   * 获取选择的图片路径
   * @param {*} e 
   */
  getMessageImage(e){
    let that = this;
    that.setData({
      picUrls: e.detail.picUrls,
    })
  },

  /**
   * 图片查看器
   * @param {*} e 
   */
  previewImage: function(e) {
    wx.previewImage({
      current: e.currentTarget.id, // 当前显示图片的http链接
      urls: [e.target.dataset.url] // 需要预览的图片http链接列表
    })
  },

  /**
   * 打开图片选择，设置上推高度，上拉聊天记录
   */
  roundadd(){
    let that = this;
    //打开图片选择组件，要先打开才能获取高度
    that.setData({
      addImage: !that.data.addImage,
    })
    //获取图片选择组件高度
    wx.createSelectorQuery().select('#add-image').boundingClientRect(function(rect){
      that.setData({
        InputBottom: rect ? rect.height : 0 // 节点的高度
      })
    }).exec();

    //自动上拉
    that.toViewBottomFun();
  },

  /**
   * 监听键盘聚焦，打开键盘则关闭图片选择
   * @param {*} e 
   */
  InputFocus(e) {
    let that = this;
    if(that.data.textarea){
      that.setData({
        addImage: false,
        InputBottom: e.detail.height
      })
    }
    //自动上拉
    that.toViewBottomFun();
  },

  /**
   * 键盘失去焦点
   * @param {*} e 
   */
  InputBlur(e) {
    let that = this;
    if(that.data.textarea){
      that.setData({
        textarea: false,
        InputBottom: 0
      })
    }
  },

  /**
   * 发送消息
   * @param {*} e 
   */
  sendMessage(e){
    let that = this;
 
    //发送消息
    webSocket.send({
      picUrls: that.data.picUrls,
      content : e.detail.value.content,
      avatarUrl: that.data.userInfo.avatarUrl,
      nickName: that.data.userInfo.nickName,
      receiveUserId : that.data.receiveUserId,
      tenantId: wx.getStorageSync('tenantId'),
    });

    //清空输入框
    that.setData({
      content: '',
      picUrls: [],
    })
    //自动上拉
    that.toViewBottomFun();
  },

  /**
   * 设置屏幕自动滚动到最后一条消息处
   */
  toViewBottomFun: function() {
    // 设置屏幕自动滚动到最后一条消息处
    let that = this;
    wx.createSelectorQuery().select('#viewCommunicationBody').boundingClientRect(function(rect) {
      wx.pageScrollTo({
        scrollTop: rect.height,
        duration: 500 // 滑动速度
      })
      that.setData({
        scrollTop: rect.height - that.data.scrollTop
      });
    }).exec();
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    let that = this;
    //获取接收消息的用户名与userId
    if (options.receiveUserId && options.chatName) {
      this.setData({
        chatName: options.chatName,
        receiveUserId: options.receiveUserId,
      })
    }else{
      util.showErrorModal("未找到用户")
      wx.navigateBack();
      return;
    }
    //重新连接socket
    that.updataWebSocket();
    //获取聊天记录
    that.getHistoryMessageList();
  },

  /**
   * websocket重新连接
   */
  updataWebSocket(){
    let that = this;
    //清空消息
    that.setData({messageList:[]})
    //重新连接
    webSocket.connect(function (res) {
      //转成json对象
      let messageList = JSON.parse(res.data);
      that.setData({
        messageList : that.data.messageList.concat(messageList)
      })
      //自动上拉
      that.toViewBottomFun();
    })
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
    webSocket.close();
  },

  getHistoryMessageList(){
    let that = this;
    util.request(api.MessageHistory, {
      receiveUserId: that.data.receiveUserId,
      page: that.data.page,
      limit: that.data.limit,
    }).then(function(res) {
      if (res.errno === "success") {
        that.setData({
          messageList : res.data.list.concat(that.data.messageList),
          totalPages: res.data.pages
        })
        //自动上拉第一页数据自动上拉
        if(res.data.page === 1){
          that.toViewBottomFun();
        }
      }
    });
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {
    wx.showNavigationBarLoading() //在标题栏中显示加载
    wx.hideNavigationBarLoading() //完成停止加载
    wx.stopPullDownRefresh() //停止下拉刷新
    if (this.data.totalPages > this.data.page) {
      this.setData({
        page: this.data.page + 1
      });
      this.getHistoryMessageList();
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