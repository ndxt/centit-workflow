package com.centit.workflow.commons;

import java.util.Map;
import java.util.Set;

/**
 * Created by codefan on 17-9-11.
 */
public class SubmitNodeOptions {

    /**
     * 提交的业务（流程）节点
     */
    private long nodeInstId;
    /**
     * 操作流程的用户
     */
    private String userCode;
    /**
     * 操作归属单位（机构）
     */
    private String unitCode;
    /**
     * 如果式以代理人身份操作的，这个变量就是代理人
     */
    private String grantorCode;

    /**
     * 业务变量数据
     */
    private Map<String,Object> variables ;

    /**
     * 指定业务节点归属机构
     */
    private Map<String, Set<String>> nodeUnits;
    /**
     * 指定业务节点操作人员
     */
    private Map<String, Set<String>> nodeOptUsers;

    public SubmitNodeOptions(){
    }

    public SubmitNodeOptions(long nodeInstId, String userCode, String unitCode) {
        this.nodeInstId = nodeInstId;
        this.userCode = userCode;
        this.unitCode = unitCode;
    }

    public long getNodeInstId() {
        return nodeInstId;
    }

    public void setNodeInstId(long nodeInstId) {
        this.nodeInstId = nodeInstId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getGrantorCode() {
        return grantorCode;
    }

    public void setGrantorCode(String grantorCode) {
        this.grantorCode = grantorCode;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public Map<String, Set<String>> getNodeUnits() {
        return nodeUnits;
    }

    public void setNodeUnits(Map<String, Set<String>> nodeUnits) {
        this.nodeUnits = nodeUnits;
    }

    public Map<String, Set<String>> getNodeOptUsers() {
        return nodeOptUsers;
    }

    public void setNodeOptUsers(Map<String, Set<String>> nodeOptUsers) {
        this.nodeOptUsers = nodeOptUsers;
    }
}
