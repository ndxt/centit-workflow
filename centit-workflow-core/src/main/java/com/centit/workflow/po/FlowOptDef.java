package com.centit.workflow.po;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * WF_OPTDEF 选项表
 * 2018年9月12日11:00:37
 **/
@Entity
@Table(name = "WF_OPTDEF")
public class FlowOptDef implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "OPT_CODE")
    private String optCode;

    @Column(name = "OPT_ID")
    private String optId;

    @Column(name = "OPT_NAME")
    private String optName;

    @Column(name = "OPT_METHOD")
    private String optMethod;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "UPDATE_DATE")
    private Date updateDate;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getOptCode() {
        return optCode;
    }

    public void setOptCode(String optCode) {
        this.optCode = optCode;
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

    public String getOptMethod() {
        return optMethod;
    }

    public void setOptMethod(String optMethod) {
        this.optMethod = optMethod;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
