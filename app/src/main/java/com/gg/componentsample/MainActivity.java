package com.gg.componentsample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.gg.common.route.RouteManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void turn(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("age",22);
        bundle.putString("name","张三");
        RouteManager.startRoute("My:home").with(bundle).create().launchActivity(this,0);
    }
}
