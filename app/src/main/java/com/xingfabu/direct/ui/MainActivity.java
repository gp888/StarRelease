package com.xingfabu.direct.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.umeng.message.IUmengCallback;
import com.umeng.message.MessageSharedPrefs;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.xingfabu.direct.R;
import com.xingfabu.direct.app.App;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.cache.SPCache;
import com.xingfabu.direct.entity.AdverImage;
import com.xingfabu.direct.entity.DuiBaResponse;
import com.xingfabu.direct.entity.User;
import com.xingfabu.direct.entity.VersionUpdate;
import com.xingfabu.direct.fragment.ClassifyFragment;
import com.xingfabu.direct.fragment.DirectFragment;
import com.xingfabu.direct.fragment.StarInfoFragment;
import com.xingfabu.direct.transform.GlideCircleTransform;
import com.xingfabu.direct.utils.MD5Helper;
import com.xingfabu.direct.utils.MyStringCallback;
import com.xingfabu.direct.utils.StarReleaseUtil;
import com.xingfabu.direct.widget.UpDateDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import java.io.File;
import java.util.ArrayList;
import io.rong.imlib.RongIMClient;
import okhttp3.Call;

public class MainActivity extends BaseActivity {
    private FragmentManager fm;
    private RadioGroup tab_menu;
    private Toolbar toolBar;
    private ImageView search_main,navi_head,logo_main;
    private TextView title_main;
    private NavigationView navigationView_main;
    private DrawerLayout drawerLayout_main;
    private TextView user_id;
    private ArrayList<AdverImage> imageCache = new ArrayList<>();
    private int HttpImageCount = 1;//广告的数量
    private int currentTarget = 0;
    private int HttpImageTime;//网络更新时间
    private  String ad_pic_url;//图片地址
    private PushAgent mPushAgent;//友盟推送

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initViews() {
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.setTitle("");
        //toolBar.setNavigationIcon(R.mipmap.ic_launcher);
        //toolBar.setLogo(R.mipmap.ic_launcher);//设置app logo
        setSupportActionBar(toolBar);

        drawerLayout_main = (DrawerLayout) findViewById(R.id.drawerLayout_main);
        //drawerLayout_main.setScrimColor(Color.TRANSPARENT);//去除抽屉划出后内容显示页背景的灰色
        //drawerLayout_main.setDrawerShadow();
        logo_main = (ImageView) findViewById(R.id.logo_main);
        search_main = (ImageView) findViewById(R.id.search_main);
        title_main = (TextView) findViewById(R.id.title_main);
        navigationView_main = (NavigationView) findViewById(R.id.navigationView_main);
        navi_head = (ImageView) navigationView_main.getHeaderView(0).findViewById(R.id.navi_head);
        user_id = (TextView) navigationView_main.getHeaderView(0).findViewById(R.id.user_id);
        //navigationView_main.setItemIconTintList(getResources().getColor(R.color.white));
        setupDrawerContent(navigationView_main);

        fm = getSupportFragmentManager();

        tab_menu = (RadioGroup) findViewById(R.id.tab_menu);
        tab_menu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.find:
                        title_main.setText(R.string.classify);
                        showFragment("one");
                        break;
                    case R.id.choiceness:
                        title_main.setText(R.string.recommend);
                        showFragment("two");
                        break;
                    case R.id.mine:
                        title_main.setText(R.string.starinfo);
                        showFragment("three");
                        break;
                }
            }
        });
        showFragment("two");
        App.setFristStart(true);
        update();
        getAdverImage();
    }

    @Override
    public void initListeners() {
        search_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });
        navi_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //没登录,去登录
//                if(TextUtils.isEmpty(SPCache.getInstance(MainActivity.this).getToken())){
//                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
//                    return;
//                }
//                startActivity(new Intent(MainActivity.this, UserInfoActivity.class));
                drawerLayout_main.closeDrawer(Gravity.LEFT);
            }
        });

        logo_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!drawerLayout_main.isDrawerVisible(Gravity.LEFT)){
                    drawerLayout_main.openDrawer(Gravity.LEFT);
                }
                //drawerLayout_main.isDrawerOpen(Gravity.LEFT);
            }
        });
    }

    @Override
    public void initData() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(MainActivity.this,mPermissionList, 100);
            }
        }
        if(!TextUtils.isEmpty(SPCache.getInstance(MainActivity.this).getToken())){
            getUserInfo();
        }
        mPushAgent = PushAgent.getInstance(this);

        if (((App)this.getApplication()).isPush_status()) {
            //开启推送
            mPushAgent.enable(new IUmengCallback() {
                @Override
                public void onSuccess() {
                    MessageSharedPrefs.getInstance(MainActivity.this).setIsEnabled(true);
                    ((App) MainActivity.this.getApplication()).setPush_status(true);
                    //设置友盟推送的别名
                    String uid = SPCache.getInstance(MainActivity.this).getUid();
                    if( uid!= null && uid.length() != 0){
                        mPushAgent.addAlias(uid+"@xingfabu.cn", "xingfabu", new UTrack.ICallBack() {
                            @Override
                            public void onMessage(boolean isSuccess, String message) {
                                String s = message;
                            }
                        });
                    }
                }
                @Override
                public void onFailure(String s, String s1) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        MenuItem menuItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
//        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem item) {
//                Toast.makeText(MainActivity.this, "onExpand", Toast.LENGTH_LONG).show();
//                return true;
//            }
//
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem item) {
//                Toast.makeText(MainActivity.this, "Collapse", Toast.LENGTH_LONG).show();
//                return true;
//            }
//        });
        return super.onCreateOptionsMenu(menu);
    }

    private void showFragment(String tag){
        Fragment fragment1 = fm.findFragmentByTag("one");
        Fragment fragment2 = fm.findFragmentByTag("two");
        Fragment fragment3 = fm.findFragmentByTag("three");
        FragmentTransaction ft = fm.beginTransaction();

        //隐藏所有已经创建的Fragment
        if(fragment1!=null){
            ft.hide(fragment1);
        }
        if(fragment2!=null){
            ft.hide(fragment2);
        }
        if(fragment3!=null){
            ft.hide(fragment3);
        }

        //从 Fragment管理中去查找Fragment
        Fragment tagFragment = fm.findFragmentByTag(tag);
        if(tagFragment==null){
            switch (tag) {
                case "one":
                    tagFragment= new ClassifyFragment();
                    ft.add(R.id.main_content, tagFragment, "one");
                    break;
                case "two":
                    tagFragment= new DirectFragment();
                    ft.add(R.id.main_content, tagFragment, "two");
                    break;
                case "three":
                    tagFragment= new StarInfoFragment();
                    ft.add(R.id.main_content, tagFragment, "three");
                    break;
                default:
                    break;
            }
        }
        //显示当前要显示的Fragment
        ft.show(tagFragment);
        ft.commitAllowingStateLoss();
    }
    private void getAdverImage() {
        String token = StarReleaseUtil.getToken(this);
        String sig = MD5Helper.GetMD5Code("Authorization=" + token + UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.LAUNCHER_AD)
                .addParams("Authorization",token)
                .addParams("sig", sig)
                .build()
                .execute(new MyStringCallback(){
                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        AdverImage resp = AdverImage.fromJson(response, AdverImage.class);
                        if(resp.retCode != 0 || resp.result == null || resp.result.path.length() == 0){
                            return;
                        }
                        imageCache.add(resp);
                        HttpImageTime = Integer.parseInt(resp.result.end_time);
                        ad_pic_url = resp.result.path;
                        loadImageCache(HttpImageTime,ad_pic_url);
                    }
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }
                });
    }
    private void update(){
        String token = StarReleaseUtil.getToken(this);
        String sig = MD5Helper.GetMD5Code("Authorization=" + token + UrlConstants.sign);

        OkHttpUtils.post()
                .url(UrlConstants.VersionUpdate)
                .addParams("Authorization",token)
                .addParams("sig", sig)
                .build()
                .execute(new MyStringCallback(){
                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        VersionUpdate resp = VersionUpdate.fromJson(response, VersionUpdate.class);
                        if (resp.retCode != 0) {
                            if(resp.retCode == 1000)
                                showToast("请重新登录");
                            SPCache.getInstance(MainActivity.this).saveToken("");
                            SPCache.getInstance(MainActivity.this).saveTouristToken("");
                            SPCache.getInstance(MainActivity.this).saveRongToken("");
                            SPCache.getInstance(MainActivity.this).saveUid("");
                            SPCache.getInstance(MainActivity.this).savePic("");
                            SPCache.getInstance(MainActivity.this).saveName("");
                            SPCache.getInstance(MainActivity.this).saveWiFiFlag(1);
//                            startActivity(new Intent(MainActivity.this,LoginActivity.class));
//                            ActivityCollector.finishAll();
                            return;
                        }else {
                            float versionOnline = Float.parseFloat(resp.result.version);

                            //int lastFlag = getVersionName(MainActivity.this).lastIndexOf(".");
                            float versionCurr = Float.parseFloat(getVersionName(MainActivity.this).substring(0,3));
                            if(versionOnline > versionCurr){
                                //showDialog(resp.result.change_log,resp.result.download_url,resp.result.force_update);
                                dialogShow(resp.result.change_log,resp.result.download_url,resp.result.force_update);
                            }
                        }
                    }
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }
                });
    }

    public void showDialog(String msg, final String url, String force){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("版本本更新");
        dialog.setMessage(msg);
        dialog.setCancelable(false);
        if("0".equals(force)){
            dialog.setNegativeButton("取消", new DialogInterface.
                    OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
        dialog.setPositiveButton("更新", new DialogInterface.
                OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                startActivity(intent);
            }
        });
        dialog.create();
        dialog.show();
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }
    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }
    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this);
        if(!TextUtils.isEmpty(SPCache.getInstance(MainActivity.this).getToken())){
         getUserInfo();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }
    private void dialogShow(String msg, final String url, String force){
        final UpDateDialog dialog = new UpDateDialog(this,
                R.layout.dialog_layoout, R.style.Theme_dialog);
        // shi点击
        dialog.findViewById(R.id.cancel).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(url == null){
                            return;
                        }
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(url);
                        intent.setData(content_url);
                        startActivity(intent);
                    }
                });
        if("1".equals(force)){
            dialog.findViewById(R.id.v_line).setVisibility(View.GONE);
            dialog.findViewById(R.id.determine).setVisibility(View.GONE);
        }
        // fou点击
        dialog.findViewById(R.id.determine).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

        TextView conctent = (TextView) dialog.findViewById(R.id.tv_content);
        conctent.setText(msg);

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RongIMClient.getInstance().logout();//注销登录不在接受Push消息,节省流量
    }
    private void loadImageCache(int time,String path) {
        /**
         * 1.第一次必然需要缓存。
         * 2.第二次的时候就需要对比后端更新的时间.
         */
//        int cacheTime = Integer.parseInt(App.getAdvertisementTimeCache());
//        if (time > cacheTime) {
//            //如果新的时间更新时间大于缓存，开始更新
//            for (AdverImage bean : imageCache) {new ShareTask(MainActivity.this).execute(bean.result.path);
//            }
//        }
        String cachePath = App.getAdvertisementUrlCache();
        if(path != null && !path.equals(cachePath) ){
            new ShareTask(MainActivity.this).execute(path);
        }
    }

    //  广告缓存
    class ShareTask extends AsyncTask<String, Void, File> {
        private final Context context;

        public ShareTask(Context context) {
            this.context = context;
        }

        @Override
        protected File doInBackground(String... params) {
            String url = params[0]; // should be easy to extend to share multiple images at once
            try {
                //  这个作用 就是下载网络原图大小的尺寸。
                return Glide.with(context)
                        .load(url)
                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get(); // needs to be called on background thread;
            } catch (Exception ex) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(File result) {
            currentTarget++;
            //这样判断方式没有缓存完毕。
            if (currentTarget == HttpImageCount) {
                App.setAdvertisementJsonCache(imageCache);
                App.setAdvertisementTimeCache(HttpImageTime + "");
                App.setAdvertisementUrlCache(ad_pic_url);
                App.setAdvertisementCache(true);
                String click_path = imageCache.get(0).result.url;
                if(!TextUtils.isEmpty(click_path)){
                    SPCache.getInstance(MainActivity.this).putAndApply("adver_url",click_path);
                }
                //adid
                String adid = imageCache.get(0).result.adId;
                SPCache.getInstance(MainActivity.this).putAndApply("adver_adid",adid);
                //展示时常
                SPCache.getInstance(MainActivity.this).putAndApply("adver_duration",imageCache.get(0).result.duration);
            }
        }
    }


    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.user_message:
                        if(TextUtils.isEmpty(SPCache.getInstance(MainActivity.this).getToken())){
//                            startActivity(new Intent(MainActivity.this,LoginActivity.class));
                            break;
                        }
                        startActivity(new Intent(MainActivity.this,MyMessageActivity.class));
                        break;
                    case R.id.user_collection:
                        if(TextUtils.isEmpty(SPCache.getInstance(MainActivity.this).getToken())){
//                            startActivity(new Intent(MainActivity.this,LoginActivity.class));
                            break;
                        }
                        startActivity(new Intent(MainActivity.this,CollectionActivity.class));
                        break;
                    case R.id.user_play_record:
                        if(TextUtils.isEmpty(SPCache.getInstance(MainActivity.this).getToken())){
//                            startActivity(new Intent(MainActivity.this,LoginActivity.class));
                            break;
                        }
                       startActivity(new Intent(MainActivity.this,PlayRecordActivity.class));
                        break;
                    case R.id.user_task:
                        showToast("敬请期待");
                        break;
                    case R.id.point_shop:
//                        if(TextUtils.isEmpty(SPCache.getInstance(MainActivity.this).getToken())){
//                            startActivity(new Intent(MainActivity.this,LoginActivity.class));
//                            break;
//                        }
//                        getDuiBaUrl();
                        showToast("敬请期待");
                        break;
                    case R.id.user_setting:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;
                }
                item.setChecked(false);
                drawerLayout_main.closeDrawer(Gravity.LEFT);
                return true;
            }
        });
    }

    private void getUserInfo(){
        String token = SPCache.getInstance(this).getToken();
        String sig = MD5Helper.GetMD5Code("Authorization=" + token + UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.GET_USER_INFO)
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
                        User user = User.fromJson(response,User.class);
                        if(user.retCode == 0){
                            if(!isFinishing()){
                                Glide.with(MainActivity.this)
                                        .load(user.result.pic)
                                        .crossFade()
                                        .transform(new GlideCircleTransform(navi_head.getContext()))
                                        .placeholder(R.drawable.icon_mo)
                                        .into(navi_head);
                            }
                            user_id.setText(user.result.name);
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Drawable d = BlurImageview.BlurImages(drawableToBitmap(iv_head.getDrawable()),getContext());
//                                    ll_userinfo.setBackgroundDrawable(d);
//                                }
//                            },2000);
                        }
                    }
                });
    }

    //Bitmap b = BitmapFactory.decodeResource(getResources(),R.drawable.mine_back);
    //blur(b,ll_userinfo,25);
    private void blur(Bitmap bkg, View view, float radius) {
        Bitmap overlay = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.drawBitmap(bkg, -view.getLeft(), -view.getTop(), null);
        RenderScript rs = RenderScript.create(this);
        Allocation overlayAlloc = Allocation.createFromBitmap(rs, overlay);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, overlayAlloc.getElement());
        blur.setInput(overlayAlloc);
        blur.setRadius(radius);
        blur.forEach(overlayAlloc);
        overlayAlloc.copyTo(overlay);
        view.setBackground(new BitmapDrawable(getResources(), overlay));
        rs.destroy();
    }
    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }
    //积分商城
    private void getDuiBaUrl(){
        String token = SPCache.getInstance(this).getToken();
        String sig = MD5Helper.GetMD5Code("Authorization=" + token + UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.JIFEN_SHOP)
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
                        DuiBaResponse duiba = DuiBaResponse.fromJson(response,DuiBaResponse.class);
                        if(duiba.retCode == 0){
                            Intent intent = new Intent(MainActivity.this,WebViewActivity.class);
                            intent.putExtra("weburl", duiba.result);
                            startActivity(intent);
                        }
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
