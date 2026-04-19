const util = require('./utils/util.js');
const api = require('./config/api.js');
const user = require('./utils/user.js');


App({
  //系统初始化
  onLaunch: function() {
    Promise.prototype.finally = function(callback){
      let P = this.constructor;
      return this.then(
        value => {
          P.resolve(callback()).then(() => value)
        },
        reason => {
          P.resolve(callback()).then(() => { throw reason })
        }
      )
    }

    //获取系统参数并写入全局参数
    wx.getSystemInfo({
      success: e => {
        this.globalData.StatusBar = e.statusBarHeight;
        let capsule = wx.getMenuButtonBoundingClientRect();
        if (capsule) {
          this.globalData.Custom = capsule;
          this.globalData.CustomBar = capsule.bottom + capsule.top - e.statusBarHeight;
        } else {
          this.globalData.CustomBar = e.statusBarHeight + 50;
        }
      }
    })

    //获取版本更新
    wx.getUpdateManager().onUpdateReady(function() {
      wx.showModal({
        title: '更新提示',
        content: '新版本已经准备好，是否重启应用？',
        success: function(res) {
          if (res.confirm) {
            // 新的版本已经下载好，调用 applyUpdate 应用新版本并重启
            wx.getUpdateManager().applyUpdate()
          }
        }
      })
    })
  },

  //系统显示
  onShow: function(options) {
    //获取授权
    util.request(api.AuthUrl,{
      appid : util.getAppid()
    },"POST").then(function(res) {
      if (res.errno === "success") {
        wx.setStorageSync('tenantId', res.data);
      }
    });

    user.checkLogin().then(res => {
      this.globalData.hasLogin = true;
    }).catch(() => {
      this.globalData.hasLogin = false;
    });

    user.checkAdminLogin().then(res => {
      this.globalData.Administrator = true;
    }).catch(() => {
      this.globalData.Administrator = false;
    });
  },

  globalData: {
    hasLogin: false,
    Administrator: false,
    ColorList: [
    {
      title: '桔橙',
      name: 'orange',
      color: '#f48f18'
    },
    {
      title: '明黄',
      name: 'yellow',
      color: '#fbbd08'
    },
    {
      title: '橄榄',
      name: 'olive',
      color: '#8dc63f'
    },
    {
      title: '森绿',
      name: 'green',
      color: '#39b54a'
    },
    {
      title: '天青',
      name: 'cyan',
      color: '#1cbbb4'
    },
    {
      title: '海蓝',
      name: 'blue',
      color: '#0081ff'
    },
    {
      title: '姹紫',
      name: 'purple',
      color: '#6739b6'
    },
    {
      title: '木槿',
      name: 'mauve',
      color: '#9c26b0'
    },
    {
      title: '桃粉',
      name: 'pink',
      color: '#e03997'
    },
    {
      title: '棕褐',
      name: 'brown',
      color: '#a5673f'
    },
    {
      title: '玄灰',
      name: 'grey',
      color: '#8799a3'
    },
    {
      title: '草灰',
      name: 'gray',
      color: '#aaaaaa'
    },
  ]
  }
})