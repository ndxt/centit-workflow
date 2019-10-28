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
    /**
     * state 流程状态变更
     */
    public static final String ACTION_TYPE_CHANGE_FLOW_STATE = "S";
    /**
     * node state 节点状态变更
     */
    public static final String ACTION_TYPE_CHANGE_NODE_STATE = "N";
    /**
     * manager 流程流传管理  强行回退、强行提交、重新运行、从当前节点运行
     */
    public static final String ACTION_TYPE_FLOW_MANAGER = "M";
    /**
     * timer 流程计时管理
     */
    public static final String ACTION_TYPE_TIME_LIMIT_MANAGER = "T";
    /**
     * attribute 流程属性维护
     */
    public static final String ACTION_TYPE_FLOW_ATTRIBUTE = "A";
    /**
     * variable 流程变量维护， 包括流程机构和 办件角色变更
     */
    public static final String ACTION_TYPE_FLOW_VARIABLE = "V";
    /**
     * job 流程任务维护
     */
    public static final String ACTION_TYPE_TASK_MANAGER = "J";
    /**
     * log 流程正常 创建、提交操作
     */
    public static final String ACTION_TYPE_LOG = "L";

    @Id
    @Column(name = "ACTION_ID")
    @ValueGenerator(strategy = GeneratorType.UUID22)
    private String actionId;

    @Column(name = "FLOW_INST_ID")
    private String flowInstId;

    @Column(name="NODE_INST_ID")
    private String nodeInstId;
    /**
     "S" state 流程状态变更
     "N" node state 节点状态变更
     "M" manager 流程流传管理  强行回退、强行提交、重新运行、从当前节点运行
     "T" timer 流程计时管理
     "A" attribute 流程属性维护
     "V" variable 流程变量维护， 包括流程机构和 办件角色变更
     "J" job 流程任务维护
     "L" log 流程正常 创建、提交操作
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
