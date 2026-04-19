package org.ysling.litemall.core.utils.Inheritable;


import java.lang.reflect.Field;
import java.util.concurrent.Callable;

/**
 * TODO 解决线程池中InheritableThreadLocal无法获取父类存储信息问题
 * 该类添加了缓存
 *
 * 1、定义一个InheritableTask抽象类，这个类实现了Runaable接口，
 * 并定义了一个runTask抽象方法，当开发者需要面对线程池获取InheritableThreadLocal值的场景时，
 * 提交的任务对象，只需要继承InheritableTask类，实现runTask方法即可。
 *
 * 2、在创建任务类时，也就是在InheritableTask构造函数中，通过反射获，
 * 获取到提交任务的业务线程的inheritableThreadLocals属性，然后复制一份，
 * 暂存到当前task的inheritableThreadLocalsObj属性中。
 *
 * 3、线程池线程在执行该任务时，其实就是去调用其run()方法，在执行run方法时，
 * 先将暂存的inheritableThreadLocalsObj属性，赋值给当前执行任务的线程，
 * 这样这个线程就可以得到提交任务的那个业务线程的inheritableThreadLocals属性值了。
 * 然后再去执行runTask(),就是真正的业务逻辑。最后，finally清理掉执行当前业务的线程的inheritableThreadLocals属性。
 *
 * @author Ysling
 */
public abstract class InheritableCallable<V> extends TaskWithCache implements Callable<V> {

    public InheritableCallable(){
        super();
    }

    /**
     * 搞个代理方法，这个方法中处理业务逻辑
     */
    public abstract V runTask();

    @Override
    public V call() {
        Thread currentThread = Thread.currentThread();
        Field field = getInheritableThreadLocalsField();
        try {
            if (obj != null && field != null) {
                field.set(currentThread, obj);
                obj = null;
            }
            // 执行任务
            return runTask();
        }catch (Exception e){
            throw new IllegalStateException(e);
        }finally {
            // 最后将线程中的InheritableThreadLocals设置为null
            if (field != null){
                try {
                    field.set(currentThread , null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
