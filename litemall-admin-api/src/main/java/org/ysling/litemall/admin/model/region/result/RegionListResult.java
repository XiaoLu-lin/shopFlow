package org.ysling.litemall.admin.model.region.result;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 行政区域列表响应
 * @author Ysling
 */
@Data
public class RegionListResult implements Serializable {

    /**
     * 行政区域ID
     */
    private String id;
    /**
     * 行政区域名称
     */
    private String name;
    /**
     * 行政区域类型，如如1则是省， 如果是2则是市，如果是3则是区县
     */
    private Byte type;
    /**
     * 行政区域编码
     */
    private Integer code;
    /**
     * 行政区域子地区
     */
    private List<RegionListResult> children;

}
