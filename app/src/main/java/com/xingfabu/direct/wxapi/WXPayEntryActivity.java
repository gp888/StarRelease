package com.xingfabu.direct.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xingfabu.direct.R;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.ui.WatchActivity;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        
    	api = WXAPIFactory.createWXAPI(this, UrlConstants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {

	}

	@Override
	public void onResp(BaseResp resp) {
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			int ret_code = resp.errCode;
			Intent intent = new Intent(this,WatchActivity.class);
			if(ret_code == 0){//支付成功
				Toast.makeText(this,"打赏成功",Toast.LENGTH_SHORT).show();
				intent.putExtra("paystatus","100");
			}else if(ret_code == -1){//异常
				Toast.makeText(this,"打赏失败...",Toast.LENGTH_SHORT).show();
				intent.putExtra("paystatus","99");
			}else if(ret_code == -2){//用户取消
				Toast.makeText(this,"取消打赏...",Toast.LENGTH_SHORT).show();
				intent.putExtra("paystatus","98");
			}
			startActivity(intent);
			finish();
		}
	}
}