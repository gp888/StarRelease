package com.xingfabu.direct.entity;

/**
 * Created by 郭平 on 2016/3/31 0031.
 */
public class LoginResponse extends BaseResponse {
    public Data result;
    public class Data{
        public String uid;
        public String token;
        public boolean is_binding;
        public String tourist;//游客信息
    }
}
