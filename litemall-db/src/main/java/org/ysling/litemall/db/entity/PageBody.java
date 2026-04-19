package org.ysling.litemall.db.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.ysling.litemall.db.validator.order.Order;
import org.ysling.litemall.db.validator.sort.Sort;
import java.io.Serializable;

/**
 * 分页查询公共类
 */
@Data
@NoArgsConstructor
public class PageBody implements Serializable {

    /**
     * ID字段常量
     */
    public static final String ID = "`id`";

    /**
     * 序号ID
     */
    private String id;

    /**
     * 分页页码  (默认：1)
     */
    private Integer page = 1;

    /**
     * 查询数量  (默认：10) (0:查询全部)
     */
    private Integer limit = 10;

    /**
     * 排序字段  (默认：add_time)
     */
    @Sort
    private String sort = "add_time";

    /**
     * 正序或倒叙排序，desc -- asc （默认：desc）
     */
    @Order
    private String order = "desc";

    /**
     * @param limit  查询数量  (默认：10) (0:查询全部)
     */
    public PageBody(Integer limit){
        this(null , limit);
    }

    /**
     * @param page  分页页码  (默认：1)
     * @param limit 查询数量  (默认：10) (0:查询全部)
     */
    public PageBody(Integer page, Integer limit){
        this(page , limit , null , null);
    }

    /**
     * @param sort  排序字段  (默认：add_time)
     */
    public PageBody(String sort){
        this(sort , null);
    }

    /**
     * @param sort  排序字段  (默认：add_time)
     * @param order 正序或倒叙排序，desc -- asc （默认：desc）
     */
    public PageBody(String sort, String order){
        this(null , null , sort , order);
    }

    /**
     *
     * @param page  分页页码  (默认：1)
     * @param limit 查询数量  (默认：10) (0:查询全部)
     * @param sort  排序字段  (默认：add_time)
     * @param order 正序或倒叙排序，desc -- asc （默认：desc）
     */
    public PageBody(Integer page, Integer limit, String sort, String order){
        if (page != null && page > 1){
            this.page = page;
        }
        if (limit != null){
            this.limit = limit;
        }
        if (sort != null){
            this.sort = sort;
        }
        if (order != null){
            this.order = order;
        }
    }
    
}
