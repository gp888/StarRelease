package com.xingfabu.direct.entity;

import java.util.List;

/**
 * Created by guoping on 16/4/13.
 */
public class VideoDetail extends BaseResponse {
    public Data result;
    public class Data{
        public String uid;
        public String name;
        public String pic;
        public String subject;
        public String time;//后台添加视频的时间
        public String city_name;
        public List<Star> star;
        public String start_time;//直播开始的时间,到秒
        public String introduction;
        public boolean is_follow;
        public boolean is_like;
        public String webinar_type;
        public int chat_num;
        public int chat_false_num;
        public List images;
        public String compere;
        public String ads_pic;
        public String ads_url;
    }
}
