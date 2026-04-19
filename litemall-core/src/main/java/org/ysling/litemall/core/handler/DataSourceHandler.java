package org.ysling.litemall.core.handler;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.ysling.litemall.core.utils.bcrypt.CryptoUtil;
import org.ysling.litemall.db.domain.LitemallTenant;
import org.ysling.litemall.db.service.ITenantService;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;

/**
 * 动态数据源服务类
 * @author Ysling
 */
@Slf4j
@Component
public class DataSourceHandler {

    @Autowired
    private ITenantService tenantService;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private DefaultDataSourceCreator dataSourceCreator;

    @PostConstruct
    public void init(){
        log.info("初始化 -> [初始化动态数据源]");
        List<LitemallTenant> tenantList = tenantService.list();
        for (LitemallTenant tenant :tenantList) {
            this.addDataSource(tenant);
        }
    }

    /**
     * 添加数据源
     */
    public void addDataSource(LitemallTenant tenant) {
        if (!StringUtils.hasText(tenant.getJdbcUrl())){
            return;
        }
        if (!StringUtils.hasText(tenant.getPassword())){
            return;
        }
        if (!StringUtils.hasText(tenant.getUsername())){
            return;
        }
        if (!StringUtils.hasText(tenant.getDriverClassName())){
            return;
        }

        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        //解密数据库用户名
        String username = CryptoUtil.decode(tenant.getUsername(), tenant.getAppId());
        dataSourceProperty.setUsername(username);
        //解密数据库密码
        String password = CryptoUtil.decode(tenant.getPassword(), tenant.getAppId());
        dataSourceProperty.setPassword(password);
        //设置数据库连接
        dataSourceProperty.setUrl(tenant.getJdbcUrl());
        dataSourceProperty.setDriverClassName(tenant.getDriverClassName());
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        DataSource creatorDataSource = dataSourceCreator.createDataSource(dataSourceProperty);
        // 测试连接
        try {
            creatorDataSource.getConnection();
        }catch (Throwable e){
            throw new IllegalStateException("[" + JSONObject.toJSONString(tenant.getId()) + "]" + "：数据源连接不上可能是连接参数有误或库未创建！");
        }
        ds.addDataSource(tenant.getId(), creatorDataSource);
    }

    /**
     * 删除容器中数据源
     */
    @DeleteMapping
    public Boolean remove(String name) {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        ds.removeDataSource(name);
        return Boolean.TRUE;
    }
}
