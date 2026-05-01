// lib/components/uploadImg/uploadImg.js
var api = require('../../../config/api.js');
Component({
  /**
   * 组件的属性列表
   */
  properties: {
    imgSite: { //图片张数
      type: Number, // 类型（必填），目前接受的类型包括：String, Number, Boolean, Object, Array, null（表示任意类型）
      value: 4 // 属性初始值（可选），如果未指定则会根据类型选择一个
    },
    pics: { //初始数据
      type: Array, // 类型（必填），目前接受的类型包括：String, Number, Boolean, Object, Array, null（表示任意类型）
      value: [],// 属性初始值（可选），如果未指定则会根据类型选择一个
      observer: function(newVal, oldVal) {
        if(newVal) return;
        this.setData({
          fileList: newVal,
        })
      }
    },
    pic: { //初始数据
      type: String, // 类型（必填），目前接受的类型包括：String, Number, Boolean, Object, Array, null（表示任意类型）
      value: "" ,// 属性初始值（可选），如果未指定则会根据类型选择一个
      observer: function(newVal, oldVal) {
        if(newVal) return;
        this.data.fileList = [];
        this.data.fileList.push(newVal);
        this.setData({
          fileList: this.data.fileList,
        })
      }
    },
    title: { //标题
      type: String, // 类型（必填），目前接受的类型包括：String, Number, Boolean, Object, Array, null（表示任意类型）
      value: "图片上传" // 属性初始值（可选），如果未指定则会根据类型选择一个
    },
  },

  /**
   * 组件的初始数据
   */
  data: {
    progress: 0,
    fileList: [],
  },

  /**
   * 组件的方法列表
   */
  methods: {

    chooseImage: function(e) {
      let that = this;
      wx.chooseMedia({
        count: 1,
        mediaType: ['image','video'],
        sourceType: ['album', 'camera'],
        maxDuration: 30,
        camera: 'back',
        success(res) {
          that.upload(res);
        }
      })
    },

    upload: function(res) {
      let that = this;
      wx.showLoading({
        title: '加载中',
      });
      const uploadTask = wx.uploadFile({
        name: 'file',
        url: api.StorageUpload,
        filePath: res.tempFiles[0].tempFilePath,
        header: {
          'Content-Type': 'application/json',
          'X-ShopFlow-TenantId': wx.getStorageSync('tenantId'),
          'X-ShopFlow-User-Token': wx.getStorageSync('userToken'),
          'X-ShopFlow-Admin-Token': wx.getStorageSync('adminToken')
        },
        success: function(res) {
          let data = JSON.parse(res.data);
          if (data.errno === "success") {
            let url = data.data.url
            that.data.fileList.push(url);
            that.setData({
              fileList: that.data.fileList,
            })
            //将值返回给getImage，getImage为组件调用页的js方法
            that.triggerEvent('getImage',{
              picUrls: that.data.fileList
            })
          }
        },
        fail: function(e) {
          console.log(e);
          wx.hideLoading();
          wx.showModal({
            title: '错误',
            content: '上传失败',
            showCancel: false
          })
        },
      })

      uploadTask.onProgressUpdate((res) => {
        that.setData({
          progress: res.progress,
        })
        if(res.progress === 100){
          wx.hideLoading();
          that.setData({
            progress: 0,
          })
        }
      })
    },

    previewImage: function(e) {
      wx.previewImage({
        current: e.currentTarget.id, // 当前显示图片的http链接
        urls: this.data.fileList // 需要预览的图片http链接列表
      })
    },
  
    DelImg(e) {
      let that = this;
      wx.showModal({
        title: '图片删除',
        content: '确定要删除这张图片吗？',
        cancelText: '再看看',
        confirmText: '再见',
        success: (res) => {
          if (res.confirm) {
            let index = e.currentTarget.dataset.index;
            that.data.fileList.splice(index, 1);
            that.setData({
              fileList: that.data.fileList,
            })
            that.triggerEvent('getImage',{
              picUrls: that.data.fileList
            })
          }
        },
      })
    },
  }
})
