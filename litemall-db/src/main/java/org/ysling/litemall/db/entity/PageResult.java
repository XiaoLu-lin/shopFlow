package org.ysling.litemall.db.entity;

import cn.hutool.core.collection.ListUtil;
import com.github.pagehelper.PageInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

/**
 * 自定义分页返回
 * @author Ysling
 */
@Data
@NoArgsConstructor
public class PageResult<V> implements Serializable {

    /**
     * 数据列表
     */
    private List<V> list;

    /**
     * 总数据条数
     */
    private long total;

    /**
     * 当前页码
     */
    private long page;

    /**
     * 每页数量
     */
    private long limit;

    /**
     * 总页数
     */
    private long pages;

    /**
     * 初始化PageInfo
     */
    public PageResult(PageInfo<?> pageInfo, List<V> list){
        this.list = list;
        this.total = pageInfo.getTotal();
        this.limit = pageInfo.getPageSize();
        this.page = pageInfo.getPageNum();
        this.pages = pageInfo.getPages();
    }

    /**
     * 自定义list分页结果
     */
    public PageResult(List<V> pageList , PageBody body){
        this.list = ListUtil.page(body.getPage()-1, body.getLimit(), pageList);
        this.total = pageList.size();
        this.limit = body.getLimit();
        this.page = body.getPage();
        this.pages = pageList.size() / body.getLimit();
    }

}
