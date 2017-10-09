package com.centit.workflow.po;

import org.jetbrains.annotations.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by chen_rj on 2017-9-19.
 */
@Entity
@Table(name = "OPT_STAGE")
public class OptStage implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    @Column(name = "OPT_STAGE_ID")
    private String optStageId;
    @NotNull
    @Column(name = "OPT_ID")
    private String optId;
    @NotNull
    @Column(name = "OPT_STAGE_NAME")
    private String optStageName;
    @NotNull
    @Column(name = "OPT_STAGE_CODE")
    private String optStageCode;
    @Column(name = "OPT_STAGE_ORDER")
    private Integer optStageOrder;
    @Column(name = "GMT_CREATE_TIME")
    private Date gmtCreateTime;
    @Column(name = "GMT_MODIFY_TIME")
    private Date gmtModifyTime;

    public String getOptStageId() {
        return optStageId;
    }

    public void setOptStageId(String optStageId) {
        this.optStageId = optStageId;
    }

    public String getOptId() {
        return optId;
    }

    public void setOptId(String optId) {
        this.optId = optId;
    }

    public String getOptStageName() {
        return optStageName;
    }

    public void setOptStageName(String optStageName) {
        this.optStageName = optStageName;
    }

    public String getOptStageCode() {
        return optStageCode;
    }

    public void setOptStageCode(String optStageCode) {
        this.optStageCode = optStageCode;
    }

    public Integer getOptStageOrder() {
        return optStageOrder;
    }

    public void setOptStageOrder(Integer optStageOrder) {
        this.optStageOrder = optStageOrder;
    }

    public Date getGmtCreateTime() {
        return gmtCreateTime;
    }

    public void setGmtCreateTime(Date gmtCreateTime) {
        this.gmtCreateTime = gmtCreateTime;
    }

    public Date getGmtModifyTime() {
        return gmtModifyTime;
    }

    public void setGmtModifyTime(Date gmtModifyTime) {
        this.gmtModifyTime = gmtModifyTime;
    }
}
