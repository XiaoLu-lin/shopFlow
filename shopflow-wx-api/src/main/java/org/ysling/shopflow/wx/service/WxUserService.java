package org.ysling.shopflow.wx.service;
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
public class WxUserService extends UserServiceImpl {


    @Cacheable(sync = true)
    public UserInfo findUserVoById(String userId) {
        ShopflowUser user = findById(userId);
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
    public ShopflowUser queryByOid(String openId) {
        QueryWrapper<ShopflowUser> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowUser.OPENID , openId);
        return getOne(wrapper);
    }

    
    @Cacheable(sync = true)
    public ShopflowUser findByShare(String userId) {
        QueryWrapper<ShopflowUser> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowUser.ID , userId);
        return getOne(wrapper);
    }

    
    @Cacheable(sync = true)
    public Integer countUser(String inviter) {
        QueryWrapper<ShopflowUser> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowUser.INVITER , inviter);
        return Math.toIntExact(count(wrapper));
    }

    
    @Cacheable(sync = true)
    public List<ShopflowUser> queryByInviter(String inviter) {
        QueryWrapper<ShopflowUser> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowUser.INVITER , inviter);
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public List<ShopflowUser> queryByUsername(String username) {
        QueryWrapper<ShopflowUser> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowUser.USERNAME , username);
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public boolean checkByUsername(String username) {
        QueryWrapper<ShopflowUser> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowUser.USERNAME , username);
        return exists(wrapper);
    }

    
    @Cacheable(sync = true)
    public List<ShopflowUser> queryByMobile(String mobile) {
        QueryWrapper<ShopflowUser> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowUser.MOBILE , mobile);
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public List<ShopflowUser> queryByOpenid(String openid) {
        QueryWrapper<ShopflowUser> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowUser.OPENID , openid);
        return queryAll(wrapper);
    }

}
