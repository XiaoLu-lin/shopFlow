package org.ysling.litemall.db.entity;
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
import lombok.Data;
import org.ysling.litemall.db.domain.*;
import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
public class GoodsAllinone implements Serializable {

    /**
     * 商品基本信息
     */
    private LitemallGoods goods;
    /**
     * 货品信息
     */
    private LitemallGoodsProduct[] products;
    /**
     * 团购规则
     */
    private LitemallGrouponRules grouponRules;
    /**
     * 商品参数
     */
    private LitemallGoodsAttribute[] attributes;
    /**
     * 商品规格
     */
    private LitemallGoodsSpecification[] specifications;

}
