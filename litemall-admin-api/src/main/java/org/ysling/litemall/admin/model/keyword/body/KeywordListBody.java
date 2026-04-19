package org.ysling.litemall.admin.model.keyword.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.litemall.db.entity.PageBody;

/**
 * 关键词列表请求参数
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KeywordListBody extends PageBody {


    /**
     * 地址
     */
    private String url;
    /**
     * 关键字
     */
    private String keyword;



}
