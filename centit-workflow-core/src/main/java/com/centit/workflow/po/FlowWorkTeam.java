package com.centit.workflow.po;

import io.swagger.annotations.ApiModelProperty;

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
@Table(name="WF_TEAM")
public class FlowWorkTeam implements java.io.Serializable {
    private static final long serialVersionUID =  1L;

    @EmbeddedId
    private FlowWorkTeamId cid;

    @Column(name = "AUTH_DESC")
    @ApiModelProperty(value = "办件角色描述")
    private String  authDesc;

    @Column(name = "AUTH_TIME")
    @ApiModelProperty(value = "设置时间")
    private Date  authTime;

    @Column(name = "USER_ORDER")
    @ApiModelProperty(value = "角色排序")
    private Long userOrder;
    // Constructors
    /** default constructor */
    public FlowWorkTeam() {
    }
    /** minimal constructor */
    public FlowWorkTeam(FlowWorkTeamId id

        , Date  authtime) {
        this.cid = id;


        this.authTime= authtime;
    }

/** full constructor */
    public FlowWorkTeam(FlowWorkTeamId id

    , String  authdesc, Date  authtime) {
        this.cid = id;
        this.authDesc= authdesc;
        this.authTime= authtime;
    }

    public FlowWorkTeam(String wfinstid, String usercode, String rolecode, Date  authtime) {
           this.cid = new FlowWorkTeamId(wfinstid,  usercode,  rolecode);
           this.authTime= authtime;
       }

    public FlowWorkTeamId getCid() {
        return this.cid;
    }

    public void setCid(FlowWorkTeamId id) {
        this.cid = id;
    }

    public String getFlowInstId() {
        if(this.cid==null)
            this.cid = new FlowWorkTeamId();
        return this.cid.getFlowInstId();
    }

    public void setFlowInstId(String wfinstid) {
        if(this.cid==null)
            this.cid = new FlowWorkTeamId();
        this.cid.setFlowInstId(wfinstid);
    }

    public String getUserCode() {
        if(this.cid==null)
            this.cid = new FlowWorkTeamId();
        return this.cid.getUserCode();
    }

    public void setUserCode(String usercode) {
        if(this.cid==null)
            this.cid = new FlowWorkTeamId();
        this.cid.setUserCode(usercode);
    }

    public String getRoleCode() {
        if(this.cid==null)
            this.cid = new FlowWorkTeamId();
        return this.cid.getRoleCode();
    }

    public void setRoleCode(String rolecode) {
        if(this.cid==null)
            this.cid = new FlowWorkTeamId();
        this.cid.setRoleCode(rolecode);
    }

    public Long getUserOrder() {
        return userOrder;
    }

    public void setUserOrder(Long userOrder) {
        this.userOrder = userOrder;
    }

    public String getAuthDesc() {
        return this.authDesc;
    }

    public void setAuthDesc(String authdesc) {
        this.authDesc = authdesc;
    }

    public Date getAuthTime() {
        return this.authTime;
    }

    public void setAuthTime(Date authtime) {
        this.authTime = authtime;
    }



    public void copy(FlowWorkTeam other){

        this.setFlowInstId(other.getFlowInstId());
        this.setUserCode(other.getUserCode());
        this.setRoleCode(other.getRoleCode());

        this.authDesc= other.getAuthDesc();
        this.authTime= other.getAuthTime();
        this.userOrder = other.getUserOrder();
    }

    public void copyNotNullProperty(FlowWorkTeam other){

        if( other.getFlowInstId() != null)
            this.setFlowInstId(other.getFlowInstId());
        if( other.getUserCode() != null)
            this.setUserCode(other.getUserCode());
        if( other.getRoleCode() != null)
            this.setRoleCode(other.getRoleCode());

        if( other.getAuthDesc() != null)
            this.authDesc= other.getAuthDesc();
        if( other.getAuthTime() != null)
            this.authTime= other.getAuthTime();

        if( other.getUserOrder() != null)
            this.userOrder = other.getUserOrder();
    }
}
