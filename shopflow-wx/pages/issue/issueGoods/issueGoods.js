// pages/addGoods/addGoods.js
const util = require('../../../utils/util');
const datePicker = require('../../../utils/DatePicker');
const api = require('../../../config/api.js');

var app = getApp();


Page({

  /**
   * 页面的初始数据
   */
  data: {
    initGoods: {},
    Administrator : false,
    imageHint: '',
    brand : {},
    index: '',  //下标
    modalName: '',//打开弹窗
    multiArray:[],//获取分类
    categoryIds: [],//分类ids
    multiIndex: [0, 0],//获取分类
    categoryList: [],//分类列表
    goods: {
      id: null, 
      picUrl: '', 
      gallery: [], 
      isHot: false, 
      isNew: true, 
      status: 0, 
      address: "", 
      isGroupon: false, 
      isTakeTheir: false, 
      unit: ''
    },//商品信息
    grouponRules: {discount: 0.00, discountMember: 2, expireTime: ''},
    specForm: { specification: '', value: '', picUrl: '' },
    specifications: [{ specification: '规格', value: '标准', picUrl: '' }],//规格列表默认标准
    products: [{ id: 0, specifications: ['标准'], price: 0.00, number: 1, url: '' }],//货品列表和规格列表联动
    attributes: [],//商品参数列表
    picker: ['待审核', '已上架', '已下架','已驳回'],
  },

  //定义价格
  bindCounterPriceInput: function(e) {
    this.setData({
      'products[0].price': e.detail.value / 2
    });
  },
  
  //打开弹窗
  showModal(e) {
    let that = this;
    let imageHint = "";
    let modalName = e.currentTarget.dataset.target;
    let index = e.currentTarget.dataset.index;
    
    if(modalName === "specImageHint"){
      modalName = "imageHint"
      imageHint = "/static/images/specImageHint.png";
    }
    if(modalName === "productsImageHint"){
      modalName = "imageHint"
      imageHint = "/static/images/productsImageHint.png";
    }
    if(modalName === "attributeImageHint"){
      modalName = "imageHint"
      imageHint = "/static/images/attributeImageHint.png";
    }

    that.setData({
      modalName: modalName,
      imageHint: imageHint,
      index : index,
    });

  },

  PickerChange(e) {
    this.setData({
      'goods.status': e.detail.value
    })
  },

  //关闭弹窗
  hideModal(e) {
    this.setData({
      modalName: null,
    })
  },

  //团购结束日期选择
  getGrouponExpireTime(e){
    this.setData({
      'grouponRules.expireTime': datePicker.formatTime(e.detail.time)
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
      'goods.isGroupon': !that.data.goods.isGroupon,
      'grouponRules.expireTime': util.formatTime(new Date()),
    })
  },

  //判断是否自提
  buidisTakeTheir(e){
    let that = this;
    that.setData({
      'goods.address': that.data.brand.address,
      'goods.isTakeTheir': !that.data.goods.isTakeTheir,
    })
  },

  //提交规格信息
  submitSpec(e){
    let that = this;
    let specForm = that.data.specForm
    specForm.value = e.detail.value.value
    specForm.specification = e.detail.value.specification
    if(!specForm.specification || !specForm.value || !specForm.picUrl){
      util.showErrorModal("参数不完整");
      return;
    }

    for (var i = 0; i < that.data.specifications.length; i++) {
      const v = that.data.specifications[i]
      if (v.specification === specForm.specification) {
        if (v.value === specForm.value) {
          util.showErrorModal('已经存在规格值:' + v.value);
          return
        }
      }
    }

    that.data.specifications.push(specForm);
    that.setData({
      modalName: null,
      specForm: { specification: '', value: '', picUrl: '' },
      specifications: that.data.specifications,
    })

    this.specToProduct();
  },

  /**
   * 规格货品规则
   */
  specToProduct() {
    let that = this;
    if (that.data.specifications.length === 0) {
      return
    }
    // 根据specifications创建临时规格列表
    var specValues = [];
    var spec = that.data.specifications[0].specification;
    var values = [];
    values.push(0);

    for (var i = 1; i < that.data.specifications.length; i++) {
      const aspec = that.data.specifications[i].specification
      if (aspec === spec) {
        values.push(i)
      } else {
        specValues.push(values)
        spec = aspec
        values = []
        values.push(i)
      }
    }
    specValues.push(values)

    // 根据临时规格列表生产货品规格
    // 算法基于 https://blog.csdn.net/tyhj_sf/article/details/53893125
    var productsIndex = 0
    var products = []
    var combination = []
    var n = specValues.length
    for (var s = 0; s < n; s++) {
      combination[s] = 0
    }
    var index = 0
    var isContinue = false
    do {
      var specifications = []
      for (var x = 0; x < n; x++) {
        var z = specValues[x][combination[x]]
        specifications.push(that.data.specifications[z].value)
      }
      products[productsIndex] = { id: productsIndex, specifications: specifications, price: 0.00, number: 0, url: '' }
      productsIndex++

      index++
      combination[n - 1] = index
      for (var j = n - 1; j >= 0; j--) {
        if (combination[j] >= specValues[j].length) {
          combination[j] = 0
          index = 0
          if (j - 1 >= 0) {
            combination[j - 1] = combination[j - 1] + 1
          }
        }
      }
      isContinue = false
      for (var p = 0; p < n; p++) {
        if (combination[p] !== 0) {
          isContinue = true
        }
      }
    } while (isContinue)

    that.setData({
      products: products
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
    let initGoods = that.data.initGoods;
    product.number = parseInt(e.detail.value.number);
    product.price = parseFloat(e.detail.value.price);

    if (!product.price ||product.price < initGoods.minAmount || product.price > initGoods.maxAmount) {
      util.showErrorModal("货品售价 "+initGoods.minAmount+" ~ "+initGoods.maxAmount+" 元");
      return;
    }

    if(!product.number || product.number < 0 || !product.url){
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
          that.setData({
            specifications: that.data.specifications,
          })
          that.specToProduct();
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
    goods.isHot = !!e.detail.value.isHot;
    goods.isNew = !!e.detail.value.isNew;
    goods.isTakeTheir = !!e.detail.value.isTakeTheir;
    goods.remarks = e.detail.value.remarks;
    goods.address = e.detail.value.address;
    goods.unit = e.detail.value.unit;
    goods.brandId = that.data.brand.id;
    goods.categoryId = that.data.categoryList[multiIndex[0]].children[multiIndex[1]].value;
    if (!that.data.Administrator) {
      goods.status = 0
    }

    console.log(goods);

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
      if(!element.specification || !element.value){
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
                  wx.navigateBack({delta: 0})
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
              util.requestSubscribe(api.NewOrderTmplIds);
              wx.showToast({
                title: '发布成功！',
                icon: 'success',
                duration: 2000,
                complete: function() {
                  wx.navigateBack({delta: 0})
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
                  wx.navigateBack({delta: 0})
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
      console.log(res)
      if (res.errno === "success") {
        that.setData({
          goods: res.data.goods,
          attributes: res.data.attributes,
          products: res.data.products,
          specifications: res.data.specifications,
          categoryIds: res.data.categoryIds,
          grouponRules : res.data.grouponRules ? res.data.grouponRules : that.data.grouponRules,
          "brand.id": res.data.goods.brandId,
        })
      }
      wx.hideLoading();
    });
  },

  getBrandRead(brandId){
    let that = this;
    util.request(api.BrandRead,{
      brandId: brandId,
    }).then(function(res) {
      console.log(res);
      if (res.errno === "success") {
        that.setData({
          brand: res.data,
        })
      }
    });
  },

  goodsInit(e){
    let that = this;
    util.request(api.BrandGoodsInit).then(function(res) {
      console.log(res)
      if (res.errno === "success") {
        that.setData({
          initGoods: res.data,
        })
      }
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    var that = this;
    console.log(options);
    if(options.brandId){
      that.getBrandRead(options.brandId);
    }
    if(options.goodId){
      that.setData({
        'goods.id': options.goodId,
      });
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
    let that = this;
    that.setData({
      Administrator : app.globalData.Administrator
    })
    that.goodsInit();
    that.getCategoryList();
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