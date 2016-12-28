package com.xingfabu.direct.entity;

/**
 * Created by guoping on 16/8/9.
 */
public class AdverImage extends BaseResponse{
    public Data result;
    public class Data{
        public String adId;
        public String url;
        public String path;
        public String start_time;
        public String end_time;
        public String duration;
    }
}