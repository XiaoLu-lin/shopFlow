// lib/components/commentModal/commentModal.js
var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');
var app = getApp();


Component({
  /**
   * 组件的属性列表
   */
  properties: {
    valueId: { //图片张数
      type: String, // 类型（必填），目前接受的类型包括：String, Number, Boolean, Object, Array, null（表示任意类型）
      value: '' // 属性初始值（可选），如果未指定则会根据类型选择一个
    },
    commentType: { //图片张数
      type: Number, // 类型（必填），目前接受的类型包括：String, Number, Boolean, Object, Array, null（表示任意类型）
      value: '' // 属性初始值（可选），如果未指定则会根据类型选择一个
    },
  },

  /**
   * 组件的初始数据
   */
  data: {
    clickScroll: false,
    commentCount: 0,
    tmpValueId: 0,
    commentId: 0,
    replyUserId: 0,
    nickName: '',
    showComment: false,
    commentImage: [],
    content: '',
    textarea: false,
    addImage: false,
    InputBottom: 0,
    commentList: [],
    page: 1,
    limit: 10,
    totalPages: 1,
  },

  /**
   * 组件的方法列表
   */
  methods: {

    //点赞
    likeSubmit(e){
      let that = this;
      let index = e.target.dataset.index;
      let rindex = e.target.dataset.rindex;
      let valueId = e.target.dataset.valueid;
      util.request(api.LikeSubmit, {
        likeType: 3,
        valueId: valueId,
      },'POST').then(function(res) {
        if (res.errno === "success") {
          let commentList = that.data.commentList;
          let comment = {};
          if (rindex || rindex == 0){
            comment = commentList[index].replyList.data.list[rindex];
          } else {
            comment = commentList[index];
          }
          comment.likeCount = !comment.commentLike ? comment.likeCount + 1 : comment.likeCount - 1;
          comment.commentLike = !comment.commentLike;
          if(comment.likeCount <= 0){
            comment.likeCount  = 0;
          }
          that.setData({
            commentList: commentList
          });
        }
      });
    },

    /**
     * 上滑触底加载更多
     */
    scrollToLower: function(e) {
      let that = this;
      if (this.data.totalPages > this.data.page) {
        this.setData({
          page: this.data.page + 1
        });
        that.getCommentList();
      } else {
        wx.showToast({
          title: '没有更多信息了',
          icon: 'none',
          duration: 2000
        });
        return false;
      }
    },

    //点击滚动窗口
    clickScrollView(e){
      let that = this;
      if(that.data.addImage){
        that.setData({
          InputBottom: 0,
        })
        if(!that.data.content && that.data.commentImage.length <= 0){
          that.setData({
            nickName: '',
            commentId: 0,
            replyUserId: 0,
          })
        }
      }
      that.setData({
        clickScroll: true,
        addImage: false,
      })
    },

    //评论内容
    bindCommentInput: function(e) {
      this.setData({
        content: e.detail.value
      });
    },

    /**
     * 发送消息
     * @param {*} e 
     */
    sendMessage(e){
      let that = this;
      util.request(api.CommentSubmit, {
        replyUserId: that.data.replyUserId,
        commentType: that.data.commentType,
        valueId: that.data.valueId,
        commentId: that.data.commentId,
        content: that.data.content,
        commentImage: that.data.commentImage,
      } , 'POST').then(function(res) {
        if (res.errno === "success") {
          //清空输入框
          that.setData({
            page: 1,
            content: '',
            nickName: '',
            commentId: 0,
            replyUserId: 0,
            commentImage: [],
            commentList: [],
          })
          that.getCommentList();
        }
      });
    },

    //获取子评论
    getReplyCommentList(e){
      let that = this;
      let index = e.currentTarget.dataset.index;
      let comment = that.data.commentList[index];
      let reply = comment.replyList.data;
      if (reply.pages > reply.page) {
        // 加载数据,模拟耗时操作
        wx.showLoading({
          title: '加载中...',
          mask: true,
          duration: 3000
        });
        util.request(api.CommentReplyList, {
          commentId: comment.commentId,
          page: reply.page + 1,
          limit: reply.limit,
        }).then(function(res) {
          if (res.errno === "success") {
            that.data.commentList[index].replyList.data.page = res.data.page;
            that.data.commentList[index].replyList.data.pages = res.data.pages;
            that.data.commentList[index].replyList.data.total = res.data.total;
            that.data.commentList[index].replyList.data.list = reply.list.concat(res.data.list);
            that.setData({
              commentList: that.data.commentList
            })
            wx.hideLoading();
          }
        });
      } else {
        wx.showToast({
          title: '没有更多信息了',
          icon: 'none',
          duration: 2000
        });
        return false;
      }
    },

    //获取评论列表
    getCommentList(e){
      let that = this;
      // 加载数据,模拟耗时操作
      wx.showLoading({
        title: '加载中...',
        mask: true,
        duration: 3000
      });
      util.request(api.CommentList, {
        commentType: that.data.commentType,
        valueId: that.data.valueId,
        page: that.data.page,
        limit: that.data.limit,
      }).then(function(res) {
        if (res.errno === "success") {
          wx.hideLoading();
          that.setData({
            commentList: that.data.commentList.concat(res.data.list),
            totalPages: res.data.pages,
          });
          that.getCommentCount();
        }
      });
    },

    //获取评论数量
    getCommentCount(){
      let that = this;
      util.request(api.CommentCount, {
        valueId: that.data.valueId,
        commentType: that.data.commentType,
      }).then(function(res) {
        if (res.errno === "success") {
          that.setData({
            commentCount: res.data.commentCount,
          });
          wx.hideLoading();
        }
      });
    },

    /**
     * 用户点击输入框，用于限制系统自动获取输入框焦点
     * @param {*} e 
     */
    getTextarea(e){
      let that = this;
      let item = e.currentTarget.dataset.item;
      if(item){
        that.setData({
          content: '',
          commentImage: [],
          commentId: item.replyId ? item.replyId : item.commentId,
          nickName: item.userInfo.nickName,
          replyUserId: item.userId,
        })
      }
      that.setData({
        textarea: true,
      })
    },

    /**
     * 获取选择的图片路径
     * @param {*} e 
     */
    getMessageImage(e){
      let that = this;
      that.setData({
        commentImage: e.detail.picUrls,
      })
    },

    /**
     * 图片查看器
     * @param {*} e 
     */
    previewImage: function(e) {
      wx.previewImage({
        current: e.currentTarget.id, // 当前显示图片的http链接
        urls: [e.target.dataset.url] // 需要预览的图片http链接列表
      })
    },

    /**
     * 打开图片选择，设置上推高度，上拉聊天记录
     */
    roundadd(){
      let that = this;
      //打开图片选择组件，要先打开才能获取高度
      that.setData({
        clickScroll: false,
        addImage: !that.data.addImage,
      })
      //获取图片选择组件高度
      that.createSelectorQuery().select('#add-image').boundingClientRect(function(rect){
        that.setData({
          InputBottom: rect ? rect.height : 0 // 节点的高度
        })
      }).exec();
    },

    /**
     * 监听键盘聚焦，打开键盘则关闭图片选择
     * @param {*} e 
     */
    InputFocus(e) {
      let that = this;
      if(that.data.textarea){
        that.setData({
          addImage: false,
          clickScroll: false,
          InputBottom: e.detail.height
        })
      }
    },

    /**
     * 键盘失去焦点
     * @param {*} e 
     */
    InputBlur(e) {
      let that = this;
      if(that.data.textarea){
        that.setData({
          textarea: false,
          InputBottom: 0,
        })
        if(!that.data.content && that.data.commentImage.length <= 0 && that.data.clickScroll){
          that.setData({
            nickName: '',
            commentId: 0,
            replyUserId: 0,
          })
        }
      }
    },

    //关闭弹窗
    closeCommentModal(e) {
      let that = this;
      that.setData({
        page: 1,
        commentList: [],
        showComment: false,
      })
    },

    //打开关闭回复
    toggleReply: function(e) {
      let that = this;
      let index = e.currentTarget.dataset.index;
      let showReply = that.data.commentList[index].showReply;
      that.data.commentList[index].showReply = !showReply;
      this.setData({
        commentList: that.data.commentList,
      })
    },

    /**
     * 打开关闭评论
     */
    togglePopup: function() {
      let that = this;
      that.getCommentList();
      this.setData({
        showComment: !that.data.showComment,
      });
    },

  }
})
