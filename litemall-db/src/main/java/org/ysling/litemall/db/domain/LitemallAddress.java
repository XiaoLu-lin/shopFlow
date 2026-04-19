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
 * 收货地址表
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("litemall_address")
public class LitemallAddress implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 收货地址表ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 收货人名称
     */
    @TableField("`name`")
    private String name;
    /**
     * 用户表的用户ID
     */
    @TableField("`user_id`")
    private String userId;
    /**
     * 行政区域表的省ID
     */
    @TableField("`province`")
    private String province;
    /**
     * 行政区域表的市ID
     */
    @TableField("`city`")
    private String city;
    /**
     * 行政区域表的区县ID
     */
    @TableField("`county`")
    private String county;
    /**
     * 详细收货地址
     */
    @TableField("`address_detail`")
    private String addressDetail;
    /**
     * 总收货地址
     */
    @TableField("`address_all`")
    private String addressAll;
    /**
     * 地区编码
     */
    @TableField("`area_code`")
    private String areaCode;
    /**
     * 邮政编码
     */
    @TableField("`postal_code`")
    private String postalCode;
    /**
     * 手机号码
     */
    @TableField("`mobile`")
    private String mobile;
    /**
     * 是否默认地址
     */
    @TableField("`is_default`")
    private Boolean isDefault;
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
     * 收货地址表ID
     */
    public static final String ID = "`id`";
    /**
     * 收货人名称
     */
    public static final String NAME = "`name`";
    /**
     * 用户表的用户ID
     */
    public static final String USER_ID = "`user_id`";
    /**
     * 行政区域表的省ID
     */
    public static final String PROVINCE = "`province`";
    /**
     * 行政区域表的市ID
     */
    public static final String CITY = "`city`";
    /**
     * 行政区域表的区县ID
     */
    public static final String COUNTY = "`county`";
    /**
     * 详细收货地址
     */
    public static final String ADDRESS_DETAIL = "`address_detail`";
    /**
     * 总收货地址
     */
    public static final String ADDRESS_ALL = "`address_all`";
    /**
     * 地区编码
     */
    public static final String AREA_CODE = "`area_code`";
    /**
     * 邮政编码
     */
    public static final String POSTAL_CODE = "`postal_code`";
    /**
     * 手机号码
     */
    public static final String MOBILE = "`mobile`";
    /**
     * 是否默认地址
     */
    public static final String IS_DEFAULT = "`is_default`";
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
