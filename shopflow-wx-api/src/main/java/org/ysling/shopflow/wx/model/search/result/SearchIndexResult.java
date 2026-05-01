package org.ysling.shopflow.wx.model.search.result;

import lombok.Data;
import org.ysling.shopflow.db.domain.ShopflowKeyword;
import org.ysling.shopflow.db.domain.ShopflowSearchHistory;

import java.io.Serializable;
import java.util.List;

/**
 * @author Ysling
 */
@Data
public class SearchIndexResult implements Serializable {


    /**
     * 取出输入框默认的关键词
     */
    private ShopflowKeyword defaultKeyword;
    /**
     * 取出热闹关键词
     */
    private List<ShopflowKeyword> hotKeywordList;
    /**
     * 用户历史搜索
     */
    private List<ShopflowSearchHistory> historyKeywordList;


}
