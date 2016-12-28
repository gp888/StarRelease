package com.xingfabu.direct.entity;

import java.util.List;

/**
 * Created by guoping on 16/6/15.
 */
public class MessageResponse extends BaseResponse{
    public List<SystemMessage> result;

    public class SystemMessage{
        public String id;
        public String subject;
        public String content;
        public String time;
    }
}