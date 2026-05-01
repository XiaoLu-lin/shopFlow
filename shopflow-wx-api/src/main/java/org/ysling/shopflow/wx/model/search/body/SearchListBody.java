package org.ysling.shopflow.wx.model.search.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.shopflow.db.entity.PageBody;

import javax.validation.constraints.NotEmpty;
import org.ysling.shopflow.core.annotation.NotValue;
import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchListBody extends PageBody implements Serializable {

    /**
     * 关键字
     */
    @NotEmpty
    private String keyword;

}
