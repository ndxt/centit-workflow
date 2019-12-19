package com.centit.workflow.client;

import com.centit.workflow.client.service.impl.FlowManagerClientImpl;
import com.centit.workflow.client.service.impl.WorkflowAppSession;


public class TestClient {
    public static void main(String[] args) {
        FlowManagerClientImpl impl = new FlowManagerClientImpl();
        WorkflowAppSession appSession = new WorkflowAppSession();
        appSession.setAppServerUrl("http://localhost:8080/workflow/workflow");
        impl.setAppSession(appSession);
        impl.suspendInstance("1","a","b");
       // System.out.println(JSON.toJSONString(a));
    }
}
