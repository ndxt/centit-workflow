package com.centit.workflow.po;

import com.sun.istack.internal.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by chen_rj on 2017/8/3.
 */
@Entity
@Table(name="APPROVAL_EVENT")
public class ApprovalEvent implements java.io.Serializable{
    private static final long serialVersionUID =  1L;
    @Id
    @NotNull
    @Column(name="APPROVAL_ID")
    private Long approvalId;
    @Column(name="EVENT_TITLE")
    private String eventTitle;
    @Column(name="EVENT_DESC")
    private String eventDesc;
    @Column(name="REQUEST_TIME")
    private Date requestTime;
    @Column(name="CURRENT_PHASE")
    private String currentPhase;
    @Column(name="APPROVAL_STATE")
    private String approvalState;
    @Column(name="COMPLETE_TIME")
    private String completeTime;
    @Column(name="RESULT_DESC")
    private String resultDesc;

    public Long getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(Long approvalId) {
        this.approvalId = approvalId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public String getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(String currentPhase) {
        this.currentPhase = currentPhase;
    }

    public String getApprovalState() {
        return approvalState;
    }

    public void setApprovalState(String approvalState) {
        this.approvalState = approvalState;
    }

    public String getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }
}
