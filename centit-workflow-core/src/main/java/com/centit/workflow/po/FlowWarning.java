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
 *
 * @author codefan@hotmail.com
 */

@Entity
@Data
@Table(name = "WF_RUNTIME_WARNING")
public class FlowWarning implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "WARNING_ID")
    @ValueGenerator(strategy = GeneratorType.UUID22)
    private String warningId;

    @Column(name = "NODE_INST_ID")
    private String nodeInstId;
    @Column(name = "FLOW_INST_ID")
    private String flowInstId;
    @Column(name = "FLOW_STAGE")
    private String flowStage;

//    F ： 工作流 N ：节点  P：阶段
    @Column(name = "OBJ_TYPE")
    private String objType;

//    warningType W，预警  A  报警  N 提醒  O 其他
    @Column(name = "WARNING_TYPE")
    private String warningType;
    @Column(name = "WARNING_CODE")
    private String warningCode;
    @Column(name = "WARNING_TIME")
    private Date warningTime;

//    D 摘牌 C 纠正 F 督办 N 未处理
    @Column(name = "WARNING_STATE")
    private String warningState;

//    0 待发送 1 已发送 2 发送消息失败
    @Column(name = "NOTICE_STATE")
    private String noticeState;
    @Column(name = "WARNINGID_MSG")
    private String warningidMsg;
    @Column(name = "SEND_MSG_TIME")
    private Date sendMsgTime;
    @Column(name = "SEND_USERS")
    private String sendUsers;

    // Constructors

    /**
     * default constructor
     */
    public FlowWarning() {
    }

    /**
     * minimal constructor
     */
    public FlowWarning(String warningid, String flowInstId) {
        this.warningId = warningid;
        this.flowInstId = flowInstId;
    }

    public FlowWarning(String flowInstId, String nodeInstId,String warningType, String objType) {
        this.flowInstId = flowInstId;
        this.nodeInstId = nodeInstId;
        this.objType = objType;
        this.warningType = warningType;
        this.warningTime = new Date();
    }

    public void copy(FlowWarning other) {

        this.setWarningId(other.getWarningId());
        this.nodeInstId = other.getNodeInstId();
        this.flowInstId = other.getFlowInstId();
        this.flowStage = other.getFlowStage();
        this.warningType = other.getWarningType();
        this.warningCode = other.getWarningCode();
        this.warningTime = other.getWarningTime();
        this.warningState = other.getWarningState();
        this.warningidMsg = other.getWarningidMsg();
        this.sendMsgTime = other.getSendMsgTime();
        this.sendUsers = other.getSendUsers();
    }

    public void copyNotNullProperty(FlowWarning other) {
        if (other.getWarningId() != null)
            this.setWarningId(other.getWarningId());

        if (other.getNodeInstId() != null)
            this.nodeInstId = other.getNodeInstId();
        if (other.getFlowInstId() != null)
            this.flowInstId = other.getFlowInstId();
        if (other.getFlowStage() != null)
            this.flowStage = other.getFlowStage();
        if (other.getWarningType() != null)
            this.warningType = other.getWarningType();
        if (other.getWarningCode() != null)
            this.warningCode = other.getWarningCode();
        if (other.getWarningTime() != null)
            this.warningTime = other.getWarningTime();
        if (other.getWarningState() != null)
            this.warningState = other.getWarningState();
        if (other.getWarningidMsg() != null)
            this.warningidMsg = other.getWarningidMsg();
        if (other.getSendMsgTime() != null)
            this.sendMsgTime = other.getSendMsgTime();
        if (other.getSendUsers() != null)
            this.sendUsers = other.getSendUsers();

    }

    public void clearProperties() {
        this.nodeInstId = null;
        this.flowInstId = null;
        this.flowStage = null;
        this.warningType = null;
        this.warningCode = null;
        this.warningTime = null;
        this.warningState = null;
        this.warningidMsg = null;
        this.sendMsgTime = null;
        this.sendUsers = null;
    }
}
