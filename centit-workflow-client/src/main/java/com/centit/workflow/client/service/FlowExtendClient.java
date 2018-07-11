package com.centit.workflow.client.service;

import com.centit.workflow.po.FlowInstance;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * Created by chen_rj on 2018-7-11.
 */
public interface FlowExtendClient{
    CloseableHttpClient getHttpClient() throws Exception ;

    void releaseHttpClient(CloseableHttpClient httpClient);

    void setWorkFlowServerUrl(String workFlowServerUrl) ;

    void updateFlowInstance(FlowInstance flowInstance);
}
