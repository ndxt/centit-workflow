package com.centit.workflow.po;


import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * create by scaffold
 * @author codefan@hotmail.com
 */
@Entity
@Data
@Table(name="WF_ACTION_LOG")
public class ActionLog implements java.io.Serializable{
    private static final long serialVersionUID =  1L;

    @Id
    @Column(name = "ACTION_ID")
    @ValueGenerator(strategy = GeneratorType.UUID22)
    private String actionId;

    @Column(name = "FLOW_INST_ID")
    private String flowInstId;

    @Column(name="NODE_INST_ID")
    private String nodeInstId;
    /**
     's: 状态变更，挂起节点、 唤醒超时节点、  唤醒节点 、使失效、 终止节点 、使一个正常的节点变为游离状态 、 是游离节点失效
     c: 创建节点  、创建一个游离节点 创建（任意）指定节点、 创建流程同时创建首节点
     r: 流转管理，包括  强行回退  、强行提交
     t: 期限管理 、 设置期限
     a: 节点任务管理  分配任务、  删除任务 、  禁用任务
     u: 变更属性';
     */
    @Column(name="ACTION_TYPE")
    private String actionType;
    @Column(name="ACTION_TIME")
    private Date actionTime;
    @Column(name="USER_CODE")
    private String userCode;
    @Column(name="ROLE_TYPE")
    private String roleType;
    @Column(name="ROLE_CODE")
    private String roleCode;
    @Column(name="GRANTOR")
    private String grantor;

    @Column(name="LOG_DETAIL")
    private String logDetail;
    // Constructors
    /** default constructor */
    public ActionLog() {
    }
    /** minimal constructor */
    public ActionLog(
        String actionId
        ,String  actiontype,Date  actiontime) {


        this.actionId = actionId;

        this.actionType = actiontype;
        this.actionTime = actiontime;
    }

/** full constructor */
    public ActionLog(
        String actionId
    ,String  nodeinstid,String  actiontype,Date  actiontime,String  usercode,
    String  roletype,String  rolecode,String grantor) {


        this.actionId = actionId;

        this.nodeInstId = nodeinstid;
        this.actionType = actiontype;
        this.actionTime = actiontime;
        this.userCode = usercode;
        this.roleType = roletype;
        this.roleCode = rolecode;
        this.grantor = grantor;
    }

    public void copy(ActionLog other){

        this.setActionId(other.getActionId());

        this.nodeInstId = other.getNodeInstId();
        this.actionType = other.getActionType();
        this.actionTime = other.getActionTime();
        this.userCode = other.getUserCode();
        this.roleType = other.getRoleType();
        this.roleCode = other.getRoleCode();
        this.grantor = other.getGrantor();

    }

    public void copyNotNullProperty(ActionLog other){

    if( other.getActionId() != null)
        this.setActionId(other.getActionId());

        if( other.getNodeInstId() != null)
            this.nodeInstId = other.getNodeInstId();
        if( other.getActionType() != null)
            this.actionType = other.getActionType();
        if( other.getActionTime() != null)
            this.actionTime = other.getActionTime();
        if( other.getUserCode() != null)
            this.userCode = other.getUserCode();
        if( other.getRoleType() != null)
            this.roleType = other.getRoleType();
        if( other.getRoleCode() != null)
            this.roleCode = other.getRoleCode();
        if(other.getGrantor() != null)
            this.grantor = other.getGrantor();

    }

}
