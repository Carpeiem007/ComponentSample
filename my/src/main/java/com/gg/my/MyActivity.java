package com.gg.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.gg.annotation.Parameter;
import com.gg.annotation.Route;
import com.gg.common.activity.BaseActivity;
import com.gg.common.route.parameter.ParameterManager;

@Route(":home")
public class MyActivity extends BaseActivity {
    @Parameter()
    int age;
    @Parameter()
    String name;
    @Parameter()
    String address;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ParameterManager.bindParameter(this);
        Toast.makeText(this, "name = " + name + "   age = " + age, Toast.LENGTH_LONG).show();
    }

}
