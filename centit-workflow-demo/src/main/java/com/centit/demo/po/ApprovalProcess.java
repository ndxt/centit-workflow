package com.centit.demo.po;

import com.sun.istack.internal.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by chen_rj on 2017/8/3.
 */
@Entity
@Table(name="APPROVAL_PROCESS")
public class ApprovalProcess implements  java.io.Serializable{
    private static final long serialVersionUID =  1L;
    @Id
    @NotNull
    @Column(name="PROCESS_ID")
    private Long processId;
    @Column(name="APPROVAL_ID")
    private Long approvalId;
    @Column(name="NODE_INST_ID")
    private Long nodeInstId;
    @Column(name="PHASE_NO")
    private String phraseNo;
    @Column(name="USER_CODE")
    private String userCode;
    @Column(name="AUDIT_RESULT")
    private String auditResult;
    @Column(name="RESULT_DESC")
    private String resultDesc;

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public Long getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(Long approvalId) {
        this.approvalId = approvalId;
    }

    public Long getNodeInstId() {
        return nodeInstId;
    }

    public void setNodeInstId(Long nodeInstId) {
        this.nodeInstId = nodeInstId;
    }

    public String getPhraseNo() {
        return phraseNo;
    }

    public void setPhraseNo(String phraseNo) {
        this.phraseNo = phraseNo;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(String auditResult) {
        this.auditResult = auditResult;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }
}
