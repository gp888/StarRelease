package com.xingfabu.direct.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.xingfabu.direct.R;
import com.xingfabu.direct.adapter.ConversationAdapter;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.cache.SPCache;
import com.xingfabu.direct.emoji.SmileLayout;
import com.xingfabu.direct.entity.Barrage;
import com.xingfabu.direct.entity.BarrageList;
import com.xingfabu.direct.entity.User;
import com.xingfabu.direct.entity.BaseResponse;
import com.xingfabu.direct.ui.LoginActivity;
import com.xingfabu.direct.ui.WatchActivity;
import com.xingfabu.direct.utils.MD5Helper;
import com.xingfabu.direct.utils.MyStringCallback;
import com.xingfabu.direct.utils.StarReleaseUtil;
import com.xingfabu.direct.widget.DividerItemDecoration;
import com.zhy.http.okhttp.OkHttpUtils;
import java.util.ArrayList;
import java.util.List;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;
import okhttp3.Call;

public class TalkFragment extends Fragment implements View.OnClickListener{
        private RecyclerView conversationlist;
        private ConversationAdapter adapter;
        private List<Barrage> speak = new ArrayList<>();;
        private String innar_id = null;
        private EditText et_content;
        private TextView tv_send;
        private String currentTime = null;
        private String portrait = null;
        private String nickName = null;
        private ImageView emoji;
        private SmileLayout smileLayout;
        private WatchActivity watchActivity;

    public static TalkFragment newInstance(String webinar_id) {
        TalkFragment tf = new TalkFragment();
        Bundle b = new Bundle();
        b.putString("talk_inarid",webinar_id);
        tf.setArguments(b);
        return tf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        innar_id = getArguments().getString("talk_inarid");
    }

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_talk,container,false);
            return v;
        }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        watchActivity = (WatchActivity) getActivity();
    }

    private void initView(View v) {
        et_content = (EditText) v.findViewById(R.id.et_content);
        tv_send = (TextView) v.findViewById(R.id.tv_send);
        emoji = (ImageView) v.findViewById(R.id.appimage_smile);
        tv_send.setOnClickListener(this);
        emoji.setOnClickListener(this);
        et_content.setOnClickListener(this);

        smileLayout = (SmileLayout) v.findViewById(R.id.write_smile_panel);
        //初始化
        smileLayout.init(et_content);

        et_content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    smileLayout.setVisibility(View.GONE);
                }
            }
        });
        conversationlist = (RecyclerView) v.findViewById(R.id.conversation_list);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,true);
        conversationlist.setLayoutManager(mLayoutManager);
        conversationlist.setHasFixedSize(true);
        conversationlist.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

        getBarrage(innar_id);//获取评论,传过来的
        adapter = new ConversationAdapter(speak,getContext());
        conversationlist.setAdapter(adapter);

        adapter.setOnItemClickListener(new ConversationAdapter.OnRecyclerViewItemClick(){
            @Override
            public void onItemClick(View view, Barrage data) {
                String content =  "@" + data.name + ":";
                et_content.setText(content);
                smileLayout.setVisibility(View.GONE);//隐藏表情
                StarReleaseUtil.showSoftKeyboard(et_content);
                et_content.setSelection(content.length());
            }
        });
        joinChatRoom(innar_id);
    }

    public void joinChatRoom(String innar_id){
        RongIMClient.getInstance().joinChatRoom(innar_id,  -1,  new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                Log.e("进入聊天室","success");
                getOtherInfo(SPCache.getInstance(getContext()).getRongUid());
                RongIMClient.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
                    @Override
                    public boolean onReceived(Message message, int i) {
                        getOtherInfo(message);
                        return true;
                    }
                });
            }
            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    public void getBarrage(String weihouId) {
        innar_id = weihouId;//点相关，更新innnar_id
        String token = StarReleaseUtil.getToken(getContext());
        String sig = MD5Helper.GetMD5Code("webinar_id=" + weihouId + "&Authorization="+token+ UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.GETBARRAGE)
                .addParams("webinar_id", weihouId)
                .addParams("Authorization",token)
                //.addParams("sig",sig)
                .build()
                .execute(new MyStringCallback(){
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        BarrageList resp = BarrageList.fromJson(response, BarrageList.class);
                        List<Barrage>  remp =  resp.result;
                        //Collections.reverse(remp);
                        speak.clear();
                        speak.addAll(remp);
                        adapter.notifyDataSetChanged();
                        //conversationlist.smoothScrollToPosition(speak.size());
                    }
                });
    }

    public void setBarrage(List<Barrage> dataList){
        speak.clear();
        speak.addAll(dataList);
        adapter.notifyDataSetChanged();
    }

    public void setData(Bundle b){
        if(b.getString("webinar_id") != null){
//            innar_id = b.getString("webinar_id");
        }else if(b.getString("videoCurrentTime") != null){
            currentTime = b.getString("videoCurrentTime");
        }else if(b.getString("direct") != null){
            currentTime = b.getString("direct");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        quitChatRoom(innar_id);
    }

    public void quitChatRoom(String innar_id){
        RongIMClient.getInstance().quitChatRoom(innar_id, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                Log.e("退出聊天室","success");
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
            }
        });
    }

    public void sendMessage(final MessageContent msg) {
            RongIMClient.getInstance().sendMessage(Conversation.ConversationType.CHATROOM, innar_id, msg, null, null, new RongIMClient.SendMessageCallback() {
                @Override
                public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                    Log.d("sendMessage", "发送消息失败ErrorCode----" + errorCode.getValue());
                }
                @Override
                public void onSuccess(Integer integer) {
                    if (msg instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) msg;
                        Log.d("sendMessage", "TextMessage发送了一条【文字消息】-----" + textMessage.getContent());
                        String e = textMessage.getExtra();
                        if("打赏".equals(textMessage.getExtra().substring(0,2))){
                            saveBarrage(textMessage.getContent()+"打赏了一个[红包]");
                        }else if(!"进入房间".equals(textMessage.getExtra()) && !"心".equals(textMessage.getExtra().substring(0,1))){
                            saveBarrage(textMessage.getContent());//保存弹幕
                        }
                    }
                }
            });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            //发消息:发弹幕,发消息,保存弹幕,添加评论
            case R.id.tv_send:
                if(TextUtils.isEmpty(SPCache.getInstance(getContext()).getToken())){
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    getActivity().finish();
                    return;
                }
                String sentence = et_content.getText().toString().trim();
                if(TextUtils.isEmpty(sentence)){
                    Toast.makeText(getContext(),"消息不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }else if(sentence.length() > 40){
                    Toast.makeText(getContext(),"字数超出限制",Toast.LENGTH_SHORT).show();
                    return;
                }

                et_content.setText("");
                StarReleaseUtil.closeImm(getActivity());
                smileLayout.setVisibility(View.GONE);
                if(!watchActivity.isFinishing()){
                    watchActivity.addDanmaKuTextAndImage(false,sentence,true);//发弹幕
                }

                TextMessage textMessage = TextMessage.obtain(sentence );//+ System.currentTimeMillis()
                textMessage.setExtra("消息");
                sendMessage(textMessage);
                break;
            case R.id.appimage_smile:
                StarReleaseUtil.hideKeyboard(et_content);
                if (smileLayout.getVisibility() == View.GONE) {
                    smileLayout.setVisibility(View.VISIBLE);
                }
                else {
                    smileLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.et_content:
                smileLayout.setVisibility(View.GONE);
                StarReleaseUtil.showSoftKeyboard(et_content);
                break;
            default:
                break;
        }
    }

    public void saveBarrage(final String content){
        String token = SPCache.getInstance(getContext()).getToken();
        if(!watchActivity.isFinishing()){
            watchActivity.setCurrentValue();
        }
        if(currentTime == null){
            return;
        }
        String sig = MD5Helper.GetMD5Code("webinar_id=" + innar_id + "&video_time=" + currentTime +"&content=" + content + "&Authorization="+token+ UrlConstants.sign);

        OkHttpUtils.post()
                .url(UrlConstants.SAVEBARRAGE)
                .addParams("webinar_id", innar_id)
                .addParams("video_time",currentTime)
                .addParams("content",content)
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
                        BaseResponse resp = BaseResponse.fromJson(response, BaseResponse.class);
                        if(resp.retCode == 0){
                            et_content.setText("");
                            addComment(portrait, nickName,content);
                        }
                    }
                });
    }

    /**
     * 更新聊天室列表
     * @param pic
     * @param name
     * @param comment 评论
     */
    public void addComment(String pic,String name,String comment){
        Barrage barrage = new Barrage();
        barrage.name = name;
        barrage.pic = pic;
        barrage.content = comment;
        speak.add(0,barrage);
        adapter.notifyDataSetChanged();
        //conversationlist.smoothScrollToPosition(speak.size());
        //Toast.makeText(getContext(),"评论成功",Toast.LENGTH_SHORT).show();
    }
    private void getOtherInfo(final Message message){
        String token = StarReleaseUtil.getToken(getContext());
        //String sig = MD5Helper.GetMD5Code("uid=" + message.getSenderUserId() + "&Authorization="+token+ UrlConstants.sign);

        OkHttpUtils.post()
                .url(UrlConstants.GET_OTHERINFO)
                .addParams("uid",message.getSenderUserId())
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
                            if (message.getContent() instanceof TextMessage) {
                                TextMessage textMessage = (TextMessage) message.getContent();
                                if("进入房间".equals(textMessage.getExtra())){
                                    addComment(user.result.pic,user.result.name,user.result.name + "进入房间");//只进入房间
                                }else if("消息".equals(textMessage.getExtra())){
                                    addComment(user.result.pic,user.result.name,textMessage.getContent());//只添加评论
                                    if(!watchActivity.isFinishing()){
                                        watchActivity.addDanmaKuTextAndImage(false,textMessage.getContent(),false);//和发弹幕
                                    }

                                }else if(textMessage.getContent().equals(user.result.name)){
                                    //点赞和打赏  心|2   打赏|说的内容 在横屏上体现
                                    //addComment(user.result.pic,user.result.name,textMessage.getExtra());

                                    if("心".equals(textMessage.getExtra().substring(0,1))){
                                       if(!watchActivity.isFinishing()){
                                           watchActivity.addHeart(Integer.parseInt(textMessage.getExtra().substring(2)));
                                       }
                                    }
                                }
                            }
//                            else if(message.getContent() instanceof RichContentMessage){
//                                RichContentMessage textMessage = (RichContentMessage) message.getContent();
//                                addComment(user.result.pic,user.result.name,textMessage.getContent());
//                            }

                        }
                    }
                });
    }

    private void getOtherInfo(String uid){
        String token = StarReleaseUtil.getToken(getContext());
        //String sig = MD5Helper.GetMD5Code("uid=" + message.getSenderUserId() + "&Authorization="+token+ UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.GET_OTHERINFO)
                .addParams("uid",uid)
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
                            portrait = user.result.pic;
                            nickName = user.result.name;

                            if(!watchActivity.isFinishing()){
                                watchActivity.setNickName(nickName);
                            }

                            //进入聊天室后发个消息
                            TextMessage textMessage = TextMessage.obtain(nickName);//+ System.currentTimeMillis()
                            textMessage.setContent(nickName);
                            textMessage.setExtra("进入房间");
                            sendMessage(textMessage);
                        }
                    }
                });
    }
}
