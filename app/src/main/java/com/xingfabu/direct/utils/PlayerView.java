package com.xingfabu.direct.utils;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.vhall.business.WatchLive;
import com.vhall.business.WatchPlayback;
import com.vhall.business.widget.ContainerLayout;
import com.vhall.playersdk.player.impl.VhallHlsPlayer;
import com.xingfabu.direct.R;
import com.xingfabu.direct.app.Param;

/**
 * Created by guoping on 2016/12/6.
 */

public class PlayerView {
    private static final String TAG = PlayerView.class.getSimpleName();
    private final Context mContext;
    /**
     * 依附的容器Activity
     */
    private final AppCompatActivity mActivity;
    /**
     * 获取当前设备的宽度
     */
    private final int screenWidthPixels;
    /**
     * 音频管理器
     */
    private final AudioManager audioManager;
    /**
     * 设备最大音量
     */
    private final int mMaxVolume;
    /**
     * 播放器整个界面
     */
    private  View rl_box;
    private  ContainerLayout container;
    /**
     * 播放器顶部控制bar
     */
    private  View ll_topbar;
    /**
     * 播放器底部控制bar
     */
    private  View ll_bottombar;
    /**
     * 视频返回按钮
     */
    private  ImageView iv_back;
    /**
     * 视频bottombar的播放按钮
     */
    private  ImageView iv_bar_player;
    private  ImageView iv_fullscreen;
    private TextView tv_speed;
    private SeekBar seekBar;
    /**
     * 播放总时长
     */
    private long duration;
    private Param param;
    private WatchPlayback watchPlayback;
    private WatchLive watchLive;
    /**
     * Activity界面的中布局的查询器
     */
    private final LayoutQuery query;
    /**
     * 是否在拖动进度条中，默认为停止拖动，true为在拖动中，false为停止拖动
     */
    private boolean isDragging;
    /**
     * 同步进度
     */
    private static final int MESSAGE_SHOW_PROGRESS = 1;
    /**
     * 禁止触摸，默认可以触摸，true为禁止false为可触摸
     */
    private boolean isForbidTouch;

    /**
     * 是否只有全屏，默认非全屏，true为全屏，false为非全屏
     */
    private boolean isOnlyFullScreen;
    /**
     * 是否禁止双击，默认不禁止，true为禁止，false为不禁止
     */
    private boolean isForbidDoulbeUp;

    /**
     * 是否是直播 默认为非直播，true为直播false为点播
     */
    private boolean isLive;

    /**
     * 滑动进度条得到的新位置，和当前播放位置是有区别的,newPosition =0也会调用设置的，故初始化值为-1
     */
    private long newPosition = -1;
    /**
     * 当前声音大小
     */
    private int volume;
    /**
     * 当前亮度大小
     */
    private float brightness;
    /**
     * 是否显示控制面板，默认为隐藏，true为显示false为隐藏
     */
    private boolean isShowControlPanl;
    /**
     * 是否隐藏topbar，true为隐藏，false为不隐藏
     */
    private boolean isHideTopBar;
    /**
     * 是否隐藏bottonbar，true为隐藏，false为不隐藏
     */
    private boolean isHideBottonBar;
    /**
     * 控制面板显示或隐藏监听
     */
    private OnControlPanelVisibilityChangeListener onControlPanelVisibilityChangeListener;
    /**
     * 当前状态
     */
    private int status = VhallHlsPlayer.STATE_IDLE;
    /**
     * 禁止收起控制面板，默认可以收起，true为禁止false为可触摸
     */
    private boolean isForbidHideControlPanl;
    /**
     * 控制面板收起或者显示的轮询监听
     */
    private AutoPlayRunnable mAutoPlayRunnable = new AutoPlayRunnable();
    /**
     * 隐藏提示的box
     */
    private static final int MESSAGE_HIDE_CENTER_BOX = 4;
    /**
     * 设置新位置
     */
    private static final int MESSAGE_SEEK_NEW_POSITION = 3;
    /**
     * 重新播放
     */
    private static final int MESSAGE_RESTART_PLAY = 5;
    /**
     * 当前播放位置
     */
    private long currentPosition;
    /**
     * 播放的时候是否需要网络提示，默认显示网络提示，true为显示网络提示，false不显示网络提示
     */
    private boolean isGNetWork = true;
    /**
     * Activity界面方向监听
     */
    private OrientationEventListener orientationEventListener;
    /**
     * 是否是竖屏，默认为竖屏，true为竖屏，false为横屏
     */
    private boolean isPortrait = true;
    /**
     * 记录播放器竖屏时的高度
     */
    private final int initHeight;
    /**
     * 视频的返回键监听
     */
    private OnPlayerBackListener mPlayerBack;
    /**
     * 记录进入后台时的播放状态0为播放，1为暂停
     */
    private int bgState;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**滑动完成，隐藏滑动提示的box*/
                case MESSAGE_HIDE_CENTER_BOX:
                    query.id(R.id.video_volume_box).gone();
                    query.id(R.id.video_brightness_box).gone();
                    query.id(R.id.video_fastForward_box).gone();
                    break;
                /**滑动完成，设置播放进度*/
                case MESSAGE_SEEK_NEW_POSITION:
                    if (!isLive && newPosition >= 0) {
                        watchPlayback.seekTo(newPosition);
                        newPosition = -1;
                    }
                    break;
                /**滑动中，同步播放进度*/
                case MESSAGE_SHOW_PROGRESS:
                    long pos = syncProgress();
                    if (!isDragging && isShowControlPanl) {
                        msg = obtainMessage(MESSAGE_SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                        updatePausePlay();
                    }
                    break;
                /**重新去播放*/
                case MESSAGE_RESTART_PLAY:
//                    status = PlayStateParams.STATE_ERROR;
//                    startPlay();
//                    updatePausePlay();
                    break;
            }
        }
    };

    /**
     * 点击事件监听
     */
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.iv_fullscreen) {
                /**视频全屏切换*/
                toggleFullScreen();
            } else if (v.getId() == R.id.iv_play) {
                /**视频播放和暂停*/
                if (watchLive.isPlaying() || watchPlayback.isPlaying()) {
                    if (isLive) {
                        watchLive.stop();
                        audioManager.abandonAudioFocus(null);
                    } else {
                        pausePlay();
                    }
                } else {
                    startPlay();
                    if (watchPlayback.isPlaying()) {
                        status = VhallHlsPlayer.STATE_READY;
                        hideStatusUI();
                    }
                }
                updatePausePlay();
            } else if (v.getId() == R.id.iv_back) {
                /**返回*/
                if (!isOnlyFullScreen && !isPortrait) {
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    if (mPlayerBack != null) {
                        mPlayerBack.onPlayerBack();
                    } else {
                        mActivity.finish();
                    }
                }
            } else if (v.getId() == R.id.video_continue) {
                /**使用移动网络提示继续播放*/
                isGNetWork = false;
                hideStatusUI();
                startPlay();
                updatePausePlay();
            } else if (v.getId() == R.id.video_replay_icon) {
                /**重新播放*/
                status = VhallHlsPlayer.STATE_IDLE;
                hideStatusUI();
                startPlay();
                updatePausePlay();
            }
        }
    };

    /**
     * 进度条滑动监听
     */
    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {

        /**数值的改变*/
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                /**不是用户拖动的，自动播放滑动的情况*/
                return;
            } else {
                long duration = getDuration();
                int position = (int) ((duration * progress * 1.0) / 1000);
                String time = generateTime(position);
                query.id(R.id.video_currentTime).text(time);
            }

        }

        /**开始拖动*/
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isDragging = true;
            mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
        }

        /**停止拖动*/
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            long duration = getDuration();
            watchPlayback.seekTo((int) ((duration * seekBar.getProgress() * 1.0) / 1000));
            mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
            isDragging = false;
            mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_PROGRESS, 1000);
        }
    };

    public PlayerView(AppCompatActivity activity , View rootView, Param param, WatchPlayback watchPlayback, WatchLive watchLive){
        this.mActivity = activity;
        this.mContext = activity;
        this.param = param;
        this.watchPlayback = watchPlayback;
        this.watchLive = watchLive;
        screenWidthPixels = mContext.getResources().getDisplayMetrics().widthPixels;
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        query = new LayoutQuery(mActivity, rootView);
        rl_box = rootView.findViewById(R.id.video_box);
        container = (ContainerLayout) rootView.findViewById(R.id.video_container);
        try {
            int e = Settings.System.getInt(this.mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            float progress = 1.0F * (float) e / 255.0F;
            android.view.WindowManager.LayoutParams layout = this.mActivity.getWindow().getAttributes();
            layout.screenBrightness = progress;
            mActivity.getWindow().setAttributes(layout);
        } catch (Settings.SettingNotFoundException e1) {
            e1.printStackTrace();
        }
        ll_topbar = rootView.findViewById(R.id.ll_back_bar);
        ll_bottombar = rootView.findViewById(R.id.ll_bottom_bar);
        iv_back = (ImageView) rootView.findViewById(R.id.iv_back);
        iv_bar_player = (ImageView) rootView.findViewById(R.id.iv_play);
        iv_fullscreen = (ImageView) rootView.findViewById(R.id.iv_fullscreen);
        tv_speed = (TextView) rootView.findViewById(R.id.video_speed);
        seekBar = (SeekBar) rootView.findViewById(R.id.seekbar);

        seekBar.setMax(1000);
        seekBar.setOnSeekBarChangeListener(mSeekListener);
        iv_bar_player.setOnClickListener(onClickListener);
        iv_fullscreen.setOnClickListener(onClickListener);
        iv_back.setOnClickListener(onClickListener);
        query.id(R.id.video_continue).clicked(onClickListener);
        query.id(R.id.video_replay_icon).clicked(onClickListener);

        final GestureDetector gestureDetector = new GestureDetector(mContext, new PlayerGestureListener());
        rl_box.setClickable(true);
        rl_box.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        if (mAutoPlayRunnable != null) {
                            mAutoPlayRunnable.stop();
                        }
                        break;
                }
                if (gestureDetector.onTouchEvent(motionEvent))
                    return true;
                // 处理手势结束
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        endGesture();
                        break;
                }
                return false;
            }
        });


        orientationEventListener = new OrientationEventListener(mActivity) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation >= 0 && orientation <= 30 || orientation >= 330 || (orientation >= 150 && orientation <= 210)) {
                    //竖屏
                    if (isPortrait) {
                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        orientationEventListener.disable();
                    }
                } else if ((orientation >= 90 && orientation <= 120) || (orientation >= 240 && orientation <= 300)) {
                    if (!isPortrait) {
                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        orientationEventListener.disable();
                    }
                }
            }
        };
        if (isOnlyFullScreen) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        isPortrait = (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initHeight = rl_box.getLayoutParams().height;
        hideAll();
    }

    /**
     * 隐藏所有界面
     */
    private void hideAll() {
        if (!isForbidHideControlPanl) {
            ll_topbar.setVisibility(View.GONE);
            ll_bottombar.setVisibility(View.GONE);
        }
        hideStatusUI();
    }

    /**
     * 手势结束
     */
    private void endGesture() {
        volume = -1;
        brightness = -1f;
        if (newPosition >= 0) {
            mHandler.removeMessages(MESSAGE_SEEK_NEW_POSITION);
            mHandler.sendEmptyMessage(MESSAGE_SEEK_NEW_POSITION);
        } else {
            /**什么都不做(do nothing)*/
        }
        mHandler.removeMessages(MESSAGE_HIDE_CENTER_BOX);
        mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE_CENTER_BOX, 500);
        if (mAutoPlayRunnable != null) {
            mAutoPlayRunnable.start();
        }

    }
    /**
     * 获取视频播放总时长
     */
    public long getDuration() {
        duration = watchPlayback.getDuration();
        return duration;
    }
    /**
     * 时长格式化显示
     */
    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }
    /**
     * ==========================================内部类=============================
     */

    /**
     * 收起控制面板轮询，默认5秒无操作，收起控制面板，
     */
    private class AutoPlayRunnable implements Runnable {
        private int AUTO_PLAY_INTERVAL = 5000;
        private boolean mShouldAutoPlay;

        /**
         * 五秒无操作，收起控制面板
         */
        public AutoPlayRunnable() {
            mShouldAutoPlay = false;
        }

        public void start() {
            if (!mShouldAutoPlay) {
                mShouldAutoPlay = true;
                mHandler.removeCallbacks(this);
                mHandler.postDelayed(this, AUTO_PLAY_INTERVAL);
            }
        }

        public void stop() {
            if (mShouldAutoPlay) {
                mHandler.removeCallbacks(this);
                mShouldAutoPlay = false;
            }
        }

        @Override
        public void run() {
            if (mShouldAutoPlay) {
                mHandler.removeCallbacks(this);
                if (!isForbidTouch && !isShowControlPanl) {
                    operatorPanl();
                }
            }
        }
    }

    /**
     * 播放器的手势监听
     */
    public class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener {

        /**
         * 是否是按下的标识，默认为其他动作，true为按下标识，false为其他动作
         */
        private boolean isDownTouch;
        /**
         * 是否声音控制,默认为亮度控制，true为声音控制，false为亮度控制
         */
        private boolean isVolume;
        /**
         * 是否横向滑动，默认为纵向滑动，true为横向滑动，false为纵向滑动
         */
        private boolean isLandscape;

        /**
         * 双击
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            /**视频视窗双击事件*/
            if (!isForbidTouch && !isOnlyFullScreen && !isForbidDoulbeUp) {
                toggleFullScreen();
            }
            return true;
        }

        /**
         * 按下
         */
        @Override
        public boolean onDown(MotionEvent e) {
            isDownTouch = true;
            return super.onDown(e);
        }


        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!isForbidTouch) {
                float mOldX = e1.getX(), mOldY = e1.getY();
                float deltaY = mOldY - e2.getY();
                float deltaX = mOldX - e2.getX();
                if (isDownTouch) {
                    isLandscape = Math.abs(distanceX) >= Math.abs(distanceY);
                    isVolume = mOldX > screenWidthPixels * 0.5f;
                    isDownTouch = false;
                }

                if (isLandscape) {
                    if (!isLive) {
                        /**进度设置*/
                        onProgressSlide(-deltaX / container.getWidth());
                    }
                } else {
                    float percent = deltaY / container.getHeight();
                    if (isVolume) {
                        /**声音设置*/
                        onVolumeSlide(percent);
                    } else {
                        /**亮度设置*/
                        onBrightnessSlide(percent);
                    }


                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        /**
         * 单击
         */
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            /**视频视窗单击事件*/
            if (!isForbidTouch) {
                operatorPanl();
            }
            return true;
        }
    }

    /**
     * 显示或隐藏操作面板
     */
    public PlayerView operatorPanl() {
        isShowControlPanl = !isShowControlPanl;
        if (isShowControlPanl) {
            ll_topbar.setVisibility(isHideTopBar ? View.GONE : View.VISIBLE);
            ll_bottombar.setVisibility(isHideBottonBar ? View.GONE : View.VISIBLE);
            if (isLive) {
                query.id(R.id.video_process_panl).invisible();
            } else {
                query.id(R.id.video_process_panl).visible();
            }
            if (isOnlyFullScreen || isForbidDoulbeUp) {
                iv_fullscreen.setVisibility(View.GONE);
            } else {
                iv_fullscreen.setVisibility(View.VISIBLE);
            }
            if (onControlPanelVisibilityChangeListener != null) {
                onControlPanelVisibilityChangeListener.change(true);
            }

            updatePausePlay();
            mHandler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
            mAutoPlayRunnable.start();
        } else {
            if (isHideTopBar) {
                ll_topbar.setVisibility(View.GONE);
            } else {
                ll_topbar.setVisibility(isForbidHideControlPanl ? View.VISIBLE : View.GONE);

            }
            if (isHideBottonBar) {
                ll_bottombar.setVisibility(View.GONE);
            } else {
                ll_bottombar.setVisibility(isForbidHideControlPanl ? View.VISIBLE : View.GONE);
            }
            mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
            if (onControlPanelVisibilityChangeListener != null) {
                onControlPanelVisibilityChangeListener.change(false);
            }
            mAutoPlayRunnable.stop();
        }
        return this;
    }

    /**
     * 更新播放、暂停和停止按钮
     */
    private void updatePausePlay() {
        if (watchPlayback.isPlaying()) {
            iv_bar_player.setImageResource(R.drawable.icon_play_pause);
        } else {
            iv_bar_player.setImageResource(R.drawable.icon_play_play);
        }
    }

    /**
     * 亮度滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        if (brightness < 0) {
            brightness = mActivity.getWindow().getAttributes().screenBrightness;
            if (brightness <= 0.00f) {
                brightness = 0.50f;
            } else if (brightness < 0.01f) {
                brightness = 0.01f;
            }
        }
        Log.d(this.getClass().getSimpleName(), "brightness:" + brightness + ",percent:" + percent);
        query.id(R.id.video_brightness_box).visible();
        WindowManager.LayoutParams lpa = mActivity.getWindow().getAttributes();
        lpa.screenBrightness = brightness + percent;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }
        query.id(R.id.video_brightness).text(((int) (lpa.screenBrightness * 100)) + "%");
        mActivity.getWindow().setAttributes(lpa);
    }

    /**
     * 滑动改变声音大小
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        if (volume == -1) {
            volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (volume < 0)
                volume = 0;
        }
        int index = (int) (percent * mMaxVolume) + volume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        // 变更进度条
        int i = (int) (index * 1.0 / mMaxVolume * 100);
        String s = i + "%";
        if (i == 0) {
            s = "off";
        }
        // 显示
        query.id(R.id.video_volume_icon).image(i == 0 ? R.drawable.simple_player_volume_off_white_36dp : R.drawable.simple_player_volume_up_white_36dp);
        query.id(R.id.video_brightness_box).gone();
        query.id(R.id.video_volume_box).visible();
        query.id(R.id.video_volume).text(s).visible();
    }

    /**
     * 快进或者快退滑动改变进度
     * @param percent
     */
    private void onProgressSlide(float percent) {
        long position = watchPlayback.getCurrentPosition();
        long duration = watchPlayback.getDuration();
        long deltaMax = Math.min(100 * 1000, duration - position);
        long delta = (long) (deltaMax * percent);
        newPosition = delta + position;
        if (newPosition > duration) {
            newPosition = duration;
        } else if (newPosition <= 0) {
            newPosition = 0;
            delta = -position;
        }
        int showDelta = (int) delta / 1000;
        if (showDelta != 0) {
            query.id(R.id.video_fastForward_box).visible();
            String text = showDelta > 0 ? ("+" + showDelta) : "" + showDelta;
            query.id(R.id.video_fastForward).text(text + "s");
            query.id(R.id.fastForward_target).text(generateTime(newPosition) + "/");
            query.id(R.id.fastForward_all).text(generateTime(duration));
        }
    }

    /**
     * 全屏切换
     */
    public PlayerView toggleFullScreen() {
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        updateFullScreenButton();
        return this;
    }

    /**
     * 更新全屏和半屏按钮
     */
    private void updateFullScreenButton() {
//        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            iv_fullscreen.setImageResource(R.drawable.simple_player_icon_fullscreen_shrink);
//        } else {
//            iv_fullscreen.setImageResource(R.drawable.simple_player_icon_fullscreen_stretch);
//        }
    }

    /**
     * 获取界面方向
     */
    public int getScreenOrientation() {
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    /**
     * 设置控制面板显示隐藏监听
     */
    public PlayerView setOnControlPanelVisibilityChangListenter(OnControlPanelVisibilityChangeListener listener) {
        this.onControlPanelVisibilityChangeListener = listener;
        return this;
    }

    /**
     * 同步进度
     */
    private long syncProgress() {
        if (isDragging) {
            return 0;
        }
        long position = watchPlayback.getCurrentPosition();
        long duration = watchPlayback.getDuration();
        if (seekBar != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                seekBar.setProgress((int) pos);
            }
//            int percent = watchPlayback.getBufferPercentage();
//            seekBar.setSecondaryProgress(percent * 10);
        }
        query.id(R.id.video_currentTime).text(generateTime(position));
        query.id(R.id.video_endTime).text(generateTime(duration));
        return position;
    }

    /**
     * 暂停播放
     */
    public PlayerView pausePlay() {
        status = VhallHlsPlayer.STATE_IDLE;//pause
        getCurrentPosition();
        watchPlayback.pause();
        return this;
    }
    /**
     * 获取当前播放位置
     */
    public long getCurrentPosition() {
        if (!isLive) {
            currentPosition = watchPlayback.getCurrentPosition();
        } else {
            /**直播*/
            currentPosition = -1;
        }
        return currentPosition;
    }

    /**
     * 开始播放
     */
    public PlayerView startPlay() {
        hideStatusUI();
        if (isGNetWork && (NetworkUtils.getNetworkType(mContext) == 4 || NetworkUtils.getNetworkType(mContext) == 5 || NetworkUtils.getNetworkType(mContext) == 6)) {
            query.id(R.id.video_net).visible();
        }else{
            if (isLive) {
                watchLive.start();
            }else{
                watchPlayback.start();
            }
        }
        return this;
    }
    /**
     * 隐藏状态界面
     */
    private void hideStatusUI() {
        query.id(R.id.video_replay).gone();
        query.id(R.id.video_net).gone();
        query.id(R.id.video_loading).gone();
        if (onControlPanelVisibilityChangeListener != null) {
            onControlPanelVisibilityChangeListener.change(false);
        }
    }

    /**
     * 设置播放器中的返回键监听
     */
    public PlayerView setPlayerBackListener(OnPlayerBackListener listener) {
        this.mPlayerBack = listener;
        return this;
    }

    public PlayerView onPause() {
        if(isLive){
            bgState = (watchLive.isPlaying() ? 0 : 1);
        }else {
            bgState = (watchPlayback.isPlaying() ? 0 : 1);
        }
        getCurrentPosition();
        if(isLive){
            watchLive.stop();
        }else {
            watchPlayback.pause();
        }
        return this;
    }

    public PlayerView onResume() {
        if (isLive) {
            watchLive.start();
        } else {
            watchPlayback.seekTo(currentPosition);
        }
        if (bgState == 0) {

        } else {
            pausePlay();
        }
        return this;
    }

    public PlayerView onDestroy() {
        orientationEventListener.disable();
        mHandler.removeMessages(MESSAGE_RESTART_PLAY);
        mHandler.removeMessages(MESSAGE_SEEK_NEW_POSITION);
        if(isLive){
            watchLive.destory();
        }else {
            watchPlayback.stop();
        }
        return this;
    }

    public PlayerView onConfigurationChanged(final Configuration newConfig) {
        isPortrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
        doOnConfigurationChanged(isPortrait);
        return this;
    }

    /**
     * 界面方向改变是刷新界面
     */
    private void doOnConfigurationChanged(final boolean portrait) {
        if (watchPlayback != null && !isOnlyFullScreen) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    setFullScreen(!portrait);
                    if (portrait) {
                        query.id(R.id.video_box).height(initHeight, false);
                    } else {
                        int heightPixels = mActivity.getResources().getDisplayMetrics().heightPixels;
                        int widthPixels = mActivity.getResources().getDisplayMetrics().widthPixels;
                        query.id(R.id.video_box).height(Math.min(heightPixels, widthPixels), false);
                    }
                    updateFullScreenButton();
                }
            });
            orientationEventListener.enable();
        }
    }

    /**
     * 设置界面方向带隐藏actionbar
     */
    private void tryFullScreen() {
        if (mActivity instanceof AppCompatActivity) {
            ActionBar supportActionBar = ((AppCompatActivity) mActivity).getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.hide();
            }
        }
    }

    /**
     * 设置界面方向
     */
    private void setFullScreen(boolean fullScreen) {
        if (mActivity != null) {
//            WindowManager.LayoutParams attrs = mActivity.getWindow().getAttributes();
            if (fullScreen) {
                immersiveStick();
//                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//                mActivity.getWindow().setAttributes(attrs);
//                mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                unImmersiveStick();
//                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                mActivity.getWindow().setAttributes(attrs);
//                mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
            //进度条和时长显示的方向切换
        }
    }

    private void immersiveStick(){
        View decorView = mActivity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void unImmersiveStick(){
        View decorView = mActivity.getWindow().getDecorView();
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

    public interface OnControlPanelVisibilityChangeListener {
        /**true 为显示 false为隐藏*/
        void change(boolean isShowing);
    }
    public interface OnPlayerBackListener {
        /**用户点击视频view中的返回键回调，可以在这里做一些处理*/
        void onPlayerBack();

    }
}
