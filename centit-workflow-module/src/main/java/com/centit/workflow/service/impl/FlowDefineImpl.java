package com.centit.workflow.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.components.SysUserFilterEngine;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitFilterCalcContextFactory;
import com.centit.framework.model.basedata.OptInfo;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.StringRegularOpt;
import com.centit.support.algorithm.UuidOpt;
import com.centit.support.common.CachedObject;
import com.centit.support.common.ObjectException;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.dao.*;
import com.centit.workflow.po.*;
import com.centit.workflow.service.FlowDefine;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Service
public class FlowDefineImpl implements FlowDefine, Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private FlowInfoDao flowDefineDao;

    @Autowired
    private NodeInfoDao flowNodeDao;

    @Autowired
    private RoleFormulaDao flowRoleDao;

    @Autowired
    private OptVariableDefineDao optVariableDefineDao;

    @Autowired
    private OptTeamRoleDao optTeamRoleDao;

    @Autowired
    private UserUnitFilterCalcContextFactory userUnitFilterFactory;

    @Autowired
    private FlowStageDao flowStageDao;

    @Autowired
    private FlowInstanceDao flowInstanceDao;

    @Autowired
    private PlatformEnvironment platformEnvironment;

    private static Logger logger = LoggerFactory.getLogger(FlowDefineImpl.class);
    public static final String BEGINNODETAG = "begin";
    public static final String ENDNODETAG = "end";

    private class FlowDataDetail {
        //这个Map放在这儿有一定的耦合性 重构成一个类
        public Map<String, String> nodeTagToId;
        public Map<String, String> transTagToId;

        public FlowDataDetail() {
            nodeTagToId = new HashMap<>();
            transTagToId = new HashMap<>();
        }
    }

    @PostConstruct
    public void registerDictionary() {
        CodeRepositoryUtil.registeExtendedCodeRepo(
            "flowCode", new CachedObject<>(() -> flowDefineDao.listFlowCodeNameMap())
        );
    }

    @Override
    @Transactional
    public JSONArray listFlowsByOptId(String optId) {
        List<FlowInfo> flows = flowDefineDao.listLastVersionFlowByOptId(optId);
        JSONArray flowDefineJsonArray = new JSONArray();
        if (CollectionUtils.sizeIsEmpty(flows)){
            return flowDefineJsonArray;
        }
        //添加流程实例个数 属性
        Object[] flowCodes = flows.stream().map(FlowInfo::getFlowCode).toArray();
        JSONArray jsonArray = flowInstanceDao.countFlowInstances(CollectionsOpt.createHashMap("flowCode",flowCodes));
        flows.forEach(flowInfo -> flowDefineJsonArray.add(combineFlowInstanceCount(jsonArray, flowInfo)));
        return flowDefineJsonArray;
    }

    @Override
    @Transactional
    public JSONArray listFlowByOsId(String osId) {
        List<FlowInfo> flows = flowDefineDao.getFlowsByOsId(osId, FlowInfo.FLOW_STATE_NORMAL);
        JSONArray flowDefineJsonArray = new JSONArray();
        if (CollectionUtils.sizeIsEmpty(flows)){
            return flowDefineJsonArray;
        }
        //添加流程实例个数 属性
        Object[] flowCodes = flows.stream().map(FlowInfo::getFlowCode).toArray();
        JSONArray jsonArray = flowInstanceDao.countFlowInstances(CollectionsOpt.createHashMap("flowCode",flowCodes));
        flows.forEach(flowInfo -> flowDefineJsonArray.add(combineFlowInstanceCount(jsonArray, flowInfo)));
        return flowDefineJsonArray;
    }

    @Override
    @Transactional
    public JSONArray listAllFlowsByOptId(String topUnit, String optId) {
        JSONArray flowDefineJsonArray = new JSONArray();
        List<FlowInfo> flows = flowDefineDao.listLastVersionFlowByOptId(optId);
        List<OptInfo> allOptInfos = platformEnvironment.listAllOptInfo(topUnit);
        List<FlowInfo> tyFlows = new ArrayList<FlowInfo>();
        OptInfo optInfo = null;
        String tyOptId = "";
        boolean flag = true;
        for(OptInfo obj : allOptInfos){
            if(obj.getOptId().equals(optId)){
                optInfo = obj;
            }
        }
        if(optInfo != null){
            for(OptInfo obj : allOptInfos){
                if(obj.getTopOptId().equals(optInfo.getTopOptId()) && obj.getOptName().equals(optInfo.OPT_INFO_FORM_CODE_COMMON_NAME)){
                    tyOptId = obj.getOptId();
                    if(tyOptId.equals(optId)){
                        flag = false;
                    }
                }
            }
        }
        //获取通用模块的流程
        if(flag){
            tyFlows = flowDefineDao.listLastVersionFlowByOptId(tyOptId);
        }
        flows.addAll(tyFlows);
        if (CollectionUtils.sizeIsEmpty(flows)){
            return flowDefineJsonArray;
        }
        //添加流程实例个数 属性
        Object[] flowCodes = flows.stream().map(FlowInfo::getFlowCode).toArray();
        JSONArray jsonArray = flowInstanceDao.countFlowInstances(CollectionsOpt.createHashMap("flowCode",flowCodes));

        flows.forEach(flowInfo -> flowDefineJsonArray.add(combineFlowInstanceCount(jsonArray, flowInfo)));
        return flowDefineJsonArray;
    }

    private FlowInfo createFlowDefByJSON(String jsonDef, String flowCode, Long version, FlowDataDetail flowData, FlowInfo flow) {
        FlowInfo flowDef = JSON.parseObject(jsonDef, FlowInfo.class);// new FlowInfo();
        flowDef.setCid(new FlowInfoId(version, flowCode));
        //getStartNode
        flowData.nodeTagToId.clear();
        flowData.transTagToId.clear();
        String startNodeId = null;
        if (StringUtils.isBlank(flow.getOptId())) {
            flow.setOptId(flow.getOptId());
        }
        if (StringUtils.isBlank(flow.getOsId())) {
            flow.setOsId(flow.getOsId());
        }
        for (NodeInfo node : flowDef.getNodeList()) {
            if (NodeInfo.NODE_TYPE_START.equals(node.getNodeType())) {
                startNodeId = node.getNodeId();
            }
            String thisNodeId = UuidOpt.getUuidAsString32();
            flowData.nodeTagToId.put(node.getNodeId(), thisNodeId);
            node.setNodeId(thisNodeId);
            // 节点的optId默认和流程的optId一致。
            if (StringUtils.isBlank(node.getOptId())) {
                node.setOptId(flow.getOptId());
            }
            // 节点的osId默认和流程的osId一致。
            if (StringUtils.isBlank(node.getOsId())) {
                node.setOsId(flow.getOsId());
            }
            //这个前端应该检测
            if (StringUtils.isBlank(node.getNodeName())) {
               if(NodeInfo.NODE_TYPE_END.equals(node.getNodeType())){
                   node.setNodeName("结束节点");
               } else if(NodeInfo.NODE_TYPE_ROUTE.equals(node.getNodeType())){
                   node.setNodeName("路由");
               } else if(NodeInfo.NODE_TYPE_START.equals(node.getNodeType())){
                    node.setNodeName("开始节点");
               } else if(NodeInfo.NODE_TYPE_AUTO.equals(node.getNodeType())){
                   node.setNodeName("自动执行");
               } else if(NodeInfo.NODE_TYPE_SYNC.equals(node.getNodeType())){
                   node.setNodeName("同步节点");
               } else {
                   node.setNodeName(StringUtils.isBlank(node.getNodeCode())?node.getNodeId():node.getNodeCode());
               }
            }
        }

        for (FlowTransition tran : flowDef.getTransList()) {
            String fromNodeId = flowData.nodeTagToId.get(tran.getStartNodeId());
            String toNodeId = flowData.nodeTagToId.get(tran.getEndNodeId());
            if (StringUtils.equals(startNodeId, tran.getStartNodeId())) {
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

    @Override
    @Transactional
    public boolean saveDraftFlowDef(FlowInfo wfDef) {
        // 将 流程定义格式保存到 版本为 0 的记录中
        FlowInfo flowDef = flowDefineDao.getObjectById(new FlowInfoId(0L, wfDef.getFlowCode()));
        if (flowDef == null) {
            wfDef.setVersion(0L);
            wfDef.setFlowClass("R");
            wfDef.setFlowState(FlowInfo.FLOW_STATE_DRAFT);
            flowDefineDao.saveNewObject(wfDef);
        }else {
            flowDef.copyNotNullProperty(wfDef);
            flowDef.setFlowState(FlowInfo.FLOW_STATE_DRAFT);//wfDef.getWfstate() == null ? "A":wfDef.getWfstate());
            flowDef.setFlowPublishDate(new Date(System.currentTimeMillis()));
            flowDefineDao.updateObject(flowDef);
        }
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
        flowDef.replaceFlowStages(wfDef.getFlowStages());
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
    public boolean saveDraftFlowDefJson(String flowCode, String flowDefXML) {
        FlowInfo flowDef = flowDefineDao.getObjectById(new FlowInfoId(0L, flowCode));
        if (flowDef == null) {
            flowDef = new FlowInfo();
            flowDef.setFlowCode(flowCode);
            flowDef.setFlowName(flowCode);
        }
        flowDef.setVersion(0L);//wfDef.getVersion()==null ? 0L : wfDef.getVersion());

        flowDef.setFlowState(FlowInfo.FLOW_STATE_DRAFT);//wfDef.getWfstate() == null ? "A":wfDef.getWfstate());
        flowDef.setFlowClass("R");
        // 获取流程信息
        JSONObject flowAttr = JSONObject.parseObject(new String(flowDefXML)).getJSONObject("attr");
        if (flowAttr != null) {
            flowDef.setFlowName(flowAttr.getString("flowName"));
            flowDef.setFlowDesc(flowAttr.getString("flowDesc"));
            flowDef.setTimeLimit(flowAttr.getString("timeLimit"));
            flowDef.setExpireOpt(flowAttr.getString("expireOpt"));
            flowDef.setExpireCallApi(flowAttr.getString("expireCallApi"));
            flowDef.setWarningParam(flowAttr.getString("warningParam"));
        }
        flowDef.setFlowXmlDesc(flowDefXML);

        flowDefineDao.mergeObject(flowDef);
        return true;
    }

    @Override
    @Transactional
    public String getDraftFlowDefJson(String flowCode) {
        // 版本号为 0 的流程定义中获得 XML
        return getFlowDefXML(flowCode, 0);
    }

    private void checkFlowDef(FlowInfo newFlowDef) {
        //验证流程节点定义
        for (NodeInfo nd : newFlowDef.getNodeList()) {
            if (NodeInfo.NODE_TYPE_SUBFLOW.equals(nd.getNodeType())) {
                if (StringRegularOpt.isNvl(nd.getSubFlowCode()))
                    throw new ObjectException("子流程节点：" + nd.getNodeName() + ",没有指定流程代码。");
            } else if (NodeInfo.NODE_TYPE_OPT.equals(nd.getNodeType())) {
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
            } else if (NodeInfo.NODE_TYPE_AUTO.equals(nd.getNodeType())) {
                if (StringUtils.isBlank(nd.getUnitExp())) {
                    nd.setUnitExp("D(P)");
                }
                if (StringUtils.isBlank(nd.getRoleType())) {
                    nd.setRoleType(SysUserFilterEngine.ROLE_TYPE_ENGINE);
                    nd.setPowerExp("U(P)");
                }
                if (NodeInfo.AUTO_NODE_OPT_CODE_CALL.equals(nd.getAutoRunType())
                    && StringRegularOpt.isNvl(nd.getOptCode())) {
                    throw new ObjectException("节点：" + nd.getNodeName() + ",没有指定业务操作代码。");
                } else if (NodeInfo.AUTO_NODE_OPT_CODE_BEAN.equals(nd.getAutoRunType())
                    && StringRegularOpt.isNvl(nd.getOptBean())) {
                    throw new ObjectException("自动运行节点：" + nd.getNodeName() + ",没有运行的bean。");
                } else if (NodeInfo.AUTO_NODE_OPT_CODE_SCRIPT.equals(nd.getAutoRunType())
                    && StringRegularOpt.isNvl(nd.getOptParam())) {
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
                if (nd != null && NodeInfo.NODE_TYPE_ROUTE.equals(nd.getNodeType()) &&
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

        if (StringUtils.isBlank(flowDef.getFlowXmlDesc())) {
            throw new ObjectException("流程没有内容，flowXmlDesc字段为空。");
        }

        // 获取新的版本号
        long nCurVersion = flowDefineDao.getLastVersion(flowDef.getFlowCode());
        Long newVersion = nCurVersion + 1L;

        FlowDataDetail flowData = new FlowDataDetail();
        FlowInfo newFlowDef =
            createFlowDefByJSON(flowDef.getFlowXmlDesc(), flowDef.getFlowCode(), newVersion, flowData, flowDef);
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

        resetNodeTransIdToJSON(flowDef.getFlowXmlDesc(), newFlowDef, flowData);
        // 保存新版本的流程,状态设置为正常
        newFlowDef.setFlowDesc(flowDef.getFlowDesc());
        newFlowDef.setFlowName(flowDef.getFlowName());
        newFlowDef.setFlowClass(flowDef.getFlowClass());
        newFlowDef.setOptId(flowDef.getOptId());
        newFlowDef.setTimeLimit(flowDef.getTimeLimit());
        newFlowDef.setExpireOpt(flowDef.getExpireOpt());
        newFlowDef.setExpireCallApi(newFlowDef.getExpireCallApi());
        newFlowDef.setWarningParam(newFlowDef.getWarningParam());

        newFlowDef.setFlowPublishDate(DatetimeOpt.currentUtilDate());
        //newFlowDef.setFirstNodeId(flowData.firstNodeId);
        newFlowDef.setFlowState(FlowInfo.FLOW_STATE_NORMAL);
        //复制相关节点信息
        //newFlowDef.getWfFlowStages()
        //修复数据遗漏的bug
        newFlowDef.setOsId(flowDef.getOsId());

        flowDefineDao.saveNewObject(newFlowDef);
        flowDefineDao.saveObjectReferences(newFlowDef);

        //将0版本更新为已发布
        flowDef.setFlowState(FlowInfo.FLOW_STATE_PUBLISHED);
        flowDefineDao.updateObject(flowDef);

        //将非0老版本流程状态改为已过期
        if (nCurVersion > 0) {
            FlowInfo oldflowDef = flowDefineDao.getObjectById(new FlowInfoId(nCurVersion, flowDef.getFlowCode()));
            if (oldflowDef != null) {
                oldflowDef.setFlowState(FlowInfo.FLOW_STATE_INVALID);
                flowDefineDao.updateObject(oldflowDef);
            }
        }
        return newVersion;
    }

    private void resetNodeTransIdToJSON(String xmlDefineDesc, FlowInfo newFlowDef, FlowDataDetail flowData) {
        JSONObject defJson = JSON.parseObject(xmlDefineDesc);
        JSONArray nodes = defJson.getJSONArray("nodeList");
        for (Object nodeObj : nodes) {
            String nodeId = ((JSONObject) nodeObj).getString("nodeId");
            ((JSONObject) nodeObj).put("nodeId", flowData.nodeTagToId.get(nodeId));
        }

        JSONArray trans = defJson.getJSONArray("transList");
        for (Object tranObj : trans) {
            JSONObject transJson = (JSONObject) tranObj;
            String transId = transJson.getString("transId");
            transJson.put("transId", flowData.transTagToId.get(transId));
            String startNodeId = transJson.getString("startNodeId");
            transJson.put("startNodeId", flowData.nodeTagToId.get(startNodeId));
            String endNodeId = transJson.getString("endNodeId");
            transJson.put("endNodeId", flowData.nodeTagToId.get(endNodeId));
        }

        newFlowDef.setFlowXmlDesc(defJson.toJSONString());
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
        optType.put("D", "自动执行");
        optType.put("S", "子流程");
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
     * 列举所有角色类别
     */
    @Override
    public Map<String, String> listRoleType() {
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
    public Map<String, Map<String, String>> listAllRole(String topUnit) {
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext(topUnit);
        Map<String, Map<String, String>> roleList = new HashMap<>();
        roleList.put(SysUserFilterEngine.ROLE_TYPE_GW /*"gw"*/, context.listAllStation());
        roleList.put(SysUserFilterEngine.ROLE_TYPE_XZ /*"xz"*/, context.listAllRank());
        //roleList.put(SysUserFilterEngine.ROLE_TYPE_ITEM /*"bj"*/, context.listAllProjectRole());
        roleList.put(SysUserFilterEngine.ROLE_TYPE_SYSTEM /*"ro"*/, context.listAllSystemRole());
        roleList.put(SysUserFilterEngine.ROLE_TYPE_ENGINE_FORMULA /*"sf"*/, flowRoleDao.listAllRoleMsg(topUnit));
        return roleList;
    }

    /**
     * @param stype 角色类别
     * @return 角色名称和类别对应列表
     */
    @Override
    public Map<String, String> listRoleByType(String stype, String topUnit) {

        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext(topUnit);
        if (SysUserFilterEngine.ROLE_TYPE_GW.equalsIgnoreCase(stype)) {
            return context.listAllStation();
        } else if (SysUserFilterEngine.ROLE_TYPE_XZ.equalsIgnoreCase(stype)) {
            return context.listAllRank();
        } /*else if(SysUserFilterEngine.ROLE_TYPE_ITEM.equalsIgnoreCase(stype)){
            return flowRoleDao.
        }*/ else if (SysUserFilterEngine.ROLE_TYPE_SYSTEM.equalsIgnoreCase(stype)) {
            return context.listAllSystemRole();
        } else if (SysUserFilterEngine.ROLE_TYPE_ENGINE_FORMULA.equalsIgnoreCase(stype)) {
            return flowRoleDao.listAllRoleMsg(topUnit);
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
    public Map<String, String> listAllSubFlow(String osId) {
        Map<String, String> subwf = new HashMap<>();

        List<FlowInfo> listflow = flowDefineDao.getFlowsByOsId(osId, "B");
        for (FlowInfo wfFlowDefine : listflow) {
            subwf.put(wfFlowDefine.getFlowCode(), wfFlowDefine.getFlowName());
        }
        return subwf;
    }

    @Override
    @Transactional
    public Long getFlowLastVersion(String flowCode) {
        return flowDefineDao.getLastVersion(flowCode);
    }

    @Override
    @Transactional
    public Map<String, String> listFlowItemRoles(String flowCode, Long version) {
        if (version == null || version < 0) {
            version = flowDefineDao.getLastVersion(flowCode);
        }
        return optTeamRoleDao.getRoleByFlowCode(flowCode, version);
    }

    @Override
    @Transactional
    public OptTeamRole getFlowItemRole(String flowCode, Long version, String roleCode) {
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
        if (version == null || version < 0) {
            version = flowDefineDao.getLastVersion(flowCode);
        }

        FlowInfo flowDef = flowDefineDao.getFlowDefineByID(flowCode, version);//流程0版本读取
        flowDefineDao.fetchObjectReference(flowDef, "flowStages");
        // 新建流程时，查询不到流程定义
        if (flowDef == null) {
            flowDef = new FlowInfo();
        }

        List<FlowStage> stageSet = flowDef.getFlowStages();

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
        if (version == null || version < 0) {
            version = flowDefineDao.getLastVersion(flowCode);
        }

        return optVariableDefineDao.listOptVariableByFlowCode(flowCode, version);
    }

    /**
     * 根据流程代码获取流程变量信息
     *
     * @param flowCode 流程代码
     * @return 流程变量信息
     */
    @Override
    @Transactional
    public Map<String, String> listFlowVariableDefines(String flowCode, Long version) {
        List<OptVariableDefine> optVariableDefines
            = listOptVariables(flowCode, version);

        Map<String, String> variableDefineMap = new HashMap<>();
        if (optVariableDefines != null) {
            for (OptVariableDefine optVariableDefine : optVariableDefines) {
                variableDefineMap.put(optVariableDefine.getVariableName(), optVariableDefine.getVariableDesc());
            }
        }
        return variableDefineMap;
    }

    @Override
    @Transactional
    public Map<String, String> listFlowDefaultVariables(String flowCode, Long version) {
        List<OptVariableDefine> optVariableDefines
            = listOptVariables(flowCode, version);

        Map<String, String> variableValueMap = new HashMap<>();
        if (optVariableDefines != null) {
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
     *
     * @param optId 流程代码
     * @return 流程变量信息
     */
    @Override
    @Transactional
    public Map<String, String> listOptVariableDefines(String optId) {
        List<OptVariableDefine> optVariableDefines
            = optVariableDefineDao.listOptVariableByOptId(optId);

        Map<String, String> variableDefineMap = new HashMap<>();
        if (optVariableDefines != null) {
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
            = optVariableDefineDao.listOptVariableByOptId(optId);

        Map<String, String> variableValueMap = new HashMap<>();
        if (optVariableDefines != null) {
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
    public void deleteFlowStageById(String stageId) {
        flowStageDao.deleteObjectById(stageId);
    }

    @Override
    public void saveFlowStage(FlowStage flowStage) {
        if (flowStage.getFlowCode() == null) {
            return;
        }
        if (flowStage.getVersion() == null) {
            flowStage.setVersion(0L);
        }
        if (flowStage.getStageId() == null) {
            // 流程阶段的stageCode不能重复
            HashMap<String, Object> filterMap = new HashMap<>();
            filterMap.put("flowCode", flowStage.getFlowCode());
            filterMap.put("stageCode", flowStage.getStageCode());
            FlowStage stage = flowStageDao.getObjectByProperties(filterMap);
            if (stage != null) {
                flowStage.setStageId(stage.getStageId());
            }
        }
        flowStageDao.mergeObject(flowStage);

        // 更新流程状态为草稿
        FlowInfo flowDef = flowDefineDao.getFlowDefineByID(flowStage.getFlowCode(), 0L);
        if (flowDef == null) {
            return;
        }
        if (!"A".equals(flowDef.getFlowState())) {
            flowDef.setFlowState(FlowInfo.FLOW_STATE_DRAFT);
            flowDef.setFlowClass("R");
            flowDefineDao.updateObject(flowDef);
        }
    }

    @Override
    @Transactional
    public int[] batchUpdateOptId(String optId, List<String> flowCodes) {
        String sql = "UPDATE wf_flow_define SET OPT_ID=? , FLOW_STATE = 'B' WHERE flow_code = ? ";
        return flowDefineDao.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, optId);
                ps.setString(2, flowCodes.get(i));
            }

            @Override
            public int getBatchSize() {
                return flowCodes.size();
            }
        });
    }

    @Override
    @Transactional
    public String copyWorkFlow(Map<String, Object> parameters) {
        String flowCode = MapUtils.getString(parameters, "flowCode");
        Long version = MapUtils.getLong(parameters, "version",0L);
        String flowName = MapUtils.getString(parameters, "flowName");
        FlowInfo flowInfo = flowDefineDao.getObjectById(new FlowInfoId(version, flowCode));
        if (null == flowInfo){
            //入参有误
            return null;
        }
        flowInfo.setFlowCode(UuidOpt.getUuidAsString32());
        flowInfo.setVersion(0L);
        flowInfo.setFlowName(flowName);
        flowInfo.setSourceId(flowInfo.getFlowCode());
        flowDefineDao.saveNewObject(flowInfo);
        return flowInfo.getFlowCode();
    }

    @Override
    public void deleteByCodes(List<String> flowCodes) {
        for (String flowCode : flowCodes) {
            this.deleteFlowDef(flowCode);
        }
    }

    @Override
    public void clearRecycle(String osId) {
        List<FlowInfo> list = flowDefineDao.getFlowsByOsId(osId, FlowInfo.FLOW_STATE_FORBIDDEN);
        if(list != null && list.size() > 0){
            for(FlowInfo flowInfo : list){
                this.deleteFlowDef(flowInfo.getFlowCode());
            }
        }
    }

    /**
     * 添加flowInfo中的实例个数
     * @param jsonArray 实例统计详情
     * @param flowInfo
     * @return
     */
    private JSONObject combineFlowInstanceCount(JSONArray jsonArray, FlowInfo flowInfo) {
        String flowCode = flowInfo.getFlowCode();
        JSONObject flowInfoJsonObject = JSON.parseObject(JSON.toJSONString(flowInfo));
        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) o;
            if (flowCode.equals(jsonObject.getString("flowCode"))){
                flowInfoJsonObject.put("nodeInstCount",jsonObject.getLongValue("count"));
                return flowInfoJsonObject;
            }
        }
        flowInfoJsonObject.put("nodeInstCount",0L);
        return flowInfoJsonObject;
    }
}
