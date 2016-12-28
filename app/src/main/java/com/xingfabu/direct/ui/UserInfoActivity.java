package com.xingfabu.direct.ui;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bumptech.glide.Glide;
import com.xingfabu.direct.R;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.cache.SPCache;
import com.xingfabu.direct.entity.User;
import com.xingfabu.direct.entity.BaseResponse;
import com.xingfabu.direct.transform.GlideCircleTransform;
import com.xingfabu.direct.utils.MD5Helper;
import com.xingfabu.direct.utils.MyStringCallback;
import com.xingfabu.direct.utils.PhotoUtil;
import com.xingfabu.direct.utils.StarReleaseUtil;
import com.xingfabu.direct.widget.Loading;
import com.zhy.http.okhttp.OkHttpUtils;
import java.io.File;
import okhttp3.Call;
/**
 * Created by guoping on 16/4/14.
 */
public class UserInfoActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener{
    ImageView iv_head;
    private PopupWindow mSetPhotoPop;
    private File mCurrentPhotoFile;
    private CoordinatorLayout mMainView;
    private static final int PHOTO_PICKED_WITH_DATA = 1881;
    private static final int CAMERA_WITH_DATA = 1882;
    private static final int CAMERA_CROP_RESULT = 1883;
    private static final int PHOTO_CROP_RESOULT = 1884;
    private static final int ICON_SIZE = 150;
    private Bitmap imageBitmap;
    private TextView tv_save;
    private TextView tv_gender,tv_birthday;
    private EditText et_sign,et_idol,et_nickname;
    int sexflag = 0;//男 0 = 女(上传)
    private String temp = "";

    //****************阿里云上传头像有关***************************************************************
    private static final String accessKeyId = "5MAjow2Eypaj1dOJ";
    private static final String accessKeySecret = "FHA3eoQd18YZRdqqR1VJrEyL02uJW7";
    String OSS_ENDPOINT = "http://oss-cn-beijing.aliyuncs.com";
    private static final String bucketName = "tinydown1";
    //**********************************************************************************************

    @Override
    public void setContentView() {
        setContentView(R.layout.layout_userinfo);
    }

    @Override
    public void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_head = (ImageView) findViewById(R.id.iv_head);
        mMainView = (CoordinatorLayout) findViewById(R.id.main_layout);
        tv_save = (TextView) findViewById(R.id.tv_save);
        tv_gender = (TextView) findViewById(R.id.tv_gender);

        et_nickname = (EditText) findViewById(R.id.et_nickname);
        et_idol = (EditText) findViewById(R.id.et_idol);
        et_sign = (EditText) findViewById(R.id.et_sign);
        tv_birthday = (TextView) findViewById(R.id.tv_birthday);
    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {
        getUserInfo();
    }
    public void onClick(View v){
        switch(v.getId()){
            case R.id.ll_pic:
                StarReleaseUtil.closeImm(this);
                showPop();
                break;
            case R.id.tv_save:
                changeUserInfo();
                break;
            case R.id.tv_gender:
                StarReleaseUtil.closeImm(this);
                showGender();
                break;
            case R.id.et_nickname:

                break;
            case R.id.et_idol:
                showToast("爱豆稍后就来");
                break;
            case R.id.et_sign:

                break;
            case R.id.tv_birthday:
                DatePickerDialog datePicker = new DatePickerDialog(
                        this, R.style.DatePicker,this, 1993, 0, 1);
                datePicker.show();
                break;
        }
    }

    public void checkPermission(String permission){
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(this, "需要开启相机权限", Toast.LENGTH_SHORT).show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{permission}, 119);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else {
            doTakePhoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 119)
        {
            if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                doTakePhoto();
            } else
            {
                // Permission Denied
                Toast.makeText(this, "请在设置中开启本程序的相机权限", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void showDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder
                (this);
        dialog.setTitle("设置头像");
        dialog.setMessage("Something important.");
        dialog.setCancelable(false);
        dialog.setPositiveButton("用图库中的图片", new DialogInterface.
                OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // choosePho();
            }
        });
        dialog.setNegativeButton("拍照", new DialogInterface.
                OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkPermission(Manifest.permission.CAMERA);
            }
        });
        dialog.show();
    }
    public void showPop(){
        View mainView = LayoutInflater.from(this).inflate(R.layout.alert_setphoto_menu_layout, null);
        TextView btnTakePhoto = (TextView) mainView.findViewById(R.id.btn_take_photo);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSetPhotoPop.dismiss();
                // 拍照获取
                checkPermission(Manifest.permission.CAMERA);
            }
        });
        TextView btnCheckFromGallery = (TextView) mainView.findViewById(R.id.btn_check_from_gallery);
        btnCheckFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSetPhotoPop.dismiss();
                // 相册获取
                doPickPhotoFromGallery();
            }
        });
        TextView btnCancle = (TextView) mainView.findViewById(R.id.btn_cancel);
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSetPhotoPop.dismiss();
            }
        });
        mSetPhotoPop = new PopupWindow(this);
        mSetPhotoPop.setBackgroundDrawable(new BitmapDrawable());
        mSetPhotoPop.setFocusable(true);
        mSetPhotoPop.setTouchable(true);
        mSetPhotoPop.setOutsideTouchable(true);
        mSetPhotoPop.setContentView(mainView);
        mSetPhotoPop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mSetPhotoPop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mSetPhotoPop.setAnimationStyle(R.style.bottomStyle);
        mSetPhotoPop.showAtLocation(mMainView, Gravity.BOTTOM, 0, 0);
        mSetPhotoPop.update();
    }
    public void showGender(){
        View mainView = LayoutInflater.from(this).inflate(R.layout.gender_layout, null);
        TextView btnTakePhoto = (TextView) mainView.findViewById(R.id.btn_nan);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSetPhotoPop.dismiss();
                // 男
                tv_gender.setText("男");
                sexflag = 1;
            }
        });
        TextView btnCheckFromGallery = (TextView) mainView.findViewById(R.id.btn_nv);
        btnCheckFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSetPhotoPop.dismiss();
                // 女
                tv_gender.setText("女");
                sexflag = 2;
            }
        });
        TextView btnCancle = (TextView) mainView.findViewById(R.id.btn_cancel);
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSetPhotoPop.dismiss();
            }
        });
        mSetPhotoPop = new PopupWindow(this);
        mSetPhotoPop.setBackgroundDrawable(new BitmapDrawable());
        mSetPhotoPop.setFocusable(true);
        mSetPhotoPop.setTouchable(true);
        mSetPhotoPop.setOutsideTouchable(true);
        mSetPhotoPop.setContentView(mainView);
        mSetPhotoPop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mSetPhotoPop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mSetPhotoPop.setAnimationStyle(R.style.bottomStyle);
        mSetPhotoPop.showAtLocation(mMainView, Gravity.BOTTOM, 0, 0);
        mSetPhotoPop.update();
    }
    /**
     * 调用系统相机拍照
     */
    protected void doTakePhoto() {
        try {
            // Launch camera to take photo for selected contact
            File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/Photo");
            if (!file.exists()) {
                file.mkdirs();
            }
            mCurrentPhotoFile = new File(file, PhotoUtil.getRandomFileName());
            final Intent intent = getTakePickIntent(mCurrentPhotoFile);
            startActivityForResult(intent, CAMERA_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "手机中无可用图片", Toast.LENGTH_LONG).show();
        }
    }
    /**
     * Constructs an intent for capturing a photo and storing it in a temporary
     * file.
     */
    public static Intent getTakePickIntent(File f) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        return intent;
    }
    /**
     * 从相册选择图片
     */
    protected void doPickPhotoFromGallery() {
        try {
            // Launch picker to choose photo for selected contact
            final Intent intent = getPhotoPickIntent();
            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "手机中无可用图片", Toast.LENGTH_LONG).show();
        }
    }
    /**
     * 获取调用相册的Intent
     */
    public static Intent getPhotoPickIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        return intent;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //ByteArrayOutputStream stream = new ByteArrayOutputStream();
            switch (requestCode) {
                case PHOTO_PICKED_WITH_DATA:
                    // 相册选择图片后裁剪图片
                    startPhotoZoom(data.getData());
                    break;
                case PHOTO_CROP_RESOULT:
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        imageBitmap = extras.getParcelable("data");
                        //imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        iv_head.setImageBitmap(imageBitmap);

                        byte [] b = PhotoUtil.Bitmap2Bytes(imageBitmap);
                        upload(b);
                    }
                    break;
                case CAMERA_WITH_DATA:
                    // 相机拍照后裁剪图片
                    doCropPhoto(mCurrentPhotoFile);
                    break;
                case CAMERA_CROP_RESULT:
                    imageBitmap = data.getParcelableExtra("data");
                    //imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    iv_head.setImageBitmap(imageBitmap);
                    byte [] b = PhotoUtil.Bitmap2Bytes(imageBitmap);
                    upload(b);
                    break;
            }
        }
    }
    /**
     * 相册裁剪图片
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");//调用Android系统自带的一个图片剪裁页面,
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");//进行修剪
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", ICON_SIZE);
        intent.putExtra("outputY", ICON_SIZE);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_CROP_RESOULT);
    }
    /**
     * 相机剪切图片
     */
    protected void doCropPhoto(File f) {
        try {
            // Add the image to the media store
            MediaScannerConnection.scanFile(this, new String[]{f.getAbsolutePath()}, new String[]{null}, null);

            // Launch gallery to crop the photo
            final Intent intent = getCropImageIntent(Uri.fromFile(f));
            startActivityForResult(intent, CAMERA_CROP_RESULT);
        } catch (Exception e) {
            Toast.makeText(this, "手机中无可用图片", Toast.LENGTH_LONG).show();
        }
    }
    /**
     * 获取系统剪裁图片的Intent.
     */
    public static Intent getCropImageIntent(Uri photoUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", ICON_SIZE);
        intent.putExtra("outputY", ICON_SIZE);
        intent.putExtra("return-data", true);
        return intent;
    }

    /**
     * 上传图片
     * @param b
     */
    private void upload(byte [] b ){
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(accessKeyId,accessKeySecret);

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSS oss = new OSSClient(this,OSS_ENDPOINT,credentialProvider,conf);

        final String picName = PhotoUtil.getRandomFileName()+".png";

        PutObjectRequest put = new PutObjectRequest(bucketName,picName,b);
        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
//                String Etag = putObjectResult.getETag();
//                String Requestid = putObjectResult.getRequestId();
                //changePic(picName);
                temp = "http://tinydown1.oss-cn-beijing.aliyuncs.com/"+picName;
            }

            @Override
            public void onFailure(PutObjectRequest putObjectRequest, ClientException clientExcepion, ServiceException serviceException) {
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                   Toast.makeText(UserInfoActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
                }
                if (serviceException != null) {
                    // 服务异常
                    Toast.makeText(UserInfoActivity.this, "", Toast.LENGTH_SHORT).show();
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        task.waitUntilFinished();
    }

    private void changeUserInfo(){
        final String nickName = et_nickname.getText().toString();
        String idol = et_idol.getText().toString();
        String sign = et_sign.getText().toString();
        String birthday = tv_birthday.getText().toString();
        //String gender = tv_gender.getText().toString();
        String pic = null;
        String city_name = null;//现在没有

        if(TextUtils.isEmpty(nickName)){
            showToast("昵称不能为空");
            return;
        }
        String token = SPCache.getInstance(this).getToken();
        String sig = MD5Helper.GetMD5Code("Authorization=" + token + "&" + "name="+nickName+"&"+"sex="+ sexflag + "&" + "idol=" + idol+ "&" +"sign=" +sign+ "&pic=" +temp +  "&birthday=" + birthday + UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.SAVE_USER_INFO)
                .addParams("Authorization",token)
                .addParams("name", nickName)
                .addParams("sex", sexflag+"")
                .addParams("idol", idol)
                .addParams("sign",sign)
                .addParams("pic",temp)
                .addParams("birthday",birthday)
                .addParams("sig", sig)
                .build()
                .execute(new MyStringCallback(new Loading(this)){
                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        BaseResponse resp = BaseResponse.fromJson(response, BaseResponse.class);
                        if (resp.retCode != 0) {
                            showToast(resp.desc);
                            return;
                        }
                        showToast("更新成功");
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
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
                .execute(new MyStringCallback(new Loading(this)){
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        User user = User.fromJson(response,User.class);
                        if(user.retCode == 0){
                            Glide.with(iv_head.getContext())
                                    .load(user.result.pic)
                                    .crossFade()
                                    .placeholder(R.drawable.icon_mo)
                                    .transform(new GlideCircleTransform(iv_head.getContext()))
                                    .into(iv_head);
                            et_nickname.setText(user.result.name);
                            //1=男 0=女(获取)
                            if("1".equals(user.result.sex)){
                                tv_gender.setText("男");
                                sexflag = 1;
                            }else if("2".equals(user.result.sex)){
                                tv_gender.setText("女");
                                sexflag = 2;
                            }else {
                                tv_gender.setText("点击选择");
                            }
                            //et_idol.setText(user.result.idol);//爱豆稍后
                            et_sign.setText(user.result.sign);
                            tv_birthday.setText(user.result.birthday);
                        }
                    }
                });
    }

    /**
     * 单独更新头像
     * @param pic
     */
    private void changePic(String pic){
        String name = et_nickname.getText().toString();

        String picture = "http://tinydown1.oss-cn-beijing.aliyuncs.com/"+"20160426153803113.png";
        String token = SPCache.getInstance(this).getToken();
        String sig = MD5Helper.GetMD5Code("Authorization=" + token + "&" + "name=" + name+ "&" +"sex="+ sexflag + "&" + "pic=" + picture + UrlConstants.sign);

        OkHttpUtils.post()
                .url(UrlConstants.SAVE_USER_INFO)
                .addParams("Authorization",token)
                .addParams("name", name)
                .addParams("sex", sexflag+"")
                .addParams("pic",picture)
                .addParams("sig", sig)
                .build()
                .execute(new MyStringCallback(new Loading(this)){
                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        BaseResponse resp = BaseResponse.fromJson(response, BaseResponse.class);
                        if (resp.retCode != 0) {
                            showToast(resp.desc);
                            return;
                        }
                        Toast.makeText(UserInfoActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }
                });
    }
    @Override
    public void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart(getString(R.string.changeSelfInfo));
//        MobclickAgent.onResume(this);
    }
    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd(getString(R.string.changeSelfInfo));
//        MobclickAgent.onPause(this);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
         String month = (monthOfYear + 1) + "";
        String day = dayOfMonth + "";
        if(monthOfYear + 1 < 10){
            month = "0"+ (monthOfYear + 1);
        }
        if(dayOfMonth < 10){
            day = "0"+ dayOfMonth;
        }
        tv_birthday.setText(year + "-" + month + "-" + day);
    }
}
