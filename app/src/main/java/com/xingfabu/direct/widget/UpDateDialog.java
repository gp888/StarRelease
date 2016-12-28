package com.xingfabu.direct.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by guoping on 16/4/28.
 */
public class UpDateDialog extends Dialog {

    public UpDateDialog(Context context, int layout, int style) {
        super(context, style);
        setContentView(layout);
        setCancelable(false);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = -1;
        params.height = -2;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }
}
