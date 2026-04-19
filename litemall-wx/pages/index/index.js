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
    StatusBar: app.globalData.StatusBar,
    CustomBar: app.globalData.CustomBar,
    Custom: app.globalData.Custom,
    hasLogin: false,
    Administrator: false,
    userInfo: {},
    banner : [],
    newGoods: [],
    hotGoods: [],
    allGoods: [],
    coupons: [],
    channel: [],
    rewardTaskList: [],
    grouponRuleList: [],
    elements: [
      { title: '店铺', name: 'brand', color: 'red', icon: 'shop',url:'/pages/brand/brandList/brandList'},
      { title: '专题', name: 'topics', color: 'orange', icon: 'selection',url:'/pages/topic/topicList/topicList' },
      { title: '团购', name: 'groupon', color: 'green', icon: 'friendadd',url:'/pages/grouponList/grouponList'},
      { title: '赏金', name: 'reward', color: 'olive', icon: 'sponsor' ,url:'/pages/rewards/rewards'},
    ],
    page: 1,
    limit: 20,
    totalPages: 1,
  },

  switchCate: function(event) {
    let id = event.currentTarget.dataset.id;
    util.navigate("/pages/category/category?id="+id)
  },

  getGrouponGoods(res){
    let id = res.currentTarget.dataset.id;
    util.navigate("/pages/goodsDetail/goodsDetail?goodId="+id)
  },

  /**
   * 添加赏金分享
   * @param {} e 
   */
  rewardShare: function(e) {
    let rewardTaskId = e.currentTarget.dataset.id
    util.request(api.RewardCreate, {
      rewardTaskId: rewardTaskId
    },"POST").then(function(res) {
      if (res.errno === "success") {
        util.navigate("/pages/goodsDetail/goodsDetail?rewardId="+res.data.id);
      }
    });
  },

  getIndexData: function() {
    wx.showLoading({
      title: '加载中...',
      mask: true,
      duration: 3000
    });
    let that = this;
    util.request(api.IndexUrl,{
      appid : util.getAppid()
    }).then(function(res) {
      wx.hideLoading();
      if (res.errno === "success") {
        console.log(res)
        that.clearIndexData();
        wx.setStorageSync('tenantId', res.data.tenantId);
        that.setData({
          banner: res.data.banner,
          newGoods: res.data.newGoodsList,
          hotGoods: res.data.hotGoodsList,
          rewardTaskList: res.data.rewardTaskList,
          grouponRuleList: res.data.grouponRuleList,
          allGoods: res.data.allGoodsList.list,
          totalPages: res.data.allGoodsList.pages,
          coupons : res.data.couponList,
          channel: res.data.channel,
        });
      }
    });
  },

  clearIndexData: function(){
    let that = this;
    that.setData({
      page : 1,
    });
  },

  /**分页获取商品 */
  getGoodsAll: function() {
    wx.showLoading({
      title: '加载中...',
      mask: true,
      duration: 3000
    });
    let that = this;
    util.request(api.GoodsList, {
      page: that.data.page,
      limit: that.data.limit
    }).then(function(res) {
      wx.hideLoading();
      if (res.errno === "success") {
        that.setData({
          allGoods: that.data.allGoods.concat(res.data.list),
          totalPages: res.data.pages,
        });
      }
    });
  },

  //如果有可领优惠券则打开弹窗
  showCoupon(e){
    this.redPacket.openTheCouponPopUp();
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    let that = this;
    //轮播商品获取宽高度
    wx.getSystemInfo({
      success: function(res) {
        that.setData({
          windowWidth: res.windowWidth,
          windowHeight: res.windowHeight
        });
      }
    });

    let sceneId , sceneType;
    //页面初始化海报分享扫码
    if (options.scene) {
      let scene = decodeURIComponent(options.scene);
      let info_arr = scene.split(',');
      sceneType = info_arr[0];
      sceneId = info_arr[1];
    }

    //存储分享者用户信息
    if(sceneType == "inviter"){
      wx.setStorageSync('inviter', sceneId);
    }

    if(options.inviter){
      wx.setStorageSync('inviter', options.inviter);
    }

    console.log("inviter" , wx.getStorageSync('inviter'))

    // 页面初始化朋友圈分享
    if (options.goodsId) {
      sceneId = options.goodsId;
      sceneType = "goodsId";
    }
    if (options.grouponId) {
      sceneId = options.grouponId;
      sceneType = "grouponId";
    }
    if (options.rewardId) {
      sceneId = options.rewardId;
      sceneType = "rewardId";
    }

    //判断首页初始化参数是否正常
    if (sceneType == 'goodsId' || sceneType == 'grouponId' || sceneType == 'rewardId') {
      util.request(api.NavigateUrl,{
        sceneId : sceneId,
        sceneType : sceneType,
      },'POST').then(function(res) {
        if (res.errno === "success") {
          if (sceneType == 'goodsId') {
            util.navigate("/pages/goodsDetail/goodsDetail?goodId="+sceneId);
          }
          if (sceneType == 'grouponId') {
            util.navigate("/pages/goodsDetail/goodsDetail?grouponId="+sceneId);
          }
          if (sceneType == 'rewardId') {
            util.navigate("/pages/goodsDetail/goodsDetail?rewardId="+sceneId);
          }
        }
      });
    }
    //加载优惠券组件
    this.redPacket = this.selectComponent("#redPacket");
    
    //加载首页数据
    this.getIndexData();
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
        selected: 0
      })
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

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {
    wx.showNavigationBarLoading() //在标题栏中显示加载
    this.getIndexData();
    //在当前页面显示导航条加载动画
    wx.hideNavigationBarLoading() //完成停止加载
    wx.stopPullDownRefresh() //停止下拉刷新
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {
    let that = this;
    if (that.data.totalPages > that.data.page) {
      that.setData({
        page: that.data.page + 1
      });
      that.getGoodsAll();
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
    return {
      title: 'litemall-plus-wx',
      desc: '唯爱与生活不可辜负',
      path: '/pages/index/index'
    }
  }
})
