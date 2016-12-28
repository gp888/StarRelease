package com.xingfabu.direct.entity;

import java.util.List;

/**
 * Created by guoping on 16/6/27.
 */
public class HotModel extends BaseResponse{
    public Data result;
    public class Data{
        public List<HotBean> items;
        public String activity_pic;
    }
}
