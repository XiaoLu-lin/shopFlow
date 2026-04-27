/**
 * 用户相关服务
 */
const util = require('../utils/util.js');
const api = require('../config/api.js');


/**
 * Promise封装wx.checkSession
 */
function checkSession() {
  return new Promise(function(resolve, reject) {
    wx.checkSession({
      success: function() {
        resolve(true);
      },
      fail: function() {
        reject(false);
      }
    })
  });
}

/**
 * Promise封装wx.login
 */
function login() {
  return new Promise(function(resolve, reject) {
    wx.login({
      success: function(res) {
        if (res.code) {
          resolve(res);
        } else {
          reject(res);
        }
      },
      fail: function(err) {
        reject(err);
      }
    });
  });
}

/**
 * 调用微信登录
 */
function loginByWeixin(e) {
  return new Promise(function(resolve, reject) {
    return login().then((res) => {
      wx.getUserInfo({
        success: (userinfo) => {
          util.request(api.AuthLoginByWeixin, {
            wxCode: res.code,
            phoneCode: e ? e.detail.code : "",
            inviter: wx.getStorageSync('inviter'),
            userInfo: userinfo.userInfo,
          }, 'POST').then(res => {
            if (res.errno === "success") {
              wx.setStorageSync('userInfo', res.data.userInfo);
              wx.setStorageSync('userToken', res.data.userToken);
              resolve(res);
            } else {
              reject(res);
            }
          }).catch((err) => {
            reject(err);
          });
        }
      })
    }).catch((err) => {
      reject(err);
    })
  });
}

/**
 * 调用账号登录
 */
function loginByAccount(username,password,code) {
  return new Promise(function(resolve, reject) {
    const prepareLogin = api.UseWxLoginForAccountLogin ? login() : Promise.resolve();
    const payload = {
      username: username,
      password: password,
    };
    if (code) {
      payload.code = code;
    }
    prepareLogin.then(() => {
      util.request(api.AuthLoginByAccount, payload, 'POST').then(res => {
        if (res.errno === "success") {
          wx.setStorageSync('adminInfo', res.data.adminInfo);
          wx.setStorageSync('adminToken', res.data.adminToken);
          if (res.data.userInfo && res.data.userToken) {
            wx.setStorageSync('userInfo', res.data.userInfo);
            wx.setStorageSync('userToken', res.data.userToken);
          }
          resolve(res);
        } else {
          reject(res);
        }
      }).catch((err) => {
        reject(err);
      });
    }).catch((err) => {
      reject(err);
    });
  });
}


/**
 * 判断用户是否登录
 */
function checkLogin() {
  return new Promise(function(resolve, reject) {
    if (wx.getStorageSync('userInfo') && wx.getStorageSync('userToken')) {
      checkSession().then(() => {
        resolve(true);
      }).catch(() => {
        reject(false);
      });
    } else {
      reject(false);
    }
  });
}

/**
 * 判断管理员是否登录
 */
function checkAdminLogin() {
  return new Promise(function(resolve, reject) {
    if (wx.getStorageSync('adminInfo') && wx.getStorageSync('adminToken')) {
      checkSession().then(() => {
        resolve(true);
      }).catch(() => {
        reject(false);
      });
    } else {
      reject(false);
    }
  });
}

module.exports = {
  loginByWeixin,
  checkLogin,
  loginByAccount,
  checkAdminLogin,
};
