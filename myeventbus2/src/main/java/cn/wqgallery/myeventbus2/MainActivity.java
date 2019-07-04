package cn.wqgallery.myeventbus2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import cn.wqgallery.myeventbus2.eventbus.EventBus;
import cn.wqgallery.myeventbus2.eventbus.Subscriber;
import cn.wqgallery.myeventbus2.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //注册
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销
        EventBus.getDefault().unRegister(this);
    }

    @Subscriber(threadMode=ThreadMode.MAIN)
    public void getMessage(Bean bean) {
        Toast.makeText(this, "测试", Toast.LENGTH_SHORT).show();
        Log.e("zdh", "----------" + bean.toString());
    }

    public void click(View view) {
        startActivity(new Intent(this,TextActivity.class));
    }
}
