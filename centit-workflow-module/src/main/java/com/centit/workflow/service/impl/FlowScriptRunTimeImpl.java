package com.centit.workflow.service.impl;

import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.basedata.NoticeMessage;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.GeneralAlgorithm;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.LeftRightPair;
import com.centit.support.compiler.Pretreatment;
import com.centit.support.compiler.VariableFormula;
import com.centit.workflow.dao.FlowInstanceDao;
import com.centit.workflow.dao.NodeInstanceDao;
import com.centit.workflow.po.FlowEventInfo;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInstance;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.FlowEventService;
import com.centit.workflow.service.FlowManager;
import com.centit.workflow.service.FlowScriptRunTime;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@Transactional
public class FlowScriptRunTimeImpl implements FlowScriptRunTime {

    @Autowired
    FlowEngine flowEngine;

    @Autowired
    private FlowManager flowManager;

    @Autowired
    private FlowInstanceDao flowInstanceDao;

    @Autowired
    private NodeInstanceDao nodeInstanceDao;

    @Autowired
    private NotificationCenter notificationCenter;

    @Autowired
    private FlowEventService flowEventService;

    private boolean beginFunction(VariableFormula formula){
        String currWord = formula.skipAWord();
        return "(".equals(currWord);
    }

    private boolean endFunction(VariableFormula formula){
        return formula.seekToRightBracket();
    }

    private Object getAFunctionParam(VariableFormula formula){
        Object object =  formula.calcFormula();

        String separatorString = formula.skipAWord();
        if(!",".equals(separatorString)) {
            formula.writeBackAWord(separatorString);
        }
        return object;
    }

    private List<Object> getAllFunctionParams(VariableFormula formula){
        Object object =  formula.calcFormula();
        String separatorString = formula.skipAWord();
        List<Object> params = new ArrayList<>(8);
        params.add(object);
        while(",".equals(separatorString)) {
            object =  formula.calcFormula();
            params.add(object);
            separatorString = formula.skipAWord();
        }
        formula.writeBackAWord(separatorString);
        return params;
    }

    /**
     * 运行流程脚本，流程脚本包括以下几个函数
     * setValue(字符串常量 name, 表达式 formula); // 计算变量
     * saveValue(字符串常量 name, 表达式 formula); // 计算变量，并保存到流程的变量表中
     * saveSuperValue(字符串常量 name, 表达式 formula); // 计算变量，并保存到流程的上级分支的变量表中
     * saveGlobalValue(字符串常量 name, 表达式 formula); // 计算变量，并保存到流程的全局变量表中
     * setNextOptUser(表达式 user); // 设置下一个交互节点的操作人员
     * closeNodes(字符串常量 nodeCode); //根据环节代码关闭节点
     * closeAllIsolatedNodes();// 关闭所有游离节点
     * closeAllOtherNodes(); // 关闭所有其他节点，非本节点全部关闭
     * deleteFlowWorkTeam(字符串常量 roleCode);//清除角色
     * assignFlowWorkTeam(字符串常量 roleCode, 表达式 users);// 设置办件角色
     * deleteFlowOrganize(字符串常量 roleCode);// 清除机构组
     * assignFlowOrganize(字符串常量 roleCode, 表达式 units);// 设置办件机构
     * runIf(condition, functionA , functionB) // 条件控制语句
     * @param script  执行的脚本
     * @param flowInst 流程实例
     * @param nodeInst 节点实例
     * @param varTrans 变量
     * @return 所有变量列表
     */
    @Override
    public Map<String, Object> runFlowScript(String script, FlowInstance flowInst, NodeInstance nodeInst,
                                             FlowVariableTranslate varTrans) {
        VariableFormula formula = new VariableFormula();
        Map<String, Object> retValueMap = new HashMap<>(16);
        formula.setFormula(script);
        formula.setTrans(varTrans);

        //Function<Object[], Object> func =  (arObjs) -> this::closeAllIsolatedNodes(flowInst) ;

        while (true) {
            LeftRightPair<String, Object> value = runWorkflowFunction(flowInst, nodeInst,
                formula, varTrans);
            if (value != null) {
                retValueMap.put(value.getLeft(), value.getRight());
            }

            String separatorString = formula.skipAWord();
            while (StringUtils.equalsAny(separatorString, ",", ";","(", ")")) {
                separatorString = formula.skipAWord();
            }
            if (StringUtils.isBlank(separatorString)) {
                break;
            }
            formula.writeBackAWord(separatorString);
        }
        return retValueMap;

    }

    public LeftRightPair<String, Object> runWorkflowFunction(FlowInstance flowInst, NodeInstance nodeInst,
                                                             VariableFormula formula, FlowVariableTranslate varTrans){
        String currWord = formula.skipAWord();
        if(StringUtils.isBlank(currWord)) {
            return null;
        }
        //currWord = currWord.toLowerCase(Locale.ROOT);
            //String funcName = currWord.toLowerCase();
        switch (currWord){
            case "setValue":
            {
                if(!beginFunction(formula)){
                    return null;
                }
                Object name = getAFunctionParam(formula);
                Object value = getAFunctionParam(formula);
                endFunction(formula);
                if(name !=null && value !=null) {
                    varTrans.setInnerVariable(StringBaseOpt.castObjectToString(name), nodeInst.getRunToken(), value);
                    return new LeftRightPair<>(StringBaseOpt.castObjectToString(name),  value);
                }
            }
                break;
            case "saveValue":
            {
                if(!beginFunction(formula)){
                    return null;
                }
                Object name = getAFunctionParam(formula);
                Object value = getAFunctionParam(formula);
                endFunction(formula);
                if(name !=null && value !=null) {
                    LeftRightPair<String, Object> retPair = new LeftRightPair<>(StringBaseOpt.castObjectToString(name),  value);
                    varTrans.setInnerVariable(retPair.getLeft(), nodeInst.getRunToken(), value);
                    flowEngine.saveFlowNodeVariable(nodeInst.getFlowInstId(), nodeInst.getRunToken(),
                        retPair.getLeft(),  value);
                    return retPair;
                }

            }
                break;

            case "saveGlobalValue":
            {
                if(!beginFunction(formula)){
                    return null;
                }
                Object name = getAFunctionParam(formula);
                Object value = getAFunctionParam(formula);
                endFunction(formula);
                if(name !=null && value !=null) {
                    LeftRightPair<String, Object> retPair = new LeftRightPair<>(StringBaseOpt.castObjectToString(name),  value);
                    varTrans.setInnerVariable(retPair.getLeft(), NodeInstance.TOP_RUNTIME_TOKEN, value);
                    flowEngine.saveFlowNodeVariable(nodeInst.getFlowInstId(), NodeInstance.TOP_RUNTIME_TOKEN,
                        retPair.getLeft(),  value);
                    return retPair;
                }
            }
            break;

            case "saveSuperValue":
            {
                if(!beginFunction(formula)){
                    return null;
                }
                Object name = getAFunctionParam(formula);
                Object value = getAFunctionParam(formula);
                endFunction(formula);
                if(name !=null && value !=null) {
                    String token = NodeInstance.calcSuperToken(nodeInst.getRunToken());
                    if(StringUtils.isBlank(token)){
                        token = NodeInstance.TOP_RUNTIME_TOKEN;
                    }
                    LeftRightPair<String, Object> retPair = new LeftRightPair<>(StringBaseOpt.castObjectToString(name),  value);
                    varTrans.setInnerVariable(retPair.getLeft(), token, value);
                    flowEngine.saveFlowNodeVariable(nodeInst.getFlowInstId(), token,
                        retPair.getLeft(),  value);
                    return retPair;
                }
            }
            break;

            case "setNextOptUser": {
                if (!beginFunction(formula)) {
                    return null;
                }
                Object param = getAFunctionParam(formula);
                endFunction(formula);
                if (param != null) {
                    String lockedUser =
                        StringBaseOpt.castObjectToString(param);
                    if (StringUtils.isNotBlank(lockedUser)) {
                        return new LeftRightPair<>("_lock_user", lockedUser);
                    }
                }
            }
                break;
            case "closeNodes": { //closeNodes(nodeCode); //根据环节代码关闭节点
                if(!beginFunction(formula)){
                    return null;
                }
                Object param = getAFunctionParam(formula);
                endFunction(formula);
                if (param!=null) {
                    String nodecode = StringBaseOpt.castObjectToString(param);
                    if(StringUtils.isNotBlank(nodecode)) {
                        closeNodes(nodecode, flowInst);
                    }
                }
            }
                break;
            case "closeAllIsolatedNodes": //closeAllIsolatedNodes();// 关闭所有游离节点
                if(!beginFunction(formula)){
                    return null;
                }
                endFunction(formula);
                closeAllIsolatedNodes(flowInst);
                break;

            case "closeAllOtherNodes": //closeAllOtherNodes(); // 关闭所有其他节点，非本节点全部关闭
                if(!beginFunction(formula)){
                    return null;
                }
                endFunction(formula);
                closeAllOtherNodes(flowInst, nodeInst);
                break;

            case "deleteFlowWorkTeam":{
                if(!beginFunction(formula)){
                    return null;
                }
                Object param = getAFunctionParam(formula);
                endFunction(formula);
                if (param!=null) {
                    String roleCode = StringBaseOpt.castObjectToString(param);
                    if(StringUtils.isNotBlank(roleCode)){
                        flowEngine.deleteFlowWorkTeam(flowInst.getFlowInstId(), roleCode);
                    }
                }
                break;
            }

            case "assignFlowWorkTeam"://setFlowTeam(roleCode, users);// 设置办件角色
            {
                if(!beginFunction(formula)){
                    return null;
                }
                Object name = getAFunctionParam(formula);
                Object value = getAFunctionParam(formula);
                endFunction(formula);
                if (name == null || value==null) {
                    break;
                }
                String roleCode = StringBaseOpt.castObjectToString(name);
                if(StringUtils.isNotBlank(roleCode)) {
                    flowEngine.assignFlowWorkTeam(flowInst.getFlowInstId(), roleCode,
                        nodeInst.getRunToken(),
                        StringBaseOpt.objectToStringList(value));
                }
            }
                break;

            case "deleteFlowOrganize":{
                if(!beginFunction(formula)){
                    return null;
                }
                Object param = getAFunctionParam(formula);
                endFunction(formula);
                if (param!=null) {
                    String roleCode = StringBaseOpt.castObjectToString(param);
                    if(StringUtils.isNotBlank(roleCode)){
                        flowEngine.deleteFlowOrganize(flowInst.getFlowInstId(), roleCode);
                    }
                }
                break;
            }

            case "assignFlowOrganize"://setFlowOrganize(roleCode, units);// 设置办件机构
            {
                if(!beginFunction(formula)){
                    return null;
                }
                Object name = getAFunctionParam(formula);
                Object value = getAFunctionParam(formula);
                endFunction(formula);
                if (name == null || value==null) {
                    break;
                }
                String roleCode = StringBaseOpt.castObjectToString(name);
                if(StringUtils.isNotBlank(roleCode)) {
                    flowEngine.assignFlowOrganize(flowInst.getFlowInstId(), roleCode,
                        StringBaseOpt.objectToStringList(value), "来自脚本引擎的授权");
                }
            }
                break;

            case "event":{ //发送流程消息
                if(!beginFunction(formula)){
                    return null;
                }
                List<Object> params =  getAllFunctionParams(formula);
                endFunction(formula);
                if(params.size()<1){
                    return null;
                }
                FlowEventInfo eventInfo = new FlowEventInfo();
                eventInfo.setFlowInstId(flowInst.getFlowInstId());
                eventInfo.setSenderUser(GeneralAlgorithm.nvl(nodeInst.getUserCode(), "system"));
                eventInfo.setEventName(StringBaseOpt.castObjectToString(params.get(0)));
                if(params.size()>1) {
                    eventInfo.setEventParam(StringBaseOpt.castObjectToString(params.get(1)));
                }
                flowEventService.saveNewEvent(eventInfo);

                break;
            }

            case "parentEvent":{ //发送流程消息
                if(!beginFunction(formula)){
                    return null;
                }
                List<Object> params =  getAllFunctionParams(formula);
                endFunction(formula);
                if(params.size()<1){
                    return null;
                }
                FlowEventInfo eventInfo = new FlowEventInfo();
                eventInfo.setFlowInstId(flowInst.getPreInstId());
                eventInfo.setSenderUser(GeneralAlgorithm.nvl(nodeInst.getUserCode(), "system"));
                eventInfo.setEventName(StringBaseOpt.castObjectToString(params.get(0)));
                if(params.size()>1) {
                    eventInfo.setEventParam(StringBaseOpt.castObjectToString(params.get(1)));
                }
                flowEventService.saveNewEvent(eventInfo);

                break;
            }

            case "sendMessage":{ //发送通知消息
                if(!beginFunction(formula)){
                    return null;
                }
                List<Object> params =  getAllFunctionParams(formula);
                endFunction(formula);
                if(params.size()<3){
                    return null;
                }
                notificationCenter.sendMessage("system", StringBaseOpt.objectToStringList(params.get(0)),
                    NoticeMessage.create().operation("workflow").method("submit").subject(
                        StringBaseOpt.castObjectToString(params.get(1)))
                        .content(
                            Pretreatment.mapTemplateString(StringBaseOpt.castObjectToString(params.get(2)), varTrans)));

                break;
            }

            case "suspendTimer": //暂停计时
                if (!beginFunction(formula)) {
                    return null;
                }
                endFunction(formula);
                flowManager.suspendFlowInstTimer(flowInst.getFlowInstId(),
                    GeneralAlgorithm.nvl(nodeInst.getUserCode(), "system"));
                /*flowInstanceDao.updateFlowTimerState(flowInst.getFlowInstId(),
                    FlowInstance.FLOW_TIMER_STATE_SUSPEND, GeneralAlgorithm.nvl(nodeInst.getUserCode(), "system"));

                OperationLog managerAct = FlowOptUtils.createActionLog(
                    GeneralAlgorithm.nvl(nodeInst.getUserCode(), "system"), flowInst.getFlowInstId(),
                    "来自节点" + nodeInst.getNodeInstId() + "脚本的暂停流程计时: " + flowInst.getFlowInstId());
                OperationLogCenter.log(managerAct);*/
                break;

            case "activizeTimer": //恢复计时
                if (!beginFunction(formula)) {
                    return null;
                }
                endFunction(formula);
                flowManager.activizeFlowInstTimer(flowInst.getFlowInstId(),
                    GeneralAlgorithm.nvl(nodeInst.getUserCode(), "system"));
                /*flowInstanceDao.updateFlowTimerState(flowInst.getFlowInstId(),
                    FlowInstance.FLOW_TIMER_STATE_RUN, GeneralAlgorithm.nvl(nodeInst.getUserCode(), "system"));

                OperationLog managerAct = FlowOptUtils.createActionLog(
                    GeneralAlgorithm.nvl(nodeInst.getUserCode(), "system"), flowInst.getFlowInstId(),
                    "来自节点" + nodeInst.getNodeInstId() + "脚本的恢复流程计时: " + flowInst.getFlowInstId());
                OperationLogCenter.log(managerAct);*/
                break;

            case "resetTimeLimit":
                if (!beginFunction(formula)) {
                    return null;
                }
                Object param = getAFunctionParam(formula);
                endFunction(formula);
                String timeLimt = StringBaseOpt.castObjectToString(param);
                if(StringUtils.isNotBlank(timeLimt)){
                    flowManager.resetFlowTimelimt(flowInst.getFlowInstId(),
                        StringBaseOpt.castObjectToString(param),
                        GeneralAlgorithm.nvl(nodeInst.getUserCode(), "system"),
                        "来自自动运行节点的重置"+nodeInst.getNodeInstId());
                }

            break;
            case "runIf":{
                if(!beginFunction(formula)){
                    return null;
                }
                Object condition = getAFunctionParam(formula);
                if(BooleanBaseOpt.castObjectToBoolean(condition, false)){
                    LeftRightPair<String, Object> trueRet =
                        runWorkflowFunction( flowInst,  nodeInst, formula,  varTrans);
                    endFunction(formula);
                    return trueRet;
                } else {
                    formula.skipAOperand();
                    String aWord = formula.skipAWord();
                    if(",".equals(aWord)) {
                        LeftRightPair<String, Object> falseRet =
                            runWorkflowFunction(flowInst, nodeInst, formula, varTrans);
                        endFunction(formula);
                        return falseRet;
                    } // else ")".equals(aWord);
                }
            }
            break;

            default:
                break;
        }

        return null;
    }

    private void closeNodeInstanceInside(NodeInstance ni) {

        if ("W".equals(ni.getNodeState())) { //结束子流程
            FlowInstance subFlowInst = flowInstanceDao.getObjectById(ni.getSubFlowInstId());
            if (subFlowInst != null) {
                FlowOptUtils.endInstance(subFlowInst, "F", "system", flowInstanceDao);
            }
        }
        ni.setNodeState(NodeInstance.NODE_STATE_FORCE);// 节点设置为无效
        ni.setLastUpdateTime(DatetimeOpt.currentUtilDate());
        ni.setLastUpdateUser("system");
        nodeInstanceDao.updateObject(ni);
    }

    private void closeNodes(String nodeCode, FlowInstance flowInst) {
        if(flowInst.getFlowNodeInstances().size() == 0){
            flowInstanceDao.fetchObjectReference(flowInst, "flowNodeInstances");
        }
        Set<NodeInstance> activeNodes = flowInst.getActiveNodeInstances();
        for(NodeInstance ni : activeNodes){
            String nc = ni.getNodeCode();
            if(nc!=null) {
                nodeInstanceDao.fetchObjectReference(ni, "node");
                nc = ni.getNodeCode();
            }
            if(StringUtils.equals(nodeCode, nc)){
                closeNodeInstanceInside(ni);
            }
        }
    }

    public Integer  closeAllIsolatedNodes(FlowInstance flowInst) {
        if(flowInst.getFlowNodeInstances().size() == 0){
            flowInstanceDao.fetchObjectReference(flowInst, "flowNodeInstances");
        }
        Set<NodeInstance> activeNodes = flowInst.getActiveNodeInstances();
        for(NodeInstance ni : activeNodes){
            if (ni.getRunToken().contains(NodeInstance.RUN_TOKEN_ISOLATED)) {
                closeNodeInstanceInside(ni);
            }
        }
        return 1;
    }

    private void closeAllOtherNodes( FlowInstance flowInst, NodeInstance nodeInst) {
        if(flowInst.getFlowNodeInstances().size() == 0){
            flowInstanceDao.fetchObjectReference(flowInst, "flowNodeInstances");
        }
        Set<NodeInstance> activeNodes = flowInst.getActiveNodeInstances();
        for(NodeInstance ni : activeNodes){
            if (! StringUtils.equals(ni.getRunToken(), nodeInst.getRunToken())) {
                closeNodeInstanceInside(ni);
            }
        }
    }
}
