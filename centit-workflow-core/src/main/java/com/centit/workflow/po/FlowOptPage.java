package com.centit.workflow.po;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * WF_OPTPAGE   流程页面定义
 * 2018年9月12日11:00:37
 **/
@Entity
@Data
@Table(name = "WF_OPTPAGE")
public class FlowOptPage implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "OPT_CODE")
    private String optCode;
    /**
     * 和flowOptInfo关联
     */
    @Column(name = "OPT_ID")
    private String optId;

    @Column(name = "OPT_NAME")
    private String optName;
    /**
     * C 公司开发的业务， E外部 需要IFrame嵌入， F 自定义表单
     */
    @Column(name = "PAGE_TYPE")
    private String pageType;

    /**
     * 页面Url varchar 200
     */
    @Column(name = "PAGE_URL")
    private String pageUrl;

    @Column(name = "OPT_METHOD")
    private String optMethod;


    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "UPDATE_DATE")
    private Date updateDate;


}
