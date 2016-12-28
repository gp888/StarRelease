package com.xingfabu.direct.entity;

import java.util.List;
/**
 * Created by guoping on 2016/11/17.
 */

public class HotAdver extends BaseResponse{
    public Data result;

    public class Data{
        public List<ItemAdver> list;
    }
    public class ItemAdver{
        public String id;
        public String title;
        public String pic;
        public String url;
    }
}
