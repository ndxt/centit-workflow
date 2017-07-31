package com.centit.workflow.client.po;


import java.util.*;

/**
 * create by scaffold
 * @author codefan@hotmail.com
 */ 
public class FlowInstance implements java.io.Serializable {
	private static final long serialVersionUID =  1L;

	private FlowInfo flowDefine;
	private Long flowInstId;
	
	private String flowOptName;

    private String flowOptTag;
    private Date createTime;
    private Long  promiseTime;
    private Long  timeLimit;
    /**
     *  流程状态 
     *  N 正常  C 完成   P 暂停 挂起     F 强行结束
     */
	private String instState;
	private String isSubInst;
	private Long preInstId;
	private Long preNodeInstId;
    
    private String unitCode;

	private String userCode;
    
	private Date  lastUpdateTime;
	private String lastUpdateUser;
	private String isTimer; //不计时N、计时T(有期限)、暂停P  忽略(无期限) F

	private String  optName;
	private String flowName;
	private String curStep;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getFlowInstId() {
        return flowInstId;
    }

    public void setFlowInstId(Long flowInstId) {
        this.flowInstId = flowInstId;
    }

    public String getFlowOptName() {
        return flowOptName;
    }

    public void setFlowOptName(String flowOptName) {
        this.flowOptName = flowOptName;
    }

    public String getFlowOptTag() {
        return flowOptTag;
    }

    public void setFlowOptTag(String flowOptTag) {
        this.flowOptTag = flowOptTag;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getPromiseTime() {
        return promiseTime;
    }

    public void setPromiseTime(Long promiseTime) {
        this.promiseTime = promiseTime;
    }

    public Long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getInstState() {
        return instState;
    }

    public void setInstState(String instState) {
        this.instState = instState;
    }

    public String getIsSubInst() {
        return isSubInst;
    }

    public void setIsSubInst(String isSubInst) {
        this.isSubInst = isSubInst;
    }

    public Long getPreInstId() {
        return preInstId;
    }

    public void setPreInstId(Long preInstId) {
        this.preInstId = preInstId;
    }

    public Long getPreNodeInstId() {
        return preNodeInstId;
    }

    public void setPreNodeInstId(Long preNodeInstId) {
        this.preNodeInstId = preNodeInstId;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getLastUpdateUser() {
        return lastUpdateUser;
    }

    public void setLastUpdateUser(String lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }

    public String getIsTimer() {
        return isTimer;
    }

    public void setIsTimer(String isTimer) {
        this.isTimer = isTimer;
    }

    public String getOptName() {
        return optName;
    }

    public void setOptName(String optName) {
        this.optName = optName;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getCurStep() {
        return curStep;
    }

    public void setCurStep(String curStep) {
        this.curStep = curStep;
    }

    public FlowInfo getFlowInfo() {
        return flowDefine;
    }

    public void setFlowInfo(FlowInfo flowInfo) {
        this.flowDefine = flowInfo;
    }
}
