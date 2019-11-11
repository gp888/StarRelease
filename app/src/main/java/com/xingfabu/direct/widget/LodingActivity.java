package com.xingfabu.direct.widget;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.xingfabu.direct.R;

/**
 * Created by guoping on 16/4/18.
 */
public class LodingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                startActivity(new Intent(LodingActivity.this,MainActivity.class));
//                finish();
                Toast.makeText(LodingActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
            }
        },3000);
    }
}
