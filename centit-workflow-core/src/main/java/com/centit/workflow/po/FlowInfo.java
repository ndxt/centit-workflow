package com.centit.workflow.po;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.*;

/**
 * create by scaffold
 *
 * @author codefan@hotmail.com
 */
@Data
@Entity
@Table(name = "WF_FLOW_DEFINE")
public class FlowInfo implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    private FlowInfoId cid;

    @Column(name = "FLOW_NAME") //FLOW_NAME
    @Length(max = 120)
    private String flowName; //flowName

    /**
     * N 普通流程，F 自由流程
     */
    @Column(name = "FLOW_CLASS")
    @Length(max = 4)
    private String flowClass;
    // A 草稿 B 正常 C 过期 D 禁用  E 已发布
    public static final String FLOW_STATE_DRAFT = "A";
    public static final String FLOW_STATE_NORMAL = "B";
    public static final String FLOW_STATE_INVALID = "C";
    public static final String FLOW_STATE_FORBIDDEN = "D";
    public static final String FLOW_STATE_PUBLISHED = "E";
    @Column(name = "FLOW_STATE")
    @Length(max = 1)
    private String flowState;

    @Column(name = "FLOW_DESC")
    @Length(max = 500)
    private String flowDesc;

    @JSONField(serialize = false)
    @Column(name = "FLOW_XML_DESC")
    private String flowXmlDesc;

    @Column(name = "FLOW_PUBLISH_DATE")
    private Date flowPublishDate;

    @Column(name = "FIRST_NODE_ID")
    @Length(max = 32)
    private String firstNodeId;

    /**
     * 等同于 wf_opt_info中的 APPLICATION_ID
     */
    @Column(name = "OS_ID")
    @Length(max = 32)
    private String osId;

    @Column(name = "OPT_ID")
    @Length(max = 32)
    private String optId;

    @Column(name = "TIME_LIMIT")
    @Length(max = 20)
    private String timeLimit;

    /**
     * 获取流程超期后处理方式
     * N：仅通知， O:不处理 ，X：挂起，E：终止（流程）
     * A ：调用api
     * @see NodeInfo
     */
    @Column(name = "EXPIRE_OPT")
    @Length(max = 1)
    private String expireOpt;

    @Column(name = "EXPIRE_CALL_API")
    private String expireCallApi;

    @Column(name = "WARNING_PARAM")
    private String warningParam;

    /**
     * 计划发布时间（生效时间），这个字段暂未使用
     */
    @Column(name = "AT_PUBLISH_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date atPublishDate;

    @Column(name = "SOURCE_ID")
    @ApiModelProperty(value = "模板来源")
    @JSONField(serialize = false)
    private String sourceId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = FlowStage.class)
    @JoinColumns({
        @JoinColumn(name = "flowCode", referencedColumnName = "flowCode"),
        @JoinColumn(name = "version", referencedColumnName = "version")
    })
    private List<FlowStage> flowStages;// new ArrayList<WfFlowStage>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = NodeInfo.class)
    @JoinColumns({
        @JoinColumn(name = "flowCode"),
        @JoinColumn(name = "version")
    })
    private List<NodeInfo> nodeList;// new ArrayList<WfNode>();


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = FlowTransition.class)
    @JoinColumns({
        @JoinColumn(name = "flowCode", referencedColumnName = "flowCode"),
        @JoinColumn(name = "version", referencedColumnName = "version")
    })

    private List<FlowTransition> transList;// new ArrayList<WfTransition>();

    // Constructors

    /**
     * default constructor
     */
    public FlowInfo() {
    }

    /**
     * minimal constructor
     */
    public FlowInfo(FlowInfoId id, String wfclass) {
        this.cid = id;
        this.flowClass = wfclass;
    }

    /**
     * full constructor
     */
    public FlowInfo(FlowInfoId id
        , String wfname, String wfclass, String wfstate, String wfdesc, String wfxmldesc, Date wfPubDate,
                    String optid, String timeLimit, String expireOpt, Date atPublishDate) {
        this.cid = id;
        this.flowName = wfname;
        this.flowClass = wfclass;
        this.flowState = wfstate;
        this.flowDesc = wfdesc;
        this.flowXmlDesc = wfxmldesc;
        this.flowPublishDate = wfPubDate;
        this.optId = optid;
        this.timeLimit = timeLimit;
        this.expireOpt = expireOpt;
        this.atPublishDate = atPublishDate;
    }

    public Long getVersion() {
        if (this.cid == null)
            this.cid = new FlowInfoId();
        return this.cid.getVersion();
    }

    public void setVersion(Long version) {
        if (this.cid == null)
            this.cid = new FlowInfoId();
        this.cid.setVersion(version);
    }

    public String getFlowCode() {
        if (this.cid == null)
            this.cid = new FlowInfoId();
        return this.cid.getFlowCode();
    }

    public void setFlowCode(String wfcode) {
        if (this.cid == null)
            this.cid = new FlowInfoId();
        this.cid.setFlowCode(wfcode);
    }

    public List<NodeInfo> getNodeList() {
        if (this.nodeList == null)
            this.nodeList = new ArrayList<>();
        return this.nodeList;
    }

    public List<NodeInfo> listNodesByNodeCode(String nodeCode) {
        List<NodeInfo> nodes = new ArrayList<>();
        if (nodeCode == null || nodeList == null)
            return nodes;
        for (NodeInfo node : nodeList) {
            if (nodeCode.equals(node.getNodeCode()))
                nodes.add(node);
        }
        return nodes;
    }


    public NodeInfo getFlowNodeById(String nodeId) {
        if (this.nodeList == null)
            return null;
        for (NodeInfo nd : nodeList)
            if (nd.getNodeId().equals(nodeId))
                return nd;
        return null;
    }

    public void setNodeList(List<NodeInfo> wfNodes) {
        if (wfNodes == null || wfNodes.size() == 0) {
            this.nodeList = wfNodes;
            return;
        }
        for (NodeInfo nodeInfo : wfNodes) {
            addFlowNode(nodeInfo);
        }
        this.nodeList = wfNodes;
    }

    public void addFlowNode(NodeInfo wfNode) {
        if (this.nodeList == null)
            this.nodeList = new ArrayList<>();
        //wfNode.setFlowDefine(this);
        wfNode.setFlowCode(this.getFlowCode());
        wfNode.setVersion(this.getVersion());
        this.nodeList.add(wfNode);
    }

    public void removeFlowNode(NodeInfo wfNode) {
        if (this.nodeList == null)
            return;
        this.nodeList.remove(wfNode);
    }

    public NodeInfo newFlowNode() {
        NodeInfo res = new NodeInfo();
        res.setFlowDefine(this);
        return res;
    }

    public NodeInfo getFirstNode() {
        if (this.nodeList == null)
            return null;
        if (StringUtils.isNotBlank(this.getFirstNodeId())) {
            NodeInfo node = getFlowNodeById(this.getFirstNodeId());
            if (node != null) {
                return node;
            }
        }
        //这段代码为了兼容老的版本
        for (NodeInfo node : nodeList) {
            if (NodeInfo.NODE_TYPE_FIRST.equals(node.getNodeType()))
                return node;
        }
        return null;
    }

    public List<FlowTransition> getTransList() {
        if (this.transList == null)
            this.transList = new ArrayList<>();
        return this.transList;
    }

    public void setTransList(List<FlowTransition> wfTransitions) {
        if (wfTransitions == null || wfTransitions.size() == 0) {
            this.transList = wfTransitions;
            return;
        }
        for (FlowTransition flowTransition : wfTransitions) {
            addFlowTransition(flowTransition);
        }
        this.transList = wfTransitions;
    }

    public void addFlowTransition(FlowTransition wfTransition) {
        if (this.transList == null)
            this.transList = new ArrayList<>();
        //wfTransition.setFlowDefine(this);
        wfTransition.setFlowCode(this.getFlowCode());
        wfTransition.setVersion(this.getVersion());
        this.transList.add(wfTransition);
    }

    public void removeFlowTransition(FlowTransition wfTransition) {
        if (this.transList == null)
            return;
        this.transList.remove(wfTransition);
    }

    public FlowTransition newFlowTransition() {
        FlowTransition res = new FlowTransition();
        res.setFlowDefine(this);
        return res;
    }

    public void replaceFlowTransitions(List<FlowTransition> wfTransitions) {
        List<FlowTransition> newObjs = new ArrayList<>();
        for (FlowTransition p : wfTransitions) {
            if (p == null)
                continue;
            FlowTransition newdt = newFlowTransition();
            newdt.copyNotNullProperty(p);
            newObjs.add(newdt);
        }
        //delete
        boolean found = false;
        Set<FlowTransition> oldObjs = new HashSet<>();
        oldObjs.addAll(getTransList());

        for (Iterator<FlowTransition> it = oldObjs.iterator(); it.hasNext(); ) {
            FlowTransition odt = it.next();
            found = false;
            for (FlowTransition newdt : newObjs) {
                if (odt.getTransId().equals(newdt.getTransId())) {
                    found = true;
                    break;
                }
            }
            if (!found)
                removeFlowTransition(odt);
        }
        oldObjs.clear();
        //insert
        for (FlowTransition newdt : newObjs) {
            found = false;
            for (Iterator<FlowTransition> it = getTransList().iterator();
                 it.hasNext(); ) {
                FlowTransition odt = it.next();
                if (odt.getTransId().equals(newdt.getTransId())) {
                    odt.copy(newdt);
                    found = true;
                    break;
                }
            }
            if (!found)
                addFlowTransition(newdt);
        }
    }

    public List<FlowStage> getFlowStages() {
        if (this.flowStages == null)
            this.flowStages = new ArrayList<>();
        return flowStages;
    }


    public void setFlowStages(List<FlowStage> flowstages) {
        if (flowstages == null || flowstages.size() == 0) {
            this.flowStages = flowstages;
            return;
        }
        for (FlowStage flowStage : flowstages) {
            addFlowStage(flowStage);
        }
        this.flowStages = flowstages;
    }


    public void addFlowStage(FlowStage wfFlowStage) {
        //wfFlowStage.setFlowDefine(this);
        wfFlowStage.setFlowCode(this.getFlowCode());
        wfFlowStage.setVersion(this.getVersion());

        //wfFlowStage.setVersion(this.getVersion());
        this.getFlowStages().add(wfFlowStage);
    }

    public void removeFlowStage(FlowStage wfFlowStage) {
        this.getFlowStages().remove(wfFlowStage);
    }

    public FlowStage newFlowStage() {
        FlowStage res = new FlowStage();
        res.setFlowDefine(this);
        return res;
    }

    /**
     * 替换子类对象数组，这个函数主要是考虑hibernate中的对象的状态，以避免对象状态不一致的问题
     */
    public void replaceFlowStages(Collection<? extends FlowStage> wfFlowStages) {
        List<FlowStage> newObjs = new ArrayList<FlowStage>();
        for (FlowStage p : wfFlowStages) {
            if (p == null)
                continue;
            FlowStage newdt = new FlowStage();
            newdt.copyNotNullProperty(p);
            newdt.setFlowDefine(this);
            newObjs.add(newdt);
        }
        //delete
        boolean found = false;
        Set<FlowStage> oldObjs = new HashSet<FlowStage>();
        oldObjs.addAll(getFlowStages());

        for (Iterator<FlowStage> it = oldObjs.iterator(); it.hasNext(); ) {
            FlowStage odt = it.next();
            found = false;
            for (FlowStage newdt : newObjs) {
                if (odt.getStageId().equals(newdt.getStageId())) {
                    found = true;
                    break;
                }
            }
            if (!found)
                removeFlowStage(odt);
        }
        oldObjs.clear();
        //insert or update
        for (FlowStage newdt : newObjs) {
            found = false;
            for (Iterator<FlowStage> it = getFlowStages().iterator();
                 it.hasNext(); ) {
                FlowStage odt = it.next();
                if (odt.getStageId().equals(newdt.getStageId())) {
                    odt.copy(newdt);
                    found = true;
                    break;
                }
            }
            if (!found)
                addFlowStage(newdt);
        }
    }


    public void copy(FlowInfo other) {
        this.setVersion(other.getVersion());
        this.setFlowCode(other.getFlowCode());

        this.flowName = other.getFlowName();
        this.flowClass = other.getFlowClass();
        this.flowState = other.getFlowState();
        this.flowDesc = other.getFlowDesc();
        this.flowXmlDesc = other.getFlowXmlDesc();
        this.flowPublishDate = other.getFlowPublishDate();
        this.firstNodeId = other.getFirstNodeId();
        this.osId = other.getOsId();
        this.optId = other.getOptId();
        this.timeLimit = other.getTimeLimit();
        this.expireOpt = other.getExpireOpt();
        this.atPublishDate = other.getAtPublishDate();
        //this.replaceFlowStages(other.getFlowStages());
    }

    public void copyNotNullProperty(FlowInfo other) {

        if (other.getVersion() != null)
            this.setVersion(other.getVersion());
        if (other.getFlowCode() != null)
            this.setFlowCode(other.getFlowCode());
        if (other.getFlowName() != null)
            this.flowName = other.getFlowName();
        if (other.getFlowClass() != null)
            this.flowClass = other.getFlowClass();
        if (other.getFlowState() != null)
            this.flowState = other.getFlowState();
        if (other.getFlowDesc() != null)
            this.flowDesc = other.getFlowDesc();
        if (other.getFlowXmlDesc() != null)
            this.flowXmlDesc = other.getFlowXmlDesc();
        if (other.getFlowPublishDate() != null)
            this.flowPublishDate = other.getFlowPublishDate();
        if (other.getFirstNodeId() != null)
            this.firstNodeId = other.getFirstNodeId();
        if (other.getOsId() != null)
            this.osId = other.getOsId();
        if (other.getOptId() != null)
            this.optId = other.getOptId();
        if (other.getTimeLimit() != null)
            this.timeLimit = other.getTimeLimit();
        if (other.getExpireOpt() != null)
            this.expireOpt = other.getExpireOpt();
        if (other.getAtPublishDate() != null)
            this.atPublishDate = other.getAtPublishDate();
        if (other.getWarningParam()!=null)
            this.warningParam=other.getWarningParam();
        /*if(other.getFlowStages() !=null)
            this.replaceFlowStages(other.getFlowStages());*/
    }

    public void ifNullCopyProperty(FlowInfo other) {
        if (this.getVersion() == null)
            this.setVersion(other.getVersion());
        if (this.getFlowCode() == null)
            this.setFlowCode(other.getFlowCode());
        if (this.getFlowName() == null)
            this.flowName = other.getFlowName();
        if (this.getFlowClass() == null)
            this.flowClass = other.getFlowClass();
        if (this.getFlowState() == null)
            this.flowState = other.getFlowState();
        if (this.getFlowDesc() == null)
            this.flowDesc = other.getFlowDesc();
        if (this.getFlowXmlDesc() == null)
            this.flowXmlDesc = other.getFlowXmlDesc();
        if (this.getFlowPublishDate() == null)
            this.flowPublishDate = other.getFlowPublishDate();
        if (this.getFirstNodeId() == null)
            this.firstNodeId = other.getFirstNodeId();
        if (this.getOsId() == null)
            this.osId = other.getOsId();
        if (this.getOptId() == null)
            this.optId = other.getOptId();
        if (this.getTimeLimit() == null)
            this.timeLimit = other.getTimeLimit();
        if (this.getExpireOpt() == null)
            this.expireOpt = other.getExpireOpt();
        if (this.getAtPublishDate() == null)
            this.atPublishDate = other.getAtPublishDate();
        if (this.getWarningParam()==null)
            this.warningParam=other.getWarningParam();
        /*if(null==this.getFlowStages())
            this.replaceFlowStages(other.getFlowStages());*/
    }

    public void clearProperties() {
        this.setVersion(null);
        this.setFlowCode(null);
        this.flowName = null;
        this.flowClass = null;
        this.flowState = null;
        this.flowDesc = null;
        this.flowXmlDesc = null;
        this.flowPublishDate = null;
        this.osId = null;
        this.optId = null;
        this.firstNodeId = null;
        this.timeLimit = null;
        this.expireOpt = null;
        this.atPublishDate = null;
        this.warningParam=null;
        this.getFlowStages().clear();
    }

}
