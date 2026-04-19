package org.ysling.litemall.admin.service;
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
import org.ysling.litemall.admin.model.user.body.DealingSlipListBody;
import org.ysling.litemall.core.weixin.enums.TransferStatus;
import org.ysling.litemall.db.domain.LitemallDealingSlip;
import org.ysling.litemall.db.service.impl.DealingSlipServiceImpl;
import java.util.List;

/**
 * 用户余额交易记录服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_dealing_slip")
public class AdminDealingSlipService extends DealingSlipServiceImpl {


    @Cacheable(sync = true)
    public List<LitemallDealingSlip> querySelective(DealingSlipListBody body) {
        QueryWrapper<LitemallDealingSlip> wrapper = startPage(body);
        if (body.getUserId() != null) {
            wrapper.eq(LitemallDealingSlip.USER_ID , body.getUserId());
        }
        if (body.getDealType() != null){
            wrapper.eq(LitemallDealingSlip.DEAL_TYPE , body.getDealType());
        }
        if (body.getStatus() != null){
            if (body.getStatus()){
                wrapper.eq(LitemallDealingSlip.STATUS, TransferStatus.SUCCESS.getStatus());
            }else {
                wrapper.ne(LitemallDealingSlip.STATUS , TransferStatus.SUCCESS.getStatus());
            }
        }
        return queryAll(wrapper);
    }


}
