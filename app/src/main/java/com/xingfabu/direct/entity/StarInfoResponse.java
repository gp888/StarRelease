package com.xingfabu.direct.entity;

import java.util.List;

/**
 * Created by guoping on 2016/11/21.
 */

public class StarInfoResponse extends BaseResponse{
    public Data result;
    public class Data{
        public List<MySectionItem> hotRows;
        public List<MySectionItem> latelyRows;
        public List<MySectionItem> allRows;
    }
}
