package org.ysling.litemall.core.transaction.model;

import lombok.Builder;
import lombok.Data;
import org.mybatis.spring.SqlSessionHolder;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 保存当前事务资源,用于线程间的事务资源COPY操作
 * 事务结束后默认会移除集合中的DataSource作为key关联的资源记录
 * 下面五个属性会在事务结束后被自动清理,无需我们手动清理
 * @author Ysling
 */
@Data
@Builder
public class TransactionResource {

    private Map<Object, Object> resources;

    private Set<TransactionSynchronization> synchronizations;

    private  String currentTransactionName;

    private Boolean currentTransactionReadOnly;

    private Integer currentTransactionIsolationLevel;

    private Boolean actualTransactionActive;

    public static TransactionResource copyTransactionResource(){
        return TransactionResource.builder()
                //返回的是不可变集合
                .resources(TransactionSynchronizationManager.getResourceMap())
                //如果需要注册事务监听者,这里记得修改--我们这里不需要,就采用默认负责--spring事务内部默认也是这个值
                .synchronizations(new LinkedHashSet<>())
                .currentTransactionName(TransactionSynchronizationManager.getCurrentTransactionName())
                .currentTransactionReadOnly(TransactionSynchronizationManager.isCurrentTransactionReadOnly())
                .currentTransactionIsolationLevel(TransactionSynchronizationManager.getCurrentTransactionIsolationLevel())
                .actualTransactionActive(TransactionSynchronizationManager.isActualTransactionActive())
                .build();
    }

    public void autoWiredTransactionResource(){
        resources.forEach(TransactionSynchronizationManager::bindResource);
        //如果需要注册事务监听者,这里记得修改--我们这里不需要,就采用默认负责--spring事务内部默认也是这个值
        TransactionSynchronizationManager.setActualTransactionActive(actualTransactionActive);
        TransactionSynchronizationManager.setCurrentTransactionIsolationLevel(currentTransactionIsolationLevel);
        TransactionSynchronizationManager.setCurrentTransactionReadOnly(currentTransactionReadOnly);
        TransactionSynchronizationManager.setCurrentTransactionName(currentTransactionName);
        TransactionSynchronizationManager.initSynchronization();
    }

    public void removeTransactionResource() {
        //事务结束后默认会移除集合中的DataSource作为key关联的资源记录
        //DataSource如果重复移除,unbindResource时会因为不存在此key关联的事务资源而报错
        resources.keySet().forEach(key -> {
            if(!(key instanceof DataSource)){
                try {
                    TransactionSynchronizationManager.unbindResource(key);
                }catch (Throwable e){
                    System.out.println(e.getMessage());
                }
            }
        });
    }
}
