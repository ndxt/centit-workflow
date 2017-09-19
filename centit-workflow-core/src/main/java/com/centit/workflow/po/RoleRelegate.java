package com.centit.workflow.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * create by scaffold
 * 
 * @author codefan@hotmail.com
 */
@Entity
@Table(name = "WF_ROLE_RELEGATE")
public class RoleRelegate implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "RELEGATE_NO")
    private Long relegateNo;
    @Column(name = "GRANTOR")
    private String grantor;
    @Column(name = "GRANTEE")
    private String grantee;
    @Column(name = "IS_VALID")
    private String isValid;
    @Column(name = "RELEGATE_TIME")
    private Date relegateTime;
    @Column(name = "EXPIRE_TIME")
    private Date expireTime;
    @Column(name = "UNIT_CODE")
    private String unitCode;
    @Column(name = "ROLE_TYPE")
    private String roleType;
    @Column(name = "ROLE_CODE")
    private String roleCode;
    @Column(name = "GRANT_DESC")
    private String grantDesc;
    @Column(name = "RECORDER")
    private String recorder;
    @Column(name = "RECORD_DATE")
    private Date recordDate;

    // Constructors
    /** default constructor */
    public RoleRelegate() {
    }

    /** minimal constructor */
    public RoleRelegate(Long relegateno, String grantor, String grantee,
                        String isvalid, Date relegatetime) {

        this.relegateNo = relegateno;

        this.grantor = grantor;
        this.grantee = grantee;
        this.isValid = isvalid;
        this.relegateTime = relegatetime;
    }

    /** full constructor */
    public RoleRelegate(Long relegateno, String grantor, String grantee,
                        String isvalid, Date relegatetime, Date expiretime,
                        String unitcode, String roletype, String rolecode,
                        String grantdesc, String recorder, Date recorddate) {

        this.relegateNo = relegateno;

        this.grantor = grantor;
        this.grantee = grantee;
        this.isValid = isvalid;
        this.relegateTime = relegatetime;
        this.expireTime = expiretime;
        this.unitCode = unitcode;
        this.roleType = roletype;
        this.roleCode = rolecode;
        this.grantDesc = grantdesc;
        this.recorder = recorder;
        this.recordDate = recorddate;
    }

    public Long getRelegateNo() {
        return this.relegateNo;
    }

    public void setRelegateNo(Long relegateno) {
        this.relegateNo = relegateno;
    }

    // Property accessors

    public String getGrantor() {
        return this.grantor;
    }

    public void setGrantor(String grantor) {
        this.grantor = grantor;
    }

    public String getGrantee() {
        return this.grantee;
    }

    public void setGrantee(String grantee) {
        this.grantee = grantee;
    }

    public String getIsValid() {
        return this.isValid;
    }

    public void setIsValid(String isvalid) {
        this.isValid = isvalid;
    }

    public Date getRelegateTime() {
        return this.relegateTime;
    }

    public void setRelegateTime(Date relegatetime) {
        this.relegateTime = relegatetime;
    }

    public Date getExpireTime() {
        return this.expireTime;
    }

    public void setExpireTime(Date expiretime) {
        this.expireTime = expiretime;
    }

    public String getUnitCode() {
        return this.unitCode;
    }

    public void setUnitCode(String unitcode) {
        this.unitCode = unitcode;
    }

    public String getRoleType() {
        return this.roleType;
    }


    public void setRoleType(String roletype) {
        this.roleType = roletype;
    }

    public String getRoleCode() {
        return this.roleCode;
    }

    public void setRoleCode(String rolecode) {
        this.roleCode = rolecode;
    }

    public String getGrantDesc() {
        return this.grantDesc;
    }

    public void setGrantDesc(String grantdesc) {
        this.grantDesc = grantdesc;
    }

    public String getRecorder() {
        return this.recorder;
    }

    public void setRecorder(String recorder) {
        this.recorder = recorder;
    }

    public Date getRecordDate() {
        return this.recordDate;
    }

    public void setRecordDate(Date recorddate) {
        this.recordDate = recorddate;
    }

    public void copy(RoleRelegate other) {

        this.setRelegateNo(other.getRelegateNo());

        this.grantor = other.getGrantor();
        this.grantee = other.getGrantee();
        this.isValid = other.getIsValid();
        this.relegateTime = other.getRelegateTime();
        this.expireTime = other.getExpireTime();
        this.unitCode = other.getUnitCode();
        this.roleType = other.getRoleType();
        this.roleCode = other.getRoleCode();
        this.grantDesc = other.getGrantDesc();
        this.recorder = other.getRecorder();
        this.recordDate = other.getRecordDate();

    }

    public void copyNotNullProperty(RoleRelegate other) {

        if (other.getRelegateNo() != null)
            this.setRelegateNo(other.getRelegateNo());

        if (other.getGrantor() != null)
            this.grantor = other.getGrantor();
        if (other.getGrantee() != null)
            this.grantee = other.getGrantee();
        if (other.getIsValid() != null)
            this.isValid = other.getIsValid();
        if (other.getRelegateTime() != null)
            this.relegateTime = other.getRelegateTime();
        if (other.getExpireTime() != null)
            this.expireTime = other.getExpireTime();
        if (other.getUnitCode() != null)
            this.unitCode = other.getUnitCode();
        if (other.getRoleType() != null)
            this.roleType = other.getRoleType();
        if (other.getRoleCode() != null)
            this.roleCode = other.getRoleCode();
        if (other.getGrantDesc() != null)
            this.grantDesc = other.getGrantDesc();
        if (other.getRecorder() != null)
            this.recorder = other.getRecorder();
        if (other.getRecordDate() != null)
            this.recordDate = other.getRecordDate();

    }

    public void clearProperties() {

        this.grantor = null;
        this.grantee = null;
        this.isValid = null;
        this.relegateTime = null;
        this.expireTime = null;
        this.unitCode = null;
        this.roleType = null;
        this.roleCode = null;
        this.grantDesc = null;
        this.recorder = null;
        this.recordDate = null;

    }
}
