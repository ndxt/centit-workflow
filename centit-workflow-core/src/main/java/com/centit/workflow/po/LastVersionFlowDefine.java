package com.centit.workflow.po;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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

    @Column(name = "VERSION")
    @NotNull(message = "字段不能为空")
    @Range( max = 9999, message = "版本号不能大于{max}")
    private Long version;

    @Column(name = "FLOW_CODE")
    @NotBlank(message = "字段不能为空")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private String flowCode;

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

    public FlowInfoId getCid() {
        return cid;
    }

    public void setCid(FlowInfoId cid) {
        this.cid = cid;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getFlowClass() {
        return flowClass;
    }

    public void setFlowClass(String flowClass) {
        this.flowClass = flowClass;
    }

    public String getFlowState() {
        return flowState;
    }

    public void setFlowState(String flowState) {
        this.flowState = flowState;
    }

    public String getFlowDesc() {
        return flowDesc;
    }

    public void setFlowDesc(String flowDesc) {
        this.flowDesc = flowDesc;
    }

    public String getFlowXmlDesc() {
        return flowXmlDesc;
    }

    public void setFlowXmlDesc(String flowXmlDesc) {
        this.flowXmlDesc = flowXmlDesc;
    }

    public Date getFlowPublishDate() {
        return flowPublishDate;
    }

    public void setFlowPublishDate(Date flowPublishDate) {
        this.flowPublishDate = flowPublishDate;
    }

    public String getOptId() {
        return optId;
    }

    public void setOptId(String optId) {
        this.optId = optId;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
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
