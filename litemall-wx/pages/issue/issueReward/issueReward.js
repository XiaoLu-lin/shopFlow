// pages/addGoods/addGoods.js
const util = require('../../../utils/util');
const api = require('../../../config/api.js');

var app = getApp();


Page({

  /**
   * 页面的初始数据
   */
  data: {
    brand : {},
    index: '',  //下标
    modalName: '',//打开弹窗
    multiArray:[],//获取分类
    categoryIds: [],//分类ids
    multiIndex: [0, 0],//获取分类
    categoryList: [],//分类列表
    goods: {id: 0, picUrl: '', gallery: [], isHot: false, isNew: true, isOnSale: false, isGroupon: false, unit: ''},//商品信息
    grouponRules: {discount: 0.00, discountMember: 2, expireTime: ''},
    specForm: { specification: '', value: '', picUrl: '' },
    specifications: [{ specification: '规格', value: '标准', picUrl: '' }],//规格列表默认标准
    products: [{ id: 0, specifications: ['标准'], price: 0.00, number: 0, url: '' }],//货品列表和规格列表联动
    attributes: [],//商品参数列表
  },

  
  //打开弹窗
  showModal(e) {
    this.setData({
      modalName: e.currentTarget.dataset.target,
      index : e.currentTarget.dataset.index
    })
  },

  //关闭弹窗
  hideModal(e) {
    this.setData({
      modalName: null,
    })
  },

  //团购结束日期选择
  DateChange(e) {
    this.setData({
      'grouponRules.expireTime': e.detail.value+" 00:00:00"
    })
  },

  //保存规格图片
  specFormImage(e){
    let that = this;
    that.data.specForm.picUrl = e.detail.picUrls[0];
    this.setData({
      specForm: that.data.specForm,
    })
  },

  //判断是否打开团购
  buidGroupon(e){
    let that = this;
    that.setData({
      'goods.isGroupon': !that.data.goods.isGroupon
    })
  },

  //提交规格信息
  submitSpec(e){
    let that = this;
    let specForm = that.data.specForm;
    specForm.specification = e.detail.value.specification;
    specForm.value = e.detail.value.value;
    if(!specForm.specification || !specForm.value || !specForm.picUrl){
      util.showErrorModal("参数不完整");
      return;
    }
    let productForm = {
      id: 0, specifications: [specForm.specification],
      price: 0.00, number: 0, url: specForm.picUrl,
    }
    that.data.specifications.push(specForm);
    that.data.products.push(productForm);
    that.setData({
      modalName: null,
      products: that.data.products,
      specifications: that.data.specifications,
    })
  },

  //保存货品图片
  productsImage(e){
    let that = this;
    let index = that.data.index;
    that.data.products[index].url = e.detail.picUrls[0];
    this.setData({
      products: that.data.products,
    })
  },

  //提交团购信息
  submitGrouponRules(e){
    let that = this;
    let grouponRules = that.data.grouponRules;
    grouponRules.discount = parseFloat(e.detail.value.discount);
    grouponRules.discountMember = parseInt(e.detail.value.discountMember);
    if(!grouponRules.discount || !grouponRules.discountMember || !grouponRules.expireTime){
      util.showErrorModal("参数不正确");
      return;
    }
    grouponRules.expireTime = grouponRules.expireTime
    that.setData({
      modalName: null,
      grouponRules: grouponRules
    })
  },

  //提交货品信息
  submitProduct(e){
    let that = this;
    let index = that.data.index;
    let product = that.data.products[index];
    product.number = parseInt(e.detail.value.number);
    product.price = parseFloat(e.detail.value.price);
    if(!product.number|| !product.price || product.number<0 || product.price<0 || !product.url){
      util.showErrorModal("参数不正确");
      return;
    }
    that.data.products[index] = product;
    that.setData({
      products : that.data.products,
      modalName: null,
    })
  },

  //提交商品参数
  submitAttribute(e){
    let that = this;
    let attributeForm = {
      attribute: e.detail.value.attribute, 
      value: e.detail.value.value, 
    }
    if(!attributeForm.attribute || !attributeForm.value){
      util.showErrorModal("参数不完整");
      return;
    }
    that.data.attributes.push(attributeForm);
    that.setData({
      attributes : that.data.attributes,
      modalName: null,
    })

  },

  goodPicImage(e){
    if(e.detail.picUrls[0]){
      this.setData({
        'goods.picUrl': e.detail.picUrls[0],
        'products[0].url': e.detail.picUrls[0],
        'specifications[0].picUrl': e.detail.picUrls[0],
        
      })
    }else{
      this.setData({
        'goods.picUrl': '',
        'products[0].url': '',
        'specifications[0].picUrl': '',
      })
    }
  },

  goodGalleryImage(e){
    this.setData({
      'goods.gallery': e.detail.picUrls
    })
  },

  getCategoryList(e){
    let that = this;
    util.request(api.BrandGoodsCategory).then(function(res) {
      let l1Array = [];
      let l2Array = [];
      let multiArray = [];
      let categoryList = res.data.categoryList;
      
      categoryList.forEach(item => {
        let catObj = {
          value: item.value,
          label: item.label
        }
        l1Array.push(catObj);
        l2Array.push(item.children);
      })
      multiArray[0] = l1Array;
      multiArray[1] = l2Array[0];


      let categoryIds = that.data.categoryIds;
      let multiIndex = that.data.multiIndex;
      if(that.data.goods.id){
        categoryList.forEach((item,index) => {
          if(item.value == categoryIds[0]){
            multiIndex[0] = index;
            item.children.forEach((item,index) => {
              if(item.value == categoryIds[1]){
                multiIndex[1] = index;
                return;
              }
            });
            return;
          }
        });
      }
      
      that.setData({
        categoryList: categoryList,
        multiArray: multiArray,
        multiIndex: multiIndex,
      })
    });
  },

  MultiChange(e) {
    this.setData({
      multiIndex: e.detail.value
    })
  },

  MultiColumnChange(e) {
    let that = this;
    let data = {
      multiArray: that.data.multiArray,
      multiIndex: that.data.multiIndex
    };
    data.multiIndex[e.detail.column] = e.detail.value;
    switch (e.detail.column) {
      case 0:
        data.multiArray[1] = that.data.categoryList[e.detail.value].children;
        data.multiIndex[1] = 0;
        break;
    }
    that.setData(data);
  },

  //删除规格
  specDelete(e){
    let that = this;
    wx.showModal({
      title: '删除规格',
      content: '确定要删除吗？',
      cancelText: '再看看',
      confirmText: '删除',
      success: res => {
        if (res.confirm) {
          that.data.specifications.splice(e.target.dataset.index,1)
          that.data.products.splice(e.target.dataset.index,1)
          that.setData({
            specifications: that.data.specifications,
            products: that.data.products
          })
        }
      }
    })
  },

  //删除参数
  attrDelete(e){
    let that = this;
    wx.showModal({
      title: '删除参数',
      content: '确定要删除吗？',
      cancelText: '再看看',
      confirmText: '删除',
      success: res => {
        if (res.confirm) {
          that.data.attributes.splice(e.target.dataset.index,1)
          that.setData({
            attributes: that.data.attributes,
          })
        }
      }
    })
  },


  submitGoods(e){
    let that = this;
    let goods = that.data.goods;
    let products = that.data.products;
    let attributes = that.data.attributes;
    let multiIndex = that.data.multiIndex;
    let grouponRules = that.data.grouponRules;
    let specifications = that.data.specifications;
    
    goods.name = e.detail.value.name;
    goods.brief = e.detail.value.brief;
    goods.counterPrice = parseFloat(e.detail.value.counterPrice);
    goods.goodsSn = e.detail.value.goodsSn;
    goods.isHot = e.detail.value.isHot;
    goods.isNew = e.detail.value.isNew;
    goods.unit = e.detail.value.unit;
    goods.brandId = that.data.brand.id;
    goods.categoryId = that.data.categoryList[multiIndex[0]].children[multiIndex[1]].value;
    
    if(!goods.name||!goods.brief||!goods.counterPrice||!goods.goodsSn){
      util.showErrorModal("商品信息错误");
      return;
    }
    if(!goods.picUrl||goods.gallery.length<=0){
      util.showErrorModal("图片未上传");
      return;
    }
    if(specifications.length <= 0){
      util.showErrorModal("规格至少有一条");
        return;
    }
    if (goods.isGroupon) {
      if(!grouponRules.discount || !grouponRules.discountMember || !grouponRules.expireTime){
        util.showErrorModal("团购参数不正确");
        return;
      }
    }
    for (const element of specifications) {
      if(!element.specification || !element.value || !element.picUrl){
        util.showErrorModal("规格不完整");
        return;
      }
    }
    for (const element of products) {
      if(element.number<=0 || element.price<=0 || !element.url){
        util.showErrorModal("货品参数不正确");
        return;
      }
    }
    for (const element of attributes) {
      if(!element.attribute || !element.value){
        util.showErrorModal("商品参数不完整");
        return;
      }
    }

    if(goods.id){
      that.goodsUpdate(goods,products,attributes,specifications,grouponRules);
    }else{
      that.goodsCreate(goods,products,attributes,specifications,grouponRules);
    }
  },

  //删除店铺商品
  goodsDelete(e){
    let that = this;
    wx.showModal({
      title: '删除商品',
      content: '确定要删除吗？',
      cancelText: '再看看',
      confirmText: '删除',
      success: res => {
        if (res.confirm) {
          wx.showLoading({
            title: '加载中',
          });
          util.request(api.BrandGoodsDelete, {
            id: that.data.goods.id,
          },'POST').then(function(res) {
            console.log(res);
            if (res.errno === "success") {
              wx.showToast({
                title: '删除成功！',
                icon: 'success',
                duration: 2000,
                complete: function() {
                  wx.reLaunch({
                    url: '/pages/issue/issueSkip/issueSkip',
                  })
                }
              });
            }
            wx.hideLoading();
          });
        }
      }
    })
  },

  //添加店铺商品
  goodsCreate(goods,products,attributes,specifications,grouponRules){
    //新订单消息订阅
    util.requestSubscribe(api.NewOrderTmplIds);
    wx.showModal({
      title: '发布商品',
      content: '确定要发布吗？',
      cancelText: '再看看',
      confirmText: '发布',
      success: res => {
        if (res.confirm) {
          wx.showLoading({
            title: '加载中',
          });
          util.request(api.BrandGoodsCreate, {
            goods: goods,
            products: products,
            attributes: attributes,
            grouponRules: grouponRules,
            specifications: specifications,
          },'POST').then(function(res) {
            console.log(res);
            if (res.errno === "success") {
              wx.showToast({
                title: '发布成功！',
                icon: 'success',
                duration: 2000,
                complete: function() {
                  wx.reLaunch({
                    url: '/pages/issue/issueSkip/issueSkip',
                  })
                }
              });
            }
            wx.hideLoading();
          });
        }
      }
    })
  },

  //更新店铺商品
  goodsUpdate(goods,products,attributes,specifications,grouponRules){
    wx.showModal({
      title: '更新商品',
      content: '确定要更新吗？',
      cancelText: '再看看',
      confirmText: '更新',
      success: res => {
        if (res.confirm) {
          wx.showLoading({
            title: '加载中',
          });
          util.request(api.BrandGoodsUpdate, {
            goods: goods,
            products: products,
            attributes: attributes,
            grouponRules: grouponRules,
            specifications: specifications,
          },'POST').then(function(res) {
            console.log(res);
            if (res.errno === "success") {
              wx.showToast({
                title: '更新成功！',
                icon: 'success',
                duration: 2000,
                complete: function() {
                  wx.reLaunch({
                    url: '/pages/issue/issueSkip/issueSkip',
                  })
                }
              });
            }
            wx.hideLoading();
          });
        }
      }
    })
  },

  goodsDetail(e){
    let that = this;
    wx.showLoading({
      title: '加载中...',
      mask: true,
      duration: 3000
    });
    util.request(api.BrandGoodsDetail, {
      id: that.data.goods.id,
    }).then(function(res) {
      if (res.errno === "success") {
        that.setData({
          goods: res.data.goods,
          attributes: res.data.attributes,
          products: res.data.products,
          specifications: res.data.specifications,
          categoryIds: res.data.categoryIds,
          grouponRules : res.data.grouponRules ? res.data.grouponRules : that.data.grouponRules,
          'brand.id': res.data.goods.brandId,
        })
      }
      wx.hideLoading();
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    var that = this;
    //获取当前日期
    let date = new Date();   

    that.setData({
      'goods.id': options.id,
      'brand.id': options.brandId,
      'brand.name': options.brandName,
      'grouponRules.expireTime':  util.formatTime(date, 'YY-MM-DD')+" 00:00:00"
    });
    
    if(options.id){
      that.goodsDetail();
    }
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
    this.getCategoryList();
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

  }
})