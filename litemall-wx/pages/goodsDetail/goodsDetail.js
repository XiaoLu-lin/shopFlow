var app = getApp();
const WxParse = require('../../lib/wxParse/wxParse.js');
const util = require('../../utils/util.js');
const api = require('../../config/api.js');


Page({

  /**
   * 页面的初始数据
   */
  data: {
    isReward: false, //标识是否赏金
    rewardTask: {}, //赏金规则
    rewardLink: {}, //赏金活动
    canShare: false,
    goodId: 0,
    goods: {},
    checkedSpecPicUrl:'',
    groupon: [], //该商品支持的团购规格
    grouponLink: {}, //参与的团购
    attribute: [],
    issueList: [],
    comment: [],
    brand: {},
    specificationList: [],
    productList: [],
    relatedGoods: [],
    cartGoodsCount: 0,
    userHasCollect: false,
    number: 1,
    checkedSpecText: '请选择规格数量',
    tmpSpecText: '请选择规格数量',
    checkedSpecPrice: 0,
    openAttr: false,
    collect: false,
    shareImage: '',
    isGroupon: false, //标识是否是一个参团购买
    soldout: false,

    hasLogin: false,
    Administrator: false,
    userInfo: {},

    canvasInfo: {},//评论饼状图
    dataList: [],//评论饼状图数据
    pieInfo: {}//评论饼状图
  },

  /**
   * 店铺客服
   */
  openChat: function() {
    let brand = this.data.brand
    if (!brand.userId || !brand.name) {
      util.showErrorToast("该店铺暂无客服")
      return;
    }
    if (this.data.hasLogin) {
      util.navigate("/pages/chat/chat?receiveUserId="+brand.userId+"&chatName="+brand.name);
    } else {
      util.navigate("/pages/auth/login/login");
    }
  },

  /**
   * 分享弹窗
   */
  showShare: function() {
    let that = this;
    if (that.data.hasLogin) {
      that.sharePop.togglePopup();
    } else {
      util.navigate("/pages/auth/login/login");
    }
  },

  //从分享的团购进入
  getGrouponInfo: function(grouponId) {
    let that = this;
    util.request(api.GroupOnJoin, {
      grouponId: grouponId
    }).then(function(res) {
      if (res.errno === "success") {
        that.setData({
          grouponLink: res.data.groupon,
          goodId: res.data.goodId
        });
        //获取商品详情
        that.getGoodsInfo();
      }
    });
  },

  /**常见问题 */
  goHelp: function () {
    util.navigate("/pages/ucenter/help/help");
  },  

  //从分享的赏金进入
  getRewardInfo: function(rewardId) {
    let that = this;
    util.request(api.RewardJoin, {
      rewardId: rewardId
    }).then(function(res) {
      if (res.errno === "success") {
        console.log(res);
        that.setData({
          rewardTask: res.data.rewardTask,
          rewardLink: res.data.reward,
          goodId: res.data.goodId
        });
        //获取商品详情
        that.getGoodsInfo();
      }
    });
  },

  /**
   * 图片查看
   * @param {*} e 
   */
  previewImage: function(e) {
    wx.previewImage({
      current: e.currentTarget.id, // 当前显示图片的http链接
      urls: [e.currentTarget.dataset.url] // 需要预览的图片http链接列表
    })
  },

  // 获取商品信息
  getGoodsInfo: function() {
    let that = this;
    util.request(api.GoodsDetail, {
      goodId: that.data.goodId
    }).then(function(res) {
      if (res.errno === "success") {
        console.log(res)
        try {
          let _specificationList = res.data.specificationList;
          let _retailPrice = res.data.info.retailPrice;
          let _productList = res.data.productList;
          for (var i = 0; i < _productList.length; i++) {
            if(_productList[i].price == _retailPrice){
              that.setData({
                checkedSpecPicUrl:_specificationList[0].valueList[0].picUrl
              })
            }
          }
          // 如果仅仅存在一种货品，那么商品页面初始化时默认checked
          if (_specificationList.length == 1) {
            if (_specificationList[0].valueList.length == 1) {
              _specificationList[0].valueList[0].checked = true

              // 如果仅仅存在一种货品，那么商品价格应该和货品价格一致
              // 这里检测一下
              let _productPrice = res.data.productList[0].price;
              let _goodsPrice = res.data.info.retailPrice;
              if (_productPrice != _goodsPrice) {
                console.error('商品数量价格和货品不一致');
              }
              that.setData({
                checkedSpecText: _specificationList[0].valueList[0].value,
                tmpSpecText:  _specificationList[0].valueList[0].value,
              });
            }
          }

          //初始化评论饼状图数据
          var _comment = res.data.comment;
          var _dataList = [ {title: "很差",value: _comment.star1,background: "#0adae9"},
                            {title: "不错",value: _comment.star2,background: "#b1e90a"},
                            {title: "满意",value: _comment.star3,background: "#e9cf0a"},
                            {title: "很好",value: _comment.star4,background: "#e97d0a"},
                            {title: "点赞",value: _comment.star5,background: "#eb4508"}]

          that.setData({
            goods: res.data.info,
            attribute: res.data.attribute,
            issueList: res.data.issue,
            comment: res.data.comment,
            brand: res.data.brand,
            specificationList: res.data.specificationList,
            productList: res.data.productList,
            userHasCollect: res.data.userHasCollect,
            shareImage: res.data.shareImage,
            checkedSpecPrice: res.data.info.retailPrice,
            groupon: res.data.groupon,
            canShare: res.data.share,
            dataList:_dataList,
          });

          console.log(res)
          //如果是通过分享的团购参加团购，则团购项目应该与分享的一致并且不可更改
          if (that.data.isGroupon) {
            let groupons = that.data.groupon;
            for (var i = 0; i < groupons.length; i++) {
              if (groupons[i].id != that.data.grouponLink.rulesId) {
                groupons.splice(i, 1);
              }
            }
            groupons[0].checked = true;
            //重设团购规格
            that.setData({
              groupon: groupons
            });
          }
          if (res.data.userHasCollect) {
            that.setData({
              collect: true
            });
          } else {
            that.setData({
              collect: false
            });
          }
          if(res.data.info.detail){
            WxParse.wxParse('goodsDetail', 'html', res.data.info.detail, that);
          }
          //获取推荐商品
          that.getGoodsRelated();
        } catch (error) {
          wx.navigateBack({delta: 0})
          console.log(error);
          util.showErrorModal("商品信息错误")
        }
      }else{
        wx.navigateBack({delta: 0})
      }
    });
  },

  // 获取推荐商品
  getGoodsRelated: function() {
    let that = this;
    util.request(api.GoodsRelated, {
      goodId: that.data.goodId
    }).then(function(res) {
      if (res.errno === "success") {
        that.setData({
          relatedGoods: res.data.list,
        });
      }
    });
  },

  // 团购选择
  clickGroupon: function(event) {
    let that = this;

    //参与团购，不可更改选择
    if (that.data.isGroupon) {
      return;
    }

    let specValueId = event.currentTarget.dataset.valueId;
    let _grouponList = this.data.groupon;
    for (let i = 0; i < _grouponList.length; i++) {
      if (_grouponList[i].id == specValueId) {
        if (_grouponList[i].checked) {
          _grouponList[i].checked = false;
        } else {
          _grouponList[i].checked = true;
        }
      } else {
        _grouponList[i].checked = false;
      }
    }

    this.setData({
      groupon: _grouponList,
    });
  },

  // 规格选择
  clickSkuValue: function(event) {
    let specName = event.currentTarget.dataset.name;
    let specValueId = event.currentTarget.dataset.valueId;
    //判断是否可以点击

    //TODO 性能优化，可在wx:for中添加index，可以直接获取点击的属性名和属性值，不用循环
    let _specificationList = this.data.specificationList;
    for (let i = 0; i < _specificationList.length; i++) {
      if (_specificationList[i].name === specName) {
        for (let j = 0; j < _specificationList[i].valueList.length; j++) {
          if (_specificationList[i].valueList[j].id == specValueId) {
            //如果已经选中，则反选
            if (_specificationList[i].valueList[j].checked) {
              _specificationList[i].valueList[j].checked = false;
            } else {
              _specificationList[i].valueList[j].checked = true;
            }
          } else {
            _specificationList[i].valueList[j].checked = false;
          }
        }
      }
    }

    this.setData({
      specificationList: _specificationList,
    });
    //重新计算spec改变后的信息
    this.changeSpecInfo();
  },

  //获取选中的团购信息
  getCheckedGrouponValue: function() {
    let checkedValues = {};
    let _grouponList = this.data.groupon;
    for (let i = 0; i < _grouponList.length; i++) {
      if (_grouponList[i].checked) {
        checkedValues = _grouponList[i];
      }
    }
    return checkedValues;
  },

  //获取选中的规格信息
  getCheckedSpecValue: function() {
    let checkedValues = [];
    let _specificationList = this.data.specificationList;
    for (let i = 0; i < _specificationList.length; i++) {
      let _checkedObj = {
        name: _specificationList[i].name,
        valueId: 0,
        valueText: '',
      };
      for (let j = 0; j < _specificationList[i].valueList.length; j++) {
        if (_specificationList[i].valueList[j].checked) {
          _checkedObj.valueId = _specificationList[i].valueList[j].id;
          _checkedObj.valueText = _specificationList[i].valueList[j].value;
        }
      }
      checkedValues.push(_checkedObj);
    }
    return checkedValues;
  },

  //判断规格是否选择完整
  isCheckedAllSpec: function() {
    return !this.getCheckedSpecValue().some(function(v) {
      if (v.valueId == 0) {
        return true;
      }
    });
  },

  getCheckedSpecKey: function() {
    let checkedValue = this.getCheckedSpecValue().map(function(v) {
      return v.valueText;
    });
    return checkedValue;
  },

  // 规格改变时，重新计算价格及显示信息
  changeSpecInfo: function() {
    let checkedNameValue = this.getCheckedSpecValue();
    //设置选择的信息
    let checkedValue = checkedNameValue.filter(function(v) {
      if (v.valueId != 0) {
        return true;
      } else {
        return false;
      }
    }).map(function(v) {
      return v.valueText;
    });
    if (checkedValue.length > 0) {
      this.setData({    
        tmpSpecText: checkedValue.join('　')
      });
    } else {
      this.setData({
        tmpSpecText: '请选择规格数量'
      });
    }
    if (this.isCheckedAllSpec()) {
      this.setData({
        checkedSpecText: this.data.tmpSpecText
      });
      // 规格所对应的货品选择以后
      let checkedProductArray = this.getCheckedProductItem(this.getCheckedSpecKey());
      if (!checkedProductArray || checkedProductArray.length <= 0) {
        this.setData({
          soldout: true ,
        });
        console.error('规格所对应货品不存在');
        return;
      }
      let checkedProduct = checkedProductArray[0];
      if (checkedProduct.number > 0) {
        this.setData({
          checkedSpecPrice: checkedProduct.price,
          checkedSpecPicUrl: checkedProduct.url,
          soldout: false
        });
      } else {
        this.setData({
          checkedSpecPrice: this.data.goods.retailPrice,
          soldout: true
        });
      }
    } else {
      this.setData({
        checkedSpecText: '规格数量选择',
        checkedSpecPrice: this.data.goods.retailPrice,
        soldout: false
      });
    }
  },

  // 获取选中的产品（根据规格）
  getCheckedProductItem: function(key) {
    return this.data.productList.filter(function(v) {
      if (v.specifications.toString() == key.toString()) {
        return true;
      } else {
        return false;
      }
    });
  },

  //添加或是取消收藏
  addCollectOrNot: function() {
    let that = this;
    util.request(api.CollectAddOrDelete, {
      type: 0,
      valueId: that.data.goodId
    }, "POST").then(function(res) {
      if (that.data.userHasCollect) {
        that.setData({
          collect: false,
          userHasCollect: false
        });
      } else {
        that.setData({
          collect: true,
          userHasCollect: true
        });
      }
    });
  },

  //立即购买（先自动加入购物车）
  addFast: function() {
    let that = this;
    if (!this.data.hasLogin) {
      util.navigate("/pages/auth/login/login");
      return false;
    }
    if (this.data.openAttr == false) {
      //打开规格选择窗口
      this.setData({
        openAttr: !this.data.openAttr
      });
    } else {
      //提示选择完整规格
      if (!this.isCheckedAllSpec()) {
        util.showErrorModal('请选择完整规格');
        return false;
      }
      //根据选中的规格，判断是否有对应的sku信息
      let checkedProductArray = this.getCheckedProductItem(this.getCheckedSpecKey());
      if (!checkedProductArray || checkedProductArray.length <= 0) {
        //找不到对应的product信息，提示没有库存
        util.showErrorModal('没有库存');
        return false;
      }
      let checkedProduct = checkedProductArray[0];
      //验证库存
      if (checkedProduct.number <= 0) {
        util.showErrorModal('没有库存');
        return false;
      }
      //验证团购是否有效
      let checkedGroupon = this.getCheckedGrouponValue();
      // 这个不用配合 wx.hideLoading();
      wx.showLoading({
        title: '加载中...',
        mask: true,
        duration: 3000
      });
      //立即购买
      util.request(api.CartFastAdd, {
        goodsId: this.data.goods.id,
        number: this.data.number,
        productId: checkedProduct.id
      }, "POST").then(function(res) {
        if (res.errno === "success") {
          // 如果storage中设置了cartId，则是立即购买，否则是购物车购买
          try {
            wx.setStorageSync('cartId', res.data);
            wx.setStorageSync('rewardLinkId', that.data.rewardLink.id);
            wx.setStorageSync('grouponLinkId', that.data.grouponLink.id);
            wx.setStorageSync('grouponRulesId', checkedGroupon.id);
            wx.navigateTo({
              url: '/pages/shopping/checkout/checkout'
            })
          } catch (e) {
            console.log(e);
          }
        } 
      });
    }
  },

  //添加到购物车
  addToCart: function() {
    var that = this;
    if (!this.data.hasLogin) {
      util.navigate("/pages/auth/login/login");
      return false;
    }
    if (this.data.openAttr == false) {
      //打开规格选择窗口
      this.setData({
        openAttr: !this.data.openAttr
      });
    } else {
      //提示选择完整规格
      if (!this.isCheckedAllSpec()) {
        util.showErrorModal('请选择完整规格');
        return false;
      }
      //根据选中的规格，判断是否有对应的sku信息
      let checkedProductArray = this.getCheckedProductItem(this.getCheckedSpecKey());
      if (!checkedProductArray || checkedProductArray.length <= 0) {
        //找不到对应的product信息，提示没有库存
        util.showErrorModal('没有库存');
        return false;
      }
      let checkedProduct = checkedProductArray[0];
      //验证库存
      if (checkedProduct.number <= 0) {
        util.showErrorModal('没有库存');
        return false;
      }

      //验证团购是否有效
      let checkedGroupon = this.getCheckedGrouponValue();
      if(checkedGroupon.checked){
        util.showErrorModal('团购不能加入购物车');
        return false;
      }

      // 防多点击，这个不用配合 wx.hideLoading();
      wx.showLoading({
        title: '加载中...',
        mask: true,
        duration: 3000
      });

      //添加到购物车
      util.request(api.CartAdd, {
        goodsId: this.data.goods.id,
        number: this.data.number,
        productId: checkedProduct.id
      }, "POST").then(function(res) {
        if (res.errno === "success") {
          wx.showToast({
            title: '添加成功'
          });
          console.log(res)
          that.setData({
            openAttr: !that.data.openAttr,
            cartGoodsCount: res.data
          });
          if (that.data.userHasCollect) {
            that.setData({
              collect: true
            });
          } else {
            that.setData({
              collect: false
            });
          }
        } 
      });
    }
  },

  cutNumber: function() {
    this.setData({
      number: (this.data.number - 1 > 1) ? this.data.number - 1 : 1
    });
  },

  addNumber: function() {
    this.setData({
      number: this.data.number + 1
    });
  },

  switchAttrPop: function() {
    if (this.data.openAttr == false) {
      this.setData({
        openAttr: !this.data.openAttr
      });
    }
  },
  closeAttr: function() {
    this.setData({
      openAttr: false,
    });
  },

  openCartPage: function() {
    wx.switchTab({
      url: '/pages/cart/cart'
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    if (options.goodId) {
      this.setData({
        goodId: options.goodId
      });
      this.getGoodsInfo();
    }
    if (options.grouponId) {
      this.setData({
        isGroupon: true,
      });
      this.getGrouponInfo(options.grouponId);
    }
    if (options.rewardId) {
      this.setData({
        isReward: true,
      });
      this.getRewardInfo(options.rewardId);
    }

    if (!options.goodId && !options.grouponId && !options.rewardId) {
      wx.navigateBack({delta: 0})
      util.showErrorModal("商品不存在")
    }
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {
    this.sharePop = this.selectComponent("#sharePop");
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    //获取用户的登录信息
    if (app.globalData.hasLogin) {
      let userInfo = wx.getStorageSync('userInfo');
      this.setData({
        userInfo: userInfo,
        hasLogin: true
      });
      //管理员登录
      if (app.globalData.Administrator) {
        this.setData({
          Administrator: true
        });
      }
    }
    var that = this;
    util.request(api.CartGoodsCount).then(function(res) {
      if (res.errno === "success") {
        that.setData({
          cartGoodsCount: res.data
        });
      }
    });
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
    let that = this;
    if (that.data.isReward) {
      return {
        title: that.data.goods.name,
        desc: '唯爱与生活不可辜负',
        path: '/pages/index/index?rewardId=' + that.data.rewardLink.id
      }
    }else {
      return {
        title: that.data.goods.name,
        desc: '唯爱与生活不可辜负',
        path: '/pages/index/index?goodsId=' + that.data.goodId
      }
    }
  }
})
