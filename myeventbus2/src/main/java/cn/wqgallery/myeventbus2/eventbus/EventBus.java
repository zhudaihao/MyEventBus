package cn.wqgallery.myeventbus2.eventbus;


import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 手写EventBus
 */
public class EventBus {
    static volatile EventBus eventBus;
    private Handler handler;
    private ExecutorService executorService;

    public static EventBus getDefault() {
        if (eventBus == null) {
            synchronized (EventBus.class) {
                if (eventBus == null) {
                    eventBus = new EventBus();
                }
            }
        }
        return eventBus;
    }

    private EventBus() {
        cacheMap = new HashMap<>();
        //创建handler设置为UI线程的
        handler = new Handler(Looper.getMainLooper());
        //创建线程池
        executorService = Executors.newCachedThreadPool();

    }

    //定义缓存map装载注册者 注解方法信息
    private Map<Object, List<SubscriberMethod>> cacheMap;

    //注册
    public void register(Object object) {
        Class<?> aClass = object.getClass();
        //从缓存获取注册者 注解方法信息
        List<SubscriberMethod> subscriberMethods = cacheMap.get(aClass);

        //缓存中存在说明已经注册了就不需要注册，空就去注册
        if (subscriberMethods == null) {
            subscriberMethods = getSubscriberMethod(object);
            //注册后放进缓存
            cacheMap.put(object, subscriberMethods);
        }

    }

    //注册
    private List<SubscriberMethod> getSubscriberMethod(Object object) {
        //通过反射 遍历获取注册者使用注解的方法 放进集合
        List<SubscriberMethod> list = new ArrayList<>();

        Class<?> aClass = object.getClass();
        //通过while循环从注册者--->注册者父类--一层层找 使用注解的方法，
        // 但是找到你源码的activity时我们不需要处理，所有需要对找到源码时做处理
        while (aClass != null) {
            //通过获取类名判断是不是源码类 如果是就返回出循环
            String name = aClass.getName();
            if (name.startsWith("java.") ||
                    name.startsWith("javax.") ||
                    name.startsWith("android.") ||
                    name.startsWith("androidx.")) {

                break;
            }
            //通过反射 获取注册者类 方法集合
            Method[] declaredMethods = aClass.getDeclaredMethods();
            //循环集合判断是否使用注解
            for (Method method : declaredMethods) {
                //通过反射获取注解类集合
                Subscriber annotation = method.getAnnotation(Subscriber.class);
                //如果注解类为空 就结束本次循环
                if (annotation == null) {
                    continue;

                }

                //反射获取方法参数类型
                Class<?>[] parameterTypes = method.getParameterTypes();
                //EventBus接受方法参数只有一个 ，通过这个可以过滤掉多参数 方法
                if (parameterTypes.length != 1) {
                    //异常提示
                    throw new RuntimeException("EventBus方法参数只能一个");
                }

                //获取 注解类线程
                ThreadMode threadMode = annotation.threadMode();

                //给注解方法信息 类赋值(方法，方法运行线程，parameterTypes[0]因为表示)
                SubscriberMethod subscriberMethod = new SubscriberMethod(method, threadMode, parameterTypes[0]);
                //添加到集合
                list.add(subscriberMethod);
            }


            //给aClass赋值为父类实现循环
            aClass = aClass.getSuperclass();
        }


        return list;
    }


    //反注册
    public void unRegister(Object object) {
        Class<?> aClass = object.getClass();
        List<SubscriberMethod> list = cacheMap.get(aClass);

        if (list != null) {
            cacheMap.remove(object);
        }

    }

    //发送方法 的参数是发送数据对象
    public void post(final Object event) {
        //获取map键集合
        Set<Object> objects = cacheMap.keySet();
        Iterator<Object> iterator = objects.iterator();
        while (iterator.hasNext()) {
            //获取map键（注册类）
            final Object className = iterator.next();

            //通过缓存map获取 注册类的 注解方法集合
            List<SubscriberMethod> list = cacheMap.get(className);
            //遍历方法集合
            for (final SubscriberMethod subscriberMethod : list) {
                //判断方法参数类型是否是和发送方法类型一致
                //isAssignableFrom判断括号里面类是否是调用这方法类的子类或者对象
                if (subscriberMethod.getEventType().isAssignableFrom(event.getClass())) {
                    //线程处理
                    switch (subscriberMethod.getThreadMode()) {
                        case POSTING:
                            invoke(subscriberMethod, className, event);
                            break;

                        case MAIN:
                            //判断当前线程是不是UI线程
                            if (Looper.myLooper() == Looper.getMainLooper()) {
                                invoke(subscriberMethod, className, event);
                            } else {
                                //子线程
                                //使用handler切换到主线程
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscriberMethod, className, event);
                                    }
                                });

                            }
                            break;

                        case MAIN_ORDERED:
                            //判断当前线程是不是UI线程
                            if (Looper.myLooper() == Looper.getMainLooper()) {
                                invoke(subscriberMethod, className, event);
                            } else {
                                //子线程
                                //使用handler切换到主线程
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscriberMethod, className, event);
                                    }
                                });

                            }
                            break;

                        case BACKGROUND:
                            //判断当前线程是不是UI线程
                            if (Looper.myLooper() == Looper.getMainLooper()) {
                                //主线程 切换到子线程
                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscriberMethod, className, event);
                                    }
                                });
                            } else {
                                invoke(subscriberMethod, className, event);
                            }
                            break;

                        case ASYNC:
                            //判断当前线程是不是UI线程
                            if (Looper.myLooper() == Looper.getMainLooper()) {
                                //主线程 切换到子线程
                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscriberMethod, className, event);
                                    }
                                });
                            } else {
                                invoke(subscriberMethod, className, event);
                            }
                            break;
                    }


                }
            }


        }


    }

    //反射 给注册类方法赋值
    private void invoke(SubscriberMethod subscriberMethod, Object className, Object event) {
        Method method = subscriberMethod.getMethod();
        try {
            //反射参数（方法所在的类，方法值）
            method.invoke(className, event);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
