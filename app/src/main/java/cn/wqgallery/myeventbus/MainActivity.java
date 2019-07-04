package cn.wqgallery.myeventbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        tv = findViewById(R.id.tv);
        new Thread() {
            @Override
            public void run() {
                super.run();
                tv.setText("改变测试");
            }
        }.start();

        //注册
        EventBus.getDefault().register(this);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销
        EventBus.getDefault().unregister(this);

    }


    //跳转
    public void click1(View view) {
        startActivity(new Intent(this, OneActivity.class));
    }

    //接受消息
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getMessage(Bean bean) {
        Log.e("main","------"+Thread.currentThread().getName());
    }

    //发送粘性消息
    public void click2(View view) {
        EventBus.getDefault().postSticky(new Bean("王四", 25));
        startActivity(new Intent(this, OneActivity.class));
    }


}
