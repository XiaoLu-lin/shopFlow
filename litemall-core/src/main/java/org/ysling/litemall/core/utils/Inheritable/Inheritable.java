package org.ysling.litemall.core.utils.Inheritable;

import org.ysling.litemall.core.tenant.handler.TenantContextHolder;
import org.ysling.litemall.core.utils.BeanUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;

/**
 * 自定义Supplier封装类
 * @author Ysling
 * @param <T>
 */
public class Inheritable<T> {

    /**
     * 异步线程返回的数据
     */
    public CompletableFuture<T> future;

    /**
     * 重新封装一层Supplier
     */
    public Inheritable(Supplier<T> supplier){
        this(supplier, BeanUtil.getBean(ThreadPoolExecutor.class));
    }

    /**
     * 重新封装一层Supplier
     */
    public Inheritable(String tenantId, Supplier<T> supplier){
        this(tenantId, supplier, BeanUtil.getBean(ThreadPoolExecutor.class));
    }

    /**
     * 重新封装一层Supplier
     */
    public Inheritable(Supplier<T> supplier, Executor executor){
        this(TenantContextHolder.getLocalTenantId(), supplier , executor);
    }

    /**
     * 重新封装一层Supplier
     */
    public Inheritable(String tenantId, Supplier<T> supplier, Executor executor){
        this.future = CompletableFuture.supplyAsync(() -> {
            try {
                TenantContextHolder.setLocalTenantId(tenantId);
                // 执行任务
                return supplier.get();
            }catch (Exception e){
                throw new IllegalStateException(e);
            }finally {
                TenantContextHolder.removeLocalTenantId();
            }
        }, executor);
    }

    /**
     * 获取结果最大等等时间两秒
     */
    public T get() throws ExecutionException, InterruptedException{
        return future.get();
    }

    /**
     * 获取结果最大等等时间两秒
     */
    public T getValue() {
        try {
            return future.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
