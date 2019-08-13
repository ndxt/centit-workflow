package com.centit.workflow.po;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * WF_OPTINFO 选项表
 * 2018年9月12日11:00:37
 **/
@Entity
@Table(name = "WF_OPTINFO")
public class FlowOptInfo implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "OPT_ID")
    private String optId;

    @Column(name = "OPT_NAME")
    private String optName;

    @Column(name = "OPT_URL")
    private String optUrl;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "UPDATE_DATE")
    private Date updateDate;

    @Column(name = "VIEW_URL")
    private String viewUrl;

    @Column(name = "FORM_TYPE")
    private String formType;

    //自定义表单得模板id
    @Column(name = "MODEL_ID")
    private String modelId;

    @Transient
    private List<FlowOptDef> wfOptDefs;

    public List<FlowOptDef> getWfOptDefs() {
        if (null == this.wfOptDefs) {
            this.wfOptDefs = new ArrayList();
        }

        return this.wfOptDefs;
    }

    public void setWfOptDefs(List<FlowOptDef> wfOptDefs) {
        this.wfOptDefs = wfOptDefs;
    }

    public void addWfOptDef(FlowOptDef FlowOptDef) {
        this.getWfOptDefs().add(FlowOptDef);
    }
    public void addAllWfOptDefs(List<FlowOptDef> wfOptDefs) {
        this.getWfOptDefs().clear();
        if (!CollectionUtils.isEmpty(wfOptDefs)) {
            Iterator var2 = wfOptDefs.iterator();

            while(var2.hasNext()) {
                FlowOptDef FlowOptDef = (FlowOptDef)var2.next();
                FlowOptDef.setOptId(this.optId);
            }

            this.getWfOptDefs().addAll(wfOptDefs);
        }
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
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

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getViewUrl() {
        return viewUrl;
    }

    public void setViewUrl(String viewUrl) {
        this.viewUrl = viewUrl;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }
}
