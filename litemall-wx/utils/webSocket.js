
const api = require('../config/api.js');
const util = require('./util.js');
 
//定时标识
let timing = false
//定时器循环时间，注意不要超过1分钟  
let IntervalTime = 30000

/**
 * 连接webSocket
 * @param {*} receiveUserId 接收用户
 * @param {*} func 方法
 */
function connect(func) {
  //建立连接
  wx.connectSocket({
    url: api.WSS_SERVER_URL +"/"+ wx.getStorageSync('userToken'),
    header: { 
      'content-type': 'application/json',
    },
    success: function () {
      console.log('websocket连接成功~')
    },
    fail: function () {
      console.log('websocket连接失败~')
    }
  })
  
  //连接成功
  wx.onSocketOpen(function (res) {
    util.showOkToast("连接成功");
    //接受服务器消息 ，func回调可以拿到服务器返回的数据
    wx.onSocketMessage(func);
    //启动心跳包
    linkWebsocketXin(IntervalTime, true)
  });
 
  //连接失败
  wx.onSocketError(function (res) {
    util.showErrorToast("连接失败");
    //关闭心跳包定时器
    linkWebsocketXin(IntervalTime, false)
  })
}

//心跳包
function linkWebsocketXin(time, status) {
  if (status == true) {
    timing = setInterval(function () {
      console.log("发送心跳");
      //循环执行代码
      wx.sendSocketMessage({
        data: JSON.stringify({
          token : wx.getStorageSync('userToken'),
        }),
        fail(res) {
          //发送失败关闭心跳
          clearInterval(timing);
        }
      });
    }, time) //time循环时间，注意不要超过1分钟  
  } else {
    //关闭定时器
    clearInterval(timing);
  }
}

//发送消息
function send(data = {}) {
  //关闭心跳包定时器
  linkWebsocketXin(IntervalTime, false)
  wx.sendSocketMessage({
    data: JSON.stringify(data),
    success:res=>{
      //重启心跳包
      linkWebsocketXin(IntervalTime, true)
    },
    fail(res) {
      util.showErrorToast("发送失败");
    }
  });
}

//关闭连接
function close() {
  //关闭心跳包定时器
  linkWebsocketXin(IntervalTime, false)
  //关闭连接
  wx.closeSocket()
}

module.exports = {
  send: send,
  close: close,
  connect: connect,
  linkWebsocketXin: linkWebsocketXin
}
