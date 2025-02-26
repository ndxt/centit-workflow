package com.centit.workflow.service.impl;

import com.centit.framework.components.SysUserFilterEngine;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.framework.model.basedata.*;
import com.centit.product.oa.service.WorkDayManager;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.DateTimeSpan;
import com.centit.support.common.ObjectException;
import com.centit.support.compiler.VariableFormula;
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

        if (StringUtils.isNotBlank(timeLimit) && !NodeInfo.TIME_EXPIRE_OPT_NONE.equals(wf.getExpireOpt())) {
            // 不计时F 、计时T(有期限)、暂停P  忽略(无期限) F
            flowInst.setTimerStatus(FlowWarning.TIMER_STATUS_RUN);
            Date today = DatetimeOpt.currentUtilDate();
            flowInst.setDeadlineTime(calcTimeLimit(topUnit, today, timeLimit, workDayManager, false));
            flowInst.setWarningTime(calcTimeLimit(topUnit,
                flowInst.getDeadlineTime(), wf.getWarningParam(), workDayManager, true));
        }

        return flowInst;
    }

    public static Date calcTimeLimit(String topUnit, Date  currentDate, String timeLimitStr,
                                              WorkDayManager workDayManager, boolean isMinus ) {
        if(StringUtils.isBlank(timeLimitStr)) {
            return currentDate;
        }
        String tlt = timeLimitStr.trim();
        if (tlt.startsWith("{") || tlt.startsWith("${")) {
            if(tlt.startsWith("{")){
                tlt = tlt.substring(1, tlt.length()-1);
            } else {
                tlt = tlt.substring(2, tlt.length()-1);
            }
            Object object = VariableFormula.calculate(tlt);
            return DatetimeOpt.castObjectToDate(object);
        }
        DateTimeSpan deadLine = new DateTimeSpan(tlt);
        if(isMinus){
            deadLine.setTimeSpan(0 - deadLine.getTimeSpan());
        }
        return workDayManager.calcWorkingDeadline(topUnit, currentDate, deadLine);

    }

    public static void setNewNodeInstTimelimit(NodeInstance nodeInst, String timeLimit,
                                               FlowInstance flowInst, NodeInstance preNodeInst,
                                               FlowInfo flowInfo, NodeInfo node,
                                               FlowVariableTranslate varTrans,
                                               WorkDayManager workDayManager) {
        String tlt = timeLimit;
        if(StringUtils.isNotBlank(tlt)) {
            if (tlt.startsWith("ref:")) {
                if(varTrans!=null){
                    tlt = StringBaseOpt.castObjectToString(varTrans.getVarValue(tlt.substring(4)));
                }
            }
        }
        DateTimeSpan extend = new DateTimeSpan();

        if (NodeInfo.TIME_LIMIT_INHERIT_LEAD.equals(node.getInheritType())) {
            if (preNodeInst != null // && !FlowWarning.TIMER_STATUS_NOLIMIT.equals(preNodeInst.getTimerStatus())
                && preNodeInst.getLastUpdateTime() != null && preNodeInst.getDeadlineTime() != null
                && preNodeInst.getLastUpdateTime().before(preNodeInst.getDeadlineTime())) {

                extend = DateTimeSpan.calcDateTimeSpan(
                        preNodeInst.getLastUpdateTime(), preNodeInst.getDeadlineTime());

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

                extend = DateTimeSpan.calcDateTimeSpan(
                    inhertInst.getLastUpdateTime(), inhertInst.getDeadlineTime());

            }
        }

        Date today = DatetimeOpt.currentUtilDate();
        nodeInst.setDeadlineTime(calcTimeLimit(flowInst.getTopUnit(), today, tlt, workDayManager, false));
        if(extend.isPositiveTimeSpan()){
            nodeInst.setDeadlineTime(workDayManager.calcWorkingDeadline(
                flowInst.getTopUnit(), nodeInst.getDeadlineTime(), extend));
        }
        nodeInst.setWarningTime(calcTimeLimit(flowInst.getTopUnit(),
            nodeInst.getDeadlineTime(), node.getWarningParam(), workDayManager, true));
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
        String timeLimit, timeLimitType;
        if (trans==null || trans.getLimitType() == null || NodeInfo.TIME_LIMIT_TYPE_IGNORE.equals(trans.getLimitType())) {
            timeLimit = node.getTimeLimit();
            timeLimitType = node.getLimitType();
        } else {
            timeLimit = trans.getTimeLimit();
            timeLimitType = trans.getLimitType();
        }

        if(StringUtils.equalsAny(timeLimitType, NodeInfo.TIME_LIMIT_TYPE_FIX, NodeInfo.TIME_LIMIT_TYPE_CYCLE)){
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
                //nodeInst.setTimeLimit( new DateTimeSpan(timeLimit).toNumber() );
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
     public static OperationLog createActionLog(String topUnit, String usercode,
                                             String flowInstId, String logDetail) {
         return OperationLog.create().topUnit(topUnit)
             .operation("workflow").tag(flowInstId)
             .user(usercode).content(logDetail);
     }
    /**
     * @param usercode 操作用户
     * @param nodeInst 流程节点实例
     * @param node 节点信息
     * @param logDetail 操作说明
     * @return ActionLog
     */
    public static OperationLog createActionLog(String topUnit, String usercode,
                                               NodeInstance nodeInst, String logDetail, NodeInfo node) {
        OperationLog actionLog = createActionLog(topUnit, usercode,
             nodeInst.getFlowInstId(), logDetail)
            .unit(nodeInst.getUnitCode())
            .oldObject(nodeInst.getNodeInstId());
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
        }
    }

    public static FlowVariableTranslate createVariableTranslate(
            NodeInstance nodeInstance, FlowInstanceDao flowInstanceDao, FlowInstance flowInstance,
            FlowVariableDao flowVariableDao, FlowEngineImpl flowEngine,
            FlowOptParamOptions options) {

        FlowVariableTranslate flowVarTrans = new FlowVariableTranslate(nodeInstance, flowInstance);
        List<String> flowInstPath = new ArrayList<>();
        FlowInstance tempInstance = flowInstance;
        while(tempInstance!=null) {
            flowInstPath.add(tempInstance.getFlowInstId());
            if(StringUtils.isBlank(tempInstance.getPreInstId())){
                tempInstance = null;
            }else{
                tempInstance = flowInstanceDao.getObjectById(tempInstance.getPreInstId());
            }
        }

        if(StringUtils.isNotBlank(flowInstance.getFlowGroupId())){
            flowInstPath.add(flowInstance.getFlowGroupId());
        }

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
                flowVarTrans.setInnerVariable(ent.getKey(), NodeInstance.RUN_TOKEN_GLOBAL, ent.getValue());
            }
        }
        List<FlowVariable> flowVariables = new ArrayList<>(64);
        for(int i = 0; i< flowInstPath.size(); i++) {
            List<FlowVariable> tempVariables = flowVariableDao.listFlowVariables(flowInstPath.get(i));
            if(tempVariables != null && !tempVariables.isEmpty()) {
                flowVariables.addAll(tempVariables);
            }
        }
        // 加载变量的默认值
        List<FlowVariable> defaultVariables = flowVariableDao.listFlowDefaultVariables(
            flowInstance.getFlowInstId(), flowInstance.getFlowCode(), flowInstance.getVersion());
        if(defaultVariables != null && !defaultVariables.isEmpty()){
            flowVariables.addAll(defaultVariables);
        }
        flowVarTrans.setFlowVariables(flowVariables);

        Map<String, List<String>> flowOrgs = new HashMap<>(64);
        for(int i = flowInstPath.size()-1; i>=0; i--){
            Map<String, List<String>> tempOrgs = flowEngine.viewFlowOrganize(flowInstPath.get(i));
            if (tempOrgs != null && !tempOrgs.isEmpty())
                flowOrgs.putAll(tempOrgs);
        }
        flowVarTrans.setFlowOrganizes(flowOrgs);

        List<FlowWorkTeam> flowTeams =  new ArrayList<>(64);
        for(int i = flowInstPath.size()-1; i>=0; i--){
            List<FlowWorkTeam> tempTeams = flowEngine.viewFlowWorkTeam(flowInstPath.get(i));
            if (null!=tempTeams)
                flowTeams.addAll(tempTeams);
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
