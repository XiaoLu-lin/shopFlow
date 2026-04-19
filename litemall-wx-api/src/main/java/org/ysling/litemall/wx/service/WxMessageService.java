package org.ysling.litemall.wx.service;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [litemall-plus] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.litemall.db.domain.LitemallMessage;
import org.ysling.litemall.db.service.impl.MessageServiceImpl;
import org.ysling.litemall.wx.model.message.body.HistoryMessageBody;
import java.util.List;
import java.util.function.Consumer;

/**
 * 聊天记录服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_message")
public class WxMessageService extends MessageServiceImpl {


    /**
     * 获取历史消息
     */
    @Cacheable(sync = true)
    public List<LitemallMessage> getHistoryMessage(String sendUserId, HistoryMessageBody body) {
        QueryWrapper<LitemallMessage> wrapper = startPage(body);
        wrapper.eq(LitemallMessage.SEND_USER_ID , sendUserId)
                .eq(LitemallMessage.RECEIVE_USER_ID , body.getReceiveUserId())
                .or()
                .eq(LitemallMessage.SEND_USER_ID , body.getReceiveUserId())
                .eq(LitemallMessage.RECEIVE_USER_ID , sendUserId);
        return queryAll(wrapper);
    }

    /**
     * 获取当前用户作为接收者的所有消息
     * @param receiveUserId 接收者用户id
     * @return 消息集合
     */
    @Cacheable(sync = true)
    public List<LitemallMessage> queryByReceiveUserId(String receiveUserId) {
        QueryWrapper<LitemallMessage> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallMessage.RECEIVE_USER_ID , receiveUserId);
        return queryAll(wrapper);
    }

    /**
     * 获取当前用户作为发送者的所有消息
     * @param sendUserId 发送者用户id
     * @return 消息集合
     */
    @Cacheable(sync = true)
    public List<LitemallMessage> queryBySendUserId(String sendUserId) {
        QueryWrapper<LitemallMessage> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallMessage.SEND_USER_ID , sendUserId);
        return queryAll(wrapper);
    }


    /**
     * 删除信息
     * @param receiveUserId 接收者
     * @param sendUserId 发送者
     */
    @CacheEvict(allEntries = true)
    public void deleteMessage(String receiveUserId, String sendUserId) {
        QueryWrapper<LitemallMessage> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallMessage.SEND_USER_ID , sendUserId);
        wrapper.eq(LitemallMessage.RECEIVE_USER_ID , receiveUserId);
        remove(wrapper);
    }


}
