package com.joe.broom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Description
 * Created by chenqiao on 2015/12/4.
 */
public class DemoActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        findViewById(R.id.jump_1).setOnClickListener(this);
        findViewById(R.id.jump_2).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.jump_1:
                startActivity(new Intent(this, CleanOneActivity.class));
                break;
            case R.id.jump_2:
                startActivity(new Intent(this, CleanTwoActivity.class));
                break;
        }
    }
}
