package com.centit.workflow.po;

import com.alibaba.fastjson.annotation.JSONField;
import com.centit.framework.core.dao.DictionaryMap;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author liu_cc
 * 流程日志(审批记录)表
 * @create 2021-05-08 11:08
 * <p>
 * CREATE TABLE opt_idea_info (
 * proc_id varchar(32)  NOT NULL      comment '主键id',
 * node_inst_id varchar(32)  NOT NULL comment '节点id',
 * flow_inst_id varchar(64) comment '流程id',
 * unit_code varchar(64) comment '部门编码',
 * unit_name varchar(64) comment '部门名称',
 * user_code varchar(64) comment '用户编码',
 * user_name varchar(64) comment '用户姓名',
 * trans_date timestamp(6)comment '操作时间',
 * idea_code varchar(32) comment  '结果代码',
 * trans_idea varchar(32) comment '结果内容',
 * trans_content text comment     '办理意见',
 * node_name varchar(64) comment  '环节名称',
 * flow_phase varchar(64) comment '流程阶段',
 * node_code varchar(64) comment  '节点编码',
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
public class OptIdeaInfo implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "PROC_ID")
    @ValueGenerator(strategy = GeneratorType.UUID22)
    @Length(max = 32, message = "字段长度不能大于{max}")
    private String procId;

    @Column(name = "NODE_INST_ID")
    private String nodeInstId;

    @Column(name = "FLOW_INST_ID")
    private String flowInstId;

    @Column(name = "NODE_CODE")
    private String nodeCode;

    @Column(name = "FLOW_PHASE")
    private String flowPhase;

    @Column(name = "NODE_NAME")
    private String nodeName;

    @Column(name = "TRANS_CONTENT")
    private String transContent;

    @Column(name = "IDEA_CODE")
    private String ideaCode;

    @Column(name = "TRANS_IDEA")
    private String transIdea;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "TRANS_DATE")
    private Date transDate;

    @DictionaryMap(value = "userCode", fieldName = "userName")
    @Column(name = "USER_CODE")
    private String userCode;

    @Column(name = "USER_NAME")
    private String userName;

    @DictionaryMap(value = "unitCode", fieldName = "unitName")
    @Column(name = "UNIT_CODE")
    private String unitCode;

    @Column(name = "UNIT_NAME")
    private String unitName;

    @Column(name = "GRANTOR")
    private String grantor;
}
