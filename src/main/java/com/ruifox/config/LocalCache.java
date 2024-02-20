package com.ruifox.config;

import com.alibaba.fastjson.JSONObject;

/**
 * 对象暂存点
 */
public enum LocalCache {
    INSTANCE;
    private final JSONObject cache;
    LocalCache(){
        cache=new JSONObject();
    }
    public JSONObject getInstance(){
        return cache;
    }
}
