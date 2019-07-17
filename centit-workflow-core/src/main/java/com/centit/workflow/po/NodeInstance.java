package com.centit.workflow.po;

import com.alibaba.fastjson.annotation.JSONField;
import com.centit.support.common.WorkTimeSpan;

import javax.persistence.*;
import java.util.*;

/**
 * create by scaffold
 * @author codefan@hotmail.com
 */
@Entity
@Table(name="WF_NODE_INSTANCE")
public class NodeInstance implements java.io.Serializable {
    private static final long serialVersionUID =  1L;

    @Id
    @Column(name = "NODE_INST_ID")
       private Long nodeInstId;

    @Column(name="FLOW_INST_ID")
    private Long  flowInstId;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY,targetEntity = NodeInfo.class)
    @JoinColumn(name="nodeId")
    private NodeInfo node;

    @Column(name = "NODE_ID")
    private Long nodeId;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name="CREATE_TIME")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name="LAST_UPDATE_TIME")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date  lastUpdateTime;
    @Column(name="PREV_NODE_INST_ID")
    private Long prevNodeInstId;
    @Column(name="PROMISE_TIME")
    private Long  promiseTime;
    @Column(name="TIME_LIMIT")
    private Long  timeLimit;
    /**
     *
     * N 正常  B 已回退    C 完成   F被强制结束
     * P 暂停   W 等待子流程返回   S 等等前置节点（可能是多个）完成
     */
    @Column(name="NODE_STATE")
    private String  nodeState;
    @Column(name="SUB_FLOW_INST_ID")
    private Long subFlowInstId;
    @Column(name="UNIT_CODE")
    private String unitCode;

    @Column(name="TRANS_PATH")
    private String  transPath;
    //T: 通过 tasklist 分配， D：通过 岗位角色 自动匹配 S：静态代办（usercode)
    @Column(name="TASK_ASSIGNED")
    private String taskAssigned;

    @Column(name="RUN_TOKEN")
    private String  runToken;
    @Column(name="LAST_UPDATE_USER")
    private String lastUpdateUser;
    @Column(name="GRANTOR")
    private String grantor;
    @Column(name="IS_TIMER")
    private String isTimer;//不计时N、计时Y、暂停P
    @Column(name = "STAGE_CODE")
    private String stageCode;
    //add by codefan@sina.com 2014-4-20
    @Column(name="USER_CODE")
    private String userCode;

    @Column(name="NODE_PARAM")
    private String nodeParam;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,targetEntity = ActionLog.class)
    @JoinColumn(name="nodeInstId")
    private Set<ActionLog> wfActionLogs = null;// new ArrayList<WfActionLog>();

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,targetEntity = ActionTask.class)
    @JoinColumn(name="nodeInstId")
    private Set<ActionTask> wfActionTasks = null;// new ArrayList<WfActionTask>();

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
    @Column(name="ROLE_CODE")
    private String roleCode;
    @Transient
    private String isRecycle;//yes:可以回收；no：不可以回收
    @Transient
    private List<String> trainsUsers;
    // Constructors
    /** default constructor */
    public NodeInstance() {
        this.timeLimit = null;
        this.taskAssigned = "D";
        this.node=new NodeInfo();
    }
    /** minimal constructor */
    public NodeInstance(
        Long nodeinstid
        ) {
        this.timeLimit = null;
        this.nodeInstId = nodeinstid;
        this.taskAssigned = "D";
        this.node=new NodeInfo();
    }

/** full constructor */
    public NodeInstance(
     Long nodeinstid
    ,Long  wfinstid,Long  nodeid,Date  createtime,Date  starttime,Long  prevnodeinstid,
    Long  promiseTime, Long timeLimit,String  nodestate,Long  subwfinstid,String  unitcode,
    String  transPath,String  taskassigned,String runToken,String lastUpdateUser,
    String isTimer,String flowPhase) {

        this.nodeInstId = nodeinstid;

        this.flowInstId= wfinstid;
        this.node.setNodeId(nodeid);
        this.createTime = createtime;
        this.lastUpdateTime= starttime;
        this.prevNodeInstId = prevnodeinstid;
        this.promiseTime = promiseTime;
        this.timeLimit = timeLimit;
        this.nodeState= nodestate;
        this.subFlowInstId = subwfinstid;
        this.unitCode = unitcode;
        this.transPath= transPath;
        this.taskAssigned = taskassigned;
        this.setRunToken(runToken);
        this.lastUpdateUser = lastUpdateUser;
        this.isTimer = isTimer;
        this.stageCode = flowPhase;
    }


    public Long getNodeInstId() {
        return this.nodeInstId;
    }

    public void setNodeInstId(Long nodeinstid) {
        this.nodeInstId = nodeinstid;
    }
    // Property accessors

    public Long getFlowInstId() {
        return this.flowInstId;
    }

    public void setFlowInstId(Long wfinstid) {
        this.flowInstId = wfinstid;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    public String getStageCode() {
        return stageCode;
    }

    public void setStageCode(String stageCode) {
        this.stageCode = stageCode;
    }

    public String getNodeCode(){
         if(this.node==null)
             return null;
         return this.node.getNodeCode();
     }


    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createtime) {
        this.createTime = createtime;
    }

    public Date getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public void setLastUpdateTime(Date starttime) {
        this.lastUpdateTime = starttime;
    }

    public Long getPrevNodeInstId() {
        return this.prevNodeInstId;
    }

    public void setPrevNodeInstId(Long prevnodeinstid) {
        this.prevNodeInstId = prevnodeinstid;
    }

    public String getPromiseTimeStr() {
        if(promiseTime==null)
            return "";
        WorkTimeSpan wts = new WorkTimeSpan();
        wts.fromNumber(promiseTime);
        return wts.getTimeSpanDesc();
    }

    public Long getPromiseTime() {
        return promiseTime;
    }

    public void setPromiseTime(Long promiseTime) {
        this.promiseTime = promiseTime;
    }

    public String getTimeLimitStr() {
        if(timeLimit==null)
            return "";
        WorkTimeSpan wts = new WorkTimeSpan();
        wts.fromNumber(timeLimit);
        return wts.getTimeSpanDesc();
    }
    public Long getTimeLimit() {
        return timeLimit;
    }
    public void setTimeLimit(Long timeLimit) {
        this.timeLimit = timeLimit;
    }

    /**
     *
     * N 正常  B 已回退    C 完成   F被强制结束
     * P 暂停   W 等待子流程返回   S 等等前置节点（可能是多个）完成
     */
    public String getNodeState() {
        return this.nodeState;
    }
    /**
     *
     * N 正常  B 已回退    C 完成   F被强制结束
     * P 暂停   W 等待子流程返回   S 等等前置节点（可能是多个）完成
     */
    public void setNodeState(String nodestate) {
        this.nodeState = nodestate;
    }

    public Long getSubFlowInstId() {
        return this.subFlowInstId;
    }

    public void setSubFlowInstId(Long subwfinstid) {
        this.subFlowInstId = subwfinstid;
    }

    public String getUnitCode() {
        return this.unitCode;
    }

    public void setUnitCode(String unitcode) {
        this.unitCode = unitcode;
    }

    public String getTransPath() {
        return this.transPath;
    }

    public void setTransPath(String transPath) {
        this.transPath = transPath;
    }

    public boolean isTaskAssign() {

        return "T".equals(taskAssigned);
    }
    /**
     * T: 通过 tasklist 分配， D：通过 岗位 自动匹配 S：静态代办（usercode)
     * @return
     */
    public String getTaskAssigned() {
        return this.taskAssigned;
    }

    /**
     * T: 通过 tasklist 分配， D：通过 岗位 自动匹配 S：静态代办（usercode)
     * @return
     */
    public void setTaskAssigned(String taskassigned) {
        this.taskAssigned = taskassigned;
    }

    /**
     * 获得节点的运行令牌
     * 令牌： T* 表示正常运行的节点  R* 表示游离节点  L* 表示临时插入的节点
     * @return
     */
    public String getRunToken() {
        if(runToken==null)
            return "";
        return runToken;
    }

    /**
     * 计算一个令牌token的上层token
     * @param token
     * @return
     */
    public static String calcSuperToken(String token){
        if(token==null)
            return "";
        int nPos = token.lastIndexOf('.');
        if(nPos<1)
            return "";
        return token.substring(0,nPos);
    }
    /**
     * 获取上层令牌
     * @return
     */
    public String getParentToken() {
        return NodeInstance.calcSuperToken(runToken);
    }

    /**
     * 计算一个令牌token的主干token，临时插入节点去掉前面的L
     * 游离节点 在汇聚式暂不考虑 R 不用管 2018-8-13
     * @param token 传入的参数
     * @return 主干token
     */
    public static String calcTrunkToken(String token){
        if(token==null)
            return "";
        int nPos = 0;
        int nl = token.length();
        while(nPos<nl){
            if(/*token.charAt(nPos) =='R' || */token.charAt(nPos) == 'L')
                nPos++;
            else
                break;
        }
        if(nPos>=nl)
            return "";
        return token.substring(nPos);
    }


    /**
     * 获取主干令牌
     * @return
     */
    public String getTrunkToken() {
        return NodeInstance.calcTrunkToken(runToken);
    }

    /**
     * 计算一个令牌token的层次 （代）
     * @param token
     * @return
     */
    public static int calcTokenGeneration(String token){
        if(token==null)
            return 0;
        int g=1;
        int nl = token.length();
        for(int j=0;j<nl;j++){
            if(token.charAt(j) =='.')
                g++;
        }
        return g;
    }

    /**
     * 获取层次 （代
     * @return
     */
    public int getTokenGeneration() {
        return NodeInstance.calcTokenGeneration(runToken);
    }
    /**
     * 计算一个令牌token的的祖先
     * @param token
     * @param generation
     * @return
     */
    public static String truncTokenGeneration(String token,int generation){
        if(token==null)
            return null;
        int nPos = 0;
        int g=0;
        int nl = token.length();
        while(nPos<nl ){
            if(token.charAt(nPos) =='.' )
              g++;
            if(g>=generation)
                break;
            nPos++;
        }
        if(nPos>=nl)
            return token;
        return token.substring(0,nPos);
    }

    /**
     * 计算一个令牌token的的祖先
     * @param generation
     * @return
     */
    public String getSuperGenerationToken(int generation){
        return NodeInstance.truncTokenGeneration(this.getRunToken(),generation);
    }
    /**
     * 获得节点的运行令牌
     * 令牌： T * 表示正常运行的节点  R * 表示游离节点  L * 表示临时插入的节点
     * @param runToken
     */
    public void setRunToken(String runToken) {
        this.runToken = runToken;
    }

    public String getFlowStage() {
        return stageCode;
    }

    public void setFlowStage(String flowPhase) {
        this.stageCode = flowPhase;
    }

    public String getUserCode() {
        return userCode;
    }
    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
    public String getNodeParam() {
        return nodeParam;
    }
    public void setNodeParam(String nodeParam) {
        this.nodeParam = nodeParam;
    }

    public Set<ActionLog> getWfActionLogs(){
        if(this.wfActionLogs==null)
            this.wfActionLogs = new HashSet<ActionLog>();
        return this.wfActionLogs;
    }

    public String getNodeName() {
        return this.nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public void setWfActionLogs(Set<ActionLog> wfActionLogs) {
        this.wfActionLogs = wfActionLogs;
    }

    public void addWfActionLog(ActionLog wfActionLog ){
        if (this.wfActionLogs==null)
            this.wfActionLogs = new HashSet<ActionLog>();
        this.wfActionLogs.add(wfActionLog);
    }

    public void removeWfActionLog(ActionLog wfActionLog ){
        if (this.wfActionLogs==null)
            return;
        this.wfActionLogs.remove(wfActionLog);
    }

    public ActionLog newWfActionLog(){
        ActionLog res = new ActionLog();

        res.setNodeInstId(this.getNodeInstId());

        return res;
    }

    public void replaceWfActionLogs(List<ActionLog> wfActionLogs) {
        List<ActionLog> newObjs = new ArrayList<ActionLog>();
        for(ActionLog p :wfActionLogs){
            if(p==null)
                continue;
            ActionLog newdt = newWfActionLog();
            newdt.copyNotNullProperty(p);
            newObjs.add(newdt);
        }
        //delete
        boolean found = false;
        Set<ActionLog> oldObjs = new HashSet<ActionLog>();
        oldObjs.addAll(getWfActionLogs());

        for(Iterator<ActionLog> it = oldObjs.iterator(); it.hasNext();){
            ActionLog odt = it.next();
            found = false;
            for(ActionLog newdt :newObjs){
                if(odt.getActionId().equals( newdt.getActionId())){
                    found = true;
                    break;
                }
            }
            if(! found)
                removeWfActionLog(odt);
        }
        oldObjs.clear();
        //insert
        for(ActionLog newdt :newObjs){
            found = false;
            for(Iterator<ActionLog> it = getWfActionLogs().iterator();
                it.hasNext();){
                ActionLog odt = it.next();
                if(odt.getActionId().equals( newdt.getActionId())){
                    odt.copy(newdt);
                    found = true;
                    break;
                }
            }
            if(! found)
                addWfActionLog(newdt);
        }
    }

    public Set<ActionTask> getWfActionTasks(){
        if(this.wfActionTasks==null)
            this.wfActionTasks = new HashSet<ActionTask>();
        return this.wfActionTasks;
    }

    public void setWfActionTasks(Set<ActionTask> wfActionTasks) {
        this.wfActionTasks = wfActionTasks;
    }

    public void addWfActionTask(ActionTask wfActionTask ){
        if (this.wfActionTasks==null)
            this.wfActionTasks = new HashSet<ActionTask>();
        this.wfActionTasks.add(wfActionTask);
    }

    public void removeWfActionTask(ActionTask wfActionTask ){
        if (this.wfActionTasks==null)
            return;
        this.wfActionTasks.remove(wfActionTask);
    }

    public ActionTask newWfActionTask(){
        ActionTask res = new ActionTask();

        res.setNodeInstId(this.getNodeInstId());

        return res;
    }

    public void replaceWfActionTasks(List<ActionTask> wfActionTasks) {
        List<ActionTask> newObjs = new ArrayList<ActionTask>();
        for(ActionTask p :wfActionTasks){
            if(p==null)
                continue;
            ActionTask newdt = newWfActionTask();
            newdt.copyNotNullProperty(p);
            newObjs.add(newdt);
        }
        //delete
        boolean found = false;
        Set<ActionTask> oldObjs = new HashSet<ActionTask>();
        oldObjs.addAll(getWfActionTasks());

        for(Iterator<ActionTask> it = oldObjs.iterator(); it.hasNext();){
            ActionTask odt = it.next();
            found = false;
            for(ActionTask newdt :newObjs){
                if(odt.getTaskId().equals( newdt.getTaskId())){
                    found = true;
                    break;
                }
            }
            if(! found)
                removeWfActionTask(odt);
        }
        oldObjs.clear();
        //insert
        for(ActionTask newdt :newObjs){
            found = false;
            for(Iterator<ActionTask> it = getWfActionTasks().iterator();
                it.hasNext();){
                ActionTask odt = it.next();
                if(odt.getTaskId().equals( newdt.getTaskId())){
                    odt.copy(newdt);
                    found = true;
                    break;
                }
            }
            if(! found)
                addWfActionTask(newdt);
        }
    }

      public void copy(NodeInstance other){

        this.setNodeInstId(other.getNodeInstId());

        this.flowInstId= other.getFlowInstId();
        this.node.setNodeId(other.getNodeId());
        this.createTime = other.getCreateTime();
        this.lastUpdateTime= other.getLastUpdateTime();
        this.prevNodeInstId = other.getPrevNodeInstId();
        this.nodeState= other.getNodeState();
        this.subFlowInstId = other.getSubFlowInstId();
        this.unitCode = other.getUnitCode();
        this.transPath= other.getTransPath();
        this.taskAssigned = other.getTaskAssigned();
        this.timeLimit = other.getTimeLimit();
        this.runToken = other.getRunToken();
        this.lastUpdateUser = other.getLastUpdateUser();
        this.isTimer = other.getIsTimer();
        this.stageCode = other.getFlowStage();

    }

    public void copyNotNullProperty(NodeInstance other){

    if( other.getNodeInstId() != null)
        this.setNodeInstId(other.getNodeInstId());

        if( other.getFlowInstId() != null)
            this.flowInstId= other.getFlowInstId();
        if( other.getNodeId() != null)
            this.node.setNodeId(other.getNodeId());
        if( other.getCreateTime() != null)
            this.createTime = other.getCreateTime();
        if( other.getLastUpdateTime() != null)
            this.lastUpdateTime= other.getLastUpdateTime();
        if( other.getPrevNodeInstId() != null)
            this.prevNodeInstId = other.getPrevNodeInstId();
        if( other.getNodeState() != null)
            this.nodeState= other.getNodeState();
        if( other.getSubFlowInstId() != null)
            this.subFlowInstId = other.getSubFlowInstId();
        if( other.getUnitCode() != null)
            this.unitCode = other.getUnitCode();
        if( other.getTransPath() != null)
            this.transPath= other.getTransPath();
        if( other.getTaskAssigned() != null)
            this.taskAssigned = other.getTaskAssigned();
       if( other.getTimeLimit() != null)
           this.timeLimit = other.getTimeLimit();
       if( other.getRunToken() != null)
           this.runToken = other.getRunToken();
       if(other.getLastUpdateUser() != null)
           this.lastUpdateUser=other.getLastUpdateUser();
       if(other.getIsTimer() != null)
           this.isTimer = other.getIsTimer();
       if(other.getFlowStage() != null)
           this.stageCode = other.getFlowStage();
       if(other.getRoleType() != null){
           this.roleType = other.getRoleType();
       }
       if(other.getRoleCode() != null){
           this.roleCode = other.getRoleCode();
       }
       if(other.getUserCode() != null){
           this.userCode = other.getUserCode();
       }
       if(other.getNodeId() != null){
           this.nodeId = other.getNodeId();
       }
       if(other.getPromiseTime() != null){
           this.promiseTime = other.getPromiseTime();
       }
    }

    public void clearProperties()
    {
        //this.wfinstid= null;
        this.node.setNodeId(null);
        this.createTime = null;
        this.lastUpdateTime= null;
        this.prevNodeInstId = null;
        this.nodeState= null;
        this.subFlowInstId = null;
        this.unitCode = null;
        this.transPath=  null;
        this.taskAssigned = "D";
        this.timeLimit =  null;
        this.runToken = null;
        this.isTimer = null;
        this.stageCode =null;
    }
    public String getFlowOptName() {
        return flowOptName;
    }
    public void setFlowOptName(String flowOptName) {
        this.flowOptName = flowOptName;
    }
    public String getFlowOptTag() {
        return flowOptTag;
    }
    public void setFlowOptTag(String flowOptTag) {
        this.flowOptTag = flowOptTag;
    }

    /**
     * xz 行政角色  gw 岗位角色 bj 办件角色  en 工作流引擎
     */
    public String getRoleType() {
        return roleType;
    }
    /**
     * xz 行政角色  gw 岗位角色 bj 办件角色  en 工作流引擎
     * @param roleType
     */
    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }
    public String getRoleCode() {
        return roleCode;
    }
    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }
    public String getLastUpdateUser() {
        return lastUpdateUser;
    }
    public void setLastUpdateUser(String lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }

    public String getGrantor() {
        return grantor;
    }
    public void setGrantor(String grantor) {
        this.grantor = grantor;
    }
    /**
     * 不计时N、计时T 、暂停 P
     */
    public String getIsTimer() {
        return isTimer;
    }
    /**
     * 不计时N、计时T  、暂停 P
     */
    public void setIsTimer(String isTimer) {
        this.isTimer = isTimer;
    }
    public String getIsRecycle() {
        return isRecycle;
    }
    public void setIsRecycle(String isRecycle) {
        this.isRecycle = isRecycle;
    }

    public String getNodeOptUrl() {

        return null;
    }
    public List<String> getTrainsUsers() {
        return trainsUsers;
    }
    public void setTrainsUsers(List<String> trainsUsers) {
        this.trainsUsers = trainsUsers;
    }
    public NodeInfo getNode() {
        return node;
    }
    public void setNode(NodeInfo node) {
        this.node = node;
    }


}
