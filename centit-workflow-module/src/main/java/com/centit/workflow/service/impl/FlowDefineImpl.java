package com.centit.workflow.service.impl;

import com.centit.support.database.utils.PageDesc;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.StringRegularOpt;
import com.centit.support.network.HtmlFormUtils;
import com.centit.support.xml.XmlUtils;
import com.centit.workflow.dao.FlowInfoDao;
import com.centit.workflow.dao.FlowTeamRoleDao;
import com.centit.workflow.dao.NodeInfoDao;
import com.centit.workflow.po.*;
import com.centit.workflow.service.FlowDefine;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;

@Service
public class FlowDefineImpl implements FlowDefine, Serializable {

    private static final long serialVersionUID = 1L;
    @Resource
    private FlowInfoDao flowDefineDao;
    @Resource
    private NodeInfoDao flowNodeDao;

    @Resource
    private FlowTeamRoleDao flowTeamRoleDao;
    
    private static Logger logger = LoggerFactory.getLogger(FlowDefineImpl.class);
    public static final String BEGINNODETAG="begin";
    public static final String ENDNODETAG="end";

    private class FlowDataDetail{
    //这个Map放在这儿有一定的耦合性 重构成一个类
        public Map<String ,Long> nodeTagToId;
        public Map<String ,Long> transTagToId;
        public long beginNodeId;
        public long firstNodeId;

        public FlowDataDetail(){
            nodeTagToId = new HashMap<String ,Long>();
            transTagToId = new HashMap<String ,Long>(); 
        }
    }

    public FlowDefineImpl()
    {

    }

    public void setFlowNodeDao(NodeInfoDao flowNodeDao) {
        this.flowNodeDao = flowNodeDao;
    }

    public void setFlowDefineDao(FlowInfoDao baseDao) {
        this.flowDefineDao = baseDao;
    }

    @Override
    @Transactional
    public List<FlowInfo> getFlowsByOptId(String optId) {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("optId", optId);
        
        List<FlowInfo> flows = flowDefineDao.getAllLastVertionFlows(filterMap);
        return new ArrayList<>(
                flows == null ? new ArrayList<>() : flows);
    }
    
    private static String getXmlNodeAttrAsStr(Element xNode,String  attrName){
        Attribute nameAttr = xNode.attribute(attrName);
        if(nameAttr != null){
            return HtmlFormUtils.htmlString(
                    nameAttr.getValue());
        }
        return null;
    }

    /**
     * 获取流程节点信息
     * 节点XML如下：
     *   <Nodes>
                <Node>
                    <!--nodetype  节点类别 A:开始 B:首节点 C:一般 D:分支 E:汇聚  R:游离分支  F:结束 -->
                    <!--OptType 操作类别 A:一般 B:抢先机制 C:多人操作  D:子流程 -->
                    <BaseProperties id="1" name="开始" nodetype="A" desc="开始节点描述"
                      opttype="A" optcode="" roletype="" rolecode="" />
                    <VMLProperties shapetype="Oval" width="40" height="40"
                        x="60" y="160" textWeight="9pt" strokeWeight="1" zIndex="1" />
                </Node>
              </Nodes>
     *
     * @param nodeList
     * @param flowDef
     * @return wfSet
     */
    private Set<NodeInfo> mapWfNodeSet(List<Node> nodeList, FlowInfo flowDef, FlowDataDetail flowData){
        flowData.nodeTagToId.clear();
        Set<NodeInfo> wfSet = new HashSet<NodeInfo>();

        if(nodeList == null || nodeList.size() == 0){
            return null;
        }
        long thisNodeId;
        for(Node tmpNode : nodeList){
            Element baseNode = (Element) tmpNode.selectSingleNode("BaseProperties");
            String sId = getXmlNodeAttrAsStr(baseNode,"id");
            if(sId == null)
                continue;
            thisNodeId = flowDefineDao.getNextNodeId();
            //这个对应关系 留给下面的 getWfTransitionSet 使用，有一定的耦合性
            flowData.nodeTagToId.put(sId , thisNodeId);

        }

        for(Node tmpNode : nodeList){

            Element baseNode = (Element) tmpNode.selectSingleNode("BaseProperties");
            String sId = getXmlNodeAttrAsStr(baseNode,"id");
            if(sId == null)
                continue;
            NodeInfo wfNode = flowDef.newFlowNode();
            thisNodeId =  flowData.nodeTagToId.get(sId); //获取节点ID

            sId = sId.toLowerCase();
            if(sId.equals(BEGINNODETAG)){
                wfNode.setNodeType("A");
                flowData.beginNodeId = thisNodeId;
            }else if(sId.equals(ENDNODETAG)){
                wfNode.setNodeType("F");
            }else{
                wfNode.setNodeType("C");
            }

            wfNode.setNodeId(thisNodeId);
            wfNode.setNodeName(getXmlNodeAttrAsStr(baseNode,"name"));
            //获取节点类型
            if(!StringRegularOpt.isNvl(
                    getXmlNodeAttrAsStr(baseNode,"flowphase"))){
                wfNode.setStageCode(getXmlNodeAttrAsStr(baseNode,"flowphase"));
            }
            wfNode.setNodeType(getXmlNodeAttrAsStr(baseNode,"nodetype"));
            wfNode.setNodeCode(getXmlNodeAttrAsStr(baseNode, "nodecode"));
            wfNode.setRiskinfo(getXmlNodeAttrAsStr(baseNode, "riskinfo"));
            wfNode.setNodeDesc(getXmlNodeAttrAsStr(baseNode,"desc"));
            wfNode.setOptType(getXmlNodeAttrAsStr(baseNode,"opttype"));
            wfNode.setOptCode(getXmlNodeAttrAsStr(baseNode,"optcode"));
            wfNode.setOptBean(getXmlNodeAttrAsStr(baseNode,"optbean"));
            
            String optparam = getXmlNodeAttrAsStr(baseNode,"optparam");
            
            if(optparam !=null && optparam.indexOf("&amp;") > 0){
                optparam = optparam.replaceAll("&amp;","&");
            }
            
            wfNode.setOptParam(optparam);
            
            wfNode.setRoleType(getXmlNodeAttrAsStr(baseNode,"roletype"));
            wfNode.setRoleCode(getXmlNodeAttrAsStr(baseNode,"rolecode"));
            wfNode.setLimitType(getXmlNodeAttrAsStr(baseNode,"timeLimitType"));
            wfNode.setIsTrunkLine(getXmlNodeAttrAsStr(baseNode,"isTrunkLine"));
            String timelimit = getXmlNodeAttrAsStr(baseNode,"timeLimit");
            if(timelimit != null && timelimit.length() > 0){
                wfNode.setTimeLimit( getXmlNodeAttrAsStr(baseNode,"timeLimit"));
            }
            wfNode.setInheritType(getXmlNodeAttrAsStr(baseNode,"inheritType"));
            wfNode.setInheritNodeCode(getXmlNodeAttrAsStr(baseNode,"inheritNodeCode"));
            wfNode.setExpireOpt(getXmlNodeAttrAsStr(baseNode,"expireopt"));
            wfNode.setUnitExp(getXmlNodeAttrAsStr(baseNode,"unitexp"));
            wfNode.setPowerExp(getXmlNodeAttrAsStr(baseNode,"powerexp"));
            wfNode.setSubFlowCode(getXmlNodeAttrAsStr(baseNode,"subwfcode"));
            wfNode.setIsAccountTime(getXmlNodeAttrAsStr(baseNode,"isaccounttime"));
            
            //codefan@sina.com 2015-4-8 添加路由相关属性
            wfNode.setRouterType(getXmlNodeAttrAsStr(baseNode,"routertype"));
            wfNode.setMultiInstType(getXmlNodeAttrAsStr(baseNode,"multiinsttype"));
            wfNode.setMultiInstParam(getXmlNodeAttrAsStr(baseNode,"multiinstparam"));
            wfNode.setConvergeType(getXmlNodeAttrAsStr(baseNode,"convergetype"));
            wfNode.setConvergeParam(getXmlNodeAttrAsStr(baseNode,"convergeparam"));
            wfNode.setWarningRule(getXmlNodeAttrAsStr(baseNode,"warningrule"));
            wfNode.setWarningParam(getXmlNodeAttrAsStr(baseNode,"warningparam"));
            
            wfSet.add(wfNode);
        }
        return wfSet;
    }
     /** 流程流转路径定义
     * 路径节点信息如下：
     *    <Transitions>
            <Transition>
                <BaseProperties id="1" name="流程开始" from="1"
                    to="3" cond="" desc="" />
                <VMLProperties points="75pt,135pt,135pt,135pt"
                    fromRelX="1" fromRelY="0.5" toRelX="0" toRelY="0.5" shapetype="PolyLine"
                    startArrow="none" endArrow="Classic" strokeWeight="1" zIndex="0" />
                <LabelProperties id="labstep0" width="60" height="20px"
                    x="119px" y="186px" />
            </Transition>
        </Transitions>
    * 
     * @param transList
     * @param flowDef
     * @return wfTranSet
     */
    private Set<FlowTransition> getWfTransitionSet(List<Node> transList, FlowInfo flowDef, FlowDataDetail flowData){
        Set<FlowTransition> wfTranSet = new HashSet<FlowTransition>();
        flowData.transTagToId.clear();

        if(transList == null || transList.size() == 0){
            return null;
        }
        for(Node tmpNode : transList){
            FlowTransition wfTran = flowDef.newFlowTransition();

            Element baseNode = (Element) tmpNode.selectSingleNode("BaseProperties");
            String sId = getXmlNodeAttrAsStr(baseNode,"id");
            if(sId == null)
                continue;
            long thisTransId = flowDefineDao.getNextTransId();
            flowData.transTagToId.put(sId , thisTransId);

            wfTran.setTransid(thisTransId);

            wfTran.setTransName( getXmlNodeAttrAsStr(baseNode,"name"));
            wfTran.setTranscondition( getXmlNodeAttrAsStr(baseNode,"cond"));

            wfTran.setLimitType(getXmlNodeAttrAsStr(baseNode,"timeLimitType"));

            String timelimit = getXmlNodeAttrAsStr(baseNode,"timeLimit");
            if(timelimit != null && timelimit.length() > 0){
                wfTran.setTimeLimit( getXmlNodeAttrAsStr(baseNode,"timeLimit"));
            }   
            
            //codefan@sina.com 2015-4-8 添加路由计时相关属性
            wfTran.setIsAccountTime(getXmlNodeAttrAsStr(baseNode,"isaccounttime"));
            wfTran.setCanIgnore(getXmlNodeAttrAsStr(baseNode,"canignore")); 
            
            sId = getXmlNodeAttrAsStr(baseNode,"from");
            long fromNodeId = flowData.nodeTagToId.get(sId);
            wfTran.setStartnodeid(fromNodeId);
            sId = getXmlNodeAttrAsStr(baseNode,"to");
            long toNodeId = flowData.nodeTagToId.get(sId);
            wfTran.setEndnodeid( toNodeId);
            if (fromNodeId == flowData.beginNodeId)
                flowData.firstNodeId = toNodeId;
            wfTran.setTransDesc( getXmlNodeAttrAsStr(baseNode,"desc"));

            wfTranSet.add(wfTran);
        }

        return wfTranSet;
    }


    @SuppressWarnings("unchecked")
    private FlowInfo createFlowDefByXML(String sXMLdef, String flowCode, Long version, FlowDataDetail flowData){

        FlowInfo flowDef = new FlowInfo();
        flowDef.setCid( new FlowInfoId(version,flowCode));
        Document doc = XmlUtils.string2xml(sXMLdef);

        logger.debug(doc.asXML());

        // <Flow code="test" name="测试流程定义" type="n" desc="这是一个测试流程图形定义界面的示例" >
        Element flowEle = (Element) doc.selectSingleNode("//Flow");
        // 流程代码
        //flowDef.setWfcode(getXmlNodeAttrAsStr(flowEle,"code"));
        // 流程名称
        flowDef.setFlowName(getXmlNodeAttrAsStr(flowEle,"name"));
        // 流程类别
        flowDef.setFlowClass(getXmlNodeAttrAsStr(flowEle,"type"));
        // 流程描述
        flowDef.setFlowDesc(getXmlNodeAttrAsStr(flowEle,"desc"));
        // 流程XML串
        flowDef.setFlowXmlDesc(sXMLdef);
        // 流程节点定义  必需先调用 getWfNodeSet 再调用 getWfTransitionSet因为nodeTagToId的耦合性
        List<Node> nodeList = flowEle.selectNodes("//Nodes/Node");
        flowDef.setFlowNodes(mapWfNodeSet(nodeList, flowDef,flowData));
        // 流程流转路径定义
        List<Node> transList = flowEle.selectNodes("//Transitions/Transition");
        flowDef.setFlowTransitions(getWfTransitionSet(transList, flowDef,flowData));

        return flowDef;
    }

    @Override
    @Transactional
    public boolean saveDraftFlowDef(FlowInfo wfDef) {
        // 将 流程定义格式保存到 版本为 0 的记录中
        FlowInfo flowDef = flowDefineDao.getObjectById(new FlowInfoId(0L,wfDef.getFlowCode()) );
        if( flowDef == null){
            flowDef = new FlowInfo();
            flowDef.setFlowCode(wfDef.getFlowCode());
        }
        flowDef.copyNotNullProperty(wfDef);        
        flowDef.setVersion(0L);//wfDef.getVersion()==null ? 0L : wfDef.getVersion());

        flowDef.setFlowState("A");//wfDef.getWfstate() == null ? "A":wfDef.getWfstate());
        flowDef.setFlowClass("R");

        flowDefineDao.mergeObject(flowDef);
        return true;
    }

    @Override
    @Transactional
    public boolean saveDraftFlowStage(FlowInfo wfDef) {
        // 将 流程定义格式保存到 版本为 0 的记录中
        FlowInfo flowDef = flowDefineDao.getObjectById(new FlowInfoId(0L,wfDef.getFlowCode()) );
        if( flowDef == null){
            return false;
        }
        flowDef.setFlowState("A");//wfDef.getWfstate() == null ? "A":wfDef.getWfstate());
        flowDef.setFlowClass("R");
        flowDef.replaceFlowStages(wfDef.getFlowStagesSet());
        flowDefineDao.saveObjectReferences(flowDef);
        return true;
    }

    @Override
    @Transactional
    public boolean saveDraftFlowRole(FlowInfo wfDef) {
        // 将 流程定义格式保存到 版本为 0 的记录中
        FlowInfo flowDef = flowDefineDao.getObjectById(new FlowInfoId(0L,wfDef.getFlowCode()) );
        if( flowDef == null){
            return false;
        }
        flowDef.setFlowState("A");//wfDef.getWfstate() == null ? "A":wfDef.getWfstate());
        flowDef.setFlowClass("R");
        flowDef.replaceFlowRoles(wfDef.getFlowRolesSet());
        flowDefineDao.saveObjectReferences(flowDef);
        return true;
    }

    @Override
    @Transactional
    public boolean saveDraftFlowVariableDef(FlowInfo wfDef) {
        // 将 流程定义格式保存到 版本为 0 的记录中
        FlowInfo flowDef = flowDefineDao.getObjectById(new FlowInfoId(0L,wfDef.getFlowCode()) );
        if( flowDef == null){
            return false;
        }
        flowDef.setFlowState("A");//wfDef.getWfstate() == null ? "A":wfDef.getWfstate());
        flowDef.setFlowClass("R");
        flowDef.replaceFlowVariableDefs(wfDef.getFlowVariableDefSet());
        flowDefineDao.saveObjectReferences(flowDef);
        return true;
    }



    @Override
    @Transactional
    public boolean saveDraftFlowDefXML(String  flowCode, String flowDefXML)
    {
        FlowInfo flowDef = flowDefineDao.getObjectById(new FlowInfoId(0L,flowCode) );
        if( flowDef == null){
            flowDef = new FlowInfo();
            flowDef.setFlowCode(flowCode);
            flowDef.setFlowName(flowCode);
        }
        flowDef.setVersion(0L);//wfDef.getVersion()==null ? 0L : wfDef.getVersion());

        flowDef.setFlowState("A");//wfDef.getWfstate() == null ? "A":wfDef.getWfstate());
        flowDef.setFlowClass("R");
        
        flowDef.setFlowXmlDesc(flowDefXML);
        
        flowDefineDao.updateObject(flowDef);
        return true;
    }

    @Override
    @Transactional
    public String getDraftFlowDefXML(String  flowCode) {
        // 版本号为 0 的流程定义中获得 XML
        return getFlowDefXML(flowCode,0);
    }

    private void checkFlowDef(FlowInfo newFlowDef) throws Exception{
        //验证流程节点定义
        for(NodeInfo nd : newFlowDef.getFlowNodes()){
            if( "C".equals(nd.getNodeType()) || "B".equals(nd.getNodeType()) ){
                if("S".equals(nd.getOptType())){
                    if(StringRegularOpt.isNvl( nd.getSubFlowCode()))
                        throw new Exception("子流程节点："+nd.getNodeName()+",没有指定流程代码。");
                }else if(!"E".equals(nd.getOptType()) && !"D".equals(nd.getOptType()) ){
                    if(StringRegularOpt.isNvl(nd.getOptCode()))
                        throw new Exception("节点："+nd.getNodeName()+",没有指定业务操作代码。");
                    if(StringRegularOpt.isNvl(nd.getRoleType()))
                        throw new Exception("节点："+nd.getNodeName()+",没有指定角色类别。");
                    else if("en".equals(nd.getRoleType())){
                        if(StringRegularOpt.isNvl(nd.getPowerExp()))
                            throw new Exception("节点："+nd.getNodeName()+",权限表达式为空。");
                    }else{
                       if(StringRegularOpt.isNvl(nd.getRoleCode()))
                           throw new Exception("节点："+nd.getNodeName()+",没有指定角色代码。");
                    }
                }else if ("D".equals(nd.getOptType()) ){
                    if(StringRegularOpt.isNvl(nd.getOptBean()))
                        throw new Exception("自动运行节点："+nd.getNodeName()+",没有运行的bean。");
                }
            }else if("R".equals(nd.getNodeType())){
                if(StringRegularOpt.isNvl(nd.getRouterType())){
                    throw new Exception("路由节点："+nd.getNodeName()+",没有指定路由类型。");
                }
            }
        }
        //检查 流转定义
        for(FlowTransition tran: newFlowDef.getFlowTransitions()){
            if(tran.getTranscondition()==null || "".equals(tran.getTranscondition())){
                NodeInfo nd = newFlowDef.getFlowNodeById(tran.getStartnodeid());
                if(nd!=null && ! "A".equals(nd.getNodeType()) && ! "C".equals(nd.getNodeType())){
                    throw new Exception("流转："+tran.getTransName()+",没有指定流转条件。");
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public long publishFlowDef(String flowCode) throws Exception{
        FlowDataDetail flowData = new FlowDataDetail();
        // 将流程从 XML 格式中解析出来
        FlowInfo flowDef = flowDefineDao.getObjectById(new FlowInfoId(0L,flowCode) );
        if(flowDef==null){
            return 0L;
        }
        // 获取新的版本号
        long nCurVersion = flowDefineDao.getLastVersion(flowCode);
        Long newVersion = nCurVersion + 1L;

        //WfFlowDefine newFlowDef = new WfFlowDefine();
        String wfDefXML = flowDef.getFlowXmlDesc();
        if(StringUtils.isBlank(wfDefXML)){
            throw new Exception("流程没有内容");
        }
        FlowInfo newFlowDef = createFlowDefByXML(wfDefXML,flowCode,newVersion,flowData);
        // 添加验证流程定义验证
        checkFlowDef(newFlowDef);

        List<FlowStage> newStages = new ArrayList<FlowStage>();
        //newFlowDef.replaceFlowStages(flowDef.getFlowStages());
        for(FlowStage p :flowDef.getFlowStages()){
            if(p==null)
                continue;
            FlowStage newdt = newFlowDef.newFlowStage();
            newdt.copyNotNullProperty(p);
            newdt.setStageId(getNextStageId());
            newStages.add(newdt);
        }
        newFlowDef.setFlowStages(newStages);
        
        Map<Long ,String>  nodeIsLeaf = new HashMap<Long ,String>();
        for(NodeInfo nd : newFlowDef.getFlowNodes()){
            if(nd.getNodeId().equals(flowData.firstNodeId ))
                nd.setNodeType("B");//首届点

        }
        //检查 孤立的节点
        for(FlowTransition tran: newFlowDef.getFlowTransitions()){
            nodeIsLeaf.put(tran.getStartnodeid(), "F");
        }

        // 替换 流程XML格式中的节点、流转编码 对照表在 ndMap 和 trMap 中
        Document wfDefDoc = XmlUtils.string2xml(wfDefXML);
        List<Node> nodeList = wfDefDoc.selectNodes("//Nodes/Node");

        for (Node tmpNode : nodeList) {
            Element baseNode = (Element) tmpNode
                    .selectSingleNode("BaseProperties");
            Attribute nodeIdAttr = baseNode.attribute("id");
            String sId = nodeIdAttr.getValue();
            if( BEGINNODETAG.compareToIgnoreCase(sId)!=0 &&    ENDNODETAG.compareToIgnoreCase(sId)!=0)
                nodeIdAttr.setValue(flowData.nodeTagToId.get(sId).toString());
        }

        List<Node> transList = wfDefDoc.selectNodes("//Transitions/Transition");
        for (Node transNode : transList) {
            Element baseNode = (Element) transNode
                    .selectSingleNode("BaseProperties");
            Attribute transIdAttr = baseNode.attribute("id");
            if(transIdAttr==null)
                continue;
            String sTransId = flowData.transTagToId.get(transIdAttr.getValue()).toString();
            transIdAttr.setValue( sTransId );

            Attribute fromAttr = baseNode.attribute("from");
            if(fromAttr !=null){
                String sId = fromAttr.getValue();
                if( BEGINNODETAG.compareToIgnoreCase(sId)!=0 &&    ENDNODETAG.compareToIgnoreCase(sId)!=0)
                    fromAttr.setValue(flowData.nodeTagToId.get(sId).toString());
            }

            Attribute toAttr = baseNode.attribute("to");
            if(toAttr !=null){
                String sId = toAttr.getValue();
                if( BEGINNODETAG.compareToIgnoreCase(sId)!=0 &&    ENDNODETAG.compareToIgnoreCase(sId)!=0)
                    toAttr.setValue(flowData.nodeTagToId.get(sId).toString());
            }

            Element labNode = (Element) transNode
                .selectSingleNode("LabelProperties");
            Attribute idAttr = labNode.attribute("id");
            if(idAttr!=null)
                idAttr.setValue("lab"+sTransId);

        }
        newFlowDef.setFlowXmlDesc(wfDefDoc.asXML());

        // 保存新版本的流程,状态设置为正常
        newFlowDef.setFlowDesc(flowDef.getFlowDesc());
        newFlowDef.setFlowName(flowDef.getFlowName());
        newFlowDef.setFlowClass(flowDef.getFlowClass());
        newFlowDef.setOptId(flowDef.getOptId());
        newFlowDef.setTimeLimit(flowDef.getTimeLimit());
        newFlowDef.setExpireOpt(flowDef.getExpireOpt());
        newFlowDef.setFlowPublishDate(DatetimeOpt.currentUtilDate());
        newFlowDef.setFlowState("B");
        //复制相关节点信息
        //newFlowDef.getWfFlowStages()

        flowDefineDao.saveNewObject(newFlowDef);
        flowDefineDao.saveObjectReferences(newFlowDef);

        //将0版本更新为已发布
        flowDef.setFlowState("E");
        flowDefineDao.updateObject(flowDef);

        //将非0老版本流程状态改为已过期
        FlowInfo oldflowDef = flowDefineDao.getObjectById(new FlowInfoId((long)nCurVersion,flowCode) );
        if(oldflowDef != null  && nCurVersion > 0){
            oldflowDef.setFlowState("C");
            flowDefineDao.updateObject(oldflowDef);
        }

        return nCurVersion+1;
    }

    @Override
    @Transactional
    public FlowInfo getFlowDefObject(String flowCode, long version){
        try{
            return flowDefineDao.getObjectCascadeById(new FlowInfoId(version,flowCode));
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    @Transactional
    public FlowInfo getFlowDefObject(String  flowCode){
        long version = flowDefineDao.getLastVersion(flowCode);
        return getFlowDefObject(flowCode,version);
    }


    /**
     * 根据节点ID获得节点定义
     * @param nodeId
     * @return
     */
    @Override
    @Transactional
    public NodeInfo getNodeInfoById(long nodeId){
        return flowNodeDao.getObjectById(nodeId);
    }
    

    @Override
    @Transactional
    public String getFlowDefXML(String  flowCode, long version) {
        // 获得某个特定版本的流程定义 文本
        FlowInfo flowDef = flowDefineDao.getObjectById(new FlowInfoId(Long.valueOf(version),flowCode) );
        if( flowDef == null)
            return "";
        return flowDef.getFlowXmlDesc();
    }
    @Override
    @Transactional
    public String getFlowDefXML(String  flowCode) {
        // 获得流程最新版本的流程定义 文本
        long nCurVersion = flowDefineDao.getLastVersion(flowCode);
        if(nCurVersion==0)
            return null;
        return getFlowDefXML(flowCode,nCurVersion);
    }
    @Override
    @Transactional
    public void disableFlow(String  flowCode) {
        //将最新版本的流程定义的状态更改为禁用 D
        FlowInfo flowDef = flowDefineDao.getLastVersionFlowByCode(flowCode);
        flowDef.setFlowState("D");
        flowDefineDao.updateObject(flowDef);
    }
    @Override
    @Transactional
    public void enableFlow(String  flowCode) {
        //将最新版本的流程定义的状态更改为正常 B
        FlowInfo flowDef = flowDefineDao.getLastVersionFlowByCode(flowCode);
        flowDef.setFlowState("B");
        flowDefineDao.updateObject(flowDef);
    }

    @Override
    @Transactional
    public String getNextPrimarykey() {        
        return flowDefineDao.getNextPrimarykey();
    }

    @Override
    @Transactional
    public long getNextStageId(){
        return flowDefineDao.getNextStageId();
    }

    @Override
    @Transactional
    public long getNextRoleId(){
        return flowDefineDao.getNextRoleId();
    }

    @Override
    @Transactional
    public long getNextVariableDefId(){
        return flowDefineDao.getNextVariableDefId();
    }

    @Override
    @Transactional
    public List<FlowInfo> getFlowsByCode(String wfCode, PageDesc pageDesc){
        List<FlowInfo> flows = flowDefineDao
                .getAllVersionFlowsByCode(wfCode,pageDesc);
        return new ArrayList<FlowInfo>(
                flows == null ? new ArrayList<FlowInfo>() : flows);
    }
    
    @Override
    @Transactional
    public List<FlowInfo> listLastVersionFlow(
            Map<String, Object> filterMap, PageDesc pageDesc) {
        List<FlowInfo> flows = flowDefineDao
                .getAllLastVertionFlows(filterMap,pageDesc);

        //获取草稿版本的状态（0版本的状态）
        for (FlowInfo def : flows) {
            FlowInfo wfDef = flowDefineDao
                    .getObjectById(new FlowInfoId(0L, def.getFlowCode()));
            if (wfDef != null) {
                def.setFlowState(wfDef.getFlowState());
            }

        }
        return new ArrayList<FlowInfo>(flows);
    }
    
    @Override
    @Transactional
    public Set<String> getUnitExp(String flowCode, Long version) {
        return flowNodeDao.getUnitExp(flowCode, version);
    }

    @Override
    @Transactional
    public Map<String, String> getRoleMapByFlowCode(String flowCode, Long version) {
        return flowTeamRoleDao.getRoleByFlowCode(flowCode,version);
    }
}
