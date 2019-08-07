package com.centit.workflow.po;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * create by scaffold
 * @author codefan@hotmail.com
 */
@Entity
@Table(name="WF_ORGANIZE")
public class FlowOrganize implements java.io.Serializable {
    private static final long serialVersionUID =  1L;

    @EmbeddedId
    private FlowOrganizeId cid;

    @Column(name = "AUTH_DESC")
    private String  authDesc;

    @Column(name = "AUTH_TIME")
    private Date  authTime;

    @Column(name = "UNIT_ORDER")
    private Long unitOrder;

    // Constructors
    /** default constructor */
    public FlowOrganize() {
    }
    /** minimal constructor */
    public FlowOrganize(FlowOrganizeId id

        , Date  authTime) {
        this.cid = id;


        this.authTime= authTime;
    }

/** full constructor */
    public FlowOrganize(FlowOrganizeId id

    , String  authDesc, Date  authTime) {
        this.cid = id;


        this.authDesc= authDesc;
        this.authTime= authTime;
    }

    public FlowOrganize(String wfinstid, String unitCode, String rolecode, Date  authtime) {
        this.cid = new FlowOrganizeId(wfinstid,  unitCode,  rolecode);
        this.authTime= authtime;
    }

    public FlowOrganizeId getCid() {
        return this.cid;
    }

    public void setCid(FlowOrganizeId id) {
        this.cid = id;
    }

    public String getFlowInstId() {
        if(this.cid==null)
            this.cid = new FlowOrganizeId();
        return this.cid.getFlowInstId();
    }

    public void setFlowInstId(String flowInstId) {
        if(this.cid==null)
            this.cid = new FlowOrganizeId();
        this.cid.setFlowInstId(flowInstId);
    }

    public String getUnitCode() {
        if(this.cid==null)
            this.cid = new FlowOrganizeId();
        return this.cid.getUnitCode();
    }

    public void setUnitCode(String unitCode) {
        if(this.cid==null)
            this.cid = new FlowOrganizeId();
        this.cid.setUnitCode(unitCode);
    }

    public String getRoleCode() {
        if(this.cid==null)
            this.cid = new FlowOrganizeId();
        return this.cid.getRoleCode();
    }

    public void setRoleCode(String roleCode) {
        if(this.cid==null)
            this.cid = new FlowOrganizeId();
        this.cid.setRoleCode(roleCode);
    }

    public Long getUnitOrder() {
        return unitOrder;
    }

    public void setUnitOrder(Long unitOrder) {
        this.unitOrder = unitOrder;
    }

    // Property accessors

    public String getAuthDesc() {
        return this.authDesc;
    }

    public void setAuthDesc(String authDesc) {
        this.authDesc = authDesc;
    }

    public Date getAuthTime() {
        return this.authTime;
    }

    public void setAuthTime(Date authTime) {
        this.authTime = authTime;
    }



    public void copy(FlowOrganize other){

        this.setFlowInstId(other.getFlowInstId());
        this.setUnitCode(other.getUnitCode());
        this.setRoleCode(other.getRoleCode());

        this.authDesc= other.getAuthDesc();
        this.authTime= other.getAuthTime();
        this.unitOrder = other.getUnitOrder();

    }

    public void copyNotNullProperty(FlowOrganize other){

        if( other.getFlowInstId() != null)
            this.setFlowInstId(other.getFlowInstId());
        if( other.getUnitCode() != null)
            this.setUnitCode(other.getUnitCode());
        if( other.getRoleCode() != null)
            this.setRoleCode(other.getRoleCode());

        if( other.getAuthDesc() != null)
            this.authDesc= other.getAuthDesc();
        if( other.getAuthTime() != null)
            this.authTime= other.getAuthTime();
        if( other.getUnitOrder() != null)
            this.unitOrder = other.getUnitOrder();
    }

    public void clearProperties(){

        this.authDesc= null;
        this.authTime= null;

    }
}
