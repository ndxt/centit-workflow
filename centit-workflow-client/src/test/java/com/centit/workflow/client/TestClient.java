package com.centit.workflow.client;

import com.centit.workflow.client.service.impl.FlowDefineClientImpl;

public class TestClient {
    public static void main(String[] args) {
        FlowDefineClientImpl impl = new FlowDefineClientImpl();
        impl.setWorkFlowServerUrl("");
    }
}
