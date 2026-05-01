package org.ysling.shopflow.admin.model.address.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.shopflow.db.entity.PageBody;

import java.io.Serializable;

/**
 * 地址列表请求参数
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AddressListBody extends PageBody implements Serializable {

    /**
     * 用户ID
     */
    private String userId;
    /**
     * 收货人名
     */
    private String name;
    /**
     * 联系电话
     */
    private String mobile;

}
