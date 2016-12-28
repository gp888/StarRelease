package com.xingfabu.direct.entity;

import java.util.List;

/**
 * Created by guoping on 16/7/27.
 */
public class PayBean extends BaseResponse{
    public Data result;
    public class Data{
        public String orderNo;
        public String prepayId;
        public String packageValue;
        public String noncestr;
        public String timestamp;
        public String sign;
    }
}
