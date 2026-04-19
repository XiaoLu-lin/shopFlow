package org.ysling.litemall.db.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ysling.litemall.db.handler.*;
import java.util.Collection;
/**
 * <p>
 * 商品规格表
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("litemall_goods_specification")
public class LitemallGoodsSpecification implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 商品规格表ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 商品表的商品ID
     */
    @TableField("`goods_id`")
    private String goodsId;
    /**
     * 商品规格名称
     */
    @TableField("`specification`")
    private String specification;
    /**
     * 商品规格值
     */
    @TableField("`value`")
    private String value;
    /**
     * 商品规格图片
     */
    @TableField("`pic_url`")
    private String picUrl;
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
     * 商品规格表ID
     */
    public static final String ID = "`id`";
    /**
     * 商品表的商品ID
     */
    public static final String GOODS_ID = "`goods_id`";
    /**
     * 商品规格名称
     */
    public static final String SPECIFICATION = "`specification`";
    /**
     * 商品规格值
     */
    public static final String VALUE = "`value`";
    /**
     * 商品规格图片
     */
    public static final String PIC_URL = "`pic_url`";
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
