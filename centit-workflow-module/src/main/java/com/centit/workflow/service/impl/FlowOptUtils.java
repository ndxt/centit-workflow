package com.centit.workflow.service.impl;

import com.centit.framework.model.basedata.OperationLog;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.WorkTimeSpan;
import com.centit.workflow.commons.FlowOptParamOptions;
import com.centit.workflow.dao.FlowInstanceDao;
import com.centit.workflow.dao.FlowVariableDao;
import com.centit.workflow.po.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
        flowInst.setOsId(wf.getOsId());
        flowInst.setOptId(wf.getOptId());
        flowInst.setUnitCode(unitcode);
        flowInst.setUserCode(usercode);
        flowInst.setPreNodeInstId("");
        flowInst.setPreInstId("");
        flowInst.setIsSubInst(false);
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
            stageInst.setStageBegin("0");
            if(BooleanBaseOpt.castObjectToBoolean(wfStage.getIsAccountTime(), false)) {
                stageInst.setPromiseTime(new WorkTimeSpan(wfStage.getTimeLimit()).toNumberAsMinute());
                stageInst.setTimeLimit(stageInst.getPromiseTime());
            } else {
                stageInst.setPromiseTime(-1l);
                stageInst.setTimeLimit(1l);
            }
            flowInst.addFlowStageInstance(stageInst);
        }

        if (!StringBaseOpt.isNvl(timeLimit)) {
            // 不计时N、计时T(有期限)、暂停P  忽略(无期限) F
            flowInst.setIsTimer("T");
            flowInst.setTimeLimit(new WorkTimeSpan(timeLimit).toNumberAsMinute());
        } else
            flowInst.setTimeLimit(null);

        flowInst.setPromiseTime(flowInst.getTimeLimit());
//        flowInst.setExpireTime(new Date(System.currentTimeMillis()+1000*60*60*24));
        return flowInst;
    }

    public static void setNewNodeInstTimelimit(NodeInstance nodeInst, String timeLimit,
                                               FlowInstance flowInst, NodeInstance preNodeInst,
                                               FlowInfo flowInfo, NodeInfo node,
                                               FlowVariableTranslate varTrans) {
        long timeLimitInMinute = 0l;
        if(StringUtils.isNotBlank(timeLimit)) {
            String tlt = timeLimit.trim();
            if (tlt.startsWith("ref:")) {
                if(varTrans!=null){
                    tlt = StringBaseOpt.castObjectToString(varTrans.getVarValue(tlt.substring(4)));
                    timeLimitInMinute = new WorkTimeSpan(tlt).toNumberAsMinute();
                }
            } else {
                timeLimitInMinute = new WorkTimeSpan(tlt).toNumberAsMinute();
            }
        }
        if ("1".equals(node.getInheritType())) {
            if (preNodeInst != null && preNodeInst.getTimeLimit() != null) {
                nodeInst.setTimeLimit(timeLimitInMinute + preNodeInst.getTimeLimit());
            } else {
                nodeInst.setTimeLimit(timeLimitInMinute);
            }
        } else if ("2".equals(node.getInheritType())) {
            //flowInst.
            List<NodeInfo> nodes = flowInfo.listNodesByNodeCode(node.getInheritNodeCode());
            NodeInstance inhertInst = null;
            for (NodeInfo thisNode : nodes) {
                NodeInstance tempInst = flowInst.findLastSameNodeInst
                    (thisNode.getNodeId(), nodeInst, nodeInst.getNodeInstId());
                if (inhertInst == null || inhertInst.getCreateTime().before(tempInst.getCreateTime()))//大小于
                    inhertInst = tempInst;
            }

            if (inhertInst == null)
                nodeInst.setTimeLimit(timeLimitInMinute);
            else
                nodeInst.setTimeLimit(timeLimitInMinute + inhertInst.getTimeLimit());
        } else
            nodeInst.setTimeLimit(timeLimitInMinute);
    }

    /**
     * 创建节点实例
     * 同时生产创建节点日志
     * N 正常  B 已回退    C 完成   F被强制结束
     * P 暂停   W 等待子流程返回   S 等等前置节点（可能是多个）完成
     */
    public static NodeInstance createNodeInst(String unitcode, String usercode,
                                              FlowInstance flowInst, NodeInstance preNodeInst,
                                              FlowInfo flowInfo, NodeInfo node, FlowTransition trans,
                                              FlowVariableTranslate varTrans) {
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
        nodeInst.setRunToken(NodeInstance.RUN_TOKEN_GLOBAL);
        nodeInst.setLastUpdateUser(usercode);
        nodeInst.setCreateTime(updateTime);
        nodeInst.setRoleCode(node.getRoleCode());
        nodeInst.setRoleType(node.getRoleType());
        nodeInst.setFlowStage(node.getStageCode());
        //计算节点的期限
        nodeInst.setIsTimer(node.getIsAccountTime());
//        nodeInst.setTimeLimit(0L);
        nodeInst.setTimeLimit(new WorkTimeSpan(node.getTimeLimit()).toNumberAsMinute());
        if(preNodeInst!=null) {
            nodeInst.setPrevNodeInstId(preNodeInst.getNodeInstId());
        }
        //计算节点的期限
        //I ： 未设置（ignore 默认 ）、N 无 (无期限 none ) 、 F 每实例固定期限 fix 、C 节点固定期限  cycle。
        String timeLimit, timeLimitType;
        if (trans==null || trans.getLimitType() == null || "I".equals(trans.getLimitType())) {
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
                    flowInst, preNodeInst, flowInfo, node, varTrans);
            }
        } else if ("F".equals(timeLimitType)) {
            //nodeInst.setTimeLimit( new WorkTimeSpan(timeLimit).toNumber() );
            setNewNodeInstTimelimit(nodeInst, timeLimit,
                flowInst, preNodeInst, flowInfo, node, varTrans);
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
        task.setAssignTime(new Date(System.currentTimeMillis()));
        task.setUserCode(usercode);
        task.setTaskState("A");
        return task;
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
