// lib/components/lyg-popup/lyg-popup.js
var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');
var app = getApp();

Component({
  /**
   * 组件的属性列表
   */
  properties: {
    title: {
      type: String,
      value: "服务协议"
    },
    // 协议路径
    protocolPath: {
      type: String
    },
    // 政策路径
    policyPath: {
      type: String
    },
    policyStorageKey: {
      type: String,
      value:"has_read_privacy"
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    showPopup: true
  },

  attached(){
    this.init();
  },

  /**
   * 组件的方法列表
   */
  methods: {

    init(){
      let that = this;
      let showPopup = wx.getStorageSync(that.data.policyStorageKey);
      that.setData({
        showPopup: showPopup === "" ? true : showPopup
      })
    },

    service(){
      let that = this;
      util.navigate(that.data.protocolPath);
    },

    /**
     * 取消授权
     */
    cancel(){
      wx.navigateBack({delta: 0})
      // wx.exitMiniProgram({success: (res) => {}})
    },
    
    // 关闭弹框
    confirm() {
      let that = this;
      that.setData({
        showPopup: false
      })
      wx.setStorageSync(that.data.policyStorageKey, false);
    }

  }
})
