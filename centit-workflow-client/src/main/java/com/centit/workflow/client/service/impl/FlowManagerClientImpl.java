package com.centit.workflow.client.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.appclient.AppSession;
import com.centit.framework.appclient.HttpReceiveJSON;
import com.centit.framework.appclient.RestfulHttpRequest;
import com.centit.workflow.client.service.FlowManagerClient;
import com.centit.workflow.po.FlowInstance;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chen_rj on 2017/7/28.
 */
@Service
public class FlowManagerClientImpl implements FlowManagerClient {

    @Value("${workflow.server:}")
    private String workFlowServerUrl;

    @Value("${workflow.server.login:}")
    private String workFlowServerLoginUrl;

    public FlowManagerClientImpl() {

    }

    private AppSession appSession;

    public CloseableHttpClient allocHttpClient() throws Exception {
        return appSession.allocHttpClient();
    }

    public void makeAppSession() {
        appSession = new AppSession(workFlowServerUrl,false,null,null);
        appSession.setAppLoginUrl(workFlowServerLoginUrl);
    }

    @PostConstruct
    public void init(){
        //this.setWorkFlowServerUrl(workFlowServerUrl);
        makeAppSession();
    }

    @Override
    public JSONArray listFlowInstNodes(String wfinstid) throws Exception{
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("flowInstId",String.valueOf(wfinstid));

        HttpReceiveJSON receiveJSON = RestfulHttpRequest.getResponseData(appSession,
            "/flow/engine/listFlowInstNodes",
            paramMap);
        RestfulHttpRequest.checkHttpReceiveJSON(receiveJSON);
        return receiveJSON.getJSONArray();
    }

    /**
     * 根据 示例ID获得实例
     *
     * @param flowInstId 流程 实例id
     * @return 流程实例信息
     */
    @Override
    public FlowInstance getFlowInstance(String flowInstId) {
        return null;
    }


    @Override
    public int stopInstance(String flowInstId, String mangerUserCode, String admindesc) throws Exception{
        /*String s = */RestfulHttpRequest.jsonPut(appSession,
            "/flow/manager/stopInstance/"+flowInstId+"/"+mangerUserCode,null);
        return 0;
    }

    /**
     * 暂停一个流程    P 暂停 挂起
     *
     * @param flowInstId     流程实例id
     * @param mangerUserCode 管理人员代码
     * @param admindesc      管理原因
     *                       throws Exception 异常
     * @return 状态码
     */
    @Override
    public int suspendInstance(String flowInstId, String mangerUserCode, String admindesc) {
        return 0;
    }

    /**
     * 激活一个 挂起的或者无效的流程  N 正常
     *
     * @param flowInstId     流程实例id
     * @param mangerUserCode 管理人员代码
     * @param admindesc      管理原因
     * @return 状态码
     */
    @Override
    public int activizeInstance(String flowInstId, String mangerUserCode, String admindesc) {
        return 0;
    }

    /**
     * 暂停流程的一个节点  P 暂停
     *
     * @param nodeInstId     节点实例id
     * @param mangerUserCode 管理人员代码
     * @return 状态码
     */
    @Override
    public int suspendNodeInstance(String nodeInstId, String mangerUserCode) {
        return 0;
    }

    /**
     * 使流程的 挂起和失效的节点 正常运行 N 正常
     *
     * @param nodeInstId     节点实例id
     * @param mangerUserCode 管理人员代码
     * @return 状态码
     */
    @Override
    public int activizeNodeInstance(String nodeInstId, String mangerUserCode) {
        return 0;
    }

    /**
     * 强制流转到下一结点，这个好像不好搞，主要是无法获得业务数据，只能提交没有分支的节点
     *
     * @param nodeInstId     节点实例id
     * @param mangerUserCode 管理人员代码
     * @return 状态码
     */
    @Override
    public String forceCommit(String nodeInstId, String mangerUserCode) {
        return null;
    }

    /**
     * 从这个节点重新运行该流程，包括已经结束的流程
     *
     * @param nodeInstId     节点实例id
     * @param mangerUserCode 管理人员代码
     * @return 新的节点实例id
     */
    @Override
    public String resetFlowToThisNode(String nodeInstId, String mangerUserCode) {
        return null;
    }

    /**
     * 分配节点任务
     * Task_assigned 设置为 S 如果多于 一个人 放在 ActionTask 表中，并且把  Task_assigned 设置为 T
     *
     * @param nodeInstId
     * @param userCode
     * @param mangerUserCode
     * @param authDesc
     */
    @Override
    public long assignNodeTask(String nodeInstId, String userCode, String mangerUserCode, String authDesc) {
        return 0;
    }

    /**
     * 添加节点任务, 添加操作人元
     * Task_assigned 设置为 S 如果多于 一个人 放在 ActionTask 表中，并且把  Task_assigned 设置为 T
     *
     * @param nodeInstId
     * @param userCode
     * @param mangerUserCode
     * @param authDesc
     */
    @Override
    public long addNodeTask(String nodeInstId, String userCode, String mangerUserCode, String authDesc) {
        return 0;
    }

    /**
     * 删除节点任务
     *
     * @param nodeInstId
     * @param userCode
     * @param mangerUserCode
     */
    @Override
    public int deleteNodeTask(String nodeInstId, String userCode, String mangerUserCode) {
        return 0;
    }

}
