package com.centit.workflow.client;

import com.alibaba.fastjson.JSON;
import com.centit.support.json.JSONOpt;
import com.centit.workflow.client.service.impl.FlowDefineClientImpl;
import com.centit.workflow.client.service.impl.FlowManagerClientImpl;
import com.centit.workflow.po.FlowInfo;
import com.centit.workflow.po.FlowInstance;
import netscape.javascript.JSObject;


public class TestClient {
    public static void main(String[] args) {
        FlowManagerClientImpl impl = new FlowManagerClientImpl();
        impl.setWorkFlowServerUrl("http://localhost:8080/workflow/workflow");
        impl.init();
        impl.suspendInstance("1","a","b");
       // System.out.println(JSON.toJSONString(a));
    }
}
