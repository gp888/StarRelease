package com.xingfabu.direct.widget;

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.xingfabu.direct.R;

/**
 * Created by guoping on 16/8/1.
 */
public class RedPacketDialog {
    private AppCompatActivity activity;
    private AlertDialog dialog;
    private String title;
    public RedPacketDialog(AppCompatActivity activity, String title) {
        this.activity = activity;
        this.title = title;
    }

    public void showDialog(){
        dialog=new AlertDialog.Builder(activity).create();
        //点击外部区域不能取消dialog
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnKeyListener(keylistener);
        dialog.show();

        Window window = dialog.getWindow();
        window.setContentView(R.layout.red_package);
        TextView tv_title = (TextView) window.findViewById(R.id.dialog_title);
        tv_title.setText(title);
        TextView tv_confirm = (TextView) window.findViewById(R.id.tv_confirm);
        TextView tv_cancel = (TextView) window.findViewById(R.id.tv_cancel);
        tv_confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //activity.getSegment("1","","0.01",xfbsid,share_title);
                dialog.dismiss();
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                Toast.makeText(activity, "取消", Toast.LENGTH_SHORT).show();
            }
        });

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    public static DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener(){
        public boolean onKey(DialogInterface dialog,  int keyCode, KeyEvent event) {
            if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    } ;
}
