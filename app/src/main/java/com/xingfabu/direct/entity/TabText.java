package com.xingfabu.direct.entity;

import java.util.List;

/**
 * Created by guoping on 16/6/14.
 */
public class TabText extends BaseResponse{
    public List<TabClassify> result;

    public class TabClassify{
        public String id;
        public String name;
    }
}
