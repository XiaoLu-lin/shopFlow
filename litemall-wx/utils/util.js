const api = require('../config/api.js');
const app = getApp();

const formatTime = (time, option) => {
  const date = new Date(time)
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const week = date.getDay()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()
  
  //获取 年月日
  if (option == 'YY-MM-DD') return [year, month, day].map(formatNumber).join('-')

  //获取 年月
  if (option == 'YY-MM') return [year, month].map(formatNumber).join('-')

  //获取 年
  if (option == 'YY') return [year].map(formatNumber).toString()

  //获取 月
  if (option == 'MM') return  [mont].map(formatNumber).toString()

  //获取 日
  if (option == 'DD') return [day].map(formatNumber).toString()

  //获取 年月日 周一 至 周日
  if (option == 'YY-MM-DD Week')  return [year, month, day].map(formatNumber).join('-') + ' ' + getWeek(week)

  //获取 月日 周一 至 周日
  if (option == 'MM-DD Week')  return [month, day].map(formatNumber).join('-') + ' ' + getWeek(week)

  //获取 周一 至 周日
  if (option == 'Week')  return getWeek(week)

  //获取 时分秒
  if (option == 'hh-mm-ss') return [hour, minute, second].map(formatNumber).join(':')

  //获取 时分
  if (option == 'hh-mm') return [hour, minute].map(formatNumber).join(':')

  //获取 分秒
  if (option == 'mm-dd') return [minute, second].map(formatNumber).join(':')

  //获取 时
  if (option == 'hh')  return [hour].map(formatNumber).toString()

  //获取 分
  if (option == 'mm')  return [minute].map(formatNumber).toString()

  //获取 秒
  if (option == 'ss') return [second].map(formatNumber).toString()

  //默认 年月日 时分秒 
  return [year, month, day].map(formatNumber).join('-') + ' ' + [hour, minute, second].map(formatNumber).join(':')
}

const formatNumber = n => {
  n = n.toString()
  return n[1] ? n : '0' + n
}

const getWeek = n => {
  switch(n) {
    case 1:
      return '星期一'
    case 2:
      return '星期二'
    case 3:
      return '星期三'
    case 4:
      return '星期四'
    case 5:
      return '星期五'
    case 6:
      return '星期六'
    case 7:
      return '星期日'
  }
}

/**
 * 封装微信的的request
 */
function request(url, data = {}, method = "GET") {
  return new Promise(function(resolve, reject) {
    wx.request({
      url: url,
      data: data,
      method: method,
      header: {
        'Content-Type': 'application/json',
        'X-Litemall-TenantId': wx.getStorageSync('tenantId'),
        'X-Litemall-User-Token': wx.getStorageSync('userToken'),
        'X-Litemall-Admin-Token': wx.getStorageSync('adminToken')
      },
      success: function(res) {
        if (res.statusCode === 200) {
          if (res.data.errno === "A0223") {
            // 清除登录相关内容
            try {
              wx.removeStorageSync('adminInfo');
              wx.removeStorageSync('adminToken');
              wx.removeStorageSync('userInfo');
              wx.removeStorageSync('userToken');
            } catch (e) {
              reject(res);
            }
            //授权弹窗
            showAuthModal();
          } else {
            if (res.data.errno !== "success" && res.data.errmsg != null) {
              console.log("错误地址："+url)
              showErrorModal(res.data.errmsg);
              resolve(res.data);
            } else {
              resolve(res.data);
            }
          }
          wx.hideLoading();
        } else {
          reject(res.errMsg);
        }
      },
      fail: function(err) {
        reject(err)
      }
    })
  });
}

/**
 * 获取Appid
 */
function getAppid() {
  let accountInfo = wx.getAccountInfoSync();
   return accountInfo.miniProgram.appId;
}

function redirect(url) {
  wx.redirectTo({
    url: url
  });
}

function navigate(url) {
  wx.navigateTo({
    url: url
  });
}

function showErrorToast(msg) {
  wx.showToast({
    title: msg,
    image: '/static/images/icon_error.png',
    duration: 2000
  })
}

function showOkToast(msg) {
  wx.showToast({
    title: msg,
    icon: 'success',
    duration: 2000
  })
}

function showErrorModal(msg) {
  wx.showModal({
    title: "错误信息",
    confirmColor: '#b4282d',
    content: msg,
    showCancel: false
  });
}

/**
 * 登陆授权弹窗
 */
function showAuthModal() {
  wx.showModal({
    title: '未授权',
    content: '您还未授权登陆，登陆后享更多服务',
    cancelText: '再看看',
    confirmText: '授权登陆',
    success: res => {
      if (res.confirm) {
        wx.navigateTo({
          url: '/pages/auth/login/login'
        });
      }
    },
  })
}

//发起消息订阅
function requestSubscribe(tmplIds){
  wx.requestSubscribeMessage({
    tmplIds: [tmplIds],
    success :(res)=>{
      console.log("订阅成功："+res);
      return true;
    },
    fail :(err) =>{
      console.log("订阅失败："+err);
      return false;
    },
    complete:(errMsg)=>{
      console.log("订阅完成："+errMsg);
    }
  });
}

module.exports = {
  formatTime,
  request,
  redirect,
  navigate,
  showErrorToast,
  requestSubscribe,
  showOkToast,
  showErrorModal,
  showAuthModal,
  getAppid,
};