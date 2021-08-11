package com.example.easypoi.utils;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * openFeign远程调用时使用IPage接口进行返回分页数据失败,进行序列化
 * 使用json格式进行数据的传输，即限制web传输的数据格式为 content-type = application/json
 *
 * @param <T>
 */
public class SerializableForFastJsonPage<T> extends Page<T> {
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);// 实现json序列化
    }

}