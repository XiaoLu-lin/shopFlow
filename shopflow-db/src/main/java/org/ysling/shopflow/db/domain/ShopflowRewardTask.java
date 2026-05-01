package org.ysling.shopflow.db.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ysling.shopflow.db.handler.*;
import java.util.Collection;
/**
 * <p>
 * 赏金任务规则表
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("shopflow_reward_task")
public class ShopflowRewardTask implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 赏金任务规则表ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 商品表的商品ID
     */
    @TableField("`goods_id`")
    private String goodsId;
    /**
     * 商品名称
     */
    @TableField("`goods_name`")
    private String goodsName;
    /**
     * 商品价格
     */
    @TableField("`goods_price`")
    private BigDecimal goodsPrice;
    /**
     * 商品图片或者商品货品图片
     */
    @TableField("`pic_url`")
    private String picUrl;
    /**
     * 奖励金额
     */
    @TableField("`award`")
    private BigDecimal award;
    /**
     * 参与者数量
     */
    @TableField("`joiners_member`")
    private Integer joinersMember;
    /**
     * 活动数量
     */
    @TableField("`reward_member`")
    private Integer rewardMember;
    /**
     * 赏金任务状态，0正常，1已完成，2已取消
     */
    @TableField("`status`")
    private Short status;
    /**
     * 创建时间
     */
    @TableField(value = "`add_time`", fill = FieldFill.INSERT)
    private LocalDateTime addTime;
    /**
     * 更新时间
     */
    @TableField(value = "`update_time`", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    /**
     * 逻辑删除
     */
    @TableField("`deleted`")
    @TableLogic
    private Boolean deleted;
    /**
     * 租户ID，用于分割多个租户
     */
    @TableField("`tenant_id`")
    private String tenantId;
    /**
     * 更新版本号
     */
    @TableField("`version`")
    @Version
    private Integer version;

    /////////////////////////////////
    // 数据库字段常量
    ////////////////////////////////

    /**
     * 赏金任务规则表ID
     */
    public static final String ID = "`id`";
    /**
     * 商品表的商品ID
     */
    public static final String GOODS_ID = "`goods_id`";
    /**
     * 商品名称
     */
    public static final String GOODS_NAME = "`goods_name`";
    /**
     * 商品价格
     */
    public static final String GOODS_PRICE = "`goods_price`";
    /**
     * 商品图片或者商品货品图片
     */
    public static final String PIC_URL = "`pic_url`";
    /**
     * 奖励金额
     */
    public static final String AWARD = "`award`";
    /**
     * 参与者数量
     */
    public static final String JOINERS_MEMBER = "`joiners_member`";
    /**
     * 活动数量
     */
    public static final String REWARD_MEMBER = "`reward_member`";
    /**
     * 赏金任务状态，0正常，1已完成，2已取消
     */
    public static final String STATUS = "`status`";
    /**
     * 创建时间
     */
    public static final String ADD_TIME = "`add_time`";
    /**
     * 更新时间
     */
    public static final String UPDATE_TIME = "`update_time`";
    /**
     * 逻辑删除
     */
    public static final String DELETED = "`deleted`";
    /**
     * 租户ID，用于分割多个租户
     */
    public static final String TENANT_ID = "`tenant_id`";
    /**
     * 更新版本号
     */
    public static final String VERSION = "`version`";
}
