package com.xingfabu.direct.entity;

/**
 * Created by guoping on 16/4/26.
 */
public class VersionUpdate extends BaseResponse{
    public Data result;
    public class Data{
        public String version;
        public String version_code;
        public String up_date;
        public String download_url;
        public String change_log;
        public String force_update;
    }
}
