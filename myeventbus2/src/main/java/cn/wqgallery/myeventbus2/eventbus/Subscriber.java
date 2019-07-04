package cn.wqgallery.myeventbus2.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscriber {
    //定义线程
    ThreadMode threadMode() default ThreadMode.POSTING;


}
