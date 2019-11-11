package com.xingfabu.direct.newpackage;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xingfabu.direct.BuildConfig;
import com.xingfabu.direct.R;
import com.xingfabu.direct.ui.MainActivity;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {
    private static Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_login);
        TextView register = (TextView) this.findViewById(R.id.login_register);
        register.setClickable(true);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        TextView login = (TextView) this.findViewById(R.id.login_btn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText login_phone = (EditText) findViewById(R.id.login_phone);
                String phone = login_phone.getText().toString();
                if (phone.equals("")) {
                    Toast.makeText(LoginActivity.this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                EditText login_password = (EditText) findViewById(R.id.login_password);
                String password = login_password.getText().toString();
                if (password.equals("")) {
                    Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String appId = BuildConfig.AppIdConfig;

                final Map<String, String> map = new HashMap<>();
                map.put("Phone", phone);
                map.put("Password", password);
                map.put("AppID", appId);
                String data = URLConnectionTem.getHttp().postRequset("api/user/login", map);
                try {
                    JSONObject root = new JSONObject(data);
                    String returnCode = root.getString("ReturnCode");
                    JSONObject rootData = new JSONObject(returnCode);
                    if (!rootData.getString("Code").equals("0")) {
                        Toast.makeText(LoginActivity.this, rootData.getString("Message"), Toast.LENGTH_LONG).show();
                    } else {
                        String contentData = root.getString("Content");
                        JSONObject contentDataChild = new JSONObject(contentData);
                        if (contentDataChild.getString("Code").equals("0")) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else if (contentDataChild.getString("Code").equals("1")) {
	                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else if (contentDataChild.getString("Code").equals("2")) {
                            Intent intent = new Intent(LoginActivity.this, MWeb.class);
                            intent.putExtra("url", contentDataChild.getString("Url"));
                            startActivity(intent);
                        }
                    }
                } catch (Exception ex) {

                }

            }
        });

    }
}
