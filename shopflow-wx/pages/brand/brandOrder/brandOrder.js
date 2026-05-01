// pages/brand/brandOrder/brandOrder.js
var api = require('../../../config/api.js');
var util = require('../../../utils/util');
var app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    mobile: '',
    showType: 0,
    tableName:['全部','待退款','待发货','已发货','售后'],
    index : 0,
    modalName: '',
    channel: [],
    orderVoList: [],//订单列表
    orderVoIndex : 0,
    page: 1,
    limit: 10,
    totalPages: 1
  },

  bindExchange: function (e) {
    this.setData({
      mobile: e.detail.value
    });
  },
  clearExchange: function () {
    this.setData({
      mobile: ''
    });
  },
  goSearch: function() {
    let that = this;
    that.setData({
      page: 1,
      orderVoList : [],
    });
    that.getOrderVoList();
  },

  //关闭弹窗
  hideModal(e) {
    this.setData({
      modalName: null,
    })
  },

  bindPickerChange: function(e) {
    this.setData({
      index: e.detail.value
    });
  },

  switchTab: function(e) {
    let showType = e.currentTarget.dataset.index;
    this.setData({
      orderVoList: [],
      showType: showType,
      page: 1,
      limit: 10,
      totalPages: 1
    });
    this.getOrderVoList();
  },

  //打开弹窗
  showModal(e) {
    this.setData({
      modalName: e.currentTarget.dataset.target,
      orderVoIndex : e.currentTarget.dataset.index,
    })
  },

  showOrder(e){
    // 防多点击，这个不用配合 wx.hideLoading();
    wx.showLoading({
      title: '加载中...',
      mask: true,
      duration: 3000
    });
    let ordervo = e.currentTarget.dataset.ordervo;
    if (ordervo.aftersaleStatus === 0) {
      util.navigate("/pages/ucenter/orderDetail/orderDetail?orderId="+ordervo.id+"&status=1");
    }else{
      util.navigate("/pages/ucenter/aftersaleDetail/aftersaleDetail?id="+ordervo.id);
    }
  },

  //获取订单信息
  getOrderVoList(){
    let that = this;
    wx.showLoading({
      title: '加载中...',
      mask: true,
      duration: 3000
    });
    util.request(api.BrandOrder, {
      showType: that.data.showType,
      page: that.data.page,
      limit: that.data.limit,
      mobile: that.data.mobile,
    }).then(function(res) {
      if (res.errno === "success") {
        console.log(res);
        that.setData({
          orderVoList: that.data.orderVoList.concat(res.data.list),
          totalPages: res.data.pages,
        });
      }
      wx.hideLoading()
    });
  },

   //管理员发货
   wxShip:function(options){
    let that = this;
    let index = that.data.orderVoIndex;
    let shipSn = options.detail.value.shipSn;
    let shipChannel = options.detail.target.dataset.shipchannel;
    let orderVo = that.data.orderVoList[index];
    if(!shipSn || !shipChannel){
      util.showErrorModal("参数不完整");
      return;
    }
    that.hideModal();
    // 防多点击，这个不用配合 wx.hideLoading();
    wx.showLoading({
      title: '加载中...',
      mask: true,
      duration: 3000
    });
    util.request(api.OrderAdminShip, {
      shipSn: shipSn,
      orderId: orderVo.id,
      shipChannel: shipChannel,
    },'POST').then(function(res) {
      if (res.errno === "success") {
        wx.showToast({
          title: '发货成功',
          icon: 'success',
          duration: 2000//持续的时间
        })
        that.setData({
          page: 1,
          orderVoList : [],
        });
        that.getOrderVoList();
      }
    });
  },

  //管理员取消订单
  wxCancel:function(options){
    let that = this;
    let index = options.target.dataset.index;
    let orderVo = that.data.orderVoList[index];
    wx.showModal({
      title: '订单取消',
      content: '确定要取消订单吗？',
      cancelText: '再看看',
      confirmText: '取消',
      success: res => {
        if (res.confirm) {
          // 防多点击，这个不用配合 wx.hideLoading();
          wx.showLoading({
            title: '加载中...',
            mask: true,
            duration: 3000
          });
          util.request(api.OrderAdminCancel, {
            orderId: orderVo.id,
          },'POST').then(function(res) {
            if (res.errno === "success") {
              wx.showToast({
                title: '订单取消成功',
                icon: 'success',
                duration: 2000//持续的时间
              })
              that.setData({
                page: 1,
                orderVoList : [],
              });
              that.getOrderVoList();
            }
            wx.hideLoading();
          });
        }
      }
    })
  },

  //管理员退款
  wxRefund:function(options){
    let that = this;
    let index = options.target.dataset.index;
    let orderVo = that.data.orderVoList[index];
    wx.showModal({
      title: '订单退款',
      content: '确定要退款吗？',
      cancelText: '再看看',
      confirmText: '退款',
      success: res => {
        if (res.confirm) {
          // 防多点击，这个不用配合 wx.hideLoading();
          wx.showLoading({
            title: '加载中...',
            mask: true,
            duration: 3000
          });
          util.request(api.OrderAdminRefund, {
            orderId: orderVo.id,
            refundMoney: orderVo.actualPrice,
          },'POST').then(function(res) {
            if (res.errno === "success") {
              wx.showToast({
                title: '退款成功',
                icon: 'success',
                duration: 2000//持续的时间
              })
              that.setData({
                page: 1,
                orderVoList : [],
              });
              that.getOrderVoList();
            }
            wx.hideLoading();
          });
        }
      }
    })
  },

  //售后审核拒绝
  wxAftersaleReject:function(options){
    let that = this;
    let index = options.target.dataset.index;
    let orderVo = that.data.orderVoList[index];
    wx.showModal({
      title: '售后审核',
      content: '确定要拒绝吗？',
      cancelText: '再看看',
      confirmText: '拒绝',
      success: res => {
        if (res.confirm) {
          // 防多点击，这个不用配合 wx.hideLoading();
          wx.showLoading({
            title: '加载中...',
            mask: true,
            duration: 3000
          });
          util.request(api.AftersaleReject, {
            orderId: orderVo.id,
          },'POST').then(function(res) {
            if (res.errno === "success") {
              wx.showToast({
                title: '拒绝成功',
                icon: 'success',
                duration: 2000//持续的时间
              })
              that.setData({
                page: 1,
                orderVoList : [],
              });
              that.getOrderVoList();
            }
            wx.hideLoading();
          });
        }
      }
    })
  },


  //售后审核通过
  wxAftersaleRecept:function(options){
    let that = this;
    let index = options.target.dataset.index;
    let orderVo = that.data.orderVoList[index];
    wx.showModal({
      title: '售后审核',
      content: '确定要通过吗？',
      cancelText: '再看看',
      confirmText: '通过',
      success: res => {
        if (res.confirm) {
          // 防多点击，这个不用配合 wx.hideLoading();
          wx.showLoading({
            title: '加载中...',
            mask: true,
            duration: 3000
          });
          util.request(api.AftersaleRecept, {
            orderId: orderVo.id,
          },'POST').then(function(res) {
            if (res.errno === "success") {
              wx.showToast({
                title: '通过成功',
                icon: 'success',
                duration: 2000//持续的时间
              })
              that.setData({
                page: 1,
                orderVoList : [],
              });
              that.getOrderVoList();
            }
            wx.hideLoading();
          });
        }
      }
    })
  },

  //售后退款
  wxAftersaleRefund:function(options){
    let that = this;
    let index = options.target.dataset.index;
    let orderVo = that.data.orderVoList[index];
    wx.showModal({
      title: '商品退款',
      content: '确定要退款吗？',
      cancelText: '再看看',
      confirmText: '退款',
      success: res => {
        if (res.confirm) {
          // 防多点击，这个不用配合 wx.hideLoading();
          wx.showLoading({
            title: '加载中...',
            mask: true,
            duration: 3000
          });
          util.request(api.AftersaleRefund, {
            orderId: orderVo.id,
          },'POST').then(function(res) {
            if (res.errno === "success") {
              wx.showToast({
                title: '退款成功',
                icon: 'success',
                duration: 2000//持续的时间
              })
              that.setData({
                page: 1,
                orderVoList : [],
              });
              that.getOrderVoList();
            }
            wx.hideLoading();
          });
        }
      }
    })
  },


  /**
   * 快递类型
   * @param {*} e 
   */
  getChannel(e){
    let that = this;
    util.request(api.ExpressChannel).then(function(res) {
      if (res.errno === "success") {
        that.setData({
          channel : res.data,
        })
      }
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.getOrderVoList();
    this.getChannel();
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
    let that = this;
    that.setData({
      page: 1,
      orderVoList : [],
    });
    that.getOrderVoList();
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
      this.getOrderVoList();
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