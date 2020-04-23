package com.centit.workflow.service.impl;

import com.centit.framework.appclient.RestfulHttpRequest;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.components.SysUserFilterEngine;
import com.centit.framework.components.impl.ObjectUserUnitVariableTranslate;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitVariableTranslate;
import com.centit.framework.model.basedata.IUserUnit;
import com.centit.framework.model.basedata.NoticeMessage;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.support.algorithm.*;
import com.centit.support.common.LeftRightPair;
import com.centit.support.compiler.VariableFormula;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.database.utils.QueryUtils;
import com.centit.workflow.commons.*;
import com.centit.workflow.dao.*;
import com.centit.workflow.po.*;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.FlowManager;
import com.centit.workflow.service.UserUnitFilterCalcContextFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletContext;
import java.io.Serializable;
import java.util.*;

@Service
@Transactional
public class FlowEngineImpl implements FlowEngine, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(FlowEngineImpl.class);
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
    private FlowOptInfoDao flowOptInfoDao;
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

    private final static Object lockObject = new Object();

    public FlowEngineImpl() {
        //lockObject = new Object();
    }

    private void saveValueAndRoleInOptions(String flowInstId, String runToken, FlowOptParamOptions options){
        // 设置流程变量
        if(options.getVariables() != null && !options.getVariables().isEmpty()) {
            for(Map.Entry<String,Object> ent : options.getVariables().entrySet()) {
                saveFlowNodeVariable(flowInstId, runToken, ent.getKey(), ent.getValue());
            }
        }
        // 设置全局流程变量
        if(options.getGlobalVariables() != null && !options.getGlobalVariables().isEmpty()) {
            for(Map.Entry<String,Object> ent : options.getGlobalVariables().entrySet()) {
                saveFlowVariable(flowInstId, ent.getKey(), ent.getValue());
            }
        }
        // 设置办件角色
        if(options.getFlowRoleUsers() != null && !options.getFlowRoleUsers().isEmpty()) {
            for(Map.Entry<String,List<String>> ent : options.getFlowRoleUsers().entrySet()) {
                assignFlowWorkTeam(flowInstId, ent.getKey(), ent.getValue());
            }
        }
        // 设置流程机构
        if(options.getFlowOrganizes() != null && !options.getFlowOrganizes().isEmpty()) {
            for(Map.Entry<String,List<String>> ent : options.getFlowOrganizes().entrySet()) {
                assignFlowOrganize(flowInstId, ent.getKey(), ent.getValue());
            }
        }
    }

    /**
     * 创建流程实例或子流程实例
     *
     * @param options NewFlowInstanceOptions   流程编码
     * param nodeInstId 节点实例编号 ,节点编号不为0表示为子流程
     * param userCode   用户编码
     * param unitCode   机构编码
     * @param varTrans UserUnitVariableTranslate 机构执行环境
     * @param application spring上下文环境。作为独立服务后这个应该不需要了
     * @return FlowInstance
     */
    @Override
    public FlowInstance createInstance(CreateFlowOptions options,
                                             UserUnitVariableTranslate varTrans, ServletContext application) {
        FlowInstance instance = createInstanceInside(options, varTrans,  application,true);
        // 记录日志
        return instance;
    }

    /**
     * 创建流程实例  返回流程实例
     * @param options NewFlowInstanceOptions 流程创建选项编码
     * @return 流程实例
     */
    @Override
    public FlowInstance createInstance(CreateFlowOptions options){
        return createInstance(options,
            new ObjectUserUnitVariableTranslate(
                CollectionsOpt.unionTwoMap(
                    options.getVariables(), options.getGlobalVariables())),
            null);
    }

    private FlowInstance createInstanceInside(CreateFlowOptions options,
                                             UserUnitVariableTranslate varTrans, ServletContext application, boolean saveOptions) {

        Date createTime = new Date(System.currentTimeMillis());
        if(options.getFlowVersion()<1) {
            options.version(flowDefDao.getLastVersion(options.getFlowCode()));
        }
        //获取流程信息
        FlowInfo wf = flowDefDao.getFlowDefineByID(options.getFlowCode(), options.getFlowVersion());

        //获取流程实例编号
        String flowInstId = StringUtils.isBlank(options.getFlowInstId())?
                            UuidOpt.getUuidAsString32() : options.getFlowInstId();

        FlowInstance flowInst = FlowOptUtils.createFlowInst(
            options.getUnitCode(), options.getUserCode(), wf, flowInstId, options.getTimeLimitStr());

        flowInst.setCreateTime(createTime);
        flowInst.setFlowGroupId(options.getFlowGroupId());
        //节点实例编号不为空，为子流程，创建子流程时要给父节点的状态设置为 W：等待子流程返回
        if (StringUtils.isNotBlank(options.getParentNodeInstId())) {
            flowInst.setPreNodeInstId(options.getParentNodeInstId());
            flowInst.setPreInstId(options.getParentFlowInstId());
            FlowInstance parentInst = flowInstanceDao.getObjectById(options.getParentFlowInstId());
            //如果没有指定 分组，使用父节点分组
            if(StringUtils.isBlank(options.getFlowGroupId())
                && parentInst != null
                && StringUtils.isNotBlank(parentInst.getFlowGroupId())) {
                // 子流程继承父流程的 流程实例组
                flowInst.setFlowGroupId(parentInst.getFlowGroupId());
            }
            flowInst.setIsSubInst("Y");
        }
        flowInst.setFlowOptName(options.getFlowOptName());
        flowInst.setFlowOptTag(options.getFlowOptTag());
        flowInstanceDao.saveNewObject(flowInst);
        // ----------- 创建 流程 实例 结束
        if(saveOptions) {
            saveValueAndRoleInOptions(flowInstId, "T", options);
        }
        //生成首节点实例编号
        NodeInfo node = wf.getFirstNode();
        if (node == null) {
            throw new WorkflowException(WorkflowException.FlowDefineError, "找不到首节点");
        }
        FlowVariableTranslate flowVarTrans = FlowOptUtils.createVariableTranslate(
            null, flowInst,flowVariableDao,this, options);
        flowVarTrans.setFlowVarTrans(varTrans);

        List<String> nodeInsts = submitToNextNode(node, "T", flowInst, wf,
            null, null, null,
            options, flowVarTrans, application);

        if(options.isSkipFirstNode() && !"R".equals(node.getNodeType()) && nodeInsts.size()==1){
            nodeInsts = submitOptInside(SubmitOptOptions.create()
                .copy(options).nodeInst(nodeInsts.iterator().next()),
                varTrans, application,false, false);
        }

        OperationLogCenter.log(FlowOptUtils.createActionLog(
            options.getUserCode(), flowInstId ,"创建流程，创建首节点:" +
                StringBaseOpt.castObjectToString(nodeInsts)));
        //flowInstanceDao.saveObjectReference(flowInst, "flowNodeInstances");
        //flowInstanceDao.saveObjectReference(flowInst, "flowStageInstances");
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
        if(flowInst==null){
            return null;
        }
        return flowDefDao.getFlowDefineByID(
            flowInst.getFlowCode(), flowInst.getVersion());
    }

    /**
     * 获取流程业务信息
     * @param flowInstId 实例id
     * @return 流程业务信息
     */
    @Override
    public FlowOptInfo getFlowOptInfo(String flowInstId){
        FlowInfo flowInfo = getFlowDefine(flowInstId);
        if(flowInfo==null){
            return null;
        }
        return flowOptInfoDao.getObjectById(flowInfo.getOptId());
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
    public NodeInfo getNodeInfo(String nodeInstId){
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if(nodeInst==null){
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
        /*FlowInstance flowInst = flowInstanceDao.getObjectById(flowInstId);
        if (flowInst == null)
            return;
        flowInst.setFlowOptName(flowOptName);
        flowInst.setFlowOptTag(flowOptTag);
        flowInstanceDao.updateObject(flowInst);*/
        flowInstanceDao.updateFlowInstOptInfo(flowInstId, flowOptName, flowOptTag);
    }

    /**
     * 设置节点实例参数
     * @param nodeInstId 节点实例id
     * @param nodeParam  节点实例参数
     */
    @Override
    public void updateNodeInstanceParam(String nodeInstId, String nodeParam){
        nodeInstanceDao.updtNodeInstParam(nodeInstId, nodeParam);
    }

    /**
     * 针对 抢先类别的 节点， 锁定任务，这个任务后续只能由 他来做
     * @param nodeInstId 节点实例id
     * @param userCode  用户
     */
    @Override
    public void lockNodeTask(String nodeInstId, String userCode){
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if("T".equals(nodeInst.getTaskAssigned())){
            NodeInfo node = flowNodeDao.getObjectById(nodeInst.getNodeId());
            //B: 抢先机制
            if("B".equals(node.getOptType())){
                nodeInst.setUserCode(userCode);
                nodeInst.setTaskAssigned("S");
                nodeInstanceDao.updateObject(nodeInst);
            }
        }
    }

    @Override
    public void updateFlowInstParentNode(String flowInstId, String parentFlowInstId, String parentNodeInstId) {
        FlowInstance flowInst = flowInstanceDao.getObjectById(flowInstId);
        if (flowInst == null)
            return;
        flowInst.setPreInstId(parentFlowInstId);
        flowInst.setPreNodeInstId(parentNodeInstId);
        flowInstanceDao.updateObject(flowInst);
    }

    @Override
    public String getNodeOptUrl(String nodeInstId, String userCode) {

        List<UserTask> taskList = actionTaskDao.listUserTaskByFilter(
            QueryUtils.createSqlParamsMap("nodeInstId", nodeInstId), new PageDesc(-1, -1));

        if (taskList == null || taskList.size() == 0)
            return null;
        else {
            UserTask task = taskList.get(0);
            return task.getNodeOptUrl();
        }
    }

    /**
     * 列举当前流程可以创建的所有节点
     * @param flowInstId 流程实例代码
     * @return Map 节点代码， 节点名称
     */
    @Override
    public Map<String, String> listFlowNodeForCreate(String flowInstId){
        FlowInstance flowInst = flowInstanceDao.getObjectById(flowInstId);
        if (flowInst == null)
            return null;
        List<NodeInfo> nodeList = flowNodeDao.listObjects(
            CollectionsOpt.createHashMap("flowCode",flowInst.getFlowCode(),
            "version", flowInst.getVersion()));
        Map<String, String> nodes = new HashMap<>();
        for(NodeInfo node:nodeList){
            if(StringUtils.isNotBlank(node.getNodeCode())){
                nodes.put(node.getNodeCode(), node.getNodeName());
            }
        }
        return nodes;
    }

    @Override
    public List<NodeInstance> listNodeInstsByNodecode(String flowInstId, String nodeCode) {
        FlowInstance flowInst = flowInstanceDao.getObjectById(flowInstId);
        if (flowInst == null)
            return null;
        List<NodeInfo> nodeList = flowNodeDao.listNodeByNodecode(flowInst.getFlowCode(),
            flowInst.getVersion(), nodeCode);
        if (nodeList == null)
            return null;
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
        if (nodeInst == null)
            return null;
        FlowInstance flowInst = flowInstanceDao.getObjectById(nodeInst.getFlowInstId());
        if (flowInst == null)
            return null;
        //return flowInst.getStageInstanceByCode(nodeInst.getFlowPhase());
        NodeInfo nodeinfo = flowNodeDao.getObjectById(nodeInst.getNodeId());
        if (nodeinfo == null)
            return null;

        return flowInst.getStageInstanceByCode(nodeinfo.getStageCode());
    }


    private void endFlowInstance(FlowInstance flowInst, FlowInfo flowInfo, NodeInfo endNode,
                                 String transPath, FlowTransition trans, String preNodeInstId, String userCode, String unitCode) {
        FlowOptUtils.endInstance(flowInst, "C", userCode, flowInstanceDao);

        NodeInstance endNodeInst =
            FlowOptUtils.createNodeInst(unitCode, userCode, flowInst, null, flowInfo, endNode, trans);
        endNodeInst.setNodeInstId(UuidOpt.getUuidAsString32());
//        endNodeInst.setNodeInstId(nodeInstanceDao.getNextNodeInstId());
        endNodeInst.setNodeState("C");
        Date updateTime = DatetimeOpt.currentUtilDate();
        endNodeInst.setLastUpdateTime(updateTime);
        endNodeInst.setLastUpdateUser(userCode);
        endNodeInst.setPrevNodeInstId(preNodeInstId);
        if (StringUtils.isBlank(transPath))
            endNodeInst.setTransPath(trans.getTransId());
        else
            endNodeInst.setTransPath(transPath + "," + trans.getTransId());
        nodeInstanceDao.saveNewObject(endNodeInst);
        //FlowOptUtils.sendFinishMsg(flowInst.getFlowInstId(), userCode);
    }

    private LeftRightPair<Set<String>, Set<String>>
        calcNodeUnitAndOpterators(FlowInstance flowInst, NodeInstance preNodeInst,
                                                         String nodeToken,
                                                         NodeInfo nextOptNode,
                                                         FlowOptParamOptions options,
                                                         FlowVariableTranslate varTrans) {
        // 参数指定
        Set<String> nodeUnits = null;
        if (options.getNodeUnits() != null ) {
            String nodeUnit = options.getNodeUnits().get(nextOptNode.getNodeId());
            if(StringUtils.isBlank(nodeUnit)){
                nodeUnit = options.getNodeUnits().get(nextOptNode.getNodeCode());
            }
            if(StringUtils.isNotBlank(nodeUnit)){
                nodeUnits = CollectionsOpt.createHashSet(nodeUnit);
            }
        }
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext();
        context.setVarTrans(varTrans);
        //构建内置变量
        NodeInstance oldNodeInst = flowInst.findLastSameNodeInst(nextOptNode.getNodeId(), preNodeInst, "");
        // L 上一次运行到本节点的 用户和机构
        if (oldNodeInst != null) {
            context.addUnitParam("L", oldNodeInst.getUnitCode());
            context.addUserParam("L", oldNodeInst.getUserCode());
        }
        // P 前面一个节点的 用户和机构
        NodeInstance preNode = flowInst.getNearestNode(preNodeInst, nodeToken);
        if (preNode != null) {
            context.addUnitParam( "P", preNode.getUnitCode());
            context.addUserParam("P", preNode.getUserCode());
        }
        // C 参数指定的，就是提交的人和机构
        context.addUnitParam( "C",
            options.getUnitCode() == null ?
                context.getUserInfoByCode(options.getUserCode()).getPrimaryUnit():
                options.getUnitCode());
        context.addUserParam("C",
            options.getUserCode());
        // F 流程的 用户 和 机构
        context.addUnitParam( "F", flowInst.getUnitCode());
        context.addUserParam("F", flowInst.getUserCode());

        //调用机构引擎来计算 unitCode
        // 如果指定机构 就不需要再进行计算了
        if (CollectionUtils.isEmpty(nodeUnits)) {
            nodeUnits = UserUnitCalcEngine.calcUnitsByExp(context,
                nextOptNode.getUnitExp());
            //nextNodeUnit = UserUnitCalcEngine.calcSingleUnitByExp(userUnitFilterCalcContext,
            // nextOptNode.getUnitExp(),unitParams, varTrans);
        }
        if(CollectionUtils.isNotEmpty(nodeUnits)) {
            // 将 机构表达式
            context.addUnitParam("N", nodeUnits);
        }
        // 强制锁定用户 优先级最好
        if(options.isLockOptUser()){
            String optUser = options.getWorkUserCode();
            if(StringUtils.isNotBlank(optUser)) {
                return new LeftRightPair<>(nodeUnits, CollectionsOpt.createHashSet(optUser));
            }
        }

        // 通过节点 映射
        Set<String> optUsers = null;
        if(options.getNodeOptUsers() != null){
            optUsers = options.getNodeOptUsers().get(nextOptNode.getNodeId());
            if(optUsers != null && !optUsers.isEmpty()){
                return new LeftRightPair<>(nodeUnits, optUsers);
            }
            optUsers = options.getNodeOptUsers().get(nextOptNode.getNodeCode());
            if(optUsers != null && !optUsers.isEmpty()){
                return new LeftRightPair<>(nodeUnits, optUsers);
            }
        }
        // 流程引擎 权限体系
        if (SysUserFilterEngine.ROLE_TYPE_ENGINE.equalsIgnoreCase(nextOptNode.getRoleType())
            || SysUserFilterEngine.ROLE_TYPE_ENGINE_FORMULA .equalsIgnoreCase(nextOptNode.getRoleType()) ) {
            // 权限表达式
            String roleFormula = nextOptNode.getPowerExp();
            if(SysUserFilterEngine.ROLE_TYPE_ENGINE_FORMULA .equalsIgnoreCase(nextOptNode.getRoleType())){
                RoleFormula rf = roleFormulaDao.getObjectById(nextOptNode.getRoleCode());
                if(rf!=null){
                    roleFormula = rf.getRoleFormula();
                }
            }
            //如果节点的角色类别为 权限引擎则要调用权限引擎来分配角色
            //根据权限表达式创建任务列表
            optUsers = UserUnitCalcEngine.calcOperators(context, roleFormula);
            if (optUsers == null || optUsers.size() == 0) {
                logger.error("权限引擎没有识别出符合表达式的操作人员！ wid:" + flowInst.getFlowInstId() + " nid:" + nextOptNode.getNodeId());
            }
        } else if (SysUserFilterEngine.ROLE_TYPE_ITEM.equalsIgnoreCase(nextOptNode.getRoleType())) {
            optUsers = new HashSet<>();
            List<FlowWorkTeam> users = flowTeamDao.listFlowWorkTeamByRole(flowInst.getFlowInstId(), nextOptNode.getRoleCode());
            for (FlowWorkTeam u : users)
                optUsers.add(u.getUserCode());
            //流程角色（审批角色）待测试
        } else/*gw xz ro(system)*/
            if(StringUtils.isNotBlank(nextOptNode.getRoleType())
                && StringUtils.isNotBlank(nextOptNode.getRoleCode())) {
            optUsers = SysUserFilterEngine.getUsersByRoleAndUnit(context,
                nextOptNode.getRoleType(), nextOptNode.getRoleCode(), options.getUnitCode());
        }

        return new LeftRightPair<>(nodeUnits, optUsers);
    }

    private void closeNodeInstanceInside(Collection<NodeInstance> sameNodes, Date currentTime, String userCode){
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
        }
    }

    private List<String> submitToNextRouterNode(
        NodeInfo nextRoutertNode, String nodeToken, FlowInstance flowInst, FlowInfo flowInfo,
        NodeInstance preNodeInst/*创建首节点时为null*/, String transPath, FlowTransition trans,
        FlowOptParamOptions options,
        FlowVariableTranslate flowVarTrans, ServletContext application)
        throws WorkflowException {

        String sRT = nextRoutertNode.getRouterType();
        List<String> resNodes = new ArrayList<>();
        String preTransPath = StringUtils.isBlank(transPath) ?
            trans.getTransId() : transPath + "," + trans.getTransId();

        if ("H".equals(sRT) || "D".equals(sRT)) {//D 分支和 H 并行
            //提交游离分支上的叶子节点 将不会向后流转
            //获取下一批流转节点
            Set<FlowTransition> selTrans = selectTransitions(nextRoutertNode, flowVarTrans);
            if (selTrans == null || selTrans.size() < 1) {
                if (nodeToken.startsWith("R")) { // 这个表示游离节点如果没有后续节点直接返回
                    return resNodes;
                } else {
                    String errorMsg = "找不到后续节点：" + flowInst.getFlowInstId() +
                        (preNodeInst!=null? "; 节点：" + preNodeInst.getNodeInstId() :";") +
                        " 路由：" + nextRoutertNode.getNodeId();
                    logger.error(errorMsg);
                    throw new WorkflowException(WorkflowException.NotFoundNextNode,errorMsg);
                }
            }
            // D:分支 E:汇聚  G 多实例节点  H并行  R 游离 S：同步
            if ("D".equals(sRT)) {
                // 分支节点只执行第一个符合条件的，如果有多个符合条件的 按道理应该报异常
                // 这里为了最大可运行不报异常
                FlowTransition nodeTran = selTrans.iterator().next();
                String nextNodeId = nodeTran.getEndNodeId();
                resNodes = submitToNextNode(
                    flowNodeDao.getObjectById(nextNodeId), nodeToken, flowInst, flowInfo,
                    preNodeInst, preTransPath, nodeTran, options,
                    flowVarTrans, application);
            } else {
                int nNo = 1; //子令牌序号
                for (FlowTransition tran : selTrans) {
                    String nextNodeId = tran.getEndNodeId();
                    List<String> nNs = submitToNextNode(
                        flowNodeDao.getObjectById(nextNodeId), nodeToken + "." + nNo,
                        flowInst, flowInfo,
                        preNodeInst, preTransPath, tran, options,
                        flowVarTrans, application);
                    resNodes.addAll(nNs);
                    nNo++;
                }
            }
        } else {
            if ("E".equals(sRT)) {//汇聚
                String preRunToken = NodeInstance.calcSuperToken(nodeToken);
                Set<String> nNs = // 所有未提交的需要等待的 子节点令牌
                    flowInst.calcNoSubmitSubNodeTokensInstByToken(preRunToken);
                //汇聚节点，所有节点都已提交,或者只要当前节点
                boolean canSubmit = nNs == null || nNs.size() == 0 || (nNs.size() == 1 && nNs.contains(nodeToken));
                //A 所有都完成，R 至少有X完成，L 至多有X未完成， V 完成比率达到X
                String sCT = nextRoutertNode.getConvergeType();
                if (! canSubmit && !"A".equals(sCT)) {
                    Set<String> submitNodeIds = flowInst.calcSubmitSubNodeIdByToken(preRunToken);
                    List<FlowTransition> transList =
                        flowTransitionDao.getNodeInputTrans(nextRoutertNode.getNodeId());
                    canSubmit = true;
                    for (FlowTransition tran : transList) {
                        if ("F".equals(tran.getCanIgnore()) && !submitNodeIds.contains(tran.getStartNodeId()))
                            canSubmit = false;
                    }

                    if (canSubmit) {
                        if (StringRegularOpt.isNumber(nextRoutertNode.getConvergeParam())) {
                            if ("R".equals(sCT)) {
                                canSubmit = submitNodeIds.size() >=
                                    Integer.valueOf(nextRoutertNode.getConvergeParam());
                            } else if ("L".equals(sCT)) {
                                canSubmit = nNs.size() <= Integer.valueOf(nextRoutertNode.getConvergeParam());
                            } else if ("V".equals(sCT)) {
                                canSubmit = (double) submitNodeIds.size() / (double) (submitNodeIds.size() + nNs.size())
                                    >= Double.valueOf(nextRoutertNode.getConvergeParam());
                            } else
                                canSubmit = false;
                        } else
                            canSubmit = false;
                    }
                }

                if (canSubmit) {
                    Set<NodeInstance> sameNodes = flowInst.findAllActiveSubNodeInstByToken(preRunToken);
                    if(sameNodes != null && !sameNodes.isEmpty()) {
                        //结束这些节点
                        Date currentTime = new Date(System.currentTimeMillis());
                        closeNodeInstanceInside(sameNodes, currentTime, options.getUserCode());
                    }

                    FlowTransition nodeTran = selectOptNodeTransition(nextRoutertNode);
                    String nextNodeId = nodeTran.getEndNodeId();
                    for (FlowTransition f : flowInfo.getFlowTransitions()) {
                        if (nextRoutertNode.getNodeId().equals(f.getEndNodeId()) && !f.getTransId().equals(trans.getTransId())) {
                            preTransPath += "," + f.getTransId();
                        }
                    }
                    resNodes = submitToNextNode(
                        flowNodeDao.getObjectById(nextNodeId), preRunToken, flowInst, flowInfo,
                        preNodeInst, preTransPath, nodeTran, options,
                        flowVarTrans, application);
                }
            } else if ("G".equals(sRT)) {//多实例
                FlowTransition nodeTran = selectOptNodeTransition(nextRoutertNode);
                String nextNodeId = nodeTran.getEndNodeId();
                NodeInfo nextNode = flowNodeDao.getObjectById(nextNodeId);

                if (!"C".equals(nextNode.getNodeType())) { //报错
                    throw new WorkflowException(WorkflowException.FlowDefineError,
                        "多实例路由后面必须是业务节点：" +  flowInst.getFlowInstId() +
                            (preNodeInst!=null? "; 节点：" + preNodeInst.getNodeInstId() :";") +
                            " 路由：" + nextRoutertNode.getNodeId());
                }
                LeftRightPair<Set<String>, Set<String>> unitAndUsers = calcNodeUnitAndOpterators(flowInst, preNodeInst, nodeToken,
                    nextRoutertNode, options,
                    flowVarTrans);
                //D 机构， U  人员（权限表达式） V 变量
                if ("D".equals(nextRoutertNode.getMultiInstType())) {
                    Set<String> nextNodeUnits = unitAndUsers.getLeft();

                    if (nextNodeUnits == null || nextNodeUnits.size() == 0) { //报错
                        throw new WorkflowException(WorkflowException.NoValueForMultiInst,
                            "多实例节点对应的机构变量为空：" +  flowInst.getFlowInstId() +
                                (preNodeInst!=null? "; 节点：" + preNodeInst.getNodeInstId() :";") +
                                " 路由：" + nextRoutertNode.getNodeId());
                    } else {
                        int nRn = 1;
                        for (String uc : nextNodeUnits) {
                            // 持久变量，供后续节点使用
                            this.saveFlowNodeVariable(flowInst.getFlowInstId(), nodeToken + "." + nRn,
                                "cd_" + nextRoutertNode.getNodeCode(), uc);
                            //flowVarTrans.setInnerVariable( "cd_" + nextRoutertNode.getNodeCode(), uc);
                            flowVarTrans.setInnerVariable( "cursor", uc);
                            resNodes.addAll(submitToNextNode(
                                nextNode, nodeToken + "." + nRn, flowInst, flowInfo,
                                preNodeInst, preTransPath, nodeTran, options,
                                flowVarTrans,
                                application));
                            nRn++;
                        }
                    }
                } else if ("U".equals(nextRoutertNode.getMultiInstType())) {
                    Set<String> optUsers = unitAndUsers.getRight();
                    if (optUsers == null || optUsers.size() == 0) {
                        throw new WorkflowException(WorkflowException.NoValueForMultiInst,
                            "多实例节点对应的权限表达式人员为空：" +  flowInst.getFlowInstId() +
                                (preNodeInst!=null? "; 节点：" + preNodeInst.getNodeInstId() :";") +
                                " 路由：" + nextRoutertNode.getNodeId());
                    } else {
                        int nRn = 1;
                        //Date currentTime = new Date(System.currentTimeMillis());
                        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext();
                        for (String uc : optUsers) {
                            // 持久变量，供后续节点使用
                            this.saveFlowNodeVariable(flowInst.getFlowInstId(), nodeToken + "." + nRn,
                                "cu_" + nextRoutertNode.getNodeCode(), uc);
                            flowVarTrans.setInnerVariable( "cursor", uc);
                            resNodes.addAll(submitToNextNode(
                                nextNode, nodeToken + "." + nRn, flowInst, flowInfo,
                                preNodeInst, preTransPath, nodeTran,
                                SubmitOptOptions.create().copy(options).workUser(uc).lockOptUser(true)
                                    .unit(context.getUserInfoByCode(options.getUserCode()).getPrimaryUnit()),
                                flowVarTrans,
                                application));
                            nRn++;
                        }
                    }
                } /*else if ("V".equals(nextRoutertNode.getMultiInstType())) {
                    // 实现变量多实例 这个没有实际意义
                }*/
            } else if ("R".equals(sRT)) {//游离
                FlowTransition nodeTran = selectOptNodeTransition(nextRoutertNode);
                String nextNodeId = nodeTran.getEndNodeId();
                resNodes = submitToNextNode(
                    flowNodeDao.getObjectById(nextNodeId), "R" + nodeToken, flowInst, flowInfo,
                    preNodeInst, preTransPath, nodeTran, options,
                    flowVarTrans, application);
            } else if ("S".equals(sRT)) {//同步 保留
                String preRunToken = NodeInstance.calcSuperToken(nodeToken);
                Set<String> nNs =
                    flowInst.calcNoSubmitSubNodeTokensInstByToken(preRunToken);
                //查找需要同步的节点
                if (nNs == null || nNs.size() == 0) {
                    Map<String, NodeInstance> syncNodes =
                        flowInst.findSubmitSubNodeInstByToken(preRunToken);
                    for (Map.Entry<String, NodeInstance> ent : syncNodes.entrySet()) {
                        NodeInfo rtN = selectNextNodeByNodeId(ent.getValue().getNodeId());
                        if ("R".equals(rtN.getNodeType()) && "S".equals(rtN.getRouterType())) {
                            FlowTransition nextTran = selectOptNodeTransition(rtN);
                            List<String> sN = submitToNextNode(
                                flowNodeDao.getObjectById(nextTran.getEndNodeId()),
                                ent.getValue().getRunToken(), flowInst, flowInfo,
                                ent.getValue(), preTransPath, nextTran,
                                SubmitOptOptions.create().copy(options).user(ent.getValue().getLastUpdateUser())
                                    .unit( ent.getValue().getUnitCode()),
                                flowVarTrans, application);
                            resNodes.addAll(sN);
                        }
                    }
                }
            }
        }
        //WfNode routeNode =
        return resNodes;
    }

    private List<String> submitToNextNode(
        NodeInfo nextNode , String nodeToken, FlowInstance flowInst, FlowInfo flowInfo,
        NodeInstance preNodeInst, String transPath, FlowTransition nodeTran,
        FlowOptParamOptions options,
        FlowVariableTranslate varTrans,
        ServletContext application) throws WorkflowException {

        //Set<String> resNodes = new HashSet<>();
        //NodeInfo nextNode = flowNodeDao.getObjectById(nextNodeId);
        // A:开始 B:首节点(首节点不能是路由节点，如果是路由节点请设置为 哑元，跳转到后一个节点； B 的处理换个C一样)
        // C:业务节点  F结束  R: 路由节点
        if ("R".equals(nextNode.getNodeType())) { // 后续节点为路由节点
            return submitToNextRouterNode(
                nextNode, nodeToken, flowInst, flowInfo,
                preNodeInst, transPath, nodeTran, options,
                varTrans, application);
        } else if ("F".equals(nextNode.getNodeType())) {
            //如果是最后一个节点，则要结束整个流程 调用 endInstance
            this.endFlowInstance(flowInst, flowInfo, nextNode, transPath,
                nodeTran, preNodeInst.getNodeInstId(), options.getUserCode(), options.getUnitCode());
            if ("Y".equals(flowInst.getIsSubInst())) {
                //long otherSubFlows = flowInstanceDao.calcOtherSubflowSum(
                    //flowInst.getPareNodeInstId(), flowInst.getFlowInstId());
                // 这个应该始终为 0
                //if (otherSubFlows == 0) {// 其他所有子流程都关闭了，则提交父流程对应的节点
                return submitOptInside(SubmitOptOptions.create()
                        .copy(options).nodeInst(flowInst.getPareNodeInstId()),
                    varTrans, application, true, true);
                // }
            }
            return new ArrayList<>();
        } else {

            return submitToNextOptNode(
                nextNode, nodeToken, flowInst, flowInfo,
                preNodeInst, transPath, nodeTran, options,
                varTrans, application);
        }
    }

    private List<String> submitToNextOptNode(
        NodeInfo nextOptNode,  String nodeToken, FlowInstance flowInst, FlowInfo flowInfo,
        NodeInstance preNodeInst, String transPath, FlowTransition trans,
        FlowOptParamOptions options,
        FlowVariableTranslate varTrans,
        ServletContext application) throws WorkflowException {
        Date currentTime = new Date(System.currentTimeMillis());
        //long nextCode = nextOptNode.getNodeId();
//        long lastNodeInstId = nodeInstanceDao.getNextNodeInstId();
        String lastNodeInstId = UuidOpt.getUuidAsString32();
        NodeInstance nodeInst = FlowOptUtils.createNodeInst(options.getUnitCode(), options.getUserCode(),
            flowInst, preNodeInst, flowInfo, nextOptNode, trans);
        nodeInst.setNodeInstId(lastNodeInstId);
        if(preNodeInst!=null) {
            nodeInst.setPrevNodeInstId(preNodeInst.getNodeInstId());
        }
        if(trans!=null) {
            nodeInst.setTransPath(
                StringUtils.isBlank(transPath) ? String.valueOf(trans.getTransId()) :
                    transPath + "," + trans.getTransId());
        }
        nodeInst.setRunToken(nodeToken);
        //设置阶段进入时间 或者变更时间
        if(StringUtils.isNotBlank(nextOptNode.getStageCode())) {
            StageInstance stage = flowInst.getStageInstanceByCode(nextOptNode.getStageCode());
            if (stage != null) {
                if ("1".equals(stage.getStageBegin())) {
                    stage.setLastUpdateTime(DatetimeOpt.currentUtilDate());
                } else {
                    stage.setStageBegin("1");
                    stage.setBeginTime(DatetimeOpt.currentUtilDate());
                    stage.setLastUpdateTime(DatetimeOpt.currentUtilDate());
                }
            }
        }
        List<String> createNodes = new ArrayList<>();
        createNodes.add(nodeInst.getNodeInstId());
        LeftRightPair<Set<String>, Set<String>> unitAndUser = calcNodeUnitAndOpterators(flowInst, preNodeInst, nodeToken,
            nextOptNode, options, varTrans);
        Set<String> nodeUnits = unitAndUser.getLeft();
        Set<String> optUsers = unitAndUser.getRight();
        if(nodeUnits!=null && !nodeUnits.isEmpty()){
            nodeInst.setUnitCode(nodeUnits.iterator().next());
        }

        if(optUsers!=null && !optUsers.isEmpty()){
            nodeInst.setUserCode(optUsers.iterator().next());
        }

        // A: 唯一执行人 B: 抢先机制 C: 多人操作 D: 自动执行 E: 哑元（可用于嵌套汇聚） S:子流程
        if ("S".equals(nextOptNode.getOptType())) {
            //如果是子流程 启动流程
            nodeInst.setNodeState("W");
            String tempFlowTimeLimit = "";
            if ("T".equals(flowInst.getIsTimer())
                && flowInst.getTimeLimit() != null
                && "I".equals(nextOptNode.getIsAccountTime())) {
                //子流程实例计时可以继承父流程剩余时间
                long flowTime = flowInst.getTimeLimit();
                //天
                long day = flowTime / 60 / 8;
                //小时
                long hour = flowTime / 60 % 8;
                //分钟
                long minute = flowTime % 60;
                tempFlowTimeLimit = day + "d" + hour + "h" + minute + "m";
            }
            //子流程的机构 要和 节点的机构一致
            FlowInstance tempFlow = createInstanceInside(
                CreateFlowOptions.create().copy(options)
                    .flow(nextOptNode.getSubFlowCode())
                    .version(flowDefDao.getLastVersion(nextOptNode.getSubFlowCode()))
                    .optName(flowInst.getFlowOptName() + "--" + nextOptNode.getNodeName())
                    .optTag(flowInst.getFlowOptTag())
                    .parentFlow(nodeInst.getFlowInstId(), lastNodeInstId)
                    .timeLimit(tempFlowTimeLimit), varTrans, application, true);

            nodeInst.setSubFlowInstId(tempFlow.getFlowInstId());
            //子流程的时间限制和父流程节点的一致
            /*f (nextNodeInst.getTimeLimit() != null) {
                flowInst.setTimeLimit(nextNodeInst.getTimeLimit());
                flowInstanceDao.updateObject(flowInst);
            }*/
            NodeInstance tempFirstNode = tempFlow.getFirstNodeInstance();
            createNodes.add(tempFirstNode.getNodeInstId());
        } else {
            //计算人员的分配策略
            if ( (optUsers==null || optUsers.isEmpty())
                && !"D".equals(nextOptNode.getOptType()) && !"E".equals(nextOptNode.getOptType())) {
                logger.error("流程" + flowInst.getFlowInstId() + "的下一个节点:" + nextOptNode.getNodeName() + ",找不到权限为" + nextOptNode.getRoleCode() + "的操作人员");
                throw new WorkflowException(WorkflowException.NodeUserNotFound, "流程" + flowInst.getFlowInstId() + "的下一个节点:" + nextOptNode.getNodeName() + ",找不到权限为" + nextOptNode.getRoleCode() + "的操作人员");
            }

            if (SysUserFilterEngine.ROLE_TYPE_GW.equalsIgnoreCase(nextOptNode.getRoleType())) {/* &&
                    "A".equals(nextOptNode.getOptType())){*/
                nodeInst.setTaskAssigned("D");
            } else if ("A".equals(nextOptNode.getOptType())
                    || "C".equals(nextOptNode.getOptType())
                    || "B".equals(nextOptNode.getOptType())) {
               /* if(optUsers.size()==1){
                    nextNodeInst.setTaskAssigned("S");
                }else{*/
                if (optUsers.size() > 1) {
                    nodeInst.setTaskAssigned("T");
                    for (String uc : optUsers) {
                        ActionTask wfactTask = FlowOptUtils.createActionTask(nodeInst.getNodeInstId(), uc);
                        //wfactTask.setTaskId(actionTaskDao.getNextTaskId());
                        wfactTask.setAssignTime(currentTime);
                        actionTaskDao.saveNewObject(wfactTask);
                        nodeInst.addWfActionTask(wfactTask);
                    }
                } else {
                    nodeInst.setTaskAssigned("S");
                    //nodeInst.setUserCode(optUsers.iterator().next());
                }
                notificationCenter.sendMessage("system", optUsers,
                    NoticeMessage.create().operation("workflow").method("submit").subject("您有新任务")
                        .content("您有新任务:" + nextOptNode.getNodeName()));
            }
        }
        /**
         *  检查令牌冲突（自由流程，令牌的冲突有业务程序和流程图自己控制，无需检查）
         *  这段代码是检查令牌的一致性，多实例节点多次运行时会出错的，
         *  这个本来的目的是为了检查从分支中返回到 主干上，因为有游离节点的存在所以需要这个检查
         *  这个算法不是判断是否相等，而是应该判断层次是否一致，只要层次一致就没有问题，如果不一致就需要截断后面的层次
         */
        if (! "F".equals(flowInfo.getFlowClass())) {//自由流程，令牌的冲突有业务程序和流程图自己控制，无需检查
            //NodeInstance thisNodeInst = nodeInst;
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
                    if(sameNodes!=null && !sameNodes.isEmpty()) {
                        closeNodeInstanceInside(sameNodes, currentTime, options.getUserCode());
                    }
                }
            }
        }

        nodeInstanceDao.saveNewObject(nodeInst);
        flowInst.addNodeInstance(nodeInst);
        flowInst.setLastUpdateTime(currentTime);
        flowInst.setLastUpdateUser(options.getUserCode());

        //执行节点创建后 事件
        FlowOptPage optPage = flowOptPageDao.getObjectById(nextOptNode.getOptCode());
        //检查自动执行节点 并执行相关操作
        // 添加自动运行的处理结果
        if ("D".equals(nextOptNode.getOptType()) || "E".equals(nextOptNode.getOptType())
            || "A".equals(optPage.getPageType())) {
            boolean needSubmit = true;
            SubmitOptOptions autoSubmitOptions = SubmitOptOptions.create().copy(options).nodeInst(lastNodeInstId);
            if("A".equals(optPage.getPageType()) ||
                    //bean 事件
                  ("D".equals(nextOptNode.getOptType()) && "B".equals(nextOptNode.getOptCode()))) {
                NodeEventSupport nodeEventExecutor =
                    NodeEventSupportFactory.createNodeEventSupportBean(nextOptNode, optPage);
                needSubmit = nodeEventExecutor.runAutoOperator(flowInst, nodeInst,
                    nextOptNode, options.getUserCode());
            } else if("D".equals(nextOptNode.getOptType()) && "S".equals(nextOptNode.getOptCode())) {
                //添加脚本的运行
                Map<String, Object> objectMap = varTrans.calcScript(nextOptNode.getOptParam());
                for(Map.Entry<String, Object> ent : objectMap.entrySet()) {
                    if(! ent.getKey().startsWith("_") && ent.getValue() != null) {
                        saveFlowNodeVariable(flowInst.getFlowInstId(), nodeToken,
                            ent.getKey(), ent.getValue());
                    }
                }
                String lockUser = StringBaseOpt.castObjectToString(
                    objectMap.get("_lock_user"));
                if(StringUtils.isNotBlank(lockUser)) {
                    autoSubmitOptions.workUser(lockUser);
                }
            }
            //暂时先取第一个节点实例，解决部分问题
            //varTrans改为一个空的
            if(needSubmit) {
                List<String> nextNodes = this.submitOptInside(
                    autoSubmitOptions,
                    varTrans, application, false, true);
                createNodes.addAll(nextNodes);
            }
        } else if(StringUtils.isNotBlank(nextOptNode.getOptBean())) {
            NodeEventSupport nodeEventExecutor =
                NodeEventSupportFactory.createNodeEventSupportBean(nextOptNode, optPage);
            nodeEventExecutor.runAfterCreate(flowInst, nodeInst, nextOptNode, options.getUserCode());
        }

        return createNodes;
    }


    private NodeInfo selectNextNodeByNodeId(String nodeId) {
        List<FlowTransition> transList = flowTransitionDao.getNodeTrans(nodeId);
        if (transList == null || transList.size() < 1)
            return null;
        /*if(transList.size()>1){
            return null;
        }*/
        return flowNodeDao.getObjectById(transList.get(0).getEndNodeId());
    }

    private FlowTransition selectOptNodeTransition(NodeInfo currNode) throws WorkflowException {
        List<FlowTransition> transList = flowTransitionDao.getNodeTrans(currNode.getNodeId());
        if (transList == null || transList.size() < 1)
            return null;
        if (transList.size() > 1) {
            throw new WorkflowException(WorkflowException.FlowDefineError,
                "流程图绘制问题，业务节点流转路径不是有且唯一的一条："
                    + currNode.getFlowCode() + ":" + currNode.getVersion() + ":"
                    + currNode.getNodeId() + ":"
                    + currNode.getNodeCode() + ":" + currNode.getNodeName());
        }
        return transList.get(0);
    }

    private Set<FlowTransition> selectTransitions(NodeInfo currNode, FlowVariableTranslate varTrans) {
        List<FlowTransition> transList = flowTransitionDao.getNodeTrans(currNode.getNodeId());
        Set<FlowTransition> selTrans = new HashSet<>();
        if (transList == null || transList.size() < 1)
            return selTrans;

        String sRT = currNode.getRouterType();

        if ("H".equals(sRT) || "D".equals(sRT)) {
            VariableFormula formula = new VariableFormula();
            formula.setTrans(varTrans);
            formula.addExtendFunc(
                "rank",
                (a) -> {
                    UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext();
                    return context.getUserRank(StringBaseOpt.castObjectToString(a[0]));
                }
            );
            for (FlowTransition trans : transList) {
                if (BooleanBaseOpt.castObjectToBoolean(formula.calcFormula(trans.getTransCondition()))) {
                    //保存目标节点实例
                    selTrans.add(trans);
                    // D:分支节点 只能有一个出口
                    if ("D".equals(sRT))
                        break;
                }
            }
        } else
            selTrans.add(transList.get(0));

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
                                        ServletContext application) throws WorkflowException {

        return submitOptInside(options, varTrans, application,true, true);
    }

    /**
     * 返回下一步节点的节点实例ID
     * @param options 当前节点实例编号
     * @return  节点实例编号列表
     */
    @Override
    public List<String> submitOpt(SubmitOptOptions options) throws WorkflowException{
        return submitOpt(options,
            new ObjectUserUnitVariableTranslate(
                CollectionsOpt.unionTwoMap(
                    options.getVariables(), options.getGlobalVariables())),
            null);
    }

    private List<String> submitOptInside(SubmitOptOptions options,
            UserUnitVariableTranslate varTrans,
            ServletContext application, boolean saveOptions, boolean saveLog) throws WorkflowException {
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
        if (!"N,M".contains(flowInst.getInstState()) ||
            (!"N".equals(nodeInst.getNodeState())
                && !"W".equals(nodeInst.getNodeState()) // 等待子流程返回
            )
            ) {
            logger.error("流程节点状态不正确，流程：" + nodeInst.getFlowInstId() + " 状态：" + flowInst.getInstState() +
                "节点：" + options.getNodeInstId() + " 状态：" + nodeInst.getNodeState());
            throw new WorkflowException(WorkflowException.IncorrectNodeState,
                "流程节点状态不正确，流程：" + nodeInst.getFlowInstId() + " 状态：" + flowInst.getInstState() +
                    "节点：" + options.getNodeInstId() + " 状态：" + nodeInst.getNodeState());
        }

        String sGrantor;
        NodeInfo currNode = flowNodeDao.getObjectById(nodeInst.getNodeId());
        if (SysUserFilterEngine.ROLE_TYPE_GW.equalsIgnoreCase(currNode.getRoleType())) {
            //TODO 判断人员岗位吻合
            sGrantor = options.getUserCode();
        } else if (StringUtils.isBlank(options.getGrantorCode())
            || StringUtils.equals(options.getGrantorCode(), options.getUserCode())) {
            sGrantor = actionTaskDao.getTaskGrantor(options.getNodeInstId(), options.getUserCode());
            //哑元、自动执行以及子流程 不判断
            if (sGrantor == null && !"E".equals(currNode.getOptType())
                && !"D".equals(currNode.getOptType()) && !"S".equals(currNode.getOptType())) {
                logger.error("用户没有权限操作该节点：" + options.getUserCode() + " -- " + options.getNodeInstId());
                throw new WorkflowException(WorkflowException.WithoutPermission, "用户没有权限操作该节点：" + options.getUserCode() + " -- " + options.getNodeInstId());
            }
        } else {
            sGrantor = options.getGrantorCode();
            if (!"E".equals(currNode.getOptType()) && !"D".equals(currNode.getOptType())
                && !"S".equals(currNode.getOptType()) &&
                !actionTaskDao.hasOptPower(options.getNodeInstId(), options.getUserCode(), options.getGrantorCode())) {
                logger.error("用户没有权限操作该节点：" + options.getUserCode() + " -- " + options.getNodeInstId());
                throw new WorkflowException(WorkflowException.WithoutPermission, "用户没有权限操作该节点：" + options.getUserCode() + " -- " + options.getNodeInstId());
            }
        }

        if(saveOptions) {
            saveValueAndRoleInOptions(nodeInst.getFlowInstId(), nodeInst.getRunToken(), options);
        }

        Date updateTime = DatetimeOpt.currentUtilDate();
        nodeInst.setLastUpdateTime(updateTime);
        nodeInst.setLastUpdateUser(options.getUserCode());
        //创建节点提交日志 S:提交节点
        if(saveLog) {
            OperationLog wfactlog = FlowOptUtils.createActionLog(
                options.getUserCode(), nodeInst, "提交节点", currNode);

            if (sGrantor != null && !sGrantor.equals(options.getUserCode())) {
                nodeInst.setGrantor(sGrantor);
                wfactlog.setNewValue(wfactlog + " 授予 " + options.getUserCode()
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
        }
        flowInst.setLastUpdateTime(updateTime);
        flowInst.setLastUpdateUser(options.getUserCode());
        flowInstanceDao.updateObject(flowInst);

        List<String> nextNodeInsts = new ArrayList<>();


        if ("T".equals(nodeInst.getTaskAssigned())) {
            //多人操作节点 等待所有人都提交才可以提交
            /**这个应该会不需要了，暂时保留
             * 这样的需求应该会被 按人进行多实例划分，
             */
            int havnotSubmit = 0;
            for (ActionTask task : nodeInst.getWfActionTasks()) {
                if ("A".equals(task.getTaskState())) {
                    if (/*userCode*/sGrantor.equals(task.getUserCode())) {
                        //任务的完成时间在任务的活动日志中
                        task.setTaskState("C");
                    } else {
                        if ("C".equals(currNode.getOptType()))
                            havnotSubmit++;
                        //暂时取消这个任务作废的做法，防止在回退或者回收之后，操作人员与原本定义不符
//                        else//不是多人操作是抢先机制的，其他人任务作废。
//                            task.setIsValid("F");
                    }
                }
//                else {
//                    //其他无效任务作废，提高效率。
//                    task.setIsValid("F");
//                }
                //添加actionTask保存
                actionTaskDao.updateObject(task);
            }
            //判断是否是多人操作，如果是多人操作，最后一个人提交才正在提交
            //前面人提交只更改任务列表中的任务完成状态，多人操作一定要配合 流程活动任务单 工作
            //这个任务可以是业务填写也可以是权限引擎填写
            if ("C".equals(currNode.getOptType()) && havnotSubmit > 0) {
                nodeInstanceDao.updateObject(nodeInst);
                nextNodeInsts.clear();
                nextNodeInsts.add(options.getNodeInstId());
                return nextNodeInsts;
            }
        }
        /**
         * 节点提交前事件
         */
        NodeEventSupport nodeEventExecutor = NodeEventSupportFactory
            .createNodeEventSupportBean(currNode);
        nodeEventExecutor.runBeforeSubmit(flowInst, nodeInst, currNode, options.getUserCode());

        //判断是否为临时插入节点
        if (nodeInst.getRunToken().startsWith("L")) {
            //提交临时插入节点
            nodeInst.setNodeState("C");
            if (flowInst.checkNotCommitPreNodes(nodeInst.getPrevNodeInstId()) < 1) {
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
            if (nodeInst.getRunToken().startsWith("R") /*|| nodeInst.getRunToken().startsWith("L")*/) {
                //logger.info("游离节点:" + nodeInstId);
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
            /*WfNodeInstance*/
            nodeInst = nodeInstanceDao.getObjectWithReferences(options.getNodeInstId());
            if (!"N".equals(nodeInst.getNodeState()) && !"W".equals(nodeInst.getNodeState())) {
                logger.error("流程：" + nodeInst.getFlowInstId() + "节点：" + options.getNodeInstId() + " " + currNode.getNodeName() + " 已经被其他线程提交，请避免重复提交。");
                throw new WorkflowException(WorkflowException.IncorrectNodeState,
                    "流程：" + nodeInst.getFlowInstId() + "节点：" + options.getNodeInstId() + " " + currNode.getNodeName() + " 已经被其他线程提交，请避免重复提交。");

            }
            nodeInst.setNodeState("C");
            nodeInstanceDao.updateObject(nodeInst);
            //DatabaseOptUtils.flush(nodeInstanceDao.getCurrentSession());
        }
        //刷新 变量接口 里面的变量
        FlowVariableTranslate flowVarTrans = FlowOptUtils.createVariableTranslate(
            nodeInst, flowInst,flowVariableDao,this, options);
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
        for (NodeInstance ni : flowInst.getFlowNodeInstances())
            if (ni.getNodeInstId() != nodeInstId &&
                //活动状态的节点
                ("N".equals(ni.getNodeState()) || "S".equals(ni.getNodeState()) ||
                    "W".equals(ni.getNodeState()) || "P".equals(ni.getNodeState())) &&                     //相同父类令牌的节点
                preToken.equals(NodeInstance.calcSuperToken(ni.getRunToken()))
                ) {
                if ("W".equals(ni.getNodeState())) { //结束子流程
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
        // 添加令牌算法
        NodeInstance thisNodeInst = nodeInstanceDao.getObjectWithReferences(nodeInstId);
        if (thisNodeInst == null)
            return null;
        // 当前节点状态必需为正常
        if (!"N".equals(thisNodeInst.getNodeState()))
            return null;

        FlowInstance flowInst = flowInstanceDao.getObjectWithReferences(thisNodeInst
            .getFlowInstId());
        if (flowInst == null)
            return null;

        /*
         * WfNode nodedef = flowNodeDao.getObjectById( thisnode.getNodeId());
         * if("E".equals(nodedef.getNodetype())){ //本节点为汇聚节点不允许回退 return -5; }
         */
        // 查找上一个流经节点
        NodeInstance prevNodeInst = null;
        if (thisNodeInst.getPrevNodeInstId() != null) {
            prevNodeInst = nodeInstanceDao.getObjectWithReferences(thisNodeInst.getPrevNodeInstId());
        } else {
            prevNodeInst = flowInst.getPareNodeInst(nodeInstId);
        }
        //是否子流程退回父流程
        Boolean subProcess = false;
        // 查找上一个流经节点
        FlowInstance prevFlowInst = null;
        if (prevNodeInst == null) {
            //找不到上一节点之后，判断是否子流程
            if (flowInst.getPreNodeInstId() == null) {
                return null;
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
            if ("D".equals(nodedef.getOptType())
                || "E".equals(nodedef.getOptType())
                || "S".equals(nodedef.getOptType())) {
                pns.add(prevNodeInst);
                //优先通过节点表中的prenodeinstid来查找上一节点
                String currnetNodeInstId = prevNodeInst.getPrevNodeInstId();
                prevNodeInst = nodeInstanceDao.getObjectWithReferences(currnetNodeInstId);
                if (prevNodeInst == null) {
                    prevNodeInst = flowInst.getPareNodeInst(currnetNodeInstId);
                }
                if (prevNodeInst == null)
                    return null;
                //判断一下父流程中的节点是否已经被退回，已经被退回之后就不能再次退回
                if (prevFlowInst != null) {
                    for (NodeInstance no : prevFlowInst.getFlowNodeInstances()) {
                        //罗列处于正常、暂缓的节点
                        if ("N,S,P".contains(no.getNodeState())) {
                            //如果想要退回的节点处于正常、暂缓，无需退回
                            if (prevNodeInst.getNodeId().equals(no.getNodeId())) {
                                return null;
                            }
                        }
                    }
                }
                nodedef = flowNodeDao.getObjectById(prevNodeInst.getNodeId());
            } else
                break;
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
        // nextNodeInst.setTimeLimit(null);
        nextNodeInst.setNodeState("N");
        nextNodeInst.setTaskAssigned(prevNodeInst.getTaskAssigned());
        nextNodeInst.setLastUpdateUser(managerUserCode);
        nextNodeInst.setLastUpdateTime(updateTime);

        for (ActionTask task : prevNodeInst.getWfActionTasks()) {
                ActionTask newtask = FlowOptUtils.createActionTask(
                    nextNodeInst.getNodeInstId(), task.getUserCode());
                //newtask.setTaskId(actionTaskDao.getNextTaskId());
                // 要判断 过期时间的问题
                //nextNodeInst.addWfActionTask(newtask);
                nextNodeInst.setTimeLimit(null);
                nextNodeInst.setTaskAssigned("T");
                actionTaskDao.saveNewObject(newtask);
        }

        flowInst.addNodeInstance(nextNodeInst);
        nodeInstanceDao.mergeObject(thisNodeInst);
        nodeInstanceDao.mergeObject(nextNodeInst);
        flowInstanceDao.updateObject(flowInst);

        //执行节点创建后 事件
        NodeEventSupport nodeEventExecutor = NodeEventSupportFactory
            .createNodeEventSupportBean(nodedef);
        nodeEventExecutor.runAfterCreate(flowInst, nextNodeInst, nodedef, managerUserCode);
        //调用发送消息接口
        Set<String> nodeInstIds = new HashSet<>();
        nodeInstIds.add(lastNodeInstId);
        //FlowOptUtils.sendMsg(nodeInstId, nodeInstIds, mangerUserCode);
        return lastNodeInstId;
    }

    /**
     * 如果后续节点是 自动运行 和哑元 节点，节点被操作的判断将会误判
     */
    public boolean nodeCanBeReclaim(String nodeInstId) {
        NodeInstance thisnode = nodeInstanceDao.getObjectById(nodeInstId);
        if (thisnode == null)
            return false;
        FlowInstance flow = flowInstanceDao.getObjectWithReferences(thisnode
            .getFlowInstId());
        if (flow == null)
            return false;
        // 流程状态被更改也算被操作了
        if (!"N".equals(flow.getInstState()))
            return false;
        int nns = 0;
        for (NodeInstance nextNode : flow.getFlowNodeInstances()) {
            if (thisnode.getNodeInstId().equals(nextNode.getPrevNodeInstId())) {
                nns++;
                if (!"N".equals(nextNode.getNodeState())) // ||
                    // nextNode.getWfActionLogs().size()>0)
                    return false;
            }
        }
        return nns > 0;
    }


    private Set<NodeInfo> viewRouterNextNodeInside(NodeInfo currNode, FlowVariableTranslate varTrans) {
        Set<NodeInfo> nextNodes = new HashSet<NodeInfo>();
        Set<FlowTransition> trans = selectTransitions(currNode, varTrans);

        for (FlowTransition tran : trans) {
            NodeInfo tempNode = flowNodeDao.getObjectById(tran.getEndNodeId());
            if ("C".equals(tempNode.getNodeType()))
                nextNodes.add(tempNode);
            else if ("R".equals(tempNode.getNodeType())) {
                nextNodes.addAll(
                    viewRouterNextNodeInside(tempNode, varTrans));
            }
        }
        return nextNodes;
    }

    @Override
    public Set<NodeInfo> viewNextNode(String nodeInstId, String userCode,
                                      String unitCode, UserUnitVariableTranslate varTrans) {
        //根据上级节点实例编号获取节点所在父流程实例信息
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null) {
            logger.error("找不到节点实例：" + nodeInstId);
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
        if ("C".equals(nextNode.getNodeType())) {
            nextNodes.add(nextNode);
        } else if ("R".equals(nextNode.getNodeType())) {
            FlowVariableTranslate flowVarTrans = FlowOptUtils.createVariableTranslate(
                nodeInst, flowInst,flowVariableDao,this,null);
            // 分支节点的条件
            flowVarTrans.setFlowVarTrans(varTrans);
            nextNodes = viewRouterNextNodeInside(nextNode, flowVarTrans);
        }
        return nextNodes;
    }

    @Override
    public Set<String> viewNextNodeOperator(String nextNodeId,
                                            String curNodeInstId,
                                            String userCode, String unitCode,
                                            UserUnitVariableTranslate varTrans) {

        NodeInstance nodeInst = nodeInstanceDao.getObjectById(curNodeInstId);
        if (nodeInst == null) {
            logger.error("找不到节点实例：" + curNodeInstId);
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
        if (!"S".equals(nextNode.getOptType())) {
            FlowVariableTranslate flowVarTrans = FlowOptUtils.createVariableTranslate(
                nodeInst, flowInst, flowVariableDao,this,null);
            flowVarTrans.setFlowVarTrans(varTrans);

            LeftRightPair<Set<String>, Set<String>> unitAndUser =
            calcNodeUnitAndOpterators(flowInst, nodeInst, nodeInst.getRunToken(),
                nextNode, SubmitOptOptions.create().user(userCode).unit(unitCode),
                flowVarTrans);

            return unitAndUser.getRight();
        }

        return new HashSet<>();
    }

    @Override
    public String getTaskGrantor(String nodeInstId, String userCode) {
        return actionTaskDao.getTaskGrantor(nodeInstId, userCode);
    }

     /** 加签,并指定到人
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

        if (nodeList == null || nodeList.size() < 1)
            return null;
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
        if (nodeInst.getRunToken().startsWith("R")) {
            logger.error("游离节点不能创建前置节点：" + curNodeInstId + " token:" + nodeInst.getRunToken() + "。");
            return null;
        }

        FlowInfo flowInfo = flowDefDao.getFlowDefineByID(flowInst.getFlowCode(), flowInst.getVersion());

        NodeInfo nextNode = flowNodeDao.getObjectById(nodeId);
        //获取上一个相同节点实例机构

        String lastNodeInstId = UuidOpt.getUuidAsString32();
//        long lastNodeInstId = nodeInstanceDao.getNextNodeInstId();

        NodeInstance nextNodeInst = FlowOptUtils.createNodeInst(unitCode, userCode, flowInst, nodeInst, flowInfo, nextNode, null);

        nextNodeInst.setNodeInstId(lastNodeInstId);
        nextNodeInst.setPrevNodeInstId(curNodeInstId);
        nextNodeInst.setRunToken("L" + nodeInst.getRunToken());//设置为插入前置节点
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
        if (flowInst == null)
            return null;
        List<NodeInfo> nodeList = flowNodeDao.listNodeByNodecode(flowInst.getFlowCode(),
            flowInst.getVersion(), nodeCode);

        if (nodeList == null || nodeList.size() < 1)
            return null;
        if (nodeList.size() > 1)
            logger.error("流程 " + flowInst.getFlowCode() + "（版本号" + flowInst.getVersion()
                + "）中对应环节代码为" + nodeCode + "的节点有多个，系统随机的创建一个，如有问题请和管理人员联系。");

        NodeInstance nodeInst = nodeInstanceDao.getObjectById(curNodeInstId);
        //必需存在且状态为正常 或者 暂停
        if (nodeInst == null
               /* || (!"N".equals(nodeInst.getNodeState())
                        && !"S".equals(nodeInst.getNodeState()) )*/) {
            logger.error("找不到节点实例：" + curNodeInstId);
            return null;
        }
        String nodeId = nodeList.get(0).getNodeId();

        FlowInfo flowInfo = flowDefDao.getFlowDefineByID(flowInst.getFlowCode(), flowInst.getVersion());

        NodeInfo nextNode = flowNodeDao.getObjectById(nodeId);
        //获取上一个相同节点实例机构

        String lastNodeInstId = UuidOpt.getUuidAsString32();
//        String lastNodeInstId = nodeInstanceDao.getNextNodeInstId();

        NodeInstance nextNodeInst = FlowOptUtils.createNodeInst(unitCode, createUser, flowInst, nodeInst, flowInfo, nextNode, null);

        nextNodeInst.setNodeInstId(lastNodeInstId);
        nextNodeInst.setPrevNodeInstId(curNodeInstId);
        nextNodeInst.setRunToken("RT" + nodeInst.getRunToken());//设置为游离节点
        nextNodeInst.setUserCode(userCode);
        nextNodeInst.setTaskAssigned("S");
        nextNodeInst.setTransPath("");

        nodeInstanceDao.saveNewObject(nextNodeInst);

        return nextNodeInst;
    }



    @Override
    public void assignFlowWorkTeam(String flowInstId, String roleCode, String userCode) {
        Date assignDate = new Date(System.currentTimeMillis());
        flowTeamDao.mergeObject(new FlowWorkTeam(flowInstId, userCode, roleCode, assignDate));
    }

    @Override
    public void assignFlowWorkTeam(String flowInstId, String roleCode,
                                   List<String> userCodeSet) {
        Date assignDate = new Date(System.currentTimeMillis());
        if (userCodeSet != null)
            for (String usercode : userCodeSet)
                if (StringUtils.isNotBlank(usercode)) {
                    flowTeamDao.mergeObject(new FlowWorkTeam(flowInstId, usercode, roleCode, assignDate));
                }

    }

    @Override
    public void assignFlowWorkTeam(String flowInstId, String roleCode, String userCode, String authdesc) {
        Date assignDate = new Date(System.currentTimeMillis());
        FlowWorkTeam team = new FlowWorkTeam(flowInstId, userCode, roleCode, assignDate);
        team.setAuthDesc(authdesc);
        flowTeamDao.mergeObject(team);
    }

    @Override
    public void assignFlowWorkTeam(String flowInstId, String roleCode,
                                   List<String> userCodeSet, String authdesc) {
        Date assignDate = new Date(System.currentTimeMillis());
        if (userCodeSet != null) {
            for (String usercode : userCodeSet) {
                if (usercode != null && !"".equals(usercode)) {
                    FlowWorkTeam team = new FlowWorkTeam(flowInstId, usercode, roleCode, assignDate);
                    team.setAuthDesc(authdesc);
                    flowTeamDao.mergeObject(team);
                }
            }
        }
    }

    public void deleteFlowWorkTeam(String flowInstId, String roleCode, String userCode) {
        flowTeamDao.deleteObjectById(new FlowWorkTeamId(flowInstId, userCode, roleCode));
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
                if (us == null)
                    us = new ArrayList<>();
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

    @Transactional
    public List<FlowWorkTeam> viewFlowWorkTeamList(String flowInstId, String roleCode) {
        List<FlowWorkTeam> users = flowTeamDao.listFlowWorkTeamByRole(flowInstId, roleCode);
        return users;
    }

    public List<FlowWorkTeam> viewFlowWorkTeamList(String flowInstId, String roleCode, String authdesc) {
        return flowTeamDao.listFlowWorkTeam(flowInstId, roleCode, authdesc);
    }

    public FlowVariable viewNodeVariable(String flowInstId, String runToken,
                                         String varname) {
        FlowVariableId id = new FlowVariableId(flowInstId, runToken,
            varname);
        return flowVariableDao.getObjectById(id);
    }


    @Override
    public void assignFlowOrganize(String flowInstId, String roleCode,
                                   String unitCode) {
        Date assignDate = new Date(System.currentTimeMillis());
        FlowOrganize dbObj = flowOrganizeDao.getObjectById(new FlowOrganizeId(flowInstId, unitCode, roleCode));
        if (dbObj == null || StringBaseOpt.isNvl(dbObj.getUnitCode()))
            flowOrganizeDao.mergeObject(new FlowOrganize(flowInstId, unitCode, roleCode, assignDate));

    }

    @Override
    public void assignFlowOrganize(String flowInstId, String roleCode,
                                   List<String> unitCodeSet) {
        Date assignDate = new Date(System.currentTimeMillis());
        if (unitCodeSet != null)
            for (String unitCode : unitCodeSet)
                if (unitCode != null && !"".equals(unitCode)) {
                    FlowOrganize dbObj = flowOrganizeDao.getObjectById(new FlowOrganizeId(flowInstId, unitCode, roleCode));
                    if (dbObj == null || StringBaseOpt.isNvl(dbObj.getUnitCode()))
                        flowOrganizeDao.mergeObject(new FlowOrganize(flowInstId, unitCode, roleCode, assignDate));
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
        if (unitCodeSet != null)
            for (String unitCode : unitCodeSet)
                if (unitCode != null && !"".equals(unitCode)) {
                    FlowOrganize dbObj = flowOrganizeDao.getObjectById(new FlowOrganizeId(flowInstId, unitCode, roleCode));
                    if (dbObj == null || StringBaseOpt.isNvl(dbObj.getUnitCode())) {
                        FlowOrganize orgObj = new FlowOrganize(flowInstId, unitCode, roleCode, assignDate);
                        orgObj.setAuthDesc(authDesc);
                        flowOrganizeDao.mergeObject(orgObj);
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
                if (us == null)
                    us = new ArrayList<>();
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

    @Override
    public void saveFlowVariable(String flowInstId, String sVar, Object sValue) {
        String objStr = StringBaseOpt.objectToString(sValue);
        if (StringUtils.isBlank(objStr)) {
            flowVariableDao.deleteObjectById(new FlowVariableId(flowInstId, "A", sVar));
        } else {
            String varType = sValue.getClass().isArray() || sValue instanceof Collection?"E":"S";
            FlowVariableId cid = new FlowVariableId(flowInstId, "A", sVar);
            FlowVariable varO = flowVariableDao.getObjectById(cid);
            if (varO == null) {
                varO = new FlowVariable(flowInstId, "A", sVar, objStr, varType);
                flowVariableDao.saveNewObject(varO);
            } else {
                varO.setVarType(varType);
                varO.setVarValue(objStr);
                flowVariableDao.updateObject(varO);
            }
        }
    }

    /**
     * 设置流程节点上下文变量
     *
     * @param flowInstId 流程实例id
     * @param runToken 令牌
     * @param sVar 流程变量
     * @param sValue 流程值
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
        String varType = sValue.getClass().isArray() || sValue instanceof Collection?"E":"S";
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
    public void saveFlowNodeVariable(String nodeInstId, String sVar, Set<String> sValues) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null) {
            logger.error("找不到节点实例：" + nodeInstId);
            return;
        }
        if (sValues == null || sValues.size() == 0) {
            flowVariableDao.deleteObjectById(new FlowVariableId(nodeInst.getFlowInstId(),
                nodeInst.getRunToken(), sVar));
            return;
        }
        FlowVariable varO = new FlowVariable(nodeInst.getFlowInstId(),
            nodeInst.getRunToken(), sVar, FlowVariable.stringsetToString(sValues), "E");
        flowVariableDao.mergeObject(varO);
    }

    @Override
    public List<FlowVariable> listFlowVariables(String flowInstId) {
        List<FlowVariable> lv = flowVariableDao.listFlowVariables(flowInstId);
        if (lv == null)
            lv = new ArrayList<>();
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
                if (StringUtils.isBlank(flowOptName) || !(flowOptName.indexOf(optName) > -1)) {
                    iterator.remove();
                    continue;
                }
            }
        }
        return flowInstanceList;
    }

    public List<FlowVariable> viewFlowVariablesByVarname(String flowInstId,
                                                         String varname) {
        return flowVariableDao.viewFlowVariablesByVarname(
            flowInstId, varname);
    }

    @Override
    public List<UserTask> listUserTasks(String userCode, PageDesc pageDesc) {
        List<UserTask> taskList = actionTaskDao.listUserTaskByFilter(
            CollectionsOpt.createHashMap("userCode", userCode), pageDesc);
        return taskList;
    }

    @Override
    public List<UserTask> listUserTasksByFilter(Map<String, Object> filterMap, PageDesc pageDesc) {
        List<UserTask> taskList = actionTaskDao.listUserTaskByFilter(filterMap, pageDesc);
        return taskList;
    }


    @Override
    public List<UserTask> listUserTasksByFlowCode(String userCode,
                                                  String flowCode, PageDesc pageDesc) {
        List<UserTask> taskList = actionTaskDao.listUserTaskByFilter(
            QueryUtils.createSqlParamsMap("userCode", userCode, "flowCode", flowCode), pageDesc);
        return taskList;
    }


    @Override
    public List<UserTask> listUserTasksByFlowStage(String userCode,
                                                   String flowStage, PageDesc pageDesc) {

        List<UserTask> taskList = actionTaskDao.listUserTaskByFilter(
            QueryUtils.createSqlParamsMap("userCode", userCode, "stageCode", flowStage), pageDesc);
        return taskList;
    }

    @Override
    public List<UserTask> listUserTasksByNodeCode(String userCode,
                                                  String nodeCode, PageDesc pageDesc) {
        return actionTaskDao.listUserTaskByFilter(
            QueryUtils.createSqlParamsMap("userCode", userCode, "nodeCode", nodeCode), pageDesc);
    }

    /**
     * 获取节点的所有操作人员
     * @param nodeInstId 节点实例id
     * @return 操作人员
     */
    @Override
    public List<UserTask> listNodeOperator(String nodeInstId){
        Map<String, Object> searchColumn = new HashMap<>();
        searchColumn.put("nodeInstId", nodeInstId);
        return this.listUserTasksByFilter(searchColumn, new PageDesc(-1, -1));
    }

    @Override
    public List<UserTask> listDynamicTask(Map<String, Object> searchColumn, PageDesc pageDesc) {
        List<UserTask> taskList = new ArrayList<>();
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext();
        //动态任务
        //1.找到用户所有机构下的岗位和职务
        List<? extends IUserUnit> iUserUnits =context.listUserUnits((String) searchColumn.get("userCode"));

        //2.以机构，岗位，职务来查询任务
        if (iUserUnits == null || iUserUnits.size() == 0) {
            return taskList;
        }
        for (IUserUnit i : iUserUnits) {
            searchColumn.put("unitCode", i.getUnitCode());
            searchColumn.put("userStation", i.getUserStation());
            List<UserTask> dynamicTask = actionTaskDao.queryDynamicTask(searchColumn, pageDesc);
            if (dynamicTask!=null)
              taskList.addAll(dynamicTask);
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
            if(StringUtils.isNotBlank(unitCode)){
                if(!unitCode.equals(i.getUnitCode())){
                    continue;
                }
            }
            if (!StringUtils.equals(station, i.getUserStation())){
                continue;
            }
            searchColumn.put("nodeInstId", nodeInstId);
            searchColumn.put("unitCode", i.getUnitCode());
            searchColumn.put("userStation", i.getUserStation());
            List<UserTask> dynamicTask = actionTaskDao.queryDynamicTask(searchColumn, pageDesc);
            if (dynamicTask!=null) {
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
        List<UserTask> staticTaskList = actionTaskDao.queryStaticTask((String) searchColumn.get("userCode"));
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
                if ("T".equals(iUserUnit.getIsPrimary())) {
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
        if (userCode == null)
            return false;
        return actionTaskDao.hasOptPower(nodeInstId, userCode, null);
    }

    @Override
    public List<UserTask> listUserCompleteTasks(Map<String, Object> filterMap, PageDesc pageDesc) {
        List<UserTask> taskList = actionTaskDao.listUserTaskFinByFilter(filterMap, pageDesc);
        return taskList;
    }


    @Override
    public List<FlowWarning> listFlowWarning(Map<String, Object> filterMap,
                                             PageDesc pageDesc) {
        return new ArrayList<FlowWarning>(
            runtimeWarningDao.listObjectsByProperties(filterMap, pageDesc));
    }

    @Override
    public List<FlowWarning> listFlowWarningByInst(String flowInstId,
                                                   PageDesc pageDesc) {
        return new ArrayList<FlowWarning>(
            runtimeWarningDao.listFlowWarningByInst(flowInstId, pageDesc));
    }

    @Override
    public List<FlowWarning> listFlowWarningByNodeInst(String nodeInstId,
                                                       PageDesc pageDesc) {
        return new ArrayList<FlowWarning>(
            runtimeWarningDao.listFlowWarningByNodeInst(nodeInstId, pageDesc));
    }

    @Override
    public List<FlowWarning> listFlowWarningByWarningCode(String warningCode,
                                                          PageDesc pageDesc) {
        return new ArrayList<FlowWarning>(
            runtimeWarningDao.listFlowWarningByWarningCode(warningCode, pageDesc));
    }

    //外部传进来的变量可能不是FlowVariableTranslate类型的，强转可能会失败
    private void flushVariables(FlowVariableTranslate varTrans, List<FlowVariable> flowVariables,
                                Map<String, List<String>> flowOrganizes, Map<String, List<String>> flowWorkTeam) {
        if (varTrans != null) {
            varTrans.setFlowVariables(flowVariables);
            varTrans.setFlowOrganizes(flowOrganizes);
            varTrans.setFlowWorkTeam(flowWorkTeam);
            flushVariables((FlowVariableTranslate) varTrans.getFlowVarTrans(), flowVariables, flowOrganizes, flowWorkTeam);
        } else {
            return;
        }
    }

    public void deleteFlowVariable(String flowInstId, String runToken, String varName) {
        if (flowInstId == null)
            return;
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
    public List<FlowInstanceGroup> listFlowInstGroup(Map<String, Object> filterMap, PageDesc pageDesc){
        return flowInstanceGroupDao.listObjects(filterMap, pageDesc);
    }
}
