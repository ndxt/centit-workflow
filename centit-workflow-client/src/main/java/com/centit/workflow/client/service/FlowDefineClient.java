package com.centit.workflow.client.service;

import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.FlowInfo;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.List;
import java.util.Map;

/**
 * Created by chen_rj on 2018-5-2.
 */
public interface FlowDefineClient {
    CloseableHttpClient allocHttpClient() throws Exception ;

    void releaseHttpClient(CloseableHttpClient httpClient);

    void setWorkFlowServerUrl(String workFlowServerUrl) ;

    /**
     * 列举系统中的所有流程，只显示最新版本的
     */
    List<FlowInfo> list();
}
