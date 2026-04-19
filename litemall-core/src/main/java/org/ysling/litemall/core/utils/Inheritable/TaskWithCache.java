package org.ysling.litemall.core.utils.Inheritable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 获取主线程存储信息问题
 */
public class TaskWithCache {

    public Object obj;
    private static volatile Field inheritableThreadLocalsField;
    private static volatile Class<?> threadLocalMapClazz;
    private static volatile Method createInheritedMapMethod;
    private static final Object accessLock = new Object();

    public TaskWithCache(){
        try{
            Thread currentThread = Thread.currentThread();
            Field field = getInheritableThreadLocalsField();
            // 得到当前线程中的inheritableThreadLocals熟悉值ThreadLocalMap, key是各种inheritableThreadLocal，value是值
            Object threadLocalMapObj = field.get(currentThread);
            if(threadLocalMapObj != null){
                Class<?> threadLocalMapClazz = getThreadLocalMapClazz();
                Method method =  getCreateInheritedMapMethod(threadLocalMapClazz);
                // 创建一个新的ThreadLocalMap
                obj = method.invoke(ThreadLocal.class,threadLocalMapObj);
            }
        }catch (Exception e){
            throw new IllegalStateException(e);
        }
    }

    private Class<?> getThreadLocalMapClazz(){
        if(inheritableThreadLocalsField == null){
            return null;
        }else {
            if(threadLocalMapClazz == null){
                synchronized (accessLock){
                    if(threadLocalMapClazz == null){
                        threadLocalMapClazz = inheritableThreadLocalsField.getType();
                    }
                }
            }
        }
        return threadLocalMapClazz;
    }

    public Field getInheritableThreadLocalsField(){
        if(inheritableThreadLocalsField == null){
            synchronized (accessLock){
                if(inheritableThreadLocalsField == null){
                    try {
                        Field field = Thread.class.getDeclaredField("inheritableThreadLocals");
                        field.setAccessible(true);
                        inheritableThreadLocalsField = field;
                    }catch (Exception e){
                        throw new IllegalStateException(e);
                    }
                }
            }
        }
        return inheritableThreadLocalsField;
    }

    private Method getCreateInheritedMapMethod(Class<?> threadLocalMapClazz){
        if(threadLocalMapClazz != null && createInheritedMapMethod == null){
            synchronized (accessLock){
                if(createInheritedMapMethod == null){
                    try {
                        Method method =  ThreadLocal.class.getDeclaredMethod("createInheritedMap",threadLocalMapClazz);
                        method.setAccessible(true);
                        createInheritedMapMethod = method;
                    }catch (Exception e){
                        throw new IllegalStateException(e);
                    }
                }
            }
        }
        return createInheritedMapMethod;
    }
}
