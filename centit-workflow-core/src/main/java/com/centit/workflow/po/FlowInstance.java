package com.centit.workflow.po;

import com.alibaba.fastjson.annotation.JSONField;
import com.centit.framework.core.dao.DictionaryMap;
import com.centit.support.common.WorkTimeSpan;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * create by scaffold
 *
 * @author codefan@hotmail.com
 */
@Entity
@Data
@Table(name = "WF_FLOW_INSTANCE")
public class FlowInstance implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "FLOW_INST_ID")
    @ValueGenerator(strategy = GeneratorType.UUID22)
    private String flowInstId;

    /**
     * 流程分组id 可以为空
     **/
    @Column(name = "FLOW_GROUP_ID")
    private String flowGroupId;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "version"),
        @JoinColumn(name = "flowCode")
    })
    private FlowInfo flowDefine;

    /**
     * 框架解析 不到ManyToOne的属性 这儿单独 设置
     */
    @Column(name = "VERSION")
    @NotNull(message = "字段不能为空")
    @Range(max = 9999, message = "版本号不能大于{max}")
    private Long version;

    @Column(name = "FLOW_CODE")
    @NotNull(message = "字段不能为空")
    @Length(max = 32, message = "字段长度不能大于{max}")
    @DictionaryMap(value = "flowCode", fieldName = "flowName")
    private String flowCode;
    /**
     * 等同于 wf_opt_info中的 APPLICATION_ID
     */
    @Column(name = "OS_ID")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private String osId;

    @Column(name = "OPT_ID")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private String optId;

    /**
     * varchar(800)
     */
    @Column(name = "FLOW_OPT_NAME")
    private String flowOptName;

    public FlowInfo getFlowDefine() {
        if (null == flowDefine)
            return this.flowDefine = new FlowInfo();
        return flowDefine;
    }

    public void setFlowDefine(FlowInfo flowDefine) {
        this.flowDefine = flowDefine;
    }

    /**
     * varchar(200)
     */
    @Column(name = "FLOW_OPT_TAG")
    private String flowOptTag;
    @Column(name = "CREATE_TIME")
    @OrderBy(value = "DESC")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @Column(name = "PROMISE_TIME")
    private Long promiseTime;

    @Column(name = "TIME_LIMIT")
    private Long timeLimit;
    /**
     * 流程状态
     * N 正常  C 完成  P 暂停 挂起  F 强行结束  M
     */
    @Column(name = "INST_STATE")
    private String instState;

    @Column(name = "IS_SUB_INST")
    private Boolean isSubInst;

    /**
     * 作为子流程时，其父流程 ID
     */
    @Column(name = "PRE_INST_ID")
    private String preInstId;
    /**
     * 作为子流程时，其父节点 ID
     */
    @Column(name = "PRE_NODE_INST_ID")
    private String preNodeInstId;

    @Column(name = "UNIT_CODE")
    @DictionaryMap(value = "unitCode", fieldName = "unitName")
    private String unitCode;

    @Column(name = "USER_CODE")
    @DictionaryMap(value = "userCode", fieldName = "userName")
    private String userCode;

    @Column(name = "LAST_UPDATE_TIME")
    private Date lastUpdateTime;
    @Column(name = "LAST_UPDATE_USER")
    private String lastUpdateUser;

    //不计时 F、计时T(有期限)、暂停P 忽略(无期限) F
    public static final String FLOW_TIMER_STATE_NOLIMIT = "F";
    public static final String FLOW_TIMER_STATE_RUN = "T";
    public static final String FLOW_TIMER_STATE_SUSPEND = "P";
    @Column(name = "IS_TIMER")
    private String isTimer;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = NodeInstance.class)
    @JoinColumn(name = "flowInstId")
    @JSONField(serialize = false)
    private List<NodeInstance> flowNodeInstances;

    @Transient
    @JSONField(serialize = true)
    private List<NodeInstance> activeNodeList;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = StageInstance.class)
    @JoinColumn(name = "flowInstId")
    @JSONField(serialize = false)
    private List<StageInstance> flowStageInstances;// new ArrayList<WfNodeInstance>();

    @Transient
    private String optName;
    @Transient
    private String flowName;
    @Transient
    private String curStep;

    /**
     * default constructor
     */
    public FlowInstance() {
        this.timeLimit = null;
        this.flowNodeInstances = null;
        this.flowStageInstances = null;
        this.activeNodeList = null;
        this.isSubInst = false;
    }

    /**
     * minimal constructor
     */
    public FlowInstance(String wfinstid, Date createtime) {
        this.timeLimit = null;
        this.flowInstId = wfinstid;
        this.createTime = createtime;
        this.isSubInst = false;
        this.flowNodeInstances = null;
        this.flowStageInstances = null;
        this.activeNodeList = null;
    }

    public boolean checkIsInRunning() {
        return StringUtils.equalsAny(this.getInstState(), "N", "M");
    }

    public String getPromiseTimeStr() {
        if (promiseTime == null)
            return "";
        WorkTimeSpan wts = new WorkTimeSpan();
        wts.fromNumberAsMinute(promiseTime);
        return wts.getTimeSpanDesc();
    }

    public String getTimeLimitStr() {
        if (timeLimit == null)
            return "";
        WorkTimeSpan wts = new WorkTimeSpan();
        wts.fromNumberAsMinute(timeLimit);
        return wts.getTimeSpanDesc();
    }


    public Set<NodeInstance> getActiveNodeInstances() {
        Set<NodeInstance> nodeInstSet = new HashSet<>();
        if (this.flowNodeInstances == null)
            return nodeInstSet;
        for (NodeInstance nodeInst : flowNodeInstances)
            if ("N".equals(nodeInst.getNodeState())
                || "R".equals(nodeInst.getNodeState())
                || "W".equals(nodeInst.getNodeState()))
                nodeInstSet.add(nodeInst);
        return nodeInstSet;
    }

    public List<NodeInstance> getFlowNodeInstances() {
        if (flowNodeInstances == null) {
            this.flowNodeInstances = new ArrayList<>();
            return this.flowNodeInstances;
        }

        this.flowNodeInstances.sort(Comparator.comparing(NodeInstance::getCreateTime));
        return this.flowNodeInstances;
    }


    public void setFlowNodeInstances(List<NodeInstance> flowNodeInstances) {
        this.flowNodeInstances = flowNodeInstances;
    }

    public void addNodeInstance(NodeInstance wfNodeInstance) {
        if (this.flowNodeInstances == null)
            this.flowNodeInstances = new ArrayList<>();
        this.flowNodeInstances.add(wfNodeInstance);
    }

    public void removeNodeInstance(NodeInstance wfNodeInstance) {
        if (this.flowNodeInstances == null)
            return;
        this.flowNodeInstances.remove(wfNodeInstance);
    }

    public NodeInstance newNodeInstance() {
        NodeInstance res = new NodeInstance();
        res.setFlowInstId(this.getFlowInstId());
        return res;
    }

    public NodeInstance getNodeInstanceById(String nodeInstId) {
        if (this.flowNodeInstances == null)
            return null;

        for (NodeInstance nodeInst : flowNodeInstances)
            if (nodeInst.getNodeInstId().equals(nodeInstId))
                return nodeInst;

        return null;
    }

    public List<StageInstance> getFlowStageInstances() {
        if (this.flowStageInstances == null)
            this.flowStageInstances = new ArrayList<>();
        return this.flowStageInstances;
    }

    public List<StageInstance> getStageInstanceList() {
        List<StageInstance> stageList = new ArrayList<>();
        stageList.addAll(getFlowStageInstances());
        return stageList;
    }

    public List<StageInstance> getExpiredStageInstanceList() {
        List<StageInstance> stageList = new ArrayList<>();
        for (StageInstance stage : getFlowStageInstances()) {
            if (stage.getTimeLimit() < 0)
                stageList.add(stage);
        }
        return stageList;
    }

    public void addFlowStageInstance(StageInstance wfStageInstance) {
        if (this.flowStageInstances == null)
            this.flowStageInstances = new ArrayList<>();
        this.flowStageInstances.add(wfStageInstance);
    }

    public void removeFlowStageInstance(StageInstance wfStageInstance) {
        if (this.flowStageInstances == null)
            return;
        this.flowStageInstances.remove(wfStageInstance);
    }

    public StageInstance newFlowStageInstance() {

        StageInstance res = new StageInstance();
        res.setFlowInstId(this.getFlowInstId());
        return res;
    }

    public StageInstance getStageInstanceByCode(String stageCode) {
        if (this.flowStageInstances == null)
            return null;

        for (StageInstance stageInst : this.flowStageInstances)
            if (stageInst.getStageCode().equals(stageCode))
                return stageInst;
        return null;
    }

    public NodeInstance getFirstNodeInstance() {
        if (this.flowNodeInstances == null)
            return null;
        NodeInstance firstNode = null;
        for (NodeInstance nodeInst : flowNodeInstances) {
            if (nodeInst.getPrevNodeInstId() == null) {
                firstNode = nodeInst;
                break;
            }
        }
        return firstNode;
    }

    public List<NodeInstance> getSameLevelNodeInstances(String nodeInstId) {
        NodeInstance nodeInst = this.getNodeInstanceById(nodeInstId);
        if (nodeInst == null)
            return null;
        List<NodeInstance> nodes = new ArrayList<NodeInstance>();
        String thisToken = nodeInst.getRunToken();
        while (true) {
            if (thisToken == null || thisToken.equals(nodeInst.getRunToken()))
                nodes.add(nodeInst);
            nodeInst = getNodeInstanceById(nodeInst.getPrevNodeInstId());
            if (nodeInst == null)
                break;
        }
        return nodes;
    }

    public List<NodeInstance> getRunTraceNodeInstances(String nodeInstId) {
        NodeInstance nodeInst = this.getNodeInstanceById(nodeInstId);
        if (nodeInst == null)
            return null;
        List<NodeInstance> nodes = new ArrayList<NodeInstance>();
        //String thisToken  = nodeInst.getRunToken();
        while (true) {
            //if(thisToken==null || thisToken.equals(nodeInst.getRunToken()))
            nodes.add(nodeInst);
            nodeInst = getNodeInstanceById(nodeInst.getPrevNodeInstId());
            if (nodeInst == null)
                break;
        }
        return nodes;
    }

    /**
     * 获取相同Token的最新节点所属机构
     *
     * @param nodeInst  节点
     * @param thisToken 令牌
     * @return NodeInstance 实例
     */
    public NodeInstance getNearestNode(NodeInstance nodeInst, String thisToken) {
        NodeInstance curNode = nodeInst;
        while (curNode != null) {
            String prevToken = curNode.getRunToken();
            if (thisToken == null || prevToken == null
                || thisToken.equals(prevToken)
                || thisToken.startsWith(prevToken + '.'))
                return curNode;
            if (curNode.getPrevNodeInstId() == null)
                return null;
            curNode = this.getNodeInstanceById(curNode.getPrevNodeInstId());
        }
        return null;
    }

    /**
     * 检查是否有未提交的前置节点
     *
     * @param nodeInstId
     * @return
     */
    public int checkNotCommitPreNodes(String nodeInstId) {
        int notCommit = 0;
        for (NodeInstance nodeInst : flowNodeInstances) {
            if (nodeInst.getNodeState().equals("N") && //没有提交
                nodeInst.getRunToken().endsWith(NodeInstance.RUN_TOKEN_INSERT) && //临时节点
                nodeInst.getPrevNodeInstId().equals(nodeInstId)) //前节点ID
                notCommit++;
        }
        return notCommit;
    }

    /**
     * 获得流程中同一个节点的所有节点实例，
     *
     * @param nodeId
     * @return
     */
    public List<NodeInstance> getAllNodeInstancesByNodeid(String nodeId) {
        List<NodeInstance> sameNodes = new ArrayList<NodeInstance>();
        if (this.flowNodeInstances == null)
            return sameNodes;

        for (NodeInstance ni : flowNodeInstances)
            if (ni.getNodeId().equals(nodeId))
                sameNodes.add(ni);

        return sameNodes;
    }

    public List<NodeInstance> findSameNodeInsts(NodeInstance nodeInst) {
        List<NodeInstance> sameNodes = new ArrayList<NodeInstance>();
        if (nodeInst == null || this.flowNodeInstances == null)
            return sameNodes;
        String nodeId = nodeInst.getNodeId();
        String thisNodeInstId = nodeInst.getNodeInstId();
        String runToken = nodeInst.getRunToken();
        for (NodeInstance ni : flowNodeInstances)
            if (ni.getNodeId().equals(nodeId) && !ni.getNodeInstId().equals(thisNodeInstId)
                && (runToken == null || ni.getRunToken() == null || runToken.equals(ni.getRunToken())
                || runToken.startsWith(ni.getRunToken() + ".") || ni.getRunToken().startsWith(runToken + ".")))
                sameNodes.add(ni);

        return sameNodes;
    }

    /**
     * 查找在同一条运行路径上的相同节点
     */
    public NodeInstance findLastSameNodeInst(String nodeId, NodeInstance nodeInst, String thisNodeInstId) {
        if (this.flowNodeInstances == null)
            return null;

        NodeInstance sameInst = null;
        String runToken = null;
        if (nodeInst != null) {
            runToken = nodeInst.getRunToken();
        }
        for (NodeInstance ni : flowNodeInstances)
            if (ni.getNodeId().equals(nodeId) && !ni.getNodeInstId().equals(thisNodeInstId)
                && (runToken == null || ni.getRunToken() == null || runToken.equals(ni.getRunToken())
                || runToken.startsWith(ni.getRunToken() + ".") || ni.getRunToken().startsWith(runToken + "."))) {

                if (sameInst == null || sameInst.getCreateTime().before(ni.getCreateTime()))//大小于
                    sameInst = ni;
            }
        return sameInst;
    }

    public Set<NodeInstance> findSubNodeInstByToken(String token) {
        Set<NodeInstance> sameNodes = new HashSet<NodeInstance>();
        if (this.flowNodeInstances == null)
            return sameNodes;
        for (NodeInstance nodeInst : flowNodeInstances) {
            String thisToken = nodeInst.getRunToken();
            if (thisToken != null && thisToken.startsWith(token + '.'))
                sameNodes.add(nodeInst);
        }
        return sameNodes;
    }

    /**
     * 找到所有的活动的分支节点
     *
     * @param token
     * @return
     */
    public Set<NodeInstance> findActiveSubNodeInstByToken(String token) {
        Set<NodeInstance> sameNodes = new HashSet<NodeInstance>();
        if (this.flowNodeInstances == null)
            return sameNodes;
        for (NodeInstance nodeInst : flowNodeInstances) {
            String thisToken = nodeInst.getRunToken();
            if (thisToken != null && thisToken.startsWith(token + '.') &&
                nodeInst.checkIsNotCompleted()) {
                sameNodes.add(nodeInst);
            }
        }
        return sameNodes;
    }

    /**
     * 找到所有的活动的分支节点，包括游离节点和临时插入节点
     *
     * @param token
     * @return
     */
    public Set<NodeInstance> findAllActiveSubNodeInstByToken(String token) {
        Set<NodeInstance> sameNodes = new HashSet<>();
        if (this.flowNodeInstances == null)
            return sameNodes;
        for (NodeInstance nodeInst : flowNodeInstances) {
            String thisToken = nodeInst.getTrunkToken();
            if (thisToken != null && thisToken.startsWith(token + '.')
                && nodeInst.checkIsNotCompleted()) {
                sameNodes.add(nodeInst);
            }
        }
        return sameNodes;
    }


    /**
     * 找到汇聚节点所有已经提交的子节点
     * （preNodeInst不为null时，视正在提交中的节点为已办理节点）
     *
     * @param token
     * @param preNodeInst 汇聚节点的父节点实例
     * @return token, NodeInst
     */
    public Map<String, NodeInstance> findSubmitSubNodeInstByToken(String token, NodeInstance preNodeInst) {
        Map<String, NodeInstance> sameNodes = new HashMap<String, NodeInstance>();
        if (token == null || this.flowNodeInstances == null)
            return sameNodes;

        NodeInstance sameInst = null;

        for (NodeInstance ni : flowNodeInstances) {
            if (token.endsWith(ni.getRunToken())) {
                if(sameInst == null || ni.getCreateTime().after(sameInst.getCreateTime())){
                    sameInst = ni;
                }
//                if (sameInst == null)
//                    sameInst = ni;
//                else if (ni.getCreateTime().after(sameInst.getCreateTime()))//大小于
//                    sameInst = ni;
            }
        }
        if (sameInst == null)
            return sameNodes;
        int subg = NodeInstance.calcTokenGeneration(token) + 1;

        for (NodeInstance nodeInst : flowNodeInstances) {
            String thisToken = nodeInst.getTrunkToken();
            if (thisToken != null && thisToken.startsWith(token + '.') &&
                ("C".equals(nodeInst.getNodeState()) || (preNodeInst != null && preNodeInst.getNodeInstId().equals(nodeInst.getNodeInstId()))) &&
                nodeInst.getTokenGeneration() == subg &&
                !nodeInst.getCreateTime().before(sameInst.getCreateTime())) {
                NodeInstance tempInst = sameNodes.get(thisToken);
                if (tempInst == null || tempInst.getCreateTime().before(nodeInst.getCreateTime()))
                    sameNodes.put(thisToken, nodeInst);
            }
        }
        return sameNodes;
    }

    /**
     * 找到汇聚节点所有已经提交的子节点
     * （preNodeInst不为null时，视正在提交中的节点为已办理节点）
     *
     * @param token
     * @param preNodeInst 汇聚节点的父节点实例
     * @return Nodeid
     */
    public Set<String> calcSubmitSubNodeIdByToken(String token, NodeInstance preNodeInst) {
        Map<String, NodeInstance> subNodes = findSubmitSubNodeInstByToken(token, preNodeInst);
        Set<String> subTokens = new HashSet<String>();
        for (Map.Entry<String, NodeInstance> ent : subNodes.entrySet())
            subTokens.add(ent.getValue().getNodeId());
        return subTokens;
    }

    /**
     * 找到汇聚节点所有未提交的子节点
     *
     * @param token 节点令牌
     * @return subToken 子节点令牌
     */
    public Set<String> calcNoSubmitSubNodeTokensInstByToken(String token) {
        Set<NodeInstance> sameNodes = findAllActiveSubNodeInstByToken(token);
        Set<String> subTokens = new HashSet<>();

        int subg = NodeInstance.calcTokenGeneration(token) + 1;
        for (NodeInstance nodeInst : sameNodes) {
            String thisToken = nodeInst.getSuperGenerationToken(subg);
            subTokens.add(thisToken);
        }
        return subTokens;
    }

    public NodeInstance getPareNodeInst(String thisNodeInstId) {
        NodeInstance nodeInst = getNodeInstanceById(thisNodeInstId);
        if (nodeInst == null)
            return null;
        String thisToken = nodeInst.getRunToken();

        NodeInstance pareNode = null;
        for (NodeInstance ni : flowNodeInstances) {
            //一开始写的是String tempToken = ni.getTrunkToken(); 不太明白
            String tempToken = ni.getRunToken();
            if (thisToken.equals(tempToken) && "C".equals(ni.getNodeState())
                && ni.getCreateTime().before(nodeInst.getCreateTime())) {//大小于
                if (pareNode == null || pareNode.getCreateTime().before(ni.getCreateTime()))//大小于
                    pareNode = ni;
            }
        }
        return pareNode;
        /*while(true){
            WfNodeInstance prevNodeInst = getNodeInstanceById( nodeInst.getPrevNodeInstId());
            if(prevNodeInst==null)
                return null;

            String prevToken = prevNodeInst.getRunToken();
            if( (thisToken==null || prevToken==null)
                    || thisToken.equals(prevToken)
                    || thisToken.startsWith(prevToken+'.'))

                return prevNodeInst;

            nodeInst = prevNodeInst;
        }*/
    }

    public void replaceFlowStageInstances(List<StageInstance> wfStageInstances) {
        List<StageInstance> newObjs = new ArrayList<StageInstance>();
        for (StageInstance p : wfStageInstances) {
            if (p == null)
                continue;
            StageInstance newdt = newFlowStageInstance();
            newdt.copyNotNullProperty(p);
            newObjs.add(newdt);
        }
        //delete
        boolean found = false;
        Set<StageInstance> oldObjs = new HashSet<StageInstance>();
        oldObjs.addAll(getFlowStageInstances());

        for (Iterator<StageInstance> it = oldObjs.iterator(); it.hasNext(); ) {
            StageInstance odt = it.next();
            found = false;
            for (StageInstance newdt : newObjs) {
                if (odt.getCid().equals(newdt.getCid())) {
                    found = true;
                    break;
                }
            }
            if (!found)
                removeFlowStageInstance(odt);
        }
        oldObjs.clear();
        //insert
        for (StageInstance newdt : newObjs) {
            found = false;
            for (Iterator<StageInstance> it = getFlowStageInstances().iterator();
                 it.hasNext(); ) {
                StageInstance odt = it.next();
                if (odt.getCid().equals(newdt.getCid())) {
                    odt.copy(newdt);
                    found = true;
                    break;
                }
            }
            if (!found)
                addFlowStageInstance(newdt);
        }
    }

    public void replaceFlowNodeInstances(List<NodeInstance> wfNodeInstances) {
        List<NodeInstance> newObjs = new ArrayList<NodeInstance>();
        for (NodeInstance p : wfNodeInstances) {
            if (p == null)
                continue;
            NodeInstance newdt = newNodeInstance();
            newdt.copyNotNullProperty(p);
            newObjs.add(newdt);
        }
        //delete
        boolean found = false;
        Set<NodeInstance> oldObjs = new HashSet<>();
        oldObjs.addAll(getFlowNodeInstances());

        for (Iterator<NodeInstance> it = oldObjs.iterator(); it.hasNext(); ) {
            NodeInstance odt = it.next();
            found = false;
            for (NodeInstance newdt : newObjs) {
                if (odt.getNodeInstId().equals(newdt.getNodeInstId())) {
                    found = true;
                    break;
                }
            }
            if (!found)
                removeNodeInstance(odt);
        }
        oldObjs.clear();
        //insert
        for (NodeInstance newdt : newObjs) {
            found = false;
            for (Iterator<NodeInstance> it = getFlowNodeInstances().iterator();
                 it.hasNext(); ) {
                NodeInstance odt = it.next();
                if (odt.getNodeInstId().equals(newdt.getNodeInstId())) {
                    odt.copy(newdt);
                    found = true;
                    break;
                }
            }
            if (!found)
                addNodeInstance(newdt);
        }
    }

    public void copy(FlowInstance other) {
        this.setFlowInstId(other.getFlowInstId());
        this.setVersion(other.getVersion());
        this.setFlowCode(other.getFlowCode());
        this.flowOptName = other.getFlowOptName();
        this.flowOptTag = other.getFlowOptTag();
        this.createTime = other.getCreateTime();
        this.instState = other.getInstState();
        this.isSubInst = other.getIsSubInst();
        this.preInstId = other.getPreInstId();
        this.preNodeInstId = other.getPreNodeInstId();

        this.setUnitCode(other.getUnitCode());
        this.userCode = other.getUserCode();
        this.osId = other.getOsId();
        this.optId = other.getOptId();
        this.promiseTime = other.getPromiseTime();
        this.timeLimit = other.getTimeLimit();
        this.flowNodeInstances = other.getFlowNodeInstances();
        this.flowStageInstances = other.getFlowStageInstances();
        this.lastUpdateTime = other.getLastUpdateTime();
        this.lastUpdateUser = other.getLastUpdateUser();
        this.isTimer = other.getIsTimer();
    }

    public void copyNotNullProperty(FlowInstance other) {

        if (other.getFlowInstId() != null)
            this.setFlowInstId(other.getFlowInstId());

        if (other.getVersion() != null)
            this.setVersion(other.getVersion());
        if (other.getFlowCode() != null)
            this.setFlowCode(other.getFlowCode());
        if (other.getFlowOptName() != null)
            this.flowOptName = other.getFlowOptName();
        if (other.getFlowOptTag() != null)
            this.flowOptTag = other.getFlowOptTag();
        if (other.getOsId() != null)
            this.osId = other.getOsId();
        if (other.getOptId() != null)
            this.optId = other.getOptId();
        if (other.getCreateTime() != null)
            this.createTime = other.getCreateTime();
        if (other.getInstState() != null)
            this.instState = other.getInstState();
        if (other.getIsSubInst() != null)
            this.isSubInst = other.getIsSubInst();
        if (other.getPreInstId() != null)
            this.preInstId = other.getPreInstId();
        if (other.getPreNodeInstId() != null)
            this.preNodeInstId = other.getPreNodeInstId();
        if (other.getUnitCode() != null)
            this.setUnitCode(other.getUnitCode());
        if (other.getUserCode() != null)
            this.userCode = other.getUserCode();

        if (other.getPromiseTime() != null)
            this.promiseTime = other.getPromiseTime();
        if (other.getTimeLimit() != null)
            this.timeLimit = other.getTimeLimit();
        if (other.getLastUpdateTime() != null)
            this.lastUpdateTime = other.getLastUpdateTime();
        if (other.getLastUpdateUser() != null)
            this.lastUpdateUser = other.getLastUpdateUser();
        if (other.getIsTimer() != null)
            this.isTimer = other.getIsTimer();

        this.replaceFlowNodeInstances(other.getFlowNodeInstances());
        this.replaceFlowStageInstances(other.getFlowStageInstances());

    }

    public void clearProperties() {
        //this.wfinstid= null;
        this.setVersion(null);
        this.setFlowCode(null);
        this.flowOptName = null;
        this.flowOptTag = null;
        this.createTime = null;
        this.instState = null;
        this.isSubInst = null;
        this.preInstId = null;
        this.preNodeInstId = null;
        this.setUnitCode(null);
        this.userCode = null;
        this.timeLimit = null;
        this.promiseTime = null;
        this.flowNodeInstances = null;
        this.flowStageInstances = null;
        this.lastUpdateTime = null;
        this.lastUpdateUser = null;
        this.isTimer = null;
    }
}
