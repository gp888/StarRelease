package com.xingfabu.direct.entity;

import java.util.List;

/**
 * Created by guoping on 16/4/6.
 */
public class DirectBroadcastResponse extends BaseResponse {
    public Data result;
    public class Data{
        public List<Video> list;
        public List<Advertisment> adver;
        public List<Video> top;
    }
}
