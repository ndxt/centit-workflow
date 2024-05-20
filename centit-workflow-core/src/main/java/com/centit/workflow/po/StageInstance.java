package com.centit.workflow.po;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * create by scaffold
 * @author codefan@hotmail.com
 */
@Entity
@Data
@Table(name = "WF_STAGE_INSTANCE")
public class StageInstance implements java.io.Serializable {
    private static final long serialVersionUID =  1L;
    @EmbeddedId
    private StageInstanceId cid;
    @Column(name = "STAGE_CODE")
    private String  stageCode;

    @Transient
    private String  stageName;

    /**
     * 计时状态 N 为开始 not begin  F 不计是 、T 计时 、P 暂停 、 W 已预警 、 E 已逾期处理
     */
    @Column(name = "TIMER_STATUS")
    private String timerStatus;

    @Column(name = "BEGIN_TIME")
    private Date beginTime;

    @Column(name = "LAST_UPDATE_TIME")
    private Date  lastUpdateTime;

    /**
     * 预警时间
     */
    @Column(name = "warning_time")
    private Date warningTime;
    /**
     * 截止时间
     */
    @Column(name = "deadline_time")
    private Date deadlineTime;
    /**
     * 暂停时间 isTimer=='P' 有效
     */
    @Column(name = "pause_time")
    private Date pauseTime;


    // Constructors
    /** default constructor */
    public StageInstance() {
        this.timerStatus = FlowWarning.TIMER_STATUS_NOT_BEGIN;
    }
    /** minimal constructor */
    public StageInstance(StageInstanceId id) {
        this.cid = id;
        this.timerStatus = FlowWarning.TIMER_STATUS_NOT_BEGIN;
    }

/** full constructor */
    public StageInstance(StageInstanceId id, String stageCode, Date beginTime, Date lastUpdateTime) {
        this.cid = id;
        this.stageCode = stageCode;
        this.beginTime= beginTime;
        this.lastUpdateTime= lastUpdateTime;
    }

    public StageInstanceId getCid() {
        return this.cid;
    }

    public void setCid(StageInstanceId id) {
        this.cid = id;
    }

    public String getFlowInstId() {
        if(this.cid==null)
            this.cid = new StageInstanceId();
        return this.cid.getFlowInstId();
    }

    public void setFlowInstId(String flowInstId) {
        if(this.cid==null)
            this.cid = new StageInstanceId();
        this.cid.setFlowInstId(flowInstId);
    }

    public String getStageId() {
        if(this.cid==null)
            this.cid = new StageInstanceId();
        return this.cid.getStageId();
    }

    public void setStageId(String stageId) {
        if(this.cid==null)
            this.cid = new StageInstanceId();
        this.cid.setStageId(stageId);
    }

    public void copy(StageInstance other){
        this.setFlowInstId(other.getFlowInstId());
        this.setStageId(other.getStageId());
        this.stageCode = other.getStageCode();
        this.timerStatus = other.getTimerStatus();
        this.deadlineTime = other.getDeadlineTime();
        this.pauseTime = other.getPauseTime();
        this.beginTime=  other.getBeginTime();
        this.lastUpdateTime= other.getLastUpdateTime();
    }

    public void copyNotNullProperty(StageInstance other){
        if( other.getFlowInstId() != null)
            this.setFlowInstId(other.getFlowInstId());
        if( other.getStageId() != null)
            this.setStageId(other.getStageId());
        if( other.getStageCode() != null)
            this.stageCode = other.getStageCode();
        if (other.getTimerStatus() != null)
            this.timerStatus = other.getTimerStatus();
        if (other.getDeadlineTime() != null)
            this.deadlineTime = other.getDeadlineTime();
        if (other.getPauseTime() != null)
            this.pauseTime = other.getPauseTime();
        if( other.getBeginTime() != null)
            this.beginTime=  other.getBeginTime();
        if( other.getLastUpdateTime() != null)
            this.lastUpdateTime= other.getLastUpdateTime();
    }

    public void clearProperties(){
        this.stageCode = null;
        this.timerStatus = null;
        this.deadlineTime = null;
        this.pauseTime = null;
        this.beginTime=  null;
        this.lastUpdateTime= null;
    }

}
