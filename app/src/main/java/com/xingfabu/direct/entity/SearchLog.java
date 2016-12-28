package com.xingfabu.direct.entity;

import java.util.List;

/**
 * Created by guoping on 16/6/29.
 */
public class SearchLog extends BaseResponse{
    public Data result;
    public class Data{
        public List<String> logs;
        public List<String> hot;
    }
}