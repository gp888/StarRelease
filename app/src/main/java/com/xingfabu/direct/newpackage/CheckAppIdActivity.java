package com.xingfabu.direct.newpackage;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.xingfabu.direct.BuildConfig;
import com.xingfabu.direct.R;
import com.xingfabu.direct.ui.MainActivity;

import org.json.JSONObject;


public class CheckAppIdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_app_id);


       String data= URLConnectionTem.getHttp().getRequset("api/switch?appId="+ BuildConfig.AppIdConfig);
        try {
            JSONObject jsonData = new JSONObject(data);
            String returnCode = jsonData.getString("ReturnCode");
            JSONObject rootData = new JSONObject(returnCode);
            if (!rootData.getString("Code").equals("0")) {
                Toast.makeText(CheckAppIdActivity.this, rootData.getString("Message"), Toast.LENGTH_LONG).show();
            } else {
                String contentData = jsonData.getString("Content");
                JSONObject contentDataChild = new JSONObject(contentData);
                  if(contentDataChild.getString("Code").equals("0")){
                      Intent intent=new Intent(CheckAppIdActivity.this, MainActivity.class);
                      startActivity(intent);
                }
                else if(contentDataChild.getString("Code").equals("1")){
                    Intent intent=new Intent(CheckAppIdActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
                else if(contentDataChild.getString("Code").equals("2")){
                      Intent intent=new Intent(CheckAppIdActivity.this,MWeb.class);
                      intent.putExtra("url", contentDataChild.getString("Url"));
                      startActivity(intent);
                  }
            }
        } catch (Exception ex) {

        }

    }
}
