package com.centit.workflow.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.support.file.FileIOOpt;
import com.centit.workflow.po.NodeInfo;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.io.IOException;

/**
 * 根据 OSID
 * @author codefan
 * @create 2013-7-10
 * @version
 */
public class NodeEventSupportFactory {
    //读取配置文件
    private static String jsonFile = Thread.currentThread().getContextClassLoader().getResource("").getPath()+"ip_environmen.json";
    public static NodeEventExecutor getNodeEventSupportBean(NodeInfo nodeInfo){
        if(nodeInfo == null){
            return  null;
        }
        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        ServletContext servletContext = webApplicationContext.getServletContext();
        Boolean isLocal = true;
        String osId  =nodeInfo.getOsId();
        String url = "";
        NodeEventExecutor nodeEventExecutor = null;
        JSONObject json = null;
        try {
            String jsonStr = FileIOOpt.readStringFromFile(jsonFile,"utf-8");
            json = JSON.parseObject(jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(json == null ){
            return null;
        }
        JSONArray osInfos = JSONArray.parseArray(json.get("osInfos").toString());
        if(osInfos == null || osInfos.size() == 0){
            return  null;
        }
        for(int i = 0;i < osInfos.size();i++){
            JSONObject osInfo = (JSONObject)osInfos.get(i);
            if(osId != null && osId.equals(osInfo.get("osId"))){
                url = (String) osInfo.get("osUrl");
                isLocal = false;
                break;
            }
        }
        if(!isLocal){
            RemoteBeanNodeEventSupport remoteNodeEventExecutor = new RemoteBeanNodeEventSupport();
            remoteNodeEventExecutor.setUrl(url);
            nodeEventExecutor = remoteNodeEventExecutor;
        }else{
            LocalBeanNodeEventSupport localNodeEventExecutor = new LocalBeanNodeEventSupport();
            localNodeEventExecutor.setApplication(servletContext);
            nodeEventExecutor = localNodeEventExecutor;
        }
        return nodeEventExecutor;
    }
}
