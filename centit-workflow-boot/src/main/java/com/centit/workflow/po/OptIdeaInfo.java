package com.centit.workflow.po;

import com.alibaba.fastjson.annotation.JSONField;
import com.centit.framework.core.dao.DictionaryMap;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author liu_cc
 * 流程日志(审批记录)表
 * @create 2021-05-08 11:08
 * <p>
 * CREATE TABLE opt_idea_info (
 * proc_id varchar(32)       NOT NULL comment '主键id',
 * node_inst_id varchar(32)  NOT NULL comment '节点id',
 * flow_inst_id varchar(64)  NOT NULL comment '流程id',
 * unit_code varchar(64) comment '部门编码',
 * unit_name varchar(64) comment '部门名称',
 * user_code varchar(64) NOT NULL  comment '用户编码',
 * user_name varchar(64) comment '用户姓名',
 * trans_date timestamp(6)comment '操作时间',
 * idea_code varchar(32) comment  '结果代码',
 * trans_idea varchar(32) comment '结果内容',
 * trans_content text comment     '办理意见',
 * node_code varchar(64) comment  '节点编码',
 * node_name varchar(64) comment  '环节名称',
 * flow_phase varchar(64) comment '流程阶段',
 * grantor varchar(64) comment    '委托人',
 * PRIMARY KEY (proc_id, node_inst_id)
 * );
 * ALTER TABLE opt_idea_info comment '流程日志(审批记录)表';
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "OPT_IDEA_INFO")
@ApiModel("流程日志(审批记录)表")
public class OptIdeaInfo implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @Column(name = "PROC_ID")
    @ValueGenerator(strategy = GeneratorType.UUID22)
    @Length(max = 32, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "主键id")
    private String procId;

    @NotNull
    @Column(name = "NODE_INST_ID")
    @ApiModelProperty(value = "节点实例id")
    private String nodeInstId;

    @NotNull
    @Column(name = "FLOW_INST_ID")
    @ApiModelProperty(value = "流程实例id")
    private String flowInstId;

    @DictionaryMap(value = "unitCode", fieldName = "unitName")
    @Column(name = "UNIT_CODE")
    @ApiModelProperty(value = "部门编码")
    private String unitCode;

    @Column(name = "UNIT_NAME")
    @ApiModelProperty(value = "部门名称")
    private String unitName;

    @NotNull
    @DictionaryMap(value = "userCode", fieldName = "userName")
    @Column(name = "USER_CODE")
    @ApiModelProperty(value = "用户编码")
    private String userCode;

    @Column(name = "USER_NAME")
    @ApiModelProperty(value = "用户姓名")
    private String userName;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "操作时间")
    @Column(name = "TRANS_DATE")
    private Date transDate;

    @Column(name = "IDEA_CODE")
    @ApiModelProperty(value = "结果代码")
    private String ideaCode;

    @Column(name = "TRANS_IDEA")
    @ApiModelProperty(value = "结果内容")
    private String transIdea;

    @Column(name = "TRANS_CONTENT")
    @ApiModelProperty(value = "办理意见")
    private String transContent;

    @Column(name = "NODE_CODE")
    @ApiModelProperty(value = "节点编码")
    private String nodeCode;

    @Column(name = "NODE_NAME")
    @ApiModelProperty(value = "环节名称")
    private String nodeName;

    @Column(name = "FLOW_PHASE")
    @ApiModelProperty(value = "流程阶段")
    private String flowPhase;

    @Column(name = "GRANTOR")
    @ApiModelProperty(value = "委托人")
    private String grantor;
}
