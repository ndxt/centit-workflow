package com.centit.workflow.po;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * WF_OPTINFO 流程业务定义
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

    @Column(name = "OPT_VIEW_URL")
    private String optViewUrl;
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
    private List<FlowOptPage> optPages;

    public List<FlowOptPage> getOptPages() {
        if (null == this.optPages) {
            this.optPages = new ArrayList();
        }

        return this.optPages;
    }

    public void addOptPage(FlowOptPage optPage) {
        this.getOptPages().add(optPage);
    }

    public void addOptPages(Collection<FlowOptPage> optPages) {
        this.getOptPages().clear();
        if (!CollectionUtils.isEmpty(optPages)) {
            for(FlowOptPage optPage : optPages) {
                optPage.setOptId(this.optId);
            }
            this.getOptPages().addAll(optPages);
        }
    }

}
