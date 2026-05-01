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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.shopflow.db.domain.ShopflowDealingSlip;
import org.ysling.shopflow.db.entity.PageBody;
import org.ysling.shopflow.db.service.impl.DealingSlipServiceImpl;
import java.util.List;

/**
 * 交易记录服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_dealing_slip")
public class WxDealingSlipService extends DealingSlipServiceImpl {


    /**
     * 判断outBatchNo是否存在
     * @param userId  用户id
     * @param outBatchNo 转账单号
     * @return true
     */
    @Cacheable(sync = true)
    public Boolean isByOutBatchNo(String userId, String outBatchNo) {
        QueryWrapper<ShopflowDealingSlip> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowDealingSlip.USER_ID , userId);
        wrapper.eq(ShopflowDealingSlip.OUT_BATCH_NO , outBatchNo);
        return exists(wrapper);
    }


    @Cacheable(sync = true)
    public List<ShopflowDealingSlip> querySelective(String userId, String openId, PageBody body) {
        QueryWrapper<ShopflowDealingSlip> wrapper = startPage(body);
        if (userId != null) {
            wrapper.eq(ShopflowDealingSlip.USER_ID , userId);
        }
        if (openId != null){
            wrapper.eq(ShopflowDealingSlip.OPENID , openId);
        }
        return queryAll(wrapper);
    }


}
