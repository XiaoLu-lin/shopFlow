// lib/components/redPacket/redPacket.js
const util = require('../../../utils/util.js');
const api = require('../../../config/api.js');

Component({
  /**
   * 组件的属性列表
   */
  properties: {
    couponArr: { //优惠券信息
      type: Array, // 类型（必填），目前接受的类型包括：String, Number, Boolean, Object, Array, null（表示任意类型）
      value: [], // 属性初始值（可选），如果未指定则会根据类型选择一个
      observer: function(newVal, oldVal) {
        // console.log("newVal",newVal);
        // console.log("oldVal",oldVal);
        if(newVal == null) return;
        for (const item of newVal) {
          item.HasItBeenClaimed = false
        }
        this.setData({
          couponArr: newVal,
        })
      }
    },
  },

  /**
   * 组件的初始数据
   */
  data: {
    topImages: '/static/images/topImages.png',
    couponLeftImages: 'http://m.qpic.cn/psc?/V14ZaBeY40XWC8/zkoezU7GGNbZGOF.DPhgQXDCLspq1L1upRR.ZiRnZuFvq1XezxpUwmIc4ky9cr0DEpxn.YXOFA15Y03Wwkk2zJSBTVERFZsf3KTl5vSZorE!/b&bo=lgCWAJYAlgADCSw!&rf=viewer_4',
    closeBtnImages: '/static/images/closeBtnImages.png',
    // 是否显示优惠劵弹窗
    isShowCouponPopUp: false
  },

  /**
   * 组件的方法列表
   */
  methods: {
    //阻止弹出层滑动事件，空函数，不做任何处理
    onPreventTouchMove: function () {
      return false;
    },
    //打开优惠劵弹窗
    openTheCouponPopUp: function () {
      var that = this;
      setTimeout(() => {
        // 先开启优惠劵弹窗
        that.setData({
          isShowCouponPopUp: true
        })
        // 设置优惠劵弹窗打开动画
        var animation = wx.createAnimation({
          duration: 600,
          timingFunction: 'ease',
        })
        that.animation = animation;
        animation.scale(1).step();
        that.setData({
          animationData: animation.export()
        })
      }, 1000)
    },
    //关闭优惠劵弹窗
    closeTheCouponPopUp: function () {
      // 设置优惠劵弹窗关闭动画
      var animation = wx.createAnimation({
        duration: 300,
        timingFunction: 'ease',
      })
      this.animation = animation;
      animation.scale(0).step();
      this.setData({
        animationData: animation.export(),
      })
      //执行完动画后再关闭
      setTimeout(() => {
        this.setData({
          isShowCouponPopUp: false
        })
      }, 200)
    },

    getCoupon(e) {
      let couponId = e.currentTarget.dataset.index
      util.request(api.CouponReceive, {
        couponId: couponId
      }, 'POST').then(res => {
        if (res.errno === "success") {
          wx.showToast({
            title: "领取成功"
          })
        }
      })
    },

    //领取单个优惠劵
    getCoupons: function (e) {
      let that = this;
      let index = e.currentTarget.dataset.index;
      let couponArr = that.data.couponArr;
      util.request(api.CouponReceive, {
        couponId: couponArr[index].id
      }, 'POST').then(res => {
        if (res.errno === "success") {
          wx.showToast({
            title: "领取成功"
          })
          couponArr[index].HasItBeenClaimed = true;
          that.setData({
            couponArr: couponArr
          })
        }
      })
    },

    //已领取优惠劵
    alreadyReceived: function () {
      wx.showToast({
        title: '优惠券已领取',
      })
    },

    //领取全部优惠劵
    getAllCoupons: function () {
      let that = this;
      let couponArr = this.data.couponArr;
      for (const item of couponArr) {
        util.request(api.CouponReceive, {
          couponId: item.id
        }, 'POST').then(res => {
          if (res.errno === "success") {
            item.HasItBeenClaimed = true;
          }
        })
      }

      util.showOkToast("领取成功");
      //关闭弹框
      that.closeTheCouponPopUp();
    },
  }
})
