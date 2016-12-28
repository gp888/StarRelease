package com.xingfabu.direct.entity;

import com.chad.library.adapter.base.entity.SectionEntity;

/**
 * Created by guoping on 2016/11/8.
 */

public class MySection extends SectionEntity<MySectionItem>{
    public MySection(boolean isHeader, String header) {
        super(isHeader, header);
    }
    public MySection(MySectionItem item){
        super(item);
    }
}
