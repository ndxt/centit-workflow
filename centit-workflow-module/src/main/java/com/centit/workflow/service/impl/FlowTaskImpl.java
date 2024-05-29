package com.centit.workflow.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.centit.dde.adapter.DdeDubboTaskRun;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.basedata.NoticeMessage;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.ObjectException;
import com.centit.workflow.commons.SubmitOptOptions;
import com.centit.workflow.dao.*;
import com.centit.workflow.po.*;
import com.centit.workflow.service.FlowEngine;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 工作流引擎定时检测任务期限，并发出相应的消息
 *
 * @author codefan
 * @create 2012-2-23
 */
@Component("flowTaskImpl")
public class FlowTaskImpl {

    private static final Logger logger = LoggerFactory.getLogger(FlowTaskImpl.class);

    @Autowired
    NodeInstanceDao nodeInstanceDao;

    @Autowired
    UserTaskListDao userTaskListDao;

    @Autowired
    private NodeInfoDao nodeInfoDao;

    @Autowired
    private FlowWarningDao wfRuntimeWarningDao;
    /**
     * 这个map需要更换会系统的通知中心
     */
    @Autowired
    private NotificationCenter notificationCenter;

    @Autowired
    private FlowInstanceDao flowInstanceDao;

    @Autowired
    private FlowInfoDao flowInfoDao;

    @Autowired
    private FlowEventDao flowEventDao;

    @Autowired
    private FlowEngine flowEngine;

    @Autowired
    private StageInstanceDao stageInstanceDao;

    @Autowired
    private FlowStageDao flowStageDao;

    @Autowired
    private DdeDubboTaskRun ddeDubboTaskRun;

    private void doNodeAlertWarning() {
        List<NodeInstance> activeNodes =  nodeInstanceDao.listWarningNodeInstance();
        if (activeNodes == null || activeNodes.isEmpty()) {
            return;
        }
        for (NodeInstance nodeInst : activeNodes){
            NodeInfo nodeInfo = nodeInfoDao.getObjectById(nodeInst.getNodeCode());
            if(!NodeInfo.TIME_EXPIRE_OPT_NONE.equals(nodeInfo.getExpireOpt())){
                FlowInstance flowInst = flowInstanceDao.getObjectById(nodeInst.getFlowInstId());

                String message = "业务" + flowInst.getFlowOptName() + "(" + flowInst.getFlowInstId() + ")的" +
                    nodeInfo.getNodeName() + "(" + nodeInst.getNodeInstId() + ") 节点超时预警，请尽快处理。";
                NoticeMessage noticeMessage = NoticeMessage.create().subject("节点预报警提示")
                    .content(message)
                    .operation("WF_WARNING").method("NOTIFY").tag(String.valueOf(nodeInst.getNodeInstId()));

                notificationCenter.sendMessage("system", nodeInst.getUserCode(), noticeMessage);

                FlowWarning flowWarning = new FlowWarning();
                flowWarning.setWarningType("W");
                flowWarning.setObjType("N");
                flowWarning.setFlowInstId(flowInst.getFlowInstId());
                flowWarning.setNodeInstId(nodeInst.getNodeInstId());
                flowWarning.setWarningTime(DatetimeOpt.currentUtilDate());
                flowWarning.setWarningMsg(message);
                flowWarning.setSendUsers(nodeInst.getUserCode());
                wfRuntimeWarningDao.saveNewObject(flowWarning);
            }
            if(NodeInfo.TIME_EXPIRE_OPT_NONE.equals(nodeInfo.getExpireOpt()) ||
                NodeInfo.TIME_EXPIRE_OPT_NOTIFY.equals(nodeInfo.getExpireOpt()) ){
                nodeInst.setTimerStatus("E");
            } else {
                nodeInst.setTimerStatus("W");
            }
            nodeInstanceDao.updtNodeTimerStatus(nodeInst.getNodeInstId(), nodeInst.getTimerStatus());
        }
    }

    private void doStageAlertWarning() {
        List<StageInstance> activeStages =  stageInstanceDao.listWarningStageInstance();
        if (activeStages == null || activeStages.isEmpty()) {
            return;
        }
        for (StageInstance stageInst : activeStages) {
            FlowStage stageInfo = flowStageDao.getObjectById(stageInst.getStageId());

            if (!NodeInfo.TIME_EXPIRE_OPT_NONE.equals(stageInfo.getExpireOpt())) {
                List<NodeInstance> activeNodes = nodeInstanceDao.listActiveTimerNodeByFlowStage(
                    stageInst.getFlowInstId(), stageInst.getStageCode());
                if (activeNodes != null && !activeNodes.isEmpty()) {
                    FlowInstance flowInst = flowInstanceDao.getObjectById(stageInst.getFlowInstId());
                    List<String> users = new ArrayList<>();
                    String message = "业务" + flowInst.getFlowOptName() + "(" + flowInst.getFlowInstId() + ") 的阶段" +
                        stageInfo.getStageName() + "(" + stageInfo.getStageCode() + ") 超时预警。";
                    for (NodeInstance nodeInst : activeNodes) {

                        NoticeMessage noticeMessage = NoticeMessage.create().subject("流程阶段预报警提示")
                            .content(message + "请尽快处理和您相关业务（" + nodeInst.getNodeInstId() + ")")
                            .operation("WF_WARNING").method("NOTIFY").tag(String.valueOf(nodeInst.getNodeInstId()));

                        notificationCenter.sendMessage("system", nodeInst.getUserCode(), noticeMessage);
                        users.add(nodeInst.getUserCode());
                    }

                    FlowWarning flowWarning = new FlowWarning();
                    flowWarning.setWarningType("W");
                    flowWarning.setObjType("P");//阶段
                    flowWarning.setFlowInstId(flowInst.getFlowInstId());
                    flowWarning.setFlowStage(stageInfo.getStageCode());
                    flowWarning.setWarningTime(DatetimeOpt.currentUtilDate());
                    flowWarning.setWarningMsg(message);
                    flowWarning.setSendUsers(StringBaseOpt.castObjectToString(users));
                    wfRuntimeWarningDao.saveNewObject(flowWarning);
                }
            }
            if (NodeInfo.TIME_EXPIRE_OPT_NONE.equals(stageInfo.getExpireOpt()) ||
                NodeInfo.TIME_EXPIRE_OPT_NOTIFY.equals(stageInfo.getExpireOpt())) {
                stageInst.setTimerStatus("E");
            } else {
                stageInst.setTimerStatus("W");
            }
            stageInstanceDao.updtStageTimerStatus(stageInst.getFlowInstId(), stageInst.getStageId(), stageInst.getTimerStatus());
        }
    }

    private void doFlowAlertWarning() {
        List<FlowInstance> activeFlows = flowInstanceDao.listWarningFLowInstance();
        if (activeFlows == null || activeFlows.isEmpty()) {
            return;
        }
        for (FlowInstance flowInst : activeFlows) {
            FlowInfo flowInfo = flowInfoDao.getFlowDefineByID(flowInst.getFlowCode(), flowInst.getVersion());
            if (!NodeInfo.TIME_EXPIRE_OPT_NONE.equals(flowInfo.getExpireOpt())) {
                List<String> users = new ArrayList<>();
                List<NodeInstance> activeNodes = nodeInstanceDao.listActiveTimerNodeByFlow(flowInst.getFlowInstId());
                if (activeNodes != null && !activeNodes.isEmpty()) {
                    String message = "业务" + flowInst.getFlowOptName() + "(" + flowInst.getFlowInstId() + ") 超时预警。";
                    for (NodeInstance nodeInst : activeNodes) {

                        NoticeMessage noticeMessage = NoticeMessage.create().subject("流程预报警提示")
                            .content(message + "请尽快处理和您相关业务（" + nodeInst.getNodeInstId()+ ")")
                            .operation("WF_WARNING").method("NOTIFY").tag(String.valueOf(nodeInst.getNodeInstId()));

                        notificationCenter.sendMessage("system", nodeInst.getUserCode(), noticeMessage);
                        users.add(nodeInst.getUserCode());
                    }

                    FlowWarning flowWarning = new FlowWarning();
                    flowWarning.setWarningType("W");
                    flowWarning.setObjType("F");//流程
                    flowWarning.setFlowInstId(flowInst.getFlowInstId());
                    flowWarning.setWarningTime(DatetimeOpt.currentUtilDate());
                    flowWarning.setWarningMsg(message);
                    flowWarning.setSendUsers(StringBaseOpt.castObjectToString(users));
                    wfRuntimeWarningDao.saveNewObject(flowWarning);
                }
                if (NodeInfo.TIME_EXPIRE_OPT_NONE.equals(flowInfo.getExpireOpt()) ||
                    NodeInfo.TIME_EXPIRE_OPT_NOTIFY.equals(flowInfo.getExpireOpt())) {
                    flowInst.setTimerStatus("E");
                } else {
                    flowInst.setTimerStatus("W");
                }
                flowInstanceDao.updtFlowTimerStatus(flowInst.getFlowInstId(), flowInst.getTimerStatus());
            }
        }
    }

    private void doNodeExpiredOpt() {
        List<NodeInstance> activeNodes =  nodeInstanceDao.listExpireNodeInstance();
        if (activeNodes == null || activeNodes.isEmpty()) {
            return;
        }
        for (NodeInstance nodeInst : activeNodes){
            NodeInfo nodeInfo = nodeInfoDao.getObjectById(nodeInst.getNodeCode());
            FlowInstance flowInst = flowInstanceDao.getObjectById(nodeInst.getFlowInstId());

            if(NodeInfo.TIME_EXPIRE_OPT_END_FLOW.equals(nodeInfo.getExpireOpt()) ||
                NodeInfo.TIME_EXPIRE_OPT_SUBMIT.equals(nodeInfo.getExpireOpt()) ||
                NodeInfo.TIME_EXPIRE_OPT_CALL_API.equals(nodeInfo.getExpireOpt())){
                String message = "OK！";
                //* N：通知（预警）， O:不处理 ， X：挂起， E：终止（流程）， C：完成（强制提交,提交失败就挂起）
                if (NodeInfo.TIME_EXPIRE_OPT_END_FLOW.equals(nodeInfo.getExpireOpt())) {
                    message = "节点超时，自动终止流程";
                    flowInst.setInstState(FlowInstance.FLOW_STATE_FORCE);
                    flowInstanceDao.updateObject(flowInst);
                } else if (NodeInfo.TIME_EXPIRE_OPT_SUBMIT.equals(nodeInfo.getExpireOpt())) {
                    message = "节点超时，自动提交节点";
                    flowEngine.submitOpt(SubmitOptOptions.create().nodeInst(nodeInst.getNodeInstId())
                        .user(nodeInst.getUserCode()).unit(nodeInst.getUnitCode()).tenant(flowInst.getTopUnit()));
                } else if (NodeInfo.TIME_EXPIRE_OPT_CALL_API.equals(nodeInfo.getExpireOpt())) {
                    JSONObject params = JSONObject.from(flowInst);
                    params.putAll(JSONObject.from(nodeInfo));
                    params.putAll(JSONObject.from(nodeInst));
                    logger.info("节点超时，自动运行api网关" + nodeInfo.getExpireCallApi() + "，参数:" + params);
                    Object obj =  ddeDubboTaskRun.runTask(nodeInfo.getExpireCallApi(), params);
                    message = "节点超时，自动运行api网关:" +JSON.toJSONString(obj);
                }

                FlowWarning flowWarning = new FlowWarning();
                flowWarning.setWarningType("E");
                flowWarning.setObjType("N");
                flowWarning.setFlowInstId(flowInst.getFlowInstId());
                flowWarning.setNodeInstId(nodeInst.getNodeInstId());
                flowWarning.setWarningTime(DatetimeOpt.currentUtilDate());
                flowWarning.setWarningMsg(message);
                flowWarning.setSendUsers("system");
                wfRuntimeWarningDao.saveNewObject(flowWarning);
            }
            nodeInst.setTimerStatus("E");
            nodeInstanceDao.updtNodeTimerStatus(nodeInst.getNodeInstId(), nodeInst.getTimerStatus());
        }
    }

    private void doFlowExpiredOpt() {
        List<FlowInstance> activeFlows = flowInstanceDao.listExpireFlowInstance();
        if (activeFlows == null || activeFlows.isEmpty()) {
            return;
        }
        for (FlowInstance flowInst : activeFlows) {
            FlowInfo flowInfo = flowInfoDao.getFlowDefineByID(flowInst.getFlowCode(), flowInst.getVersion());
            if (NodeInfo.TIME_EXPIRE_OPT_END_FLOW.equals(flowInfo.getExpireOpt()) ||
                NodeInfo.TIME_EXPIRE_OPT_CALL_API.equals(flowInfo.getExpireOpt())) {
                String message = "OK！";
                if (NodeInfo.TIME_EXPIRE_OPT_END_FLOW.equals(flowInfo.getExpireOpt())) {
                    message = "流程超时，自动终止流程";
                    flowInst.setInstState(FlowInstance.FLOW_STATE_FORCE);
                    flowInstanceDao.updateObject(flowInst);
                } else if (NodeInfo.TIME_EXPIRE_OPT_CALL_API.equals(flowInfo.getExpireOpt())){
                    JSONObject params = JSONObject.from(flowInst);
                    logger.info("流程超时，自动运行api网关" + flowInfo.getExpireCallApi() + "，参数:" + params);
                    Object obj = ddeDubboTaskRun.runTask(flowInfo.getExpireCallApi(), params);
                    message = "流程超时，自动运行api网关:" +JSON.toJSONString(obj);
                }
                FlowWarning flowWarning = new FlowWarning();
                flowWarning.setWarningType("E");
                flowWarning.setObjType("F");//流程
                flowWarning.setFlowInstId(flowInst.getFlowInstId());
                flowWarning.setWarningTime(DatetimeOpt.currentUtilDate());
                flowWarning.setWarningMsg(message);
                flowWarning.setSendUsers("system");
                wfRuntimeWarningDao.saveNewObject(flowWarning);
            }
            flowInst.setTimerStatus("E");
            flowInstanceDao.updtFlowTimerStatus(flowInst.getFlowInstId(), flowInst.getTimerStatus());
        }
    }

    private void doStageExpiredOpt() {
        List<StageInstance> activeStages =  stageInstanceDao.listExpireStageInstance();
        if (activeStages == null || activeStages.isEmpty()) {
            return;
        }
        for (StageInstance stageInst : activeStages) {
            FlowStage stageInfo = flowStageDao.getObjectById(stageInst.getStageId());
            // 判断是否有执行的节点
            int activeTimerNodes = nodeInstanceDao.countActiveTimerNodeByFlowStage(
                stageInst.getFlowInstId(), stageInst.getStageCode());

            if ( activeTimerNodes>0 &&
                (NodeInfo.TIME_EXPIRE_OPT_END_FLOW.equals(stageInfo.getExpireOpt()) ||
                NodeInfo.TIME_EXPIRE_OPT_CALL_API.equals(stageInfo.getExpireOpt())) ) {
                FlowInstance flowInst = flowInstanceDao.getObjectById(stageInst.getFlowInstId());
                String message = "OK！";
                if (NodeInfo.TIME_EXPIRE_OPT_END_FLOW.equals(stageInfo.getExpireOpt())) {
                    message = "阶段超时，自动终止流程";
                    flowInst.setInstState(FlowInstance.FLOW_STATE_FORCE);
                    flowInstanceDao.updateObject(flowInst);
                } else if (NodeInfo.TIME_EXPIRE_OPT_CALL_API.equals(stageInfo.getExpireOpt())) {
                    JSONObject params = JSONObject.from(flowInst);
                    params.putAll(JSONObject.from(stageInfo));
                    params.putAll(JSONObject.from(stageInst));
                    logger.info("阶段超时，自动运行api网关" + stageInfo.getExpireCallApi() + "，参数:" + params);
                    Object obj = ddeDubboTaskRun.runTask(stageInfo.getExpireCallApi(), params);
                    message = "阶段超时，自动运行api网关:" +JSON.toJSONString(obj);
                }
                FlowWarning flowWarning = new FlowWarning();
                flowWarning.setWarningType("E");
                flowWarning.setObjType("P");//阶段
                flowWarning.setFlowInstId(stageInst.getFlowInstId());
                flowWarning.setFlowStage(stageInfo.getStageCode());
                flowWarning.setWarningTime(DatetimeOpt.currentUtilDate());
                flowWarning.setWarningMsg(message);
                flowWarning.setSendUsers("system");
                wfRuntimeWarningDao.saveNewObject(flowWarning);
            }

            stageInst.setTimerStatus("E");
            stageInstanceDao.updtStageTimerStatus(stageInst.getFlowInstId(), stageInst.getStageId(), stageInst.getTimerStatus());
        }
    }

    private void runEventTask(int maxRows) {
        // 获取所有事件 来处理
        List<FlowEventInfo> events = flowEventDao.listEventForOpt(maxRows);
        // 获取所有 时间事件的同步节点
        if (events != null && !events.isEmpty()) {
            for (FlowEventInfo eventInfo : events) {
                List<NodeInstance> nodes = nodeInstanceDao.listNodeInstByState(eventInfo.getFlowInstId(), NodeInstance.NODE_STATE_SYNC);
                boolean hasOptEvent = false;
                String successOpt = FlowEventInfo.OPT_STATE_SUCCESS;//"S";
                try {
                    if (nodes != null) {
                        for (NodeInstance nodeInst : nodes) {
                            nodeInstanceDao.fetchObjectReference(nodeInst, "node");
                            if (NodeInfo.SYNC_NODE_TYPE_MESSAGE.equals(nodeInst.getNode().getNodeSyncType())
                                && StringUtils.equals(eventInfo.getEventName(), nodeInst.getNode().getMessageCode())) {
                                hasOptEvent = true;
                                Object ret = flowEngine.submitOpt(
                                    SubmitOptOptions.create().nodeInst(nodeInst.getNodeInstId()));
                                eventInfo.setOptResult(StringBaseOpt.castObjectToString(ret));
                            }
                        }
                    }
                } catch (ObjectException e) {
                    successOpt = FlowEventInfo.OPT_STATE_FAILED;// "F";
                    eventInfo.setOptResult(e.getMessage());
                }
                eventInfo.setOptTime(DatetimeOpt.currentUtilDate());
                if (hasOptEvent) {
                    eventInfo.setOptState(successOpt);
                } else {
                    FlowInstance flowInst = flowInstanceDao.getObjectById(eventInfo.getFlowInstId());
                    if (flowInst == null || !FlowInstance.FLOW_STATE_NORMAL.equals(flowInst.getInstState())) {
                        eventInfo.setOptState(FlowEventInfo.OPT_STATE_EXPIRED);
                        eventInfo.setOptResult("流程不在正常运行状态！");
                    }
                }
                // 没有处理的也要更新一下optTime 下一个循环排在后面
                flowEventDao.updateObject(eventInfo);
            }
        }
    }

    private void doTimerSyncNodeEvent(){
        List<NodeInstance> activeNodes =  nodeInstanceDao.listNeedSubmitSyncNodeInstance();
        if (activeNodes == null || activeNodes.isEmpty()) {
            return;
        }
        for (NodeInstance nodeInst : activeNodes){
            FlowInstance flowInst = flowInstanceDao.getObjectById(nodeInst.getFlowInstId());
            //自动提交
            try {
                flowEngine.submitOpt(SubmitOptOptions.create().nodeInst(nodeInst.getNodeInstId())
                    .user(nodeInst.getUserCode()).unit(nodeInst.getUnitCode()).tenant(flowInst.getTopUnit()));
            } catch (Exception e){
                nodeInstanceDao.updtNodeTimerStatus(nodeInst.getNodeInstId(), FlowWarning.TIMER_STATUS_EXCEED);
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void doFlowTimerJob() {
        runEventTask(100);
        doTimerSyncNodeEvent();

        doNodeAlertWarning();
        doStageAlertWarning();
        doFlowAlertWarning();

        doNodeExpiredOpt();
        doStageExpiredOpt();
        doFlowExpiredOpt();
    }
}
