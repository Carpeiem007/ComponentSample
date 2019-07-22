package com.gg.my;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gg.annotation.Parameter;
import com.gg.annotation.Route;

@Route("main")
public class MainActivity extends AppCompatActivity {
    @Parameter()
    int sex ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
