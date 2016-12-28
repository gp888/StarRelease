package com.xingfabu.direct.entity;

/**
 * Created by guoping on 16/6/27.
 */
public class HotBean {

    public String sid;
    public String subject;
    public String webinar_id;//微吼id
    public String pic;
    public String webinar_type;//1直播2预约3结束4录播
    public String support;
    public String host;

    public String position;
//    public String time;
//    public String start_time;//直播开始的时间 到秒
//    public String introduction;

    public boolean is_close;//霸屏榜活动 活动是否开始
    public int surplus;//霸屏榜活动剩余点赞数
    public String share_num;//分享次数
}
