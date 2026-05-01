// lib/components/drawer/index.js
let touchDotX = 0;//X按下时坐标
let touchDotY = 0;//y按下时坐标
let interval;//计时器
let time = 0;//从按下到松开共多少时间*100

Component({
  
  /**
   * 组件的属性列表
   */
  properties: {
    

  },

  /**
   * 组件的初始数据
   */
  data: {
    userInfo: {},
    hasLogin: false
  },

  /**
   * 组件的方法列表
   */
  methods: {
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

  }
})
