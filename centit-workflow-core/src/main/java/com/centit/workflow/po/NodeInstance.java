package com.centit.workflow.po;

import com.alibaba.fastjson.annotation.JSONField;
import com.centit.framework.core.dao.DictionaryMap;
import com.centit.support.common.WorkTimeSpan;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.*;

/**
 * create by scaffold
 *
 * @author codefan@hotmail.com
 */
@Data
@Entity
@Table(name = "WF_NODE_INSTANCE")
public class NodeInstance implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    public static final String TOP_RUNTIME_TOKEN = "T";

    @Id
    @Column(name = "NODE_INST_ID")
    @ValueGenerator(strategy = GeneratorType.UUID22)
    private String nodeInstId;
    /**
     * 流程实例ID
     */
    @Column(name = "FLOW_INST_ID")
    private String flowInstId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = NodeInfo.class)
    @JoinColumn(name = "nodeId")
    private NodeInfo node;

    @Column(name = "NODE_ID")
    private String nodeId;
    //    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "CREATE_TIME")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @ValueGenerator(strategy = GeneratorType.FUNCTION, value = "today()")
    private Date createTime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "LAST_UPDATE_TIME")
//    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateTime;
    @Column(name = "PREV_NODE_INST_ID")
    private String prevNodeInstId;
    @Column(name = "PROMISE_TIME")
    private Long promiseTime;
    @Column(name = "TIME_LIMIT")
    private Long timeLimit;

    public static final String NODE_STATE_NORMAL = "N";
    public static final String NODE_STATE_ROLLBACK = "B";
    public static final String NODE_STATE_COMPLETE = "C";
    public static final String NODE_STATE_FORCE = "F";
    public static final String NODE_STATE_WAITE_SUBPROCESS = "W";
    public static final String NODE_STATE_PAUSE = "P";
    public static final String NODE_STATE_SUSPEND = "S";
    public static final String NODE_STATE_SYNC = "T";
    /**
     * N 正常  B 已回退  C 完成  F 被强制结束
     * P 暂停  W 等待子流程返回  S 等等前置节点（可能是多个）完成
     * T 同步节点，等待消息触发
     */
    @Column(name = "NODE_STATE")
    private String nodeState;
    @Column(name = "SUB_FLOW_INST_ID")
    private String subFlowInstId;
    @Column(name = "UNIT_CODE")
    private String unitCode;

    @Column(name = "TRANS_PATH")
    private String transPath;

    //T: 通过 tasklist 分配(已废弃)， D：通过 岗位角色 自动匹配 S：静态代办（usercode)
    public static final String TASK_ASSIGN_TYPE_STATIC = "S";
    public static final String TASK_ASSIGN_TYPE_DYNAMIC = "D";
    public static final String TASK_ASSIGN_TYPE_MULTI = "T";
    @Column(name = "TASK_ASSIGNED")
    private String taskAssigned;

    public static final String RUN_TOKEN_GLOBAL = "T";
    public static final String RUN_TOKEN_FLOW = RUN_TOKEN_GLOBAL;
    public static final String RUN_TOKEN_ISOLATED = "R";
    public static final String RUN_TOKEN_INSERT = "L";

    @Column(name = "RUN_TOKEN")
    //令牌： T * 表示正常运行的节点  R * 表示游离节点  L * 表示临时插入的节点
    private String runToken;
    @Column(name = "LAST_UPDATE_USER")
    private String lastUpdateUser;
    @Column(name = "GRANTOR")
    private String grantor;
    @Column(name = "IS_TIMER")
    //不计时N、计时T 、暂停 P
    //T 计时、有期限   F 不计时   H仅环节计时  暂停P
    private String isTimer;
    @Column(name = "STAGE_CODE")
    private String stageCode;
    @DictionaryMap(value = "userCode", fieldName = "userName")
    @Column(name = "USER_CODE")
    private String userCode;
    /**
     * 节点参数，又业务填写，并且通过业务url返回给业务系统
     * 可以作为业务相关表的主键
     */
    @Column(name = "NODE_PARAM")
    private String nodeParam;

    /*@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = ActionTask.class)
    @JoinColumn(name = "nodeInstId")
    private Set<ActionTask> wfActionTasks = null;*/

    //非持久化属性
    @Transient
    private String nodeName;
    @Transient
    private String flowOptName;
    @Transient
    private String flowOptTag;
    /**
     * xz 行政角色  gw 岗位角色 bj 办件角色  en 工作流引擎
     */
    @Column(name = "ROLE_TYPE")
    private String roleType;

    @Column(name = "ROLE_CODE")
    private String roleCode;

    @Transient
    //yes:可以回收；no：不可以回收
    private String isRecycle;
    @Transient
    private List<String> trainsUsers;
    @Transient
    // 暂时用于返回数据，后面有好的解决方案再修改
    private String optUrl;

    public NodeInstance() {
        this.timeLimit = null;
        this.taskAssigned = "D";
        this.node = new NodeInfo();
    }

    public NodeInstance(
        String nodeInstId
    ) {
        this.timeLimit = null;
        this.nodeInstId = nodeInstId;
        this.taskAssigned = "D";
        this.node = new NodeInfo();
    }

    public NodeInstance(String nodeInstId, String wfinstid, String nodeid, Date createtime, Date starttime, String prevnodeinstid,
                        Long promiseTime, Long timeLimit, String nodestate, String subwfinstid, String unitcode,
                        String transPath, String taskassigned, String runToken, String lastUpdateUser,
                        String isTimer, String flowPhase) {
        this.nodeInstId = nodeInstId;
        this.flowInstId = wfinstid;
        this.node.setNodeId(nodeid);
        this.createTime = createtime;
        this.lastUpdateTime = starttime;
        this.prevNodeInstId = prevnodeinstid;
        this.promiseTime = promiseTime;
        this.timeLimit = timeLimit;
        this.nodeState = nodestate;
        this.subFlowInstId = subwfinstid;
        this.unitCode = unitcode;
        this.transPath = transPath;
        this.taskAssigned = taskassigned;
        this.setRunToken(runToken);
        this.lastUpdateUser = lastUpdateUser;
        this.isTimer = isTimer;
        this.stageCode = flowPhase;
    }

    public String getNodeCode() {
        if (this.node == null) {
            return null;
        }
        return this.node.getNodeCode();
    }

    public String getPromiseTimeStr() {
        if (promiseTime == null) {
            return "";
        }
        WorkTimeSpan wts = new WorkTimeSpan();
        wts.fromNumberAsMinute(promiseTime);
        return wts.getTimeSpanDesc();
    }

    public String getTimeLimitStr() {
        if (timeLimit == null) {
            return "";
        }
        WorkTimeSpan wts = new WorkTimeSpan();
        wts.fromNumberAsMinute(timeLimit);
        return wts.getTimeSpanDesc();
    }

    public boolean checkIsInRunning() {
        return StringUtils.equalsAny(this.nodeState,
            "N", "W", "T");
    }

    public boolean checkIsNotCompleted() {
        return StringUtils.equalsAny(this.nodeState,
            "N", "W", "T", "S", "P");
    }

    public boolean isTaskAssign() {
        return "T".equals(taskAssigned);
    }

    /**
     * 获得节点的运行令牌
     * 令牌： T* 表示正常运行的节点  R* 表示游离节点  L* 表示临时插入的节点
     *
     * @return
     */
    public String getRunToken() {
        if (runToken == null) {
            return "";
        }
        return runToken;
    }

    /**
     * 计算一个令牌token的上层token
     *
     * @param token 当前令牌
     * @return 上层令牌
     */
    public static String calcSuperToken(String token) {
        if (token == null) {
            return "";
        }
        int nPos = token.lastIndexOf('.');
        if (nPos < 1) {
            return "";
        }
        return token.substring(0, nPos);
    }

    /**
     * 获取上层令牌
     *
     * @return
     */
    public String getParentToken() {
        return NodeInstance.calcSuperToken(runToken);
    }

    /**
     * 计算一个令牌token的主干token，临时插入节点去掉前面的L
     * 游离节点 在汇聚式暂不考虑 R 不用管 2018-8-13
     *
     * @param token 传入的参数
     * @return 主干token
     */
    private static String calcTrunkToken(String token) {
        if (token == null) {
            return "";
        }
        int nPos = 0;
        int nl = token.length();
        while (nPos < nl) {
            if (token.charAt(nPos) == 'L') {
                nPos++;
            } else {
                break;
            }
        }
        if (nPos >= nl) {
            return "";
        }
        return token.substring(nPos);
    }


    /**
     * 获取主干令牌
     *
     * @return
     */
    public String getTrunkToken() {
        return NodeInstance.calcTrunkToken(runToken);
    }

    /**
     * 计算一个令牌token的层次 （代）
     *
     * @param token
     * @return
     */
    public static int calcTokenGeneration(String token) {
        if (token == null) {
            return 0;
        }
        int g = 1;
        int nl = token.length();
        for (int j = 0; j < nl; j++) {
            if (token.charAt(j) == '.') {
                g++;
            }
        }
        return g;
    }

    /**
     * 获取层次 （代
     *
     * @return
     */
    public int getTokenGeneration() {
        return NodeInstance.calcTokenGeneration(runToken);
    }

    /**
     * 计算一个令牌token的的祖先
     *
     * @param token
     * @param generation
     * @return
     */
    public static String truncTokenGeneration(String token, int generation) {
        if (token == null) {
            return null;
        }
        int nPos = 0;
        int g = 0;
        int nl = token.length();
        while (nPos < nl) {
            if (token.charAt(nPos) == '.') {
                g++;
            }
            if (g >= generation) {
                break;
            }
            nPos++;
        }
        if (nPos >= nl) {
            return token;
        }
        return token.substring(0, nPos);
    }

    /**
     * 计算一个令牌token的的祖先
     *
     * @param generation
     * @return
     */
    public String getSuperGenerationToken(int generation) {
        return NodeInstance.truncTokenGeneration(this.getRunToken(), generation);
    }

    public void copy(NodeInstance other) {
        this.setNodeInstId(other.getNodeInstId());
        this.flowInstId = other.getFlowInstId();
        this.node.setNodeId(other.getNodeId());
        this.createTime = other.getCreateTime();
        this.lastUpdateTime = other.getLastUpdateTime();
        this.prevNodeInstId = other.getPrevNodeInstId();
        this.nodeState = other.getNodeState();
        this.subFlowInstId = other.getSubFlowInstId();
        this.unitCode = other.getUnitCode();
        this.transPath = other.getTransPath();
        this.taskAssigned = other.getTaskAssigned();
        this.timeLimit = other.getTimeLimit();
        this.runToken = other.getRunToken();
        this.lastUpdateUser = other.getLastUpdateUser();
        this.isTimer = other.getIsTimer();
        this.stageCode = other.getStageCode();
    }

    public void copyNotNullProperty(NodeInstance other) {
        if (other.getNodeInstId() != null) {
            this.setNodeInstId(other.getNodeInstId());
        }
        if (other.getFlowInstId() != null) {
            this.flowInstId = other.getFlowInstId();
        }
        if (other.getNodeId() != null) {
            this.node.setNodeId(other.getNodeId());
        }
        if (other.getCreateTime() != null) {
            this.createTime = other.getCreateTime();
        }
        if (other.getLastUpdateTime() != null) {
            this.lastUpdateTime = other.getLastUpdateTime();
        }
        if (other.getPrevNodeInstId() != null) {
            this.prevNodeInstId = other.getPrevNodeInstId();
        }
        if (other.getNodeState() != null) {
            this.nodeState = other.getNodeState();
        }
        if (other.getSubFlowInstId() != null) {
            this.subFlowInstId = other.getSubFlowInstId();
        }
        if (other.getUnitCode() != null) {
            this.unitCode = other.getUnitCode();
        }
        if (other.getTransPath() != null) {
            this.transPath = other.getTransPath();
        }
        if (other.getTaskAssigned() != null) {
            this.taskAssigned = other.getTaskAssigned();
        }
        if (other.getTimeLimit() != null) {
            this.timeLimit = other.getTimeLimit();
        }
        if (other.getRunToken() != null) {
            this.runToken = other.getRunToken();
        }
        if (other.getLastUpdateUser() != null) {
            this.lastUpdateUser = other.getLastUpdateUser();
        }
        if (other.getIsTimer() != null) {
            this.isTimer = other.getIsTimer();
        }
        if (other.getStageCode() != null) {
            this.stageCode = other.getStageCode();
        }
        if (other.getRoleType() != null) {
            this.roleType = other.getRoleType();
        }
        if (other.getRoleCode() != null) {
            this.roleCode = other.getRoleCode();
        }
        if (other.getUserCode() != null) {
            this.userCode = other.getUserCode();
        }
        if (other.getNodeId() != null) {
            this.nodeId = other.getNodeId();
        }
        if (other.getPromiseTime() != null) {
            this.promiseTime = other.getPromiseTime();
        }
    }

    public void clearProperties() {
        this.node.setNodeId(null);
        this.createTime = null;
        this.lastUpdateTime = null;
        this.prevNodeInstId = null;
        this.nodeState = null;
        this.subFlowInstId = null;
        this.unitCode = null;
        this.transPath = null;
        this.taskAssigned = "D";
        this.timeLimit = null;
        this.runToken = null;
        this.isTimer = null;
        this.stageCode = null;
    }
}
