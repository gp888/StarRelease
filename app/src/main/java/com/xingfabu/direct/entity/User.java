package com.xingfabu.direct.entity;

import java.util.List;

/**
 * Created by guoping on 16/4/26.
 */
public class User extends BaseResponse{
    public Data result;
    public class Data{
       public String name;
        public String pic;
        public String sex;
        public String sign;
        public String city_name;
        public String virtual_coin;
        public String mobile;
        public String birthday;
        public List idol;
        public String show;
        public List signIn;
        public String signInExplain;
        public String datetime;//获取房间内别人说话的时候用
    }
}