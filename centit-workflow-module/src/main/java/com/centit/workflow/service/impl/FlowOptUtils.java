package com.centit.workflow.service.impl;

import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.WorkTimeSpan;
import com.centit.workflow.dao.FlowInstanceDao;
import com.centit.workflow.dao.FlowVariableDao;
import com.centit.workflow.po.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FlowOptUtils {
    //private static final Logger logger = LoggerFactory.getLogger(FlowOptUtils.class);

    /**
     * 创建流程实例
     */
    public static FlowInstance createFlowInst(String unitcode, String usercode, FlowInfo wf, String flowInstId, String timeLimitStr) {
        FlowInstance flowInst = new FlowInstance();
        flowInst.setFlowInstId(flowInstId);
        flowInst.setFlowCode(wf.getFlowCode());
        flowInst.setVersion(wf.getVersion());
        flowInst.setUnitCode(unitcode);
        flowInst.setUserCode(usercode);
        flowInst.setPreNodeInstId("");
        flowInst.setPreInstId("");
        flowInst.setIsSubInst("N");
        flowInst.setInstState("N");
        flowInst.setCreateTime(new Date(System.currentTimeMillis()));
        String timeLimit = wf.getTimeLimit();
        if (StringUtils.isNotBlank(timeLimitStr)) {
            timeLimit = timeLimitStr;
        }
        flowInst.setIsTimer("F");
        // 创建 环节实例
        for (FlowStage wfStage : wf.getFlowStages()) {
            StageInstance stageInst = flowInst.newFlowStageInstance();
            stageInst.setFlowInstId(flowInstId);
            stageInst.setStageCode(wfStage.getStageCode());
            stageInst.setStageId(wfStage.getStageId());
            stageInst.setPromiseTime(new WorkTimeSpan(timeLimit).toNumber());
            stageInst.setStageBegin("0");
            stageInst.setTimeLimit(stageInst.getPromiseTime());
            flowInst.addFlowStageInstance(stageInst);
        }

        if (!StringBaseOpt.isNvl(timeLimit)) {
            // 不计时N、计时T(有期限)、暂停P  忽略(无期限) F
            flowInst.setIsTimer("T");
            flowInst.setTimeLimit(new WorkTimeSpan(timeLimit).toNumber());
        } else
            flowInst.setTimeLimit(null);

        flowInst.setPromiseTime(flowInst.getTimeLimit());
//        flowInst.setExpireTime(new Date(System.currentTimeMillis()+1000*60*60*24));
        return flowInst;
    }

    public static void setNewNodeInstTimelimit(NodeInstance nodeInst, String timeLimit,
                                               FlowInstance flowInst, NodeInstance preNodeInst, FlowInfo flowInfo, NodeInfo node) {

        if ("1".equals(node.getInheritType())) {
            if (preNodeInst != null && preNodeInst.getTimeLimit() != null) {
                nodeInst.setTimeLimit(new WorkTimeSpan(timeLimit).toNumber() +
                    preNodeInst.getTimeLimit());
            } else {
                nodeInst.setTimeLimit(new WorkTimeSpan(timeLimit).toNumber());
            }
        } else if ("2".equals(node.getInheritType())) {
            //flowInst.
            Set<NodeInfo> nodes = flowInfo.listNodesByNodeCode(node.getInheritNodeCode());
            NodeInstance inhertInst = null;
            for (NodeInfo thisNode : nodes) {
                NodeInstance tempInst = flowInst.findLastSameNodeInst
                    (thisNode.getNodeId(), nodeInst, nodeInst.getNodeInstId());
                if (inhertInst == null || inhertInst.getCreateTime().before(tempInst.getCreateTime()))//大小于
                    inhertInst = tempInst;
            }

            if (inhertInst == null)
                nodeInst.setTimeLimit(new WorkTimeSpan(timeLimit).toNumber());
            else
                nodeInst.setTimeLimit(new WorkTimeSpan(timeLimit).toNumber() + inhertInst.getTimeLimit());
        } else
            nodeInst.setTimeLimit(new WorkTimeSpan(timeLimit).toNumber());
    }

    /**
     * 创建节点实例
     * 同时生产创建节点日志
     * N 正常  B 已回退    C 完成   F被强制结束
     * P 暂停   W 等待子流程返回   S 等等前置节点（可能是多个）完成
     */
    public static NodeInstance createNodeInst(String unitcode, String usercode,
                                              FlowInstance flowInst, NodeInstance preNodeInst,
                                              FlowInfo flowInfo, NodeInfo node, FlowTransition trans) {
        NodeInstance nodeInst = new NodeInstance();
        nodeInst.setFlowInstId(flowInst.getFlowInstId());
        Date updateTime = DatetimeOpt.currentUtilDate();
        nodeInst.setNodeId(node.getNodeId());
        nodeInst.setUnitCode(unitcode);
        nodeInst.setUserCode(usercode);
        //nodeInst.setNodeParam(nodeParam);
        nodeInst.setNodeState("N");
        //nodeInst.setIsTimer(isTimer);
        nodeInst.setTaskAssigned("S");
        //给一个默认的令牌 T
        nodeInst.setRunToken("T");
        nodeInst.setLastUpdateUser(usercode);
        nodeInst.setCreateTime(updateTime);
        nodeInst.setRoleCode(node.getRoleCode());
        nodeInst.setRoleType(node.getRoleType());
        nodeInst.setFlowStage(node.getStageCode());
        //计算节点的期限
        nodeInst.setIsTimer(node.getIsAccountTime());
        nodeInst.setTimeLimit(0L);
        //计算节点的期限
        //I ： 未设置（ignore 默认 ）、N 无 (无期限 none ) 、 F 每实例固定期限 fix 、C 节点固定期限  cycle。
        String timeLimit, timeLimitType;
        if (trans == null || "I".equals(trans.getLimitType())) {
            timeLimit = node.getTimeLimit();
            timeLimitType = node.getLimitType();
        } else {
            timeLimit = trans.getTimeLimit();
            timeLimitType = trans.getLimitType();
        }
        if ("C".equals(timeLimitType)) {
            NodeInstance sameInst = flowInst.findLastSameNodeInst(nodeInst.getNodeId(), nodeInst, nodeInst.getNodeInstId());
            if (sameInst != null)
                nodeInst.setTimeLimit(sameInst.getTimeLimit());
            else {
                setNewNodeInstTimelimit(nodeInst, timeLimit,
                    flowInst, preNodeInst, flowInfo, node);
            }
        } else if ("F".equals(timeLimitType)) {
            //nodeInst.setTimeLimit( new WorkTimeSpan(timeLimit).toNumber() );
            setNewNodeInstTimelimit(nodeInst, timeLimit,
                flowInst, preNodeInst, flowInfo, node);
        }
        nodeInst.setPromiseTime(nodeInst.getTimeLimit());
        //nodeInst.setLastUpdateTime(updateTime);
        return nodeInst;
    }

    /**
     * 创建任务实例
     */
    public static ActionTask createActionTask(String nodeInstId, String usercode) {
        ActionTask task = new ActionTask();
        task.setNodeInstId(nodeInstId);
        task.setIsValid("T");
        task.setAssignTime(new Date(System.currentTimeMillis()));
        task.setUserCode(usercode);
        task.setTaskState("A");
        return task;
    }

    /**
     * 创建任务实例
     */
    public static ActionTask createActionTask(String usercode,
                                              NodeInstance nodeInst, NodeInfo node) {
        ActionTask actionTask = createActionTask(nodeInst.getNodeInstId(), usercode);
        actionTask.setRoleType(node.getRoleType());
        actionTask.setRoleCode(node.getRoleCode());
        //actionTask.setIsvalid("T");
        //actionTask.setExpireTime(new Date(System.currentTimeMillis()+1000*60*60*480));
        return actionTask;
    }

     /**
      * @param actType
             "S" state 流程状态变更
             "N" node state 节点状态变更
             "M" manager 流程流传管理  强行回退、强行提交、重新运行、从当前节点运行
             "T" timer 流程计时管理
             "A" attribute 流程属性维护
             "V" variable 流程变量维护， 包括流程机构和 办件角色变更
             "J" job 流程任务维护
             "L" log 流程正常 创建、提交操作
     * @param usercode 操作用户
     * @param flowInstId 流程实例ID
     * @param logDetail 操作说明
     * @return ActionLog
     */
     public static ActionLog createActionLog(String actType, String usercode,
                                             String flowInstId, String logDetail) {
         ActionLog actionLog = new ActionLog();
         actionLog.setFlowInstId(flowInstId);
         actionLog.setActionTime(DatetimeOpt.currentUtilDate());
         actionLog.setActionType(actType);
         actionLog.setUserCode(usercode);
         actionLog.setLogDetail(logDetail);
         return actionLog;
     }
    /**
     * @param actType
    "S" state 流程状态变更
    "N" node state 节点状态变更
    "M" manager 流程流传管理  强行回退、强行提交、重新运行、从当前节点运行
    "T" timer 流程计时管理
    "A" attribute 流程属性维护
    "V" variable 流程变量维护， 包括流程机构和 办件角色变更
    "J" job 流程任务维护
    "L" log 流程正常 创建、提交操作
     * @param usercode 操作用户
     * @param nodeInst 流程节点实例
     * @param node 节点信息
     * @param logDetail 操作说明
     * @return ActionLog
     */
    public static ActionLog createActionLog(String actType, String usercode,
                                            NodeInstance nodeInst, String logDetail, NodeInfo node) {
        ActionLog actionLog = createActionLog(actType, usercode,
            nodeInst.getFlowInstId(), logDetail);
        actionLog.setNodeInstId(nodeInst.getNodeInstId());
        if (node != null) {
            actionLog.setRoleType(node.getRoleType());
            actionLog.setRoleCode(node.getRoleCode());
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
                    //flowInstanceDao.updateObject(subFlowInst);
                }
            }
            //ni.setNodeState("F");//因为流程被强制结束而被强制结束
            //ni.setLastUpdateTime(updateTime);
            //ni.setLastUpdateUser(userCode);
            //}
        }
        flowInstanceDao.updateObject(flowInst);
    }

    public static FlowVariableTranslate createVariableTranslate(
            NodeInstance nodeInstance, FlowInstance flowInstance,
            FlowVariableDao flowVariableDao, FlowEngineImpl flowEngine) {

        FlowVariableTranslate flowVarTrans = new FlowVariableTranslate(nodeInstance, flowInstance);
        boolean hasFlowGroup = StringUtils.isNotBlank(flowInstance.getFlowGroupId());
        // 优先加载本流程的变量
        List<FlowVariable> flowVariables = flowVariableDao.listFlowVariables(flowInstance.getFlowInstId());
        // 如果有 流程组加载流程组变量
        if(hasFlowGroup) {
            if(null!=flowVariableDao.listFlowVariables(flowInstance.getFlowGroupId()))
              flowVariables.addAll(flowVariableDao.listFlowVariables(flowInstance.getFlowGroupId()));
        }
        // 加载变量的默认值
        if(null!=flowVariableDao.listFlowDefaultVariables(
            flowInstance.getFlowInstId(), flowInstance.getFlowCode(), flowInstance.getVersion()))
           flowVariables.addAll(flowVariableDao.listFlowDefaultVariables(
              flowInstance.getFlowInstId(), flowInstance.getFlowCode(), flowInstance.getVersion()));

        flowVarTrans.setFlowVariables(flowVariables);

        Map<String, List<String>> flowOrgs = flowEngine.viewFlowOrganize(flowInstance.getFlowInstId());
        if(hasFlowGroup) {
            Map<String, List<String>> tempOrgs = flowEngine.viewFlowOrganize(flowInstance.getFlowGroupId());
            if (null!=flowOrgs)
               tempOrgs.putAll(flowOrgs);
            flowOrgs = tempOrgs;
        }
        flowVarTrans.setFlowOrganizes(flowOrgs);

        Map<String, List<String>> flowTeams = flowEngine.viewFlowWorkTeam(flowInstance.getFlowInstId());
        if(hasFlowGroup) {
            Map<String, List<String>> tempTeams = flowEngine.viewFlowWorkTeam(flowInstance.getFlowGroupId());
            tempTeams.putAll(flowTeams);
            flowTeams = tempTeams;
        }
        flowVarTrans.setFlowWorkTeam(flowTeams);

        return flowVarTrans;
    }

}
