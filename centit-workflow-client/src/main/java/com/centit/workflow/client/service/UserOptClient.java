package com.centit.workflow.client.service;

import org.apache.http.impl.client.CloseableHttpClient;

import java.util.Map;

/**
 * Created by chen_rj on 2018-5-2.
 */
public interface UserOptClient {
    CloseableHttpClient allocHttpClient() throws Exception ;

    void releaseHttpClient(CloseableHttpClient httpClient);

    void setWorkFlowServerUrl(String workFlowServerUrl);

    void saveOptIdeaForAutoSubmit(Map<String,Object> paraMap);

}
