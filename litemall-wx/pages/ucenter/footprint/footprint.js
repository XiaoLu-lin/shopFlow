var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');
var app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    footprintList: [],
    page: 1,
    limit: 10,
    totalPages: 1,
  },

  //按下事件开始  
  touchStart: function(e) {
    let that = this;
    that.setData({
      touchStart: e.timeStamp
    })
    console.log(e.timeStamp + '- touchStart')
  },

  //按下事件结束  
  touchEnd: function(e) {
    let that = this;
    that.setData({
      touchEnd: e.timeStamp
    })
    console.log(e.timeStamp + '- touchEnd')
  },

  getFootprintList() {
    // 防多点击，这个不用配合 wx.hideLoading();
    wx.showLoading({
      title: '加载中...',
      mask: true,
      duration: 3000
    });
    let that = this;
    util.request(api.FootprintList, {
      page: that.data.page,
      limit: that.data.limit
    }).then(function(res) {
      if (res.errno === "success") {
        let f1 = that.data.footprintList;
        let f2 = res.data.list;
        for (let i = 0; i < f2.length; i++) {
          f2[i].addDate = f2[i].addTime.substring(0, 10)
          let last = f1.length - 1;
          if (last >= 0 && f1[last][0].addDate === f2[i].addDate) {
            f1[last].push(f2[i]);
          } else {
            let tmp = [];
            tmp.push(f2[i])
            f1.push(tmp);
          }
        }
        console.log(f1);
        that.setData({
          footprintList: f1,
          totalPages: res.data.pages
        });
      }
      wx.hideLoading();
    });
  },

  deleteItem(event) {
    let that = this;
    let index = event.currentTarget.dataset.index;
    let iindex = event.currentTarget.dataset.iindex;
    let footprintId = this.data.footprintList[index][iindex].id;
    let goodsId = this.data.footprintList[index][iindex].goodsId;
    var touchTime = that.data.touchEnd - that.data.touchStart;
    console.log(touchTime);
    //如果按下时间大于350为长按  
    if (touchTime > 350) {
      wx.showModal({
        title: '足迹删除',
        content: '确认要删除所选足迹？',
        success: function(res) {
          if (res.confirm) {
            util.request(api.FootprintDelete, {
              id: footprintId
            }, 'POST').then(function(res) {
              if (res.errno === "success") {
                wx.showToast({
                  title: '删除成功',
                  icon: 'success',
                  duration: 2000
                });
                that.data.footprintList[index].splice(iindex, 1)
                if (that.data.footprintList[index].length == 0) {
                  that.data.footprintList.splice(index, 1)
                }
                that.setData({
                  footprintList: that.data.footprintList
                });
              }
            });
          }
        }
      });
    } else {
      wx.navigateTo({
        url: '/pages/goodsDetail/goodsDetail?goodId=' + goodsId,
      });
    }
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      footprintList: [],
      page: 1,
      limit: 10,
      totalPages: 1
    })
    this.getFootprintList();
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
    this.setData({
      footprintList: [],
      page: 1,
      limit: 10,
      totalPages: 1
    })
    this.getFootprintList();
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
      this.getFootprintList();
    } else {
      wx.showToast({
        title: '没有更多用户足迹了',
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
