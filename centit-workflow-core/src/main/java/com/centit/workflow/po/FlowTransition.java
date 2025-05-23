package com.centit.workflow.po;

import com.alibaba.fastjson2.annotation.JSONField;
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
@Table(name = "WF_TRANSITION")
@Data
public class FlowTransition implements java.io.Serializable {
    private static final long serialVersionUID =  1L;

    @Id
    @Column(name = "TRANS_ID")
    private String transId;

    @Column(name = "TRANS_CLASS")
    private String transClass;
    @Column(name = "TRANS_NAME")
    private String transName;
    @Column(name = "TRANS_DESC")
    private String transDesc;
    @Column(name = "START_NODE_ID")
    private String startNodeId;
    @Column(name = "END_NODE_ID")
    private String endNodeId;
    @Column(name = "TRANS_CONDITION")
    private String transCondition;

    /**
     * 期限类别 I ： 未设置（ignore 默认 ）、N 无 (无期限 none ) 、 F 每实例固定期限 fix 、
     * C 节点固定期限  cycle、 H 继承上一个节点剩余时间 hierarchical。
     * @see com.centit.workflow.po.NodeInfo
     */
    @Column(name = "LIMIT_TYPE")
    private String limitType;
    @Column(name = "TIME_LIMIT")
    private String timeLimit;

    // * T可以忽略 F 不可以忽略  是否可以忽略运行
    @Column(name = "CAN_IGNORE")
    private Boolean canIgnore;

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

    // Constructors
    /** default constructor */
    public FlowTransition() {
        this.canIgnore= true;
        this.limitType = NodeInfo.TIME_LIMIT_TYPE_IGNORE;
    }

    public void copy(FlowTransition other){
        //this.setTransId(other.getTransId());
        this.setFlowDefine(other.getFlowDefine());
        this.transClass = other.getTransClass();
        this.transName = other.getTransName();
        this.transDesc = other.getTransDesc();
        this.startNodeId = other.getStartNodeId();
        this.endNodeId = other.getEndNodeId();
        this.transCondition = other.getTransCondition();
        this.limitType = other.getLimitType();
        this.timeLimit = other.getTimeLimit();
        this.canIgnore= other.getCanIgnore();
    }

    public void copyNotNullProperty(FlowTransition other){
        /*if( other.getTransId() != null)
            this.setTransId(other.getTransId());*/
        if( other.getFlowDefine() != null)
            this.setFlowDefine(other.getFlowDefine());
        if( other.getTransClass() != null)
            this.transClass = other.getTransClass();
        if( other.getTransName() != null)
            this.transName = other.getTransName();
        if( other.getTransDesc() != null)
            this.transDesc = other.getTransDesc();
        if( other.getStartNodeId() != null)
            this.startNodeId = other.getStartNodeId();
        if( other.getEndNodeId() != null)
            this.endNodeId = other.getEndNodeId();
        if( other.getTransCondition() != null)
            this.transCondition = other.getTransCondition();
        if( other.getLimitType() != null)
            this.limitType= other.getLimitType();
        if( other.getTimeLimit() != null)
            this.limitType= other.getTimeLimit();
        if( other.getCanIgnore() != null)
            this.canIgnore= other.getCanIgnore();
    }

  public void clearProperties(){
        this.flowDefine = null;
        this.transClass =null;
        this.transName = null;
        this.transDesc = null;
        this.startNodeId =null;
        this.endNodeId = null;
        this.transCondition = null;
        this.limitType = NodeInfo.TIME_LIMIT_TYPE_IGNORE;
        this.timeLimit = null;
        this.canIgnore=true;
    }

}
