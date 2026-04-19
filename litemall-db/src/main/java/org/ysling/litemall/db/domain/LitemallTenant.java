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
 * 租户表，逻辑分库
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("litemall_tenant")
public class LitemallTenant implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 租户表ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 租户地址
     */
    @TableField("`address`")
    private String address;
    /**
     * 小程序appId
     */
    @TableField("`app_id`")
    private String appId;
    /**
     * 小程序密钥
     */
    @TableField("`app_secret`")
    private String appSecret;
    /**
     * 消息推送key
     */
    @TableField("`aes_key`")
    private String aesKey;
    /**
     * 消息推送token
     */
    @TableField("`token`")
    private String token;
    /**
     * 微信商户号
     */
    @TableField("`mch_id`")
    private String mchId;
    /**
     * 微信商户密钥
     */
    @TableField("`mch_key`")
    private String mchKey;
    /**
     * 微信支付证书地址
     */
    @TableField("`key_path`")
    private String keyPath;
    /**
     * 微信支付v3密钥
     */
    @TableField("`api_v3_key`")
    private String apiV3Key;
    /**
     * 微信支付证书地址.cert
     */
    @TableField("`private_cert_path`")
    private String privateCertPath;
    /**
     * 微信支付证书地址.key
     */
    @TableField("`private_key_path`")
    private String privateKeyPath;
    /**
     * 数据库链接地址
     */
    @TableField("`jdbc_url`")
    private String jdbcUrl;
    /**
     * 数据库用户名
     */
    @TableField("`username`")
    private String username;
    /**
     * 数据库密码
     */
    @TableField("`password`")
    private String password;
    /**
     * 数据库驱动
     */
    @TableField("`driver_class_name`")
    private String driverClassName;
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
     * 租户表ID
     */
    public static final String ID = "`id`";
    /**
     * 租户地址
     */
    public static final String ADDRESS = "`address`";
    /**
     * 小程序appId
     */
    public static final String APP_ID = "`app_id`";
    /**
     * 小程序密钥
     */
    public static final String APP_SECRET = "`app_secret`";
    /**
     * 消息推送key
     */
    public static final String AES_KEY = "`aes_key`";
    /**
     * 消息推送token
     */
    public static final String TOKEN = "`token`";
    /**
     * 微信商户号
     */
    public static final String MCH_ID = "`mch_id`";
    /**
     * 微信商户密钥
     */
    public static final String MCH_KEY = "`mch_key`";
    /**
     * 微信支付证书地址
     */
    public static final String KEY_PATH = "`key_path`";
    /**
     * 微信支付v3密钥
     */
    public static final String API_V3_KEY = "`api_v3_key`";
    /**
     * 微信支付证书地址.cert
     */
    public static final String PRIVATE_CERT_PATH = "`private_cert_path`";
    /**
     * 微信支付证书地址.key
     */
    public static final String PRIVATE_KEY_PATH = "`private_key_path`";
    /**
     * 数据库链接地址
     */
    public static final String JDBC_URL = "`jdbc_url`";
    /**
     * 数据库用户名
     */
    public static final String USERNAME = "`username`";
    /**
     * 数据库密码
     */
    public static final String PASSWORD = "`password`";
    /**
     * 数据库驱动
     */
    public static final String DRIVER_CLASS_NAME = "`driver_class_name`";
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
