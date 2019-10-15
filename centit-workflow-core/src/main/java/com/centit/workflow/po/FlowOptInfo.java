package com.centit.workflow.po;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
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
@Data
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

    /**
     * 默认流程
     */
    @Column(name = "DEFAULT_FLOW")
    private String default_flow;

    /**
     * 业务标题魔板，flowOptName
     */
    @Column(name = "TITLE_TEMPLATE")
    private String title_template;

    //自定义表单得模板id
    @Column(name = "MODEL_ID")
    private String modelId;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "UPDATE_DATE")
    private Date updateDate;

    @Transient
    private List<FlowOptPage> wfOptDefs;

    public List<FlowOptPage> getWfOptDefs() {
        if (null == this.wfOptDefs) {
            this.wfOptDefs = new ArrayList();
        }

        return this.wfOptDefs;
    }



    public void addWfOptDef(FlowOptPage FlowOptDef) {
        this.getWfOptDefs().add(FlowOptDef);
    }
    public void addAllWfOptDefs(List<FlowOptPage> wfOptDefs) {
        this.getWfOptDefs().clear();
        if (!CollectionUtils.isEmpty(wfOptDefs)) {
            Iterator var2 = wfOptDefs.iterator();

            while(var2.hasNext()) {
                FlowOptPage FlowOptDef = (FlowOptPage)var2.next();
                FlowOptDef.setOptId(this.optId);
            }

            this.getWfOptDefs().addAll(wfOptDefs);
        }
    }


}
