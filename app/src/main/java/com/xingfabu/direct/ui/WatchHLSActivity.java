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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.alipay.sdk.app.PayTask;
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
import com.vhall.playersdk.player.impl.HlsRendererBuilder;
import com.vhall.playersdk.player.impl.VhallHlsPlayer;
import com.vhall.playersdk.player.util.Util;
import com.vinny.vinnylive.LiveParam;
import com.xingfabu.direct.R;
import com.xingfabu.direct.adapter.MyFragmentpagerAdapter;
import com.xingfabu.direct.app.Constants;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.cache.SPCache;
import com.xingfabu.direct.emoji.StringUtil;
import com.xingfabu.direct.entity.BarrageList;
import com.xingfabu.direct.entity.BaseResponse;
import com.xingfabu.direct.entity.PayBean;
import com.xingfabu.direct.entity.VideoDetail;
import com.xingfabu.direct.fragment.TalkFragment;
import com.xingfabu.direct.fragment.VideoDetailFragment;
import com.xingfabu.direct.heart.PeriscopeLayout;
import com.xingfabu.direct.utils.LogUtil;
import com.xingfabu.direct.utils.MD5Helper;
import com.xingfabu.direct.utils.MyStringCallback;
import com.xingfabu.direct.utils.PayResult;
import com.xingfabu.direct.utils.ScreenSizeUtil;
import com.xingfabu.direct.utils.StarDanmakuParser;
import com.xingfabu.direct.utils.StarReleaseUtil;
import com.xingfabu.direct.widget.Loading;
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
import java.util.Random;
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
import com.xingfabu.direct.entity.AllResponse.AcSupport;
import com.xingfabu.direct.entity.AllResponse.DailyShare;
/**
 * 观看回放和直播
 */
public class WatchHLSActivity extends BaseActivity implements SurfaceHolder.Callback,GestureDetector.OnGestureListener,View.OnTouchListener{
	private static final String TAG = "XFBMediaPlayer";
	private VhallHlsPlayer mMediaPlayer;//播放器
	private VhallPlayerListener mVhallPlayerListener;//监听回调
	private long playerCurrentPosition = 0L; // 度播放的当前标志，毫秒
	private long errorCurrentPosition = 0L;//记录跳转时候的进度
	private long playerDuration;// 播放资源的时长，毫秒
	private String playerDurationTimeStr = "00:00";//视频格式化的时间

	private DanmakuView mDanmakuView;//弹幕层
	private DanmakuContext mContext;
	private BaseDanmakuParser mParser;

	private String type = "";//direct直播,video回放
	private String video_url = "";//视频地址
	private LiveParam param;
	private String xfbsid = null;
	private String xfbwebinar_id = null;
	private String start_time = null;//直播开始的时间戳

	private LinearLayout ll_root;//根布局
	private SurfaceView surface;
	private SurfaceHolder holder;
	private LinearLayout ll_actions;
	private LinearLayout ll_back_bar;//返回 title 分享条
	private RelativeLayout rl_danlayout;//发送弹幕布局
	private ImageView iv_play,iv_heart,tv_enjoy;
	private SeekBar seekbar;
	private TextView tv_pos,tv_big_title;
	private ProgressBar pb;
	private ImageView iv_fullscreen,iv_edit,iv_danmuke,iv_money,iv_damuke_pori;

	private Timer timer;
	private TabLayout tabLayout;
	private ViewPager viewPager;
	private List<String> tabs;
	private List<Fragment> fragments;
	private MyFragmentpagerAdapter adapter;
	private boolean isDanmuOn = true;
	private EditText et_content;
	private TextView tv_support;
	private String danmuArray = null,nickName;
	private PeriscopeLayout periscopeLayout;
	private Random mRandom = new Random();
	private int heart_count = 0;//接口获得的爱心的数量
	private int temp_heart_count = 0;//临时储存的
	private int blood = 10;//能量值,10个阶段

	private int videoWidth = 0;
	private int videoHeight = 0;
	private String share_title = null;
	private String share_content = null;
	private UMImage image = null;
	private String url = null;

	private RelativeLayout root_layout;// 根布局
	private RelativeLayout mRelativeVideoSize;
	private LinearLayout gesture_volume_layout;//音量控制布局
	private LinearLayout gesture_bright_layout;//亮度控制布局
	private LinearLayout gesture_progress_layout;//快进布局
	private TextView geture_tv_volume_percentage, geture_tv_bright_percentage;// 音量百分比,亮度百分比
	private ImageView gesture_iv_player_volume, gesture_iv_player_bright;// 音量图标,亮度图标
	private ImageView gesture_iv_progress;// 快进或快退标志
	private TextView geture_tv_progress_time;// 播放时间进度
	private GestureDetector gestureDetector;
	private AudioManager audiomanager;
	private int maxVolume, currentVolume;
	/** 视频窗口的宽和高 */
	private int playerWidth, playerHeight;
	private int GESTURE_FLAG = 0;// 1,调节进度，2，调节音量,3.调节亮度
	private boolean firstScroll = false;// 每次触摸屏幕后，第一次scroll的标志
	private static final int GESTURE_MODIFY_PROGRESS = 1;
	private static final int GESTURE_MODIFY_VOLUME = 2;
	private static final int GESTURE_MODIFY_BRIGHT = 3;
	private static final float STEP_PROGRESS = 2f;// 设定进度滑动时的步长，避免每次滑动都改变，导致改变过快
	private static final float STEP_VOLUME = 2f;// 协调音量滑动时的步长，避免每次滑动都改变，导致改变过快
	private float mBrightness = -1f; // 亮度

	private boolean is_close;//活动开始
	private int surplus;//剩余次数
	private int from;//来自

	private IWXAPI api;
	String red_money = "";
	private  boolean is_like;

	private static final int SDK_PAY_FLAG = 1;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case SDK_PAY_FLAG: {
					/**
					 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
					 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
					 * docType=1) 建议商户依赖异步通知
					 */
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
				}
			}
		}
	};
	//分享
	private UMShareListener umShareListener = new UMShareListener() {
		@Override
		public void onResult(SHARE_MEDIA platform) {
			com.umeng.socialize.utils.Log.d("plat","platform"+platform);
			if(platform.name().equals("WEIXIN_FAVORITE")){
				showToast(platform + "收藏成功");
			}else{
				//showToast(platform + "分享成功");
				dailyShare(xfbwebinar_id);
			}
		}
		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			showToast(platform + "分享失败");
		}
		@Override
		public void onCancel(SHARE_MEDIA platform) {
			showToast(platform + "分享取消");
		}
	};
	//注意新浪分享的title是不显示的，URL链接只能加在分享文字后显示，并且需要确保withText()不为空
	//当同时传递URL参数和图片时，注意确保图片不能超过32K，否则无法分享，不传递URL参数时图片不受32K限制
	private void action_share(final String title, final String content, final UMImage imagee,final String url) {
		final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
				{SHARE_MEDIA.SINA,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.QZONE,SHARE_MEDIA.QQ};
		new ShareAction(this)
				.setDisplayList(displaylist)
//				.withTitle(title)
//				.withMedia(imagee)
				.setShareboardclickCallback(new ShareBoardlistener() {
					@Override
					public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
						new ShareAction(WatchHLSActivity.this)
								.setPlatform(share_media)
								.setCallback(umShareListener)
								.withText(content)
								.withTitle(title)
								.withMedia(imagee)
								.withTargetUrl(url)
								//.withExtra(new UMImage(WatchHLSActivity.this,R.mipmap.ic_launcher))
								.share();
					}
				}).open();
	}

	BroadcastReceiver netWorkRecever = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 处理各种情况
			String action = intent.getAction();
			if (ACTION_NETWORK_CHANGE.equals(action)) { // 网络发生变化
				int i = getNetype(context);
				if(-1 == i){
					showToast("请检查网络");
				}else if(1 == i){
					//showToast("WiFi状态");
				}else{//数据流量
					if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
						mMediaPlayer.setPlayWhenReady(false);
						iv_play.setImageResource(R.drawable.icon_play_play);
					}
					AlertDialog.Builder dialog = new AlertDialog.Builder(WatchHLSActivity.this);
					dialog.setMessage("当前非WiFi环境,是否继续播放");
					dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
//							if(mMediaPlayer != null && !mMediaPlayer.isPlaying()){
//								mMediaPlayer.setPlayWhenReady(true);
//								iv_play.setImageResource(R.drawable.icon_play_pause);
//							}
							iv_play.setImageResource(R.drawable.icon_play_pause);
							playVideo(video_url);
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

	@Override
	public void setContentView() {
		setContentView(R.layout.play_back);

		Intent formItent = getIntent();
		type = formItent.getStringExtra("type");
		video_url = formItent.getStringExtra("url");//视频地址
		param = (LiveParam) formItent.getSerializableExtra("param");
		xfbsid = formItent.getStringExtra("sid");
		xfbwebinar_id = formItent.getStringExtra("webinar_id");
		String share_pic = formItent.getStringExtra("share_pic");
		image = new UMImage(WatchHLSActivity.this,share_pic);

		is_close = formItent.getBooleanExtra("is_close",false);
		surplus = formItent.getIntExtra("surplus",0);
		from = formItent.getIntExtra("from", Constants.TUIJIAN);
	}

	@Override
	public void initViews() {
		initLayout();
		initView();
	}
	@Override
	public void initListeners() {
	}

	@Override
	public void initData() {
		api = WXAPIFactory.createWXAPI(WatchHLSActivity.this,UrlConstants.APP_ID);
		api.registerApp(UrlConstants.APP_ID);//注册到微信
	}

	private void initLayout(){
		//聊天,详情
		viewPager = (ViewPager) findViewById(R.id.viewPager1);
		tabLayout = (TabLayout) findViewById(R.id.watch_tab);
		tabs = new ArrayList<>();
		tabs.add("聊天");
		tabs.add("详情");
		fragments = new ArrayList<>();
		fragments.add(new TalkFragment());
		fragments.add(new VideoDetailFragment());
		adapter = new MyFragmentpagerAdapter(getSupportFragmentManager(),tabs,fragments);

		tabLayout.setTabMode(TabLayout.MODE_FIXED);
		tabLayout.addTab(tabLayout.newTab().setText(tabs.get(0)));

		tabLayout.addTab(tabLayout.newTab().setText(tabs.get(1)));
		viewPager.setAdapter(adapter);
		tabLayout.setupWithViewPager(viewPager);

		viewPager.setCurrentItem(0);
	}
	private void initView() {
		ll_root = (LinearLayout) findViewById(R.id.ll_root);
		ll_actions = (LinearLayout) this.findViewById(R.id.ll_actions);//暂停开始功能布局
		ll_back_bar = (LinearLayout) findViewById(R.id.ll_back_bar);
		pb = (ProgressBar) this.findViewById(R.id.pb);

		mRelativeVideoSize = (RelativeLayout) this.findViewById(R.id.video_layout);
		surface = (SurfaceView) this.findViewById(R.id.surface);
		iv_play = (ImageView) this.findViewById(R.id.iv_play);
		seekbar = (SeekBar) this.findViewById(R.id.seekbar);
		tv_pos = (TextView) this.findViewById(R.id.tv_pos);
		iv_fullscreen = (ImageView) findViewById(R.id.iv_fullscreen);
		tv_big_title = (TextView) this.findViewById(R.id.tv_big_title);//横屏title
		rl_danlayout = (RelativeLayout) findViewById(R.id.rl_danlayout);
		et_content = (EditText) findViewById(R.id.et_content);
		mDanmakuView = (DanmakuView) findViewById(R.id.sv_danmaku);//弹幕层

		iv_edit = (ImageView) this.findViewById(R.id.iv_edit);
		iv_danmuke = (ImageView) this.findViewById(R.id.iv_danmuke);
		iv_damuke_pori = (ImageView) findViewById(R.id.iv_damuke_pori);
		iv_money = (ImageView) this.findViewById(R.id.iv_money);

		periscopeLayout = (PeriscopeLayout) findViewById(R.id.heart_layout);
		iv_heart = (ImageView) findViewById(R.id.iv_heart);
		tv_support = (TextView) findViewById(R.id.tv_support);
		tv_enjoy = (ImageView) findViewById(R.id.tv_enjoy);

		root_layout = (RelativeLayout) findViewById(R.id.root_layout);
		gesture_volume_layout = (LinearLayout) findViewById(R.id.gesture_volume_layout);
		gesture_bright_layout = (LinearLayout) findViewById(R.id.gesture_bright_layout);
		gesture_progress_layout = (LinearLayout) findViewById(R.id.gesture_progress_layout);
		geture_tv_progress_time = (TextView) findViewById(R.id.geture_tv_progress_time);
		geture_tv_volume_percentage = (TextView) findViewById(R.id.geture_tv_volume_percentage);
		geture_tv_bright_percentage = (TextView) findViewById(R.id.geture_tv_bright_percentage);
		gesture_iv_progress = (ImageView) findViewById(R.id.gesture_iv_progress);
		gesture_iv_player_volume = (ImageView) findViewById(R.id.gesture_iv_player_volume);
		gesture_iv_player_bright = (ImageView) findViewById(R.id.gesture_iv_player_bright);
		gestureDetector = new GestureDetector(this, this);
		root_layout.setLongClickable(true);
		gestureDetector.setIsLongpressEnabled(true);
		root_layout.setOnTouchListener(this);
		audiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume = audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 获取系统最大音量
		currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值


		/** 获取视频播放窗口的尺寸 */
		ViewTreeObserver viewObserver = root_layout.getViewTreeObserver();
		viewObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				root_layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				playerWidth = root_layout.getWidth();
				playerHeight = root_layout.getHeight();
			}
		});

		holder = surface.getHolder();
		//holder.addCallback(this);

		if(1 == getNetype(this) && SPCache.getInstance(WatchHLSActivity.this).getWiFiFlag() != 2){
			if (type.equals("direct")) {
				seekbar.setEnabled(false);
			} else if (type.equals("video")) {
			}
			clickToPlay();
		}

		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				playerCurrentPosition = seekBar.getProgress();
				//快进
				if (mMediaPlayer != null) {
					mMediaPlayer.seekTo(playerCurrentPosition);
					mMediaPlayer.start();
					//initDamu();//快进的时候重新初始化弹幕引擎
				} else {
				}
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				tv_pos.setText(converLongTimeToStr(progress) + "/" + playerDurationTimeStr);
			}
		});
		getBarrage(xfbwebinar_id);//获取弹幕
		getVideoDetail(xfbsid,xfbwebinar_id);
		getSupport();//获取点赞数量

		daShangBtn();//打赏按钮

//		RongIMClient.getInstance().getChatRoomInfo(xfbwebinar_id, 5, ChatRoomInfo.ChatRoomMemberOrder.RC_CHAT_ROOM_MEMBER_DESC, new RongIMClient.ResultCallback<ChatRoomInfo>() {
//			@Override
//			public void onSuccess(ChatRoomInfo chatRoomInfo) {
//				List<ChatRoomMemberInfo> member = chatRoomInfo.getMemberInfo();
//			}
//
//			@Override
//			public void onError(RongIMClient.ErrorCode errorCode) {
//
//			}
//		});
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);//自动感应
		mHandler.postDelayed(hide_bars,3000);
	}

	private void initTimer() {
		if (timer != null)
			return;
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
							playerCurrentPosition = mMediaPlayer.getCurrentPosition();
							seekbar.setProgress((int) playerCurrentPosition);
							String playerCurrentPositionStr = converLongTimeToStr(playerCurrentPosition);
							tv_pos.setText(playerCurrentPositionStr + "/" + playerDurationTimeStr);
						}
					}
				});

			}
		}, 1000, 1000);
	}
	/**
	 * 创建播放器,并播放
	 * @param path
	 */
	private void playVideo(String path) {
		try {
			if (path == "") {
				return;
			}
			// Create a new media player and set the listeners
			String userAgent = Util.getUserAgent(this, "StarRelease");
			mVhallPlayerListener = new VhallPlayerListener();
			mMediaPlayer = new VhallHlsPlayer(new HlsRendererBuilder(this, userAgent, path));
			mMediaPlayer.addListener(mVhallPlayerListener);
			if(errorCurrentPosition != 0){
				mMediaPlayer.seekTo(errorCurrentPosition);//跳转回来的时候按原进度播放
			}else{
				mMediaPlayer.seekTo(playerCurrentPosition);
			}
			mMediaPlayer.prepare();
			mMediaPlayer.setSurface(holder.getSurface());
			if (!this.isFinishing()) {
				mMediaPlayer.setPlayWhenReady(true);
			} else {
				releaseMediaPlayer();
			}
		} catch (Exception e) {
			Log.e(TAG, "error: " + e.getMessage(), e);
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		LogUtil.e("Surface created",holder+"");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		//LogUtil.e("Surface changed",holder+"");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		LogUtil.e("Surface destroyed",holder+"");
	}

	/**
	 * 自定义播放器监听事件处理
	 */
	private class VhallPlayerListener implements VhallHlsPlayer.Listener {
		@Override
		public void onStateChanged(boolean playWhenReady, int playbackState) {
			switch (playbackState) {
			case VhallHlsPlayer.STATE_IDLE:
				Log.e(TAG, "--------------------->STATE_IDLE");
				break;
			case VhallHlsPlayer.STATE_PREPARING:
				pb.setVisibility(View.VISIBLE);
				Log.e(TAG, "--------------------->STATE_PREPARING");
				break;
			case VhallHlsPlayer.STATE_BUFFERING:
				pb.setVisibility(View.VISIBLE);
				Log.e(TAG, "--------------------->STATE_BUFFERING");
				break;
			case VhallHlsPlayer.STATE_READY:
				Log.e(TAG, "--------------------->STATE_READY");
				pb.setVisibility(View.GONE);
				if (type.equals("video")) {
					playerDuration = mMediaPlayer.getDuration();
					playerDurationTimeStr = converLongTimeToStr(playerDuration);
					seekbar.setMax((int) playerDuration);
					initTimer();
				}
				break;
			case VhallHlsPlayer.STATE_ENDED:
				Log.e(TAG, "--------------------->STATE_ENDED");
				pb.setVisibility(View.GONE);
				releaseMediaPlayer();
				break;
			default:
				break;
			}
		}

		/**
		 * 按返回键会调起
		 * @param e
         */
		@Override
		public void onError(Exception e) {
			if(mMediaPlayer != null){
				errorCurrentPosition = mMediaPlayer.getCurrentPosition();
				releaseMediaPlayer();
			}
		}

		@Override
		public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
			if (width == 0 || height == 0) {
				return;
			}
//			Log.e("视频大小改变", "width:" + width + "---" + "height:" + height);
//			Log.e("未应用旋转角度","未应用旋转角度"+unappliedRotationDegrees+";"+"像素宽高比率"+pixelWidthHeightRatio);
			videoWidth = width;
			videoHeight = height;
			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
				setSurfaceFullScreen();
			}else {
				setSurfaceFixSize(200);
			}
		}
	}

	/**
	 * 释放播放器,当前进度置零
	 */
	private void releaseMediaPlayer() {
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
			playerCurrentPosition = 0;
			iv_play.setImageResource(R.drawable.icon_play_play);
		}
	}
	/**
	 * 将长整型值转化成字符串
	 * @param time
	 * @return
	 */
	public static String converLongTimeToStr(long time) {
		int ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;

		//long hour = (time) / hh;
		//long minute = (time - hour * hh) / mi;
		long minute = time / mi;
		long second = (time - minute * mi) / ss;

		//String strHour = hour < 10 ? "0" + hour : "" + hour;
		String strMinute = minute < 10 ? "0" + minute : "" + minute;
		String strSecond = second < 10 ? "0" + second : "" + second;
		if (minute > 10) {
			//return strHour + ":" + strMinute + ":" + strSecond;
			return minute + ":" + strSecond;
		} else {
			return strMinute + ":" + strSecond;
		}
	}

	public void onClick(View v){
		switch(v.getId()){
			case R.id.iv_share:
				if(from == Constants.BAPINGBANG){
					gotoLogin();
				}
				action_share(share_title,share_content,image,url);
				break;
			case R.id.iv_back:
				if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
				if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				}else{
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				}
				break;
			//播放
			case R.id.iv_play:
				clickToPlay();
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
			case R.id.iv_money:
				//new RedPacketDialog(this,"红包打赏(元)").showDialog();
				dialogShow();
				break;
			//声音,亮度层
			case R.id.root_layout:
				//见onSingleTapUp
				break;
			//发弹幕:飘弹幕,发消息,添加评论,保存弹幕
			case R.id.tv_send:
				immersive();
				gotoLogin();

				String sendText = et_content.getText().toString();
				if(TextUtils.isEmpty(sendText)){
					Toast.makeText(this,"消息不能为空",Toast.LENGTH_SHORT).show();
					return;
				}else if(sendText.length() > 40){
					Toast.makeText(this,"字数超出限制",Toast.LENGTH_SHORT).show();
					return;
				}
				addDanmaku(false,sendText,true);//飘弹幕
				et_content.setText("");
				rl_danlayout.setVisibility(View.GONE);
				hideKeyboard();
				ll_actions.setVisibility(View.GONE);
				ll_back_bar.setVisibility(View.GONE);

				TalkFragment talkFragment = (TalkFragment)fragments.get(0);
				TextMessage textMessage = TextMessage.obtain(sendText);
				textMessage.setExtra("消息");
				talkFragment.sendMessage(textMessage);//发消息
				break;
			//点爱心
			case R.id.iv_heart:
				if(from == Constants.BAPINGBANG){
					if(surplus == 0){
						showToast("本小时赞已用完,请稍后再试!");
						return;
					}
					if(is_close == true){
						showToast("活动还未开始,请稍后点赞.");
						return;
					}
					gotoLogin();
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
				mHandler.postDelayed(save_temp_support,3000);
				break;
		}
	}

	/**
	 * 播放,暂停
	 */
	private void clickToPlay(){
		if (TextUtils.isEmpty(video_url))
			return;
		if (mMediaPlayer == null) {
			playVideo(video_url);
			iv_play.setImageResource(R.drawable.icon_play_pause);
		} else {
			if (mMediaPlayer.isPlaying()) {
				// mMediaPlayer.pause();
				mMediaPlayer.setPlayWhenReady(false);
				iv_play.setImageResource(R.drawable.icon_play_play);
			} else {
				// mMediaPlayer.start();
				mMediaPlayer.setPlayWhenReady(true);
				iv_play.setImageResource(R.drawable.icon_play_pause);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
		//com.umeng.socialize.utils.Log.d("result","onActivityResult");
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

	private Runnable send_heart = new Runnable() {
		@Override
		public void run() {
			TalkFragment talkFragment = (TalkFragment)fragments.get(0);
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

	private Runnable save_temp_support = new Runnable() {
		@Override
		public void run() {
			saveTempSupport(temp_heart_count);
			if(from == Constants.BAPINGBANG){
				saveActivitySupport(xfbwebinar_id,temp_heart_count+"");
			}
			temp_heart_count = 0;
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

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			iv_fullscreen.setVisibility(View.VISIBLE);
			iv_fullscreen.setImageResource(R.drawable.icon_full);
			iv_edit.setVisibility(View.GONE);
			rl_danlayout.setVisibility(View.GONE);
			periscopeLayout.setVisibility(View.GONE);//隐藏弹出心得控件
			iv_heart.setVisibility(View.GONE);
			tv_support.setVisibility(View.GONE);
			iv_danmuke.setVisibility(View.GONE);
			iv_damuke_pori.setVisibility(View.VISIBLE);
			unImmersive();
			setSurfaceFixSize(200);
			//ll_root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
		}else if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
//			iv_fullscreen.setImageResource(R.drawable.icon_unfull);
			iv_fullscreen.setVisibility(View.GONE);
			iv_edit.setVisibility(View.VISIBLE);
			periscopeLayout.setVisibility(View.VISIBLE);
			iv_heart.setVisibility(View.VISIBLE);
			tv_support.setVisibility(View.VISIBLE);
			iv_danmuke.setVisibility(View.VISIBLE);
			iv_damuke_pori.setVisibility(View.GONE);
			immersive();
			setSurfaceFullScreen();
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			StarReleaseUtil.closeImm(this);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				return true;
			}
		finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStop() {
		super.onStop();
		//releaseMediaPlayer();
		OkHttpUtils.getInstance().cancelTag(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseMediaPlayer();
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
	public void onAttachFragment(Fragment fragment) {
	if(fragment instanceof VideoDetailFragment){
//		VideoDetailFragment v = (VideoDetailFragment)fragment;
//				v.getVideoContent(xfbsid,xfbwebinar_id);
	}else if(fragment instanceof TalkFragment){

		TalkFragment t = (TalkFragment) fragment;
		Bundle db = new Bundle();
		if(xfbwebinar_id != null){
			db.putCharSequence("webinar_id",xfbwebinar_id);
		}
		t.setData(db);
	}
		super.onAttachFragment(fragment);
	}

	/**
	 * 获取回放当前时间,毫秒,直播进行的时间,毫秒
	 */
	public void setCurrentValue(){
		Bundle bundle = new Bundle();
		TalkFragment t = (TalkFragment)fragments.get(0);
		if("direct".equals(type) && start_time != null){
			Long direct_time = System.currentTimeMillis() - (Long.parseLong(start_time)) * 1000;//直播开始的毫秒
			bundle.putCharSequence("direct",direct_time+"");
		}else if("video".equals(type)){
			if(playerCurrentPosition == 0){
				playerCurrentPosition = playerDuration;
			}
			bundle.putCharSequence("videoCurrentTime",playerCurrentPosition+"");//回放的毫秒数
		}
		t.setData(bundle);
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
				.execute(new MyStringCallback(new Loading(this)){
					@Override
					public void onError(Call call, Exception e, int id) {
						super.onError(call, e, id);
					}

					@Override
					public void onResponse(String response, int id) {
						super.onResponse(response, id);
						VideoDetail res = VideoDetail.fromJson(response,VideoDetail.class);
						//详情传给详情fragment
						VideoDetailFragment detailFragment = (VideoDetailFragment)fragments.get(1);
						detailFragment.setVideoDetail(res);

						is_like = res.result.is_like;
						if(true == is_like){
							tv_enjoy.setImageResource(R.drawable.icon_collection);
						}
						start_time = res.result.start_time;
						tv_big_title.setText(res.result.subject);//title
						share_title = res.result.subject;
						share_content = res.result.introduction;//分享用

						url = UrlConstants.SHARE_URL + "?id=" + xfbwebinar_id +"&title=" + share_title + "&tag=e";
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
						BarrageList resp = BarrageList.fromJson(response, BarrageList.class);
						TalkFragment talkFragment = (TalkFragment)fragments.get(0);
						talkFragment.setBarrage(resp.result);
					}
				});
	}

	private void getSupport(){
		String token = StarReleaseUtil.getToken(this);
		OkHttpUtils.post()
				.url(UrlConstants.GET_SUPPORT)
				.addParams("webinar_id", xfbwebinar_id)
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
						surplus = resp.result.surplus;
					}
				});
	}

	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
	}
	public void setNickName(String name){
		this.nickName = name;
	}

	public void immersive(){
		int uiOptions = ll_root.getSystemUiVisibility();
		uiOptions &= ~View.SYSTEM_UI_FLAG_LOW_PROFILE;
		uiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
		uiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE;
		uiOptions &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		ll_root.setSystemUiVisibility(uiOptions);
	}
	public void unImmersive(){
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//没有使用
		//getMenuInflater().inflate(R.menu.menu_watch, menu);
		//MenuItem menuItem = menu.findItem(R.id.action_share);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		switch(item.getItemId()) {
//			case R.id.action_share:
//				action_share("星发布直播:",share_title,image,url);
//				supportInvalidateOptionsMenu();
//				return true;
//		}
		return super.onOptionsItemSelected(item);
	}

	public void setSurfaceFixSize(int h) {
		if (videoWidth == 0 || videoHeight == 0)
			return;
		int fixWidth = 0;
		int fixHeight = 0;

		fixHeight = ScreenSizeUtil.Dp2Px(this, h);
		fixWidth = videoWidth * fixHeight / videoHeight;
		if (fixWidth <= 0 || fixHeight <= 0)
			return;

		if (surface != null) {
			//mRelativeVideoSize.setLayoutParams(new RelativeLayout.LayoutParams(fixWidth,fixHeight));
			surface.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, fixHeight));
			surface.getHolder().setFixedSize(fixWidth, fixHeight);
			mDanmakuView.setLayoutParams(new RelativeLayout.LayoutParams(fixWidth,fixHeight));
		}
	}

	public void setSurfaceFullScreen() {
//		boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
//		boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
//		int screenWidth = ScreenSizeUtil.getScreenWidth(this);
		int screenHeight = ScreenSizeUtil.getScreenHeight(this);
		if (videoWidth == 0 || videoHeight == 0)
			return;
		int fixWidth = 0;
		int fixHeight = 0;
		fixHeight = screenHeight;
		fixWidth= (screenHeight * videoWidth) / videoHeight ;
		if (fixWidth <= 0 || fixHeight <= 0)
			return;
		if (surface != null){
			surface.setLayoutParams(new RelativeLayout.LayoutParams(fixWidth,fixHeight));//ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
			surface.getHolder().setFixedSize(fixWidth, fixHeight);
			mDanmakuView.setLayoutParams(new RelativeLayout.LayoutParams(fixWidth,fixHeight));
		}
	}

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
			hideKeyboard();
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

	private Runnable hide_bars = new Runnable() {
		@Override
		public void run() {
			ll_back_bar.setVisibility(View.GONE);
			ll_actions.setVisibility(View.GONE);
		}
	};

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		float mOldX = e1.getX(), mOldY = e1.getY();
		int y = (int) e2.getRawY();

		//onScroll是多次执行的
		if (firstScroll) {// 以触摸屏幕后第一次滑动为标准，避免在屏幕上操作切换混乱
			// 横向的距离变化大则调整进度，纵向的变化大则调整音量
			if (Math.abs(distanceX) >= Math.abs(distanceY)) {
				gesture_progress_layout.setVisibility(View.VISIBLE);
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
					if (playingTime < videoTotalTime - 10) {// 避免超过总时长
						playingTime += 10;// scroll执行一次快进3秒
					} else {
						playingTime = videoTotalTime;
					}
				}
				if (playingTime < 0) {
					playingTime = 0;
				}else if(playingTime > videoTotalTime){
					playingTime = videoTotalTime;
				}
				mMediaPlayer.seekTo(playingTime * 1000);
				geture_tv_progress_time.setText(converLongTimeToStr(playingTime * 1000) + "/" + converLongTimeToStr(videoTotalTime * 1000));
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

	public void getSegment(final String payType, String note, String price, String sid, String subject){
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
							String orderInfo = StarReleaseUtil.getOrderInfo("红包打赏", "红包打赏", "0.01",res.result.orderNo);
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
									// 构造PayTask 对象
									PayTask alipay = new PayTask(WatchHLSActivity.this);
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
						getSegment("1",talk,red_money,xfbsid,share_title);
						red_money = null;
						errorCurrentPosition = mMediaPlayer.getCurrentPosition();//记录进度
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

	//微信打赏回调
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		//TODO 优化打赏成功
		//playVideo(video_url);
		switch(intent.getStringExtra("paystatus")){
			case "100":
				redPacketResult();
				break;
		}
	}
	/**
	 * 打赏成功回调
	 */
	public void redPacketResult(){
		showToast("打赏成功");
		addDanmaKuTextAndImage(false,nickName+"打赏了一个[红包]",true);//发弹幕
		TalkFragment talkFragment = (TalkFragment)fragments.get(0);
		TextMessage textMessage = TextMessage.obtain(nickName);
		textMessage.setExtra("打赏|"+nickName+"打赏了一个[红包]");
		talkFragment.sendMessage(textMessage);//发消息
	}
	/**
	 * 打赏按钮
	 */
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
	//没登录去登录
	private void gotoLogin(){
		if(TextUtils.isEmpty(SPCache.getInstance(this).getToken())){
//			startActivity(new Intent(this, LoginActivity.class));
//			finish();
			return;
		}
	}

	//每日分享
	private void dailyShare(String webinar_id){
		String token = SPCache.getInstance(this).getToken();
		String sig = MD5Helper.GetMD5Code("webinar_id=" + webinar_id + "&Authorization=" + token + UrlConstants.sign);
		OkHttpUtils.post()
				.url(UrlConstants.DAILYSHARE)
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
						DailyShare resp = DailyShare.fromJson(response,DailyShare.class);
							if(resp.result > 0){
								showToast("首次分享成功!加500赞!");
							}else{
								showToast("分享成功!");
							}
					}
				});
	}
}