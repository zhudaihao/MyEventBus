package cn.wqgallery.myeventbus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class OneActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);

        //注册
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //接受粘性消息
    @Subscribe(sticky = true)
    public void getStickMessage(Bean bean) {
//        Log.e("one","--------"+bean.toString());
    }

    public void click1(View view) {
        //发送消息
        new Thread(){
            @Override
            public void run() {
                super.run();
                EventBus.getDefault().post(new Bean("zhangsan", 18));
                finish();
                Log.e("one","--------"+Thread.currentThread().getName());

            }
        }.start();

    }
}
