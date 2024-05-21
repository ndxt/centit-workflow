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
 * 这个可以只为 超时报警（预警）的日志， 过期执行日志
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

    @Column(name = "FLOW_INST_ID")
    private String flowInstId;

    @Column(name = "NODE_INST_ID")
    private String nodeInstId;

    @Column(name = "FLOW_STAGE")
    private String flowStage;

    // F ： 工作流  N ：节点  P：阶段
    @Column(name = "OBJ_TYPE")
    private String objType;

    // W 预警 E 超时
    @Column(name = "WARNING_TYPE")
    private String warningType;

    @Column(name = "WARNING_TIME")
    private Date warningTime;

    // 发送的消息，或者执行失败的信息
    @Column(name = "WARNING_MSG")
    private String warningMsg;

    @Column(name = "SEND_USERS")
    private String sendUsers;

    /**
     * default constructor
     */
    public FlowWarning() {
    }

}
