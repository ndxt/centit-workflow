package com.centit.workflow.po;

import javax.validation.constraints.NotNull;

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
@Table(name = "OPT_NODE")
public class OptNode implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    @Column(name = "OPT_NODE_ID")
    private String optNodeId;
    @NotNull
    @Column(name = "OPT_ID")
    private String optId;
    @NotNull
    @Column(name = "OPT_NODE_NAME")
    private String optName;
    @Column(name = "OPT_URL")
    private String optUrl;
    @Column(name = "OPT_TYPE")
    private String optType;
    @Column(name = "OPT_NODE_ORDER")
    private Integer optNodeOrder;
    @Column(name = "GMT_CREATE_TIME")
    private Date gmtCreateTime;
    @Column(name = "GMT_MODIFY_TIME")
    private Date gmtModifyTime;

    public String getOptNodeId() {
        return optNodeId;
    }

    public void setOptNodeId(String optNodeId) {
        this.optNodeId = optNodeId;
    }

    public String getOptId() {
        return optId;
    }

    public void setOptId(String optId) {
        this.optId = optId;
    }

    public String getOptName() {
        return optName;
    }

    public void setOptName(String optName) {
        this.optName = optName;
    }

    public String getOptUrl() {
        return optUrl;
    }

    public void setOptUrl(String optUrl) {
        this.optUrl = optUrl;
    }

    public String getOptType() {
        return optType;
    }

    public void setOptType(String optType) {
        this.optType = optType;
    }

    public Integer getOptNodeOrder() {
        return optNodeOrder;
    }

    public void setOptNodeOrder(Integer optNodeOrder) {
        this.optNodeOrder = optNodeOrder;
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
