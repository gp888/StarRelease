package com.xingfabu.direct.ui;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;
import com.vhall.business.MessageServer;
import com.vhall.business.VhallSDK;
import com.vhall.business.Watch;
import com.vhall.business.WatchLive;
import com.vhall.business.WatchPlayback;
import com.vhall.business.widget.ContainerLayout;
import com.vhall.playersdk.player.impl.VhallHlsPlayer;
import com.xingfabu.direct.R;
import com.xingfabu.direct.adapter.MyFragmentpagerAdapter;
import com.xingfabu.direct.app.App;
import com.xingfabu.direct.app.Constants;
import com.xingfabu.direct.app.Param;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.cache.SPCache;
import com.xingfabu.direct.emoji.StringUtil;
import com.xingfabu.direct.entity.AllResponse;
import com.xingfabu.direct.entity.BarrageList;
import com.xingfabu.direct.entity.BaseResponse;
import com.xingfabu.direct.entity.PayBean;
import com.xingfabu.direct.entity.VideoDetail;
import com.xingfabu.direct.fragment.RelatedFragment;
import com.xingfabu.direct.fragment.TalkFragment;
import com.xingfabu.direct.fragment.VideoDetailFragment;
import com.xingfabu.direct.heart.PeriscopeLayout;
import com.xingfabu.direct.transform.GlideCircleTransform;
import com.xingfabu.direct.utils.MD5Helper;
import com.xingfabu.direct.utils.MyStringCallback;
import com.xingfabu.direct.utils.PayResult;
import com.xingfabu.direct.utils.ScreenSizeUtil;
import com.xingfabu.direct.utils.StarDanmakuParser;
import com.xingfabu.direct.utils.StarReleaseUtil;
import com.xingfabu.direct.utils.VhallUtil;
import com.xingfabu.direct.widget.UpDateDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import io.rong.message.TextMessage;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.android.JSONSource;
import master.flame.danmaku.ui.widget.DanmakuView;
import okhttp3.Call;
import com.xingfabu.direct.entity.AllResponse.DailyShare;
import com.xingfabu.direct.entity.AllResponse.AcSupport;

public class WatchActivity extends BaseActivity implements View.OnClickListener,GestureDetector.OnGestureListener,View.OnTouchListener{
	private static final String TAG = "WatchActivity";
	private ContainerLayout rl_video_container;//视频区容器
	private ImageView iv_play, iv_fullscreen;
	private SeekBar seekbar;
	private TextView tv_pos,tv_support,tv_big_title;
	public ProgressBar pb;
	private WatchPlayback watchPlayback;
	private WatchLive watchLive;
	private long playerDuration;
	private String playerDurationTimeStr = "00:00:00";
	public Param param;
	private Timer timer;
	private long playerCurrentPosition = 0L;
	public boolean isWatching = false;//直播
	private TextView download_speed;
	int screenWidthPx;
	int screenHeightPx;
	private String xfbsid = null;
	private String xfbwebinar_id = null;
	private UMImage shareImage = null;

	private boolean is_close;//活动开始
	private int surplus;//剩余次数
	private int from;//来自

	public DanmakuView mDanmakuView;//弹幕层
	private RelativeLayout root_layout,rl_danlayout,video_layout;
	private LinearLayout ll_bottom_bar,ll_back_bar;

	private TabLayout tabLayout;
	private ViewPager viewPager;
	private List<String> tabs;
	private List<Fragment> fragments;
	private MyFragmentpagerAdapter adapter;
	private String danmuArray = null,nickName;

	private DanmakuContext mContext;
	private BaseDanmakuParser mParser;
	private boolean is_like;
	private ImageView tv_enjoy,iv_heart,iv_edit,iv_danmuke,	iv_money,iv_back,iv_share,iv_damuke_pori;

	private LinearLayout gesture_volume_layout,gesture_bright_layout,gesture_progress_layout;//音量控制布局,亮度控制布局,快进布局
	private TextView geture_tv_volume_percentage, geture_tv_bright_percentage;// 音量百分比,亮度百分比
	private ImageView gesture_iv_player_volume, gesture_iv_player_bright;// 音量图标,亮度图标
	private ImageView gesture_iv_progress;// 快进或快退标志
	private TextView geture_tv_progress_time;// 播放时间进度
	private PeriscopeLayout periscopeLayout;
	private LinearLayout ll_actions;
	private String start_time = null;//直播开始的时间戳
	private String share_title = null;
	private String share_content = null;
	private int heart_count = 0;//接口获得的爱心的数量
	private String shareUrl = null;
	private IWXAPI api;
	private int blood = 10;//能量值,10个阶段
	private int temp_heart_count = 0;//临时储存的
	private EditText et_content;
	private boolean isDanmuOn = true;
	private boolean firstScroll = false;// 每次触摸屏幕后，第一次scroll的标志
	private int GESTURE_FLAG = 0;// 1,调节进度，2，调节音量,3.调节亮度
	/** 视频窗口的宽和高 */
	private int playerWidth, playerHeight;
	private static final int GESTURE_MODIFY_PROGRESS = 1;
	private static final int GESTURE_MODIFY_VOLUME = 2;
	private static final int GESTURE_MODIFY_BRIGHT = 3;
	private static final float STEP_PROGRESS = 2f;// 设定进度滑动时的步长，避免每次滑动都改变，导致改变过快
	private static final float STEP_VOLUME = 2f;// 协调音量滑动时的步长，避免每次滑动都改变，导致改变过快
	private float mBrightness = -1f; // 亮度
	private AudioManager audiomanager;
	private int maxVolume, currentVolume;
	private GestureDetector gestureDetector;
	String red_money = "";
	private final int SDK_PAY_FLAG = 1;
	private TalkFragment talkFragment;
	private RelatedFragment relatedFragment;
	private VideoDetailFragment videoDetailFragment;
	private RelativeLayout rl_video_adver;
	private ImageView video_adver,close_video_adver;
	private String ads_pic,ads_url;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case SDK_PAY_FLAG:
					rePlay();
					PayResult payResult = new PayResult((String) msg.obj);
					String resultInfo = payResult.getResult();// 同步返回需要验证的信息
					String resultStatus = payResult.getResultStatus();
					if (TextUtils.equals(resultStatus, "9000")) {
						redPacketResult();
					} else if (TextUtils.equals(resultStatus, "8000")) {
						showToast("打赏结果确认中");
					} else if(TextUtils.equals(resultStatus, "6001")) {
						showToast("取消打赏");
					}else if(TextUtils.equals(resultStatus,"6002")){
						showToast("请检查网络");
					}
					break;
				case 0:
					if (getWatchPlayback().isPlaying()) {
						playerCurrentPosition = getWatchPlayback().getCurrentPosition();
						seekbar.setProgress((int) playerCurrentPosition);
						String playerCurrentPositionStr = VhallUtil.converLongTimeToStr(playerCurrentPosition);
						setProgressLabel(playerCurrentPositionStr + "/" + playerDurationTimeStr);
					}
					break;
				default:
					break;
			}
		}
	};

	BroadcastReceiver netWorkRecever = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_NETWORK_CHANGE.equals(action)) { // 网络发生变化
				int i = getNetype(context);
				if(-1 == i){
					showToast("请检查网络");
				}else if(1 == i){
					//showToast("WiFi状态");
				}else{//数据流量
					if(param.watch_type == Param.WATCH_PLAYBACK){
						paushPlay();
					}else{
						stopWatch();
					}
					AlertDialog.Builder dialog = new AlertDialog.Builder(WatchActivity.this);
					dialog.setMessage("当前非WiFi环境,是否继续播放");
					dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(param.watch_type == Param.WATCH_PLAYBACK){
								startPlay();
							}else {
								getWatchLive().start();
							}
						}
					});

					dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					AlertDialog simpledialog=dialog.create();
					simpledialog.show();
				}
			}
		}
	};
	//分享
	private UMShareListener umShareListener = new UMShareListener() {
		@Override
		public void onResult(SHARE_MEDIA platform) {
			com.umeng.socialize.utils.Log.d("plat","platform"+platform);
			rePlay();
			if(platform.name().equals("WEIXIN_FAVORITE")){
				showToast(platform + "收藏成功");
			}else{
				dailyShare(xfbwebinar_id,from);
			}
		}
		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			rePlay();
			showToast(platform + "分享失败");
		}
		@Override
		public void onCancel(SHARE_MEDIA platform) {
			rePlay();
			showToast(platform + "分享取消");
		}
	};

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_watch);

		Intent fromItent = getIntent();
		param = (Param) fromItent.getSerializableExtra("param");
		xfbsid = fromItent.getStringExtra("sid");
		xfbwebinar_id = param.id;
		String share_pic = fromItent.getStringExtra("share_pic");
		shareImage = new UMImage(WatchActivity.this,share_pic);

		is_close = fromItent.getBooleanExtra("is_close",true);
		surplus = fromItent.getIntExtra("surplus",0);
		from = fromItent.getIntExtra("from", Constants.TUIJIAN);
	}

	@Override
	public void initViews() {
		video_layout = (RelativeLayout) findViewById(R.id.video_layout);
		rl_video_container = (ContainerLayout)findViewById(R.id.rl_video_container);
		mDanmakuView = (DanmakuView) findViewById(R.id.sv_danmaku);//弹幕层
		root_layout = (RelativeLayout) findViewById(R.id.root_layout);//亮度，声音
		ll_bottom_bar = (LinearLayout) findViewById(R.id.ll_bottom_bar);//底部条
		ll_back_bar = (LinearLayout) findViewById(R.id.ll_back_bar);//头部条
		rl_danlayout = (RelativeLayout) findViewById(R.id.rl_danlayout);//发送弹幕

		viewPager = (ViewPager) findViewById(R.id.viewPager1);
		tabLayout = (TabLayout) findViewById(R.id.watch_tab);

		pb = (ProgressBar)findViewById(R.id.pb);
		download_speed = (TextView) findViewById(R.id.download_speed);
		gesture_volume_layout = (LinearLayout) findViewById(R.id.gesture_volume_layout);
		gesture_iv_player_volume = (ImageView) findViewById(R.id.gesture_iv_player_volume);
		geture_tv_volume_percentage = (TextView) findViewById(R.id.geture_tv_volume_percentage);
		gesture_bright_layout = (LinearLayout) findViewById(R.id.gesture_bright_layout);
		gesture_iv_player_bright = (ImageView) findViewById(R.id.gesture_iv_player_bright);
		geture_tv_bright_percentage = (TextView) findViewById(R.id.geture_tv_bright_percentage);
		gesture_progress_layout = (LinearLayout) findViewById(R.id.gesture_progress_layout);
		gesture_iv_progress = (ImageView) findViewById(R.id.gesture_iv_progress);
		geture_tv_progress_time = (TextView) findViewById(R.id.geture_tv_progress_time);
		periscopeLayout = (PeriscopeLayout) findViewById(R.id.heart_layout);
		iv_heart = (ImageView) findViewById(R.id.iv_heart);
		tv_support = (TextView) findViewById(R.id.tv_support);
		ll_actions = (LinearLayout) this.findViewById(R.id.ll_actions);//暂停开始功能布局
		iv_play = (ImageView)findViewById(R.id.iv_play);
		seekbar = (SeekBar)findViewById(R.id.seekbar);
		tv_pos = (TextView)findViewById(R.id.tv_pos);
		iv_edit = (ImageView)findViewById(R.id.iv_edit);
		iv_danmuke = (ImageView) this.findViewById(R.id.iv_danmuke);
		iv_money = (ImageView) this.findViewById(R.id.iv_money);
		iv_fullscreen = (ImageView)findViewById(R.id.iv_fullscreen);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		tv_big_title = (TextView) this.findViewById(R.id.tv_big_title);//横屏title
		tv_enjoy = (ImageView) findViewById(R.id.tv_enjoy);
		iv_share = (ImageView) findViewById(R.id.iv_share);
		iv_damuke_pori = (ImageView) findViewById(R.id.iv_damuke_pori);
		et_content = (EditText) findViewById(R.id.et_content);

		gestureDetector = new GestureDetector(this, this);
		root_layout.setLongClickable(true);
		gestureDetector.setIsLongpressEnabled(true);
		root_layout.setOnTouchListener(this);
		audiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume = audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 获取系统最大音量
		currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值

		rl_video_adver = (RelativeLayout) findViewById(R.id.rl_video_adver);
		video_adver = (ImageView) findViewById(R.id.video_adver);
		close_video_adver = (ImageView) findViewById(R.id.close_video_adver);

		seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				setProgressLabel(VhallUtil.converLongTimeToStr(progress) + "/" + playerDurationTimeStr);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				onStopTracking(seekBar);
				//initDamu();//快进的时候重新初始化弹幕引擎
			}
		});

		//VhallSDK.getInstance().setLogEnable(true);//调试
		showProgressbar(true);
		if(param.watch_type == Param.WATCH_PLAYBACK){
			initWatchPlayback();
		}else {
			seekbar.setVisibility(View.INVISIBLE);
			tv_pos.setVisibility(View.INVISIBLE);
			initWatchLive();
		}
		mHandler.post(hide_bars);
		screenWidthPx = VhallUtil.getScreenWidth(WatchActivity.this);
		screenHeightPx = VhallUtil.getScreenHeight(WatchActivity.this);

		initLayout();//fragment

		getBarrage(xfbwebinar_id);//获取弹幕
		getVideoDetail(xfbsid,xfbwebinar_id);
		getSupport(xfbwebinar_id);//获取点赞数量
		daShangBtn();//打赏按钮
	}

	@Override
	public void initListeners() {
		api = WXAPIFactory.createWXAPI(WatchActivity.this,UrlConstants.APP_ID);
	}

	@Override
	public void initData() {

	}

	private void initLayout(){
		tabs = new ArrayList<>();
		tabs.add("推荐");
		tabs.add("评论");
		tabs.add("详情");
		fragments = new ArrayList<>();
		relatedFragment = RelatedFragment.newInstance(xfbwebinar_id);
		fragments.add(relatedFragment);
		talkFragment = TalkFragment.newInstance(xfbwebinar_id);
		fragments.add(talkFragment);
		videoDetailFragment = VideoDetailFragment.newInstance(xfbsid,xfbwebinar_id);
		fragments.add(videoDetailFragment);
		adapter = new MyFragmentpagerAdapter(getSupportFragmentManager(),tabs,fragments);

		tabLayout.setTabMode(TabLayout.MODE_FIXED);
		tabLayout.addTab(tabLayout.newTab().setText(tabs.get(0)));
		tabLayout.addTab(tabLayout.newTab().setText(tabs.get(1)));
		tabLayout.addTab(tabLayout.newTab().setText(tabs.get(2)));
		viewPager.setOffscreenPageLimit(2);
		viewPager.setAdapter(adapter);
		tabLayout.setupWithViewPager(viewPager);
		viewPager.setCurrentItem(0);
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.iv_share:
				if(from == Constants.BAPINGBANG){
					gotoLogin();
				}
				if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
					changeScreenOri();
					return;
				}
				action_share(share_title,share_content,shareImage,shareUrl);
				break;
			case R.id.iv_back:
				if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
					changeScreenOri();
					return;
				}
				finish();
				break;
			case R.id.tv_enjoy:
				gotoLogin();
				if(is_like == true){
					tv_enjoy.setImageResource(R.drawable.icon_uncollection);
					addCollection(xfbsid,xfbwebinar_id,"2");
				}else {
					tv_enjoy.setImageResource(R.drawable.icon_collection);
					addCollection(xfbsid,xfbwebinar_id,"1");//1=喜欢,2=不喜欢
				}
				break;
			case R.id.iv_fullscreen:
				changeScreenOri();
				break;
			case R.id.iv_play:
				if(param.watch_type == Param.WATCH_PLAYBACK){
					onPlayClick();
				}else {
					onWatchBtnClick();
				}
				break;
			//编辑
			case R.id.iv_edit:
				if(rl_danlayout.getVisibility() == View.VISIBLE){
					rl_danlayout.setVisibility(View.GONE);
				}else if(rl_danlayout.getVisibility() == View.GONE){
					rl_danlayout.setVisibility(View.VISIBLE);

					ll_back_bar.setVisibility(View.GONE);
					ll_actions.setVisibility(View.GONE);

					et_content.requestFocus();
					StarReleaseUtil.openImm(this);
				}
				break;
			//弹幕的显示
			case R.id.iv_danmuke:
				danmukeVisible(iv_danmuke);
				break;
			case R.id.iv_damuke_pori:
				danmukeVisible(iv_damuke_pori);
				break;
			//声音,亮度层
			case R.id.root_layout:
				//见onSingleTapUp
				break;
			case R.id.iv_money:
				//new RedPacketDialog(this,"红包打赏(元)").showDialog();
				if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
					changeScreenOri();
				}
				dialogShow();
				break;
			//发弹幕:飘弹幕,发消息,添加评论,保存弹幕
			case R.id.tv_send:
				immersiveStick();
				gotoLogin();

				String sendText = et_content.getText().toString().trim();
				if(TextUtils.isEmpty(sendText)){
					Toast.makeText(this,"评论不能为空",Toast.LENGTH_SHORT).show();
					return;
				}else if(sendText.length() > 40){
					Toast.makeText(this,"字数超出限制",Toast.LENGTH_SHORT).show();
					return;
				}
				addDanmaku(false,sendText,true);//飘弹幕
				et_content.setText("");
				rl_danlayout.setVisibility(View.GONE);
				StarReleaseUtil.hideKeyboard(et_content);
				mHandler.post(hide_bars);

				TextMessage textMessage = TextMessage.obtain(sendText);
				textMessage.setExtra("消息");
				talkFragment.sendMessage(textMessage);//发消息
				break;
			//点爱心
			case R.id.iv_heart:
				if(from == Constants.BAPINGBANG){
					gotoLogin();
					if(surplus == 0){
						showToast("本小时赞已用完,请稍后再试!");
						mHandler.post(save_temp_support);
						return;
					}
					if(is_close == true){
						showToast("不在活动时间内,不能点赞.");
						return;
					}
				}
				if(blood == 0){
					return;
				}
				blood --;
				surplus--;
				addHeart(1);
				setHeartResource();
				temp_heart_count ++;//临时
				mHandler.removeCallbacks(renew_blood);
				mHandler.postDelayed(renew_blood,4000);

				mHandler.post(send_heart);

				mHandler.removeCallbacks(save_temp_support);
				mHandler.postDelayed(save_temp_support,2500);
				break;
			default:
				break;
		}
	}

	public WatchLive getWatchLive() {
		if (watchLive == null) {
			WatchLive.Builder builder = new WatchLive
					.Builder().context(this)
					.containerLayout(rl_video_container)
					.bufferDelay(param.bufferSecond)
					.callback(new WatchCallback())
					.messageCallback(new MessageEventCallback())
					.chatCallback(null);//new ChatCallback()
			watchLive = builder.build();
		}
		return watchLive;
	}

	/**
	 * 观看直播过程中事件监听
	 */
	private class WatchCallback implements WatchLive.WatchEventCallback {
		@Override
		public void onError(int errorCode, String errorMsg) {
			switch (errorCode) {
				case WatchLive.ERROR_CONNECT:
					isWatching = false;
					showProgressbar(false);
					setPlayIcon(true);
					Toast.makeText(App.getApp(), errorMsg, Toast.LENGTH_SHORT).show();
					if(errorCode == 20208){
						initWatchLive();
					}
					break;
				default:
					Toast.makeText(App.getApp(), errorMsg, Toast.LENGTH_SHORT).show();
			}
		}
		@Override
		public void onStateChanged(int stateCode) {
			switch (stateCode) {
				case WatchLive.STATE_CONNECTED:
					isWatching = true;
					setPlayIcon(false);
					download_speed.setVisibility(View.VISIBLE);
					break;
				case WatchLive.STATE_BUFFER_START:
					if (isWatching)
						showProgressbar(true);
					download_speed.setVisibility(View.VISIBLE);
					break;
				case WatchLive.STATE_BUFFER_STOP:
					showProgressbar(false);
					download_speed.setVisibility(View.GONE);
					playerHeight = root_layout.getHeight();
					playerWidth = root_layout.getWidth();
					break;
				case WatchLive.STATE_STOP:
					isWatching = false;
					setPlayIcon(true);
					download_speed.setVisibility(View.GONE);
					break;
			}
		}

		@Override
		public void uploadSpeed(String kbps) {
			download_speed.setText(kbps+"/kb");
		}
	}

	/**
	 * 观看直播过程消息监听
	 */
	private class MessageEventCallback implements MessageServer.Callback {
		@Override
		public void onEvent(MessageServer.MsgInfo messageInfo) {
			switch (messageInfo.event) {
				case MessageServer.EVENT_PPT_CHANGED://PPT翻页消息
					break;
				case MessageServer.EVENT_DISABLE_CHAT://禁言
					break;
				case MessageServer.EVENT_KICKOUT://踢出
					break;
				case MessageServer.EVENT_OVER://直播结束
					showToast("直播结束");
					stopWatch();
					break;
				case MessageServer.EVENT_PERMIT_CHAT://解除禁言
					break;
			}
		}
		@Override
		public void onMsgServerConnected() {
		}

		@Override
		public void onConnectFailed() {
//            getWatchLive().connectMsgServer();
		}

		@Override
		public void onMsgServerClosed() {
		}
	}

	//观看回放
	public WatchPlayback getWatchPlayback() {
		if (watchPlayback == null) {
			WatchPlayback.Builder builder = new WatchPlayback
					.Builder().context(this)
					.containerLayout(rl_video_container)
					.callback(new WatchPlayback.WatchEventCallback() {
						@Override
						public void onStartFailed(String reason) {//开始播放失败
							setPlayIcon(true);
						}

						@Override
						public void onStateChanged(boolean playWhenReady, int playbackState) {//播放过程中的状态信息
							switch (playbackState) {
								case VhallHlsPlayer.STATE_IDLE:// 闲置状态
									Log.e(TAG, "STATE_IDLE");
									break;
								case VhallHlsPlayer.STATE_PREPARING:// 准备状态
									Log.e(TAG, "STATE_PREPARING");
									showProgressbar(true);
									break;
								case VhallHlsPlayer.STATE_BUFFERING:// 正在加载
									Log.e(TAG, "STATE_BUFFERING");
									showProgressbar(true);
									break;
								case VhallHlsPlayer.STATE_READY:// 准备就绪
									showProgressbar(false);
									playerDuration = getWatchPlayback().getDuration();//视频时常
									playerDurationTimeStr = VhallUtil.converLongTimeToStr(playerDuration);
									seekbar.setMax((int) playerDuration);

									playerHeight = root_layout.getHeight();
									playerWidth = root_layout.getWidth();
									Log.e(TAG, "STATE_READY");
									break;
								case VhallHlsPlayer.STATE_ENDED:// 结束
									showProgressbar(false);
									Log.e(TAG, "STATE_ENDED");
									stopPlay();
									break;
								default:
									break;
							}
						}

						@Override
						public void onError(Exception e) {//播放出错
							stopPlay();
						}

						@Override
						public void onVideoSizeChanged(int width, int height) {//视频宽高改变960/540
//                    int videoWidth = width;
//                    int videoHeight = height;
//                    int screenWidthPx = VhallUtil.getScreenWidth(WatchActivity.this);//px
//                    int videoHeightPx = screenWidthPx * videoHeight / videoWidth;
//                    int videoHeightDp = VhallUtil.pixelsToDp(videoHeightPx,WatchActivity.this);
//                    rl_video_container.setLayoutParams(new RelativeLayout.LayoutParams(screenWidthPx,videoHeightPx));
						}
					});
			watchPlayback = builder.build();
		}
		return watchPlayback;
	}

	//暂停开始图标
	public void setPlayIcon(boolean isStop) {
		if (isStop) {
			iv_play.setImageResource(R.drawable.icon_play_play);
		} else {
			iv_play.setImageResource(R.drawable.icon_play_pause);
		}
	}

	public void showProgressbar(boolean show) {
		if (show)
			pb.setVisibility(View.VISIBLE);
		else
			pb.setVisibility(View.GONE);
	}

	private  void rePlay(){
		if(param.watch_type == Param.WATCH_PLAYBACK){
			onPlayClick();
		}else {
			onWatchBtnClick();
		}
	}
	//回放按键
	public void onPlayClick() {
		if (getWatchPlayback().isPlaying()) {
			paushPlay();
			displayAdvers();//展示广告
		} else {
			if (getWatchPlayback().isAvaliable()) {
				startPlay();
				rl_video_adver.setVisibility(View.GONE);//隐藏广告
			} else {
				initWatchPlayback();
			}
		}
	}

	/**
	 * 广告展示
	 */
	public void displayAdvers(){
		if(ads_pic != null){
			Glide.with(WatchActivity.this)
					.load(ads_pic)
					.crossFade()
					.into(video_adver);
			rl_video_adver.setVisibility(View.VISIBLE);
		}
		video_adver.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(ads_url != null && ads_url.length() != 0){
					click_adver();
					Uri uri = Uri.parse(ads_url);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				}
			}
		});

		close_video_adver.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				rl_video_adver.setVisibility(View.GONE);
			}
		});
	}

	private void click_adver(){
		String token = StarReleaseUtil.getToken(this);
		String id = param.id;
		String sig = MD5Helper.GetMD5Code("type=4&" + "market_id=" + id + "&Authorization="+token+ UrlConstants.sign);
		OkHttpUtils.post()
				.url(UrlConstants.ADVER_CLICK)
				.addParams("type","4")
				.addParams("market_id",id)
				.addParams("Authorization",token)
				.addParams("sig",sig)
				.build()
				.execute(new MyStringCallback(){
					@Override
					public void onError(Call call, Exception e, int id) {
						super.onError(call, e, id);
					}

					@Override
					public void onResponse(String response, int id) {
						super.onResponse(response, id);
					}
				});
	}
	//停止回放
	public void stopPlay() {
		getWatchPlayback().stop();
		setPlayIcon(true);
	}
	//暂停回放
	public void paushPlay() {
		getWatchPlayback().pause();
		setPlayIcon(true);
	}

	//开始回放
	public void startPlay() {
		if (!getWatchPlayback().isAvaliable())
			return;
		getWatchPlayback().start();
		setPlayIcon(false);
	}
	//直播按键
	public void onWatchBtnClick() {
		if (isWatching) {
			stopWatch();
		} else {
			if (getWatchLive().isAvaliable()) {
				getWatchLive().start();
			} else {
				initWatchLive();
			}
		}
	}
	//停止直播
	public void stopWatch() {
		if (isWatching) {
			getWatchLive().stop();
			isWatching = false;
			setPlayIcon(true);
		}
	}
	//观看回放
	private void initWatchPlayback() {
		VhallSDK.getInstance().initWatch(param.id, "test", "test@vhall.com",
				"", param.record_id, param.k, getWatchPlayback(),
				new VhallSDK.RequestCallback() {
					@Override
					public void success() {
						handlePosition();
						getWatchPlayback().setScaleType(Watch.FIT_X);
						rl_video_container.setVisibility(View.VISIBLE);
						if(getNetype(WatchActivity.this) == 1 && SPCache.getInstance(WatchActivity.this).getWiFiFlag() != 2){
							onPlayClick();
						}
					}

					@Override
					public void failed(int errorCode, String reason) {
						//Toast.makeText(App.getApp(), reason, Toast.LENGTH_SHORT).show();
					}
				});
	}
	//观看直播
	private void initWatchLive(){
		VhallSDK.getInstance().initWatch(param.id, "test", "test@vhall.com",
				"", "", param.k, getWatchLive(),
				new VhallSDK.RequestCallback() {
					@Override
					public void success() {
						getWatchLive().setScaleType(Watch.FIT_X);//DPI_UHD:480 * 854  DPI_HD:480 * 852  DPI_SD:360 * 640
						rl_video_container.setVisibility(View.VISIBLE);
						if(getNetype(WatchActivity.this) == 1 && SPCache.getInstance(WatchActivity.this).getWiFiFlag() != 2){
							onWatchBtnClick();
						}
					}
					@Override
					public void failed(int errorCode, String msg) {
						String errMsg = msg;
						//Toast.makeText(App.getApp(), msg, Toast.LENGTH_SHORT).show();
					}

				});
	}

	//回放，每秒获取一下进度
	private void handlePosition() {
		if (timer != null)
			return;
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(mHandler != null){
					mHandler.sendEmptyMessage(0);
				}
			}
		}, 1000, 1000);
	}
	public void setProgressLabel(String text) {
		tv_pos.setText(text);
	}
	//横竖屏切换
	public int changeScreenOri() {
		if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			immersiveStick();
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			unImmersiveStick();
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		return getRequestedOrientation();
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
			tabLayout.setVisibility(View.VISIBLE);
			viewPager.setVisibility(View.VISIBLE);
			iv_fullscreen.setImageResource(R.drawable.icon_full);
			iv_edit.setVisibility(View.GONE);
			rl_danlayout.setVisibility(View.GONE);
			periscopeLayout.setVisibility(View.GONE);//隐藏弹出心得控件
			iv_heart.setVisibility(View.GONE);
			tv_support.setVisibility(View.GONE);
			iv_danmuke.setVisibility(View.GONE);
			iv_damuke_pori.setVisibility(View.VISIBLE);
		}else if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
			tabLayout.setVisibility(View.GONE);
			viewPager.setVisibility(View.GONE);
			iv_fullscreen.setImageResource(R.drawable.icon_unfull);
			iv_edit.setVisibility(View.VISIBLE);
			periscopeLayout.setVisibility(View.VISIBLE);
			iv_heart.setVisibility(View.VISIBLE);
			tv_support.setVisibility(View.VISIBLE);
			iv_danmuke.setVisibility(View.VISIBLE);
			iv_damuke_pori.setVisibility(View.GONE);
			StarReleaseUtil.hideKeyboard(et_content);
		}
	}

	public void onStopTracking(SeekBar seekBar) {
		playerCurrentPosition = seekBar.getProgress();
		if (!getWatchPlayback().isPlaying()) {
			startPlay();
		}
		getWatchPlayback().seekTo(playerCurrentPosition);
	}
	@Override
	public void onResume() {
		super.onResume();
//		MobclickAgent.onPageStart(getString(R.string.playVideo));
//		MobclickAgent.onResume(this);
		if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
			mDanmakuView.resume();
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_NETWORK_CHANGE);
		registerReceiver(netWorkRecever, filter);
	}
	@Override
	public void onPause() {
		super.onPause();
//		MobclickAgent.onPageEnd(getString(R.string.playVideo));
//		MobclickAgent.onPause(this);
		if (mDanmakuView != null && mDanmakuView.isPrepared()) {
			mDanmakuView.pause();
		}
		unregisterReceiver(netWorkRecever);
	}
	@Override
	protected void onStop() {
		super.onStop();
		if(param.watch_type == Param.WATCH_PLAYBACK){
			stopPlay();
		}else {
			stopWatch();
		}
		OkHttpUtils.getInstance().cancelTag(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(param.watch_type == Param.WATCH_LIVE){
			getWatchLive().destory();
		}
//		else {
//			getWatchPlayback().destory();
//		}
		shareImage = null;
		umShareListener = null;
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (mDanmakuView != null) {
			mDanmakuView.release();
			mDanmakuView = null;
		}
		mHandler.removeCallbacks(hide_bars);
		mHandler.removeCallbacks(send_heart);
		mHandler.removeCallbacks(renew_blood);
		mHandler.removeCallbacks(save_temp_support);
	}

	//没用到
	public void onSwitchPixel(int level) {
		if (getWatchLive().getDefinition() == level) {
			return;
		}
		if (isFinishing()){
			return;
		}
		if (isWatching) {
			stopWatch();
		}
		getWatchLive().setDefinition(level);
		/** 停止观看 不能立即重连 要延迟一秒重连*/
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (!isWatching && !isFinishing()) {
					getWatchLive().start();
				}
			}
		}, 500);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}

	private void immersiveStick(){
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}

	private void unImmersiveStick(){
		View decorView = getWindow().getDecorView();
		int uiOptions = decorView.getSystemUiVisibility();
		int newUiOptions = uiOptions;
		//newUiOptions &= ~View.SYSTEM_UI_FLAG_LOW_PROFILE;
		newUiOptions &= ~View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
		newUiOptions &= ~View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
		newUiOptions &= ~View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		newUiOptions &= ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		newUiOptions &= ~View.SYSTEM_UI_FLAG_FULLSCREEN;
		//newUiOptions &= ~View.SYSTEM_UI_FLAG_IMMERSIVE;
		newUiOptions &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		decorView.setSystemUiVisibility(newUiOptions);
	}

	private void getBarrage(String weihouId) {
		String token = StarReleaseUtil.getToken(this);
		OkHttpUtils.post()
				.url(UrlConstants.GETBARRAGE)
				.addParams("webinar_id", weihouId)
				.addParams("Authorization",token)
				.build()
				.execute(new MyStringCallback(){
					@Override
					public void onError(Call call, Exception e, int id) {
						super.onError(call, e, id);
					}

					@Override
					public void onResponse(String response, int id) {
						super.onResponse(response, id);
						try {
							danmuArray = new JSONObject(response).getJSONArray("result").toString();
						} catch (JSONException e) {
							e.printStackTrace();
						}
						initDamu();
//						BarrageList resp = BarrageList.fromJson(response, BarrageList.class);
//						talkFragment.setBarrage(resp.result);
					}
				});
	}

	private void initDamu(){
		// 设置最大显示行数
		HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
		maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 8); // 滚动弹幕最大显示8行
		// 设置是否禁止重叠
		HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
		overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
		overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
		mContext = DanmakuContext.create();
		mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)//描边样式
				.setDuplicateMergingEnabled(false)
				.setScrollSpeedFactor(1.2f)//是否启用合并重复弹幕
				.setScaleTextSize(1.2f)//设置弹幕滚动速度系数,只对滚动弹幕有效
				.setCacheStuffer(new SpannedCacheStuffer(), null) // 图文混排使用SpannedCacheStuffer
//        		.setCacheStuffer(new BackgroundCacheStuffer())  // 绘制背景使用BackgroundCacheStuffer
				.setMaximumLines(maxLinesPair)//设置最大显示行数
				.preventOverlapping(overlappingEnablePair);//设置防弹幕重叠，null为允许重叠
		if (mDanmakuView != null) {
			//解析弹幕
			mParser = createParser(null);//this.getResources().openRawResource(R.raw.comments)
			mDanmakuView.setCallback(new master.flame.danmaku.controller.DrawHandler.Callback() {
				@Override
				public void updateTimer(DanmakuTimer timer) {
					//timer.update();
				}

				@Override
				public void drawingFinished() {
				}

				@Override
				public void danmakuShown(BaseDanmaku danmaku) {
//                    Log.d("DFM", "danmakuShown(): text=" + danmaku.text);
				}

				@Override
				public void prepared() {
					mDanmakuView.start();
				}
			});

			mDanmakuView.setOnDanmakuClickListener(new IDanmakuView.OnDanmakuClickListener() {
				@Override
				public void onDanmakuClick(BaseDanmaku latest) {
					Log.d("DFM", "onDanmakuClick text:" + latest.text);
				}

				@Override
				public void onDanmakuClick(IDanmakus danmakus) {
					Log.d("DFM", "onDanmakuClick danmakus size:" + danmakus.size());
				}
			});
			mDanmakuView.prepare(mParser, mContext);
			//mDanmakuView.showFPS(true);
			mDanmakuView.enableDanmakuDrawingCache(true);
		}
	}

	/**
	 * 创建解析器对象，解析输入流
	 * @param stream
	 * @return
	 */
	private BaseDanmakuParser createParser(InputStream stream) {
//		if (stream == null) {
//			return new BaseDanmakuParser() {
//				@Override
//				protected Danmakus parse() {
//					return new Danmakus();
//				}
//			};
//		}
		StarDanmakuParser parser = new StarDanmakuParser(this);
		try {
			JSONSource dataSource = new JSONSource(danmuArray);
			parser.load(dataSource);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return parser;
	}

	/**
	 * 发图文弹幕
	 * @param islive
	 * @param danmuText
	 * @param isBenren
	 */
	public void addDanmaKuTextAndImage(boolean islive,String danmuText,boolean isBenren) {
		BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);

		SpannableString spannableString = StringUtil.stringToSpannableString(danmuText,this);//将特定字符转换为表情

		danmaku.text = spannableString;
		danmaku.padding = 5;
		danmaku.priority = 1;// 一定会显示, 一般用于本机发送的弹幕
		danmaku.isLive = islive;
		danmaku.time = mDanmakuView.getCurrentTime() + 400;
		danmaku.textSize = 18f * (mParser.getDisplayer().getDensity() - 0.6f);
		danmaku.textColor = Color.WHITE;
		danmaku.textShadowColor = 0; // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
		if(isBenren){
			danmaku.underlineColor = Color.GREEN;
		}
		mDanmakuView.addDanmaku(danmaku);
	}
	/**
	 * 发纯文字的弹幕
	 * @param islive
	 */
	public void addDanmaku(boolean islive,String danmuText,boolean isBenren) {
		BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
		if (danmaku == null || mDanmakuView == null) {
			return;
		}
		//danmaku.text = "这是一条弹幕" + System.nanoTime();
		danmaku.text = danmuText;//替换表情;
		danmaku.padding = 3;
		danmaku.priority = 0;  // 可能会被各种过滤器过滤并隐藏显示
		danmaku.isLive = islive;
		danmaku.time = mDanmakuView.getCurrentTime() + 500;//延时
		danmaku.textSize = 18f * (mParser.getDisplayer().getDensity() - 0.6f);
		danmaku.textColor = Color.WHITE;
		//danmaku.textShadowColor = Color.RED;
		if(isBenren){
			danmaku.underlineColor = Color.GREEN;
		}
		//danmaku.borderColor = Color.GREEN;
		mDanmakuView.addDanmaku(danmaku);
	}
	/**
	 * 获取回放当前时间,毫秒,直播进行的时间,毫秒
	 */
	public void setCurrentValue(){
		Bundle bundle = new Bundle();
		if(param.watch_type == Param.WATCH_LIVE && start_time != null){
			Long direct_time = System.currentTimeMillis() - (Long.parseLong(start_time)) * 1000;//直播开始的毫秒
			bundle.putCharSequence("direct",direct_time+"");
		}else{
			if(playerCurrentPosition == 0){
				playerCurrentPosition = playerDuration;
			}
			bundle.putCharSequence("videoCurrentTime",playerCurrentPosition+"");//回放的毫秒数
		}
		talkFragment.setData(bundle);
	}

	/**
	 * 获取视频直播开始的时间
	 */
	public void getVideoDetail(String sid,String webinar_id) {
		String token = StarReleaseUtil.getToken(this);
		String sig = MD5Helper.GetMD5Code("sid=" + sid + "&"+"webinar_id="+ webinar_id +"&"+"Authorization="+token+ UrlConstants.sign);
		OkHttpUtils.post()
				.url(UrlConstants.VIDEO_DETAIL)
				.addParams("sid",sid)
				.addParams("webinar_id",webinar_id)
				.addParams("Authorization",token)
				.addParams("sig",sig)
				.build()
				.execute(new MyStringCallback(){
					@Override
					public void onError(Call call, Exception e, int id) {
						super.onError(call, e, id);
					}

					@Override
					public void onResponse(String response, int id) {
						super.onResponse(response, id);
						VideoDetail res = VideoDetail.fromJson(response,VideoDetail.class);
						//详情传给详情fragment
//						VideoDetailFragment detailFragment = (VideoDetailFragment)fragments.get(2);
//						detailFragment.setVideoDetail(res);

						ads_pic = res.result.ads_pic;
						ads_url = res.result.ads_url;

						is_like = res.result.is_like;
						if(true == is_like){
							tv_enjoy.setImageResource(R.drawable.icon_collection);
						}else {
							tv_enjoy.setImageResource(R.drawable.icon_uncollection);
						}
						start_time = res.result.start_time;
						tv_big_title.setText(res.result.subject);//title
						share_title = res.result.subject;
//						try {
//							share_title = URLEncoder.encode(share_title,"UTF-8");
//						} catch (UnsupportedEncodingException e) {
//							e.printStackTrace();
//						}
						share_content = res.result.introduction;//分享用

						shareUrl = UrlConstants.SHARE_URL + "?id=" + xfbwebinar_id +"&title=" + "&tag=e";//share_title
					}
				});
	}

	private void getSupport(String inar_id){
		String token = StarReleaseUtil.getToken(this);
		OkHttpUtils.post()
				.url(UrlConstants.GET_SUPPORT)
				.addParams("webinar_id", inar_id)
				.addParams("Authorization",token)
				.build()
				.execute(new MyStringCallback(){
					@Override
					public void onError(Call call, Exception e, int id) {
						super.onError(call, e, id);
					}

					@Override
					public void onResponse(String response, int id) {
						super.onResponse(response, id);
						try {
							String get_heart_count = new JSONObject(response).getJSONObject("result").getString("support");
							heart_count = Integer.parseInt(get_heart_count);
							tv_support.setText(heart_count+"");
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void daShangBtn() {
		OkHttpUtils.post()
				.url(UrlConstants.CONTROL_REDPACKET)
				.addParams("platform", "2")
				.build()
				.execute(new MyStringCallback(){
					@Override
					public void onError(Call call, Exception e, int id) {
						super.onError(call, e, id);
					}
					@Override
					public void onResponse(String response, int id) {
						super.onResponse(response, id);
						try {
							String redPacket_btn_flage = new JSONObject(response).getJSONObject("result").getString("reward_2_5");
							if("0".equals(redPacket_btn_flage)){
								iv_money.setVisibility(View.GONE);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	public void addHeart(int h) {
		heart_count += h;
		tv_support.setText(heart_count+"");
		if(periscopeLayout.getVisibility() == View.GONE){
			return;
		}
		for(int i = 0;i<h;i++){
			periscopeLayout.addHeart();
		}
	}

	public void setNickName(String name){
		this.nickName = name;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
	}

	//没登录去登录
	private void gotoLogin(){
		if(TextUtils.isEmpty(SPCache.getInstance(this).getToken())){
			startActivity(new Intent(this, LoginActivity.class));
			finish();
			return;
		}
	}

	private void action_share(final String title, final String content, final UMImage imagee, final String url) {
		final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
				{SHARE_MEDIA.SINA,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.QZONE,SHARE_MEDIA.QQ};
		new ShareAction(this)
				.setDisplayList(displaylist)
				.withTitle(title)
				.withText(content)
				.withMedia(imagee)
				.withTargetUrl(url)
				.setCallback(umShareListener)
				.open();
//				.setShareboardclickCallback(new ShareBoardlistener() {
//					@Override
//					public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
//						new ShareAction(WatchActivity.this)
//								.setPlatform(share_media)
//								.setCallback(umShareListener)
//								.withTitle(title)
//								.withText(content)
//								.withMedia(imagee)
//								.withTargetUrl(url)
//								//.withExtra(new UMImage(WatchHLSActivity.this,R.mipmap.ic_launcher))
//								.share();
//					}
//				}).open();
	}

	//每日分享
	private void dailyShare(String webinar_id, final int from){
		String url = from == Constants.BAPINGBANG ? UrlConstants.DAILYSHARE : UrlConstants.SIMPLE_SHARE;
		String token = StarReleaseUtil.getToken(this);
		String sig = MD5Helper.GetMD5Code("webinar_id=" + webinar_id + "&Authorization=" + token + UrlConstants.sign);
		OkHttpUtils.post()
				.url(url)
				.addParams("webinar_id",webinar_id)
				.addParams("Authorization",token)
				.addParams("sig",sig)
				.build()
				.execute(new MyStringCallback(){
					@Override
					public void onError(Call call, Exception e, int id) {
						super.onError(call, e, id);
					}
					@Override
					public void onResponse(String response, int id) {
						super.onResponse(response, id);
						if(from == Constants.BAPINGBANG){
							AllResponse.DailyShare resp = DailyShare.fromJson(response,DailyShare.class);
							if(resp.result > 0 && from == Constants.BAPINGBANG){
								showToast("首次分享成功!加500赞!");
							}else{
								showToast("分享成功!");
							}
						}else {
							AllResponse.SimpleShare resp = AllResponse.SimpleShare.fromJson(response,AllResponse.SimpleShare.class);
							if(resp.retCode == 0){
								showToast("分享成功!");
							}
						}
					}
				});
	}

	private void addCollection(String sid, String webinar_id, final String like){
		String token = SPCache.getInstance(this).getToken();
		String sig = MD5Helper.GetMD5Code("sid=" + sid + "&webinar_id=" + webinar_id + "&type=" + like + "&Authorization=" + token + UrlConstants.sign);
		OkHttpUtils.post()
				.url(UrlConstants.ADD_COLLECTION)
				.addParams("sid",sid)
				.addParams("webinar_id",webinar_id)
				.addParams("type",like)
				.addParams("Authorization",token)
				.addParams("sig",sig)
				.build()
				.execute(new MyStringCallback(){
					@Override
					public void onError(Call call, Exception e, int id) {
						super.onError(call, e, id);
					}
					@Override
					public void onResponse(String response, int id) {
						super.onResponse(response, id);
						BaseResponse resp = BaseResponse.fromJson(response,BaseResponse.class);
						if(resp.retCode == 0){
							if("1".equals(like)){
								is_like = true;
								showToast(getString(R.string.collect_success));
							}else{
								is_like = false;
							}
						}
					}
				});
	}
//****************************手势********************
	@Override
	public boolean onDown(MotionEvent e) {
		firstScroll = true;// 设定是触摸屏幕后第一次scroll的标志
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		//隐藏输入框和键盘
		if (rl_danlayout.getVisibility() == View.VISIBLE) {
			rl_danlayout.setVisibility(View.GONE);
			StarReleaseUtil.hideKeyboard(et_content);
		}
		if(ll_back_bar.getVisibility() == View.VISIBLE){
			mHandler.removeCallbacks(hide_bars);
			ll_back_bar.setVisibility(View.GONE);
			ll_actions.setVisibility(View.GONE);
		}else{
			ll_back_bar.setVisibility(View.VISIBLE);
			ll_actions.setVisibility(View.VISIBLE);
			mHandler.postDelayed(hide_bars,3000);
		}
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		float mOldX = e1.getX(), mOldY = e1.getY();
		int y = (int) e2.getRawY();

		//onScroll是多次执行的
		if (firstScroll) {// 以触摸屏幕后第一次滑动为标准，避免在屏幕上操作切换混乱
			// 横向的距离变化大则调整进度，纵向的变化大则调整音量
			if (Math.abs(distanceX) >= Math.abs(distanceY)) {
				//直播隐藏progress
				if(param.watch_type == Param.WATCH_PLAYBACK){
					gesture_progress_layout.setVisibility(View.VISIBLE);
				}
				gesture_volume_layout.setVisibility(View.GONE);
				gesture_bright_layout.setVisibility(View.GONE);
				GESTURE_FLAG = GESTURE_MODIFY_PROGRESS;
			} else {
				if (mOldX > playerWidth * 3.0 / 5) {// 音量 手指在屏幕的哪边
					gesture_volume_layout.setVisibility(View.VISIBLE);
					gesture_bright_layout.setVisibility(View.GONE);
					gesture_progress_layout.setVisibility(View.GONE);
					GESTURE_FLAG = GESTURE_MODIFY_VOLUME;
				} else if (mOldX < playerWidth * 2.0 / 5) {// 亮度
					gesture_bright_layout.setVisibility(View.VISIBLE);
					gesture_volume_layout.setVisibility(View.GONE);
					gesture_progress_layout.setVisibility(View.GONE);
					GESTURE_FLAG = GESTURE_MODIFY_BRIGHT;
				}
			}
		}
		// 如果每次触摸屏幕后第一次scroll是调节进度，那之后的scroll事件都处理音量进度，直到离开屏幕执行下一次操作
		if (GESTURE_FLAG == GESTURE_MODIFY_PROGRESS) {
			// distanceX=lastScrollPositionX-currentScrollPositionX，因此为正时是快进
			int playingTime = (int)playerCurrentPosition / 1000;
			int videoTotalTime = (int) (playerDuration / 1000);
			if (Math.abs(distanceX) > Math.abs(distanceY)) {// 横向移动大于纵向移动
				if (distanceX >= ScreenSizeUtil.Dp2Px(this, STEP_PROGRESS)) {// 快退，用步长控制改变速度，可微调
					gesture_iv_progress.setImageResource(R.drawable.souhu_player_backward);
					if (playingTime > 10) {// 避免为负
						playingTime -= 10;// scroll方法执行一次快退10秒
					} else {
						playingTime = 0;
					}
				} else if (distanceX <= -ScreenSizeUtil.Dp2Px(this, STEP_PROGRESS)) {// 快进
					gesture_iv_progress.setImageResource(R.drawable.souhu_player_forward);
					if (playingTime < videoTotalTime - 20) {// 避免超过总时长
						playingTime += 10;// scroll执行一次快进3秒
					} else {
						playingTime = videoTotalTime - 10;
					}
				}
				if (playingTime < 0) {
					playingTime = 0;
				}
				if(param.watch_type == Param.WATCH_PLAYBACK){
					getWatchPlayback().seekTo(playingTime * 1000);
					geture_tv_progress_time.setText(VhallUtil.converLongTimeToStr(playingTime * 1000) + "/" + VhallUtil.converLongTimeToStr(videoTotalTime * 1000));
				}
			}
		}
		// 如果每次触摸屏幕后第一次scroll是调节音量，那之后的scroll事件都处理音量调节，直到离开屏幕执行下一次操作
		else if (GESTURE_FLAG == GESTURE_MODIFY_VOLUME) {
			currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC); //获取当前值
			if (Math.abs(distanceY) > Math.abs(distanceX)) {// 纵向移动大于横向移动
				if (distanceY >= ScreenSizeUtil.Dp2Px(this, STEP_VOLUME)) {// 音量调大,注意横屏时的坐标体系,尽管左上角是原点，但横向向上滑动时distanceY为正
					if (currentVolume < maxVolume) {// 为避免调节过快，distanceY应大于一个设定值
						currentVolume++;
					}
					gesture_iv_player_volume.setImageResource(R.drawable.souhu_player_volume);
				} else if (distanceY <= -ScreenSizeUtil.Dp2Px(this, STEP_VOLUME)) {// 音量调小
					if (currentVolume > 0) {
						currentVolume--;
						if (currentVolume == 0) {// 静音，设定静音独有的图片
							gesture_iv_player_volume.setImageResource(R.drawable.souhu_player_silence);
						}
					}
				}
				int percentage = (currentVolume * 100) / maxVolume;
				geture_tv_volume_percentage.setText(percentage + "%");
				audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume, 0);
			}
		}
		// 如果每次触摸屏幕后第一次scroll是调节亮度，那之后的scroll事件都处理亮度调节，直到离开屏幕执行下一次操作
		else if (GESTURE_FLAG == GESTURE_MODIFY_BRIGHT) {
			gesture_iv_player_bright.setImageResource(R.drawable.souhu_player_bright);
			if (mBrightness < 0) {
				mBrightness = getWindow().getAttributes().screenBrightness;
				if (mBrightness <= 0.00f)
					mBrightness = 0.50f;
				if (mBrightness < 0.01f)
					mBrightness = 0.01f;
			}
			WindowManager.LayoutParams lpa = getWindow().getAttributes();
			lpa.screenBrightness = mBrightness + (mOldY - y) / playerHeight;
			if (lpa.screenBrightness > 1.0f)
				lpa.screenBrightness = 1.0f;
			else if (lpa.screenBrightness < 0.01f)
				lpa.screenBrightness = 0.01f;
			getWindow().setAttributes(lpa);
			geture_tv_bright_percentage.setText((int) (lpa.screenBrightness * 100) + "%");
		}
		firstScroll = false;// 第一次scroll执行完成，修改标志
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// 手势里除了singleTapUp，没有其他检测up的方法
		if (event.getAction() == MotionEvent.ACTION_UP) {
			GESTURE_FLAG = 0;// 手指离开屏幕后，重置调节音量或进度的标志
			gesture_volume_layout.setVisibility(View.GONE);
			gesture_bright_layout.setVisibility(View.GONE);
			gesture_progress_layout.setVisibility(View.GONE);
		}
		return gestureDetector.onTouchEvent(event);
	}

	private Runnable hide_bars = new Runnable() {
		@Override
		public void run() {
			ll_back_bar.setVisibility(View.GONE);
			ll_actions.setVisibility(View.GONE);
		}
	};

	private Runnable send_heart = new Runnable() {
		@Override
		public void run() {
			TextMessage textMessage = TextMessage.obtain(nickName);
			textMessage.setExtra("心|"+1);
			talkFragment.sendMessage(textMessage);//发消息
		}
	};

	//不点后,4s自动涨血,1s涨一格,涨到10的时候停止
	private Runnable renew_blood = new Runnable() {
		@Override
		public void run() {
			if(blood >= 10){
				mHandler.removeCallbacks(renew_blood);
				return;
			}
			blood++;
			setHeartResource();
			mHandler.postDelayed(this, 1000);
		}
	};

	private void setHeartResource() {
		switch(blood){
			case 10:
				iv_heart.setImageResource(R.drawable.red_hearth);
				break;
			case 9:
				iv_heart.setImageResource(R.drawable.heart9);
				break;
			case 8:
				iv_heart.setImageResource(R.drawable.heart8);
				break;
			case 7:
				iv_heart.setImageResource(R.drawable.heart7);
				break;
			case 6:
				iv_heart.setImageResource(R.drawable.heart6);
				break;
			case 5:
				iv_heart.setImageResource(R.drawable.heart5);
				break;
			case 4:
				iv_heart.setImageResource(R.drawable.heart4);
				break;
			case 3:
				iv_heart.setImageResource(R.drawable.heart3);
				break;
			case 2:
				iv_heart.setImageResource(R.drawable.heart2);
				break;
			case 1:
				iv_heart.setImageResource(R.drawable.heart1);
				break;
			case 0:
				iv_heart.setImageResource(R.drawable.heart0);
				break;
		}
	}

	private Runnable save_temp_support = new Runnable() {
		@Override
		public void run() {
			if(from == Constants.BAPINGBANG){
				saveActivitySupport(xfbwebinar_id,temp_heart_count+"");
			}else{
				saveTempSupport(temp_heart_count);
			}
			temp_heart_count = 0;
		}
	};

	/**
	 * 存一次点心数量
	 * @param heart
	 */
	private void saveTempSupport(int heart){
		String token = StarReleaseUtil.getToken(this);
		OkHttpUtils.post()
				.url(UrlConstants.SAVE_CLICKS)
				.addParams("webinar_id", xfbwebinar_id)
				.addParams("clicks",heart+"")
				.addParams("Authorization",token)
				.build()
				.execute(new MyStringCallback(){
					@Override
					public void onError(Call call, Exception e, int id) {
						super.onError(call, e, id);
					}

					@Override
					public void onResponse(String response, int id) {
						super.onResponse(response, id);
					}
				});
	}

	private void saveActivitySupport(String xfbwebinar_id,String support){
		String token = StarReleaseUtil.getToken(this);
		String sig = MD5Helper.GetMD5Code("webinar_id=" + xfbwebinar_id + "&support=" + support +"&Authorization=" + token + UrlConstants.sign);
		OkHttpUtils.post()
				.url(UrlConstants.ACSUPPORT)
				.addParams("webinar_id", xfbwebinar_id)
				.addParams("support",support)
				.addParams("Authorization",token)
				.addParams("sig",sig)
				.build()
				.execute(new MyStringCallback(){
					@Override
					public void onError(Call call, Exception e, int id) {
						super.onError(call, e, id);
					}

					@Override
					public void onResponse(String response, int id) {
						super.onResponse(response, id);
						AcSupport resp = AcSupport.fromJson(response,AcSupport.class);
						if(resp.retCode == 0){
							surplus = resp.result.surplus;
						}else {
							surplus = 0;
						}
					}
				});
	}

	/**
	 * 弹幕隐藏显示
	 * @param imageView 图标
	 */
	public void danmukeVisible(ImageView imageView){
		if(imageView == null){
			return;
		}
		if(isDanmuOn == true){
			isDanmuOn = false;
			mDanmakuView.hide();
			imageView.setImageResource(R.drawable.icon_intan);
		}else if(isDanmuOn == false){
			isDanmuOn = true;
			mDanmakuView.show();
			imageView.setImageResource(R.drawable.icon_tan);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
				changeScreenOri();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private void dialogShow(){
		final UpDateDialog dialog = new UpDateDialog(this, R.layout.red_package, R.style.Theme_dialog);
		dialog.setCanceledOnTouchOutside(true);
		final TextView money_1 = (TextView) dialog.findViewById(R.id.money_1);
		final TextView money_2 = (TextView) dialog.findViewById(R.id.money_2);
		final TextView money_3 = (TextView) dialog.findViewById(R.id.money_3);
		money_1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(money_1.isSelected()){
					money_1.setSelected(false);
					red_money = "";
					return;
				}
				money_1.setSelected(true);
				money_2.setSelected(false);
				money_3.setSelected(false);
				red_money = "1.88";
			}
		});
		money_2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(money_2.isSelected()){
					money_2.setSelected(false);
					red_money = "";
					return;
				}
				money_1.setSelected(false);
				money_2.setSelected(true);
				money_3.setSelected(false);
				red_money = "8.88";
			}
		});
		money_3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(money_3.isSelected()){
					money_3.setSelected(false);
					red_money = "";
					return;
				}
				money_1.setSelected(false);
				money_2.setSelected(false);
				money_3.setSelected(true);
				red_money = "88.88";
			}
		});

		final EditText et_price = (EditText) dialog.findViewById(R.id.et_price);
		final EditText et_sentence = (EditText) dialog.findViewById(R.id.et_sentence);

		et_price.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.toString().contains(".")) {
					if (s.length() - 1 - s.toString().indexOf(".") > 2) {
						s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
						et_price.setText(s);
						et_price.setSelection(s.length());
					}
				}
				if (s.toString().trim().substring(0).equals(".")) {
					s = "0" + s;
					et_price.setText(s);
					et_price.setSelection(2);
				}
				if (s.toString().startsWith("0")
						&& s.toString().trim().length() > 1) {
					if (!s.toString().substring(1, 2).equals(".")) {
						et_price.setText(s.subSequence(0, 1));
						et_price.setSelection(1);
						return;
					}
				}
			}
			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		dialog.findViewById(R.id.ll_wechat).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String talk = et_sentence.getText().toString();//有可能为空
						String selt_price = et_price.getText().toString();
						if(!TextUtils.isEmpty(selt_price)){
							red_money = selt_price;
						}
						if(TextUtils.isEmpty(red_money) || red_money.endsWith(".")){
							showToast("请重新填写打赏金额");
							return;
						}
						if(!TextUtils.isEmpty(talk) && talk.length() > 40){
							showToast("字数超出限制");
							return;
						}
						//TODO xfbsid重新获得。。。
						getSegment("1",talk,red_money,xfbsid,share_title);
						red_money = null;
						//TODO 记录进度
						dialog.dismiss();
					}
				});

		dialog.findViewById(R.id.ll_zhifubao).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String talk = et_sentence.getText().toString();
						String selt_price = et_price.getText().toString();
						if(!TextUtils.isEmpty(selt_price)){
							red_money = selt_price;
						}
						if(TextUtils.isEmpty(red_money) || red_money.endsWith(".")){
							showToast("请重新填写打赏金额");
							return;
						}
						if(!TextUtils.isEmpty(talk) && talk.length() > 40){
							showToast("字数超出限制");
							return;
						}
						getSegment("2",talk,red_money,xfbsid,share_title);
						red_money = null;
						dialog.dismiss();
					}
				});
		dialog.show();
	}

	private void getSegment(final String payType, String note, final String price, String sid, String subject){
		String token = StarReleaseUtil.getToken(this);
		OkHttpUtils.post()
				.url(UrlConstants.CREATE_ORDER)
				.addParams("payType",payType)//1 微信  2 支付宝
				.addParams("note",note)//说的话
				.addParams("price",price)
				.addParams("sid",sid)
				.addParams("subject",subject)
				.addParams("Authorization",token)
				.build()
				.execute(new MyStringCallback(){
					@Override
					public void onError(Call call, Exception e, int id) {
						super.onError(call, e, id);
					}

					@Override
					public void onResponse(String response, int id) {
						super.onResponse(response, id);
						PayBean res = PayBean.fromJson(response,PayBean.class);
						if(res.retCode != 0){
							return;
						}
						if("1".equals(payType)){
							//res.result.orderNo
							PayReq req = new PayReq();
							req.appId = UrlConstants.APP_ID;
							req.partnerId = UrlConstants.partnerId;
							req.prepayId =res.result.prepayId;
							req.nonceStr =res.result.noncestr;
							req.timeStamp =res.result.timestamp;
							req.packageValue =res.result.packageValue;
							req.sign =res.result.sign;
							//req.extData ="";
							api.sendReq(req);
						}else if("2".equals(payType)){
							String orderInfo = StarReleaseUtil.getOrderInfo("红包打赏", "红包打赏", price, res.result.orderNo);
							String sign = StarReleaseUtil.sign(orderInfo);
							try {
								sign = URLEncoder.encode(sign, "UTF-8");
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + StarReleaseUtil.getSignType();
							Runnable payRunnable = new Runnable() {
								@Override
								public void run() {
									PayTask alipay = new PayTask(WatchActivity.this);
									// 调用支付接口，获取支付结果
									String result = alipay.pay(payInfo, true);

									Message msg = Message.obtain();
									msg.what = SDK_PAY_FLAG;
									msg.obj = result;
									mHandler.sendMessage(msg);
								}
							};
							// 必须异步调用
							Thread payThread = new Thread(payRunnable);
							payThread.start();
						}
					}
				});
	}

	/**
	 * 打赏成功回调
	 */
	public void redPacketResult(){
		showToast("打赏成功");
		addDanmaKuTextAndImage(false,nickName+"打赏了一个[红包]",true);//发弹幕
		TextMessage textMessage = TextMessage.obtain(nickName);
		textMessage.setExtra("打赏|"+nickName+"打赏了一个[红包]");
		talkFragment.sendMessage(textMessage);//发消息
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(intent.getStringExtra("sid") != null){
			talkFragment.quitChatRoom(xfbwebinar_id);

			xfbsid = intent.getStringExtra("sid");
			param = (Param) intent.getSerializableExtra("param");
			xfbwebinar_id = param.id;
			String share_pic = intent.getStringExtra("share_pic");
			shareImage = new UMImage(WatchActivity.this,share_pic);

			is_close = intent.getBooleanExtra("is_close",true);
			surplus = intent.getIntExtra("surplus",0);
			from = intent.getIntExtra("from", Constants.TUIJIAN);

			talkFragment.getBarrage(xfbwebinar_id);
			talkFragment.joinChatRoom(xfbwebinar_id);
			relatedFragment.getData(xfbwebinar_id);
			videoDetailFragment.getVideoContent(xfbsid,xfbwebinar_id);

			showProgressbar(true);
			if(param.watch_type == Param.WATCH_PLAYBACK){
				initWatchPlayback();
			}else {
				seekbar.setVisibility(View.INVISIBLE);
				tv_pos.setVisibility(View.INVISIBLE);
				initWatchLive();
			}
			mHandler.post(hide_bars);
			screenWidthPx = VhallUtil.getScreenWidth(WatchActivity.this);
			screenHeightPx = VhallUtil.getScreenHeight(WatchActivity.this);

			getBarrage(xfbwebinar_id);//获取弹幕
			getVideoDetail(xfbsid,xfbwebinar_id);
			getSupport(xfbwebinar_id);//获取点赞数量
			daShangBtn();//打赏按钮
		}else{
			//微信打赏回调
			String status = intent.getStringExtra("paystatus");
			rePlay();
			switch(status){
				case "100":
					redPacketResult();
					break;
				default:
					break;
			}
		}
	}
}