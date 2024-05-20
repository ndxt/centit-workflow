package com.centit.workflow.service.impl;

import com.centit.framework.components.SysUserFilterEngine;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.framework.model.basedata.*;
import com.centit.product.oa.service.WorkDayManager;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.ObjectException;
import com.centit.support.common.WorkTimeSpan;
import com.centit.workflow.commons.FlowOptParamOptions;
import com.centit.workflow.dao.FlowInstanceDao;
import com.centit.workflow.dao.FlowVariableDao;
import com.centit.workflow.po.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class FlowOptUtils {
    //private static final Logger logger = LoggerFactory.getLogger(FlowOptUtils.class);

    /**
     * 创建流程实例
     */
    public static FlowInstance createFlowInst(String topUnit, String unitcode, String usercode,
                                              FlowInfo wf, String flowInstId, String timeLimitStr,
                                              WorkDayManager workDayManager) {
        if(StringUtils.isBlank(topUnit)){
            throw new ObjectException(ObjectException.DATA_VALIDATE_ERROR, "topUnit属性为null无法创建流程实例，请和开发人员联系。");
        }
        FlowInstance flowInst = new FlowInstance();
        flowInst.setFlowInstId(flowInstId);
        flowInst.setFlowCode(wf.getFlowCode());
        flowInst.setVersion(wf.getVersion());
        flowInst.setOsId(wf.getOsId());
        flowInst.setOptId(wf.getOptId());
        flowInst.setUnitCode(unitcode);
        flowInst.setUserCode(usercode);
        flowInst.setPreNodeInstId("");
        flowInst.setTopUnit(topUnit);
        flowInst.setPreInstId("");
        flowInst.setIsSubInst(false);
        flowInst.setInstState(FlowInstance.FLOW_STATE_NORMAL);// "N"
        flowInst.setCreateTime(new Date(System.currentTimeMillis()));
        String timeLimit = wf.getTimeLimit();
        if (StringUtils.isNotBlank(timeLimitStr)) {
            timeLimit = timeLimitStr;
        }

        flowInst.setTimerStatus(FlowWarning.TIMER_STATUS_NO_LIMIT);
        // 创建 环节实例
        for (FlowStage wfStage : wf.getFlowStages()) {
            StageInstance stageInst = flowInst.newFlowStageInstance();
            stageInst.setFlowInstId(flowInstId);
            stageInst.setStageCode(wfStage.getStageCode());
            stageInst.setStageId(wfStage.getStageId());
            stageInst.setTimerStatus(FlowWarning.TIMER_STATUS_NOT_BEGIN);
            flowInst.addFlowStageInstance(stageInst);
        }

        if (StringUtils.isNotBlank(timeLimit) && !"O".equals(wf.getExpireOpt())) {
            // 不计时F 、计时T(有期限)、暂停P  忽略(无期限) F
            flowInst.setTimerStatus(FlowWarning.TIMER_STATUS_RUN);
            WorkTimeSpan deadLine= new WorkTimeSpan(timeLimit);
            Date today = DatetimeOpt.currentUtilDate();
            flowInst.setDeadlineTime(
                workDayManager.calcWorkingDeadline(topUnit, today, deadLine));
            deadLine.subtractWorkTimeSpan(new WorkTimeSpan(wf.getWarningParam()));
            flowInst.setWarningTime(workDayManager.calcWorkingDeadline(topUnit, today, deadLine));
        }

        return flowInst;
    }

    public static void setNewNodeInstTimelimit(NodeInstance nodeInst, String timeLimit,
                                               FlowInstance flowInst, NodeInstance preNodeInst,
                                               FlowInfo flowInfo, NodeInfo node,
                                               FlowVariableTranslate varTrans,
                                               WorkDayManager workDayManager) {
        WorkTimeSpan deadLine= new WorkTimeSpan();
        if(StringUtils.isNotBlank(timeLimit)) {
            String tlt = timeLimit.trim();
            if (tlt.startsWith("ref:")) {
                if(varTrans!=null){
                    tlt = StringBaseOpt.castObjectToString(varTrans.getVarValue(tlt.substring(4)));
                    deadLine = new WorkTimeSpan(tlt);
                }
            } else {
                deadLine = new WorkTimeSpan(tlt);
            }
        }
        if (NodeInfo.TIME_LIMIT_INHERIT_LEAD.equals(node.getInheritType())) {
            if (preNodeInst != null // && !FlowWarning.TIMER_STATUS_NOLIMIT.equals(preNodeInst.getTimerStatus())
                && preNodeInst.getLastUpdateTime() != null && preNodeInst.getDeadlineTime() != null
                && preNodeInst.getLastUpdateTime().before(preNodeInst.getDeadlineTime())) {

                deadLine.addWorkTimeSpan(WorkTimeSpan.calcWorkTimeSpan(
                        preNodeInst.getLastUpdateTime(), preNodeInst.getDeadlineTime()));

            }
        } else if (NodeInfo.TIME_LIMIT_INHERIT_ASSIGNED.equals(node.getInheritType())) {
            //flowInst.
            List<NodeInfo> nodes = flowInfo.listNodesByNodeCode(node.getInheritNodeCode());
            NodeInstance inhertInst = null;
            for (NodeInfo thisNode : nodes) {
                NodeInstance tempInst = flowInst.findLastSameNodeInst
                    (thisNode.getNodeId(), nodeInst, nodeInst.getNodeInstId());
                if (inhertInst == null || inhertInst.getCreateTime().before(tempInst.getCreateTime()))//大小于
                    inhertInst = tempInst;
            }

            if (inhertInst != null // && !FlowWarning.TIMER_STATUS_NOLIMIT.equals(inhertInst.getTimerStatus())
                && inhertInst.getLastUpdateTime() != null && inhertInst.getDeadlineTime() != null
                && inhertInst.getLastUpdateTime().before(inhertInst.getDeadlineTime())) {

                deadLine.addWorkTimeSpan(WorkTimeSpan.calcWorkTimeSpan(
                    inhertInst.getLastUpdateTime(), inhertInst.getDeadlineTime()));

            }
        }

        Date today = DatetimeOpt.currentUtilDate();
        nodeInst.setDeadlineTime(workDayManager.calcWorkingDeadline(flowInst.getTopUnit(), today, deadLine));
        deadLine.subtractWorkTimeSpan(new WorkTimeSpan(node.getWarningParam()));
        nodeInst.setWarningTime(workDayManager.calcWorkingDeadline(flowInst.getTopUnit(), today, deadLine));
    }

    /**
     * 创建节点实例
     * 同时生产创建节点日志
     * N 正常  B 已回退    C 完成   F被强制结束
     * P 暂停   W 等待子流程返回   S 等等前置节点（可能是多个）完成
     */
    public static NodeInstance createNodeInst(String unitcode, String createUser,
                                              FlowInstance flowInst, NodeInstance preNodeInst,
                                              FlowInfo flowInfo, NodeInfo node, FlowTransition trans,
                                              FlowVariableTranslate varTrans,
                                              WorkDayManager workDayManager) {
        NodeInstance nodeInst = new NodeInstance();
        nodeInst.setFlowInstId(flowInst.getFlowInstId());
        Date updateTime = DatetimeOpt.currentUtilDate();
        nodeInst.setNodeId(node.getNodeId());
        nodeInst.setUnitCode(unitcode);
        nodeInst.setNodeState(NodeInstance.NODE_STATE_NORMAL);
        nodeInst.setTaskAssigned(NodeInstance.TASK_ASSIGN_TYPE_STATIC);
        //给一个默认的令牌 T
        nodeInst.setRunToken(NodeInstance.RUN_TOKEN_GLOBAL);
        nodeInst.setLastUpdateUser(createUser);
        nodeInst.setCreateTime(updateTime);
        nodeInst.setRoleCode(node.getRoleCode());
        nodeInst.setRoleType(node.getRoleType());
        nodeInst.setStageCode(node.getStageCode());
        //计算节点的期限

        if(preNodeInst!=null) {
            nodeInst.setPrevNodeInstId(preNodeInst.getNodeInstId());
        }

        //计算节点的期限
        //I ： 未设置（ignore 默认 ）、N 无 (无期限 none ) 、 F 每实例固定期限 fix 、C 节点固定期限  cycle。
        // I 忽略，N 无，F 固定期限，C 循环
        String timeLimit, timeLimitType, isAccountTime;
        if (trans==null || trans.getLimitType() == null || NodeInfo.TIME_LIMIT_TYPE_IGNORE.equals(trans.getLimitType())) {
            timeLimit = node.getTimeLimit();
            timeLimitType = node.getLimitType();
            isAccountTime = node.getIsAccountTime();
        } else {
            timeLimit = trans.getTimeLimit();
            timeLimitType = trans.getLimitType();
            isAccountTime = trans.getIsAccountTime();
        }

        if(StringUtils.equalsAny(isAccountTime, NodeInfo.TIME_LIMIT_NORMAL, NodeInfo.TIME_LIMIT_ONLY_NODE)){
            nodeInst.setTimerStatus(FlowWarning.TIMER_STATUS_RUN);
            if (NodeInfo.TIME_LIMIT_TYPE_CYCLE.equals(timeLimitType)) {
                NodeInstance sameInst = flowInst.findLastSameNodeInst(nodeInst.getNodeId(), nodeInst, nodeInst.getNodeInstId());
                if (sameInst != null) {
                    nodeInst.setDeadlineTime(sameInst.getDeadlineTime());
                    nodeInst.setWarningTime(sameInst.getWarningTime());
                    // 是否需要重复预警
                    // nodeInst.setTimerStatus(sameInst.getTimerStatus());
                } else {
                    setNewNodeInstTimelimit(nodeInst, timeLimit,
                        flowInst, preNodeInst, flowInfo, node, varTrans, workDayManager);
                }
            } else if (NodeInfo.TIME_LIMIT_TYPE_FIX.equals(timeLimitType)) {
                //nodeInst.setTimeLimit( new WorkTimeSpan(timeLimit).toNumber() );
                setNewNodeInstTimelimit(nodeInst, timeLimit,
                    flowInst, preNodeInst, flowInfo, node, varTrans, workDayManager);
            }
        } else {
            nodeInst.setTimerStatus(FlowWarning.TIMER_STATUS_NO_LIMIT);
        }
        return nodeInst;
    }

     /**
     * @param usercode 操作用户
     * @param flowInstId 流程实例ID
     * @param logDetail 操作说明
     * @return ActionLog
     */
     public static OperationLog createActionLog(String usercode,
                                             String flowInstId, String logDetail) {
         return OperationLog.create()
             .operation("workflow").tag(flowInstId)
             .method("flowOpt").user(usercode).content(logDetail);
     }
    /**
     * @param usercode 操作用户
     * @param nodeInst 流程节点实例
     * @param node 节点信息
     * @param logDetail 操作说明
     * @return ActionLog
     */
    public static OperationLog createActionLog(String usercode,
                                               NodeInstance nodeInst, String logDetail, NodeInfo node) {
        OperationLog actionLog = createActionLog(usercode, nodeInst.getFlowInstId(), logDetail)
            .unit(nodeInst.getUnitCode())
            .method(nodeInst.getNodeInstId());
        if (node != null) {
            actionLog.setNewValue(usercode + ":" + node.getRoleType() + ":" + node.getRoleCode());
        }
        return actionLog;
    }

    /**
     * @param flowInst 流程实例
     * @param endType  C 完成 F强制结束
     */
    public static void endInstance(FlowInstance flowInst, String endType, String userCode, FlowInstanceDao flowInstanceDao) {
        flowInst.setInstState(endType);
        Date updateTime = DatetimeOpt.currentUtilDate();
        flowInst.setLastUpdateTime(updateTime);
        flowInst.setLastUpdateUser(userCode);
        for (NodeInstance ni : flowInst.getFlowNodeInstances()) {
            //if("N".equals(ni.getNodeState())||"P".equals(ni.getNodeState())
            //||"S".equals(ni.getNodeState())||"W".equals(ni.getNodeState())){
            if ("W".equals(ni.getNodeState())) { //结束子流程
                FlowInstance subFlowInst = flowInstanceDao.getObjectById(ni.getSubFlowInstId());
                if (subFlowInst != null && !"F,C".contains(subFlowInst.getInstState())) {
                    endInstance(subFlowInst, "F", userCode, flowInstanceDao);
                    flowInstanceDao.updateObject(subFlowInst);
                }
            }
            //ni.setNodeState("F");//因为流程被强制结束而被强制结束
            //ni.setLastUpdateTime(updateTime);
            //ni.setLastUpdateUser(userCode);
            //}
        }
    }

    public static FlowVariableTranslate createVariableTranslate(
            NodeInstance nodeInstance, FlowInstance flowInstance,
            FlowVariableDao flowVariableDao, FlowEngineImpl flowEngine,
            FlowOptParamOptions options) {

        FlowVariableTranslate flowVarTrans = new FlowVariableTranslate(nodeInstance, flowInstance);
        boolean hasFlowGroup = StringUtils.isNotBlank(flowInstance.getFlowGroupId());
        if (nodeInstance == null) {
            // 创建流程实例的时候，nodeInstance为null
            nodeInstance = new NodeInstance();
        }
        // 优先加载本流程的变量
        if(options != null && options.getVariables() != null && !options.getVariables().isEmpty()) {
            for(Map.Entry<String, Object> ent : options.getVariables().entrySet()) {
                flowVarTrans.setInnerVariable(ent.getKey(), nodeInstance.getRunToken(), ent.getValue());
            }
        }

        if(options != null && options.getGlobalVariables() != null && !options.getGlobalVariables().isEmpty()) {
            for(Map.Entry<String, Object> ent : options.getGlobalVariables().entrySet()) {
                flowVarTrans.setInnerVariable(ent.getKey(), nodeInstance.getRunToken(), ent.getValue());
            }
        }

        List<FlowVariable> flowVariables = flowVariableDao.listFlowVariables(flowInstance.getFlowInstId());
        // 如果有 流程组加载流程组变量
        if(hasFlowGroup) {
            List<FlowVariable> groupVariables = flowVariableDao.listFlowVariables(flowInstance.getFlowGroupId());
            if(flowVariables==null){
                flowVariables = groupVariables;
            } else {
                flowVariables.addAll(groupVariables);
            }
        }

        // 加载变量的默认值
        List<FlowVariable> defaultVariables = flowVariableDao.listFlowDefaultVariables(
            flowInstance.getFlowInstId(), flowInstance.getFlowCode(), flowInstance.getVersion());
        if(defaultVariables!=null){
            flowVariables.addAll(defaultVariables);
        }

        flowVarTrans.setFlowVariables(flowVariables);
        Map<String, List<String>> flowOrgs = flowEngine.viewFlowOrganize(flowInstance.getFlowInstId());
        if(hasFlowGroup) {
            Map<String, List<String>> tempOrgs = flowEngine.viewFlowOrganize(flowInstance.getFlowGroupId());
            if (null!=flowOrgs)
               tempOrgs.putAll(flowOrgs);
            flowOrgs = tempOrgs;
        }
        flowVarTrans.setFlowOrganizes(flowOrgs);

        List<FlowWorkTeam> flowTeams = flowEngine.viewFlowWorkTeam(flowInstance.getFlowInstId());
        if(flowTeams==null){
            flowTeams = new ArrayList<>();
        }
        if(hasFlowGroup) {
            List<FlowWorkTeam> tempTeams = flowEngine.viewFlowWorkTeam(flowInstance.getFlowGroupId());
            for(FlowWorkTeam team : tempTeams){
                team.setRunToken("T");
                flowTeams.add(team);
            }
        }
        flowVarTrans.setFlowWorkTeam(flowTeams);

        return flowVarTrans;
    }

    public static Map<String, Function<Object[], Object>> createExtendFuncMap(Supplier<UserUnitFilterCalcContext> supplierContent) {
        Map<String, Function<Object[], Object>> extendFuncs = new HashMap<>(12);
        extendFuncs.put(
            "userRank",
            (a) -> {
                UserUnitFilterCalcContext context = supplierContent.get();
                if (a == null || a.length < 1) {
                    return null;
                }
                return context.getUserRank(StringBaseOpt.castObjectToString(a[0]));
            }
        );
        extendFuncs.put(
            "userType",
            (a) -> {
                UserUnitFilterCalcContext context = supplierContent.get();
                if (a == null || a.length < 1) {
                    return null;
                }
                UserInfo ui = context.getUserInfoByCode(StringBaseOpt.castObjectToString(a[0]));
                if (ui == null) {
                    return "";
                }
                return ui.getUserType();
            }
        );
        extendFuncs.put(
            "unitType",
            (a) -> {
                UserUnitFilterCalcContext context = supplierContent.get();
                if (a == null || a.length < 1) {
                    return null;
                }
                UnitInfo ui = context.getUnitInfoByCode(StringBaseOpt.castObjectToString(a[0]));
                if (ui == null) {
                    return "";
                }
                return ui.getUnitType();
            }
        );

        extendFuncs.put(
            "userUnits",
            (a) -> {
                UserUnitFilterCalcContext context = supplierContent.get();
                if (a == null || a.length < 1) {
                    return null;
                }
                List<UserUnit> userUnits =
                    context.listUserUnits(StringBaseOpt.castObjectToString(a[0]));
                return CollectionsOpt.mapCollectionToSet(userUnits, UserUnit::getUnitCode);
            }
        );
        extendFuncs.put(
            "unitUsers",
            (a) -> {
                UserUnitFilterCalcContext context = supplierContent.get();
                if (a == null || a.length < 1) {
                    return null;
                }
                List<UserUnit> userUnits =
                    context.listUnitUsers(StringBaseOpt.castObjectToString(a[0]));
                return CollectionsOpt.mapCollectionToSet(userUnits, UserUnit::getUserCode);
            }
        );

        extendFuncs.put(
            "userRoles",
            (a) -> {
                UserUnitFilterCalcContext context = supplierContent.get();
                if (a == null || a.length < 1) {
                    return null;
                }
                String userCode = StringBaseOpt.castObjectToString(a[0]);
                String userType = SysUserFilterEngine.ROLE_TYPE_XZ;
                if (a.length > 1) {
                    userType = StringBaseOpt.castObjectToString(a[1]);
                }
                if (SysUserFilterEngine.ROLE_TYPE_SYSTEM.equals(userType)) {
                    List<UserRole> roles = context.listUserRoles(userCode);
                    return CollectionsOpt.mapCollectionToSet(roles, UserRole::getRoleCode);
                } else if (SysUserFilterEngine.ROLE_TYPE_GW.equals(userType)) {
                    List<UserUnit> userUnits = context.listUserUnits(userCode);
                    return CollectionsOpt.mapCollectionToSet(userUnits, UserUnit::getUserStation);
                } else {
                    List<UserUnit> userUnits = context.listUserUnits(userCode);
                    return CollectionsOpt.mapCollectionToSet(userUnits, UserUnit::getUserRank);
                }
            }
        );
        extendFuncs.put(
            "calcUnits",
            (a) -> {
                UserUnitFilterCalcContext context = supplierContent.get();
                if (a == null || a.length < 1) {
                    return null;
                }
                return UserUnitCalcEngine.calcUnitsByExp(context, StringBaseOpt.castObjectToString(a[0]));
            }
        );
        extendFuncs.put(
            "calcUsers",
            (a) -> {
                UserUnitFilterCalcContext context = supplierContent.get();
                if (a == null || a.length < 1) {
                    return null;
                }
                return UserUnitCalcEngine.calcOperators(context, StringBaseOpt.castObjectToString(a[0]));
            }
        );
        return extendFuncs;
    }

    public static Map<String, Function<Object[], Object>> createExtendFuncMap(UserUnitFilterCalcContext calcContext) {
        return createExtendFuncMap( () -> calcContext);
    }
}
