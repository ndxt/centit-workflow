package com.centit.workflow.po;

import com.alibaba.fastjson.annotation.JSONField;
import com.centit.support.database.orm.GeneratorCondition;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
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
    @ValueGenerator(strategy = GeneratorType.UUID)
    private String optCode;

    /**
     * 和flowOptInfo关联
     */
    @Column(name = "OPT_ID")
    private String optId;

    @Column(name = "OPT_NAME")
    private String optName;

    public static final String PAGE_TYPE_EXTERNAL = "E";
    public static final String PAGE_TYPE_FORM = "F";
    public static final String PAGE_TYPE_AUTO = "A";
    public static final String PAGE_TYPE_API = "D";
    /**
     * C 内部（同源业务），公司开发的业务；
     * E 外部 需要IFrame嵌入；
     * F 自定义表单；
     * A auto 自动执行http调用
     * D API网关接口（原来的数据包dataPacket）
     */
    @Column(name = "PAGE_TYPE")
    private String pageType;

    /**
     * 页面Url varchar 200
     */
    @Column(name = "PAGE_URL")
    private String pageUrl;
    /**
     * pageType = 'A' 时生效
     * C ：create - post R：read - get
     * U： update - put D：delete
     */
    @Column(name = "OPT_METHOD")
    private String optMethod;

    @Column(name = "REQUEST_PARAMS")
    private String requestParams;


    @Column(name = "REQUEST_BODY")
    private String requestBody;

    /**
     * 最后更新时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "UPDATE_DATE")
    @ValueGenerator(strategy = GeneratorType.FUNCTION,
        condition = GeneratorCondition.ALWAYS,value = "today()")
    private Date updateDate;
}
