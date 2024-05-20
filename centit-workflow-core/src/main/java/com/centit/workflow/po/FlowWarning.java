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
 * 这个可以只为 超时报警（预警）的日志
 */

@Entity
@Data
@Table(name = "WF_RUNTIME_WARNING")
public class FlowWarning implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 计时状态 N 为未开始（只用于阶段） F 不计是 、T 计时 、P 暂停 、 W 已预警 、 E 已逾期处理、 S 同步节点
     */
    //不计时 F、 计时 T(有期限)、暂停P 忽略(无期限)
    public static final String TIMER_STATUS_NOT_BEGIN = "N";
    public static final String TIMER_STATUS_NO_LIMIT = "F";
    public static final String TIMER_STATUS_RUN = "T";
    public static final String TIMER_STATUS_SUSPEND = "P";
    public static final String TIMER_STATUS_WARN = "W";
    public static final String TIMER_STATUS_EXCEED = "E";
    public static final String TIMER_STATUS_SYNC = "S";

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

    // F ： 工作流  N ：节点  P：阶段
    @Column(name = "OBJ_TYPE")
    private String objType;

    //  warningType
    //  N：仅通知， O:不处理 ，X：挂起，E：终止（流程）， C：完成（强制提交,提交失败就挂起）
    //  A ：调用api
    @Column(name = "WARNING_TYPE")
    private String warningType;

    @Column(name = "WARNING_CODE")
    private String warningCode;

    @Column(name = "WARNING_TIME")
    private Date warningTime;

    //D 摘牌 C 纠正 F 督办 N 未处理
    @Column(name = "WARNING_STATE")
    private String warningState;

    //0 待发送 1 已发送（已执行） 2 发送消息失败（执行失败）
    @Column(name = "NOTICE_STATE")
    private String noticeState;
    // 发送的消息，或者执行失败的信息
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
        this.noticeState = "0";
        this.warningId = warningid;
        this.flowInstId = flowInstId;
    }

    public FlowWarning(String flowInstId, String nodeInstId,String warningType, String objType) {
        this.noticeState = "0";
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
        this.noticeState = other.getNoticeState();
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
        if (other.getNoticeState() != null)
            this.noticeState = other.getNoticeState();
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
        this.noticeState = "0";
    }
}
