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

    public Long getRelegateno() {
        return this.relegateNo;
    }

    public void setRelegateno(Long relegateno) {
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

    public String getIsvalid() {
        return this.isValid;
    }

    public void setIsvalid(String isvalid) {
        this.isValid = isvalid;
    }

    public Date getRelegatetime() {
        return this.relegateTime;
    }

    public void setRelegatetime(Date relegatetime) {
        this.relegateTime = relegatetime;
    }

    public Date getExpiretime() {
        return this.expireTime;
    }

    public void setExpiretime(Date expiretime) {
        this.expireTime = expiretime;
    }

    public String getUnitcode() {
        return this.unitCode;
    }

    public void setUnitcode(String unitcode) {
        this.unitCode = unitcode;
    }

    public String getRoletype() {
        return this.roleType;
    }


    public void setRoletype(String roletype) {
        this.roleType = roletype;
    }

    public String getRolecode() {
        return this.roleCode;
    }

    public void setRolecode(String rolecode) {
        this.roleCode = rolecode;
    }

    public String getGrantdesc() {
        return this.grantDesc;
    }

    public void setGrantdesc(String grantdesc) {
        this.grantDesc = grantdesc;
    }

    public String getRecorder() {
        return this.recorder;
    }

    public void setRecorder(String recorder) {
        this.recorder = recorder;
    }

    public Date getRecorddate() {
        return this.recordDate;
    }

    public void setRecorddate(Date recorddate) {
        this.recordDate = recorddate;
    }

    public void copy(RoleRelegate other) {

        this.setRelegateno(other.getRelegateno());

        this.grantor = other.getGrantor();
        this.grantee = other.getGrantee();
        this.isValid = other.getIsvalid();
        this.relegateTime = other.getRelegatetime();
        this.expireTime = other.getExpiretime();
        this.unitCode = other.getUnitcode();
        this.roleType = other.getRoletype();
        this.roleCode = other.getRolecode();
        this.grantDesc = other.getGrantdesc();
        this.recorder = other.getRecorder();
        this.recordDate = other.getRecorddate();

    }

    public void copyNotNullProperty(RoleRelegate other) {

        if (other.getRelegateno() != null)
            this.setRelegateno(other.getRelegateno());

        if (other.getGrantor() != null)
            this.grantor = other.getGrantor();
        if (other.getGrantee() != null)
            this.grantee = other.getGrantee();
        if (other.getIsvalid() != null)
            this.isValid = other.getIsvalid();
        if (other.getRelegatetime() != null)
            this.relegateTime = other.getRelegatetime();
        if (other.getExpiretime() != null)
            this.expireTime = other.getExpiretime();
        if (other.getUnitcode() != null)
            this.unitCode = other.getUnitcode();
        if (other.getRoletype() != null)
            this.roleType = other.getRoletype();
        if (other.getRolecode() != null)
            this.roleCode = other.getRolecode();
        if (other.getGrantdesc() != null)
            this.grantDesc = other.getGrantdesc();
        if (other.getRecorder() != null)
            this.recorder = other.getRecorder();
        if (other.getRecorddate() != null)
            this.recordDate = other.getRecorddate();

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
