package com.ibg.receipt.utils;

import com.alibaba.fastjson.JSONObject;
import com.ibg.receipt.base.constant.FeignActionType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogUtil {

    public  static  Long ping(FeignActionType type,String businessKey,Object vo){
        log.info("业务code:{},名称:{},businessKey:{}请求报文:{}",type.name(),type.toString(),businessKey, JSONObject.toJSONString(vo));
        //增加接口耗时
        return System.currentTimeMillis();
    }

    public  static  void pong(FeignActionType type,String businessKey,Object vo,Long startTime){
        log.info("业务code:{},名称:{},businessKey:{}响应报文:{},耗时:{}",type.name(),type.toString(),businessKey, JSONObject.toJSONString(vo),System.currentTimeMillis() - startTime);
    }
}
