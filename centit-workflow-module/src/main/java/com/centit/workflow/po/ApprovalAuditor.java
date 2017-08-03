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
    private String auditorId;
    @Column(name="PHASE_NO")
    private String phraseNo;
    @Column(name="USER_CODE")
    private String userCode;
    @Column(name="IS_PRIMARY_AUDITOR")
    private String isPrimaryAuditor;

    public String getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(String auditorId) {
        this.auditorId = auditorId;
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

    public String getIsPrimaryAuditor() {
        return isPrimaryAuditor;
    }

    public void setIsPrimaryAuditor(String isPrimaryAuditor) {
        this.isPrimaryAuditor = isPrimaryAuditor;
    }
}
