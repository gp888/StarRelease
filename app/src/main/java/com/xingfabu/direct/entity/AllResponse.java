package com.xingfabu.direct.entity;


/**
 * Created by guoping on 2016/10/12.
 */

public class AllResponse {
    public class AcSupport extends BaseResponse{
        public Data result;
        public class Data{
            public int surplus;
        }
    }

    public class DailyShare extends BaseResponse{
        public int result;
    }
    public class SimpleShare extends BaseResponse{
        public String  result;
    }
}
