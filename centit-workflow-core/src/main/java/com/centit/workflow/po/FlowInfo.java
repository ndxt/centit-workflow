package com.centit.workflow.po;

import com.alibaba.fastjson.annotation.JSONField;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.*;

/**
 * create by scaffold
 * @author codefan@hotmail.com
 */ 
@Entity
@Table(name = "WF_FLOW_DEFINE")
public class FlowInfo implements java.io.Serializable {
    private static final long serialVersionUID =  1L;
    @EmbeddedId
    private FlowInfoId cid;

    @Column(name = "FLOW_NAME") //FLOW_NAME
    @Length(max = 120, message = "字段长度不能大于{max}")
    private String flowName; //flowName
    
    /**
     * N 普通流程，F 自由流程
     */
    @Column(name = "FLOW_CLASS")
    @Length(max = 4, message = "字段长度不能大于{max}")
    private String flowClass;
    
    @Column(name = "FLOW_STATE")
    @Length(max = 1, message = "字段长度不能大于{max}")
    private String flowState;
    
    @Column(name = "FLOW_DESC")
    @Length(max = 500, message = "字段长度不能大于{max}")
    private String flowDesc;
    
    @Column(name = "FLOW_XML_DESC")
    private String flowXmlDesc;
    
    @Column(name = "FLOW_PUBLISH_DATE")
    private Date flowPublishDate;

    @Column(name = "OS_ID")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private String osId;

    @Column(name = "OPT_ID")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private String optId;
    
    @Column(name = "TIME_LIMIT")
    @Length(max = 20, message = "字段长度不能大于{max}")
    private String  timeLimit;
    
    /**
     *  获取流程超期后处理方式
     * N：通知， O:不处理 ，X：挂起，E：终止（流程）
     */
    @Column(name = "EXPIRE_OPT")
    @Length(max = 1, message = "字段长度不能大于{max}")
    private String  expireOpt;
    
    @Column(name = "AT_PUBLISH_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date  atPublishDate;        

    @OneToMany(mappedBy = "flowDefine",  cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<FlowStage> flowStages;// new ArrayList<WfFlowStage>();
    @OneToMany(mappedBy = "flowDefine",  cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Set<NodeInfo> flowNodes;// new ArrayList<WfNode>();
    @OneToMany(mappedBy = "flowDefine", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Set<FlowTransition> flowTransitions;// new ArrayList<WfTransition>();
    
    // Constructors
    /** default constructor */
    public FlowInfo() {
    }
    /** minimal constructor */
    public FlowInfo(FlowInfoId id, String  wfclass) {
        this.cid = id; 
        this.flowClass = wfclass;
    }

/** full constructor */
    public FlowInfo(FlowInfoId id
    , String  wfname, String  wfclass, String  wfstate, String  wfdesc, String  wfxmldesc, Date  wfPubDate,
                    String  optid, String timeLimit, String expireOpt, Date atPublishDate) {
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

    public FlowInfoId getCid() {
        return this.cid;
    }
    
    public void setCid(FlowInfoId id) {
        this.cid = id;
    }
  
    public Long getVersion() {
        if(this.cid==null)
            this.cid = new FlowInfoId();
        return this.cid.getVersion();
    }
    
    public void setVersion(Long version) {
        if(this.cid==null)
            this.cid = new FlowInfoId();
        this.cid.setVersion(version);
    }
  
    public String getFlowCode() {
        if(this.cid==null)
            this.cid = new FlowInfoId();
        return this.cid.getFlowCode();
    }
    
    public void setFlowCode(String wfcode) {
        if(this.cid==null)
            this.cid = new FlowInfoId();
        this.cid.setFlowCode(wfcode);
    }

    // Property accessors      
    public String getFlowName() {
        return this.flowName;
    }
    
    public void setFlowName(String wfname) {
        this.flowName = wfname;
    }
  
    /**
     * 
     * N 普通流程，F 自由流程
     */
    public String getFlowClass() {
        return this.flowClass;
    }

    public void setFlowClass(String flowClass) {
        this.flowClass = flowClass;
    }
  
    /**
     * A 草稿  E 已发布 (A,E仅对0版本有效) B 正常 C 过期 D 禁用
     * @return
     */
    public String getFlowState() {
        return this.flowState;
    }
    
    
    /**
     * A 草稿 B 正常 C 过期 D 禁用
     * @param wfstate
     */
    public void setFlowState(String wfstate) {
        this.flowState = wfstate;
    }
  
    public String getFlowDesc() {
        return this.flowDesc;
    }
    
    public void setFlowDesc(String wfdesc) {
        this.flowDesc = wfdesc;
    }
  
    @JSONField(serialize=false)
    public String getFlowXmlDesc() {
        return this.flowXmlDesc;
    }
    
    public void setFlowXmlDesc(String wfxmldesc) {
        this.flowXmlDesc = wfxmldesc;
    }

    public Date getFlowPublishDate() {
        return flowPublishDate;
    }

    public void setFlowPublishDate(Date flowPublishDate) {
        this.flowPublishDate = flowPublishDate;
    }

    public String getOptId() {
        return this.optId;
    }
    
    public void setOptId(String optid) {
        this.optId = optid;
    }
    
    public String getTimeLimit() {
        return timeLimit;
    }
    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getExpireOpt() {
        return expireOpt;
    }
    public void setExpireOpt(String expireOpt) {
        this.expireOpt = expireOpt;
    }
    public Date getAtPublishDate() {
        return atPublishDate;
    }
    public void setAtPublishDate(Date atPublishDate) {
        this.atPublishDate = atPublishDate;
    }
    public Set<NodeInfo> getFlowNodes(){
        if(this.flowNodes ==null)
            this.flowNodes = new HashSet<NodeInfo>();
        return this.flowNodes;
    }

    public Set<NodeInfo> listNodesByNodeCode(String nodeCode){
        Set<NodeInfo> nodes = new HashSet<NodeInfo>();
        if(nodeCode == null || flowNodes ==null)
            return nodes;
        for(NodeInfo node : flowNodes){
            if(nodeCode.equals( node.getNodeCode()))
                nodes.add(node);
        }
        return nodes;
    }
    
    
    public NodeInfo getFlowNodeById(long nodeId){
        if(this.flowNodes ==null)
            return null;
        for(NodeInfo nd : flowNodes)
            if(nd.getNodeId().equals(nodeId))
                return nd;
        return null;
    }

    public void setFlowNodes(Set<NodeInfo> wfNodes) {
        this.flowNodes = wfNodes;
    }   

    public void addFlowNode(NodeInfo wfNode ){
        if (this.flowNodes ==null)
            this.flowNodes = new HashSet<NodeInfo>();
        wfNode.setFlowDefine(this);
        this.flowNodes.add(wfNode);
    }
    
    public void removeFlowNode(NodeInfo wfNode ){
        if (this.flowNodes ==null)
            return;
        this.flowNodes.remove(wfNode);
    }
    
    public NodeInfo newFlowNode(){
        NodeInfo res = new NodeInfo();
        res.setFlowDefine(this);
        return res;
    }
    
    public NodeInfo getFirstNode(){
        if (this.flowNodes ==null)
            return null;
        for(NodeInfo node : flowNodes){
            if("B".equals(node.getNodeType()))
                return node;
        }
        return null;
    }

    public Set<FlowTransition> getFlowTransitions(){
        if(this.flowTransitions ==null)
            this.flowTransitions = new HashSet<FlowTransition>();
        return this.flowTransitions;
    }

    public void setFlowTransitions(Set<FlowTransition> wfTransitions) {
        this.flowTransitions = wfTransitions;
    }   

    public void addFlowTransition(FlowTransition wfTransition ){
        if (this.flowTransitions ==null)
            this.flowTransitions = new HashSet<FlowTransition>();
        wfTransition.setFlowDefine(this);
        this.flowTransitions.add(wfTransition);
    }
    
    public void removeFlowTransition(FlowTransition wfTransition ){
        if (this.flowTransitions ==null)
            return;
        this.flowTransitions.remove(wfTransition);
    }
    
    public FlowTransition newFlowTransition(){
        FlowTransition res = new FlowTransition();
        res.setFlowDefine(this);
        return res;
    }
    
    public void replaceFlowTransitions(List<FlowTransition> wfTransitions) {
        List<FlowTransition> newObjs = new ArrayList<FlowTransition>();
        for(FlowTransition p :wfTransitions){
            if(p==null)
                continue;
            FlowTransition newdt = newFlowTransition();
            newdt.copyNotNullProperty(p);
            newObjs.add(newdt);
        }
        //delete
        boolean found = false;
        Set<FlowTransition> oldObjs = new HashSet<FlowTransition>();
        oldObjs.addAll(getFlowTransitions());
        
        for(Iterator<FlowTransition> it = oldObjs.iterator(); it.hasNext();){
            FlowTransition odt = it.next();
            found = false;
            for(FlowTransition newdt :newObjs){
                if(odt.getTransid().equals( newdt.getTransid())){
                    found = true;
                    break;
                }
            }
            if(! found)
                removeFlowTransition(odt);
        }
        oldObjs.clear();
        //insert 
        for(FlowTransition newdt :newObjs){
            found = false;
            for(Iterator<FlowTransition> it = getFlowTransitions().iterator();
                it.hasNext();){
                FlowTransition odt = it.next();
                if(odt.getTransid().equals( newdt.getTransid())){
                    odt.copy(newdt);
                    found = true;
                    break;
                }
            }
            if(! found)
                addFlowTransition(newdt);
        }   
    }   

    @JSONField(serialize=false)
    public Set<FlowStage> getFlowStagesSet(){
        Set<FlowStage> flowStages = new HashSet<FlowStage>();
        if(this.flowStages !=null){
            for(FlowStage fs: this.flowStages)
                flowStages.add(fs);
        }
        return flowStages;
    }
    
    public List<FlowStage> getFlowStages(){
        if(this.flowStages ==null)
            this.flowStages = new ArrayList<FlowStage>();
        return flowStages;
    }
    
    public void setFlowStages(List<FlowStage> flowstages){
        this.flowStages =flowstages;
    }
    
 
    public void addFlowStage(FlowStage wfFlowStage ){
        wfFlowStage.setFlowDefine(this);
        //wfFlowStage.setVersion(this.getVersion());
        this.getFlowStages().add(wfFlowStage);
    }
    
    public void removeFlowStage(FlowStage wfFlowStage ){
        this.getFlowStages().remove(wfFlowStage);
    }
    
    public FlowStage newFlowStage(){
        FlowStage res = new FlowStage();
        res.setFlowDefine(this);
        return res;
    }

    public String getOsId() {
        return osId;
    }

    public void setOsId(String osId) {
        this.osId = osId;
    }

    /**
     * 替换子类对象数组，这个函数主要是考虑hibernate中的对象的状态，以避免对象状态不一致的问题
     * 
     */
    public void replaceFlowStages(Collection<? extends FlowStage> wfFlowStages) {
        List<FlowStage> newObjs = new ArrayList<FlowStage>();
        for(FlowStage p :wfFlowStages){
            if(p==null)
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
        
        for(Iterator<FlowStage> it = oldObjs.iterator(); it.hasNext();){
            FlowStage odt = it.next();
            found = false;
            for(FlowStage newdt :newObjs){
                if(odt.getStageId().equals( newdt.getStageId())){
                    found = true;
                    break;
                }
            }
            if(! found)
                removeFlowStage(odt);
        }
        oldObjs.clear();
        //insert or update
        for(FlowStage newdt :newObjs){
            found = false;
            for(Iterator<FlowStage> it = getFlowStages().iterator();
                it.hasNext();){
                FlowStage odt = it.next();
                if(odt.getStageId().equals( newdt.getStageId())){
                    odt.copy(newdt);
                    found = true;
                    break;
                }
            }
            if(! found)
                addFlowStage(newdt);
        }   
    }


    
    public void copy(FlowInfo other){
  
        this.setVersion(other.getVersion());  
        this.setFlowCode(other.getFlowCode());
  
        this.flowName = other.getFlowName();
        this.flowClass = other.getFlowClass();
        this.flowState = other.getFlowState();
        this.flowDesc = other.getFlowDesc();
        this.flowXmlDesc = other.getFlowXmlDesc();
        this.flowPublishDate = other.getFlowPublishDate();
        this.optId = other.getOptId();
        this.timeLimit = other.getTimeLimit();
        this.expireOpt = other.getExpireOpt();
        this.atPublishDate = other.getAtPublishDate();
        this.osId = other.getOsId();
        //this.replaceFlowStages(other.getFlowStages());
    }
    
    public void copyNotNullProperty(FlowInfo other){
  
        if( other.getVersion() != null)
            this.setVersion(other.getVersion());  
        if( other.getFlowCode() != null)
            this.setFlowCode(other.getFlowCode());      
        if( other.getFlowName() != null)
            this.flowName = other.getFlowName();
        if( other.getFlowClass() != null)
            this.flowClass = other.getFlowClass();
        if( other.getFlowState() != null)
            this.flowState = other.getFlowState();
        if( other.getFlowDesc() != null)
            this.flowDesc = other.getFlowDesc();
        if( other.getFlowXmlDesc() != null)
            this.flowXmlDesc = other.getFlowXmlDesc();
        if( other.getFlowPublishDate() != null)
            this.flowPublishDate = other.getFlowPublishDate();
        if( other.getOptId() != null)
            this.optId = other.getOptId();
        if( other.getTimeLimit() != null)
            this.timeLimit = other.getTimeLimit();
        if (other.getExpireOpt() != null) 
            this.expireOpt = other.getExpireOpt();
        if(other.getAtPublishDate() != null)
            this.atPublishDate = other.getAtPublishDate();
        if (other.getOsId()!=null)
            this.osId = other.getOsId();
        /*if(other.getFlowStages() !=null)
            this.replaceFlowStages(other.getFlowStages());*/
    }
    public void ifNullCopyProperty(FlowInfo other){
        
        if(this.getVersion() == null)
            this.setVersion(other.getVersion());  
        if( this.getFlowCode() == null)
            this.setFlowCode(other.getFlowCode());
      
            if( this.getFlowName() == null)
                this.flowName = other.getFlowName();
            if( this.getFlowClass() == null)
                this.flowClass = other.getFlowClass();
            if( this.getFlowState() == null)
                this.flowState = other.getFlowState();
            if( this.getFlowDesc() == null)
                this.flowDesc = other.getFlowDesc();
            if( this.getFlowXmlDesc() == null)
                this.flowXmlDesc = other.getFlowXmlDesc();
            if( this.getFlowPublishDate() == null)
                this.flowPublishDate = other.getFlowPublishDate();
            if (this.getOsId() ==null)
                this.osId = other.getOsId();
            if( this.getOptId() == null)
                this.optId = other.getOptId();
            if( this.getTimeLimit() == null)
                this.timeLimit = other.getTimeLimit();
            if (this.getExpireOpt() == null) 
                this.expireOpt = other.getExpireOpt();
            if(this.getAtPublishDate() == null)
                this.atPublishDate = other.getAtPublishDate();
            /*if(null==this.getFlowStages())
                this.replaceFlowStages(other.getFlowStages());*/
        }

    public void clearProperties(){
        this.setVersion(null);  
        this.setFlowCode(null);  
        this.flowName = null;
        this.flowClass = null;
        this.flowState = null;
        this.flowDesc =null;
        this.flowXmlDesc = null;
        this.flowPublishDate =null;
        this.osId = null;
        this.optId = null;
        this.timeLimit = null;
        this.expireOpt = null;
        this.atPublishDate = null;
        this.getFlowStages().clear();
    }

}
