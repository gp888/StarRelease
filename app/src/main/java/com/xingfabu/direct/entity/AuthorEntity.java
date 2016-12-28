package com.xingfabu.direct.entity;

import java.util.List;

/**
 * Created by guoping on 2016/11/23.
 */

public class AuthorEntity extends BaseResponse{
    public Data result;
    public class Data{
       public Author authors;
       public List<LateUpdateAuthor> rows;
    }
    public class Author{
        public String username;
        public String pic;
        public String describe;
    }
}
