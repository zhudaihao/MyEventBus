package cn.wqgallery.myeventbus2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.wqgallery.myeventbus2.eventbus.EventBus;

public class TextActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
    }

    public void click(View view) {
        //发送消息
        new Thread(){
            @Override
            public void run() {
                super.run();
                EventBus.getDefault().post(new Bean("zhang",18));
            }
        }.start();

    }
}
