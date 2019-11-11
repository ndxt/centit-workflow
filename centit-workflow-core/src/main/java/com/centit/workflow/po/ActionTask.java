package com.centit.workflow.po;

import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * create by scaffold
 * @author codefan@hotmail.com
 */
@Data
@Entity
@Table(name="WF_ACTION_TASK")
public class ActionTask implements java.io.Serializable {
    private static final long serialVersionUID =  1L;

    @Id
    @Column(name = "TASK_ID")
    @ValueGenerator(strategy = GeneratorType.UUID22)
    private String taskId;

    @Column(name="NODE_INST_ID")
    private String nodeInstId;
    @Column(name="ASSIGN_TIME")
    private Date assignTime;

    @Column(name="USER_CODE")
    private String userCode;
 /*   @Column(name="ROLE_TYPE")
    private String roleType;
    @Column(name="ROLE_CODE")
    private String roleCode;*/
    @Column(name="TASK_STATE")
    private String taskState;
/*    @Column(name="IS_VALID")
    private String isValid;*/
    @Column(name="AUTH_DESC")
    private String authDesc;

    // Constructors
    /** default constructor */
    public ActionTask() {
    }
    /** minimal constructor */
    public ActionTask(
        String taskid
        ,Date  assigntime) {


        this.taskId = taskid;

        this.assignTime = assigntime;
    }


    public void copy(ActionTask other){
        this.setTaskId(other.getTaskId());
        this.nodeInstId = other.getNodeInstId();
        this.assignTime = other.getAssignTime();
        this.userCode = other.getUserCode();
        this.taskState = other.getTaskState();
    }

    public void copyNotNullProperty(ActionTask other){
        if( other.getTaskId() != null)
            this.setTaskId(other.getTaskId());
        if( other.getNodeInstId() != null)
            this.nodeInstId = other.getNodeInstId();
        if( other.getAssignTime() != null)
            this.assignTime = other.getAssignTime();
        if( other.getUserCode() != null)
            this.userCode = other.getUserCode();
    }
 }
