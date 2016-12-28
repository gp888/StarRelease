package com.xingfabu.direct.entity;

/**
 * Created by guoping on 16/4/12.
 */
public class GetVhallToken extends BaseResponse {
    public Data result;
    public class Data{
       public String token;
        public String rongToken;
    }
}
