// components/sharePop/sharePop.js
const util = require('../../../utils/util');

Component({
 /**
  * 组件的属性列表
  */
 properties: {
  url: {
    type: String,
    value: '', // 属性值 （可选）
  },
  name: {
    type: String,
    value: '分享', // 属性值 （可选）
  },
  bottom:{
    type: Number,
    value: 0, // 属性值 （可选）
  }
 },

 /**
  * 组件的初始数据
  */
 data: {
  // 弹窗显示控制
  showPop: false,
  canWrite:false,
 },

 /**
  * 组件的方法列表
  */
 methods: {
  closeShare: function() {
    this.setData({
      showPop: false,
    });
  },

  togglePopup: function() {
    let that = this;
    this.setData({
      showPop: !that.data.showPop,
    });
  },

  getUserSetting: function() {
    let that = this;
    wx.getSetting({
      success: function (res) {
        //不存在相册授权
        if (!res.authSetting['scope.writePhotosAlbum']) {
          wx.authorize({
            scope: 'scope.writePhotosAlbum',
            success: function () {
              that.saveShare();
            },
            fail: function (err) {
              wx.showModal({
                title: '警告',
                content: '不授权无法保存',
                showCancel: false
              })
              that.setData({
                canWrite: true
              })
            }
          })
        } else {
          that.saveShare();
        }
      }
    })
  },

  handleSetting: function(e) {
    var that = this;
    if (!e.detail.authSetting['scope.writePhotosAlbum']) {
      wx.showModal({
        title: '警告',
        content: '不授权无法保存',
        showCancel: false
      })
      that.setData({
        canWrite: true
      })
    } else {
      that.saveShare();
      that.setData({
        canWrite: false
      })
    }
  },


  // 保存分享图
  saveShare: function() {
    let that = this;
    if(!that.data.url){
      util.showErrorModal("图片地址为空")
      return;
    }
    wx.downloadFile({
      url: that.data.url,
      success: function(res) {
        wx.saveImageToPhotosAlbum({
          filePath: res.tempFilePath,
          success: function(res) {
          wx.showModal({
            title: '存图成功',
            content: '图片成功保存到相册了，可以分享到朋友圈了',
            showCancel: false,
            confirmText: '好的',
            confirmColor: '#a78845',
            success: function(res) {
              if (res.confirm) {
                console.log('用户点击确定');
              }
            }
          });
          },
          fail: function(res) {
            console.log(res)
          }
        })
      },
      fail: function(res) {
        console.log(res)
      }
    })
  },
  
}})