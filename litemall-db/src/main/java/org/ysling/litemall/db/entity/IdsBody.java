package org.ysling.litemall.db.entity;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * id列表请求参数
 * @author Ysling
 */
@Data
public class IdsBody implements Serializable {

    /**
     * id列表
     */
    @NotNull
    private List<String> ids;


}
