package com.centit.workflow.client.po;

import java.io.Serializable;
import java.util.Date;

public class LastVersionFlowDefine implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private FlowInfoId cid;

    private String flowName;

    private String flowClass;

    private String flowState;

    private String flowDesc;

    private String flowXmlDesc;

    private Date flowPublishDate;

    private String optId;

    private String timeLimit;

    public String getWfname() {
        return flowName;
    }

    public void setWfname(String wfname) {
        this.flowName = wfname;
    }

    public String getWfclass() {
        return flowClass;
    }

    public void setWfclass(String wfclass) {
        this.flowClass = wfclass;
    }

    public String getWfstate() {
        return flowState;
    }

    public void setWfstate(String wfstate) {
        this.flowState = wfstate;
    }

    public String getWfdesc() {
        return flowDesc;
    }

    public void setWfdesc(String wfdesc) {
        this.flowDesc = wfdesc;
    }

    public String getWfxmldesc() {
        return flowXmlDesc;
    }

    public void setWfxmldesc(String wfxmldesc) {
        this.flowXmlDesc = wfxmldesc;
    }

    public Date getWfPubDate() {
        return flowPublishDate;
    }

    public void setWfPubDate(Date wfPubDate) {
        this.flowPublishDate = wfPubDate;
    }

    public String getOptid() {
        return optId;
    }

    public void setOptid(String optid) {
        this.optId = optid;
    }

    public String getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getExpireOpt() {
        return expireOpt;
    }

    public void setExpireOpt(String expireOpt) {
        this.expireOpt = expireOpt;
    }

    public Date getAtPublishDate() {
        return atPublishDate;
    }

    public void setAtPublishDate(Date atPublishDate) {
        this.atPublishDate = atPublishDate;
    }

    public FlowInfoId getCid() {
        return cid;
    }

    public void setCid(FlowInfoId cid) {
        this.cid = cid;
    }

    private String expireOpt;

    private Date atPublishDate;

    public FlowInfo toWfFlowDefine() {
        FlowInfo wf = new FlowInfo();
        wf.setCid(this.cid);
        wf.setFlowName(this.flowName);
        wf.setFlowClass(this.flowClass);
        wf.setFlowDesc(this.flowDesc);
        wf.setFlowState(this.flowState);
        wf.setFlowXmlDesc(this.flowXmlDesc);
        wf.setPublishDate(this.flowPublishDate);
        wf.setOptId(this.optId);
        wf.setTimeLimit(this.timeLimit);
        wf.setExpireOpt(this.expireOpt);
        wf.setAtPublishDate(this.atPublishDate);

        return wf;
    }

}
