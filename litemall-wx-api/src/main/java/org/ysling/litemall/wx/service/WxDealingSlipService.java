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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.litemall.db.domain.LitemallDealingSlip;
import org.ysling.litemall.db.entity.PageBody;
import org.ysling.litemall.db.service.impl.DealingSlipServiceImpl;
import java.util.List;

/**
 * 交易记录服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_dealing_slip")
public class WxDealingSlipService extends DealingSlipServiceImpl {


    /**
     * 判断outBatchNo是否存在
     * @param userId  用户id
     * @param outBatchNo 转账单号
     * @return true
     */
    @Cacheable(sync = true)
    public Boolean isByOutBatchNo(String userId, String outBatchNo) {
        QueryWrapper<LitemallDealingSlip> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallDealingSlip.USER_ID , userId);
        wrapper.eq(LitemallDealingSlip.OUT_BATCH_NO , outBatchNo);
        return exists(wrapper);
    }


    @Cacheable(sync = true)
    public List<LitemallDealingSlip> querySelective(String userId, String openId, PageBody body) {
        QueryWrapper<LitemallDealingSlip> wrapper = startPage(body);
        if (userId != null) {
            wrapper.eq(LitemallDealingSlip.USER_ID , userId);
        }
        if (openId != null){
            wrapper.eq(LitemallDealingSlip.OPENID , openId);
        }
        return queryAll(wrapper);
    }


}
