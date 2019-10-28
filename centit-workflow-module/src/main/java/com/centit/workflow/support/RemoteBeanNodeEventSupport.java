package com.centit.workflow.support;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.appclient.AppSession;
import com.centit.framework.appclient.HttpReceiveJSON;
import com.centit.framework.appclient.RestfulHttpRequest;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.commons.WorkflowException;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInfo;
import com.centit.workflow.po.NodeInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author codefan
 * 2013-7-10
 */
public class RemoteBeanNodeEventSupport implements NodeEventSupport {

    private static Logger logger = LoggerFactory.getLogger(RemoteBeanNodeEventSupport.class);
    private AppSession appSession ;

    public static JSONObject makeRequestParams(FlowInstance flowInst, NodeInstance nodeInst,
                                               NodeInfo nodeInfo, String optUserCode){
        JSONObject paramMap = new JSONObject();
        paramMap.put("flowInst", flowInst);
        paramMap.put("nodeInst", nodeInst);
        paramMap.put("nodeInfo",nodeInfo);
        paramMap.put("optUserCode", optUserCode);
        return paramMap;
    }

    @Override
    public void runAfterCreate(FlowInstance flowInst, NodeInstance nodeInst,
                               NodeInfo nodeInfo, String optUserCode)
        throws WorkflowException {
        if (nodeInfo.getOptBean() == null || "".equals(nodeInfo.getOptBean()))
            return;
        //String optBeanUrl = url + "/" + nodeInfo.getOptBean();
        JSONObject paramMap = makeRequestParams(flowInst,  nodeInst,
             nodeInfo,  optUserCode);
        RestfulHttpRequest.jsonPost(appSession, "/workflowEventBean/runAfterCreate", paramMap);

    }

    public void runBeforeSubmit(FlowInstance flowInst, NodeInstance nodeInst,
                                NodeInfo nodeInfo, String optUserCode) throws WorkflowException {
        if (nodeInfo.getOptBean() == null || "".equals(nodeInfo.getOptBean()))
            return;
        JSONObject paramMap = makeRequestParams(flowInst,  nodeInst,
            nodeInfo,  optUserCode);
        RestfulHttpRequest.jsonPost(appSession, "/workflowEventBean/runBeforeSubmit", paramMap);
    }

    /**
     * @param flowInst FlowInstance
     * @param nodeInst NodeInstance
     * @param nodeInfo NodeInfo
     * @param optUserCode String
     * @return boolean
     */
    @Override
    public boolean runAutoOperator(FlowInstance flowInst, NodeInstance nodeInst,
                                   NodeInfo nodeInfo, String optUserCode)
        throws WorkflowException {
        JSONObject paramMap = makeRequestParams(flowInst,  nodeInst,
            nodeInfo,  optUserCode);

        String res = RestfulHttpRequest.jsonPost(appSession, "/workflowEventBean/runAutoOperator", paramMap);
        HttpReceiveJSON json = HttpReceiveJSON.valueOfJson(res);
        return BooleanBaseOpt.castObjectToBoolean(json.getData(), false);
    }

    @Override
    public boolean canStepToNext(FlowInstance flowInst, NodeInstance nodeInst,
                                 NodeInfo nodeInfo, String optUserCode) throws WorkflowException {
        JSONObject paramMap = makeRequestParams(flowInst,  nodeInst,
            nodeInfo,  optUserCode);

        String res = RestfulHttpRequest.jsonPost(appSession, "/workflowEventBean/canStepToNext", paramMap);
        HttpReceiveJSON json = HttpReceiveJSON.valueOfJson(res);
        return BooleanBaseOpt.castObjectToBoolean(json.getData(), false);
    }

    public void setAppSession(AppSession appSession) {
        this.appSession = appSession;
    }
}
