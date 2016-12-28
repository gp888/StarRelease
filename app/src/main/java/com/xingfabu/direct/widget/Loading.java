package com.xingfabu.direct.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import com.xingfabu.direct.R;

/**
 * Created by guoping on 16/4/11.
 */
public class Loading extends Dialog {

    //public abstract void cancle();

    public Loading(Context context) {
        super(context, R.style.Loading);
        setContentView(R.layout.loading_layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void onBackPressed() {
        //cancle();
        dismiss();
    }
}
