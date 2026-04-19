// pages/myBrand/myBrand.js
const util = require('../../../utils/util.js');
const api = require('../../../config/api.js');
const user = require('../../../utils/user.js');
//获取应用实例
var app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    brandId: 0,
    brand: {},
    picUrls: []
  },

  chooseImage(e){
    this.setData({
      picUrls: e.detail.picUrls
    })
  },

  submitBrand: function(e) {
    let that = this;
    if (!app.globalData.hasLogin) {
      util.navigate("/pages/auth/login/login");
      return;
    }

    let brand = that.data.brand;
    brand.name = e.detail.value.name;
    brand.depict = e.detail.value.depict;
    brand.mail = e.detail.value.mail;
    brand.trueName = e.detail.value.trueName;
    brand.floorPrice = parseFloat(e.detail.value.brandPrice);
    brand.address = e.detail.value.address;
    brand.picUrl = that.data.picUrls[0];
    if(!brand.name || !brand.depict || !brand.mail || !brand.floorPrice || !brand.picUrl || !brand.trueName){
      util.showErrorModal("参数错误");
      return;
    }
    
    wx.showModal({
      title: '保存店铺',
      content: '确定要保存吗？',
      cancelText: '再看看',
      confirmText: '保存',
      success: res => {
        if (res.confirm) {
          util.request(api.BrandSave, {
            id: brand.id,
            name : brand.name,
            depict : brand.depict,
            mail : brand.mail,
            userId : brand.userId,
            picUrl : brand.picUrl,
            address: brand.address,
            trueName: brand.trueName,
            floorPrice : brand.floorPrice,
            version : brand.version,
          }, 'POST').then(function(res) {
            if (res.errno === "success") {
              wx.showToast({
                title: '保存成功！',
                icon: 'success',
                duration: 2000,
                complete: function() {
                  util.redirect("/pages/brand/brandDetail/brandDetail?id="+res.data.id+"&status=1")
                }
              });
            }
          });
        }
      }
    })
  },

  getBrandDetail: function() {
    let that = this;
    util.request(api.BrandDetail, {
      brandId: that.data.brandId
    }).then(function(res) {
      if (res.errno === "success") {
        that.data.picUrls.push(res.data.brand.picUrl)
        that.setData({
          brand: res.data.brand,
          'brand.trueName': res.data.brandUser.trueName,
          picUrls: that.data.picUrls,
        });
      }
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    // 页面初始化 options为页面跳转所带来的参数
    var that = this;
    if (options.id) {
      that.setData({
        brandId: options.id
      });
      that.getBrandDetail();
    }
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