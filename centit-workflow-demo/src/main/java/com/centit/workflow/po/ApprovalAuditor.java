package com.centit.workflow.po;

import com.sun.istack.internal.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by chen_rj on 2017/8/3.
 */
@Entity
@Table(name="APPROVAL_AUDITOR")
public class ApprovalAuditor implements java.io.Serializable{
    private static final long serialVersionUID =  1L;
    @Id
    @NotNull
    @Column(name="AUDITOR_ID")
    private Long auditorId;
    @Column(name="APPROVAL_ID")
    private Long approvalId;
    @Column(name="PHASE_NO")
    private String phaseNo;
    @Column(name="USER_CODE")
    private String userCode;
    @Column(name="IS_PRIMARY_AUDITOR")
    private String isPrimaryAuditor;

    public Long getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(Long auditorId) {
        this.auditorId = auditorId;
    }

    public Long getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(Long approvalId) {
        this.approvalId = approvalId;
    }

    public String getPhaseNo() {
        return phaseNo;
    }

    public void setPhaseNo(String phraseNo) {
        this.phaseNo = phraseNo;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getIsPrimaryAuditor() {
        return isPrimaryAuditor;
    }

    public void setIsPrimaryAuditor(String isPrimaryAuditor) {
        this.isPrimaryAuditor = isPrimaryAuditor;
    }
}
