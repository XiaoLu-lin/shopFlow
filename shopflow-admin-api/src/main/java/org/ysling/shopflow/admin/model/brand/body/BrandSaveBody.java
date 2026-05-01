package org.ysling.shopflow.admin.model.brand.body;


import lombok.Data;
import org.ysling.shopflow.core.annotation.NotValue;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BrandSaveBody implements Serializable {

    /**
     * 品牌商表ID
     */
    private String id;
    /**
     * 品牌商名称
     */
    @NotValue(message = "店铺名称不能为空")
    private String name;
    /**
     * 品牌商简介
     */
    @NotValue(message = "店铺简介不能为空")
    private String depict;
    /**
     * 品牌商邮箱
     */
    @NotValue(message = "店铺邮箱不能为空")
    private String mail;
    /**
     * 品牌商页的品牌商图片
     */
    @NotValue(message = "店铺图片不能为空")
    private String picUrl;
    /**
     * 品牌商的商品低价，仅用于页面展示
     */
    @NotValue(message = "店铺底价不能为空")
    private BigDecimal floorPrice;

    /**
     * 自提地址
     */
    private String address;

    /**
     * 更新版本号
     */
    private Integer version;

}
