package org.ysling.shopflow.wx.web;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [ShopFlow] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ysling.shopflow.core.annotation.JsonBody;
import org.ysling.shopflow.core.utils.chatgpt.ChatGPTClient;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.ShopflowMessage;
import org.ysling.shopflow.wx.annotation.LoginUser;
import org.ysling.shopflow.wx.service.WxMessageService;
import org.ysling.shopflow.wx.model.message.body.HistoryMessageBody;
import org.ysling.shopflow.wx.web.impl.WxWebMessageService;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 用户聊天列表
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/wx/message")
@Validated
public class WxMessageController {


    @Autowired
    private WxWebMessageService messageService;

    /**
     * 获取聊天记录
     * @param userId 用户ID
     * @return 聊天记录
     */
    @GetMapping("list")
    public Object getMessageList(@LoginUser String userId){
        return messageService.getMessageList(userId);
    }

    /**
     * 获取历史聊天记录
     * @param userId 用户ID
     * @return 聊天记录
     */
    @GetMapping("history")
    public Object getHistoryMessageList(@LoginUser String userId, HistoryMessageBody body){
        return messageService.getHistoryMessageList(userId , body);
    }

    /**
     * 删除聊天记录
     * @param userId 用户ID
     * @param sendUserId 发送用户
     * @return 成功
     */
    @PostMapping("delete")
    public Object delete(@LoginUser String userId, @JsonBody String sendUserId){
        return messageService.delete(userId , sendUserId);
    }

}
