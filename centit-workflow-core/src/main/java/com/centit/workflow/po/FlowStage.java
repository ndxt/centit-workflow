package com.centit.workflow.po;

import com.alibaba.fastjson2.annotation.JSONField;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * create by scaffold
 * @author codefan@hotmail.com
 */
@Entity
@Table(name = "WF_FLOW_STAGE")
@Data
public class FlowStage implements java.io.Serializable {
    private static final long serialVersionUID =  1L;

    @Id
    @Column(name = "STAGE_ID")
    @ValueGenerator(strategy = GeneratorType.UUID22)
    private String stageId;

    @Column(name = "STAGE_CODE")
    private String  stageCode;

    @Column(name = "STAGE_NAME")
    private String  stageName;

    @Column(name = "TIME_LIMIT")
    private String  timeLimit;

    /**
     * N：仅通知， O:不处理 ，X：挂起，E：终止（流程）， C：完成（强制提交,提交失败就挂起）
     * A ：调用api
     */
    @Column(name = "EXPIRE_OPT")
    private String  expireOpt;

    @Column(name = "EXPIRE_CALL_API")
    private String expireCallApi;

    @Column(name = "WARNING_PARAM")
    private String warningParam;

    @Column(name = "STAGE_ORDER")
    private Long  stageOrder;

    /**
     * 框架解析 不到ManyToOne的属性 这儿单独 设置
     */
    @Column(name = "VERSION")
    @NotNull
    @Range( max = 9999)
    private Long version;

    @Column(name = "FLOW_CODE")
    @NotBlank
    @Length(max = 32)
    private String flowCode;

    @JSONField(serialize=false)
    @Transient
    private FlowInfo flowDefine;

    public FlowInfo getFlowDefine() {
        return flowDefine;
    }
    public void setFlowDefine(FlowInfo flowDefine) {
        this.flowDefine = flowDefine;
    }
    // Constructors
    /** default constructor */
    public FlowStage() {
    }
    /** minimal constructor */
    public FlowStage(
        String stageId
        ,String  stageCode) {
        this.stageId = stageId;
        this.stageCode= stageCode;
    }

    public void copy(FlowStage other){
        this.setStageId(other.getStageId());
        //this.setVersion(other.getVersion());
        //this.setFlowCode(other.getFlowCode());
        this.stageCode= other.getStageCode();
        this.stageName= other.getStageName();
        this.timeLimit= other.getTimeLimit();
        this.expireOpt= other.getExpireOpt();
        this.stageOrder= other.getStageOrder();
        this.warningParam=other.getWarningParam();
    }

    public void copyNotNullProperty(FlowStage other){
        if( other.getStageId() != null)
            this.setStageId(other.getStageId());
        if( other.getStageCode() != null)
            this.stageCode= other.getStageCode();
        if( other.getStageName() != null)
            this.stageName= other.getStageName();
        if( other.getTimeLimit() != null)
            this.timeLimit= other.getTimeLimit();
        if( other.getExpireOpt() != null)
            this.expireOpt= other.getExpireOpt();
        if( other.getStageOrder() != null)
            this.stageOrder= other.getStageOrder();
        if (other.getWarningParam()!=null)
            this.warningParam=other.getWarningParam();
    }

    public void clearProperties(){
        this.flowDefine=null;
        this.stageCode= null;
        this.stageName= null;
        this.timeLimit= null;
        this.expireOpt= null;
        this.stageOrder= null;
        this.warningParam=null;
    }

}
