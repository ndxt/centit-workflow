package com.centit.workflow.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.dde.adapter.DdeDubboTaskRun;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.components.SysUserFilterEngine;
import com.centit.framework.components.impl.ObjectUserUnitVariableTranslate;
import com.centit.framework.core.dao.DictionaryMapUtils;
import com.centit.framework.filter.RequestThreadLocal;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitFilterCalcContextFactory;
import com.centit.framework.model.adapter.UserUnitVariableTranslate;
import com.centit.framework.model.basedata.*;
import com.centit.support.algorithm.*;
import com.centit.support.common.LeftRightPair;
import com.centit.support.common.ObjectException;
import com.centit.support.common.WorkTimeSpan;
import com.centit.support.compiler.Pretreatment;
import com.centit.support.compiler.VariableFormula;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.commons.*;
import com.centit.workflow.dao.*;
import com.centit.workflow.po.*;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.FlowEventService;
import com.centit.workflow.service.FlowManager;
import com.centit.workflow.service.FlowScriptRunTime;
import com.centit.workflow.support.AutoRunNodeEventSupport;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

@Service
@Transactional
public class FlowEngineImpl implements FlowEngine, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(FlowEngineImpl.class);
    public static final String NODE_INST_ZERO = "0";

    @Autowired
    private FlowScriptRunTime flowScriptRunTime;

    @Autowired
    private FlowInstanceDao flowInstanceDao;
    @Autowired
    private UserUnitFilterCalcContextFactory userUnitFilterFactory;
    @Autowired
    private NodeInstanceDao nodeInstanceDao;
    @Autowired
    private NodeInfoDao flowNodeDao;
    @Autowired
    private FlowTransitionDao flowTransitionDao;
    @Autowired
    private ActionTaskDao actionTaskDao;
    @Autowired
    private FlowInfoDao flowDefDao;
    @Autowired
    private FlowOptPageDao flowOptPageDao;
    @Autowired
    private FlowManager flowManager;
    @Autowired
    private FlowWorkTeamDao flowTeamDao;
    @Autowired
    private RoleFormulaDao roleFormulaDao;
    @Autowired
    private FlowOrganizeDao flowOrganizeDao;
    @Autowired
    private FlowVariableDao flowVariableDao;
    @Autowired
    private InstAttentionDao attentionDao;
    @Autowired
    private FlowWarningDao runtimeWarningDao;
    @Autowired
    private FlowInstanceGroupDao flowInstanceGroupDao;
    @Autowired
    private NotificationCenter notificationCenter;
    @Autowired
    private OptVariableDefineDao optVariableDefineDao;
    @Autowired
    private FlowEventService flowEventService;
    @Autowired
    private StageInstanceDao stageInstanceDao;
    @Autowired
    private DdeDubboTaskRun ddeDubboTaskRun;
    private final static Object lockObject = new Object();

    public FlowEngineImpl() {
        //lockObject = new Object();
    }

    private void saveValueAndRoleInOptions(String flowInstId, String runToken,
                                           FlowOptParamOptions options) {
        // 设置流程变量
        if (options.getVariables() != null && !options.getVariables().isEmpty()) {
            for (Map.Entry<String, Object> ent : options.getVariables().entrySet()) {
                saveFlowNodeVariable(flowInstId, runToken, ent.getKey(), ent.getValue());
            }
        }
        // 设置全局流程变量
        if (options.getGlobalVariables() != null && !options.getGlobalVariables().isEmpty()) {
            for (Map.Entry<String, Object> ent : options.getGlobalVariables().entrySet()) {
                saveFlowVariable(flowInstId, ent.getKey(), ent.getValue());
            }
        }
        // 设置办件角色
        if (options.getFlowRoleUsers() != null && !options.getFlowRoleUsers().isEmpty()) {
            for (Map.Entry<String, List<String>> ent : options.getFlowRoleUsers().entrySet()) {
                assignFlowWorkTeam(flowInstId, ent.getKey(), runToken, ent.getValue());
            }
        }
        // 设置流程机构
        if (options.getFlowOrganizes() != null && !options.getFlowOrganizes().isEmpty()) {
            for (Map.Entry<String, List<String>> ent : options.getFlowOrganizes().entrySet()) {
                assignFlowOrganize(flowInstId, ent.getKey(), ent.getValue());
            }
        }
    }

    /**
     * 创建流程实例或子流程实例
     *
     * @param options     NewFlowInstanceOptions   流程编码
     *                    param nodeInstId 节点实例编号 ,节点编号不为0表示为子流程
     *                    param userCode   用户编码
     *                    param unitCode   机构编码
     * @param varTrans    UserUnitVariableTranslate 机构执行环境
     * @param application spring上下文环境。作为独立服务后这个应该不需要了
     * @return FlowInstance
     */
    @Override
    public FlowInstance createInstance(CreateFlowOptions options,
                                       UserUnitVariableTranslate varTrans,
                                       ServletContext application) {
        //查询重复的流程
        HashMap<String, Object> conditions = new HashMap<>(8);
        conditions.put("flowCode", options.getFlowCode());
        conditions.put("instState", "N");
        boolean hasCondition = false;
        if (StringUtils.isNotBlank(options.getFlowInstId())) {
            conditions.put("flowInstId", options.getFlowInstId());
            hasCondition = true;
        }
        if (StringUtils.isNotBlank(options.getModelId())) {
            conditions.put("optId", options.getModelId());
        }
        if (StringUtils.isNotBlank(options.getFlowOptTag())) {
            conditions.put("flowOptTag", options.getFlowOptTag());
            hasCondition = true;
        }

        FlowInstance instance = null;
        if (hasCondition) {
            instance = flowInstanceDao.getObjectByProperties(conditions);
        }
        if (instance == null) {
            instance = createInstanceInside(options, varTrans, application);
        }
        // 记录日志
        return instance;
    }

    /**
     * 创建流程实例  返回流程实例
     *
     * @param options NewFlowInstanceOptions 流程创建选项编码
     * @return 流程实例
     */
    @Override
    public FlowInstance createInstance(CreateFlowOptions options) {
        return createInstance(options,
            new ObjectUserUnitVariableTranslate(
                CollectionsOpt.unionTwoMap(
                    options.getVariables(), options.getGlobalVariables())),
            null);
    }

    private FlowInstance createInstanceInside(CreateFlowOptions options,
                                              UserUnitVariableTranslate varTrans,
                                              ServletContext application) {

        Date createTime = new Date(System.currentTimeMillis());
        if (options.getFlowVersion() < 1) {
            options.version(flowDefDao.getLastVersion(options.getFlowCode()));
        }
        //获取流程信息
        FlowInfo wf = flowDefDao.getFlowDefineByID(options.getFlowCode(), options.getFlowVersion());

        //获取流程实例编号
        String flowInstId = StringUtils.isBlank(options.getFlowInstId()) ?
            UuidOpt.getUuidAsString32() : options.getFlowInstId();

        FlowInstance flowInst = FlowOptUtils.createFlowInst(
            options.getUnitCode(), options.getUserCode(), wf, flowInstId, options.getTimeLimitStr());
        if (options.getModelId() != null) {
            flowInst.setOptId(options.getModelId());
        }
        flowInst.setCreateTime(createTime);
        flowInst.setFlowGroupId(options.getFlowGroupId());
        //节点实例编号不为空，为子流程，创建子流程时要给父节点的状态设置为 W：等待子流程返回
        if (StringUtils.isNotBlank(options.getParentNodeInstId())) {
            flowInst.setPreNodeInstId(options.getParentNodeInstId());
            flowInst.setPreInstId(options.getParentFlowInstId());

            FlowInstance parentInst = flowInstanceDao.getObjectById(options.getParentFlowInstId());
            //如果没有指定 分组，使用父节点分组
            if (StringUtils.isBlank(options.getFlowGroupId())
                && parentInst != null
                && StringUtils.isNotBlank(parentInst.getFlowGroupId())) {
                // 子流程继承父流程的 流程实例组
                flowInst.setFlowGroupId(parentInst.getFlowGroupId());
            }
            flowInst.setIsSubInst(true);
        }
        flowInst.setFlowOptName(options.getFlowOptName());
        flowInst.setFlowOptTag(options.getFlowOptTag());
        flowInstanceDao.saveNewObject(flowInst);
        // ----------- 创建 流程 实例 结束
        for (StageInstance flowStageInstance : flowInst.getFlowStageInstances()) {
            stageInstanceDao.saveNewObject(flowStageInstance);
        }
        //----------- 创建 流程阶段实例
        saveValueAndRoleInOptions(flowInstId, "T", options);
        //生成首节点实例编号
        NodeInfo node = wf.getFirstNode();
        if (node == null) {
            throw new WorkflowException(WorkflowException.FlowDefineError, "找不到首节点");
        }
        FlowVariableTranslate flowVarTrans = FlowOptUtils.createVariableTranslate(
            null, flowInst, flowVariableDao, this, options);
        flowVarTrans.setFlowVarTrans(varTrans);

        List<String> nodeInsts = submitToNextNode(node, "T", flowInst, wf,
            null, null, null,
            options, flowVarTrans, application);

        if (options.isSkipFirstNode() && !NodeInfo.NODE_TYPE_ROUTE.equals(node.getNodeType()) && nodeInsts.size() == 1) {
            nodeInsts = submitOptInside(SubmitOptOptions.create()
                    .copy(options).nodeInst(nodeInsts.iterator().next()),
                varTrans, application, false, false, options.isSkipFirstNode());
        }

        OperationLogCenter.log(FlowOptUtils.createActionLog(
            options.getUserCode(), flowInstId, "创建流程，创建首节点:" +
                StringBaseOpt.castObjectToString(nodeInsts)));
        return flowInst;
    }

    @Override
    public FlowInstance getFlowInstById(String flowInstId) {
        return flowInstanceDao.getObjectWithReferences(flowInstId);
    }

    /**
     * 获取流程定义信息
     *
     * @param flowInstId 实例id
     * @return 流程定义信息
     */
    @Override
    public FlowInfo getFlowDefine(String flowInstId) {
        FlowInstance flowInst = flowInstanceDao.getObjectById(flowInstId);
        if (flowInst == null) {
            return null;
        }
        return flowDefDao.getFlowDefineByID(
            flowInst.getFlowCode(), flowInst.getVersion());
    }


    @Override
    public NodeInstance getNodeInstById(String nodeInstId) {
        return nodeInstanceDao.getObjectWithReferences(nodeInstId);
    }

    /**
     * 获取节点定义信息
     *
     * @param nodeInstId 节点实例id
     * @return 节点实例信息
     */
    @Override
    public NodeInfo getNodeInfo(String nodeInstId) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null) {
            return null;
        }
        return flowNodeDao.getObjectById(nodeInst.getNodeId());
    }

    @Override
    public List<FlowInstance> listAllFlowInstByOptTag(String flowOptTag) {
        return flowInstanceDao.listAllFlowInstByOptTag(flowOptTag);
    }

    @Override
    public void updateFlowInstOptInfo(String flowInstId, String flowOptName, String flowOptTag) {
        flowInstanceDao.updateFlowInstOptInfo(flowInstId, flowOptName, flowOptTag);
    }

    /**
     * 设置节点实例参数
     *
     * @param nodeInstId 节点实例id
     * @param nodeParam  节点实例参数
     */
    @Override
    public void updateNodeInstanceParam(String nodeInstId, String nodeParam) {
        nodeInstanceDao.updtNodeInstParam(nodeInstId, nodeParam);
    }

    /**
     * 针对 抢先类别的 节点， 锁定任务，这个任务后续只能由 他来做
     *
     * @param nodeInstId 节点实例id
     * @param userCode   用户
     */
    @Override
    public void lockNodeTask(String nodeInstId, String userCode) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (NodeInstance.RUN_TOKEN_GLOBAL.equals(nodeInst.getTaskAssigned())) {
            NodeInfo node = flowNodeDao.getObjectById(nodeInst.getNodeId());
            //B: 抢先机制
            if (NodeInfo.OPT_RUN_TYPE_LEAD.equals(node.getOptRunType())) {
                nodeInst.setUserCode(userCode);
                nodeInst.setTaskAssigned("S");
                nodeInstanceDao.updateObject(nodeInst);
            }
        }
    }

    @Override
    public void updateFlowInstParentNode(String flowInstId, String parentFlowInstId,
                                         String parentNodeInstId) {
        FlowInstance flowInst = flowInstanceDao.getObjectById(flowInstId);
        if (flowInst == null) {
            return;
        }
        flowInst.setPreInstId(parentFlowInstId);
        flowInst.setPreNodeInstId(parentNodeInstId);
        flowInstanceDao.updateObject(flowInst);
    }

    @Override
    public String getNodeOptUrl(String nodeInstId, String userCode) {

        List<UserTask> taskList = actionTaskDao.listUserTaskByFilter(
            CollectionsOpt.createHashMap("nodeInstId", nodeInstId), new PageDesc(-1, -1));

        if (taskList == null || taskList.size() == 0) {
            return null;
        } else {
            UserTask task = taskList.get(0);
            return task.getNodeOptUrl();
        }
    }

    /**
     * 列举当前流程可以创建的所有节点
     *
     * @param flowInstId 流程实例代码
     * @return Map 节点代码， 节点名称
     */
    @Override
    public Map<String, String> listFlowNodeForCreate(String flowInstId) {
        FlowInstance flowInst = flowInstanceDao.getObjectById(flowInstId);
        if (flowInst == null) {
            return null;
        }
        List<NodeInfo> nodeList = flowNodeDao.listObjects(
            CollectionsOpt.createHashMap("flowCode", flowInst.getFlowCode(),
                "version", flowInst.getVersion()));
        Map<String, String> nodes = new HashMap<>();
        for (NodeInfo node : nodeList) {
            if (StringUtils.isNotBlank(node.getNodeCode())) {
                nodes.put(node.getNodeCode(), node.getNodeName());
            }
        }
        return nodes;
    }

    @Override
    public List<NodeInstance> listNodeInstanceByNodeCode(String flowInstId, String nodeCode) {
        FlowInstance flowInst = flowInstanceDao.getObjectById(flowInstId);
        if (flowInst == null) {
            return null;
        }
        List<NodeInfo> nodeList = flowNodeDao.listNodeByNodecode(flowInst.getFlowCode(),
            flowInst.getVersion(), nodeCode);
        if (nodeList == null) {
            return null;
        }
        List<NodeInstance> nodeInstList = new ArrayList<>();
        for (NodeInfo node : nodeList) {
            nodeInstList.addAll(
                flowInst.getAllNodeInstancesByNodeid(node.getNodeId()));
        }
        return nodeInstList;
    }

    @Override
    public StageInstance getStageInstByNodeInstId(String nodeInstId) {

        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null) {
            return null;
        }
        FlowInstance flowInst = flowInstanceDao.getObjectById(nodeInst.getFlowInstId());
        if (flowInst == null) {
            return null;
        }
        NodeInfo nodeinfo = flowNodeDao.getObjectById(nodeInst.getNodeId());
        if (nodeinfo == null) {
            return null;
        }
        return flowInst.getStageInstanceByCode(nodeinfo.getStageCode());
    }

    private void endFlowInstance(FlowInstance flowInst, FlowInfo flowInfo, NodeInfo endNode,
                                 String transPath, FlowTransition trans,
                                 String preNodeInstId, String userCode, String unitCode,
                                 FlowVariableTranslate varTrans) {
        FlowOptUtils.endInstance(flowInst, "C", userCode, flowInstanceDao);
        NodeInstance endNodeInst =
            FlowOptUtils.createNodeInst(unitCode, userCode, flowInst,
                null, flowInfo, endNode, trans, varTrans);
        endNodeInst.setNodeInstId(UuidOpt.getUuidAsString32());
        endNodeInst.setNodeState("C");
        Date updateTime = DatetimeOpt.currentUtilDate();
        endNodeInst.setLastUpdateTime(updateTime);
        endNodeInst.setLastUpdateUser(userCode);
        endNodeInst.setPrevNodeInstId(preNodeInstId);
        if (trans != null) {
            if (StringUtils.isBlank(transPath)) {
                endNodeInst.setTransPath(trans.getTransId());
            } else {
                endNodeInst.setTransPath(transPath + "," + trans.getTransId());
            }
        } else {
            endNodeInst.setTransPath(transPath);
        }
        nodeInstanceDao.saveNewObject(endNodeInst);
    }

    private UserUnitFilterCalcContext createCalcUserUnitContext(FlowInstance flowInst,
                                                                NodeInstance preNodeInst,
                                                                String nodeToken,
                                                                NodeInfo nextOptNode,
                                                                FlowOptParamOptions options,
                                                                FlowVariableTranslate varTrans) {
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext();
        context.setVarTrans(varTrans);
        //构建内置变量
        NodeInstance oldNodeInst = flowInst.findLastSameNodeInst(nextOptNode.getNodeId(), preNodeInst, "");
        // L 上一次运行到本节点的 用户和机构
        if (oldNodeInst != null) {
            context.addUnitParam("L", oldNodeInst.getUnitCode());
            context.addUserParam("L", oldNodeInst.getLastUpdateUser());
        }
        // P 前面一个节点的 用户和机构
        NodeInstance preNode = flowInst.getNearestNode(preNodeInst, nodeToken);
        if (preNode != null) {
            context.addUnitParam("P", preNode.getUnitCode());
            context.addUserParam("P", preNode.getUserCode());
        }
        // C 参数指定的，就是提交的人和机构
        String currUnitCode = options.getUnitCode();
        if (options.getUnitCode() == null && options.getUserCode() != null) {
            IUserInfo ui = context.getUserInfoByCode(options.getUserCode());
            if (ui != null) {
                currUnitCode = ui.getPrimaryUnit();
            }
        }
        context.addUnitParam("C", currUnitCode);
        context.addUserParam("C", StringUtils.isBlank(options.getUserCode()) ? preNodeInst.getUserCode() : options.getUserCode());
        // F 流程的 用户 和 机构
        context.addUnitParam("F", flowInst.getUnitCode());
        context.addUserParam("F", flowInst.getUserCode());
        return context;
    }

    private LeftRightPair<Set<String>, Set<String>> calcNodeUnitAndOperator(FlowInstance flowInst, NodeInstance preNodeInst, String nodeToken, NodeInfo nextOptNode, FlowOptParamOptions options, FlowVariableTranslate varTrans) {
        UserUnitFilterCalcContext context = createCalcUserUnitContext(flowInst,
            preNodeInst, nodeToken, nextOptNode, options, varTrans);
        return calcNodeUnitAndOperator(context, flowInst,
            nodeToken, nextOptNode, options);
    }

    private LeftRightPair<Set<String>, Set<String>> calcNodeUnitAndOperator(UserUnitFilterCalcContext context, FlowInstance flowInst, String nodeToken, NodeInfo nextOptNode, FlowOptParamOptions options) {
        // 参数指定
        Set<String> nodeUnits = null;
        if (options.getNodeUnits() != null) {
            String nodeUnit = options.getNodeUnits().get(nextOptNode.getNodeId());
            if (StringUtils.isBlank(nodeUnit)) {
                nodeUnit = options.getNodeUnits().get(nextOptNode.getNodeCode());
            }
            if (StringUtils.isNotBlank(nodeUnit)) {
                nodeUnits = CollectionsOpt.createHashSet(nodeUnit);
            }
        }
        //调用机构引擎来计算 unitCode 如果指定机构就不计算
        if (CollectionUtils.isEmpty(nodeUnits)) {
            nodeUnits = UserUnitCalcEngine.calcUnitsByExp(context,
                nextOptNode.getUnitExp());
        }
        if (CollectionUtils.isNotEmpty(nodeUnits)) {
            // 将 机构表达式
            context.addUnitParam("N", nodeUnits);
        }
        // 强制锁定用户 优先级最好
        if (options.isLockOptUser()) {
            String optUser = options.getWorkUserCode();
            if (StringUtils.isNotBlank(optUser)) {
                return new LeftRightPair<>(nodeUnits, CollectionsOpt.createHashSet(optUser));
            }
        }
        // 通过节点 映射
        Set<String> optUsers = null;
        if (options.getNodeOptUsers() != null) {
            optUsers = options.getNodeOptUsers().get(nextOptNode.getNodeId());
            if (optUsers != null && !optUsers.isEmpty()) {
                return new LeftRightPair<>(nodeUnits, optUsers);
            }
            optUsers = options.getNodeOptUsers().get(nextOptNode.getNodeCode());
            if (optUsers != null && !optUsers.isEmpty()) {
                return new LeftRightPair<>(nodeUnits, optUsers);
            }
        }
        // 流程引擎 权限体系
        if (SysUserFilterEngine.ROLE_TYPE_ENGINE.equalsIgnoreCase(nextOptNode.getRoleType())
            || SysUserFilterEngine.ROLE_TYPE_ENGINE_FORMULA.equalsIgnoreCase(nextOptNode.getRoleType())) {
            // 权限表达式
            String roleFormula = nextOptNode.getPowerExp();
            if (SysUserFilterEngine.ROLE_TYPE_ENGINE_FORMULA.equalsIgnoreCase(nextOptNode.getRoleType())) {
                RoleFormula rf = roleFormulaDao.getObjectById(nextOptNode.getRoleCode());
                if (rf != null) {
                    roleFormula = rf.getRoleFormula();
                }
            }
            //如果节点的角色类别为 权限引擎则要调用权限引擎来分配角色
            //根据权限表达式创建任务列表
            optUsers = UserUnitCalcEngine.calcOperators(context, roleFormula);
            if (optUsers == null || optUsers.size() == 0) {
                logger.error("权限引擎没有识别出符合表达式的操作人员：" + roleFormula + "，flow_inst_id:" + flowInst.getFlowInstId()
                    + ",node_inst_id:" + nextOptNode.getNodeId() + "。");
            }
        } else if (SysUserFilterEngine.ROLE_TYPE_ITEM.equalsIgnoreCase(nextOptNode.getRoleType())) {
            optUsers = new HashSet<>();
            List<FlowWorkTeam> users = flowTeamDao.listFlowWorkTeamByRole(flowInst.getFlowInstId(),
                nextOptNode.getRoleCode());
            //nodeToken
            String currNodeToken = null;
            for (FlowWorkTeam u : users) {
                //匹配令牌
                if (StringUtils.equals(currNodeToken, u.getRunToken())) {
                    optUsers.add(u.getUserCode());
                } else {
                    boolean chargeToken = StringUtils.equals(nodeToken, u.getRunToken())
                        || currNodeToken == null || StringUtils.isBlank(currNodeToken)
                        || (nodeToken.startsWith(u.getRunToken())
                        && (!nodeToken.startsWith(currNodeToken) || u.getRunToken().length() > currNodeToken.length()));
                    if (chargeToken) {
                        optUsers.clear();
                        currNodeToken = u.getRunToken();
                        optUsers.add(u.getUserCode());
                    }
                }
            }
            //流程角色（审批角色）待测试
        } else/*gw xz ro(system)*/
            if (StringUtils.isNotBlank(nextOptNode.getRoleType())
                && StringUtils.isNotBlank(nextOptNode.getRoleCode())) {
                String unitCode = CollectionUtils.isEmpty(nodeUnits) ? null : nodeUnits.iterator().next();
                optUsers = SysUserFilterEngine.getUsersByRoleAndUnit(context,
                    nextOptNode.getRoleType(), nextOptNode.getRoleCode(), unitCode);
            }
        return new LeftRightPair<>(nodeUnits, optUsers);
    }

    private void closeNodeInstanceInside(Collection<NodeInstance> sameNodes, Date currentTime, String userCode) {
        for (NodeInstance ni : sameNodes) {
            if ("W".equals(ni.getNodeState())) { //结束子流程
                FlowInstance subFlowInst = flowInstanceDao.getObjectById(ni.getSubFlowInstId());
                if (subFlowInst != null) {
                    FlowOptUtils.endInstance(subFlowInst, "F", userCode, flowInstanceDao);
                }
            }
            ni.setNodeState("F");// 节点设置为无效
            ni.setLastUpdateTime(currentTime);
            ni.setLastUpdateUser(userCode);
            nodeInstanceDao.updateObject(ni);
        }
    }

    private List<String> submitToNextRouterNode(
        NodeInfo nextRouterNode, String nodeToken, FlowInstance flowInst, FlowInfo flowInfo,
        NodeInstance preNodeInst/*创建首节点时为null*/, String transPath, FlowTransition trans,
        FlowOptParamOptions options,
        FlowVariableTranslate flowVarTrans, ServletContext application) {

        String routerType = nextRouterNode.getRouterType();
        List<String> resNodes = new ArrayList<>();

        StringBuilder preTransPath = new StringBuilder();
        if (trans != null) {
            preTransPath = new StringBuilder(StringUtils.isBlank(transPath) ?
                trans.getTransId() : transPath + "," + trans.getTransId());
        }

        if (NodeInfo.ROUTER_TYPE_PARALLEL.equals(routerType) || NodeInfo.ROUTER_TYPE_BRANCH.equals(routerType)) {
            //D 分支和 H 并行
            //提交游离分支上的叶子节点 将不会向后流转
            //获取下一批流转节点
            Set<FlowTransition> selTrans = selectTransitions(flowInst, preNodeInst,
                nodeToken, nextRouterNode, options, flowVarTrans);
            if (selTrans.size() < 1) {
                // 这个表示游离节点如果没有后续节点直接返回
                if (nodeToken.startsWith(NodeInstance.RUN_TOKEN_ISOLATED)) {
                    return resNodes;
                } else {
                    String errorMsg = "找不到后续节点：" + flowInst.getFlowInstId() +
                        (preNodeInst != null ? "; 节点：" + preNodeInst.getNodeInstId() : ";") +
                        " 路由：" + nextRouterNode.getNodeId();
                    logger.error(errorMsg);
                    throw new WorkflowException(WorkflowException.NotFoundNextNode, errorMsg);
                }
            }
            // D:分支 E:汇聚  G 多实例节点  H并行  R 游离 S：同步
            if (NodeInfo.ROUTER_TYPE_BRANCH.equals(routerType)) {
                // 分支节点只执行第一个符合条件的，如果有多个符合条件的 按道理应该报异常
                // 这里为了最大可运行不报异常
                FlowTransition nodeTran = selTrans.iterator().next();
                String nextNodeId = nodeTran.getEndNodeId();
                resNodes = submitToNextNode(
                    flowNodeDao.getObjectById(nextNodeId), nodeToken, flowInst, flowInfo,
                    preNodeInst, preTransPath.toString(), nodeTran, options,
                    flowVarTrans, application);
            } else {
                int nNo = 1; //子令牌序号
                for (FlowTransition tran : selTrans) {
                    String nextNodeId = tran.getEndNodeId();
                    List<String> nNs = submitToNextNode(
                        flowNodeDao.getObjectById(nextNodeId), nodeToken + "." + nNo,
                        flowInst, flowInfo,
                        preNodeInst, preTransPath.toString(), tran, options,
                        flowVarTrans, application);
                    resNodes.addAll(nNs);
                    nNo++;
                }
            }
        } else {
            if (NodeInfo.ROUTER_TYPE_COLLECT.equals(routerType)) {
                String preRunToken = NodeInstance.calcSuperToken(nodeToken);
                Set<String> nNs = // 所有未提交的需要等待的 子节点令牌
                    flowInst.calcNoSubmitSubNodeTokensInstByToken(preRunToken);
                //汇聚节点，所有节点都已提交,或者只要当前节点
                boolean canSubmit = nNs == null || nNs.size() == 0 ||
                    (nNs.size() == 1 && nNs.contains(nodeToken));
                //A 所有都完成，R 至少有X完成，L 至多有X未完成， V 完成比率达到X
                String convergeType = nextRouterNode.getConvergeType();
                if (!canSubmit && !NodeInfo.ROUTER_COLLECT_TYPE_ALL_COMPLETED.equals(convergeType)) {
                    // 除了要找到汇聚节点所有已经提交的子节点外 还要添加当前正在提交的节点（视正在提交中的节点为已办理节点）
                    Set<String> submitNodeIds = flowInst.calcSubmitSubNodeIdByToken(preRunToken, preNodeInst);
                    // 获取指向汇聚节点的流程线
                    List<FlowTransition> transList =
                        flowTransitionDao.getNodeInputTrans(nextRouterNode.getNodeId());
                    canSubmit = true;
                    for (FlowTransition tran : transList) {
                        if (!tran.getCanIgnore() && !submitNodeIds.contains(tran.getStartNodeId())) {
                            canSubmit = false;
                            break;
                        }
                    }

                    if (canSubmit) {
                        if (StringRegularOpt.isNumber(nextRouterNode.getConvergeParam())) {
                            if (NodeInfo.ROUTER_COLLECT_TYPE_LEAST_COMPLETED.equals(convergeType)) {
                                canSubmit = submitNodeIds.size() >=
                                    Integer.parseInt(nextRouterNode.getConvergeParam());
                            } else if (NodeInfo.ROUTER_COLLECT_TYPE_MOST_UNCOMPLETED.equals(convergeType)) {
                                canSubmit = nNs.size() <= Integer.parseInt(nextRouterNode.getConvergeParam());
                            } else if (NodeInfo.ROUTER_COLLECT_TYPE_RATE.equals(convergeType)) {
                                canSubmit = (double) submitNodeIds.size() / (double) (submitNodeIds.size() + nNs.size())
                                    >= Double.parseDouble(nextRouterNode.getConvergeParam());
                            } else {
                                canSubmit = false;
                            }
                        } else {
                            canSubmit = false;
                        }
                    }
                }

                if (canSubmit) {
                    Set<NodeInstance> sameNodes = flowInst.findAllActiveSubNodeInstByToken(preRunToken);
                    // 移除待提交节点
                    sameNodes.removeIf(
                        nodeInst -> preNodeInst.getNodeInstId().equals(nodeInst.getNodeInstId())
                    );
                    if (!sameNodes.isEmpty()) {
                        //结束这些节点
                        Date currentTime = new Date(System.currentTimeMillis());
                        closeNodeInstanceInside(sameNodes, currentTime, options.getUserCode());
                    }

                    FlowTransition nodeTran = selectOptNodeTransition(nextRouterNode);
                    String nextNodeId = "";
                    if (nodeTran != null) {
                        nextNodeId = nodeTran.getEndNodeId();
                    }
                    for (FlowTransition f : flowInfo.getTransList()) {
                        if (nextRouterNode.getNodeId().equals(f.getEndNodeId()) && trans != null && !f.getTransId().equals(trans.getTransId())) {
                            preTransPath.append(",").append(f.getTransId());
                        }
                    }
                    resNodes = submitToNextNode(
                        flowNodeDao.getObjectById(nextNodeId), preRunToken, flowInst, flowInfo,
                        preNodeInst, preTransPath.toString(), nodeTran, options,
                        flowVarTrans, application);
                }
            } else if (NodeInfo.ROUTER_TYPE_MULTI_INST.equals(routerType)) {//多实例
                FlowTransition nodeTran = selectOptNodeTransition(nextRouterNode);
                String nextNodeId = "";
                if (nodeTran != null) {
                    nextNodeId = nodeTran.getEndNodeId();
                }
                NodeInfo nextNode = flowNodeDao.getObjectById(nextNodeId);

                LeftRightPair<Set<String>, Set<String>> unitAndUsers = calcNodeUnitAndOperator(flowInst, preNodeInst, nodeToken,
                    nextRouterNode, options,
                    flowVarTrans);

                //D 机构， U  人员（权限表达式） V 变量
                if (NodeInfo.ROUTER_MULTI_TYPE_UNIT.equals(nextRouterNode.getMultiInstType())) {
                    Set<String> nextNodeUnits = unitAndUsers.getLeft();

                    if (nextNodeUnits == null || nextNodeUnits.size() == 0) { //报错
                        throw new WorkflowException(WorkflowException.NoValueForMultiInst,
                            "多实例节点对应的机构变量为空：" + flowInst.getFlowInstId() +
                                (preNodeInst != null ? "; 节点：" + preNodeInst.getNodeInstId() : ";") +
                                " 路由：" + nextRouterNode.getNodeId());
                    } else {
                        int nRn = 1;
                        for (String uc : nextNodeUnits) {
                            // 持久变量，供后续节点使用
                            this.saveFlowNodeVariable(flowInst.getFlowInstId(), nodeToken + "." + nRn,
                                "cd_" + GeneralAlgorithm.nvl(nextRouterNode.getNodeCode(), "unit") , uc);
                            String runToken = preNodeInst == null ? "T" : preNodeInst.getRunToken();
                            flowVarTrans.setInnerVariable("cursor", runToken, uc);
                            resNodes.addAll(submitToNextNode(
                                nextNode, nodeToken + "." + nRn, flowInst, flowInfo,
                                preNodeInst, preTransPath.toString(), nodeTran, options,
                                flowVarTrans,
                                application));
                            nRn++;
                        }
                    }
                } else if (NodeInfo.ROUTER_MULTI_TYPE_USER.equals(nextRouterNode.getMultiInstType())) {
                    Set<String> optUsers = unitAndUsers.getRight();
                    if (optUsers == null || optUsers.size() == 0) {
                        throw new WorkflowException(WorkflowException.NoValueForMultiInst,
                            "多实例节点对应的权限表达式人员为空：" + flowInst.getFlowInstId() +
                                (preNodeInst != null ? "; 节点：" + preNodeInst.getNodeInstId() : ";") +
                                " 路由：" + nextRouterNode.getNodeId());
                    } else {
                        int nRn = 1;
                        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext();
                        for (String uc : optUsers) {
                            // 持久变量，供后续节点使用
                            this.saveFlowNodeVariable(flowInst.getFlowInstId(), nodeToken + "." + nRn,
                                "cu_"  + GeneralAlgorithm.nvl(nextRouterNode.getNodeCode(), "user") , uc);
                            // 创建首节点时preNodeInst为null， 默认token为T
                            String runToken = preNodeInst == null ? "T" : preNodeInst.getRunToken();
                            flowVarTrans.setInnerVariable("cursor", runToken, uc);
                            resNodes.addAll(submitToNextNode(
                                nextNode, nodeToken + "." + nRn, flowInst, flowInfo,
                                preNodeInst, preTransPath.toString(), nodeTran,
                                SubmitOptOptions.create().copy(options).workUser(uc).lockOptUser(true)
                                    .unit(context.getUserInfoByCode(options.getUserCode()).getPrimaryUnit()),
                                flowVarTrans,
                                application));
                            nRn++;
                        }
                    }
                } else {
                    // 实现变量多实例 这个没有实际意义
                    throw new WorkflowException(WorkflowException.FlowDefineError,
                        "多实例路由目前只支持人员和机构数组：" + flowInst.getFlowInstId() +
                            (preNodeInst != null ? "; 节点：" + preNodeInst.getNodeInstId() : ";") +
                            " 路由：" + nextRouterNode.getNodeId());
                }
            } else if (NodeInfo.ROUTER_TYPE_ISOLATED.equals(routerType)) {//游离
                FlowTransition nodeTran = selectOptNodeTransition(nextRouterNode);
                String nextNodeId = "";
                if (nodeTran != null) {
                    nextNodeId = nodeTran.getEndNodeId();
                }
                resNodes = submitToNextNode(
                    flowNodeDao.getObjectById(nextNodeId), nodeToken + "." + NodeInstance.RUN_TOKEN_ISOLATED, flowInst, flowInfo,
                    preNodeInst, preTransPath.toString(), nodeTran, options,
                    flowVarTrans, application);
            }
        }
        return resNodes;
    }

    private List<String> submitToNextNode(NodeInfo nextNode, String nodeToken, FlowInstance flowInst, FlowInfo flowInfo,
                                          NodeInstance preNodeInst, String transPath, FlowTransition nodeTran, FlowOptParamOptions options, FlowVariableTranslate varTrans, ServletContext application) {
        //每次重置当前节点实例
        varTrans.setNodeInst(preNodeInst);
        // A:开始 B:首节点(首节点不能是路由节点，如果是路由节点请设置为 哑元，跳转到后一个节点； B 的处理换个C一样)
        // C:业务节点  F结束  R: 路由节点
        if (NodeInfo.NODE_TYPE_ROUTE.equals(nextNode.getNodeType())) { // 后续节点为路由节点
            return submitToNextRouterNode(
                nextNode, nodeToken, flowInst, flowInfo,
                preNodeInst, transPath, nodeTran, options,
                varTrans, application);
        } else if (NodeInfo.NODE_TYPE_END.equals(nextNode.getNodeType())) {
            //如果是最后一个节点，则要结束整个流程 调用 endInstance
            this.endFlowInstance(flowInst, flowInfo, nextNode, transPath,
                nodeTran, preNodeInst.getNodeInstId(),
                options.getUserCode(), options.getUnitCode(), varTrans);
            if (flowInst.getIsSubInst()) {
                return submitOptInside(SubmitOptOptions.create()
                        .copy(options).nodeInst(flowInst.getPreNodeInstId()),
                    varTrans, application, true);
            }
            //返回一个不存在的id表示0流程结束
            return CollectionsOpt.createList("0");
        } else {
            return submitToNextOptNode(
                nextNode, nodeToken, flowInst, flowInfo,
                preNodeInst, transPath, nodeTran, options,
                varTrans, application);
        }
    }

    private List<String> submitToNextOptNode(NodeInfo nextOptNode, String nodeToken, FlowInstance flowInst, FlowInfo flowInfo,
                                             NodeInstance preNodeInst, String transPath, FlowTransition trans, FlowOptParamOptions options,
                                             FlowVariableTranslate varTrans, ServletContext application) {
        Date currentTime = new Date(System.currentTimeMillis());
        String lastNodeInstId = UuidOpt.getUuidAsString32();
        NodeInstance nodeInst = FlowOptUtils.createNodeInst(options.getUnitCode(), options.getUserCode(),
            flowInst, preNodeInst, flowInfo, nextOptNode, trans, varTrans);
        nodeInst.setNodeInstId(lastNodeInstId);
        if (trans != null) {
            nodeInst.setTransPath(
                StringUtils.isBlank(transPath) ? trans.getTransId() :
                    transPath + "," + trans.getTransId());
        }
        nodeInst.setRunToken(nodeToken);
        //设置阶段进入时间 或者变更时间
        if (StringUtils.isNotBlank(nextOptNode.getStageCode())) {
            StageInstance stage = flowInst.getStageInstanceByCode(nextOptNode.getStageCode());
            if (stage != null) {
                if ("1".equals(stage.getStageBegin())) {
                    stage.setLastUpdateTime(DatetimeOpt.currentUtilDate());
                } else {
                    stage.setStageBegin("1");
                    stage.setBeginTime(DatetimeOpt.currentUtilDate());
                    stage.setLastUpdateTime(DatetimeOpt.currentUtilDate());
                }
                stageInstanceDao.updateObject(stage);
            }
        }
        List<String> createNodes = new ArrayList<>();
        createNodes.add(nodeInst.getNodeInstId());
        UserUnitFilterCalcContext context = createCalcUserUnitContext(flowInst,
            preNodeInst, nodeToken, nextOptNode, options, varTrans);

        LeftRightPair<Set<String>, Set<String>> unitAndUser =
            calcNodeUnitAndOperator(context, flowInst,
                nodeToken, nextOptNode, options);
        Set<String> nodeUnits = unitAndUser.getLeft();
        Set<String> optUsers = unitAndUser.getRight();

        if (nodeUnits != null && !nodeUnits.isEmpty()) {
            nodeInst.setUnitCode(nodeUnits.iterator().next());
        }

        if (optUsers != null && !optUsers.isEmpty()) {
            nodeInst.setUserCode(optUsers.iterator().next());
        }

        // S：子流程
        if (NodeInfo.NODE_TYPE_SUBFLOW.equals(nextOptNode.getNodeType())) {
            //如果是子流程 启动流程
            nodeInst.setNodeState("W");
            String tempFlowTimeLimit = "";
            if (BooleanBaseOpt.ONE_CHAR_TRUE.equals(flowInst.getIsTimer())
                && flowInst.getTimeLimit() != null
                && !NodeInfo.TIME_LIMIT_NONE.equals(nextOptNode.getIsAccountTime())) {
                //子流程实例计时可以继承父流程剩余时间
                WorkTimeSpan workTimeSpan = new WorkTimeSpan();
                workTimeSpan.fromNumberAsMinute(flowInst.getTimeLimit());
                tempFlowTimeLimit = workTimeSpan.toStringAsMinute();
            }
            //子流程的机构 要和 节点的机构一致
            FlowInstance tempFlow = createInstanceInside(
                CreateFlowOptions.create().copy(options)
                    .flow(nextOptNode.getSubFlowCode())
                    .version(flowDefDao.getLastVersion(nextOptNode.getSubFlowCode()))
                    .optName(flowInst.getFlowOptName() + "--" + nextOptNode.getNodeName())
                    .optTag(flowInst.getFlowOptTag())
                    .parentFlow(nodeInst.getFlowInstId(), lastNodeInstId)
                    .timeLimit(tempFlowTimeLimit), varTrans, application);

            nodeInst.setSubFlowInstId(tempFlow.getFlowInstId());
            //子流程的时间限制和父流程节点的一致
            NodeInstance tempFirstNode = tempFlow.getFirstNodeInstance();
            createNodes.add(tempFirstNode.getNodeInstId());
        } else if (NodeInfo.NODE_TYPE_OPT.equals(nextOptNode.getNodeType())) {
            //交互节点，计算人员的分配策略
            if ((optUsers == null || optUsers.isEmpty())) {
                logger.error("流程" + flowInst.getFlowInstId() + "的下一个节点:" + nextOptNode.getNodeName() + ",找不到权限为" + nextOptNode.getRoleCode() + "的操作人员");
                throw new WorkflowException(WorkflowException.NodeUserNotFound, "流程" + flowInst.getFlowInstId() + "的下一个节点:" + nextOptNode.getNodeName() + ",找不到权限为" + nextOptNode.getRoleCode() + "的操作人员");
            }

            if (SysUserFilterEngine.ROLE_TYPE_GW.equalsIgnoreCase(nextOptNode.getRoleType())) {
                nodeInst.setTaskAssigned("D");
            } else {
                if (optUsers.size() == 1 || NodeInfo.OPT_RUN_TYPE_NORMAL.equals(nextOptNode.getOptRunType())) {
                    nodeInst.setTaskAssigned("S");
                    if (optUsers.size() > 1) {
                        optUsers.clear();
                        optUsers.add(nodeInst.getUserCode());
                    }
                } else {
                    nodeInst.setTaskAssigned("T");
                    for (String uc : optUsers) {
                        ActionTask actionTask = FlowOptUtils.createActionTask(nodeInst.getNodeInstId(), uc);
                        actionTask.setAssignTime(currentTime);
                        actionTaskDao.saveNewObject(actionTask);
                        nodeInst.addWfActionTask(actionTask);
                    }
                }

            }
            //检查令牌冲突（自由流程，令牌的冲突有业务程序和流程图自己控制，无需检查）
            //这段代码是检查令牌的一致性，多实例节点多次运行时会出错的，
            //这个本来的目的是为了检查从分支中返回到 主干上，因为有游离节点的存在所以需要这个检查
            //这个算法不是判断是否相等，而是应该判断层次是否一致，只要层次一致就没有问题，如果不一致就需要截断后面的层次
            if (!"F".equals(flowInfo.getFlowClass())) {
                //自由流程，令牌的冲突有业务程序和流程图自己控制，无需检查
                //查找在同一条运行路径上的相同节点
                NodeInstance sameInst = flowInst.findLastSameNodeInst(nodeInst.getNodeId(), nodeInst, nodeInst.getNodeInstId());
                if (sameInst != null) {
                    int oldGen = sameInst.getTokenGeneration();
                    // 发现冲退
                    if (oldGen > 0 && nodeInst.getTokenGeneration() > oldGen) {
                        String thisToken = NodeInstance.truncTokenGeneration(
                            nodeInst.getRunToken(), oldGen);
                        nodeInst.setRunToken(thisToken);
                        //将相关的分支节点设置为无效
                        Set<NodeInstance> sameNodes = flowInst.findAllActiveSubNodeInstByToken(thisToken);
                        if (sameNodes != null && !sameNodes.isEmpty()) {
                            closeNodeInstanceInside(sameNodes, currentTime, options.getUserCode());
                        }
                    }
                }
            }
        } else if (NodeInfo.NODE_TYPE_SYNC.equals(nextOptNode.getNodeType())) {
            //  新建同步节点
            nodeInst.setNodeState("T");
            //  判断同步节点的同步方式是否为时间触发
            if (NodeInfo.SYNC_NODE_TYPE_TIME.equals(nextOptNode.getOptType())) {
                // 设置时间
                nodeInst.setIsTimer(NodeInfo.TIME_LIMIT_NORMAL);
                nodeInst.setTimeLimit(new WorkTimeSpan(nextOptNode.getTimeLimit()).toNumberAsMinute());
                nodeInst.setPromiseTime(nodeInst.getTimeLimit());
            }
        }

        //消息通知需要加强，不仅仅是通知操作人员，后续可能需要添加发送时机、发送方式，这部分应该使用策略模式
        if (NodeInfo.NODE_NOTICE_TYPE_DEFAULT.equals(nextOptNode.getNoticeType())) {
            boolean notSendMessage = true;
            if (StringUtils.isBlank(nextOptNode.getNoticeUserExp())
                && NodeInfo.NODE_TYPE_OPT.equals(nextOptNode.getNodeType())) {
                if (optUsers != null) {
                    notificationCenter.sendMessage("system", optUsers,
                        NoticeMessage.create().operation("workflow").method("submit").subject("您有新任务")
                            .content(
                                Pretreatment.mapTemplateString(nextOptNode.getNoticeMessage(), options)));
                }
                notSendMessage = false;
            } else if (StringUtils.isNotBlank(nextOptNode.getNoticeUserExp())) {
                context.addUnitParam("N", nodeUnits);
                context.addUserParam("N", optUsers);
                Set<String> sendMessageUser = UserUnitCalcEngine.calcOperators(context, nextOptNode.getNoticeUserExp());
                if (sendMessageUser != null && sendMessageUser.size() > 0) {
                    notificationCenter.sendMessage("system", sendMessageUser,
                        NoticeMessage.create().operation("workflow").method("submit").subject("您有新任务")
                            .content(
                                Pretreatment.mapTemplateString(nextOptNode.getNoticeMessage(), options)));
                    notSendMessage = false;
                }
            }
            if (notSendMessage) {
                logger.error("发送消息找不到对应的接收人：" + JSON.toJSONString(nodeInst));
            }
        }

        nodeInstanceDao.saveNewObject(nodeInst);
        flowInst.addNodeInstance(nodeInst);
        flowInst.setLastUpdateTime(currentTime);
        flowInst.setLastUpdateUser(options.getUserCode());

        //检查自动执行节点 并执行相关操作
        // 添加自动运行的处理结果
        if (NodeInfo.NODE_TYPE_AUTO.equals(nextOptNode.getNodeType())) {
            boolean needSubmit = true;
            SubmitOptOptions autoSubmitOptions = SubmitOptOptions.create().copy(options).nodeInst(lastNodeInstId);
            if (NodeInfo.AUTO_NODE_OPT_CODE_SCRIPT.equals(nextOptNode.getAutoRunType())) {
                //添加脚本的运行
                Map<String, Object> objectMap = flowScriptRunTime.runFlowScript(nextOptNode.getOptParam(),
                    flowInst, nodeInst, varTrans);
                /*Map<String, Object> objectMap = varTrans.calcScript(nextOptNode.getOptParam());
                for (Map.Entry<String, Object> ent : objectMap.entrySet()) {
                    if (!ent.getKey().startsWith("_")) {
                        saveFlowNodeVariable(flowInst.getFlowInstId(), nodeToken,
                            ent.getKey(), ent.getValue());
                    }
                }*/
                String lockUser = StringBaseOpt.castObjectToString(
                    objectMap.get("_lock_user"));
                if (StringUtils.isNotBlank(lockUser)) {
                    autoSubmitOptions.workUser(lockUser);
                }
                /* 这个变量列表已经在 varTrans 的内部变量中
                if(autoSubmitOptions.getVariables() ==null){
                    autoSubmitOptions.setVariables(objectMap);
                } else {
                    autoSubmitOptions.getVariables().putAll(objectMap);
                }*/
            } else if (NodeInfo.AUTO_NODE_OPT_CODE_CALL.equals(nextOptNode.getAutoRunType())) {
                //执行节点创建后 事件
                FlowOptPage optPage = flowOptPageDao.getObjectById(nextOptNode.getOptCode());
                NodeEventSupport nodeEventExecutor =
                    new AutoRunNodeEventSupport(optPage, nextOptNode,
                        varTrans, this, optVariableDefineDao, ddeDubboTaskRun);
                needSubmit = nodeEventExecutor.runAutoOperator(flowInst, preNodeInst == null ? nodeInst : preNodeInst,
                    nextOptNode, options.getUserCode());
            } else if (NodeInfo.AUTO_NODE_OPT_CODE_BEAN.equals(nextOptNode.getAutoRunType())) {
                NodeEventSupport nodeEventExecutor =
                    NodeEventSupportFactory.createNodeEventSupportBean(nextOptNode, this);
                needSubmit = nodeEventExecutor.runAutoOperator(flowInst, preNodeInst == null ? nodeInst : preNodeInst,
                    nextOptNode, options.getUserCode());
            } // 要实现一个 发送内部同步消息的 自动运行节点
            else if (NodeInfo.AUTO_NODE_OPT_CODE_MESSAGE.equals(nextOptNode.getAutoRunType())) {
                FlowEventInfo eventInfo = new FlowEventInfo();
                eventInfo.setFlowInstId(flowInst.getFlowInstId());
                eventInfo.setSenderUser(options.getUserCode());
                eventInfo.setEventName(nextOptNode.getMessageCode());
                eventInfo.setEventParam(nextOptNode.getOptParam());
                flowEventService.saveNewEvent(eventInfo);
            }

            //暂时先取第一个节点实例，解决部分问题
            //varTrans改为一个空的
            if (needSubmit) {
                List<String> nextNodes = this.submitOptInside(
                    autoSubmitOptions,
                    varTrans, application, false);
                createNodes.addAll(nextNodes);
            }
        } else if (StringUtils.isNotBlank(nextOptNode.getOptBean())) {
            NodeEventSupport nodeEventExecutor =
                NodeEventSupportFactory.createNodeEventSupportBean(nextOptNode, this);
            nodeEventExecutor.runAfterCreate(flowInst, nodeInst, nextOptNode, options.getUserCode());
        }

        return createNodes;
    }


    private NodeInfo selectNextNodeByNodeId(String nodeId) {
        List<FlowTransition> transList = flowTransitionDao.getNodeTrans(nodeId);
        if (transList == null || transList.size() < 1) {
            return null;
        }
        /*if(transList.size()>1){
            return null;
        }*/
        return flowNodeDao.getObjectById(transList.get(0).getEndNodeId());
    }

    private FlowTransition selectOptNodeTransition(NodeInfo currNode) {
        List<FlowTransition> transList = flowTransitionDao.getNodeTrans(currNode.getNodeId());
        if (transList == null || transList.size() < 1) {
            return null;
        }
        if (transList.size() > 1) {
            throw new WorkflowException(WorkflowException.FlowDefineError,
                "流程图绘制问题，业务节点流转路径不是有且唯一的一条："
                    + currNode.getFlowCode() + ":" + currNode.getVersion() + ":"
                    + currNode.getNodeId() + ":"
                    + currNode.getNodeCode() + ":" + currNode.getNodeName());
        }
        return transList.get(0);
    }

    private Set<FlowTransition> selectTransitions(FlowInstance flowInst, NodeInstance preNodeInst, String nodeToken,
                                                  NodeInfo currNode, FlowOptParamOptions options, FlowVariableTranslate varTrans) {
        List<FlowTransition> transList = flowTransitionDao.getNodeTrans(currNode.getNodeId());
        Set<FlowTransition> selTrans = new HashSet<>();
        if (transList == null || transList.size() < 1) {
            return selTrans;
        }
        String routerType = currNode.getRouterType();
        if (NodeInfo.ROUTER_TYPE_BRANCH.equals(routerType)
            || NodeInfo.ROUTER_TYPE_PARALLEL.equals(routerType)) {
            VariableFormula formula = new VariableFormula();
            formula.setTrans(varTrans);
            formula.addExtendFunc(
                "userRank",
                (a) -> {
                    UserUnitFilterCalcContext context = createCalcUserUnitContext(flowInst,
                        preNodeInst, nodeToken, currNode, options, varTrans);
                    if (a == null || a.length < 1) {
                        return null;
                    }
                    return context.getUserRank(StringBaseOpt.castObjectToString(a[0]));
                }
            );
            formula.addExtendFunc(
                "userType",
                (a) -> {
                    UserUnitFilterCalcContext context = createCalcUserUnitContext(flowInst,
                        preNodeInst, nodeToken, currNode, options, varTrans);
                    if (a == null || a.length < 1) {
                        return null;
                    }
                    IUserInfo ui = context.getUserInfoByCode(StringBaseOpt.castObjectToString(a[0]));
                    if (ui == null) {
                        return "";
                    }
                    return ui.getUserType();
                }
            );
            formula.addExtendFunc(
                "unitType",
                (a) -> {
                    UserUnitFilterCalcContext context = createCalcUserUnitContext(flowInst,
                        preNodeInst, nodeToken, currNode, options, varTrans);
                    if (a == null || a.length < 1) {
                        return null;
                    }
                    IUnitInfo ui = context.getUnitInfoByCode(StringBaseOpt.castObjectToString(a[0]));
                    if (ui == null) {
                        return "";
                    }
                    return ui.getUnitType();
                }
            );

            formula.addExtendFunc(
                "userUnits",
                (a) -> {
                    UserUnitFilterCalcContext context = createCalcUserUnitContext(flowInst,
                        preNodeInst, nodeToken, currNode, options, varTrans);
                    if (a == null || a.length < 1) {
                        return null;
                    }
                    List<? extends IUserUnit> userUnits =
                        context.listUserUnits(StringBaseOpt.castObjectToString(a[0]));
                    return CollectionsOpt.mapCollectionToSet(userUnits, IUserUnit::getUnitCode);
                }
            );
            formula.addExtendFunc(
                "unitUsers",
                (a) -> {
                    UserUnitFilterCalcContext context = createCalcUserUnitContext(flowInst,
                        preNodeInst, nodeToken, currNode, options, varTrans);
                    if (a == null || a.length < 1) {
                        return null;
                    }
                    List<? extends IUserUnit> userUnits =
                        context.listUnitUsers(StringBaseOpt.castObjectToString(a[0]));
                    return CollectionsOpt.mapCollectionToSet(userUnits, IUserUnit::getUserCode);
                }
            );

            formula.addExtendFunc(
                "userRoles",
                (a) -> {
                    UserUnitFilterCalcContext context = createCalcUserUnitContext(flowInst,
                        preNodeInst, nodeToken, currNode, options, varTrans);
                    if (a == null || a.length < 1) {
                        return null;
                    }
                    String userCode = StringBaseOpt.castObjectToString(a[0]);
                    String userType = SysUserFilterEngine.ROLE_TYPE_XZ;
                    if (a.length > 1) {
                        userType = StringBaseOpt.castObjectToString(a[1]);
                    }
                    if (SysUserFilterEngine.ROLE_TYPE_SYSTEM.equals(userType)) {
                        List<? extends IUserRole> roles = context.listUserRoles(userCode);
                        return CollectionsOpt.mapCollectionToSet(roles, IUserRole::getRoleCode);
                    } else if (SysUserFilterEngine.ROLE_TYPE_GW.equals(userType)) {
                        List<? extends IUserUnit> userUnits = context.listUserUnits(userCode);
                        return CollectionsOpt.mapCollectionToSet(userUnits, IUserUnit::getUserStation);
                    } else {
                        List<? extends IUserUnit> userUnits = context.listUserUnits(userCode);
                        return CollectionsOpt.mapCollectionToSet(userUnits, IUserUnit::getUserRank);
                    }
                }
            );
            formula.addExtendFunc(
                "calcUnits",
                (a) -> {
                    UserUnitFilterCalcContext context = createCalcUserUnitContext(flowInst,
                        preNodeInst, nodeToken, currNode, options, varTrans);
                    if (a == null || a.length < 1) {
                        return null;
                    }
                    return UserUnitCalcEngine.calcUnitsByExp(context, StringBaseOpt.castObjectToString(a[0]));
                }
            );
            formula.addExtendFunc(
                "calcUsers",
                (a) -> {
                    UserUnitFilterCalcContext context = createCalcUserUnitContext(flowInst,
                        preNodeInst, nodeToken, currNode, options, varTrans);
                    if (a == null || a.length < 1) {
                        return null;
                    }
                    return UserUnitCalcEngine.calcOperators(context, StringBaseOpt.castObjectToString(a[0]));
                }
            );

            for (FlowTransition trans : transList) {
                if (StringUtils.isBlank(trans.getTransCondition())) {
                    throw new ObjectException(WorkflowException.FlowDefineError, "没有配置相关的条件流转参数！");
                }
                if (BooleanBaseOpt.castObjectToBoolean(formula.calcFormula(trans.getTransCondition()), false)) {
                    //保存目标节点实例
                    selTrans.add(trans);
                    // D:分支节点 只能有一个出口
                    if (NodeInfo.ROUTER_TYPE_BRANCH.equals(routerType)) {
                        break;
                    }
                }
            }
            //如果没有后续分支检测是否有else分支
            if (selTrans.size() < 1) {
                for (FlowTransition trans : transList) {
                    if ("else".equalsIgnoreCase(trans.getTransCondition())) {
                        selTrans.add(trans);
                        break;
                    }
                }
            }
        } else {
            selTrans.add(transList.get(0));
        }
        return selTrans;
    }

    /*
     * 提交一个流程节点
     * 根据条件选择路劲并创建下一个节点
     *         如果是分支节点可能创建多个节点
     *         如果是汇聚节点可能不创建节点
     *        如果下一个节点是介绍节点则介绍整个流程
     * 记录提交日志，节点创建情况
     */
    @Override
    public List<String> submitOpt(SubmitOptOptions options,
                                  UserUnitVariableTranslate varTrans,
                                  ServletContext application) {

        return submitOptInside(options, varTrans, application, true);
    }

    @Override
    public Map<String, Object> submitFlowOpt(SubmitOptOptions options) {
        List<String> nextNodeInstList = submitOpt(options,
            new ObjectUserUnitVariableTranslate(
                CollectionsOpt.unionTwoMap(
                    options.getVariables(), options.getGlobalVariables())),
            null);
        // 返回提交后节点的名称
        Set<String> nodeNames = new HashSet<>();
        for (String nodeInstId : nextNodeInstList) {
            NodeInfo nodeInfo = getNodeInfo(nodeInstId);
            if (nodeInfo != null) {
                nodeNames.add(nodeInfo.getNodeName());

            }
        }
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("nextNodeInsts", nextNodeInstList);
        resultMap.put("nodeNames", StringUtils.join(nodeNames, ","));
        return resultMap;
    }

    /**
     * 返回下一步节点的节点实例ID
     *
     * @param options 当前节点实例编号
     * @return 节点实例编号列表
     */
    @Override
    public List<String> submitOpt(SubmitOptOptions options) {
        return submitOpt(options,
            new ObjectUserUnitVariableTranslate(
                CollectionsOpt.unionTwoMap(
                    options.getVariables(), options.getGlobalVariables())),
            null);
    }

    private List<String> submitOptInside(SubmitOptOptions options,
                                         UserUnitVariableTranslate varTrans,
                                         ServletContext application, boolean saveOptions) {
        return submitOptInside(options, varTrans, application, saveOptions, true, false);
    }

    private List<String> submitOptInside(SubmitOptOptions options,
                                         UserUnitVariableTranslate varTrans,
                                         ServletContext application, boolean saveOptions, boolean saveLog, boolean isSkipNode) {
        //2012-04-16 重构提交事件，添加一个多实例节点类型，这个节点类型会根据不同的机构创建不同的节点
        //根据上级节点实例编号获取节点所在父流程实例信息
        NodeInstance nodeInst = nodeInstanceDao.getObjectWithReferences(options.getNodeInstId());
        if (nodeInst == null) {
            logger.error("找不到节点实例：" + options.getNodeInstId());
            throw new WorkflowException(WorkflowException.NodeInstNotFound, "找不到节点实例：" + options.getNodeInstId());
        }
        FlowInstance flowInst = flowInstanceDao.getObjectWithReferences(nodeInst.getFlowInstId());
        if (flowInst == null) {
            logger.error("找不到流程实例：" + nodeInst.getFlowInstId());
            throw new WorkflowException(WorkflowException.FlowInstNotFound,
                "找不到流程实例：" + nodeInst.getFlowInstId());
        }

        FlowInfo flowInfo = flowDefDao.getFlowDefineByID(flowInst.getFlowCode(), flowInst.getVersion());

        if ("P".equals(nodeInst.getIsTimer())) {
            logger.error("流程节点处于暂停计时 状态：" + flowInst.getInstState() +
                "节点：" + options.getNodeInstId());
            throw new WorkflowException(WorkflowException.PauseTimerNode,
                "流程节点处于暂停计时 状态：" + flowInst.getInstState() +
                    "节点：" + options.getNodeInstId());
        }
        //校验节点状态 流程和节点状态都要为正常
        if (!flowInst.checkIsInRunning() || !nodeInst.checkIsInRunning()) {
            logger.error("流程节点状态不正确，流程：" + nodeInst.getFlowInstId() + " 状态：" + flowInst.getInstState() +
                "节点：" + options.getNodeInstId() + " 状态：" + nodeInst.getNodeState());
            throw new WorkflowException(WorkflowException.IncorrectNodeState,
                "流程节点状态不正确，流程：" + nodeInst.getFlowInstId() + " 状态：" + flowInst.getInstState() +
                    "节点：" + options.getNodeInstId() + " 状态：" + nodeInst.getNodeState());
        }

        String runAsUser = null;
        NodeInfo currNode = flowNodeDao.getObjectById(nodeInst.getNodeId());
        if (isSkipNode) {
            runAsUser = options.getUserCode();
        } else {
            if (SysUserFilterEngine.ROLE_TYPE_GW.equalsIgnoreCase(currNode.getRoleType())) {
                //TODO 判断人员岗位吻合
                runAsUser = options.getUserCode();
            } else if (NodeInfo.NODE_TYPE_OPT.equals(currNode.getNodeType())) {
                runAsUser = actionTaskDao.checkTaskGrantor(options.getNodeInstId(), options.getUserCode(), options.getGrantorCode());
                if (runAsUser == null) {
                    logger.error("用户没有权限操作该节点：" + options.getUserCode() + " -- " + options.getNodeInstId());
                    throw new WorkflowException(WorkflowException.WithoutPermission, "用户没有权限操作该节点：" + options.getUserCode() + " -- " + options.getNodeInstId());
                }
            }
        }

        if (saveOptions) {
            saveValueAndRoleInOptions(nodeInst.getFlowInstId(), nodeInst.getRunToken(), options);
        }

        Date updateTime = DatetimeOpt.currentUtilDate();
        nodeInst.setLastUpdateTime(updateTime);
        nodeInst.setLastUpdateUser(options.getUserCode());
        //创建节点提交日志 S:提交节点
        if (saveLog) {
            OperationLog wfactlog = FlowOptUtils.createActionLog(
                options.getUserCode(), nodeInst, "提交节点", currNode);
            if (NodeInfo.NODE_TYPE_OPT.equals(currNode.getNodeType()) &&
                !StringUtils.equals(runAsUser, options.getUserCode())) {
                nodeInst.setGrantor(runAsUser);
                wfactlog.setNewValue(runAsUser + " 授予 " + options.getUserCode()
                    + ":" + currNode.getRoleType() + ":" + currNode.getRoleCode());
            }
            OperationLogCenter.log(wfactlog);
        }
        nodeInstanceDao.updateObject(nodeInst);
        //设置阶段进 变更时间（提交时间）
        StageInstance stage = flowInst.getStageInstanceByCode(currNode.getStageCode());
        if (stage != null) {
            if ("1".equals(stage.getStageBegin())) {
                stage.setLastUpdateTime(DatetimeOpt.currentUtilDate());
            } else {//这一句应该是运行不到的
                stage.setStageBegin("1");
                stage.setBeginTime(DatetimeOpt.currentUtilDate());
                stage.setLastUpdateTime(DatetimeOpt.currentUtilDate());
            }
            stageInstanceDao.updateObject(stage);

        }
        flowInst.setLastUpdateTime(updateTime);
        flowInst.setLastUpdateUser(options.getUserCode());
        flowInstanceDao.updateObject(flowInst);

        List<String> nextNodeInsts = new ArrayList<>();
        if (NodeInfo.NODE_TYPE_OPT.equals(currNode.getNodeType()) && "T".equals(nodeInst.getTaskAssigned())) {
            //多人操作节点 等待所有人都提交才可以提交
            //这个应该会不需要了，暂时保留
            //这样的需求应该会被 按人进行多实例划分，
            int havnotSubmit = 0;
            for (ActionTask task : nodeInst.getWfActionTasks()) {
                if ("A".equals(task.getTaskState())) {
                    if (StringUtils.equals(runAsUser, task.getUserCode())) {
                        //任务的完成时间在任务的活动日志中
                        task.setTaskState("C");
                    } else {
                        if (NodeInfo.OPT_RUN_TYPE_TEAMWORK.equals(currNode.getOptRunType())) {
                            havnotSubmit++;
                        }
                    }
                }
                actionTaskDao.updateObject(task);
            }
            //判断是否是多人操作，如果是多人操作，最后一个人提交才正在提交
            //前面人提交只更改任务列表中的任务完成状态，多人操作一定要配合 流程活动任务单 工作
            //这个任务可以是业务填写也可以是权限引擎填写
            if (NodeInfo.OPT_RUN_TYPE_TEAMWORK.equals(currNode.getOptRunType()) && havnotSubmit > 0) {
                nodeInstanceDao.updateObject(nodeInst);
                nextNodeInsts.add(options.getNodeInstId());
                return nextNodeInsts;
            }
        }
        //节点提交前事件
        NodeEventSupport nodeEventExecutor = NodeEventSupportFactory
            .createNodeEventSupportBean(currNode, this);
        nodeEventExecutor.runBeforeSubmit(flowInst, nodeInst, currNode, options.getUserCode());

        //判断是否为临时插入节点
        if (nodeInst.getRunToken().endsWith(NodeInstance.RUN_TOKEN_INSERT)) {
            //提交临时插入节点
            nodeInst.setNodeState("C");
            if (flowInst.checkNotCommitPreNodes(nodeInst.getPrevNodeInstId()) > 0) {
                NodeInstance preNodeInst =
                    flowInst.getNodeInstanceById(nodeInst.getPrevNodeInstId());
                preNodeInst.setNodeState("N");
                nextNodeInsts.add(nodeInst.getPrevNodeInstId());
                nodeInstanceDao.updateObject(preNodeInst);
            }
            nodeInstanceDao.updateObject(nodeInst);
            return nextNodeInsts;
        }

        FlowTransition nodeTran = selectOptNodeTransition(currNode);
        if (nodeTran == null) {
            // 临时节点 和 游离节点都可以不管
            if (nodeInst.getRunToken().contains(NodeInstance.RUN_TOKEN_ISOLATED)) {
                //将节点的状态设置为已完成
                nodeInst.setNodeState("C");
                nodeInstanceDao.updateObject(nodeInst);
                return nextNodeInsts;
            } else {
                logger.error("流程：" + nodeInst.getFlowInstId() + "节点：" + options.getNodeInstId() + " " + currNode.getNodeName() + " 没有找到符合流转条件的后续节点。");
                throw new WorkflowException(WorkflowException.NotFoundNextNode,
                    "流程：" + nodeInst.getFlowInstId() + "节点：" + options.getNodeInstId() + " " + currNode.getNodeName() + " 没有找到符合流转条件的后续节点。");
            }
        }
        synchronized (lockObject) {
            nodeInst = nodeInstanceDao.getObjectWithReferences(options.getNodeInstId());
            if (!nodeInst.checkIsInRunning()) {
                logger.error("流程：" + nodeInst.getFlowInstId() + "节点：" + options.getNodeInstId() + " " + currNode.getNodeName() + " 已经被其他线程提交，请避免重复提交。");
                throw new WorkflowException(WorkflowException.IncorrectNodeState,
                    "流程：" + nodeInst.getFlowInstId() + "节点：" + options.getNodeInstId() + " " + currNode.getNodeName() + " 已经被其他线程提交，请避免重复提交。");

            }
            nodeInst.setNodeState("C");
            nodeInstanceDao.updateObject(nodeInst);
        }
        FlowVariableTranslate flowVarTrans = FlowOptUtils.createVariableTranslate(
            nodeInst, flowInst, flowVariableDao, this, options);
        flowVarTrans.setFlowVarTrans(varTrans);
        String nextNodeId = nodeTran.getEndNodeId();
        nextNodeInsts = submitToNextNode(
            flowNodeDao.getObjectById(nextNodeId), nodeInst.getRunToken(), flowInst, flowInfo,
            nodeInst, null, nodeTran, options,
            flowVarTrans, application);
        return nextNodeInsts;
    }

    /**
     * 关闭本节点分支以外的其他分支的所有节点,特指和本节点平行的分支，就是同一个父类令牌的分支
     *
     * @param nodeInstId  当前活动节点
     * @param optUserCode 操作人员
     */
    @Override
    public void disableOtherBranchNodes(String nodeInstId, String optUserCode) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null || !"N".equals(nodeInst.getNodeState())) {
            logger.error("找不到节点实例：" + nodeInstId + "，或者实例不是正常状态的节点。");
            return;
        }
        FlowInstance flowInst = flowInstanceDao.getObjectWithReferences(nodeInst.getFlowInstId());
        if (flowInst == null) {
            logger.error("找不到流程实例：" + nodeInst.getFlowInstId());
            return;
        }
        Date updateTime = DatetimeOpt.currentUtilDate();
        //一个分支只有一个活动节点
        String preToken = NodeInstance.calcSuperToken(nodeInst.getRunToken());
        for (NodeInstance ni : flowInst.getFlowNodeInstances()) {
            if (!ni.getNodeInstId().equals(nodeInstId) &&
                ("N".equals(ni.getNodeState()) || "S".equals(ni.getNodeState()) ||
                    "W".equals(ni.getNodeState()) || "P".equals(ni.getNodeState())) &&
                preToken.equals(NodeInstance.calcSuperToken(ni.getRunToken()))
            ) {
                if ("W".equals(ni.getNodeState())) {
                    FlowInstance subFlowInst = flowInstanceDao.getObjectById(ni.getSubFlowInstId());
                    if (subFlowInst != null) {
                        FlowOptUtils.endInstance(subFlowInst, "F", optUserCode, flowInstanceDao);
                        subFlowInst.setLastUpdateUser(optUserCode);
                        flowInstanceDao.updateObject(subFlowInst);
                    }
                }
                ni.setNodeState("F");// 节点设置为强制结束
                ni.setLastUpdateUser(optUserCode);
                ni.setLastUpdateTime(updateTime);
            }
        }
        flowInst.setLastUpdateUser(optUserCode);
        flowInst.setLastUpdateTime(updateTime);
        flowInstanceDao.updateObject(flowInst);
    }

    /**
     * 将流程回滚到上一个节点，如果是汇聚节点则不能回滚（暂不支持），如果是并行分支节点需要管理员配合挂起落空的节点
     * 暂时忽略流程的状态，但是要判断节点的状态，只有正在运行的节点（状态为N）才可以回退
     *
     * @return >0：新节点的ID,-1 找不到节点，-2找不到流程 -3 找不到上一个节点 -4 上一个流程为子流程 不允许回退 -5
     * 本节点为汇聚节点不允许回退
     * <p>
     * 新增子流程退回父流程的方法
     */
    @Override
    public String rollBackNode(String nodeInstId, String managerUserCode) {
        String msg;
        // 添加令牌算法
        NodeInstance thisNodeInst;
        try {
            thisNodeInst = nodeInstanceDao.getObjectWithReferences(nodeInstId);
        } catch (NullPointerException e) {
            msg = "流程回退 flowRollback 失败, 节点不存在： nodeInstId: " + nodeInstId;
            logger.error(msg);
            throw new WorkflowException(WorkflowException.NodeInstNotFound, msg);
        }
        // 当前节点状态必需为正常
        if (!"N".equals(thisNodeInst.getNodeState())) {
            msg = "流程回退 flowRollback 失败, 节点状态不是办理中： nodeInstId: " + nodeInstId + "  nodeState :" + thisNodeInst.getNodeState();
            logger.error(msg);
            throw new WorkflowException(WorkflowException.IncorrectNodeState, msg);
        }

        FlowInstance flowInst = flowInstanceDao.getObjectWithReferences(thisNodeInst
            .getFlowInstId());
        if (flowInst == null) {
            logger.error("找不到流程实例：" + thisNodeInst.getFlowInstId());
            throw new WorkflowException(WorkflowException.FlowInstNotFound,
                "找不到流程实例：" + thisNodeInst.getFlowInstId());
        }
        NodeInstance prevNodeInst;
        if (thisNodeInst.getPrevNodeInstId() != null) {
            prevNodeInst = nodeInstanceDao.getObjectWithReferences(thisNodeInst.getPrevNodeInstId());
        } else {
            prevNodeInst = flowInst.getPareNodeInst(nodeInstId);
        }
        //是否子流程退回父流程
        boolean subProcess = false;
        // 查找上一个流经节点
        FlowInstance prevFlowInst = null;
        if (prevNodeInst == null) {
            //找不到上一节点之后，判断是否子流程
            if (flowInst.getPreNodeInstId() == null) {
                msg = "流程回退 rollBackNode 失败,找不到上一节点";
                logger.error(msg);
                throw new WorkflowException(WorkflowException.NodeInstNotFound, msg);
            } else {
                //是子流程的话，把流程实例中的prenodeinst取出来找到对应的前一节点
                subProcess = true;
                prevNodeInst = nodeInstanceDao.getObjectWithReferences(flowInst.getPreNodeInstId());
                prevFlowInst = flowInstanceDao.getObjectWithReferences(flowInst.getPreInstId());
            }
        }
        NodeInfo nodedef = flowNodeDao.getObjectById(prevNodeInst.getNodeId());
        Set<NodeInstance> pns = new HashSet<>();
        // 不能回退到 自动执行，哑元，和子流程节点
        while (true) {
            if (StringUtils.equalsAny(nodedef.getNodeType(),
                NodeInfo.NODE_TYPE_AUTO, NodeInfo.NODE_TYPE_SYNC, NodeInfo.NODE_TYPE_SUBFLOW)) {
                pns.add(prevNodeInst);
                String currentNodeInstId = prevNodeInst.getPrevNodeInstId();
                if (currentNodeInstId != null) {
                    prevNodeInst = nodeInstanceDao.getObjectWithReferences(currentNodeInstId);
                } else {
                    prevNodeInst = flowInst.getPareNodeInst(null);
                }
                if (prevNodeInst == null) {
                    String nodeType = "";
                    if (StringUtils.equals(nodedef.getNodeType(), NodeInfo.NODE_TYPE_AUTO)) {
                        nodeType = "自动运行节点";
                    } else if (StringUtils.equals(nodedef.getNodeType(), NodeInfo.NODE_TYPE_SYNC)) {
                        nodeType = "同步节点";
                    } else if (StringUtils.equals(nodedef.getNodeType(), NodeInfo.NODE_TYPE_SUBFLOW)) {
                        nodeType = "子流程";
                    }
                    msg = "流程回退 rollBackNode 失败,经过" + nodeType + "后,找不到上一节点";
                    logger.error(msg);
                    throw new WorkflowException(WorkflowException.IncorrectNodeState, msg);
                }
                //判断一下父流程中的节点是否已经被退回，已经被退回之后就不能再次退回
                if (prevFlowInst != null) {
                    for (NodeInstance no : prevFlowInst.getFlowNodeInstances()) {
                        //罗列处于正常、暂缓的节点
                        if ("N,S,P".contains(no.getNodeState())) {
                            //如果想要退回的节点处于正常、暂缓，无需退回
                            if (prevNodeInst.getNodeId().equals(no.getNodeId())) {
                                msg = "流程回退 rollBackNode 失败,父流程中的节点已经被退回";
                                logger.error(msg);
                                throw new WorkflowException(WorkflowException.IncorrectNodeState, msg);
                            }
                        }
                    }
                }
                nodedef = flowNodeDao.getObjectById(prevNodeInst.getNodeId());
            } else {
                break;
            }
        }
        pns.add(prevNodeInst);
        Date updateTime = DatetimeOpt.currentUtilDate();
        thisNodeInst.setNodeState("B");
        for (NodeInstance pn : pns) {
            pn.setNodeState("B");
            nodeInstanceDao.updateObject(pn);
        }
        // 设置最后更新时间和更新人
        thisNodeInst.setLastUpdateUser(managerUserCode);
        thisNodeInst.setLastUpdateTime(updateTime);
        String lastNodeInstId = UuidOpt.getUuidAsString32();
        NodeInstance nextNodeInst = flowInst.newNodeInstance();
        //如果是子流程退回父流程，把流程id置为父流程的流程id
        if (subProcess) {
            nextNodeInst.setFlowInstId(flowInst.getPreInstId());
            flowInst.setInstState("B");
        }
        nextNodeInst.copyNotNullProperty(prevNodeInst);
        nextNodeInst.setNodeInstId(lastNodeInstId);
        nextNodeInst.setCreateTime(updateTime);
        nextNodeInst.setNodeState("N");
        nextNodeInst.setTaskAssigned(prevNodeInst.getTaskAssigned());
        nextNodeInst.setLastUpdateUser(managerUserCode);
        nextNodeInst.setLastUpdateTime(updateTime);
        for (ActionTask task : prevNodeInst.getWfActionTasks()) {
            ActionTask actionTask = FlowOptUtils.createActionTask(
                nextNodeInst.getNodeInstId(), task.getUserCode());
            nextNodeInst.setTimeLimit(null);
            nextNodeInst.setTaskAssigned("T");
            actionTaskDao.saveNewObject(actionTask);
        }
        flowInst.addNodeInstance(nextNodeInst);
        nodeInstanceDao.mergeObject(thisNodeInst);
        nodeInstanceDao.mergeObject(nextNodeInst);
        flowInstanceDao.updateObject(flowInst);
        //执行节点创建后 事件
        NodeEventSupport nodeEventExecutor = NodeEventSupportFactory
            .createNodeEventSupportBean(nodedef, this);
        nodeEventExecutor.runAfterCreate(flowInst, nextNodeInst, nodedef, managerUserCode);
        //调用发送消息接口
        OperationLogCenter.log(FlowOptUtils.createActionLog(
            managerUserCode, flowInst.getFlowInstId(), "回退到上一个节点;"));
        return lastNodeInstId;
    }

    /**
     * 如果后续节点是 自动运行 和哑元 节点，节点被操作的判断将会误判
     */
    @Override
    public boolean nodeCanBeReclaim(String nodeInstId) {
        NodeInstance thisnode = nodeInstanceDao.getObjectById(nodeInstId);
        if (thisnode == null) {
            return false;
        }
        FlowInstance flow = flowInstanceDao.getObjectWithReferences(thisnode
            .getFlowInstId());
        if (flow == null) {
            return false;
        }
        // 流程状态被更改也算被操作了
        if (!"N".equals(flow.getInstState())) {
            return false;
        }
        int nns = 0;
        for (NodeInstance nextNode : flow.getFlowNodeInstances()) {
            if (thisnode.getNodeInstId().equals(nextNode.getPrevNodeInstId())) {
                nns++;
                if (!"N".equals(nextNode.getNodeState())) {
                    return false;
                }
            }
        }
        return nns > 0;
    }


    private Set<NodeInfo> viewRouterNextNodeInside(
        FlowInstance flowInst,
        NodeInstance preNodeInst,
        NodeInfo currNode, FlowOptParamOptions options, FlowVariableTranslate varTrans) {
        Set<NodeInfo> nextNodes = new HashSet<>();
        Set<FlowTransition> trans = selectTransitions(flowInst,
            preNodeInst, preNodeInst.getRunToken(), currNode, options, varTrans);

        for (FlowTransition tran : trans) {
            NodeInfo tempNode = flowNodeDao.getObjectById(tran.getEndNodeId());
            if (NodeInfo.NODE_TYPE_OPT.equals(tempNode.getNodeType())) {
                nextNodes.add(tempNode);
            } else if (NodeInfo.NODE_TYPE_ROUTE.equals(tempNode.getNodeType())) {
                nextNodes.addAll(
                    viewRouterNextNodeInside(flowInst,
                        preNodeInst, tempNode, options, varTrans));
            }
        }
        return nextNodes;
    }

    @Override
    public Set<NodeInfo> viewNextNode(SubmitOptOptions options) {
        //根据上级节点实例编号获取节点所在父流程实例信息
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(options.getNodeInstId());
        if (nodeInst == null) {
            logger.error("找不到节点实例：" + options.getNodeInstId());
            return null;
        }
        FlowInstance flowInst = flowManager.getFlowInstance(nodeInst.getFlowInstId());
        if (flowInst == null) {
            logger.error("找不到流程实例：" + nodeInst.getFlowInstId());
            return null;
        }

        NodeInfo currNode = flowNodeDao.getObjectById(nodeInst.getNodeId());
        Set<NodeInfo> nextNodes = new HashSet<>();
        NodeInfo nextNode = selectNextNodeByNodeId(currNode.getNodeId());
        if (nextNode != null) {
            if (NodeInfo.NODE_TYPE_OPT.equals(nextNode.getNodeType())) {
                nextNodes.add(nextNode);
            } else if (NodeInfo.NODE_TYPE_ROUTE.equals(nextNode.getNodeType())) {
                FlowVariableTranslate flowVarTrans = FlowOptUtils.createVariableTranslate(
                    nodeInst, flowInst, flowVariableDao, this, null);
                // 分支节点的条件
                UserUnitVariableTranslate varTrans = new ObjectUserUnitVariableTranslate(
                    CollectionsOpt.unionTwoMap(
                        options.getVariables() == null ? new HashMap<>() : options.getVariables(),
                        options.getGlobalVariables() == null ? new HashMap<>() : options.getGlobalVariables()));
                flowVarTrans.setFlowVarTrans(varTrans);
                nextNodes = viewRouterNextNodeInside(flowInst, nodeInst, nextNode, options, flowVarTrans);
            }
        }
        return nextNodes;
    }

    @Override
    public Set<String> viewNextNodeOperator(String nextNodeId, SubmitOptOptions options) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(options.getNodeInstId());
        if (nodeInst == null) {
            logger.error("找不到节点实例：" + options.getNodeInstId());
            return null;
        }
        FlowInstance flowInst = flowInstanceDao.getObjectById(nodeInst.getFlowInstId());
        if (flowInst == null) {
            logger.error("找不到流程实例：" + nodeInst.getFlowInstId());
            return null;
        }

        //判断是否为 结束节点 A:开始 B:首节点 C:一般 D:分支 E:汇聚 F结束
        NodeInfo nextNode = flowNodeDao.getObjectById(nextNodeId);
        //判断是否为子流程 A:一般 B:抢先机制 C:多人操作 S:子流程
        if (!NodeInfo.NODE_TYPE_SUBFLOW.equals(nextNode.getNodeType())) {
            FlowVariableTranslate flowVarTrans = FlowOptUtils.createVariableTranslate(
                nodeInst, flowInst, flowVariableDao, this, null);
            UserUnitVariableTranslate varTrans = new ObjectUserUnitVariableTranslate(
                CollectionsOpt.unionTwoMap(
                    options.getVariables() == null ? new HashMap<>() : options.getVariables(),
                    options.getGlobalVariables() == null ? new HashMap<>() : options.getGlobalVariables()));
            flowVarTrans.setFlowVarTrans(varTrans);

            LeftRightPair<Set<String>, Set<String>> unitAndUser =
                calcNodeUnitAndOperator(flowInst, nodeInst, nodeInst.getRunToken(),
                    nextNode, SubmitOptOptions.create().user(options.getUserCode()).unit(options.getUnitCode()),
                    flowVarTrans);

            return unitAndUser.getRight();
        }

        return new HashSet<>();
    }

    @Override
    public String getTaskGrantor(String nodeInstId, String userCode) {
        return actionTaskDao.checkTaskGrantor(nodeInstId, userCode, null);
    }

    /**
     * 加签,并指定到人
     * <p>
     * 用户手动创建一个节点实例，当前节点实例挂起，等这个新建的节点实例运行完提交时，当前节点实例继续运行.
     * 同一个节点可以创建多个前置节点，当所有的前置节点都执行提交后，现有的节点才被唤醒
     *
     * @param flowInstId    流程实例号
     * @param curNodeInstId 当前节点实例号
     * @param nodeCode      节点环节代码，这个节点在这个流程中必需唯一
     * @param userCode      指定用户
     * @param unitCode      指定机构
     * @param createUser    创建用户
     * @return 节点实例
     */
    @Override
    public NodeInstance createPrepNodeInst(String flowInstId, String curNodeInstId,
                                           String nodeCode, String createUser,
                                           String userCode, String unitCode) {

        FlowInstance flowInst = flowInstanceDao.getObjectById(flowInstId);
        if (flowInst == null) {
            logger.error("找不到流程实例：" + flowInstId);
            return null;
        }
        List<NodeInfo> nodeList = flowNodeDao.listNodeByNodecode(flowInst.getFlowCode(),
            flowInst.getVersion(), nodeCode);

        if (nodeList == null || nodeList.size() < 1) {
            return null;
        }
        if (nodeList.size() > 1) {
            logger.error("流程 " + flowInst.getFlowCode() + "（版本号" + flowInst.getVersion()
                + "）中对应环节代码为" + nodeCode + "的节点有多个，系统随机的创建一个，如有问题请和管理人员联系。");
        }
        String nodeId = nodeList.get(0).getNodeId();

        NodeInstance nodeInst = nodeInstanceDao.getObjectById(curNodeInstId);
        //必需存在且状态为正常 或者 暂停
        if (nodeInst == null || (!"N".equals(nodeInst.getNodeState()) && !"S".equals(nodeInst.getNodeState()))) {
            logger.error("找不到节点实例：" + curNodeInstId);
            return null;
        }
        if (nodeInst.getRunToken().contains(NodeInstance.RUN_TOKEN_ISOLATED)) {
            logger.error("游离节点不能创建前置节点：" + curNodeInstId + " token:" + nodeInst.getRunToken() + "。");
            return null;
        }
        FlowInfo flowInfo = flowDefDao.getFlowDefineByID(flowInst.getFlowCode(), flowInst.getVersion());
        NodeInfo nextNode = flowNodeDao.getObjectById(nodeId);
        //获取上一个相同节点实例机构
        String lastNodeInstId = UuidOpt.getUuidAsString32();
        NodeInstance nextNodeInst = FlowOptUtils.createNodeInst(unitCode, userCode,
            flowInst, nodeInst, flowInfo, nextNode, null, null);
        nextNodeInst.setNodeInstId(lastNodeInstId);
        nextNodeInst.setPrevNodeInstId(curNodeInstId);
        nextNodeInst.setRunToken(nodeInst.getRunToken() + "." + NodeInstance.RUN_TOKEN_INSERT);
        nextNodeInst.setUserCode(userCode);
        nextNodeInst.setTaskAssigned("S");
        nextNodeInst.setTransPath("");
        //等待前置节点提交
        nodeInst.setNodeState("S");
        nodeInstanceDao.saveNewObject(nextNodeInst);
        nodeInstanceDao.updateObject(nodeInst);
        return nextNodeInst;
    }


    /**
     * 创建孤立节点  知会、关注
     * <p>
     * 用户手动创建一个节点实例，当前节点实例挂起，等这个新建的节点实例运行完提交时，当前节点实例继续运行.
     * 同一个节点可以创建多个前置节点，当所有的前置节点都执行提交后，现有的节点才被唤醒
     *
     * @param flowInstId    流程实例号
     * @param curNodeInstId 当前节点实例号
     * @param nodeCode      节点环节代码，这个节点在这个流程中必需唯一
     * @param userCode      指定用户
     * @param createUser    创建用户
     * @param unitCode      指定机构
     * @return 节点实例
     */
    @Override
    public NodeInstance createIsolatedNodeInst(String flowInstId, String curNodeInstId,
                                               String nodeCode, String createUser,
                                               String userCode, String unitCode) {

        FlowInstance flowInst = flowInstanceDao.getObjectById(flowInstId);
        if (flowInst == null) {
            return null;
        }
        List<NodeInfo> nodeList = flowNodeDao.listNodeByNodecode(flowInst.getFlowCode(),
            flowInst.getVersion(), nodeCode);

        if (nodeList == null || nodeList.size() < 1) {
            return null;
        }
        if (nodeList.size() > 1) {
            logger.error("流程 " + flowInst.getFlowCode() + "（版本号" + flowInst.getVersion()
                + "）中对应环节代码为" + nodeCode + "的节点有多个，系统随机的创建一个，如有问题请和管理人员联系。");
        }

        NodeInstance nodeInst = nodeInstanceDao.getObjectById(curNodeInstId);
        if (NODE_INST_ZERO.equals(curNodeInstId)) {
            nodeInst = new NodeInstance();
        }
        //必需存在且状态为正常 或者 暂停
        if (nodeInst == null) {
            logger.error("找不到节点实例：" + curNodeInstId);
            return null;
        }
        String nodeId = nodeList.get(0).getNodeId();

        FlowInfo flowInfo = flowDefDao.getFlowDefineByID(flowInst.getFlowCode(), flowInst.getVersion());

        NodeInfo nextNode = flowNodeDao.getObjectById(nodeId);
        //获取上一个相同节点实例机构
        String lastNodeInstId = UuidOpt.getUuidAsString32();
        NodeInstance nextNodeInst = FlowOptUtils.createNodeInst(unitCode, createUser, flowInst,
            nodeInst, flowInfo, nextNode, null, null);
        nextNodeInst.setNodeInstId(lastNodeInstId);
        nextNodeInst.setPrevNodeInstId(curNodeInstId);
        nextNodeInst.setRunToken(nodeInst.getRunToken() + "." + NodeInstance.RUN_TOKEN_ISOLATED);
        nextNodeInst.setUserCode(userCode);
        nextNodeInst.setTaskAssigned("S");
        nextNodeInst.setTransPath("");
        nodeInstanceDao.saveNewObject(nextNodeInst);
        return nextNodeInst;
    }


    @Override
    public void assignFlowWorkTeam(String flowInstId, String roleCode, String runToken,
                                   List<String> userCodeSet) {
        if (userCodeSet == null) {
            return;
        }
        Date assignDate = new Date(System.currentTimeMillis());
        for (String usercode : userCodeSet) {
            if (StringUtils.isNotBlank(usercode)) {
                flowTeamDao.mergeObject(
                    new FlowWorkTeam(flowInstId, usercode, roleCode, runToken, assignDate));
            }
        }
    }


    @Override
    public void assignFlowWorkTeamByNode(String nodeInstId, String roleCode,
                                         List<String> userCodeSet) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        assignFlowWorkTeam(nodeInst.getFlowInstId(), roleCode,
            nodeInst.getRunToken(), userCodeSet);
    }

    @Override
    public void assignFlowWorkTeam(String flowInstId, String roleCode,
                                   List<String> userCodeSet) {
        assignFlowWorkTeam(flowInstId, roleCode, "T", userCodeSet);
    }


    @Override
    public void deleteFlowWorkTeam(String flowInstId, String roleCode, String userCode) {
        flowTeamDao.deleteObjectsByProperties(
            CollectionsOpt.createHashMap(
                "flowInstId", flowInstId, "userCode", userCode, "roleCode", roleCode));
    }

    @Override
    public void deleteFlowWorkTeam(String flowInstId, String roleCode) {
        flowTeamDao.deleteFlowWorkTeam(flowInstId, roleCode);
    }

    @Override
    public Map<String, List<String>> viewFlowWorkTeam(String flowInstId) {
        List<FlowWorkTeam> users = flowTeamDao.listFlowWorkTeam(flowInstId);
        Map<String, List<String>> team = new HashMap<>();
        if (null != users) {
            for (FlowWorkTeam user : users) {
                List<String> us = team.get(user.getRoleCode());
                if (us == null) {
                    us = new ArrayList<>();
                }
                us.add(user.getUserCode());
                team.put(user.getRoleCode(), us);
            }
        }
        return team;
    }

    @Override
    public List<String> viewFlowWorkTeam(String flowInstId, String roleCode) {
        List<FlowWorkTeam> users = flowTeamDao.listFlowWorkTeamByRole(flowInstId, roleCode);
        List<String> us = new ArrayList<>();
        for (FlowWorkTeam user : users) {
            us.add(user.getUserCode());
        }
        return us;
    }

    @Override
    public List<FlowWorkTeam> viewFlowWorkTeamList(String flowInstId, String roleCode) {
        return flowTeamDao.listFlowWorkTeamByRole(flowInstId, roleCode);
    }

    @Override
    public List<FlowWorkTeam> viewFlowWorkTeamList(String flowInstId, String roleCode, String authdesc) {
        return flowTeamDao.listFlowWorkTeam(flowInstId, roleCode, authdesc);
    }

    @Override
    public FlowVariable viewNodeVariable(String flowInstId, String runToken,
                                         String varName) {
        FlowVariableId id = new FlowVariableId(flowInstId, runToken,
            varName);
        return flowVariableDao.getObjectById(id);
    }


    @Override
    public void assignFlowOrganize(String flowInstId, String roleCode,
                                   String unitCode) {
        Date assignDate = new Date(System.currentTimeMillis());
        FlowOrganize dbObj = flowOrganizeDao.getObjectById(new FlowOrganizeId(flowInstId, unitCode, roleCode));
        if (dbObj == null || StringBaseOpt.isNvl(dbObj.getUnitCode())) {
            flowOrganizeDao.mergeObject(new FlowOrganize(flowInstId, unitCode, roleCode, assignDate));
        }

    }

    @Override
    public void assignFlowOrganize(String flowInstId, String roleCode,
                                   List<String> unitCodeSet) {
        if (unitCodeSet == null) {
            return;
        }
        Date assignDate = new Date(System.currentTimeMillis());
        for (String unitCode : unitCodeSet) {
            if (unitCode != null && !"".equals(unitCode)) {
                FlowOrganize dbObj = flowOrganizeDao.getObjectById(new FlowOrganizeId(flowInstId, unitCode, roleCode));
                if (dbObj == null || StringBaseOpt.isNvl(dbObj.getUnitCode())) {
                    flowOrganizeDao.mergeObject(new FlowOrganize(flowInstId, unitCode, roleCode, assignDate));
                }
            }
        }
    }

    @Override
    public void assignFlowOrganize(String flowInstId, String roleCode,
                                   String unitCode, String authDesc) {
        Date assignDate = new Date(System.currentTimeMillis());
        FlowOrganize dbObj = flowOrganizeDao.getObjectById(new FlowOrganizeId(flowInstId, unitCode, roleCode));
        if (dbObj == null || StringBaseOpt.isNvl(dbObj.getUnitCode())) {
            FlowOrganize orgObj = new FlowOrganize(flowInstId, unitCode, roleCode, assignDate);
            orgObj.setAuthDesc(authDesc);
            flowOrganizeDao.mergeObject(orgObj);
        }
    }

    @Override
    public void assignFlowOrganize(String flowInstId, String roleCode,
                                   List<String> unitCodeSet, String authDesc) {
        Date assignDate = new Date(System.currentTimeMillis());
        if (unitCodeSet != null) {
            for (String unitCode : unitCodeSet) {
                if (unitCode != null && !"".equals(unitCode)) {
                    FlowOrganize dbObj = flowOrganizeDao.getObjectById(new FlowOrganizeId(flowInstId, unitCode, roleCode));
                    if (dbObj == null || StringBaseOpt.isNvl(dbObj.getUnitCode())) {
                        FlowOrganize orgObj = new FlowOrganize(flowInstId, unitCode, roleCode, assignDate);
                        orgObj.setAuthDesc(authDesc);
                        flowOrganizeDao.mergeObject(orgObj);
                    }
                }
            }
        }
    }

    @Override
    public void deleteFlowOrganize(String flowInstId, String roleCode,
                                   String unitCode) {
        flowOrganizeDao.deleteObjectById(new FlowOrganizeId(flowInstId, unitCode, roleCode));

    }

    @Override
    public void deleteFlowOrganize(String flowInstId, String roleCode) {
        flowOrganizeDao.deleteFlowOrganize(flowInstId, roleCode);
    }

    @Override
    public void deleteFlowOrganizeByAuth(String flowInstId, String roleCode, String authDesc) {
        flowOrganizeDao.deleteFlowOrganize(flowInstId, roleCode, authDesc);
    }

    @Override
    public Map<String, List<String>> viewFlowOrganize(String flowInstId) {
        List<FlowOrganize> units = flowOrganizeDao.listFlowOrganize(flowInstId);
        Map<String, List<String>> orgs = new HashMap<>();
        if (null != units) {
            for (FlowOrganize unit : units) {
                List<String> us = orgs.get(unit.getRoleCode());
                if (us == null) {
                    us = new ArrayList<>();
                }
                us.add(unit.getUnitCode());
                orgs.put(unit.getRoleCode(), us);
            }
        }
        return orgs;
    }

    @Override
    public List<String> viewFlowOrganize(String flowInstId, String roleCode) {
        List<FlowOrganize> units = flowOrganizeDao.listFlowOrganizeByRole(flowInstId, roleCode);
        List<String> orgs = new ArrayList<>();
        if (null != units) {
            for (FlowOrganize unit : units) {
                orgs.add(unit.getUnitCode());
            }
        }
        return orgs;
    }

    @Override
    public List<FlowOrganize> viewFlowOrganizeList(String flowInstId, String roleCode) {
        return new ArrayList<>(
            flowOrganizeDao.listFlowOrganizeByRole(flowInstId, roleCode));
    }

    @Override
    public List<FlowOrganize> viewFlowOrganizeList(String flowInstId, String roleCode, String authDesc) {
        return new ArrayList<>(
            flowOrganizeDao.listFlowOrganize(flowInstId, roleCode, authDesc));
    }



    /**
     * 设置流程节点上下文变量
     *
     * @param flowInstId 流程实例id
     * @param runToken   令牌
     * @param sVar       流程变量
     * @param sValue     流程值
     */
    @Override
    public void saveFlowNodeVariable(String flowInstId, String runToken, String sVar, Object sValue) {
        String objStr = StringBaseOpt.objectToString(sValue);
        if (StringUtils.isBlank(objStr)) {
            flowVariableDao.deleteObjectById(new FlowVariableId(flowInstId,
                runToken, sVar));
            return;
        }
        FlowVariableId cid = new FlowVariableId(flowInstId,
            runToken, sVar);
        String varType = sValue.getClass().isArray() || sValue instanceof Collection
             || objStr.indexOf(',') > 1 ? "E" : "S";

        FlowVariable varO = flowVariableDao.getObjectById(cid);
        if (varO == null) {
            varO = new FlowVariable(flowInstId,
                runToken, sVar, objStr, varType);
            flowVariableDao.saveNewObject(varO);
        } else {
            varO.setVarType(varType);
            varO.setVarValue(objStr);
            flowVariableDao.updateObject(varO);
        }
    }

    @Override
    public void saveFlowVariable(String flowInstId, String sVar, Object sValue) {
        saveFlowNodeVariable(flowInstId, NodeInstance.RUN_TOKEN_GLOBAL, sVar, sValue);
    }

    @Override
    public void saveFlowNodeVariable(String nodeInstId, String sVar, Object sValue) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null) {
            logger.error("找不到节点实例：" + nodeInstId);
            return;
        }
        String nodeToken = nodeInst.getRunToken();
        saveFlowNodeVariable(nodeInst.getFlowInstId(), nodeToken, sVar, sValue);
    }

    @Override
    public List<FlowVariable> listFlowVariables(String flowInstId) {
        List<FlowVariable> lv = flowVariableDao.listFlowVariables(flowInstId);
        if (lv == null) {
            lv = new ArrayList<>();
        }
        return lv;
    }

    @Override
    public void saveFlowAttention(InstAttention attObj) {
        attObj = new InstAttention(attObj.getUserCode(), attObj.getFlowInstId(), DatetimeOpt.currentUtilDate(), attObj.getAttSetUser(), attObj.getAttSetMemo());
        attentionDao.mergeObject(attObj);
    }

    @Override
    public void deleteFlowAttention(String flowInstId, String attUser) {
        attentionDao.deleteObjectById(new InstAttentionId(attUser, flowInstId));
    }


    /**
     * 删除流程关注人员
     *
     * @param flowInstId 流程实例ID
     * @param optUser    关注设置人员
     */
    @Override
    public void deleteFlowAttentionByOptUser(String flowInstId, String optUser) {
        attentionDao.deleteFlowAttentionByOptUser(flowInstId, optUser);
    }

    @Override
    public void deleteFlowAttention(String flowInstId) {
        attentionDao.deleteFlowAttention(flowInstId);
    }


    /**
     * 获取流程关注人员
     *
     * @param flowInstId 流程实例ID
     * @return 关注人员
     */
    @Override
    public List<InstAttention> viewFlowAttention(String flowInstId) {
        return attentionDao.listAttentionByFlowInstId(flowInstId);
    }

    /**
     * @param flowInstId 流程实例ID
     * @param userCode   关注人员
     * @return 关注人员
     */
    @Override
    public InstAttention getFlowAttention(String flowInstId, String userCode) {
        return attentionDao.getObjectById(
            new InstAttentionId(userCode, flowInstId));
    }


    /**
     * 返回所有关在的项目
     *
     * @param userCode  关注人
     * @param instState N 正常  C 完成   P 暂停 挂起     F 强行结束  A 所有
     * @return 关注项目（流程）
     */
    @Override
    public List<FlowInstance> viewAttentionFLowInstance(String userCode, String instState) {
        //查询出指定用户的关注
        List<FlowInstance> flowInstances = new ArrayList<>();
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("userCode", userCode);
        List<InstAttention> instAttentions = attentionDao.listObjects(filterMap);
        if (instAttentions != null && instAttentions.size() > 0) {
            //对每个关注下的实例验证状态
            for (InstAttention instAttention : instAttentions) {
                FlowInstance flowInstance = flowInstanceDao.getObjectById(instAttention.getFlowInstId());
                if ("A".equals(instState)) {
                    flowInstances.add(flowInstance);
                    continue;
                }
                if (flowInstance != null && instState != null && instState.equals(flowInstance.getInstState())) {
                    flowInstances.add(flowInstance);
                }
            }
        }
        return flowInstances;
    }

    @Override
    public List<FlowInstance> viewAttentionFLowInstanceByOptName(String optName, String userCode, String instState) {
        List<FlowInstance> flowInstanceList = viewAttentionFLowInstance(userCode, instState);
        if (StringUtils.isBlank(optName)) {
            return flowInstanceList;
        }
        if (flowInstanceList != null && flowInstanceList.size() > 0) {
            Iterator<FlowInstance> iterator = flowInstanceList.iterator();
            while (iterator.hasNext()) {
                FlowInstance flowInstance = iterator.next();
                String flowOptName = flowInstance.getFlowOptName();
                if (StringUtils.isBlank(flowOptName) || !(flowOptName.contains(optName))) {
                    iterator.remove();
                }
            }
        }
        return flowInstanceList;
    }

    @Override
    public List<FlowVariable> viewFlowVariablesByVarName(String flowInstId,
                                                         String varName) {
        return flowVariableDao.viewFlowVariablesByVarname(
            flowInstId, varName);
    }

    @Override
    public List<UserTask> listUserTasks(String userCode, PageDesc pageDesc) {
        return actionTaskDao.listUserTaskByFilter(
            CollectionsOpt.createHashMap("userCode", userCode), pageDesc);
    }

    @Override
    public List<UserTask> listUserTasksByFilter(Map<String, Object> filterMap, PageDesc pageDesc) {
        Object notNodeCodes = filterMap.get("notNodeCodes");
        Object nodeCodes = filterMap.get("nodeCodes");
        if (notNodeCodes != null) {
            filterMap.put("notNodeCodes", notNodeCodes.toString().split(","));
        }
        if (nodeCodes != null) {
            filterMap.put("nodeCodes", nodeCodes.toString().split(","));
        }
        return actionTaskDao.listUserTaskByFilter(filterMap, pageDesc);
    }


    @Override
    public List<UserTask> listUserTasksByFlowCode(String userCode,
                                                  String flowCode, PageDesc pageDesc) {
        return actionTaskDao.listUserTaskByFilter(
            CollectionsOpt.createHashMap("userCode", userCode, "flowCode", flowCode), pageDesc);
    }


    @Override
    public List<UserTask> listUserTasksByFlowStage(String userCode,
                                                   String flowStage, PageDesc pageDesc) {

        return actionTaskDao.listUserTaskByFilter(
            CollectionsOpt.createHashMap("userCode", userCode, "stageCode", flowStage), pageDesc);
    }

    @Override
    public List<UserTask> listUserTasksByNodeCode(String userCode,
                                                  String nodeCode, PageDesc pageDesc) {
        return actionTaskDao.listUserTaskByFilter(
            CollectionsOpt.createHashMap("userCode", userCode, "nodeCode", nodeCode), pageDesc);
    }

    /**
     * 获取节点的所有操作人员
     *
     * @param nodeInstId 节点实例id
     * @return 操作人员
     */
    @Override
    public List<UserTask> listNodeOperator(String nodeInstId) {
        Map<String, Object> searchColumn = new HashMap<>(1);
        searchColumn.put("nodeInstId", nodeInstId);
        return this.listUserTasksByFilter(searchColumn, new PageDesc(-1, -1));
    }

    @Override
    public List<UserTask> listDynamicTask(Map<String, Object> searchColumn, PageDesc pageDesc) {
        List<UserTask> taskList = new ArrayList<>();
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext();
        //动态任务
        //1.找到用户所有机构下的岗位和职务
        List<? extends IUserUnit> iUserUnits = context.listUserUnits((String) searchColumn.get("userCode"));

        //2.以机构，岗位，职务来查询任务
        if (iUserUnits == null || iUserUnits.size() == 0) {
            return taskList;
        }
        for (IUserUnit i : iUserUnits) {
            searchColumn.put("unitCode", i.getUnitCode());
            searchColumn.put("userStation", i.getUserStation());
            List<UserTask> dynamicTask = actionTaskDao.queryDynamicTask(searchColumn, pageDesc);
            if (dynamicTask != null) {
                taskList.addAll(dynamicTask);
            }
        }

        return taskList;
    }

    @Override
    public List<UserTask> listDynamicTaskByUnitStation(Map<String, Object> searchColumn, PageDesc pageDesc) {
        List<UserTask> taskList = new ArrayList<>();
        String station = StringBaseOpt.castObjectToString(searchColumn.get("userStation"));
        String unitCode = StringBaseOpt.castObjectToString(searchColumn.get("unitCode"));
        String nodeInstId = (String) searchColumn.get("nodeInstId");
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext();
        List<? extends IUserUnit> userUnits = context.listAllUserUnits();
        //2.以机构，岗位，职务来查询任务
        for (IUserUnit i : userUnits) {
            if (StringUtils.isNotBlank(unitCode)) {
                if (!unitCode.equals(i.getUnitCode())) {
                    continue;
                }
            }
            if (!StringUtils.equals(station, i.getUserStation())) {
                continue;
            }
            searchColumn.put("nodeInstId", nodeInstId);
            searchColumn.put("unitCode", i.getUnitCode());
            searchColumn.put("userStation", i.getUserStation());
            List<UserTask> dynamicTask = actionTaskDao.queryDynamicTask(searchColumn, pageDesc);
            if (dynamicTask != null) {
                for (UserTask u : dynamicTask) {
                    u.setUserCode(i.getUserCode());
                }
                taskList.addAll(dynamicTask);
            }
        }

        return taskList;
    }

    @Override
    public List<UserTask> listTasks(Map<String, Object> searchColumn, PageDesc pageDesc) {
        List<UserTask> taskList = new ArrayList<>();
        //静态任务
        //List<UserTask> staticTaskList = actionTaskDao.queryStaticTask((String) searchColumn.get("userCode"));
        List<UserTask> staticTaskList = actionTaskDao.listUserTaskByFilter(searchColumn, pageDesc);
        if (staticTaskList != null) {
            taskList.addAll(staticTaskList);
        }
        //动态任务
        //1.找到用户主机构下的岗位和职务
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext();
        List<? extends IUserUnit> iUserUnits = context.listUserUnits((String) searchColumn.get("userCode"));
        IUserUnit userUnit = null;
        if (iUserUnits != null && iUserUnits.size() > 0) {
            for (IUserUnit iUserUnit : iUserUnits) {
                if ("T".equals(iUserUnit.getRelType()) || "O".equals(iUserUnit.getRelType())) {
                    userUnit = iUserUnit;
                    break;
                }
            }
        }
        //2.以机构，岗位，职务来查询任务
        if (userUnit == null) {
            return taskList;
        }
        searchColumn.put("unitCode", userUnit.getUnitCode());
        searchColumn.put("userStation", userUnit.getUserStation());
        List<UserTask> dynamicTaskList = actionTaskDao.queryDynamicTask(searchColumn, pageDesc);
        if (dynamicTaskList != null) {
            taskList.addAll(dynamicTaskList);
        }
        return taskList;
    }

    @Override
    public boolean canAccess(String nodeInstId, String userCode) {
        if (userCode == null) {
            return false;
        }
        return StringUtils.isNotBlank(
            actionTaskDao.checkTaskGrantor(nodeInstId, userCode, null));
    }

    @Override
    public List<UserTask> listUserCompleteTasks(Map<String, Object> filterMap, PageDesc pageDesc) {
        Object nodeCodes = filterMap.get("nodeCodes");
        if (nodeCodes != null) {
            filterMap.put("nodeCodes", nodeCodes.toString().split(","));
        }
        return actionTaskDao.listUserTaskFinByFilter(filterMap, pageDesc);
    }


    @Override
    public List<FlowWarning> listFlowWarning(Map<String, Object> filterMap,
                                             PageDesc pageDesc) {
        return new ArrayList<>(
            runtimeWarningDao.listObjectsByProperties(filterMap, pageDesc));
    }

    @Override
    public List<FlowWarning> listFlowWarningByInst(String flowInstId,
                                                   PageDesc pageDesc) {
        return new ArrayList<>(
            runtimeWarningDao.listFlowWarningByInst(flowInstId, pageDesc));
    }

    @Override
    public List<FlowWarning> listFlowWarningByNodeInst(String nodeInstId,
                                                       PageDesc pageDesc) {
        return new ArrayList<>(
            runtimeWarningDao.listFlowWarningByNodeInst(nodeInstId, pageDesc));
    }

    @Override
    public List<FlowWarning> listFlowWarningByWarningCode(String warningCode,
                                                          PageDesc pageDesc) {
        return new ArrayList<>(
            runtimeWarningDao.listFlowWarningByWarningCode(warningCode, pageDesc));
    }

    @Override
    public void deleteFlowVariable(String flowInstId, String runToken, String varName) {
        if (flowInstId == null) {
            return;
        }
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("flowInstId", flowInstId);
        if (StringUtils.isNotBlank(runToken)) {
            filterMap.put("runToken", runToken);
        }
        if (StringUtils.isNotBlank(varName)) {
            filterMap.put("varName", varName);
        }
        flowVariableDao.deleteObjectsByProperties(filterMap);
    }

    @Override
    public FlowInstanceGroup createFlowInstGroup(String name, String desc) {
        String flowInstGroupId = UuidOpt.getUuidAsString32();
        FlowInstanceGroup flowInstGroup = new FlowInstanceGroup();
        flowInstGroup.setFlowGroupId(flowInstGroupId);
        flowInstGroup.setFlowGroupName(name);
        flowInstGroup.setFlowGroupDesc(desc);
        flowInstanceGroupDao.saveNewObject(flowInstGroup);
        return flowInstGroup;
    }

    @Override
    public List<FlowInstanceGroup> listFlowInstGroup(Map<String, Object> filterMap, PageDesc pageDesc) {
        return flowInstanceGroupDao.listObjects(filterMap, pageDesc);
    }

    @Override
    public JSONArray viewFlowNodes(String flowInstId) {
        FlowInstance flowInst = flowInstanceDao.getObjectById(flowInstId);

        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("flowInstId", flowInstId);
        filterMap.put("flowCode", flowInst.getFlowCode());
        filterMap.put("version", flowInst.getVersion());
        JSONArray flowNodes = nodeInstanceDao.viewFlowNodes(filterMap);
        for (Object flowNode : flowNodes) {
            JSONObject jsonObject = (JSONObject) flowNode;
            jsonObject.putIfAbsent("nodeState", "0");
            if ("N".equals((jsonObject).getString("nodeState"))) {
                // 办理中节点添加待办用户
                List<UserTask> userTasks = actionTaskDao.listUserTaskByFilter(
                    CollectionsOpt.createHashMap("nodeInstId", (jsonObject).getString("nodeInstId")), null);
                (jsonObject).put("userTasks", DictionaryMapUtils.objectsToJSONArray(userTasks));
            } else {
                String lastUpdateUserName = CodeRepositoryUtil.getValue("userCode", (jsonObject).getString("lastUpdateUser"));
                (jsonObject).put("lastUpdateUserName", lastUpdateUserName);
            }
        }
        return flowNodes;
    }

    @Override
    public JSONArray listNodeTasks(List<String> nextNodeInstList) {
        JSONArray nodeTasks = null;
        for (String nodeInstId : nextNodeInstList) {
            if (nodeTasks == null) {
                nodeTasks = getNodeTasks(nodeInstId);
            } else {
                nodeTasks.addAll(getNodeTasks(nodeInstId));
            }
        }
        return nodeTasks;
    }

    @Override
    public JSONArray getNodeTasks(String nodeInstId) {
        JSONArray nodeTaskList = new JSONArray();
        Map<String, Object> nodeTaskMap = new HashMap<>();
        NodeInstance nodeInstance = this.getNodeInstById(nodeInstId);
        if (nodeInstance == null) {
            return nodeTaskList;
        }
        NodeInfo nodeInfo = nodeInstance.getNode();
        // D:自动运行节点  R:路由节点  E:消息相应节点（同步节点） F:结束
        if ("D,R,E,F".contains(nodeInfo.getNodeType())) {
            return nodeTaskList;
        }

        nodeTaskMap.put("nodeInstId", nodeInstance.getNodeInstId());
        nodeTaskMap.put("taskAssigned", nodeInstance.getTaskAssigned());
        nodeTaskMap.put("nodeName", nodeInfo.getNodeName());
        nodeTaskMap.put("nodeCode", nodeInfo.getNodeCode());
        HttpServletRequest request = RequestThreadLocal.getLocalThreadWrapperRequest();
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        if ("S".equals(nodeInstance.getTaskAssigned())) {
            IUserInfo userInfo = CodeRepositoryUtil.getUserInfoByCode(topUnit, nodeInstance.getUserCode());
            Map<String, Object> userTaskMap = new HashMap<>(nodeTaskMap);
            userTaskMap.put("userCode", nodeInstance.getUserCode());
            if (userInfo != null) {
                userTaskMap.put("userName", userInfo.getUserName());
                userTaskMap.put("regCellPhone", userInfo.getRegCellPhone());
                userTaskMap.put("regEmail", userInfo.getRegEmail());
            }
            nodeTaskList.add(userTaskMap);
        } else {
            Set<ActionTask> wfActionTasks = nodeInstance.getWfActionTasks();
            Set<String> userSet = new HashSet<>();
            wfActionTasks.forEach(t -> userSet.add(t.getUserCode()));
            List<IUserInfo> userTasks = CodeRepositoryUtil.getUserInfosByCodes(topUnit, userSet);
            userTasks.forEach(userInfo -> {
                Map<String, Object> userTaskMap = new HashMap<>(nodeTaskMap);
                //userTaskMap.put("userCode", nodeInstance.getUserCode()); userCode改为从userInfo中获取 修改人:薛庆坤
                userTaskMap.put("userCode", userInfo.getUserCode());
                if (userInfo != null) {
                    userTaskMap.put("userName", userInfo.getUserName());
                    userTaskMap.put("regCellPhone", userInfo.getRegCellPhone());
                    userTaskMap.put("regEmail", userInfo.getRegEmail());
                }
                nodeTaskList.add(userTaskMap);
            });
        }
        return nodeTaskList;
    }

    /**
     * 更新办件角色
     */
    @Override
    public void updateFlowWorkTeam(FlowWorkTeam u) {
        flowTeamDao.mergeObject(u);
    }
}
