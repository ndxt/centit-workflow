package com.centit.workflow.client;

import com.centit.workflow.client.service.impl.FlowManagerClientImpl;


public class TestClient {
    public static void main(String[] args) {
        FlowManagerClientImpl impl = new FlowManagerClientImpl();
        impl.setWorkFlowServerUrl("http://localhost:8080/workflow/workflow");
        impl.init();
        impl.suspendInstance("1","a","b");
       // System.out.println(JSON.toJSONString(a));
    }
}
