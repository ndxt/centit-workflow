package com.centit.workflow.po;

import com.alibaba.fastjson2.annotation.JSONField;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * create by scaffold
 * @author codefan@hotmail.com
 */
@Entity
@Table(name = "WF_FLOW_STAGE")
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

    @Column(name = "IS_ACCOUNT_TIME")
    private String  isAccountTime;

    /**
     * 期限类别 I ： 未设置（ignore 在流转线上默认 ）、
     * N 无 (无期限 none 默认) 、
     * F 每实例固定期限 fix 、
     * C 节点固定期限  cycle、
     * H 继承其他节点期限 hierarchical。
     * D 当天完成 day
     * W 当周完成 week
     * M 当月完成 month
     * Y 当年完成 year
     */
    @Column(name = "LIMIT_TYPE")
    private String  limitType;

    @Column(name = "TIME_LIMIT")
    private String  timeLimit;

    /**
     * N：通知， O:不处理 ，X：挂起，E：终止（流程）， C：完成（强制提交,提交失败就挂起）
     * A ：调用api
     */
    @Column(name = "EXPIRE_OPT")
    private String  expireOpt;

    @Column(name = "EXPIRE_CALL_API")
    private String expireCallApi;

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

/** full constructor */
    public FlowStage(
            String stageId
    , FlowInfo flowDefine, String  stageCode, String  stageName, String  isAccountTime, String  limitType, String  timeLimit, String  expireOpt, Long stageOrder) {

        this.stageId = stageId;
        this.flowDefine = flowDefine;
        this.stageCode= stageCode;
        this.stageName= stageName;
        this.isAccountTime= isAccountTime;
        this.limitType= limitType;
        this.timeLimit= timeLimit;
        this.expireOpt= expireOpt;
        this.stageOrder= stageOrder;
    }



    public String getStageId() {
        return this.stageId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }
    // Property accessors


    /*public void setVersion(Long version) {
        if (null != flowDefine) {
            this.flowDefine.setVersion(version);
        }
    }*/


    /*public void setFlowCode(String flowCode) {
        if (null != flowDefine) {
            this.flowDefine.setFlowCode(flowCode);
        }
    }*/

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
    }

    public String getStageCode() {
        return this.stageCode;
    }

    public void setStageCode(String stageCode) {
        this.stageCode = stageCode;
    }

    public String getStageName() {
        return this.stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getIsAccountTime() {
        return this.isAccountTime;
    }

    public void setIsAccountTime(String isAccountTime) {
        this.isAccountTime = isAccountTime;
    }

    public String getLimitType() {
        return this.limitType;
    }

    public void setLimitType(String limitType) {
        this.limitType = limitType;
    }

    public String getTimeLimit() {
        return this.timeLimit;
    }

    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getExpireOpt() {
        return this.expireOpt;
    }

    public void setExpireOpt(String expireOpt) {
        this.expireOpt = expireOpt;
    }

    public Long getStageOrder() {
        return stageOrder;
    }

    public void setStageOrder(Long stageOrder) {
        this.stageOrder = stageOrder;
    }

    public void copy(FlowStage other){
        this.setStageId(other.getStageId());
        //this.setVersion(other.getVersion());
        //this.setFlowCode(other.getFlowCode());
        this.stageCode= other.getStageCode();
        this.stageName= other.getStageName();
        this.isAccountTime= other.getIsAccountTime();
        this.limitType= other.getLimitType();
        this.timeLimit= other.getTimeLimit();
        this.expireOpt= other.getExpireOpt();
        this.stageOrder= other.getStageOrder();

    }

    public void copyNotNullProperty(FlowStage other){

        if( other.getStageId() != null)
            this.setStageId(other.getStageId());
        /*if( other.getFlowDefine() != null)
            this.flowDefine = other.getFlowDefine();*/
        if( other.getStageCode() != null)
            this.stageCode= other.getStageCode();
        if( other.getStageName() != null)
            this.stageName= other.getStageName();
        if( other.getIsAccountTime() != null)
            this.isAccountTime= other.getIsAccountTime();
        if( other.getLimitType() != null)
            this.limitType= other.getLimitType();
        if( other.getTimeLimit() != null)
            this.timeLimit= other.getTimeLimit();
        if( other.getExpireOpt() != null)
            this.expireOpt= other.getExpireOpt();
        if( other.getStageOrder() != null)
            this.stageOrder= other.getStageOrder();
    }

    public void clearProperties(){

        this.flowDefine=null;
        this.stageCode= null;
        this.stageName= null;
        this.isAccountTime= null;
        this.limitType= null;
        this.timeLimit= null;
        this.expireOpt= null;
        this.stageOrder= null;

    }

}
