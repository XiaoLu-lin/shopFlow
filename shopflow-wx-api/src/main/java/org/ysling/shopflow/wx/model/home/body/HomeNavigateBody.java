package org.ysling.shopflow.wx.model.home.body;

import lombok.Data;

import org.ysling.shopflow.core.annotation.NotValue;
import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
public class HomeNavigateBody implements Serializable {


    /**
     * 分享ID
     */
    @NotValue(message = "分享ID不能为空")
    private String sceneId;

    /**
     * 分享类型
     */
    @NotValue(message = "分享类型不能为空")
    private String sceneType;


}
