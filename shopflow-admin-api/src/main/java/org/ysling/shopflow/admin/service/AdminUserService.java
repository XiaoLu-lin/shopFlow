package org.ysling.shopflow.admin.service;
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

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.shopflow.admin.model.user.body.UserListBody;
import org.ysling.shopflow.db.domain.ShopflowUser;
import org.ysling.shopflow.db.entity.UserInfo;
import org.ysling.shopflow.db.service.impl.UserServiceImpl;
import java.util.List;


/**
 * 用户服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_user")
public class AdminUserService extends UserServiceImpl {


    @Cacheable(sync = true)
    public UserInfo findUserVoById(String userId) {
        ShopflowUser user = getById(userId);
        UserInfo userInfo = new UserInfo();
        if (user != null) {
            BeanUtil.copyProperties(user , userInfo);
            return userInfo;
        }
        userInfo.setNickName("匿名用户");
        userInfo.setAvatarUrl("https://th.bing.com/th?id=OSK.2fe5b3f3f141834f896fe8a9ffe3a1dc&w=148&h=148&c=7&o=6&dpr=1.8&pid=SANGAM");
        return userInfo;
    }

    
    @Cacheable(sync = true)
    public List<ShopflowUser> querySelective(UserListBody body) {
        QueryWrapper<ShopflowUser> wrapper = startPage(body);
        if (body.getUserId() != null){
            wrapper.eq(ShopflowUser.ID , body.getUserId());
        }
        if (body.getLevel() != null){
            wrapper.eq(ShopflowUser.USER_LEVEL , body.getLevel());
        }
        if (body.getInviter() != null){
            wrapper.eq(ShopflowUser.INVITER , body.getInviter());
        }
        if (StringUtils.hasText(body.getMobile())) {
            wrapper.like(ShopflowUser.MOBILE , body.getMobile());
        }
        if (StringUtils.hasText(body.getUsername())) {
            wrapper.like(ShopflowUser.USERNAME , body.getUsername());
        }
        return queryAll(wrapper);
    }


}
