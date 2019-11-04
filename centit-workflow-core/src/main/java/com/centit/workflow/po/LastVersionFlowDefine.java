package com.centit.workflow.po;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "F_V_LASTVERSIONFLOW")
public class LastVersionFlowDefine implements Serializable {

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

    @Column(name = "EXPIRE_OPT")
    private String expireOpt;

    @Column(name = "AT_PUBLISH_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date atPublishDate;

    public Long getVersion() {
        if(this.cid==null)
            this.cid = new FlowInfoId();
        return this.cid.getVersion();
    }

    public void setVersion(Long version) {
        if(this.cid==null)
            this.cid = new FlowInfoId();
        this.cid.setVersion(version);
    }

    public String getFlowCode() {
        if(this.cid==null)
            this.cid = new FlowInfoId();
        return this.cid.getFlowCode();
    }

    public void setFlowCode(String wfcode) {
        if(this.cid==null)
            this.cid = new FlowInfoId();
        this.cid.setFlowCode(wfcode);
    }

    public FlowInfo toWfFlowDefine() {
        FlowInfo wf = new FlowInfo();
        wf.setCid(this.cid);
        wf.setFlowName(this.flowName);
        wf.setFlowClass(this.flowClass);
        wf.setFlowDesc(this.flowDesc);
        wf.setFlowState(this.flowState);
        wf.setFlowXmlDesc(this.flowXmlDesc);
        wf.setFlowPublishDate(this.flowPublishDate);
        wf.setOptId(this.optId);
        wf.setTimeLimit(this.timeLimit);
        wf.setExpireOpt(this.expireOpt);
        wf.setAtPublishDate(this.atPublishDate);

        return wf;
    }

}
