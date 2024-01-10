package com.centit.workflow.po;

import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Table(name = "WF_EVENT_INFO")
public class FlowEventInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "FLOW_EVENT_ID")
    @ApiModelProperty(value = "流程实例编号", required = true)
    @ValueGenerator(strategy = GeneratorType.UUID22)
    private String flowEventId;

    @Column(name = "FLOW_INST_ID")
    @ApiModelProperty(value = "流程实例编号", required = true)
    private String flowInstId;

    @Column(name = "SENDER_USER")
    private String senderUser;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_PARAM")
    private String eventParam;

    @Column(name = "RECEIVE_TIME")
    @OrderBy("DESC")
    private Date   receiveTime;

    @Column(name = "OPT_TIME")
    private Date   optTime;

    public  static final String OPT_STATE_NORMAL = "N";
    public  static final String OPT_STATE_SUCCESS = "S";
    public  static final String OPT_STATE_FAILED = "F";
    /**
     * N:未处理 S：处理成功 F：处理失败 P：需要再次执行 E： 消息失效
     */
    @Column(name = "OPT_STATE")
    private String optState;

    @Column(name = "OPT_RESULT")
    private String optResult;

    public FlowEventInfo(){
        this.optState = OPT_STATE_NORMAL;
        this.receiveTime = DatetimeOpt.currentUtilDate();
    }
}
