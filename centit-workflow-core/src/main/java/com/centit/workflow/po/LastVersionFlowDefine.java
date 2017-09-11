package com.centit.workflow.po;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "F_V_LASTVERSIONFLOW")
public class LastVersionFlowDefine implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private FlowInfoId cid;

    @Column(name = "FLOW_NAME")
    private String flowName;

    @Column(name = "FLOW_CLASS")
    private String flowClass;

    @Column(name = "FLOW_STATE")
    private String flowState;

    @Column(name = "FLOW_DESC")
    private String flowDesc;

    @Column(name = "FLOW_XML_DESC")
    private String flowXmlDesc;

    @Column(name = "FLOW_PUBLISH_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date flowPublishDate;

    @Column(name = "OPT_ID")
    private String optId;

    @Column(name = "TIME_LIMIT")
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

    @Column(name = "EXPIRE_OPT")
    private String expireOpt;

    @Column(name = "AT_PUBLISH_DATE")
    @Temporal(TemporalType.TIMESTAMP)
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
