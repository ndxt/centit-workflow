package com.centit.workflow.service.impl;

import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.components.SysUserFilterEngine;
import com.centit.framework.components.UserUnitFilterCalcContext;
import com.centit.framework.components.UserUnitParamBuilder;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.framework.model.adapter.UserUnitVariableTranslate;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.algorithm.StringRegularOpt;
import com.centit.support.compiler.VariableFormula;
import com.centit.support.database.utils.QueryUtils;
import com.centit.workflow.commons.NewFlowInstanceOptions;
import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.commons.WorkflowException;
import com.centit.workflow.dao.*;
import com.centit.workflow.po.*;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.FlowManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class FlowEngineImpl implements FlowEngine, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(FlowEngineImpl.class);
    @Resource
    private FlowInstanceDao flowInstanceDao;

    @Resource
    private UserUnitFilterCalcContext userUnitFilterCalcContext;

    @Resource
    private NodeInstanceDao nodeInstanceDao;
    @Resource
    private NodeInfoDao flowNodeDao;
    @Resource
    private FlowTransitionDao flowTransitionDao;
    @Resource
    private ActionTaskDao actionTaskDao;
    @Resource
    private ActionLogDao actionLogDao;
    @Resource
    private FlowInfoDao flowDefDao;
    @Resource
    private FlowManager flowManager;
    @Resource
    private FlowWorkTeamDao flowTeamDao;

    @Resource
    private FlowOrganizeDao flowOrganizeDao;
    @Resource
    private FlowVariableDao flowVariableDao;
    @Resource
    private InstAttentionDao attentionDao;
    @Resource
    private FlowWarningDao runtimeWarningDao;

    private final static Object lockObject = new Object();

    public FlowEngineImpl() {
        //lockObject = new Object();
    }

    /**
     * 创建一个新的流程
     * 同时创建第一个节点，并在活动日志中记录创建活动
     * 判断创建权限可以在应用系统中做，如果在流程引擎中做就用开始节点的权限代替
     */

    @Override
    public FlowInstance createInstance(String flowCode, String flowOptName, String flowOptTag, String userCode, String unitCode) {
        return createInstInside(flowCode, flowDefDao.getLastVersion(flowCode), flowOptName, flowOptTag, userCode,
            unitCode, 0, 0, null, false,null);
    }

    @Override
    public FlowInstance createInstanceWithDefaultVersion(NewFlowInstanceOptions newFlowInstanceOptions) {
        FlowInstance flowInstance = createInstInside(newFlowInstanceOptions.getFlowCode(),
            flowDefDao.getLastVersion(newFlowInstanceOptions.getFlowCode()),
            newFlowInstanceOptions.getFlowOptName(), newFlowInstanceOptions.getFlowOptTag(),
            newFlowInstanceOptions.getUserCode(), newFlowInstanceOptions.getUnitCode(),
            newFlowInstanceOptions.getNodeInstId(), newFlowInstanceOptions.getFlowInstid(),
            null, newFlowInstanceOptions.isLockFirstOpt(),newFlowInstanceOptions.getTimeLimitStr());
        Set<Long> nodes = new HashSet<>();
        for (NodeInstance n : flowInstance.getFlowNodeInstances()) {
            nodes.add(n.getNodeInstId());
        }
        FlowOptUtils.sendMsg(0, nodes, newFlowInstanceOptions.getUserCode());
        return flowInstance;
    }

    @Override
    public FlowInstance createInstance(String flowCode, long version, String flowOptName, String flowOptTag,
                                       String userCode, String unitCode) {
        return createInstInside(flowCode, version, flowOptName, flowOptTag, userCode,
            unitCode, 0, 0, null, false,null);
    }

    @Override
    public FlowInstance createInstanceWithSpecifiedVersion(NewFlowInstanceOptions newFlowInstanceOptions) {
        return createInstInside(newFlowInstanceOptions.getFlowCode(), newFlowInstanceOptions.getVersion(),
            newFlowInstanceOptions.getFlowOptName(), newFlowInstanceOptions.getFlowOptTag(),
            newFlowInstanceOptions.getUserCode(), newFlowInstanceOptions.getUnitCode(),
            newFlowInstanceOptions.getNodeInstId(), newFlowInstanceOptions.getFlowInstid(),
            null, newFlowInstanceOptions.isLockFirstOpt(),null);
    }

    @Override
    public FlowInstance createInstanceLockFirstNode(String flowCode, String flowOptName,
                                                    String flowOptTag, String userCode, String unitCode) {

        return createInstInside(flowCode, flowDefDao.getLastVersion(flowCode), flowOptName, flowOptTag, userCode,
            unitCode, 0, 0, null, true,null);
    }


    @Override
    public FlowInstance createInstanceLockFirstNode(String flowCode, long version,
                                                    String flowOptName, String flowOptTag, String userCode, String unitCode) {
        return createInstInside(flowCode, version, flowOptName, flowOptTag, userCode,
            unitCode, 0, 0, null, true,null);
    }


    /**
     * 创建流程实例  返回流程实例
     *
     * @param flowCode    流程编码
     * @param flowOptName 这个名称用户 查找流程信息，用来显示业务办件名称，
     * @param flowOptTag  这个标记用户 查找流程信息，比如办件代码，由业务系统自己解释可以用于反向关联
     * @param userCode    创建用户
     * @param unitCode    将流程指定一个所属机构
     * @param varTrans    变量转换接口，用于表达式计算，可以为null
     * @param application 容器句柄，用于自动执行节点，一般首节点不会为自动执行节点，可以为null
     * @return
     */
    @Override
    public FlowInstance createInstance(String flowCode, String flowOptName,
                                       String flowOptTag, String userCode, String unitCode,
                                       UserUnitVariableTranslate varTrans, ServletContext application) {

        return createInstInside(flowCode, flowDefDao.getLastVersion(flowCode), flowOptName, flowOptTag, userCode,
            unitCode, 0, 0, varTrans, false,null);

    }

    @Override
    public FlowInstance createInstance(String flowCode, long version, String flowOptName,
                                       String flowOptTag, String userCode, String unitCode,
                                       UserUnitVariableTranslate varTrans, ServletContext application) {

        return createInstInside(flowCode, version, flowOptName, flowOptTag, userCode,
            unitCode, 0, 0, varTrans, false,null);
    }


    /**
     * 创建流程实例或子流程实例
     *
     * @param flowCode   流程编码
     * @param nodeInstId 节点实例编号 ,节点编号不为0表示为子流程
     * @param userCode   用户编码
     * @param unitCode   机构编码
     * @return
     */
    private FlowInstance createInstInside(String flowCode, long version, String flowOptName, String flowOptTag, String userCode,
                                          String unitCode, long nodeInstId, long flowInstid, UserUnitVariableTranslate varTrans,
                                          boolean lockFirstOpt,String timeLimitStr) {

        Date createTime = new Date(System.currentTimeMillis());

        //获取流程信息
        FlowInfo wf = flowDefDao.getFlowDefineByID(flowCode, version);

        //获取流程实例编号
        Long flowInstId = flowInstanceDao.getNextFlowInstId();//update by ljy
        FlowInstance flowInst = FlowOptUtils.createFlowInst(unitCode, userCode, wf, flowInstId,timeLimitStr);
//        flowInst.setFlowInstId(flowInstanceDao.getNextFlowInstId());
        flowInst.setCreateTime(createTime);

        //节点实例编号不为空，为子流程，创建子流程时要给父节点的状态设置为 W：等待子流程返回
        if (nodeInstId != 0) {
            flowInst.setPreNodeInstId(nodeInstId);
            flowInst.setPreInstId(flowInstid);
            flowInst.setIsSubInst("Y");
        }
        flowInst.setFlowOptName(flowOptName);
        flowInst.setFlowOptTag(flowOptTag);
        //生成首节点实例编号
        NodeInfo node = wf.getFirstNode();
        if (node == null)
            return null;

        NodeInstance nodeInst = FlowOptUtils.createNodeInst(unitCode, userCode, null, flowInst, null, wf, node, null);
        //添加令牌算法 首节点的令牌为初始值 系统默认值
        // nodeInst.setRunToken("T");
        //同步创建时间
        nodeInst.setNodeInstId(nodeInstanceDao.getNextNodeInstId());
        nodeInst.setCreateTime(createTime);
        flowInst.addWfNodeInstance(nodeInst);
        //创建节点操作日志 W:创建首节点
        ActionLog wfactlog = FlowOptUtils.createActionLog("W", userCode, nodeInst, node);
        wfactlog.setActionId(actionLogDao.getNextActionId());
        wfactlog.setActionTime(createTime);
        nodeInst.addWfActionLog(wfactlog);

        UserUnitVariableTranslate flowVarTrans = new FlowVariableTranslate(varTrans, null, nodeInst, flowInst);

        Map<String, Set<String>> unitParams = UserUnitParamBuilder.createEmptyParamMap();
        UserUnitParamBuilder.addParamToParamMap(unitParams, "U",
            unitCode == null ? CodeRepositoryUtil.getUserInfoByCode(userCode).getPrimaryUnit() : unitCode);
        UserUnitParamBuilder.addParamToParamMap(unitParams, "P", unitCode);
        UserUnitParamBuilder.addParamToParamMap(unitParams, "F", flowInst.getUnitCode());
        //计算节点机构
        String nextNodeUnit = UserUnitCalcEngine.calcSingleUnitByExp(userUnitFilterCalcContext,
            node.getUnitExp(),
            unitParams, flowVarTrans);
        nodeInst.setUnitCode(nextNodeUnit);
        //如果锁定首节点只能有本人操作，则要在任务表中添加一条记录
        if (lockFirstOpt) {
            nodeInst.setUserCode(userCode);
            nodeInst.setTaskAssigned("S");
        } else {
            Set<String> optUsers = null;
            if (SysUserFilterEngine.ROLE_TYPE_ENGINE.equalsIgnoreCase(node.getRoleType())) {
                //如果节点的角色类别为 权限引擎则要调用权限引擎来分配角色
                //根据权限表达式创建任务列表
                UserUnitParamBuilder.addParamToParamMap(unitParams, "N", nextNodeUnit);

                Map<String, Set<String>> userParams = UserUnitParamBuilder.createEmptyParamMap();
                UserUnitParamBuilder.addParamToParamMap(userParams, "C", flowInst.getUserCode());
                UserUnitParamBuilder.addParamToParamMap(userParams, "O", userCode);
                optUsers = UserUnitCalcEngine.calcOperators(userUnitFilterCalcContext, node.getPowerExp(),
                    unitParams, userParams, null, flowVarTrans);

                if (optUsers == null || optUsers.size() == 0)
                    logger.error("权限引擎没有识别出符合表达式的操作人员！");
            } else if ("bj".equalsIgnoreCase(node.getRoleType())) {
                optUsers = new HashSet<String>();
                List<FlowWorkTeam> users = flowTeamDao.listFlowWorkTeamByRole(nodeInst.getFlowInstId(), node.getRoleCode());
                for (FlowWorkTeam u : users) {
                    optUsers.add(u.getUserCode());
                }
            } else/*gw xz*/ {
                optUsers = SysUserFilterEngine.getUsersByRoleAndUnit(userUnitFilterCalcContext,
                    node.getRoleType(), node.getRoleCode(), nextNodeUnit);
            }

            //计算人员的分配策略
            nodeInst.setTaskAssigned("S");

            if (optUsers != null && optUsers.size() > 0)
                nodeInst.setUserCode(optUsers.iterator().next());

            if (SysUserFilterEngine.ROLE_TYPE_GW.equalsIgnoreCase(nodeInst.getRoleType())) {/* &&
                    "A".equals(nextOptNode.getOptType())){*/
                nodeInst.setTaskAssigned("D");
            } else if (optUsers != null && optUsers.size() > 0 &&
                (SysUserFilterEngine.ROLE_TYPE_ENGINE.equalsIgnoreCase(node.getRoleType())
                    || SysUserFilterEngine.ROLE_TYPE_ITEM.equalsIgnoreCase(node.getRoleType())
                            /*行政角色按道理是不能有多个人可以同时做的*/
                    || SysUserFilterEngine.ROLE_TYPE_XZ.equalsIgnoreCase(node.getRoleType())
                    || "C".equals(node.getOptType()))) {
                if (optUsers.size() == 1) {
                    nodeInst.setTaskAssigned("S");
                } else {
                    nodeInst.setTaskAssigned("T");
                    for (String uc : optUsers) {
                        ActionTask wfactTask = FlowOptUtils.createActionTask(uc, nodeInst, node);
                        wfactTask.setTaskId(actionTaskDao.getNextTaskId());
                        wfactTask.setAssignTime(createTime);
                        actionTaskDao.saveNewObject(wfactTask);
                        //nodeInst.addWfActionTask(wfactTask);
                    }
                }
            }
        }

        //flowInst.addWfNodeInstance(nodeInst);
        //用AbstractTransactionalDataSourceSpringContextTests测试必需分别保存
        //nodeInstanceDao.saveNewObject(nodeInst);
        flowInstanceDao.saveNewObject(flowInst);
        flowInstanceDao.saveObjectReferences(flowInst);

        //执行节点创建后bean事件
        NodeEventSupport nodeEventExecutor = NodeEventSupportFactory.getNodeEventSupportBean(node);
        nodeEventExecutor.runAfterCreate(flowInst, nodeInst, node, userCode);
        //如果首节点是哑元 或者自动执行，请运行自动提交

        //自动执行
        if ("D".equals(node.getOptType())) {
            boolean needSubmit = nodeEventExecutor.runAutoOperator(flowInst, nodeInst,
                node, userCode);
            if (needSubmit)
                this.submitOpt(nodeInst.getNodeInstId(), userCode, unitCode, varTrans, null);

        } else if ("E".equals(node.getOptType())) {  //哑元节点 自动提交
            try {
                this.submitOpt(nodeInst.getNodeInstId(), userCode, unitCode, varTrans, null);
            } catch (WorkflowException e) {
                logger.error("自动提交哑元节点 " + nodeInst.getNodeInstId() + "后提交出错 。" + e.getMessage());
                throw e;
            }
        }

        return flowInst;
    }

    @Override
    public FlowInstance getFlowInstById(long flowInstId) {
        return flowInstanceDao.getObjectCascadeById(flowInstId);
    }

    @Override
    public NodeInstance getNodeInstById(long nodeInstId) {
        return nodeInstanceDao.getObjectCascadeById(nodeInstId);
    }

    @Override
    public List<FlowInstance> listAllFlowInstByOptTag(String flowOptTag) {
        return flowInstanceDao.listAllFlowInstByOptTag(flowOptTag);
    }

    @Override
    public void updateFlowInstOptInfo(long flowInstId, String flowOptName, String flowOptTag) {
        /*FlowInstance flowInst = flowInstanceDao.getObjectById(flowInstId);
        if (flowInst == null)
            return;
        flowInst.setFlowOptName(flowOptName);
        flowInst.setFlowOptTag(flowOptTag);
        flowInstanceDao.updateObject(flowInst);*/
        flowInstanceDao.updateFlowInstOptInfo(flowInstId, flowOptName, flowOptTag);
    }

    @Override
    public void updateFlowInstParentNode(long flowInstId, long parentFlowInstId, long parentNodeInstId) {
        FlowInstance flowInst = flowInstanceDao.getObjectById(flowInstId);
        if (flowInst == null)
            return;
        flowInst.setPreInstId(parentFlowInstId);
        flowInst.setPreNodeInstId(parentNodeInstId);
        flowInstanceDao.updateObject(flowInst);
    }

    @Override
    public String getNodeOptUrl(long nodeInstId, String userCode) {

        List<UserTask> taskList = actionTaskDao.listUserTaskByFilter(
            QueryUtils.createSqlParamsMap("nodeInstId", nodeInstId), new PageDesc(-1, -1));

        if (taskList == null || taskList.size() == 0)
            return null;
        else {
            UserTask task = taskList.get(0);
            return task.getNodeOptUrl();
        }

    }

    @Override
    public List<NodeInstance> listNodeInstsByNodecode(long flowInstId, String nodeCode) {
        FlowInstance flowInst = flowInstanceDao.getObjectById(flowInstId);
        if (flowInst == null)
            return null;
        List<NodeInfo> nodeList = flowNodeDao.listNodeByNodecode(flowInst.getFlowCode(),
            flowInst.getVersion(), nodeCode);
        if (nodeList == null)
            return null;
        List<NodeInstance> nodeInstList = new ArrayList<NodeInstance>();
        for (NodeInfo node : nodeList) {
            nodeInstList.addAll(
                flowInst.getAllNodeInstancesByNodeid(node.getNodeId()));
        }
        return nodeInstList;
    }

    @Override
    public StageInstance getStageInstByNodeInstId(long nodeInstId) {

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
                                 String transPath, FlowTransition trans, long preNodeInstId, String userCode, String unitCode) {
        FlowOptUtils.endInstance(flowInst, "C", userCode, flowInstanceDao);

        NodeInstance endNodeInst =
            FlowOptUtils.createNodeInst(unitCode, userCode, null, flowInst, null, flowInfo, endNode, trans);
        endNodeInst.setNodeInstId(nodeInstanceDao.getNextNodeInstId());
        endNodeInst.setNodeState("C");
        Date updateTime = DatetimeOpt.currentUtilDate();
        endNodeInst.setLastUpdateTime(updateTime);
        endNodeInst.setLastUpdateUser(userCode);
        endNodeInst.setPrevNodeInstId(preNodeInstId);
        if (StringUtils.isBlank(transPath))
            endNodeInst.setTransPath(String.valueOf(trans.getTransId()));
        else
            endNodeInst.setTransPath(transPath + "," + String.valueOf(trans.getTransId()));
        flowInst.addWfNodeInstance(endNodeInst);
    }

    private Set<String> calcNodeOpterators(FlowInstance flowInst, NodeInstance nodeInst, String nodeToken,
                                           NodeInfo nextOptNode,
                                           String userCode, String unitCode,
                                           FlowVariableTranslate varTrans) {


        Set<String> optUsers = null;
        if ("en".equals(nextOptNode.getRoleType())) {
            //如果节点的角色类别为 权限引擎则要调用权限引擎来分配角色
            //根据权限表达式创建任务列表
            String oldNodeInstUnitCode = null;
            NodeInstance oldNodeInst = flowInst.findLastSameNodeInst(nextOptNode.getNodeId(), nodeInst, 0l);
            if (oldNodeInst != null)
                oldNodeInstUnitCode = oldNodeInst.getUnitCode();

            Map<String, Set<String>> unitParams = UserUnitParamBuilder.createEmptyParamMap();
            UserUnitParamBuilder.addParamToParamMap(unitParams, "L", oldNodeInstUnitCode);
            UserUnitParamBuilder.addParamToParamMap(unitParams, "U",
                unitCode == null ? CodeRepositoryUtil.getUserInfoByCode(userCode).getPrimaryUnit() : unitCode);
            UserUnitParamBuilder.addParamToParamMap(unitParams, "P", flowInst.getNearestNodeUnitCode(nodeInst, nodeToken));
            UserUnitParamBuilder.addParamToParamMap(unitParams, "N", unitCode);
            UserUnitParamBuilder.addParamToParamMap(unitParams, "F", flowInst.getUnitCode());

            Map<String, Set<String>> userParams = UserUnitParamBuilder.createEmptyParamMap();
            UserUnitParamBuilder.addParamToParamMap(userParams, "C", flowInst.getUserCode());
            UserUnitParamBuilder.addParamToParamMap(userParams, "O", userCode);

            optUsers = UserUnitCalcEngine.calcOperators(userUnitFilterCalcContext, nextOptNode.getPowerExp(),
                unitParams, userParams, null, varTrans);
            if (optUsers == null || optUsers.size() == 0) {
                logger.error("权限引擎没有识别出符合表达式的操作人员！ wid:" + flowInst.getFlowInstId() + " nid" + nextOptNode.getNodeId());
            }
        } else if ("bj".equals(nextOptNode.getRoleType())) {
            optUsers = new HashSet<>();
            List<FlowWorkTeam> users = flowTeamDao.listFlowWorkTeamByRole(nodeInst.getFlowInstId(), nextOptNode.getRoleCode());
            for (FlowWorkTeam u : users)
                optUsers.add(u.getUserCode());
        } else/*gw xz*/ {
            optUsers = SysUserFilterEngine.getUsersByRoleAndUnit(userUnitFilterCalcContext,
                nextOptNode.getRoleType(), nextOptNode.getRoleCode(), unitCode);
        }

        return optUsers;
    }

    /**
     * @param flowInst
     * @param flowInfo
     * @param nodeInst
     * @param trans
     * @param userCode
     * @param unitCode
     * @param varTrans
     * @param nodeUnits
     * @param nodeOptUsers
     * @param application
     * @return
     * @throws WorkflowException
     */
    private Set<Long> submitToNextRouterNode(
        NodeInfo nextRoutertNode, FlowInstance flowInst, FlowInfo flowInfo,
        NodeInstance nodeInst, String transPath, FlowTransition trans, String userCode, String unitCode, String nodeToken,
        FlowVariableTranslate varTrans, Map<Long, Set<String>> nodeUnits,
        Map<Long, Set<String>> nodeOptUsers, ServletContext application)
        throws WorkflowException {
        String sRT = nextRoutertNode.getRouterType();
        Set<Long> resNodes = new HashSet<>();
        String preTransPath = StringUtils.isBlank(transPath) ?
            String.valueOf(trans.getTransId()) : transPath + "," + String.valueOf(trans.getTransId());

        if ("H".equals(sRT) || "D".equals(sRT)) {//D 分支和 H 并行
            //提交游离分支上的叶子节点 将不会向后流转
            /*FlowVariableTranslate  flowVarTrans = new FlowVariableTranslate(varTrans,
                      flowVariableDao.listFlowVariables(flowInst.getFlowInstId()),nodeInst,flowInst);

            flowVarTrans.setFlowOrganizes( this.viewFlowOrganize(flowInst.getFlowInstId()));
            flowVarTrans.setFlowWorkTeam( this.viewFlowWorkTeam(flowInst.getFlowInstId()));*/
            //获取下一批流转节点
            Set<FlowTransition> selTrans = selectTransitions(nextRoutertNode, varTrans);
            if (selTrans == null || selTrans.size() < 1) {
                if (nodeToken.startsWith("R")) {
                    return resNodes;
                } else {
                    throw new WorkflowException(WorkflowException.NotFoundNextNode,
                        "找不到后续节点：" + nodeInst.getFlowInstId() +
                            " 节点：" + nodeInst.getNodeInstId() + " 路由：" + nextRoutertNode.getNodeId());
                }
            }

            // D:分支 E:汇聚  G 多实例节点  H并行  R 游离 S：同步
            if ("D".equals(sRT)) {
                FlowTransition nodeTran = selTrans.iterator().next();
                long nextNodeId = nodeTran.getEndNodeId();
                resNodes = submitToNextNode(
                    nextNodeId, flowInst, flowInfo,
                    nodeInst, preTransPath, nodeTran, userCode, null, unitCode, nodeToken,
                    varTrans, nodeUnits, nodeOptUsers, application);
            } else {
                int nNo = 1;
                for (FlowTransition tran : selTrans) {
                    long nextNodeId = tran.getEndNodeId();
                    Set<Long> nNs = submitToNextNode(
                        nextNodeId, flowInst, flowInfo,
                        nodeInst, preTransPath, tran, userCode, null, unitCode, nodeToken + "." + nNo,
                        varTrans, nodeUnits, nodeOptUsers, application);
                    resNodes.addAll(nNs);
                    nNo++;
                }
            }

        } else {
            if ("E".equals(sRT)) {//汇聚  策略部分暂时没有做，先做以前一样的功能
                String preRunToken = NodeInstance.calcSuperToken(nodeToken);
                Set<String> nNs =
                    flowInst.calcNoSubmitSubNodeTokensInstByToken(preRunToken);
                //汇聚节点，所有节点都已提交,或者只要当前节点
                if (nNs == null || nNs.size() == 0 || (nNs.size() == 1 && nNs.contains(nodeInst.getRunToken()))) {
                    FlowTransition nodeTran = selectOptNodeTransition(nextRoutertNode);
                    long nextNodeId = nodeTran.getEndNodeId();
                    resNodes = submitToNextNode(
                        nextNodeId, flowInst, flowInfo,
                        nodeInst, preTransPath, nodeTran, userCode, null, unitCode, preRunToken,
                        varTrans, nodeUnits, nodeOptUsers, application);
                } else {
                    //A 所有都完成，R 至少有X完成，L 至多有X未完成， V 完成比率达到X
                    String sCT = nextRoutertNode.getConvergeType();
                    if (!"A".equals(sCT)) {
                        Set<Long> submitNodeIds = flowInst.calcSubmitSubNodeIdByToken(preRunToken);
                        List<FlowTransition> transList = flowTransitionDao.getNodeInputTrans
                            (nextRoutertNode.getNodeId());
                        boolean canSubmit = true;
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
                                    ;
                                } else if ("V".equals(sCT)) {
                                    canSubmit = Double.valueOf(submitNodeIds.size()) / Double.valueOf(submitNodeIds.size() + nNs.size())
                                        >= Double.valueOf(nextRoutertNode.getConvergeParam());
                                    ;
                                } else
                                    canSubmit = false;
                            } else
                                canSubmit = false;
                        }

                        if (canSubmit) {
                            Set<NodeInstance> sameNodes = flowInst.findAllActiveSubNodeInstByToken(preRunToken);
                            //结束这些节点
                            Date currentTime = new Date(System.currentTimeMillis());
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

                            FlowTransition nodeTran = selectOptNodeTransition(nextRoutertNode);
                            long nextNodeId = nodeTran.getEndNodeId();
                            resNodes = submitToNextNode(
                                nextNodeId, flowInst, flowInfo,
                                nodeInst, preTransPath, nodeTran, userCode, null, unitCode, preRunToken,
                                varTrans, nodeUnits, nodeOptUsers, application);
                        }
                    }
                }

            } else if ("G".equals(sRT)) {//多实例
                FlowTransition nodeTran = selectOptNodeTransition(nextRoutertNode);
                long nextNodeId = nodeTran.getEndNodeId();
                NodeInfo nextNode = flowNodeDao.getObjectById(nextNodeId);

                if (!"C".equals(nextNode.getNodeType())) { //报错
                    throw new WorkflowException(WorkflowException.FlowDefineError,
                        "多实例路由后面必须是业务节点：" + nodeInst.getFlowInstId() +
                            " 节点：" + nodeInst.getNodeInstId() + " 路由：" + nextRoutertNode.getNodeId());
                }

                FlowVariableTranslate flowVarTrans = new FlowVariableTranslate(varTrans,
                    flowVariableDao.listFlowVariables(flowInst.getFlowInstId()), nodeInst, flowInst);

                flowVarTrans.setFlowOrganizes(this.viewFlowOrganize(flowInst.getFlowInstId()));
                flowVarTrans.setFlowWorkTeam(this.viewFlowWorkTeam(flowInst.getFlowInstId()));
                //D 机构， U  人员（权限表达式） V 变量
                if ("D".equals(nextRoutertNode.getMultiInstType())) {
                    Set<String> nextNodeUnits = null;
                    if (nodeUnits != null)
                        nextNodeUnits = nodeUnits.get(nextRoutertNode.getNodeId());
                    if (nextNodeUnits == null) {
                        Map<String, Set<String>> unitParams = UserUnitParamBuilder.createEmptyParamMap();
                        UserUnitParamBuilder.addParamToParamMap(unitParams, "L", unitCode);
                        UserUnitParamBuilder.addParamToParamMap(unitParams, "U",
                            CodeRepositoryUtil.getUserInfoByCode(userCode).getPrimaryUnit());
                        UserUnitParamBuilder.addParamToParamMap(unitParams, "P", nodeInst.getUnitCode());
                        UserUnitParamBuilder.addParamToParamMap(unitParams, "F", flowInst.getUnitCode());

                        nextNodeUnits = UserUnitCalcEngine.calcUnitsByExp(userUnitFilterCalcContext, nextRoutertNode.getUnitExp(),
                            unitParams, flowVarTrans);
                    }

                    if (nextNodeUnits == null || nextNodeUnits.size() == 0) { //报错
                        throw new WorkflowException(WorkflowException.NoValueForMultiInst,
                            "多实例节点对应的机构变量为空：" + nodeInst.getFlowInstId() +
                                " 节点：" + nodeInst.getNodeInstId() + " 路由：" + nextRoutertNode.getNodeId());
                    } else {
                        int nRn = 1;
                        for (String uc : nextNodeUnits) {
                            //TODO 下一个节点不一定式业务节点，也有可能还是一个路由
                            this.saveFlowNodeVariable(flowInst.getFlowInstId(), nodeToken + "." + nRn,
                                "cd_" + nextRoutertNode.getNodeCode(), uc);
                            NodeInstance nextNodeInst = submitToSingleNextOptNode(
                                nextNode, flowInst, flowInfo,
                                nodeInst, preTransPath, nodeTran, userCode, uc, null, nodeToken + "." + nRn,
                                false, varTrans, nodeUnits, nodeOptUsers,
                                application);
                            resNodes.add(nextNodeInst.getNodeInstId());
                            nRn++;
                        }
                    }
                } else if ("U".equals(nextRoutertNode.getMultiInstType())) {

                    Set<String> optUsers = null;
                    if (nodeOptUsers != null)
                        optUsers = nodeOptUsers.get(nextRoutertNode.getNodeId());
                    if (optUsers == null) {
                        optUsers = calcNodeOpterators(flowInst, nodeInst, nodeToken,
                            nextRoutertNode, userCode, unitCode,
                            varTrans);
                    }
                    if (optUsers == null || optUsers.size() == 0) {
                        throw new WorkflowException(WorkflowException.NoValueForMultiInst,
                            "多实例节点对应的权限表达式人员为空：" + nodeInst.getFlowInstId() +
                                " 节点：" + nodeInst.getNodeInstId() + " 路由：" + nextRoutertNode.getNodeId());
                    } else {
                        int nRn = 1;
                        //Date currentTime = new Date(System.currentTimeMillis());
                        for (String uc : optUsers) {
                            //TODO 下一个节点不一定式业务节点，也有可能还是一个路由
                            this.saveFlowNodeVariable(flowInst.getFlowInstId(), nodeToken + "." + nRn,
                                "cu_" + nextRoutertNode.getNodeCode(), uc);
                            NodeInstance nextNodeInst = submitToSingleNextOptNode(
                                nextNode, flowInst, flowInfo,
                                nodeInst, preTransPath, nodeTran, uc,
                                CodeRepositoryUtil.getUserPrimaryUnit(uc) != null ? CodeRepositoryUtil.getUserPrimaryUnit(uc).getUnitCode() : null,
                                //CodeRepositoryUtil.getUserInfoByCode(uc).getPrimaryUnit(),
                                null, nodeToken + "." + nRn,
                                true, varTrans, nodeUnits, nodeOptUsers,
                                application);

                            resNodes.add(nextNodeInst.getNodeInstId());
                            nRn++;
                        }
                    }
                }//else if("V".equals(nextRoutertNode.getMultiInstType())){
                // 保留
                //}

            } else if ("R".equals(sRT)) {//游离
                FlowTransition nodeTran = selectOptNodeTransition(nextRoutertNode);
                long nextNodeId = nodeTran.getEndNodeId();
                resNodes = submitToNextNode(
                    nextNodeId, flowInst, flowInfo,
                    nodeInst, preTransPath, nodeTran, userCode, null, unitCode, "R" + nodeToken,
                    varTrans, nodeUnits, nodeOptUsers, application);

            } else if ("S".equals(sRT)) {//同步 保留
                String preRunToken = NodeInstance.calcSuperToken(nodeToken);
                Set<String> nNs =
                    flowInst.calcNoSubmitSubNodeTokensInstByToken(preRunToken);
                //汇聚节点，所有节点都已提交
                if (nNs == null || nNs.size() == 0) {
                    Map<String, NodeInstance> syncNodes =
                        flowInst.findSubmitSubNodeInstByToken(preRunToken);
                    for (Map.Entry<String, NodeInstance> ent : syncNodes.entrySet()) {
                        NodeInfo rtN = selectNextNodeByNodeId(ent.getValue().getNodeId());
                        if ("R".equals(rtN.getNodeType()) && "S".equals(rtN.getRouterType())) {
                            FlowTransition nextTran = selectOptNodeTransition(rtN);
                            Set<Long> sN = submitToNextNode(
                                nextTran.getEndNodeId(), flowInst, flowInfo,
                                ent.getValue(), preTransPath, nextTran, ent.getValue().getLastUpdateUser(),
                                null, ent.getValue().getUnitCode(), ent.getValue().getRunToken(),
                                varTrans, nodeUnits, nodeOptUsers, application);
                            resNodes.addAll(sN);
                        }
                    }
                }
            }
        }
        //WfNode routeNode =
        return resNodes;
    }

    private Set<Long> submitToNextNode(
        long nextNodeId, FlowInstance flowInst, FlowInfo flowInfo,
        NodeInstance nodeInst, String transPath, FlowTransition nodeTran,
        String userCode, String grantorCode, String unitCode, String nodeToken,
        FlowVariableTranslate varTrans,
        Map<Long, Set<String>> nodeUnits, Map<Long, Set<String>> nodeOptUsers,
        ServletContext application) throws WorkflowException {

        Set<Long> resNodes = new HashSet<Long>();
        NodeInfo nextNode = flowNodeDao.getObjectById(nextNodeId);
        if ("R".equals(nextNode.getNodeType())) { // 后续节点为路由节点

            resNodes = submitToNextRouterNode(
                nextNode, flowInst, flowInfo,
                nodeInst, transPath, nodeTran, userCode, unitCode, nodeToken,
                varTrans, nodeUnits, nodeOptUsers, application);

        } else if ("F".equals(nextNode.getNodeType())) {
            //如果是最后一个节点，则要结束整个流程 调用 endInstance
            this.endFlowInstance(flowInst, flowInfo, nextNode, transPath,
                nodeTran, nodeInst.getNodeInstId(), userCode, unitCode);

            if ("Y".equals(flowInst.getIsSubInst())) {
                long otherSubFlows = flowInstanceDao.calcOtherSubflowSum(
                    flowInst.getPareNodeInstId(), flowInst.getFlowInstId());
                if (otherSubFlows == 0)// 其他所有子流程都关闭了，则提交父流程对应的节点
                    resNodes = submitOptInside(flowInst.getPareNodeInstId(), userCode, grantorCode, unitCode,
                        varTrans, nodeUnits, nodeOptUsers, application);
            }
        } else {

            NodeInstance ns = submitToNextOptNode(
                nextNode, flowInst, flowInfo,
                nodeInst, transPath, nodeTran, userCode, unitCode, nodeToken,
                varTrans, nodeUnits, nodeOptUsers, application);
            resNodes.add(ns.getNodeInstId());

        }

        return resNodes;
    }


    /**
     * @param nextOptNode
     * @param flowInst
     * @param flowInfo
     * @param nodeInst
     * @param trans
     * @param optUserCode
     * @param optUnitCode
     * @param varTrans
     * @param nodeUnits
     * @param nodeOptUsers
     * @param application
     * @return
     * @throws WorkflowException
     */
    private NodeInstance submitToNextOptNode(
        NodeInfo nextOptNode, FlowInstance flowInst, FlowInfo flowInfo,
        NodeInstance nodeInst, String transPath, FlowTransition trans,
        String optUserCode, String optUnitCode, String nodeToken,
        FlowVariableTranslate varTrans,
        Map<Long, Set<String>> nodeUnits, Map<Long, Set<String>> nodeOptUsers,
        ServletContext application) throws WorkflowException {

        //String nodeToken = nodeInst.getRunToken();
        long nextCode = nextOptNode.getNodeId();


        String nextNodeUnit = null;
        if (nodeUnits != null && nodeUnits.containsKey(nextCode)) {
            Set<String> thisNodeUints = nodeUnits.get(nextCode);
            if (thisNodeUints != null && thisNodeUints.size() > 0)
                nextNodeUnit = thisNodeUints.iterator().next();
        }

        //调用机构引擎来计算 unitCode
        // 如果指定机构 就不需要再进行计算了
        if (nextNodeUnit == null) {
            //获取上一个相同节点实例机构
            String oldNodeInstUnitCode = null;
            NodeInstance oldNodeInst = flowInst.findLastSameNodeInst(nextCode, nodeInst, 0l);
            if (oldNodeInst != null)
                oldNodeInstUnitCode = oldNodeInst.getUnitCode();

            Map<String, Set<String>> unitParams = UserUnitParamBuilder.createEmptyParamMap();
            UserUnitParamBuilder.addParamToParamMap(unitParams, "L", oldNodeInstUnitCode);
            UserUnitParamBuilder.addParamToParamMap(unitParams, "U",
                optUnitCode == null ?
                    CodeRepositoryUtil.getUserInfoByCode(optUserCode).getPrimaryUnit() :
                    optUnitCode);
            UserUnitParamBuilder.addParamToParamMap(unitParams, "P",
                flowInst.getNearestNodeUnitCode(nodeInst, nodeToken));
            UserUnitParamBuilder.addParamToParamMap(unitParams, "F", flowInst.getUnitCode());

            nextNodeUnit = UserUnitCalcEngine.calcSingleUnitByExp(userUnitFilterCalcContext, nextOptNode.getUnitExp(),
                unitParams, varTrans);
        }

        //如果用户指定的节点对应的操作人员为空 则不创建这个节点
        Set<String> thisNodeUsers = null;
        String nextNodeUser = optUserCode;
        boolean assignedUser = false;
        if (nodeOptUsers != null && nodeOptUsers.containsKey(nextCode)) {
            thisNodeUsers = nodeOptUsers.get(nextCode);
            if (thisNodeUsers != null && thisNodeUsers.size() > 0) {
                nextNodeUser = thisNodeUsers.iterator().next();
                assignedUser = true;
            }
        }

        //计算节点的用户
        NodeInstance nextNodeInst = submitToSingleNextOptNode(
            nextOptNode, flowInst, flowInfo,
            nodeInst, transPath, trans, nextNodeUser, nextNodeUnit, null, nodeToken,
            false, varTrans, nodeUnits, nodeOptUsers,
            application);
        //如果用户手动指定节点的操作用户，替换节点的用户分配信息
        if (assignedUser && thisNodeUsers.size() > 0) {
            //去掉已有的任务分配信息
            nextNodeInst.getWfActionTasks().clear();
            if ("A".equals(nextOptNode.getOptType()) || thisNodeUsers.size() == 1) {
                nextNodeInst.setTaskAssigned("S");
                nextNodeInst.setUserCode(nextNodeUser);
            } else/* if( thisNodeUsers.size() >1 )*/ {
                Date createTime = new Date(System.currentTimeMillis());
                nextNodeInst.setTaskAssigned("T");
                for (String uc : thisNodeUsers) {
                    ActionTask wfactTask = FlowOptUtils.createActionTask(uc, nextNodeInst, nextOptNode);
                    wfactTask.setTaskId(actionTaskDao.getNextTaskId());
                    wfactTask.setAssignTime(createTime);
                    nextNodeInst.addWfActionTask(wfactTask);
                }
            }
        }
        //flowInstanceDao.updateObject(flowInst);
        return nextNodeInst;
    }

    private NodeInstance submitToSingleNextOptNode(
        NodeInfo nextOptNode, FlowInstance flowInst, FlowInfo flowInfo,
        NodeInstance nodeInst, String transPath, FlowTransition trans, String userCode,
        String unitCode, String nodeParam, String nodeToken,
        boolean assignedUser, FlowVariableTranslate varTrans,
        Map<Long, Set<String>> nodeUnits, Map<Long, Set<String>> nodeOptUsers,
        ServletContext application) throws WorkflowException {

        Date currentTime = new Date(System.currentTimeMillis());

        //long nextCode = nextOptNode.getNodeId();
        long lastNodeInstId = nodeInstanceDao.getNextNodeInstId();

        NodeInstance nextNodeInst = FlowOptUtils.createNodeInst(unitCode, userCode, nodeParam,
            flowInst, nodeInst, flowInfo, nextOptNode, trans);

        nextNodeInst.setNodeInstId(lastNodeInstId);
        nextNodeInst.setPrevNodeInstId(nodeInst.getNodeInstId());

        nextNodeInst.setTransPath(
            StringUtils.isBlank(transPath) ? String.valueOf(trans.getTransId()) :
                transPath + "," + String.valueOf(trans.getTransId()));
        nextNodeInst.setRunToken(nodeToken);

        //设置阶段进入时间 或者变更时间
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
        //TODO 如果自动执行的节点下一步生成的节点实例有多个，这边的return就不对了
        //暂时先取第一个节点实例，解决部分问题
        NodeInstance tempFirstNode = null;
        //判断是否为子流程 A:一般 B:抢先机制 C:多人操作 S:子流程
        if ("S".equals(nextOptNode.getOptType())) {
            //如果是子流程 启动流程
            nextNodeInst.setNodeState("W");
            //子流程的机构 要和 节点的机构一致
            FlowInstance tempFlow = createInstInside(nextOptNode.getSubFlowCode(),
                flowDefDao.getLastVersion(nextOptNode.getSubFlowCode()),
                flowInst.getFlowOptName() + "--" + nextOptNode.getNodeName(),
                flowInst.getFlowOptTag(), userCode, unitCode, lastNodeInstId, nodeInst.getFlowInstId(),
                varTrans, false,null);
            nextNodeInst.setSubFlowInstId(tempFlow.getFlowInstId());
            //子流程的时间限制和父流程节点的一致
            if (nextNodeInst.getTimeLimit() != null) {
                flowInst.setTimeLimit(nextNodeInst.getTimeLimit());
                flowInstanceDao.updateObject(flowInst);
            }
            tempFirstNode = tempFlow.getFirstNodeInstance();
        } else if (!assignedUser) {
            Set<String> optUsers = calcNodeOpterators(flowInst, nodeInst, nodeToken,
                nextOptNode,
                userCode, unitCode,
                varTrans);
            //计算人员的分配策略
            if (optUsers != null && optUsers.size() > 0)
                nextNodeInst.setUserCode(optUsers.iterator().next());

            if ("gw".equals(nextOptNode.getRoleType())) {/* &&
                    "A".equals(nextOptNode.getOptType())){*/
                nextNodeInst.setTaskAssigned("D");
            } else if (optUsers != null && optUsers.size() > 0 &&
                ("en".equals(nextOptNode.getRoleType())
                    || "bj".equals(nextOptNode.getRoleType())
                          /*行政角色按道理是不能有多个人可以同时做的*/
                    || "xz".equals(nextOptNode.getRoleType())
                    || "C".equals(nextOptNode.getOptType()))) {
               /* if(optUsers.size()==1){
                    nextNodeInst.setTaskAssigned("S");
                }else{*/
                if (optUsers.size() > 1) {
                    nextNodeInst.setTaskAssigned("T");
                    for (String uc : optUsers) {
                        ActionTask wfactTask = FlowOptUtils.createActionTask(uc, nextNodeInst, nextOptNode);
                        wfactTask.setTaskId(actionTaskDao.getNextTaskId());
                        wfactTask.setAssignTime(currentTime);
                        actionTaskDao.saveNewObject(wfactTask);
                        nextNodeInst.addWfActionTask(wfactTask);
                    }
                } else {
                    nextNodeInst.setTaskAssigned("S");
                    nextNodeInst.setUserCode(optUsers.iterator().next());
                }
            }
        }
        /**
         *  检查令牌冲突（自由流程，令牌的冲突有业务程序和流程图自己控制，无需检查）
         *  这段代码是检查令牌的一致性，多实例节点多次运行时会出错的，
         *  这个本来的目的是为了检查从分支中返回到 主干上，因为有游离节点的存在所以需要这个检查
         *  这个算法不是判断是否相等，而是应该判断层次是否一致，只要层次一致就没有问题，如果不一致就需要截断后面的层次
         */
        if ("F".equals(flowInfo.getFlowClass())) {//自由流程，令牌的冲突有业务程序和流程图自己控制，无需检查
            NodeInstance thisNodeInst = nextNodeInst;
            //查找在同一条运行路径上的相同节点
            NodeInstance sameInst = flowInst.findLastSameNodeInst(thisNodeInst.getNodeId(), thisNodeInst, thisNodeInst.getNodeInstId());
            if (sameInst != null) {
                int oldGen = sameInst.getTokenGeneration();
                if (oldGen > 0 && thisNodeInst.getTokenGeneration() > oldGen) {
                    String thisToken = NodeInstance.truncTokenGeneration(
                        thisNodeInst.getRunToken(), oldGen);
                    thisNodeInst.setRunToken(thisToken);
                    //将相关的分支节点设置为无效
                    for (NodeInstance ni : flowInst.findAllActiveSubNodeInstByToken(thisToken)) {
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
            }
        }
        nodeInstanceDao.saveNewObject(nextNodeInst);
        flowInst.setLastUpdateTime(currentTime);
        flowInst.setLastUpdateUser(userCode);
        flowInstanceDao.updateObject(flowInst);

        //执行节点创建后 事件
        NodeEventSupport nodeEventExecutor = NodeEventSupportFactory.getNodeEventSupportBean(nextOptNode);
        nodeEventExecutor.runAfterCreate(flowInst, nextNodeInst, nextOptNode, userCode);


        //检查自动执行节点 并执行相关操作
        if ("D".equals(nextOptNode.getOptType())) {
            boolean needSubmit = nodeEventExecutor.runAutoOperator(flowInst, nextNodeInst,
                nextOptNode, userCode);
            if (needSubmit) {
                //TODO 如果自动执行的节点下一步生成的节点实例有多个，这边的return就不对了
                //暂时先取第一个节点实例，解决部分问题
                Set<Long> nextNodes = this.submitOptInside(lastNodeInstId, userCode, null, unitCode,
                    varTrans, nodeUnits, nodeOptUsers, application);
                for (Long n : nextNodes) {
                    nextNodeInst = nodeInstanceDao.getObjectById(n);
                    break;
                }
            }

        } else if ("E".equals(nextOptNode.getOptType())) {  //哑元节点 自动提交
            try {
                Set<Long> nextNodes = this.submitOptInside(lastNodeInstId, userCode, null, unitCode,
                    varTrans, nodeUnits, nodeOptUsers, application);
                for (Long n : nextNodes) {
                    nextNodeInst = nodeInstanceDao.getObjectById(n);
                    break;
                }
            } catch (WorkflowException e) {
                logger.error("自动提交哑元节点 " + lastNodeInstId + "后提交出错 。" + e.getMessage());
                throw e;
            }
        }
        if (tempFirstNode != null) {
            return tempFirstNode;
        }
        return nextNodeInst;
    }


    private NodeInfo selectNextNodeByNodeId(Long nodeId) {
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
            for (FlowTransition trans : transList) {
                if (BooleanBaseOpt.castObjectToBoolean(
                    VariableFormula.calculate(trans.getTransCondition(), varTrans))) {
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
    private Set<Long> submitOptInside(long nodeInstId, String userCode, String grantorCode,
                                      String unitCode, UserUnitVariableTranslate varTrans,
                                      Map<Long, Set<String>> nodeUnits, Map<Long, Set<String>> nodeOptUsers,
                                      ServletContext application) throws WorkflowException {

        //2012-04-16 重构提交事件，添加一个多实例节点类型，这个节点类型会根据不同的机构创建不同的节点
        //根据上级节点实例编号获取节点所在父流程实例信息
        NodeInstance nodeInst = nodeInstanceDao.getObjectCascadeById(nodeInstId);
        if (nodeInst == null) {
            logger.error("找不到节点实例：" + nodeInstId);
            throw new WorkflowException(WorkflowException.NodeInstNotFound, "找不到节点实例：" + nodeInstId);
        }
        FlowInstance flowInst = flowInstanceDao.getObjectCascadeById(nodeInst.getFlowInstId());
        if (flowInst == null) {
            logger.error("找不到流程实例：" + nodeInst.getFlowInstId());
            throw new WorkflowException(WorkflowException.FlowInstNotFound,
                "找不到流程实例：" + nodeInst.getFlowInstId());
        }

        FlowInfo flowInfo = flowDefDao.getFlowDefineByID(flowInst.getFlowCode(), flowInst.getVersion());

        if ("P".equals(nodeInst.getIsTimer())) {
            logger.error("流程节点处于暂停计时 状态：" + flowInst.getInstState() +
                "节点：" + nodeInstId);
            throw new WorkflowException(WorkflowException.PauseTimerNode,
                "流程节点处于暂停计时 状态：" + flowInst.getInstState() +
                    "节点：" + nodeInstId);
        }
        //校验节点状态 流程和节点状态都要为正常
        if (!"N".equals(flowInst.getInstState()) ||
            (!"N".equals(nodeInst.getNodeState())
                && !"W".equals(nodeInst.getNodeState()) // 等待子流程返回
            )
            ) {
            logger.error("流程节点状态不正确，流程：" + nodeInst.getFlowInstId() + " 状态：" + flowInst.getInstState() +
                "节点：" + nodeInstId + " 状态：" + nodeInst.getNodeState());
            throw new WorkflowException(WorkflowException.IncorrectNodeState,
                "流程节点状态不正确，流程：" + nodeInst.getFlowInstId() + " 状态：" + flowInst.getInstState() +
                    "节点：" + nodeInstId + " 状态：" + nodeInst.getNodeState());
        }

        String sGrantor;
        NodeInfo currNode = flowNodeDao.getObjectById(nodeInst.getNodeId());
        if ("gw".equals(currNode.getRoleType())) {
            //TODO 判断人员岗位吻合
            sGrantor = userCode;
        } else if (grantorCode == null || grantorCode.equals(userCode)) {
            sGrantor = actionTaskDao.getTaskGrantor(nodeInstId, userCode);
            //哑元、自动执行以及子流程 不判断
            if (sGrantor == null && !"E".equals(currNode.getOptType())
                && !"D".equals(currNode.getOptType()) && !"S".equals(currNode.getOptType())) {
                logger.error("用户没有权限操作该节点：" + userCode + " -- " + nodeInstId);
                throw new WorkflowException(WorkflowException.WithoutPermission, "用户没有权限操作该节点：" + userCode + " -- " + nodeInstId);
            }
        } else {
            sGrantor = grantorCode;
            if (!"E".equals(currNode.getOptType()) && !"D".equals(currNode.getOptType())
                && !"S".equals(currNode.getOptType()) &&
                !actionTaskDao.hasOptPower(nodeInstId, userCode, grantorCode)) {
                logger.error("用户没有权限操作该节点：" + userCode + " -- " + nodeInstId);
                throw new WorkflowException(WorkflowException.WithoutPermission, "用户没有权限操作该节点：" + userCode + " -- " + nodeInstId);
            }
        }

        Date updateTime = DatetimeOpt.currentUtilDate();
        nodeInst.setLastUpdateTime(updateTime);
        nodeInst.setLastUpdateUser(userCode);
        //创建节点提交日志 S:提交节点
        ActionLog wfactlog = FlowOptUtils.createActionLog("S", userCode, nodeInst, currNode);
        wfactlog.setActionId(actionLogDao.getNextActionId());
        wfactlog.setActionTime(updateTime);
        if (sGrantor != null && !sGrantor.equals(userCode)) {
            nodeInst.setGrantor(sGrantor);
            wfactlog.setGrantor(sGrantor);
        }
        nodeInst.addWfActionLog(wfactlog);
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
        flowInst.setLastUpdateUser(userCode);
        flowInstanceDao.updateObject(flowInst);

        Set<Long> nextNodeInsts = new HashSet<>();


        if ("T".equals(nodeInst.getTaskAssigned())) {
            //多人操作节点 等待所有人都提交才可以提交
            /**这个应该会不需要了，暂时保留
             * 这样的需求应该会被 按人进行多实例划分，
             */
            int havnotSubmit = 0;
            for (ActionTask task : nodeInst.getWfActionTasks()) {
                if ("T".equals(task.getIsValid()) && "A".equals(task.getTaskState())
                    //这个可能存在一个问题就是最后一个人没有提交，但是已经过期了，这个需要处理，需要在设置任务列表时注意
                    && (task.getExpireTime() == null || task.getExpireTime().after(new Date(System.currentTimeMillis())))) {
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
                nextNodeInsts.add(nodeInstId);
                return nextNodeInsts;
            }
        }
        /**
         * 节点提交前事件
         */
        NodeEventSupport nodeEventExecutor = NodeEventSupportFactory.getNodeEventSupportBean(currNode);
        nodeEventExecutor.runBeforeSubmit(flowInst, nodeInst, currNode, userCode);

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
            if (nodeInst.getRunToken().startsWith("R")) {
                //logger.info("游离节点:" + nodeInstId);
                //将节点的状态设置为已完成
                nodeInst.setNodeState("C");
                nodeInstanceDao.updateObject(nodeInst);
                return nextNodeInsts;
            } else {
                logger.error("流程：" + nodeInst.getFlowInstId() + "节点：" + nodeInstId + " " + currNode.getNodeName() + " 没有找到符合流转条件的后续节点。");
                throw new WorkflowException(WorkflowException.NotFoundNextNode,
                    "流程：" + nodeInst.getFlowInstId() + "节点：" + nodeInstId + " " + currNode.getNodeName() + " 没有找到符合流转条件的后续节点。");
            }
        }

        synchronized (lockObject) {
            /*WfNodeInstance*/
            nodeInst = nodeInstanceDao.getObjectCascadeById(nodeInstId);
            if (!"N".equals(nodeInst.getNodeState()) && !"W".equals(nodeInst.getNodeState())) {
                logger.error("流程：" + nodeInst.getFlowInstId() + "节点：" + nodeInstId + " " + currNode.getNodeName() + " 已经被其他线程提交，请避免重复提交。");
                throw new WorkflowException(WorkflowException.IncorrectNodeState,
                    "流程：" + nodeInst.getFlowInstId() + "节点：" + nodeInstId + " " + currNode.getNodeName() + " 已经被其他线程提交，请避免重复提交。");

            }
            nodeInst.setNodeState("C");
            nodeInstanceDao.updateObject(nodeInst);
            //DatabaseOptUtils.flush(nodeInstanceDao.getCurrentSession());
        }
        //刷新 变量接口 里面的变量
        List<FlowVariable> flowVariables = flowVariableDao.listFlowVariables(flowInst.getFlowInstId());
        Map<String, List<String>> flowOrganizes = this.viewFlowOrganize(flowInst.getFlowInstId());
        Map<String, List<String>> flowWorkTeam = this.viewFlowWorkTeam(flowInst.getFlowInstId());
        //flushVariables((FlowVariableTranslate) varTrans,flowVariables,flowOrganizes,flowWorkTeam);
        FlowVariableTranslate flowVarTrans = new FlowVariableTranslate(varTrans,
            flowVariables, nodeInst, flowInst);
        flowVarTrans.setFlowOrganizes(flowOrganizes);
        flowVarTrans.setFlowWorkTeam(flowWorkTeam);

        long nextNodeId = nodeTran.getEndNodeId();
        String nodeToken = nodeInst.getRunToken();
        nextNodeInsts = submitToNextNode(
            nextNodeId, flowInst, flowInfo,
            nodeInst, "", nodeTran, userCode, grantorCode, unitCode, nodeToken,
            flowVarTrans, nodeUnits, nodeOptUsers, application);

        //flowInst.setLastUpdateTime(updateTime);
        //flowInst.setLastUpdateUser(userCode);

        nodeInstanceDao.updateObject(nodeInst);
        //flowInstanceDao.updateObject(flowInst);

        return nextNodeInsts;
    }

    /**
     * 关闭本节点分支以外的其他分支的所有节点,特指和本节点平行的分支，就是同一个父类令牌的分支
     *
     * @param nodeInstId  当前活动节点
     * @param optUserCode 操作人员
     */
    public void disableOtherBranchNodes(long nodeInstId, String optUserCode) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null || !"N".equals(nodeInst.getNodeState())) {
            logger.error("找不到节点实例：" + nodeInstId + "，或者实例不是正常状态的节点。");
            return;
        }
        FlowInstance flowInst = flowInstanceDao.getObjectById(nodeInst.getFlowInstId());
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
     *
     * 新增子流程退回父流程的方法
     */
    @Override
    public long rollbackOpt(long nodeInstId, String mangerUserCode) {
        // 添加令牌算法
        NodeInstance thisNodeInst = nodeInstanceDao.getObjectCascadeById(nodeInstId);
        if (thisNodeInst == null)
            return -1;
        // 当前节点状态必需为正常
        if (!"N".equals(thisNodeInst.getNodeState()))
            return -6;

        FlowInstance flowInst = flowInstanceDao.getObjectCascadeById(thisNodeInst
            .getFlowInstId());
        if (flowInst == null)
            return -2;

        /*
         * WfNode nodedef = flowNodeDao.getObjectById( thisnode.getNodeId());
         * if("E".equals(nodedef.getNodetype())){ //本节点为汇聚节点不允许回退 return -5; }
         */
        // 查找上一个流经节点
        NodeInstance prevNodeInst = null;
        if (thisNodeInst.getPrevNodeInstId() != null) {
            prevNodeInst = nodeInstanceDao.getObjectCascadeById(thisNodeInst.getPrevNodeInstId());
        } else {
            prevNodeInst = flowInst.getPareNodeInst(nodeInstId);
        }
        //是否子流程退回父流程
        Boolean subProcess=false;
        // 查找上一个流经节点
        FlowInstance prevFlowInst = null;
        if (prevNodeInst == null) {
            //找不到上一节点之后，判断是否子流程
            if(flowInst.getPreNodeInstId()==null) {
                return -3;
            }else{
                //是子流程的话，把流程实例中的prenodeinst取出来找到对应的前一节点
                subProcess=true;
                prevNodeInst=nodeInstanceDao.getObjectCascadeById(flowInst.getPreNodeInstId());
                prevFlowInst=flowInstanceDao.getObjectCascadeById(flowInst.getPreInstId());
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
                prevNodeInst = nodeInstanceDao.getObjectCascadeById(prevNodeInst.getPrevNodeInstId());
                if (prevNodeInst == null) {
                    prevNodeInst = flowInst.getPareNodeInst(prevNodeInst
                        .getNodeInstId());
                }
                if (prevNodeInst == null)
                    return -3;
                //判断一下父流程中的节点是否已经被退回，已经被退回之后就不能再次退回
                if(prevFlowInst!=null){
                    for(NodeInstance no:prevFlowInst.getFlowNodeInstances()){
                        //罗列处于正常、暂缓的节点
                        if("N,S,P".contains(no.getNodeState())){
                            //如果想要退回的节点处于正常、暂缓，无需退回
                            if(prevNodeInst.getNodeId().equals(no.getNodeId())){
                                return -5;
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
        thisNodeInst.setLastUpdateUser(mangerUserCode);
        thisNodeInst.setLastUpdateTime(updateTime);

        long lastNodeInstId = nodeInstanceDao.getNextNodeInstId();
        NodeInstance nextNodeInst = flowInst.newWfNodeInstance();
        //如果是子流程退回父流程，把流程id置为父流程的流程id
        if(subProcess) {
            nextNodeInst.setFlowInstId(flowInst.getPreInstId());
            flowInst.setInstState("B");
        }
        nextNodeInst.copyNotNullProperty(prevNodeInst);
        nextNodeInst.setNodeInstId(lastNodeInstId);
        nextNodeInst.setCreateTime(updateTime);
        // nextNodeInst.setTimeLimit(null);
        nextNodeInst.setNodeState("N");
        nextNodeInst.setTaskAssigned(prevNodeInst.getTaskAssigned());
        nextNodeInst.setLastUpdateUser(mangerUserCode);
        nextNodeInst.setLastUpdateTime(updateTime);

        for (ActionTask task : prevNodeInst.getWfActionTasks()) {
            if ("T".equals(task.getIsValid())) {
                ActionTask newtask = FlowOptUtils.createActionTask(
                    task.getUserCode(), nextNodeInst, nodedef);
                newtask.setTaskId(actionTaskDao.getNextTaskId());
                // 要判断 过期时间的问题
                //nextNodeInst.addWfActionTask(newtask);
                nextNodeInst.setTimeLimit(null);
                nextNodeInst.setTaskAssigned("T");
                actionTaskDao.saveNewObject(newtask);
            }
        }

        flowInst.addWfNodeInstance(nextNodeInst);
        nodeInstanceDao.mergeObject(thisNodeInst);
        nodeInstanceDao.mergeObject(nextNodeInst);
        flowInstanceDao.updateObject(flowInst);
        //调用发送消息接口
        Set<Long> nodeInstIds = new HashSet<>();
        nodeInstIds.add(lastNodeInstId);
        FlowOptUtils.sendMsg(nodeInstId, nodeInstIds, mangerUserCode);
        return lastNodeInstId;
    }

    /**
     * 如果后续节点是 自动运行 和哑元 节点，节点被操作的判断将会误判
     */
    public boolean nodeCanBeReclaim(long nodeInstId) {
        NodeInstance thisnode = nodeInstanceDao.getObjectById(nodeInstId);
        if (thisnode == null)
            return false;
        FlowInstance flow = flowInstanceDao.getObjectById(thisnode
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

    @Override
    public Set<Long> submitOpt(long nodeInstId, String userCode, String unitCode,
                               UserUnitVariableTranslate varTrans, ServletContext application)
        throws WorkflowException {
        Set<Long> nextNodeInsts = submitOptInside(nodeInstId, userCode, userCode, unitCode,
            varTrans, null, null, application);
        FlowOptUtils.sendMsg(nodeInstId, nextNodeInsts, userCode);
        return nextNodeInsts;
    }

    @Override
    public Set<Long> submitOpt(long nodeInstId, String userCode, String grantorCode,
                               String unitCode, UserUnitVariableTranslate varTrans, ServletContext application)
        throws WorkflowException {
        Set<Long> nextNodeInsts = submitOptInside(nodeInstId, userCode, grantorCode, unitCode,
            varTrans, null, null, application);
        FlowOptUtils.sendMsg(nodeInstId, nextNodeInsts, userCode);
        return nextNodeInsts;
    }

    /**
     * 返回下一步节点的节点实例ID
     *
     * @param nodeInstId   当前节点实例编号
     * @param userCode     操作用户编号 对应用户表达式  O operator
     * @param unitCode     用户机构，如果为空系统会自动负责为 操作用户的主机构，机构表达式要为 U
     * @param varTrans     变量转换器
     * @param nodeOptUsers 预设的节点操作用户  给程序自行判断用户和机构用的
     * @param nodeUnits    预设的节点机构 给程序自行判断用户和机构用的
     * @return 节点实例编号列表
     */
    @Override
    public Set<Long> submitOptWithAssignUnitAndUser(long nodeInstId, String userCode,
                                                    String unitCode, UserUnitVariableTranslate varTrans,
                                                    Map<Long, Set<String>> nodeUnits, Map<Long, Set<String>> nodeOptUsers,
                                                    ServletContext application) throws WorkflowException {
        return submitOptInside(nodeInstId, userCode, userCode, unitCode,
            varTrans, nodeUnits, nodeOptUsers, application);
    }


    /**
     * 返回下一步节点的节点实例ID
     *
     * @param nodeInstId   当前节点实例编号
     * @param userCode     操作用户编号 对应用户表达式  O operator
     * @param unitCode     用户机构，如果为空系统会自动负责为 操作用户的主机构，机构表达式要为 U
     * @param varTrans     变量转换器
     * @param nodeOptUsers 预设的节点操作用户  给程序自行判断用户和机构用的
     * @param nodeUnits    预设的节点机构 给程序自行判断用户和机构用的
     * @return 节点实例编号列表
     */
    @Override
    public Set<Long> submitOptWithAssignUnitAndUser(long nodeInstId, String userCode,
                                                    String grantorCode, String unitCode, UserUnitVariableTranslate varTrans,
                                                    Map<Long, Set<String>> nodeUnits, Map<Long, Set<String>> nodeOptUsers,
                                                    ServletContext application) throws WorkflowException {
        return submitOptInside(nodeInstId, userCode, grantorCode, unitCode,
            varTrans, nodeUnits, nodeOptUsers, application);
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
    public Set<NodeInfo> viewNextNode(long nodeInstId, String userCode,
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


        Set<NodeInfo> nextNodes = new HashSet<NodeInfo>();
        NodeInfo nextNode = selectNextNodeByNodeId(currNode.getNodeId());
        if ("C".equals(nextNode.getNodeType())) {
            nextNodes.add(nextNode);
        } else if ("R".equals(nextNode.getNodeType())) {
            FlowVariableTranslate flowVarTrans = new FlowVariableTranslate(varTrans,
                flowVariableDao.listFlowVariables(flowInst.getFlowInstId()), nodeInst, flowInst);

            flowVarTrans.setFlowOrganizes(this.viewFlowOrganize(flowInst.getFlowInstId()));
            flowVarTrans.setFlowWorkTeam(this.viewFlowWorkTeam(flowInst.getFlowInstId()));
            nextNodes = viewRouterNextNodeInside(nextNode, flowVarTrans);
        }
        return nextNodes;
    }

    @Override
    public Set<String> viewNextNodeOperator(long nextNodeId,
                                            long curNodeInstId, String userCode, String unitCode, UserUnitVariableTranslate varTrans) {

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
        Set<String> optUsers = null;
        //判断是否为 结束节点 A:开始 B:首节点 C:一般 D:分支 E:汇聚 F结束
        NodeInfo nextNode = flowNodeDao.getObjectById(nextNodeId);
        //获取上一个相同节点实例机构
        String oldNodeInstUnitCode = null;
        NodeInstance oldNodeInst = flowInst.findLastSameNodeInst(nextNodeId, nodeInst, -1l);
        if (oldNodeInst != null)
            oldNodeInstUnitCode = oldNodeInst.getUnitCode();

        //调用机构引擎来计算 unitCode
        Map<String, Set<String>> unitParams = UserUnitParamBuilder.createEmptyParamMap();
        UserUnitParamBuilder.addParamToParamMap(unitParams, "L", oldNodeInstUnitCode);
        UserUnitParamBuilder.addParamToParamMap(unitParams, "U",
            unitCode == null ? CodeRepositoryUtil.getUserInfoByCode(userCode).getPrimaryUnit() : unitCode);
        UserUnitParamBuilder.addParamToParamMap(unitParams, "P", nodeInst.getUnitCode());
        UserUnitParamBuilder.addParamToParamMap(unitParams, "F", flowInst.getUnitCode());

        String nextNodeUnit =
            UserUnitCalcEngine.calcSingleUnitByExp(userUnitFilterCalcContext, nextNode.getUnitExp(), unitParams, null);


        FlowVariableTranslate flowVarTrans = new FlowVariableTranslate(varTrans,
            flowVariableDao.listFlowVariables(flowInst.getFlowInstId()), nodeInst, flowInst);

        //判断是否为子流程 A:一般 B:抢先机制 C:多人操作 S:子流程
        if (!"S".equals(nextNode.getOptType())) {
            optUsers = calcNodeOpterators(flowInst, nodeInst, nodeInst.getRunToken(),
                nextNode, userCode, nextNodeUnit,
                flowVarTrans);
        }

        return optUsers;
    }

    @Override
    public String getTaskGrantor(long nodeInstId, String userCode) {
        return actionTaskDao.getTaskGrantor(nodeInstId, userCode);
    }

    @Override
    public void recordActionLog(long nodeInstId, String userCode,
                                String actionType) {

        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null)
            return;

        String sGrantor = actionTaskDao.getTaskGrantor(nodeInstId, userCode);
        if (sGrantor == null) {
            logger.error("用户没有权限操作该节点：" + userCode + " -- " + nodeInstId);
            throw new WorkflowException(WorkflowException.WithoutPermission, "用户没有权限操作该节点：" + userCode + " -- " + nodeInstId);
        }

        if ("C".equals(actionType))
            nodeInst.setLastUpdateTime(new Date(System.currentTimeMillis()));
        ActionLog wfActionLog = FlowOptUtils.createActionLog(actionType, userCode, nodeInstId);
        wfActionLog.setActionId(actionLogDao.getNextActionId());
        if (!sGrantor.equals(userCode))
            wfActionLog.setGrantor(sGrantor);

        nodeInst.addWfActionLog(wfActionLog);
        nodeInstanceDao.updateObject(nodeInst);
    }


    /**
     * 加签,并指定到人
     * <p>
     * 用户手动创建一个节点实例，当前节点实例挂起，等这个新建的节点实例运行完提交时，当前节点实例继续运行.
     * 同一个节点可以创建多个前置节点，当所有的前置节点都执行提交后，现有的节点才被唤醒
     *
     * @param flowInstId    流程实例号
     * @param curNodeInstId 当前节点实例号
     * @param nodeId        节点号
     * @param createUser    当前创建用户
     * @param userCode      指定操作用户
     * @param unitCode      指定机构
     * @return 节点实例
     */
    public NodeInstance createPrepNodeInstLockUser(long flowInstId, long curNodeInstId,
                                                   long nodeId, String createUser, String userCode, String unitCode) {
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
        FlowInstance flowInst = flowInstanceDao.getObjectById(nodeInst.getFlowInstId());
        if (flowInst == null) {
            logger.error("找不到流程实例：" + nodeInst.getFlowInstId());
            return null;
        }

        FlowInfo flowInfo = flowDefDao.getFlowDefineByID(flowInst.getFlowCode(), flowInst.getVersion());

        NodeInfo nextNode = flowNodeDao.getObjectById(nodeId);
        //获取上一个相同节点实例机构

        long lastNodeInstId = nodeInstanceDao.getNextNodeInstId();

        NodeInstance nextNodeInst = FlowOptUtils.createNodeInst(
            unitCode, createUser, null, flowInst, nodeInst, flowInfo, nextNode, null);

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
     * 加签,并指定到人
     * <p>
     * 用户手动创建一个节点实例，当前节点实例挂起，等这个新建的节点实例运行完提交时，当前节点实例继续运行.
     * 同一个节点可以创建多个前置节点，当所有的前置节点都执行提交后，现有的节点才被唤醒
     *
     * @param flowInstId    流程实例号
     * @param curNodeInstId 当前节点实例号
     * @param nodeCode      节点号
     * @param createUser    当前创建用户
     * @param userCode      指定操作用户
     * @param unitCode      指定机构
     * @return 节点实例
     */
    public NodeInstance createPrepNodeInstLockUser(long flowInstId, long curNodeInstId,
                                                   String nodeCode, String createUser, String userCode, String unitCode) {

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

        return createPrepNodeInstLockUser(flowInstId, curNodeInstId,
            nodeList.get(0).getNodeId(), createUser, userCode, unitCode);
    }


    /**
     * 用户手动创建一个节点实例，当前节点实例挂起，等这个新建的节点实例运行完提交时，当前节点实例继续运行.
     * 同一个节点可以创建多个前置节点，当所有的前置节点都执行提交后，现有的节点才被唤醒
     *
     * @param flowInstId    流程实例号
     * @param curNodeInstId 当前节点实例号
     * @param nodeId        节点号
     * @param userCode      指定用户
     * @param unitCode      指定机构
     * @return 节点实例
     */
    @Override
    public NodeInstance createPrepNodeInst(long flowInstId, long curNodeInstId,
                                           long nodeId, String userCode, String unitCode) {

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
        FlowInstance flowInst = flowInstanceDao.getObjectById(nodeInst.getFlowInstId());
        if (flowInst == null) {
            logger.error("找不到流程实例：" + nodeInst.getFlowInstId());
            return null;
        }

        FlowInfo flowInfo = flowDefDao.getFlowDefineByID(flowInst.getFlowCode(), flowInst.getVersion());

        NodeInfo nextNode = flowNodeDao.getObjectById(nodeId);
        //获取上一个相同节点实例机构

        long lastNodeInstId = nodeInstanceDao.getNextNodeInstId();

        NodeInstance nextNodeInst = FlowOptUtils.createNodeInst(unitCode, userCode, null, flowInst, nodeInst, flowInfo, nextNode, null);

        nextNodeInst.setNodeInstId(lastNodeInstId);
        nextNodeInst.setPrevNodeInstId(curNodeInstId);
        nextNodeInst.setRunToken("L" + nodeInst.getRunToken());//设置为插入前置节点
        nextNodeInst.setTransPath("");

        //等待前置节点提交
        nodeInst.setNodeState("S");

        nodeInstanceDao.saveNewObject(nextNodeInst);
        nodeInstanceDao.updateObject(nodeInst);
        return nextNodeInst;
    }

    /**
     * 用户手动创建一个节点实例，当前节点实例挂起，等这个新建的节点实例运行完提交时，当前节点实例继续运行.
     * 同一个节点可以创建多个前置节点，当所有的前置节点都执行提交后，现有的节点才被唤醒
     *
     * @param flowInstId    流程实例号
     * @param curNodeInstId 当前节点实例号
     * @param nodeCode      节点环节代码，这个节点在这个流程中必需唯一
     * @param userCode      指定用户
     * @param unitCode      指定机构
     * @return 节点实例
     */
    public NodeInstance createPrepNodeInst(long flowInstId, long curNodeInstId,
                                           String nodeCode, String userCode, String unitCode) {

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

        return createPrepNodeInst(flowInstId, curNodeInstId,
            nodeList.get(0).getNodeId(), userCode, unitCode);
    }


    /**
     * 创建孤立节点  知会、关注
     * <p>
     * 用户手动创建一个节点实例，当前节点实例挂起，等这个新建的节点实例运行完提交时，当前节点实例继续运行.
     * 同一个节点可以创建多个前置节点，当所有的前置节点都执行提交后，现有的节点才被唤醒
     *
     * @param flowInstId    流程实例号
     * @param curNodeInstId 当前节点实例号
     * @param nodeId        节点号
     * @param userCode      指定用户
     * @param unitCode      指定机构
     * @return 节点实例
     */
    @Override
    public NodeInstance createIsolatedNodeInst(long flowInstId, long curNodeInstId,
                                               long nodeId, String createUser, String userCode, String unitCode) {

        NodeInstance nodeInst = nodeInstanceDao.getObjectById(curNodeInstId);
        //必需存在且状态为正常 或者 暂停
        if (nodeInst == null
               /* || (!"N".equals(nodeInst.getNodeState())
                        && !"S".equals(nodeInst.getNodeState()) )*/) {
            logger.error("找不到节点实例：" + curNodeInstId);
            return null;
        }

        FlowInstance flowInst = flowInstanceDao.getObjectById(nodeInst.getFlowInstId());
        if (flowInst == null) {
            logger.error("找不到流程实例：" + nodeInst.getFlowInstId());
            return null;
        }

        FlowInfo flowInfo = flowDefDao.getFlowDefineByID(flowInst.getFlowCode(), flowInst.getVersion());

        NodeInfo nextNode = flowNodeDao.getObjectById(nodeId);
        //获取上一个相同节点实例机构

        long lastNodeInstId = nodeInstanceDao.getNextNodeInstId();

        NodeInstance nextNodeInst = FlowOptUtils.createNodeInst(unitCode, createUser, null, flowInst, nodeInst, flowInfo, nextNode, null);

        nextNodeInst.setNodeInstId(lastNodeInstId);
        nextNodeInst.setPrevNodeInstId(curNodeInstId);
        nextNodeInst.setRunToken("R" + nodeInst.getRunToken());//设置为游离节点
        nextNodeInst.setUserCode(userCode);
        nextNodeInst.setTaskAssigned("S");
        nextNodeInst.setTransPath("");

        nodeInstanceDao.saveNewObject(nextNodeInst);

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
     * @param unitCode      指定机构
     * @return 节点实例
     */
    public NodeInstance createIsolatedNodeInst(long flowInstId, long curNodeInstId,
                                               String nodeCode, String createUser, String userCode, String unitCode) {

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

        return createIsolatedNodeInst(flowInstId, curNodeInstId,
            nodeList.get(0).getNodeId(), createUser, userCode, unitCode);
    }

    @Override
    public void assignFlowWorkTeam(long flowInstId, String roleCode, String userCode) {
        Date assignDate = new Date(System.currentTimeMillis());
        flowTeamDao.mergeObject(new FlowWorkTeam(flowInstId, userCode, roleCode, assignDate));
    }

    @Override
    public void assignFlowWorkTeam(long flowInstId, String roleCode,
                                   List<String> userCodeSet) {
        Date assignDate = new Date(System.currentTimeMillis());
        if (userCodeSet != null)
            for (String usercode : userCodeSet)
                if (StringUtils.isNotBlank(usercode)) {
                    flowTeamDao.mergeObject(new FlowWorkTeam(flowInstId, usercode, roleCode, assignDate));
                }

    }

    @Override
    public void assignFlowWorkTeam(long flowInstId, String roleCode, String userCode, String authdesc) {
        Date assignDate = new Date(System.currentTimeMillis());
        FlowWorkTeam team = new FlowWorkTeam(flowInstId, userCode, roleCode, assignDate);
        team.setAuthDesc(authdesc);
        flowTeamDao.mergeObject(team);
    }

    @Override
    public void assignFlowWorkTeam(long flowInstId, String roleCode,
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

    public void deleteFlowWorkTeam(long flowInstId, String roleCode, String userCode) {
        flowTeamDao.deleteObjectById(new FlowWorkTeamId(flowInstId, userCode, roleCode));
    }

    @Override
    public void deleteFlowWorkTeam(long flowInstId, String roleCode) {
        flowTeamDao.deleteFlowWorkTeam(flowInstId, roleCode);
    }

    @Override
    public Map<String, List<String>> viewFlowWorkTeam(long flowInstId) {
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
    public List<String> viewFlowWorkTeam(long flowInstId, String roleCode) {
        List<FlowWorkTeam> users = flowTeamDao.listFlowWorkTeamByRole(flowInstId, roleCode);
        List<String> us = new ArrayList<>();
        for (FlowWorkTeam user : users) {
            us.add(user.getUserCode());
        }
        return us;
    }

    @Transactional
    public List<FlowWorkTeam> viewFlowWorkTeamList(long flowInstId, String roleCode) {
        List<FlowWorkTeam> users = flowTeamDao.listFlowWorkTeamByRole(flowInstId, roleCode);
        return users;
    }

    public List<FlowWorkTeam> viewFlowWorkTeamList(long flowInstId, String roleCode, String authdesc) {
        return flowTeamDao.listFlowWorkTeam(flowInstId, roleCode, authdesc);
    }

    public FlowVariable viewNodeVariable(long flowInstId, String runToken,
                                         String varname) {
        FlowVariableId id = new FlowVariableId(flowInstId, runToken,
            varname);
        return flowVariableDao.getObjectById(id);
    }


    /**
     * 在任务列表中指定工作人员，这样就屏蔽了按照角色自动查找符合权限的人员
     */
    @Override
    public long assignNodeTask(long nodeInstId, String userCode,
                               String mangerUserCode, Date expiretime, String authDesc) {
        NodeInstance node = nodeInstanceDao.getObjectById(nodeInstId);
        if (node == null)
            return -1;
        // * T: 通过 tasklist 分配， D：通过 岗位、行政角色 自动匹配 S：静态代办（usercode)
        Set<ActionTask> taskList = node.getWfActionTasks();
        //如果只有一个 人，放在 wf_node_instance 表中的user_code
        if ((taskList != null && taskList.size() > 1) ||
            (taskList != null && taskList.size() == 1 &&
                userCode != null && !userCode.equals(taskList.iterator().next().getUserCode()))) {
            node.setTaskAssigned("T");
            for (ActionTask task : taskList) {
                if ("T".equals(task.getIsValid())
                    && userCode.equals(task.getUserCode()))
                    return -2;
            }

            ActionTask task = FlowOptUtils.createActionTask(nodeInstId,
                userCode);
            task.setTaskId(actionTaskDao.getNextTaskId());
            task.setExpireTime(expiretime);
            task.setAuthDesc(authDesc);
            node.addWfActionTask(task);
        } else {//taskList 为空 或者 新加入的用户已经存在情况
            node.setTaskAssigned("S");
            node.setUserCode(userCode);
        }
        nodeInstanceDao.saveNewObject(node);

 /*       WfManageAction managerAct = FlowOptUtils.createManagerAction(
                node.getFlowInstId(), userCode, "A");
        managerAct.setActionId(manageActionDao.getNextManageId());
        managerAct.setAdminDesc(authDesc);
        manageActionDao.saveObject(managerAct);*/
        return 0;
    }

    /**
     * 收回任务分配
     */
    @Override
    public int disableNodeTask(long nodeInstId, String userCode,
                               String mangerUserCode) {

        NodeInstance node = nodeInstanceDao.getObjectById(nodeInstId);
        if (node == null)
            return -1;
        ActionTask assignedTask = null;
        Set<ActionTask> taskList = node.getWfActionTasks();
        int atc = 0;
        for (ActionTask task : taskList) {
            if ("T".equals(task.getIsValid())
                && "A".equals(task.getTaskState()))// 只能禁用未完成的任务
            {
                if (userCode.equals(task.getUserCode()))
                    assignedTask = task;
                else
                    atc++;
            }
        }

        if (assignedTask == null)
            return -3;
        assignedTask.setIsValid("F");
        node.setTaskAssigned(atc > 0 ? "T" : "D");
        nodeInstanceDao.updateObject(node);

/*        WfManageAction managerAct = FlowOptUtils.createManagerAction(
                node.getFlowInstId(), mangerUserCode, "P");
        managerAct.setActionId(manageActionDao.getNextManageId());
        managerAct.setAdminDesc("node:" + nodeInstId + " user:" + userCode);
        manageActionDao.saveObject(managerAct);*/
        return 0;
    }


    /**
     * 删除任务节点
     */
    @Override
    public int deleteNodeTask(long nodeInstId, String userCode,
                              String mangerUserCode) {
        NodeInstance node = nodeInstanceDao.getObjectById(nodeInstId);
        if (node == null)
            return -1;
        ActionTask assignedTask = null;
        Set<ActionTask> taskList = node.getWfActionTasks();
        int atc = 0;
        for (ActionTask task : taskList) {
            if ("T".equals(task.getIsValid())
                && "A".equals(task.getTaskState()))// 只能禁用未完成的任务
            {
                if (userCode.equals(task.getUserCode()))
                    assignedTask = task;
                else
                    atc++;
            }
        }

        if (assignedTask == null)
            return -3;

        node.removeWfActionTask(assignedTask);
        node.setTaskAssigned(atc > 0 ? "T" : "D");
        nodeInstanceDao.updateObject(node);

/*        WfManageAction managerAct = FlowOptUtils.createManagerAction(
                node.getFlowInstId(), mangerUserCode, "D");
        managerAct.setActionId(manageActionDao.getNextManageId());
        managerAct.setAdminDesc("node:" + nodeInstId + " user:" + userCode);
        manageActionDao.saveObject(managerAct);*/
        return 0;
    }

    @Override
    public int deleteNodeAllTask(long nodeInstId, String mangerUserCode) {
        NodeInstance node = nodeInstanceDao.getObjectById(nodeInstId);
        if (node == null)
            return -1;

        Set<ActionTask> taskList = node.getWfActionTasks();
        for (ActionTask task : taskList) {
            if ("T".equals(task.getIsValid())
                && "A".equals(task.getTaskState()))// 只能禁用未完成的任务
            {
                node.removeWfActionTask(task);
            }
        }
        node.setTaskAssigned("D");
        nodeInstanceDao.updateObject(node);

/*        WfManageAction managerAct = FlowOptUtils.createManagerAction(
                node.getFlowInstId(), mangerUserCode, "D");
        managerAct.setActionId(manageActionDao.getNextManageId());
        managerAct.setAdminDesc("node:" + nodeInstId + " user:" + userCode);
        manageActionDao.saveObject(managerAct);*/
        return 0;
    }


    @Override
    public void assignFlowOrganize(long flowInstId, String roleCode,
                                   String unitCode) {
        Date assignDate = new Date(System.currentTimeMillis());
        FlowOrganize dbObj = flowOrganizeDao.getObjectById(new FlowOrganizeId(flowInstId, unitCode, roleCode));
        if (dbObj == null || StringBaseOpt.isNvl(dbObj.getUnitCode()))
            flowOrganizeDao.mergeObject(new FlowOrganize(flowInstId, unitCode, roleCode, assignDate));

    }

    @Override
    public void assignFlowOrganize(long flowInstId, String roleCode,
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
    public void assignFlowOrganize(long flowInstId, String roleCode,
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
    public void assignFlowOrganize(long flowInstId, String roleCode,
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
    public void deleteFlowOrganize(long flowInstId, String roleCode,
                                   String unitCode) {
        flowOrganizeDao.deleteObjectById(new FlowOrganizeId(flowInstId, unitCode, roleCode));

    }

    @Override
    public void deleteFlowOrganize(long flowInstId, String roleCode) {
        flowOrganizeDao.deleteFlowOrganize(flowInstId, roleCode);
    }

    @Override
    public void deleteFlowOrganizeByAuth(long flowInstId, String roleCode, String authDesc) {
        flowOrganizeDao.deleteFlowOrganize(flowInstId, roleCode, authDesc);
    }

    @Override
    public Map<String, List<String>> viewFlowOrganize(long flowInstId) {
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
    public List<String> viewFlowOrganize(long flowInstId, String roleCode) {
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
    public List<FlowOrganize> viewFlowOrganizeList(long flowInstId, String roleCode) {
        return new ArrayList<FlowOrganize>(
            flowOrganizeDao.listFlowOrganizeByRole(flowInstId, roleCode));
    }

    @Override
    public List<FlowOrganize> viewFlowOrganizeList(long flowInstId, String roleCode, String authDesc) {
        return new ArrayList<FlowOrganize>(
            flowOrganizeDao.listFlowOrganize(flowInstId, roleCode, authDesc));
    }

    @Override
    public void saveFlowVariable(long flowInstId, String sVar, String sValue) {
        if (StringBaseOpt.isNvl(sValue)) {
            flowVariableDao.deleteObjectById(new FlowVariableId(flowInstId, "A", sVar));
        } else {
            FlowVariableId cid = new FlowVariableId(flowInstId, "A", sVar);
            FlowVariable varO = flowVariableDao.getObjectById(cid);
            if (varO == null) {
                varO = new FlowVariable(flowInstId, "A", sVar, sValue, "S");
                flowVariableDao.saveNewObject(varO);
            } else {
                varO.setVarValue(sValue);
                flowVariableDao.updateObject(varO);
            }
        }
    }

    /**
     * 设置流程节点上下文变量
     *
     * @param flowInstId
     * @param runToken
     * @param sVar
     * @param sValue
     */
    @Override
    public void saveFlowNodeVariable(long flowInstId, String runToken, String sVar, String sValue) {

        if (StringBaseOpt.isNvl(sValue)) {
            flowVariableDao.deleteObjectById(new FlowVariableId(flowInstId,
                runToken, sVar));
            return;
        }
        FlowVariableId cid = new FlowVariableId(flowInstId,
            runToken, sVar);
        FlowVariable varO = flowVariableDao.getObjectById(cid);
        if (varO == null) {
            varO = new FlowVariable(flowInstId,
                runToken, sVar, sValue, "S");
            flowVariableDao.saveNewObject(varO);
        } else {
            varO.setVarValue(sValue);
            flowVariableDao.updateObject(varO);
        }
    }


    @Override
    public void saveFlowNodeVariable(long nodeInstId, String sVar, String sValue) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null) {
            logger.error("找不到节点实例：" + nodeInstId);
            return;
        }
        String nodeToken = nodeInst.getRunToken();
        saveFlowNodeVariable(nodeInst.getFlowInstId(), nodeToken, sVar, sValue);
    }

    @Override
    public void saveFlowVariable(long flowInstId, String sVar, Set<String> sValues) {
        if (sValues == null || sValues.size() == 0) {
            flowVariableDao.deleteObjectById(new FlowVariableId(flowInstId, "A", sVar));
        } else {
            FlowVariable varO = new FlowVariable(flowInstId, "A", sVar, FlowVariable.stringsetToString(sValues), "E");
            flowVariableDao.mergeObject(varO);
        }
    }

    @Override
    public void saveFlowNodeVariable(long nodeInstId, String sVar, Set<String> sValues) {
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
    public List<FlowVariable> listFlowVariables(long flowInstId) {
        List<FlowVariable> lv = flowVariableDao.listFlowVariables(flowInstId);
        if (lv == null)
            lv = new ArrayList<FlowVariable>();
        return new ArrayList<FlowVariable>(lv);
    }

    public void setFlowInstanceDao(FlowInstanceDao flowInstanceDao) {
        this.flowInstanceDao = flowInstanceDao;
    }

    public void setNodeInstanceDao(NodeInstanceDao nodeInstanceDao) {
        this.nodeInstanceDao = nodeInstanceDao;
    }

    public void setFlowNodeDao(NodeInfoDao flowNodeDao) {
        this.flowNodeDao = flowNodeDao;
    }

    public void setFlowTransitionDao(FlowTransitionDao flowTransitionDao) {
        this.flowTransitionDao = flowTransitionDao;
    }

    public void setActionTaskDao(ActionTaskDao actionTaskDao) {
        this.actionTaskDao = actionTaskDao;
    }

    public void setActionLogDao(ActionLogDao actionLogDao) {
        this.actionLogDao = actionLogDao;
    }

    public void setFlowDefDao(FlowInfoDao flowDefDao) {
        this.flowDefDao = flowDefDao;
    }

    public void setFlowTeamDao(FlowWorkTeamDao flowTeamDao) {
        this.flowTeamDao = flowTeamDao;
    }

    public void setFlowOrganizeDao(FlowOrganizeDao flowOrganizeDao) {
        this.flowOrganizeDao = flowOrganizeDao;
    }

    public void setFlowVariableDao(FlowVariableDao flowVariableDao) {
        this.flowVariableDao = flowVariableDao;
    }

    public void setFlowAttentionDao(InstAttentionDao flowAttentionDao) {
        this.attentionDao = flowAttentionDao;
    }

    public void setRuntimeWarningDao(FlowWarningDao warningDao) {
        this.runtimeWarningDao = warningDao;
    }

    @Override
    public void saveFlowAttention(long flowInstId, String attUser, String optUser) {
        InstAttention attObj = new InstAttention(attUser, flowInstId, DatetimeOpt.currentUtilDate(), optUser);
        attentionDao.mergeObject(attObj);
    }

    @Override
    public void deleteFlowAttention(long flowInstId, String attUser) {
        attentionDao.deleteObjectById(new InstAttentionId(attUser, flowInstId));
    }


    /**
     * 删除流程关注人员
     *
     * @param flowInstId
     * @param optUser    关注设置人员
     */
    public void deleteFlowAttentionByOptUser(long flowInstId, String optUser) {
        attentionDao.deleteFlowAttentionByOptUser(flowInstId, optUser);
    }

    @Override
    public void deleteFlowAttention(long flowInstId) {
        attentionDao.deleteFlowAttention(flowInstId);
    }


    /**
     * 获取流程关注人员
     *
     * @param flowInstId
     * @return
     */
    @Override
    public List<InstAttention> viewFlowAttention(long flowInstId) {

        List<InstAttention> attentions = attentionDao.listAttentionByFlowInstId(flowInstId);
        return attentions;
    }

    /**
     * @param flowInstId
     * @param userCode   关注人员
     * @return
     */
    @Override
    public InstAttention getFlowAttention(long flowInstId, String userCode) {
        return attentionDao.getObjectById(
            new InstAttentionId(userCode, flowInstId));
    }


    /**
     * 返回所有关在的项目
     *
     * @param userCode  关注人
     * @param instState N 正常  C 完成   P 暂停 挂起     F 强行结束  A 所有
     * @return
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

    public List<FlowVariable> viewFlowVariablesByVarname(long flowInstId,
                                                         String varname) {
        List<FlowVariable> lv = flowVariableDao.viewFlowVariablesByVarname(
            flowInstId, varname);
        if (lv == null)
            return null;
        return new ArrayList<FlowVariable>(lv);
    }

    @Override
    public List<UserTask> listUserTasks(String userCode, PageDesc pageDesc) {
        List<UserTask> taskList = actionTaskDao.listUserTaskByFilter(
            QueryUtils.createSqlParamsMap("userCode", userCode), pageDesc);
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

    @Override
    public boolean canAccess(long nodeInstId, String userCode) {
        if (userCode == null)
            return false;
        return actionTaskDao.hasOptPower(nodeInstId, userCode, null);
    }

    /**
     * 这个方法很低效，如果有必要可以通过添加视图来优化这个方法
     */
   /* @Override
    public List<NodeInstance> listUserCompleteTasks(
            Map<String, Object> filterMap, PageDesc pageDesc, String userCode) {
        //添加用户参与的过滤条件
        filterMap.put("lastUpdateUser", userCode);

        List<NodeInstance> tempList = nodeInstanceDao.listObjects(filterMap,
                pageDesc);
        List<NodeInstance> temp = new ArrayList<>();
        if (tempList != null) {

            for (NodeInstance nodeInst : tempList) {
                FlowInstance flowInst = flowInstanceDao
                        .getObjectById(nodeInst.getFlowInstId());
                if (flowInst == null) {
                    continue;
                }
                nodeInst.setFlowOptName(flowInst.getFlowOptName());
                nodeInst.setFlowOptTag(flowInst.getFlowOptTag());

                NodeInfo wfNode = flowNodeDao.getObjectById(nodeInst.getNodeId());
                if (wfNode == null) {
                    continue;
                }
                nodeInst.setNodeName(wfNode.getNodeName());

                // 判断节点任务是否提供回收：a.下一步办理人为当前操作人员的;b.下一节点已经被处理过的
                if (nodeCanBeReclaim(nodeInst.getNodeInstId())
                        && !canAccess(nodeInst.getNodeInstId(), userCode)) {
                    nodeInst.setIsRecycle("yes");
                    temp.add(nodeInst);
                } else {
                    nodeInst.setIsRecycle("no");
                }
            }

            return temp;
        }
        return new ArrayList<>();
    }*/
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
    public List<FlowWarning> listFlowWarningByInst(Long flowInstId,
                                                   PageDesc pageDesc) {
        return new ArrayList<FlowWarning>(
            runtimeWarningDao.listFlowWarningByInst(flowInstId, pageDesc));
    }

    @Override
    public List<FlowWarning> listFlowWarningByNodeInst(Long nodeInstId,
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

  /*  @Override
    public void saveOptIdeaForAutoSubmit(Map<String,Object> paraMap) {
        nodeInstanceDao.saveOptIdeaForAutoSubmit(paraMap);
    }*/
}
