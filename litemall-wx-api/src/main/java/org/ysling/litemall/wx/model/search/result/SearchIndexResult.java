package org.ysling.litemall.wx.model.search.result;

import lombok.Data;
import org.ysling.litemall.db.domain.LitemallKeyword;
import org.ysling.litemall.db.domain.LitemallSearchHistory;

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
    private LitemallKeyword defaultKeyword;
    /**
     * 取出热闹关键词
     */
    private List<LitemallKeyword> hotKeywordList;
    /**
     * 用户历史搜索
     */
    private List<LitemallSearchHistory> historyKeywordList;


}
