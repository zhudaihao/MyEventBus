package cn.wqgallery.myeventbus2.eventbus;

import java.lang.reflect.Method;

//注册类的 注解方法信息
public class SubscriberMethod {
    //注解方法
    private Method method;

    //注解线程
    private ThreadMode threadMode;

    //方法参数类型 比如 void text(String content){} 这个方法参数类型为string
    private Class<?> eventType;


    public SubscriberMethod(Method method, ThreadMode threadMode, Class<?> eventType) {
        this.method = method;
        this.threadMode = threadMode;
        this.eventType = eventType;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(ThreadMode threadMode) {
        this.threadMode = threadMode;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public void setEventType(Class<?> eventType) {
        this.eventType = eventType;
    }
}
