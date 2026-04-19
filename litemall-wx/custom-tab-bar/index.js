// custom-tab-bar/index.js
var util = require('../utils/util.js');
var api = require('../config/api.js');
var app = getApp();

Component({
	properties: {},
	data: {
    //当前高亮项
		selected: 0,
    //tabBar页面数据
		tabList: [
      {
        "pagePath": "pages/index/index",
        "text": "首页"
      },
      {
        "pagePath": "pages/dynamic/dynamic",
        "text": "动态"
		  },
		  {
        "pagePath": "pages/messageList/messageList",
        "text": "消息"
      },
      {
        "pagePath": "pages/cart/cart",
        "text": "购物车"
      },
      {
        "pagePath": "pages/ucenter/index/index",
        "text": "我的"
      }
		]
	},
	attached: function() {},
	methods: {

		//底部切换
		switchTab(e) {
			let key = Number(e.currentTarget.dataset.index);
			let tabList = this.data.tabList;
			let selected= this.data.selected;
			
			if(selected !== key){
				this.setData({
					selected:key
				});
				wx.switchTab({
					url: `/${tabList[key].pagePath}`,
				})
			}
		},
	}
})