const util = require('../../../utils/util.js');
const api = require('../../../config/api.js');
const user = require('../../../utils/user.js');
var app = getApp();

let touchDotX = 0;//X按下时坐标
let touchDotY = 0;//y按下时坐标
let interval;//计时器
let time = 0;//从按下到松开共多少时间*100


Page({

  /**
   * 页面的初始数据
   */
  data: {
    hasLogin: false,
    Administrator: false,
    integralPrice: 0,
    userLevel: "普通用户",
    userInfo: {},
    brand:{},
    order: {
      unpaid: 0,
      unship: 0,
      unrecv: 0,
      uncomment: 0
    },
  },

  getProfile(){
    util.navigate("/pages/ucenter/profile/profile");
  },
  goQrAuth(){
    util.navigate("/pages/auth/qrauth/qrauth");
  },
  goLogin() {
    if (!this.data.hasLogin) {
      util.navigate("/pages/auth/login/login");
    }
  },
  goOrder() {
    if (this.data.hasLogin) {
      util.navigate("/pages/ucenter/order/order?tab=0");
    } else {
      util.navigate("/pages/auth/login/login");
    }
  },
  shareMe() {
    if (this.data.hasLogin) {
      util.navigate("/pages/ucenter/shareMe/shareMe");
    } else {
      util.navigate("/pages/auth/login/login");
    }
  },
  goWithdraw(){
    if (this.data.hasLogin) {
      util.navigate("/pages/ucenter/withdraw/withdraw");
    } else {
      util.navigate("/pages/auth/login/login");
    }
  },
  goOrderIndex(e) {
    if (this.data.hasLogin) {
      let tab = e.currentTarget.dataset.index
      let route = e.currentTarget.dataset.route
      util.navigate(route+'?tab='+tab);
    } else {
      util.navigate("/pages/auth/login/login");
    };
  },
  goBrand(){
    let that = this;
    let brand = that.data.brand;
    if(!that.data.hasLogin){
      util.navigate("/pages/auth/login/login");
      return;
    }
    if(!brand){
      util.navigate("/pages/brand/brandAdd/brandAdd");
      return;
    }
    util.navigate("/pages/brand/brandDetail/brandDetail?id="+brand.id+"&status=1");
  },
  goDaily() {
    util.navigate("/pages/issue/issueDaily/issueDaily");
  },
  goCoupon() {
    util.navigate("/pages/ucenter/couponList/couponList");
  },
  goCollect() {
    util.navigate("/pages/ucenter/collect/collect");
  },
  goFeedback(e) {
    util.navigate("/pages/ucenter/feedback/feedback");
  },
  goFootprint() {
    util.navigate("/pages/ucenter/footprint/footprint");
  },
  goAddress() {
    util.navigate("/pages/ucenter/address/address");
  },
  goAfterSale: function() {
    util.navigate("/pages/ucenter/aftersaleList/aftersaleList");
  },
  aboutUs: function() {
    util.navigate("/pages/ucenter/about/about");
  },
  goHelp: function () {
    util.navigate("/pages/ucenter/help/help");
  },  

  bindPhoneNumber: function(e) {
    if (e.detail.errMsg !== "getPhoneNumber:ok") {
      // 拒绝授权
      return;
    }
    if (!this.data.hasLogin) {
      wx.showToast({
        title: '绑定失败：请先登录',
        icon: 'none',
        duration: 2000
      });
      return;
    }
    util.request(api.AuthBindPhone, {
      iv: e.detail.iv,
      encryptedData: e.detail.encryptedData
    }, 'POST').then(function(res) {
      if (res.errno === "success") {
        wx.showToast({
          title: '绑定手机号码成功',
          icon: 'success',
          duration: 2000
        });
      }
    });
  },
  
  exitLogin: function() {
    wx.showModal({
      title: '退出登录',
      confirmColor: '#b4282d',
      content: '确认退出登录？',
      success: function(res) {
        if (!res.confirm) {
          return;
        }
        util.request(api.AuthLogout, {}, 'POST');
        app.globalData.hasLogin = false;
        app.globalData.Administrator = false;
        //清除用户数据
        wx.removeStorageSync('userToken');
        wx.removeStorageSync('userInfo');
        //清除管理员数据
        wx.removeStorageSync('adminInfo');
        wx.removeStorageSync('adminToken');
        wx.reLaunch({
          url: '/pages/index/index'
        });
      }
    })
  },

  // 触摸开始事件，滑动打开关闭抽屉
  touchStart: function (e) {
    touchDotX = e.touches[0].pageX; // 获取触摸时的原点
    touchDotY = e.touches[0].pageY;
    // 使用js计时器记录时间    
    interval = setInterval(function () {
        time++;
    }, 100);
  },
  // 触摸结束事件，滑动打开关闭抽屉
  touchEnd: function (e) {
    let touchMoveX = e.changedTouches[0].pageX;
    let touchMoveY = e.changedTouches[0].pageY;
    let tmX = touchMoveX - touchDotX;
    let tmY = touchMoveY - touchDotY;
    if (time < 20 && (tmX > 200 || tmX < -50)) {
        let absX = Math.abs(tmX);
        let absY = Math.abs(tmY);
        if (absX > 2 * absY) {
          if (tmX < 0) {
            this.setData({
              modalName: null
            })
          } else {
            this.setData({
              modalName: "viewModal"
            })
          }
        }
    }
    clearInterval(interval); // 清除setInterval
    time = 0;
  },


  //打开抽屉
  showModal(e) {
    this.setData({
      modalName: e.currentTarget.dataset.target
    })
  },

  //关闭抽屉
  hideModal(e) {
    this.setData({
      modalName: null
    })
  },

  goLogin() {
    if (!this.data.hasLogin) {
      wx.navigateTo({
        url: "/pages/auth/login/login"
      });
    }
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
      let that = this;
      util.request(api.UserIndex).then(function(res) {
        if (res.errno === "success") {
          that.setData({
            order: res.data.order,
            brand: res.data.brand,
            userLevel: res.data.userLevel,
            integralPrice: res.data.integralPrice
          });
        }
      });
    }

    //自定义底部导航栏高亮显示不加会导致高亮随机跳
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({
        selected: 4
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
