package com.xingfabu.direct.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import com.xingfabu.direct.R;

/**
 * 没有使用
 */
public class NetLoadingDialog extends Dialog implements OnDismissListener { 
    
	private static NetLoadingDialog instance = null;
	private static volatile Context mContext;
	private static volatile int show_flg = 0;
	private static volatile boolean isContextChanged;
	private static Object object = new Object();
	
	private NetLoadingDialog(Context context) {
    	super(context, R.style.Loading);

    	show_flg = 0;
    	mContext = context;
    	isContextChanged = false;
    	setContentView(R.layout.loading_layout);
	    Window window = getWindow();
	    WindowManager.LayoutParams params = window.getAttributes();
	    params.width = -1;
	    params.height = -2;
	    params.gravity = Gravity.CENTER;
	    window.setAttributes(params);
	    setOnDismissListener(this);
	    setCancelable(false);
    }
   
    public static synchronized void Show(Context context) {  
		if(instance == null ){
    		instance = new NetLoadingDialog(context);
    	}
    	else if(instance != null && context != mContext){    		
    		if(instance.isShowing())
    		{
    			isContextChanged = true;
    			instance.dismiss();
    			return;
    		}
    		else{
    			instance = new NetLoadingDialog(context);
    		}
    	}
    	
    	show_flg ++;
    	instance.show();
    }
    
    public static synchronized void Dismiss(Context context) {
    	if(context == mContext){
    		show_flg --;
        	if(show_flg <= 0){
        		show_flg = 0;
        		instance.dismiss();
        	}
    	}
    }

	@Override
	public void onDismiss(DialogInterface dialog) {
		synchronized(object){
			if(isContextChanged){
				instance = new NetLoadingDialog(mContext);
				show_flg ++;
		    	instance.show();
			}
		}
	}
}
