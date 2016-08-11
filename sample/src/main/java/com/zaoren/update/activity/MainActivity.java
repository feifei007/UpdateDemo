package com.zaoren.update.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ypn.update.UpdateFunGo;
import com.ypn.update.config.UpdateKey;
import com.zaoren.update.R;
import com.zaoren.update.thread.UpdateThread;

/**
 * Created by Administrator on 2016/8/11.
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UpdateKey.API_TOKEN = "4d4f1e2b9c75fab4d28528cbad7a743e";
        UpdateKey.APP_ID = "57ac73a3959d6918c5001773";
        initView();
    }

    private void initView() {
        UpdateThread thread = new UpdateThread();
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UpdateFunGo.init(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateFunGo.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        UpdateFunGo.onStop();
    }
}

