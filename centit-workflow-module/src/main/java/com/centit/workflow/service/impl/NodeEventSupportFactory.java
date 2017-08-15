package com.centit.workflow.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.support.file.FileIOOpt;

import java.io.IOException;

/**
 * 根据 OSID
 * @author codefan
 * @create 2013-7-10
 * @version
 */
public class NodeEventSupportFactory {
    public static NodeEventExecutor getNodeEventSupportBean(){
        //读取配置文件
        String jsonFile = Thread.currentThread().getContextClassLoader().getResource("").getPath()+"ip_environmen.json";
        JSONObject json = null;
        try {
            String jsonStr = FileIOOpt.readStringFromFile(jsonFile,"utf-8");
            json = JSON.parseObject(jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(json == null || json.get("osInfos") == null){
            return null;
        }
        return null;
    }
}
