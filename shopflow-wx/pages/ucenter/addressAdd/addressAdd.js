var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');
var check = require('../../../utils/check.js');
var area = require('../../../utils/area.js');
var app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    address: {
      id: 0,
      areaCode: 0,
      address: '',
      name: '',
      mobile: '',
      addressAll: '',
      addressDetail: '',
      isDefault: true,
      province: '',
      city: '',
      county: ''
    },
    region: ['广东省', '广州市', '海珠区'],
  },

  //获取姓名
  bindinputName(event) {
    let address = this.data.address;
    address.name = event.detail.value;
    this.setData({
      address: address
    });
  },

  //获取电话
  bindinputMobile(event) {
    let address = this.data.address;
    address.mobile = event.detail.value;
    this.setData({
      address: address
    });
  },

  //获取省市区
  RegionChange: function(e) {
    let address = this.data.address;
    address.province = e.detail.value[0];
    address.city = e.detail.value[1];
    address.county = e.detail.value[2];
    this.setData({
      region: e.detail.value,
      address: address,
    })
  },

  //获取详细地址
  bindinputAddress(event) {
    let address = this.data.address;
    address.addressDetail = event.detail.value;
    this.setData({
      address: address
    });
  },

  //是否默认
  bindIsDefault() {
    let address = this.data.address;
    address.isDefault = !address.isDefault;
    this.setData({
      address: address
    });
  },

  getAddressDetail() {
    let that = this;
    util.request(api.AddressDetail, {
      id: that.data.addressId
    }).then(function (res) {
      if (res.errno === "success") {
        if (res.data) {
          let region = that.data.region;
          region[0] = res.data.province;
          region[1] = res.data.city;
          region[2] = res.data.county;
          that.setData({
            address: res.data,
            region: region,
          });
        }
      }
    });
  },

  /**
   * 取消
   */
  cancelAddress() {
    wx.navigateBack();
  },

  /**
   * 保存
   */
  saveAddress() {
    let that = this;
    let address = this.data.address;
    if (address.name == '') {
      util.showErrorModal('请输入姓名');
      return false;
    }
    if (address.mobile == '') {
      util.showErrorModal('请输入手机号码');
      return false;
    }
    if(!(/(^1(3\d|4[5-9]|5[0-35-9]|6[567]|7[0-8]|8\d|9[0-35-9])\d{8}$)/.test(address.mobile))){
      util.showErrorModal('手机号不正确');
      return false;
    }
    if (address.addressDetail == '') {
      util.showErrorModal('请输入详细地址');
      return false;
    }
    let addressAll = address.province + address.city + address.county + address.addressDetail;
    util.request(api.AddressSave, {
      id: address.id,
      name: address.name,
      mobile: address.mobile,
      province: address.province,
      addressAll: addressAll,
      city: address.city,
      county: address.county,
      areaCode: address.areaCode,
      addressDetail: address.addressDetail,
      isDefault: address.isDefault
    }, 'POST').then(function (res) {
      if (res.errno === "success") {
        //返回之前，先取出上一页对象，并设置addressId
        var pages = getCurrentPages();
        var prevPage = pages[pages.length - 2];
        if (prevPage.route == "pages/checkout/checkout") {
          prevPage.setData({
            addressId: res.data
          })
          try {
            wx.setStorageSync('addressId', res.data);
          } catch (e) {
            console.log(e);
          }
        }
        wx.navigateBack();
      }
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    if (options.id && options.id != 0) {
      this.setData({
        addressId: options.id
      });
      this.getAddressDetail();
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
