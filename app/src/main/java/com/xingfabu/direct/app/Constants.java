package com.xingfabu.direct.app;

public class Constants {
	public static final int TYPE_SELF = 0;//自助式
	public static final int TYPE_STREAM = 1;//流式
	public static int sdk_type = -1;
	public static final String APP_KEY = "1285992200";
	public static final String APP_SECRET_KEY = "9bccfb3d463dce01720c420317000287";
	public static final String SECRET_KEY = "8b8c4a4af575119d7b2b28723c7cd3dc";

	//1	直播2	预约3	结束4	回放
	public static String getStatusStr(int type){
		String statusStr = "";
		switch (type) {
		case 1:
			statusStr = "直播";
			break;
		case 2:
			statusStr = "预约";
			break;
		case 3:
			statusStr = "结束";
			break;
		case 4:
			statusStr = "回放";
			break;

		default:
			break;
		}
		return "当前直播处在"+statusStr+"状态！";
	}


	public static final int  BAPINGBANG = 0;
	public static final int  TUIJIAN = 1;
	public static final int  FENLEI = 2;
	public static final int  SHOUCANG = 3;
	public static final int  BOFANGJILU = 4;
	public static final int  SOUSUO = 5;
	public static final int RELATED = 6;
	public static final int STAR_INFO = 7;
}
