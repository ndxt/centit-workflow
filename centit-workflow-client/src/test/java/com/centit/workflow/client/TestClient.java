package com.centit.workflow.client;

import com.centit.framework.model.basedata.UserInfo;
import com.centit.framework.model.security.CentitUserDetails;
import com.centit.support.network.HardWareUtils;
import com.centit.workflow.client.service.impl.FlowManagerClientImpl;
import com.centit.workflow.client.service.impl.WorkflowAppSession;


public class TestClient {
    public static void main(String[] args) {

        CentitUserDetails userDetails = new CentitUserDetails();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserCode("admin");
        userDetails.setUserInfo(userInfo);
        userDetails.setLoginIp(HardWareUtils.getLocalhost().getHostAddress());

        FlowManagerClientImpl impl = new FlowManagerClientImpl();
        WorkflowAppSession appSession = new WorkflowAppSession();
        appSession.setAppServerUrl("http://localhost:8080/workflow/workflow");
        impl.setAppSession(appSession);
        impl.suspendInstance("1",userDetails,"b");
       // System.out.println(JSON.toJSONString(a));
    }
}
