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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.shopflow.admin.model.user.body.DealingSlipListBody;
import org.ysling.shopflow.core.weixin.enums.TransferStatus;
import org.ysling.shopflow.db.domain.ShopflowDealingSlip;
import org.ysling.shopflow.db.service.impl.DealingSlipServiceImpl;
import java.util.List;

/**
 * 用户余额交易记录服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_dealing_slip")
public class AdminDealingSlipService extends DealingSlipServiceImpl {


    @Cacheable(sync = true)
    public List<ShopflowDealingSlip> querySelective(DealingSlipListBody body) {
        QueryWrapper<ShopflowDealingSlip> wrapper = startPage(body);
        if (body.getUserId() != null) {
            wrapper.eq(ShopflowDealingSlip.USER_ID , body.getUserId());
        }
        if (body.getDealType() != null){
            wrapper.eq(ShopflowDealingSlip.DEAL_TYPE , body.getDealType());
        }
        if (body.getStatus() != null){
            if (body.getStatus()){
                wrapper.eq(ShopflowDealingSlip.STATUS, TransferStatus.SUCCESS.getStatus());
            }else {
                wrapper.ne(ShopflowDealingSlip.STATUS , TransferStatus.SUCCESS.getStatus());
            }
        }
        return queryAll(wrapper);
    }


}
