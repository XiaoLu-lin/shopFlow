const util = require('../../utils/util.js');
const api = require('../../config/api.js');
const user = require('../../utils/user.js');

var app = getApp();


Page({

  /**
   * 页面的初始数据
   */
  data: {
    StatusBar: app.globalData.StatusBar,
    CustomBar: app.globalData.CustomBar,
    Custom: app.globalData.Custom,
    hasLogin: false,
    Administrator: false,
    userInfo: {},
    goods: {},
    cartGoods: [],
    cartTotal: {
      "goodsCount": 0,
      "goodsAmount": 0.00,
      "checkedGoodsCount": 0,
      "checkedGoodsAmount": 0.00
    },
    isEditCart: false,
    checkedAllStatus: true,
    editCartList: [],
    openAttr: false,//规格打开判断
    checkedSpecText: '请选择规格数量',
    goodsIndex:0,
    tmpSpecText:"请选择规格数量",
    checkedSpecPrice: 0,//规格价格
    checkedNumber:0,//规格商品数量
    productList: [],
    specificationList: [],//规格列表
    checkedSpecPicUrl:'',//规格默认图片
  },

  getCartList: function() {
    if(!app.globalData.hasLogin){
      return;
    }
    wx.showLoading({
      title: '加载中...',
      mask: true,
      duration: 3000
    });
    let that = this;
    util.request(api.CartList).then(function(res) {
      if (res.errno === "success") {
        that.setData({
          cartGoods: res.data.cartList,
          cartTotal: res.data.cartTotal
        });
        that.setData({
          checkedAllStatus: that.isCheckedAll()
        });
        wx.hideLoading();
      }
    });
  },

  //关闭规格弹窗
  closeAttr: function() {
    this.setData({
      openAttr: false,
    });
  },

  //规格选择弹出
  switchAttrPop: function(event) {
    let that = this;
    if (this.data.openAttr == false) {
      let itemIndex = event.target.dataset.itemIndex;
      let cartItem = that.data.cartGoods[itemIndex];
      util.request(api.GoodsDetail, {
        goodId: cartItem.goodsId
      }).then(function(res) {
        if (res.errno === "success") {
          let _specificationList = res.data.specificationList;
          for(var i = 0; i < _specificationList.length; i++){
            for(var j = 0; j < _specificationList[i].valueList.length; j++){
              if(cartItem.specifications == _specificationList[i].valueList[j].value){
                _specificationList[i].valueList[j].checked = true
              }
            }
          }
          that.setData({
            openAttr: !that.data.openAttr,
            goods: res.data.info,
            productList: res.data.productList,
            checkedNumber: cartItem.number,
            checkedSpecPrice:cartItem.price,
            checkedSpecPicUrl:cartItem.picUrl,
            specificationList: res.data.specificationList,
            checkedSpecText: cartItem.specifications,
            tmpSpecText: cartItem.specifications,
            goodsIndex: itemIndex,
          });
        }
      }); 
    }
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
    //重新计算哪些值不可以点击
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
        tmpSpecText: checkedValue.join(' ')
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

  isCheckedAll: function() {
    //判断购物车商品已全选
    return this.data.cartGoods.every(function(element, index, array) {
      if (element.checked == true) {
        return true;
      } else {
        return false;
      }
    });
  },

  doCheckedAll: function() {
    this.setData({
      checkedAllStatus: this.isCheckedAll()
    });
  },

  checkedItem: function(event) {
    let itemIndex = event.target.dataset.itemIndex;
    let that = this;
    let productIds = [];
    productIds.push(that.data.cartGoods[itemIndex].productId);
    if (!this.data.isEditCart) {
      util.request(api.CartChecked, {
        productIds: productIds,
        isChecked: that.data.cartGoods[itemIndex].checked ? 0 : 1
      }, 'POST').then(function(res) {
        if (res.errno === "success") {
          that.setData({
            cartGoods: res.data.cartList,
            cartTotal: res.data.cartTotal
          });
        }
        that.setData({
          checkedAllStatus: that.isCheckedAll()
        });
      });
    } else {
      //编辑状态
      let tmpCartData = this.data.cartGoods.map(function(element, index, array) {
        if (index == itemIndex) {
          element.checked = !element.checked;
        }
        return element;
      });
      that.setData({
        cartGoods: tmpCartData,
        checkedAllStatus: that.isCheckedAll(),
        'cartTotal.checkedGoodsCount': that.getCheckedGoodsCount()
      });
    }
  },

  getCheckedGoodsCount: function() {
    let checkedGoodsCount = 0;
    this.data.cartGoods.forEach(function(v) {
      if (v.checked === true) {
        checkedGoodsCount += v.number;
      }
    });
    return checkedGoodsCount;
  },

  checkedAll: function() {
    let that = this;
    if (!this.data.isEditCart) {
      var productIds = this.data.cartGoods.map(function(v) {
        return v.productId;
      });
      util.request(api.CartChecked, {
        productIds: productIds,
        isChecked: that.isCheckedAll() ? 0 : 1
      }, 'POST').then(function(res) {
        if (res.errno === "success") {
          that.setData({
            cartGoods: res.data.cartList,
            cartTotal: res.data.cartTotal
          });
        }
        that.setData({
          checkedAllStatus: that.isCheckedAll()
        });
      });
    } else {
      //编辑状态
      let checkedAllStatus = that.isCheckedAll();
      let tmpCartData = this.data.cartGoods.map(function(v) {
        v.checked = !checkedAllStatus;
        return v;
      });
      that.setData({
        cartGoods: tmpCartData,
        checkedAllStatus: that.isCheckedAll(),
        'cartTotal.checkedGoodsCount': that.getCheckedGoodsCount()
      });
    }

  },
  
  editCart: function() {
    var that = this;
    if (this.data.isEditCart) {
      this.getCartList();
      this.setData({
        isEditCart: !this.data.isEditCart
      });
    } else {
      //编辑状态
      let tmpCartList = this.data.cartGoods.map(function(v) {
        v.checked = false;
        return v;
      });
      this.setData({
        editCartList: this.data.cartGoods,
        cartGoods: tmpCartList,
        isEditCart: !this.data.isEditCart,
        checkedAllStatus: that.isCheckedAll(),
        'cartTotal.checkedGoodsCount': that.getCheckedGoodsCount()
      });
    }
  },

  submitAttr: function(event) {
    let that = this;
    if(that.isCheckedAllSpec()){
      let itemIndex = event.target.dataset.itemIndex;
      let cartItem = that.data.cartGoods[itemIndex];
      cartItem.number = that.data.checkedNumber;
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
      that.updateCart(checkedProduct.id, cartItem.goodsId, cartItem.number, cartItem.id);
      cartItem.price = checkedProduct.price;
      cartItem.specifications = checkedProduct.specifications;
      cartItem.picUrl = checkedProduct.url;
      //关闭规格
      that.closeAttr();
    }else{
      util.showErrorModal("请选择完整规格");
    }
  },

  updateCart: function(productId, goodsId, number, id) {
    let that = this;
    util.request(api.CartUpdate, {
      productId: productId,
      goodsId: goodsId,
      number: number,
      id: id
    }, 'POST').then(function(res) {
      if(res.errno === "success"){
        that.setData({
          checkedAllStatus: that.isCheckedAll(),
          cartGoods: that.data.cartGoods,
          checkedNumber:number
        });
      }
    });
  },

  cutNumber: function(event) {
    let that = this;
    let itemIndex = event.target.dataset.itemIndex;
    let cartItem = that.data.cartGoods[itemIndex];
    let number = (cartItem.number - 1 > 1) ? cartItem.number - 1 : 1;
    that.updateCart(cartItem.productId, cartItem.goodsId, number, cartItem.id);
    cartItem.number = number;
    var productIds = this.data.cartGoods.map(function(v) {
      return v.productId;
    });
    util.request(api.CartChecked, {
      productIds: productIds,
      isChecked: 1
    }, 'POST').then(function(res) {
      if (res.errno === "success") {
        that.setData({
          cartGoods: res.data.cartList,
          cartTotal: res.data.cartTotal
        });
      }
    });
  },

  addNumber: function(event) {
    let that = this;
    let itemIndex = event.target.dataset.itemIndex;
    let cartItem = that.data.cartGoods[itemIndex];
    let number = cartItem.number + 1;
    that.updateCart(cartItem.productId, cartItem.goodsId, number, cartItem.id);
    cartItem.number = number;
    var productIds = this.data.cartGoods.map(function(v) {
      return v.productId;
    });
    util.request(api.CartChecked, {
      productIds: productIds,
      isChecked: 1
    }, 'POST').then(function(res) {
      if (res.errno === "success") {
        that.setData({
          cartGoods: res.data.cartList,
          cartTotal: res.data.cartTotal
        });
      }
    });
  },

  checkoutOrder: function() {
    //获取已选择的商品
    let that = this;
    var checkedGoods = this.data.cartGoods.filter(function(element, index, array) {
      if (element.checked == true) {
        return true;
      } else {
        return false;
      }
    });
    if (checkedGoods.length <= 0) {
      return false;
    }
    // storage中设置了cartId，则是购物车购买
    try {
      wx.setStorageSync('cartId', 0);
      wx.navigateTo({
        url: '../shopping/checkout/checkout'
      })
    } catch (e) {}

  },

  deleteCart: function() {
    //获取已选择的商品
    let that = this;
    let productIds = this.data.cartGoods.filter(function(element, index, array) {
      if (element.checked == true) {
        return true;
      } else {
        return false;
      }
    });
    if (productIds.length <= 0) {
      return false;
    }
    productIds = productIds.map(function(element, index, array) {
      if (element.checked == true) {
        return element.productId;
      }
    });
    util.request(api.CartDelete, {
      productIds: productIds
    }, 'POST').then(function(res) {
      if (res.errno === "success") {
        let cartList = res.data.cartList.map(v => {
          v.checked = false;
          return v;
        });
        that.setData({
          cartGoods: cartList,
          cartTotal: res.data.cartTotal
        });
      }
      that.setData({
        checkedAllStatus: that.isCheckedAll()
      });
    });
  },

  goLogin() {
    wx.navigateTo({
      url: "/pages/auth/login/login"
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    
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
    //获取用户的登录信息
    if (app.globalData.hasLogin) {
      let userInfo = wx.getStorageSync('userInfo');
      this.setData({
        userInfo: userInfo,
        hasLogin: true,
        isEditCart:false
      });
      //管理员登录
      if (app.globalData.Administrator) {
        this.setData({
          Administrator: true
        });
      }
    }

    //自定义底部导航栏高亮显示不加会导致高亮随机跳
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({
        selected: 3
      })
    }

    this.getCartList();
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
    this.getCartList();
    wx.hideNavigationBarLoading() //完成停止加载
    wx.stopPullDownRefresh() //停止下拉刷新
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