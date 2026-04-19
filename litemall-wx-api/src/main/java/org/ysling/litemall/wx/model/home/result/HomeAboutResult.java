package org.ysling.litemall.wx.model.home.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 商城信息
 * @author Ysling
 */
@Data
public class HomeAboutResult implements Serializable {

    /**
     * 商城名称
     */
    private String name;
    /**
     * 手机号
     */
    private String phone;
    /**
     * QQ号
     */
    private String qq;
    /**
     * 经度
     */
    private String longitude;
    /**
     * 维度
     */
    private String latitude;
    /**
     * 商城地址
     */
    private String address;


}
