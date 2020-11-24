package com.centit.workflow.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.components.SysUserFilterEngine;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitFilterCalcContextFactory;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.StringRegularOpt;
import com.centit.support.algorithm.UuidOpt;
import com.centit.support.common.ObjectException;
import com.centit.support.compiler.Lexer;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.network.HtmlFormUtils;
import com.centit.support.xml.XmlUtils;
import com.centit.workflow.dao.*;
import com.centit.workflow.po.*;
import com.centit.workflow.service.FlowDefine;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;

@Service
public class FlowDefineImpl implements FlowDefine, Serializable {

    private static final long serialVersionUID = 1L;
    @Autowired
    private FlowInfoDao flowDefineDao;

    @Autowired
    private NodeInfoDao flowNodeDao;

    @Autowired
    private FlowOptPageDao flowOptPageDao;

    @Autowired
    private FlowOptInfoDao flowOptInfoDao;

    @Autowired
    private RoleFormulaDao flowRoleDao;

    @Autowired
    private OptVariableDefineDao optVariableDefineDao;

    @Autowired
    private OptTeamRoleDao optTeamRoleDao;

    @Autowired
    private UserUnitFilterCalcContextFactory userUnitFilterFactory;

    private static Logger logger = LoggerFactory.getLogger(FlowDefineImpl.class);
    public static final String BEGINNODETAG = "begin";
    public static final String ENDNODETAG = "end";

    private class FlowDataDetail {
        //这个Map放在这儿有一定的耦合性 重构成一个类
        public Map<String, String> nodeTagToId;
        public Map<String, String> transTagToId;
        public String beginNodeId;

        public FlowDataDetail() {
            nodeTagToId = new HashMap<>();
            transTagToId = new HashMap<>();
        }
    }

    public FlowDefineImpl() {
    }

    @PostConstruct
    public void registerDictionary(){
        CodeRepositoryUtil.registeExtendedCodeRepo(
            "flowCode", flowDefineDao.listFlowCodeNameMap()
            );
    }

    @Override
    @Transactional
    public List<FlowInfo> listFlowsByOptId(String optId) {
        List<FlowInfo> flows = flowDefineDao.listLastVersionFlowByOptId(optId);
        return new ArrayList<>(
            flows == null ? new ArrayList<>() : flows);
    }

    private static String getXmlNodeAttrAsStr(Element xNode, String attrName) {
        Attribute nameAttr = xNode.attribute(attrName);
        if (nameAttr != null) {
            String xmlNodeString = HtmlFormUtils.htmlString(
                nameAttr.getValue());
            //如果是空字符串或者null字符串
            if (StringUtils.isBlank(xmlNodeString) || "null".equals(xmlNodeString)) {
                xmlNodeString = "";
            }
            return xmlNodeString;
        }
        return null;
    }

    /**
     * 获取流程节点信息
     * 节点XML如下：
     * <Nodes>
     * <Node>
     * <!--nodetype  节点类别 A:开始 B:首节点 C:一般 D:分支 E:汇聚  R:游离分支  F:结束 -->
     * <!--OptType 操作类别 A:一般 B:抢先机制 C:多人操作  D:子流程 -->
     * <BaseProperties id="1" name="开始" nodetype="A" desc="开始节点描述"
     * opttype="A" optcode="" roletype="" rolecode="" />
     * <VMLProperties shapetype="Oval" width="40" height="40"
     * x="60" y="160" textWeight="9pt" strokeWeight="1" zIndex="1" />
     * </Node>
     * </Nodes>
     *
     * @param nodeList
     * @param flowDef
     * @return wfSet
     */
    private List<NodeInfo> attachNodeList(List<Node> nodeList, FlowInfo flowDef, FlowDataDetail flowData) {
        flowData.nodeTagToId.clear();
        List<NodeInfo> wfSet = new ArrayList<>();

        if (nodeList == null || nodeList.size() == 0) {
            return null;
        }
        String thisNodeId;
        for (Node tmpNode : nodeList) {
            Element baseNode = (Element) tmpNode.selectSingleNode("BaseProperties");
            String sId = getXmlNodeAttrAsStr(baseNode, "id");
            if (sId == null)
                continue;
            thisNodeId = UuidOpt.getUuidAsString32();
//            thisNodeId = flowDefineDao.getNextNodeId();
            //这个对应关系 留给下面的 attachTransitionList 使用，有一定的耦合性
            flowData.nodeTagToId.put(sId, thisNodeId);
        }

        for (Node tmpNode : nodeList) {
            Element baseNode = (Element) tmpNode.selectSingleNode("BaseProperties");
            String sId = getXmlNodeAttrAsStr(baseNode, "id");
            if (sId == null)
                continue;
            NodeInfo wfNode = flowDef.newFlowNode();
            thisNodeId = flowData.nodeTagToId.get(sId); //获取节点ID

            sId = sId.toLowerCase();
            if (sId.equals(BEGINNODETAG)) {
                wfNode.setNodeType(NodeInfo.NODE_TYPE_START);
                flowData.beginNodeId = thisNodeId;
            } else if (sId.equals(ENDNODETAG)) {
                wfNode.setNodeType(NodeInfo.NODE_TYPE_END);
            } else {
                wfNode.setNodeType(NodeInfo.NODE_TYPE_OPT);
            }

            wfNode.setNodeId(thisNodeId);
            wfNode.setNodeName(getXmlNodeAttrAsStr(baseNode, "name"));
            //获取节点类型
            if (!StringRegularOpt.isNvl(
                getXmlNodeAttrAsStr(baseNode, "flowphase"))) {
                wfNode.setStageCode(getXmlNodeAttrAsStr(baseNode, "flowphase"));
            }
            wfNode.setNodeType(getXmlNodeAttrAsStr(baseNode, "nodetype"));
            wfNode.setNodeCode(getXmlNodeAttrAsStr(baseNode, "nodecode"));
            wfNode.setRiskinfo(getXmlNodeAttrAsStr(baseNode, "riskinfo"));
            wfNode.setNodeDesc(getXmlNodeAttrAsStr(baseNode, "desc"));
            wfNode.setOptType(getXmlNodeAttrAsStr(baseNode, "opttype"));
            wfNode.setOptCode(getXmlNodeAttrAsStr(baseNode, "optcode"));
            wfNode.setOptBean(getXmlNodeAttrAsStr(baseNode, "optbean"));

            String optparam = getXmlNodeAttrAsStr(baseNode, "optparam");

            if (optparam != null && optparam.indexOf("&amp;") > 0) {
                optparam = optparam.replaceAll("&amp;", "&");
            }

            wfNode.setOptParam(optparam);

            wfNode.setRoleType(getXmlNodeAttrAsStr(baseNode, "roletype"));
            wfNode.setRoleCode(getXmlNodeAttrAsStr(baseNode, "rolecode"));
            wfNode.setLimitType(getXmlNodeAttrAsStr(baseNode, "timeLimitType"));
            wfNode.setIsTrunkLine(BooleanBaseOpt.castObjectToBoolean(
                getXmlNodeAttrAsStr(baseNode, "isTrunkLine"), false));
            String timelimit = getXmlNodeAttrAsStr(baseNode, "timeLimit");
            if (timelimit != null && timelimit.length() > 0) {
                wfNode.setTimeLimit(getXmlNodeAttrAsStr(baseNode, "timeLimit"));
            }
            wfNode.setInheritType(getXmlNodeAttrAsStr(baseNode, "inheritType"));
            wfNode.setInheritNodeCode(getXmlNodeAttrAsStr(baseNode, "inheritNodeCode"));
            wfNode.setExpireOpt(getXmlNodeAttrAsStr(baseNode, "expireopt"));
            wfNode.setUnitExp(getXmlNodeAttrAsStr(baseNode, "unitexp"));
            wfNode.setPowerExp(getXmlNodeAttrAsStr(baseNode, "powerexp"));
            wfNode.setSubFlowCode(getXmlNodeAttrAsStr(baseNode, "subwfcode"));
            wfNode.setIsAccountTime(
                    getXmlNodeAttrAsStr(baseNode, "isaccounttime"));

            //codefan@sina.com 2015-4-8 添加路由相关属性
            wfNode.setRouterType(getXmlNodeAttrAsStr(baseNode, "routertype"));
            wfNode.setMultiInstType(getXmlNodeAttrAsStr(baseNode, "multiinsttype"));
            wfNode.setMultiInstParam(getXmlNodeAttrAsStr(baseNode, "multiinstparam"));
            wfNode.setConvergeType(getXmlNodeAttrAsStr(baseNode, "convergetype"));
            wfNode.setConvergeParam(getXmlNodeAttrAsStr(baseNode, "convergeparam"));
            wfNode.setWarningRule(getXmlNodeAttrAsStr(baseNode, "warningrule"));
            wfNode.setWarningParam(getXmlNodeAttrAsStr(baseNode, "warningparam"));
            wfNode.setOsId(getXmlNodeAttrAsStr(baseNode, "osid"));

            wfSet.add(wfNode);
        }
        return wfSet;
    }

    /**
     * 流程流转路径定义
     * 路径节点信息如下：
     * <Transitions>
     * <Transition>
     * <BaseProperties id="1" name="流程开始" from="1"
     * to="3" cond="" desc="" />
     * <VMLProperties points="75pt,135pt,135pt,135pt"
     * fromRelX="1" fromRelY="0.5" toRelX="0" toRelY="0.5" shapetype="PolyLine"
     * startArrow="none" endArrow="Classic" strokeWeight="1" zIndex="0" />
     * <LabelProperties id="labstep0" width="60" height="20px"
     * x="119px" y="186px" />
     * </Transition>
     * </Transitions>
     *
     * @param transList
     * @param flowDef
     * @return wfTranSet
     */
    private void attachTransitionList(List<Node> transList, FlowInfo flowDef, FlowDataDetail flowData) {
        List<FlowTransition> wfTranSet = new ArrayList<>();
        flowData.transTagToId.clear();

        if (transList == null || transList.size() == 0) {
            return;
        }
        for (Node tmpNode : transList) {
            FlowTransition wfTran = flowDef.newFlowTransition();

            Element baseNode = (Element) tmpNode.selectSingleNode("BaseProperties");
            String sId = getXmlNodeAttrAsStr(baseNode, "id");
            if (sId == null)
                continue;
            String thisTransId = UuidOpt.getUuidAsString32();
//            long thisTransId = flowDefineDao.getNextTransId();
            flowData.transTagToId.put(sId, thisTransId);

            wfTran.setTransId(thisTransId);

            wfTran.setTransName(getXmlNodeAttrAsStr(baseNode, "name"));
            wfTran.setTransCondition(getXmlNodeAttrAsStr(baseNode, "cond"));

            wfTran.setLimitType(getXmlNodeAttrAsStr(baseNode, "timeLimitType"));

            String timelimit = getXmlNodeAttrAsStr(baseNode, "timeLimit");
            if (timelimit != null && timelimit.length() > 0) {
                wfTran.setTimeLimit(getXmlNodeAttrAsStr(baseNode, "timeLimit"));
            }

            //codefan@sina.com 2015-4-8 添加路由计时相关属性
            wfTran.setIsAccountTime(getXmlNodeAttrAsStr(baseNode, "isaccounttime"));
            wfTran.setCanIgnore(
                BooleanBaseOpt.castObjectToBoolean(
                    getXmlNodeAttrAsStr(baseNode, "canignore"),true));

            sId = getXmlNodeAttrAsStr(baseNode, "from");
            String fromNodeId = flowData.nodeTagToId.get(sId);
            wfTran.setStartNodeId(fromNodeId);
            sId = getXmlNodeAttrAsStr(baseNode, "to");
            String toNodeId = flowData.nodeTagToId.get(sId);
            wfTran.setEndNodeId(toNodeId);
            if (fromNodeId == flowData.beginNodeId) {
                flowDef.setFirstNodeId(toNodeId);
            }
            wfTran.setTransDesc(getXmlNodeAttrAsStr(baseNode, "desc"));

            wfTranSet.add(wfTran);
        }
        flowDef.setTransList(wfTranSet);
    }

    private FlowInfo createFlowDefByJSON(String jsonDef, String flowCode, Long version, FlowDataDetail flowData) {
        FlowInfo flowDef = JSON.parseObject(jsonDef, FlowInfo.class);// new FlowInfo();
        flowDef.setCid(new FlowInfoId(version, flowCode));
        //getStartNode
        flowData.nodeTagToId.clear();
        flowData.transTagToId.clear();
        String startNodeId = null;
        for(NodeInfo node : flowDef.getNodeList()){
            if(NodeInfo.NODE_TYPE_START.equals(node.getNodeType())){
                startNodeId = node.getNodeId();
            }
            String thisNodeId = UuidOpt.getUuidAsString32();
            flowData.nodeTagToId.put(node.getNodeId(), thisNodeId);
            node.setNodeId(thisNodeId);
        }

        for(FlowTransition tran : flowDef.getTransList()){
            String fromNodeId = flowData.nodeTagToId.get(tran.getStartNodeId());
            String toNodeId = flowData.nodeTagToId.get(tran.getEndNodeId());
            if(StringUtils.equals(startNodeId, tran.getStartNodeId())){
                flowDef.setFirstNodeId(toNodeId);
            }
            tran.setStartNodeId(fromNodeId);
            tran.setEndNodeId(toNodeId);
            String thisTransId = UuidOpt.getUuidAsString32();
            flowData.transTagToId.put(tran.getTransId(), thisTransId);
            tran.setTransId(thisTransId);
        }
        return flowDef;
    }

    @SuppressWarnings("unchecked")
    private FlowInfo createFlowDefByXML(String sXMLdef, String flowCode, Long version, FlowDataDetail flowData) {

        FlowInfo flowDef = new FlowInfo();
        flowDef.setCid(new FlowInfoId(version, flowCode));
        Document doc = XmlUtils.string2xml(sXMLdef);

        logger.debug(doc.asXML());

        // <Flow code="test" name="测试流程定义" type="n" desc="这是一个测试流程图形定义界面的示例" >
        Element flowEle = (Element) doc.selectSingleNode("//Flow");
        // 流程代码
        //flowDef.setWfcode(getXmlNodeAttrAsStr(flowEle,"code"));
        // 流程名称
        flowDef.setFlowName(getXmlNodeAttrAsStr(flowEle, "name"));
        // 流程类别
        flowDef.setFlowClass(getXmlNodeAttrAsStr(flowEle, "type"));
        // 流程描述
        flowDef.setFlowDesc(getXmlNodeAttrAsStr(flowEle, "desc"));
        // 流程XML串
        flowDef.setFlowXmlDesc(sXMLdef);
        // 流程节点定义  必需先调用 getWfNodeSet 再调用 getWfTransitionSet因为nodeTagToId的耦合性
        List<Node> nodeList = flowEle.selectNodes("//Nodes/Node");
        flowDef.setNodeList(attachNodeList(nodeList, flowDef, flowData));
        // 流程流转路径定义
        List<Node> transList = flowEle.selectNodes("//Transitions/Transition");
        attachTransitionList(transList, flowDef, flowData);

        return flowDef;
    }

    @Override
    @Transactional
    public boolean saveDraftFlowDef(FlowInfo wfDef) {
        // 将 流程定义格式保存到 版本为 0 的记录中
        FlowInfo flowDef = flowDefineDao.getObjectById(new FlowInfoId(0L, wfDef.getFlowCode()));
        if (flowDef == null) {
            flowDef = new FlowInfo();
            flowDef.setFlowCode(wfDef.getFlowCode());
        }
        flowDef.copyNotNullProperty(wfDef);
        flowDef.setVersion(0L);//wfDef.getVersion()==null ? 0L : wfDef.getVersion());

        flowDef.setFlowState(FlowInfo.FLOW_STATE_DRAFT);//wfDef.getWfstate() == null ? "A":wfDef.getWfstate());
        flowDef.setFlowClass("R");

        flowDefineDao.mergeObject(flowDef);
        return true;
    }

    @Override
    @Transactional
    public boolean saveDraftFlowStage(FlowInfo wfDef) {
        // 将 流程定义格式保存到 版本为 0 的记录中
        FlowInfo flowDef = flowDefineDao.getFlowDefineByID(wfDef.getFlowCode(), 0L);
        if (flowDef == null) {
            return false;
        }
        flowDef.setFlowState(FlowInfo.FLOW_STATE_DRAFT);//wfDef.getWfstate() == null ? "A":wfDef.getWfstate());
        flowDef.setFlowClass("R");
        flowDef.replaceFlowStages(wfDef.getFlowStagesSet());
        flowDefineDao.saveObjectReferences(flowDef);
        return true;
    }

    @Override
    @Transactional
    public boolean saveDraftFlowRole(FlowInfo wfDef) {
        // 将 流程定义格式保存到 版本为 0 的记录中
        FlowInfo flowDef = flowDefineDao.getFlowDefineByID(wfDef.getFlowCode(), 0L);
        if (flowDef == null) {
            return false;
        }
        flowDef.setFlowState(FlowInfo.FLOW_STATE_DRAFT);//wfDef.getWfstate() == null ? "A":wfDef.getWfstate());
        flowDef.setFlowClass("R");
        //flowDef.replaceFlowRoles(wfDef.getFlowRolesSet());
        flowDefineDao.saveObjectReferences(flowDef);
        return true;
    }

    @Override
    @Transactional
    public boolean saveDraftFlowVariableDef(FlowInfo wfDef) {
        // 将 流程定义格式保存到 版本为 0 的记录中
        FlowInfo flowDef = flowDefineDao.getFlowDefineByID(wfDef.getFlowCode(), 0L);
        if (flowDef == null) {
            return false;
        }
        flowDef.setFlowState(FlowInfo.FLOW_STATE_DRAFT);//wfDef.getWfstate() == null ? "A":wfDef.getWfstate());
        flowDef.setFlowClass("R");
        //flowDef.replaceFlowVariableDefs(wfDef.getFlowVariableDefSet());
        flowDefineDao.saveObjectReferences(flowDef);
        return true;
    }

    @Override
    @Transactional
    public boolean saveDraftFlowDefXML(String flowCode, String flowDefXML) {
        FlowInfo flowDef = flowDefineDao.getObjectById(new FlowInfoId(0L, flowCode));
        if (flowDef == null) {
            flowDef = new FlowInfo();
            flowDef.setFlowCode(flowCode);
            flowDef.setFlowName(flowCode);
        }
        flowDef.setVersion(0L);//wfDef.getVersion()==null ? 0L : wfDef.getVersion());

        flowDef.setFlowState(FlowInfo.FLOW_STATE_DRAFT);//wfDef.getWfstate() == null ? "A":wfDef.getWfstate());
        flowDef.setFlowClass("R");

        flowDef.setFlowXmlDesc(flowDefXML);

        flowDefineDao.updateObject(flowDef);
        return true;
    }

    @Override
    @Transactional
    public String getDraftFlowDefXML(String flowCode) {
        // 版本号为 0 的流程定义中获得 XML
        return getFlowDefXML(flowCode, 0);
    }

    private void checkFlowDef(FlowInfo newFlowDef){
        //验证流程节点定义
        for (NodeInfo nd : newFlowDef.getNodeList()) {
            if (NodeInfo.NODE_TYPE_OPT.equals(nd.getNodeType()) || NodeInfo.NODE_TYPE_FIRST.equals(nd.getNodeType())) {
                if (NodeInfo.NODE_OPT_SUBFLOW.equals(nd.getOptType())) {
                    if (StringRegularOpt.isNvl(nd.getSubFlowCode()))
                        throw new ObjectException("子流程节点：" + nd.getNodeName() + ",没有指定流程代码。");
                } else if (!NodeInfo.NODE_OPT_AUTO.equals(nd.getOptType()) && !NodeInfo.NODE_OPT_NONE.equals(nd.getOptType())) {
                    if (StringRegularOpt.isNvl(nd.getOptCode()))
                        throw new ObjectException("节点：" + nd.getNodeName() + ",没有指定业务操作代码。");
                    if (StringRegularOpt.isNvl(nd.getRoleType()))
                        throw new ObjectException("节点：" + nd.getNodeName() + ",没有指定角色类别。");
                    else if (SysUserFilterEngine.ROLE_TYPE_ENGINE.equalsIgnoreCase(nd.getRoleType())) {
                        if (StringRegularOpt.isNvl(nd.getPowerExp()))
                            throw new ObjectException("节点：" + nd.getNodeName() + ",权限表达式为空。");
                    } else {
                        if (StringRegularOpt.isNvl(nd.getRoleCode())
                            && !SysUserFilterEngine.ROLE_TYPE_ENGINE.equals(nd.getRoleType()))
                            throw new ObjectException("节点：" + nd.getNodeName() + ",没有指定角色代码。");
                    }
                } else if (NodeInfo.NODE_OPT_AUTO.equals(nd.getOptType())) {
                    if (NodeInfo.AUTO_NODE_OPT_CODE_BEAN.equals(nd.getOptCode()) && StringRegularOpt.isNvl(nd.getOptBean()))
                        throw new ObjectException("自动运行节点：" + nd.getNodeName() + ",没有运行的bean。");
                    else if (NodeInfo.AUTO_NODE_OPT_CODE_SCRIPT.equals(nd.getOptCode()) && StringRegularOpt.isNvl(nd.getOptParam()))
                        throw new ObjectException("自动运行节点：" + nd.getNodeName() + ",没有运行的script。");
                }
            } else if (NodeInfo.NODE_TYPE_ROUTE.equals(nd.getNodeType())) {
                if (StringRegularOpt.isNvl(nd.getRouterType())) {
                    throw new ObjectException("路由节点：" + nd.getNodeName() + ",没有指定路由类型。");
                }
            }
        }

        //检查 流转定义
        for (FlowTransition tran : newFlowDef.getTransList()) {
            if (StringUtils.isBlank(tran.getTransCondition())) {
                NodeInfo nd = newFlowDef.getFlowNodeById(tran.getStartNodeId());
                if (nd != null && NodeInfo.NODE_TYPE_ROUTE.equals (nd.getNodeType()) &&
                    (NodeInfo.ROUTER_TYPE_BRANCH.equals(nd.getRouterType())
                     || NodeInfo.ROUTER_TYPE_PARALLEL.equals(nd.getRouterType()))) {
                    throw new ObjectException("流转：" + tran.getTransName() + ",没有指定流转条件。");
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public long publishFlowDef(String flowCode) {
        // 将流程从 XML 格式中解析出来
        FlowInfo flowDef = flowDefineDao.getObjectWithReferences(new FlowInfoId(0L, flowCode));
        if (flowDef == null) {
            return 0L;
        }
        //WfFlowDefine newFlowDef = new WfFlowDefine();
        String wfDefXML = flowDef.getFlowXmlDesc();
        if (StringUtils.isBlank(wfDefXML)) {
            throw new ObjectException("流程没有内容");
        }
        boolean defineAsXML = "<".equals(Lexer.getFirstWord(wfDefXML));
        // 获取新的版本号
        long nCurVersion = flowDefineDao.getLastVersion(flowDef.getFlowCode());
        Long newVersion = nCurVersion + 1L;

        FlowDataDetail flowData = new FlowDataDetail();
        FlowInfo newFlowDef =defineAsXML?
            createFlowDefByXML(flowDef.getFlowXmlDesc(), flowDef.getFlowCode(), newVersion, flowData):
            createFlowDefByJSON(flowDef.getFlowXmlDesc(), flowDef.getFlowCode(), newVersion, flowData);
        // 添加验证流程定义验证
        checkFlowDef(newFlowDef);

        List<FlowStage> newStages = flowDef.getFlowStages();
        for (FlowStage p : newStages) {
            if (p == null)
                continue;
            p.setStageId(UuidOpt.getUuidAsString32());
            p.setVersion(newVersion);
        }

        newFlowDef.setFlowStages(newStages);
        for (NodeInfo nd : newFlowDef.getNodeList()) {
            /*if (nd.getNodeId().equals(flowData.firstNodeId)) {
                nd.setNodeType("B");//首届点
            }*/
            if (StringUtils.isBlank(nd.getOsId())) {
                nd.setOsId(flowDef.getOsId());
            }
        }
        if(defineAsXML){
            resetNodeTransIdToXML(flowDef.getFlowXmlDesc(), newFlowDef, flowData);
        } else {
            resetNodeTransIdToJSON(flowDef.getFlowXmlDesc(), newFlowDef, flowData);
        }

        // 保存新版本的流程,状态设置为正常
        newFlowDef.setFlowDesc(flowDef.getFlowDesc());
        newFlowDef.setOsId(flowDef.getOsId());
        newFlowDef.setFlowName(flowDef.getFlowName());
        newFlowDef.setFlowClass(flowDef.getFlowClass());
        newFlowDef.setOptId(flowDef.getOptId());
        newFlowDef.setTimeLimit(flowDef.getTimeLimit());
        newFlowDef.setExpireOpt(flowDef.getExpireOpt());
        newFlowDef.setFlowPublishDate(DatetimeOpt.currentUtilDate());
        //newFlowDef.setFirstNodeId(flowData.firstNodeId);
        newFlowDef.setFlowState(FlowInfo.FLOW_STATE_NORMAL);
        //复制相关节点信息
        //newFlowDef.getWfFlowStages()

        flowDefineDao.saveNewObject(newFlowDef);
        flowDefineDao.saveObjectReferences(newFlowDef);

        //将0版本更新为已发布
        flowDef.setFlowState(FlowInfo.FLOW_STATE_PUBLISHED);
        flowDefineDao.updateObject(flowDef);

        //将非0老版本流程状态改为已过期
        if(nCurVersion>0) {
            FlowInfo oldflowDef = flowDefineDao.getObjectById(new FlowInfoId(nCurVersion, flowDef.getFlowCode()));
            if (oldflowDef != null) {
                oldflowDef.setFlowState(FlowInfo.FLOW_STATE_INVALID);
                flowDefineDao.updateObject(oldflowDef);
            }
        }
        return newVersion;
    }

    private void resetNodeTransIdToJSON(String xmlDefineDesc, FlowInfo newFlowDef, FlowDataDetail flowData){
        JSONObject defJson = JSON.parseObject(xmlDefineDesc);
        JSONArray nodes = defJson.getJSONArray("nodeList");
        for(Object nodeObj : nodes){
            String nodeId = ((JSONObject)nodeObj).getString("nodeId");
            ((JSONObject)nodeObj).put("nodeId", flowData.nodeTagToId.get(nodeId));
        }

        JSONArray trans = defJson.getJSONArray("transList");
        for(Object tranObj : trans){
            JSONObject transJson = (JSONObject)tranObj;
            String transId = transJson.getString("transId");
            transJson.put("transId", flowData.transTagToId.get(transId));
            String startNodeId = transJson.getString("startNodeId");
            transJson.put("startNodeId", flowData.nodeTagToId.get(startNodeId));
            String endNodeId = transJson.getString("endNodeId");
            transJson.put("endNodeId", flowData.nodeTagToId.get(endNodeId));
        }

        newFlowDef.setFlowXmlDesc(defJson.toJSONString());
    }

    private void resetNodeTransIdToXML(String xmlDefineDesc, FlowInfo newFlowDef, FlowDataDetail flowData){

        // 替换 流程XML格式中的节点、流转编码 对照表在 ndMap 和 trMap 中
        Document wfDefDoc = XmlUtils.string2xml(xmlDefineDesc);//flowDef.getFlowXmlDesc());
        List<Node> nodeList = wfDefDoc.selectNodes("//Nodes/Node");

        for (Node tmpNode : nodeList) {
            Element baseNode = (Element) tmpNode
                .selectSingleNode("BaseProperties");
            Attribute nodeIdAttr = baseNode.attribute("id");
            String sId = nodeIdAttr.getValue();
            if (BEGINNODETAG.compareToIgnoreCase(sId) != 0 && ENDNODETAG.compareToIgnoreCase(sId) != 0)
                nodeIdAttr.setValue(flowData.nodeTagToId.get(sId));
        }

        List<Node> transList = wfDefDoc.selectNodes("//Transitions/Transition");
        for (Node transNode : transList) {
            Element baseNode = (Element) transNode
                .selectSingleNode("BaseProperties");
            Attribute transIdAttr = baseNode.attribute("id");
            if (transIdAttr == null)
                continue;
            String sTransId = flowData.transTagToId.get(transIdAttr.getValue()).toString();
            transIdAttr.setValue(sTransId);

            Attribute fromAttr = baseNode.attribute("from");
            if (fromAttr != null) {
                String sId = fromAttr.getValue();
                if (BEGINNODETAG.compareToIgnoreCase(sId) != 0 && ENDNODETAG.compareToIgnoreCase(sId) != 0)
                    fromAttr.setValue(flowData.nodeTagToId.get(sId));
            }

            Attribute toAttr = baseNode.attribute("to");
            if (toAttr != null) {
                String sId = toAttr.getValue();
                if (BEGINNODETAG.compareToIgnoreCase(sId) != 0 && ENDNODETAG.compareToIgnoreCase(sId) != 0)
                    toAttr.setValue(flowData.nodeTagToId.get(sId));
            }

            Element labNode = (Element) transNode
                .selectSingleNode("LabelProperties");
            Attribute idAttr = labNode.attribute("id");
            if (idAttr != null)
                idAttr.setValue("lab" + sTransId);

        }
        newFlowDef.setFlowXmlDesc(wfDefDoc.asXML());
    }

    @Override
    @Transactional
    public FlowInfo getFlowInfo(String flowCode, long version) {
        try {
            return flowDefineDao.getObjectWithReferences(new FlowInfoId(version, flowCode));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @Transactional
    public FlowInfo getFlowInfo(String flowCode) {
        long version = flowDefineDao.getLastVersion(flowCode);
        return getFlowInfo(flowCode, version);
    }


    /**
     * 根据节点ID获得节点定义
     *
     * @param nodeId
     * @return
     */
    @Override
    @Transactional
    public NodeInfo getNodeInfoById(String nodeId) {
        return flowNodeDao.getObjectById(nodeId);
    }


    @Override
    @Transactional
    public String getFlowDefXML(String flowCode, long version) {
        // 获得某个特定版本的流程定义 文本
        FlowInfo flowDef = flowDefineDao.getObjectById(new FlowInfoId(version, flowCode));
        if (flowDef == null)
            return "";
        return flowDef.getFlowXmlDesc();
    }

    @Override
    @Transactional
    public String getFlowDefXML(String flowCode) {
        // 获得流程最新版本的流程定义 文本
        long nCurVersion = flowDefineDao.getLastVersion(flowCode);
        if (nCurVersion == 0)
            return null;
        return getFlowDefXML(flowCode, nCurVersion);
    }

    @Override
    @Transactional
    public void disableFlow(String flowCode) {
        //将最新版本的流程定义的状态更改为禁用 D
        FlowInfo flowDef = flowDefineDao.getLastVersionFlowByCode(flowCode);
        flowDef.setFlowState(FlowInfo.FLOW_STATE_FORBIDDEN);
        flowDefineDao.updateObject(flowDef);
    }

    @Override
    @Transactional
    public void enableFlow(String flowCode) {
        //将最新版本的流程定义的状态更改为正常 B
        FlowInfo flowDef = flowDefineDao.getLastVersionFlowByCode(flowCode);
        flowDef.setFlowState(FlowInfo.FLOW_STATE_NORMAL);
        flowDefineDao.updateObject(flowDef);
    }

    @Override
    @Transactional
    public List<FlowInfo> listFlowsByCode(String wfCode, PageDesc pageDesc) {
        List<FlowInfo> flows = flowDefineDao
            .getAllVersionFlowsByCode(wfCode, pageDesc);
        return new ArrayList<>(
            flows == null ? new ArrayList<>() : flows);
    }

    @Override
    @Transactional
    public List<FlowInfo> listLastVersionFlow(
        Map<String, Object> filterMap, PageDesc pageDesc) {
        List<FlowInfo> flows = flowDefineDao
            .listLastVersionFlows(filterMap, pageDesc);
        //获取草稿版本的状态（0版本的状态）
        for (FlowInfo def : flows) {
            FlowInfo wfDef = flowDefineDao
                .getObjectById(new FlowInfoId(0L, def.getFlowCode()));
            if (wfDef != null) {
                def.setFlowState(wfDef.getFlowState());
            }
        }
        return flows;
    }

    @Override
    @Transactional
    public Set<String> getUnitExp(String flowCode, Long version) {
        return flowNodeDao.getUnitExp(flowCode, version);
    }

    /**
     * 获取流程业务表单，用于流程业务定义
     *
     * @param filterMap 过滤条件
     * @param pageDesc  分页信息
     * @return 流程业务信息列表
     */
    @Override
    public List<FlowOptInfo> listOptInfo(Map<String, Object> filterMap, PageDesc pageDesc) {
        return flowOptInfoDao.listObjects(filterMap, pageDesc);
    }

    @Override
    @Transactional
    public void deleteFlowDef(String flowCode) {
        flowDefineDao.deleteObjectByFlowCode(flowCode);
    }

    /**
     * 例举所有节点操作类别
     */
    @Override
    public Map<String, String> listAllOptType() {
        Map<String, String> optType = new HashMap<>();
        optType.put("A", "一般");
        optType.put("B", "抢先机制");
        optType.put("C", "多人操作");
        //optType.put("D", "自动执行");
        //optType.put("S", "子流程");
        //optType.put("E", "哑元");
        return optType;
    }

    /**
     * 例举所有节点类别
     * A:开始 B:首节点 C:一般 D:分支 E:汇聚 F结束
     */
    @Override
    public Map<String, String> listAllNoteType() {
        Map<String, String> nodeType = new HashMap<>();
        nodeType.put("A", "开始");
        //nodeType.put("B", "首节点");
        nodeType.put("C", "业务");
        nodeType.put("R", "路由");
        //nodeType.put("E", "汇聚 ");
        nodeType.put("F", "结束");
        return nodeType;
    }

    /**
     * 读取工作定义的业务操作
     * @param flowCode 流程代码
     * @param version 版本号
     * @return 对应的业务操作
     */
    private Map<String, String> listAllOptCode(String flowCode, long version) {
        FlowInfo flowDef = this.flowDefineDao.getFlowDefineByID(flowCode, version);
        //FlowOptInfo flowOptInfo = flowOptInfoDao.getObjectById(flowDef.getOptId());
        List<FlowOptPage> wfOptDefs = flowOptPageDao.listObjectsByProperty("optId", flowDef.getOptId());
        Map<String, String> optMap = new HashMap<>();
        for (FlowOptPage f : wfOptDefs) {
            //optMap.put(flowOptInfo.getOptUrl() + f.getOptMethod(), f.getOptName());
            optMap.put(f.getOptCode(),f.getOptName());
        }
        return optMap;
    }

    /**
     * 读取工作定义的业务操作
     * @param flowCode 流程代码
     * @return 对应的业务操作
     */
    @Override
    @Transactional
    public Map<String, String> listAllOptCode(String flowCode) {
        return listAllOptCode(flowCode, 0L);
    }
    /**
     * 列举所有角色类别
     */
    @Override
    public Map<String, String> listRoleType(){
        Map<String, String> roleTypes = new HashMap<>();
        roleTypes.put(SysUserFilterEngine.ROLE_TYPE_GW, "岗位职责");
        roleTypes.put(SysUserFilterEngine.ROLE_TYPE_XZ, "行政职位");
        roleTypes.put(SysUserFilterEngine.ROLE_TYPE_ITEM, "办件角色");
        roleTypes.put(SysUserFilterEngine.ROLE_TYPE_SYSTEM, "系统角色");
        roleTypes.put(SysUserFilterEngine.ROLE_TYPE_ENGINE, "权限引擎");
        roleTypes.put(SysUserFilterEngine.ROLE_TYPE_ENGINE_FORMULA, "已定义表达式");
        // roleTypes.put("sp", "审批角色(烽火)");
        return roleTypes;
    }

    /**
     * 列举所有角色
     */
    @Override
    public Map<String, Map<String, String>> listAllRole() {
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext();
        Map<String, Map<String, String>> roleList = new HashMap<>();
        roleList.put(SysUserFilterEngine.ROLE_TYPE_GW /*"gw"*/, context.listAllStation());
        roleList.put(SysUserFilterEngine.ROLE_TYPE_XZ /*"xz"*/, context.listAllRank());
        //roleList.put(SysUserFilterEngine.ROLE_TYPE_ITEM /*"bj"*/, context.listAllProjectRole());
        roleList.put(SysUserFilterEngine.ROLE_TYPE_SYSTEM /*"ro"*/, context.listAllSystemRole());
        roleList.put(SysUserFilterEngine.ROLE_TYPE_ENGINE_FORMULA /*"sf"*/, flowRoleDao.listAllRoleMsg());
        return roleList;
    }

    /**
     * @param stype 角色类别
     * @return 角色名称和类别对应列表
     */
    @Override
    public Map<String, String> listRoleByType(String stype){
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext();

        if(SysUserFilterEngine.ROLE_TYPE_GW.equalsIgnoreCase(stype)){
            return context.listAllStation();
        } else if(SysUserFilterEngine.ROLE_TYPE_XZ.equalsIgnoreCase(stype)){
            return context.listAllRank();
        } else if(SysUserFilterEngine.ROLE_TYPE_ITEM.equalsIgnoreCase(stype)){
            return context.listAllProjectRole();
        } else if(SysUserFilterEngine.ROLE_TYPE_SYSTEM.equalsIgnoreCase(stype)){
            return context.listAllSystemRole();
        } else if(SysUserFilterEngine.ROLE_TYPE_ENGINE_FORMULA.equalsIgnoreCase(stype)){
            return flowRoleDao.listAllRoleMsg();
        } else {
            return null;
        }
    }

    /**
     * @return 内置的流程结构表达式
     */
    @Override
    public Map<String, String> listInsideUnitExp() {
        Map<String, String> optType = new HashMap<>();
        optType.put("D(P)", "同上一个节点");
        optType.put("D(F)", "同流程");
        optType.put("D(C)", "用户的机构");
        optType.put("D(cursor)", "多实例游标");
        optType.put("S(D(L),D(U))", "默认用上一个副本机构");
        return optType;
    }

    /**
     * 列举所有的子流程
     */
    @Override
    @Transactional
    public Map<String, String> listAllSubFlow() {
        Map<String, String> subwf = new HashMap<>();

        List<FlowInfo> listflow = flowDefineDao.getFlowsByState("B");
        for (FlowInfo wfFlowDefine : listflow) {
            subwf.put(wfFlowDefine.getFlowCode(), wfFlowDefine.getFlowName());
        }
        return subwf;
    }

    @Override
    @Transactional
    public Long getFlowLastVersion(String flowCode){
        return flowDefineDao.getLastVersion(flowCode);
    }

    @Override
    @Transactional
    public Map<String, String> listFlowItemRoles(String flowCode, Long version) {
        if(version == null || version < 0){
            version = flowDefineDao.getLastVersion(flowCode);
        }
        return optTeamRoleDao.getRoleByFlowCode(flowCode, version);
    }

    @Override
    @Transactional
    public OptTeamRole getFlowItemRole(String flowCode, Long version, String roleCode){
        return optTeamRoleDao.getItemRole(flowCode, version, roleCode);
    }

    /**
     * 获取流程阶段信息
     *
     * @param flowCode 流程代码和名称对应表
     * @return 流程阶段
     */
    @Override
    @Transactional
    public Map<String, String> listFlowStages(String flowCode, Long version) {
        if(version == null || version < 0){
            version = flowDefineDao.getLastVersion(flowCode);
        }

        FlowInfo flowDef = flowDefineDao.getFlowDefineByID(flowCode, version);//流程0版本读取
        flowDefineDao.fetchObjectReference(flowDef,"flowStages");

        Set<FlowStage> stageSet = flowDef.getFlowStagesSet();

        Map<String, String> optmap = new HashMap<>();

        if (stageSet != null && !stageSet.isEmpty()) {
            Iterator<? extends FlowStage> it = stageSet.iterator();
            while (it.hasNext()) {
                FlowStage stage = it.next();
                optmap.put(stage.getStageCode(), stage.getStageName());
            }
        }
        return optmap;
    }

    private List<OptVariableDefine> listOptVariables(String flowCode, Long version) {
        if(version == null || version < 0){
            version = flowDefineDao.getLastVersion(flowCode);
        }

        return optVariableDefineDao.getOptVariableByFlowCode(flowCode, version);
    }

    /**
     * 根据流程代码获取流程变量信息
     * @param flowCode 流程代码
     * @return 流程变量信息
     */
    @Override
    @Transactional
    public Map<String, String> listFlowVariableDefines(String flowCode, Long version) {
        List<OptVariableDefine> optVariableDefines
            = listOptVariables(flowCode, version);

        Map<String, String> variableDefineMap = new HashMap<>();
        if(optVariableDefines != null) {
            for (OptVariableDefine optVariableDefine : optVariableDefines) {
                variableDefineMap.put(optVariableDefine.getVariableName(), optVariableDefine.getVariableDesc());
            }
        }
        return variableDefineMap;
    }

    @Override
    @Transactional
    public Map<String, String> listFlowDefaultVariables(String flowCode, Long version){
        List<OptVariableDefine> optVariableDefines
            = listOptVariables(flowCode, version);

        Map<String, String> variableValueMap = new HashMap<>();
        if(optVariableDefines != null) {
            for (OptVariableDefine optVariableDefine : optVariableDefines) {
                if (StringUtils.isNotBlank(optVariableDefine.getDefaultValue())) {
                    variableValueMap.put(optVariableDefine.getVariableName(), optVariableDefine.getDefaultValue());
                }
            }
        }
        return variableValueMap;
    }

    @Override
    @Transactional
    public Map<String, String> listOptItemRoles(String optId) {
        return optTeamRoleDao.getRoleByOptId(optId);
    }

    @Override
    @Transactional
    public OptTeamRole getOptItemRole(String optId, String roleCode) {
        return optTeamRoleDao.getItemRole(optId, roleCode);
    }

    /**
     * 根据流程业务id获取流程变量信息
     * @param optId 流程代码
     * @return 流程变量信息
     */
    @Override
    @Transactional
    public Map<String, String> listOptVariableDefines(String optId) {
        List<OptVariableDefine> optVariableDefines
            = optVariableDefineDao.getOptVariableByOptId(optId);

        Map<String, String> variableDefineMap = new HashMap<>();
        if(optVariableDefines != null) {
            for (OptVariableDefine optVariableDefine : optVariableDefines) {
                variableDefineMap.put(optVariableDefine.getVariableName(), optVariableDefine.getVariableDesc());
            }
        }
        return variableDefineMap;
    }

    @Override
    @Transactional
    public Map<String, String> listOptDefaultVariables(String optId) {
        List<OptVariableDefine> optVariableDefines
            = optVariableDefineDao.getOptVariableByOptId(optId);

        Map<String, String> variableValueMap = new HashMap<>();
        if(optVariableDefines != null) {
            for (OptVariableDefine optVariableDefine : optVariableDefines) {
                if (StringUtils.isNotBlank(optVariableDefine.getDefaultValue())) {
                    variableValueMap.put(optVariableDefine.getVariableName(), optVariableDefine.getDefaultValue());
                }
            }
        }
        return variableValueMap;
    }

}
