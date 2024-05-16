package com.centit.workflow.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.centit.dde.adapter.DdeDubboTaskRun;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.basedata.NoticeMessage;
import com.centit.product.oa.service.WorkDayManager;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.NumberBaseOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.ObjectException;
import com.centit.support.common.WorkTimeSpan;
import com.centit.support.compiler.Lexer;
import com.centit.support.compiler.Pretreatment;
import com.centit.workflow.commons.SubmitOptOptions;
import com.centit.workflow.dao.*;
import com.centit.workflow.po.*;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.FlowEventService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 工作流引擎定时检测任务期限，并发出相应的消息
 *
 * @author codefan
 * @create 2012-2-23
 */
@Component
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
    private FlowEventService flowEventService;

    @Autowired
    private FlowEngine flowEngine;

    @Autowired
    private StageInstanceDao stageInstanceDao;

    @Autowired
    private WorkDayManager workDayManager;

    @Autowired
    private DdeDubboTaskRun ddeDubboTaskRun;

    @Value("${workflow.flowTimeStart:true}")
    private Boolean flowTimeStart;

    @Value("${workflow.amStart:830}")
    private int amStart;

    @Value("${workflow.amEnd:1130}")
    private int amEnd;

    @Value("${workflow.pmStart:1300}")
    private int pmStart;

    @Value("${workflow.pmEnd:1800}")
    private int pmEnd;

    /**
     * 发送通知消息给待办用户
     * @param nodeInstId 节点编号
     * @return 0/1 是否发送
     */
    private int sendNotifyMessage(String nodeInstId) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null || NodeInstance.TASK_ASSIGN_TYPE_DYNAMIC.equals(nodeInst.getTaskAssigned())) {
            return 0;
        }
        NodeInfo nodeInfo = nodeInfoDao.getObjectById(nodeInst.getNodeCode());
        if(StringUtils.isBlank(nodeInfo.getNoticeType())){
            return 0;
        }

        //TODO 根据 nodeInfo.getNoticeType() 判断发送方式 notificationCenter.sendMessageAppointedType()
        if("api".equals(nodeInfo.getNoticeType())){
            if(StringUtils.isBlank(nodeInfo.getNoticeUserExp())){
                return 0;
            }
            JSONObject jsonObject = JSONObject.from(nodeInfo);
            jsonObject.putAll(JSONObject.from(nodeInst));
            FlowInstance flowInst = flowInstanceDao.getObjectById(nodeInst.getFlowInstId());
            if (StringUtils.isNotBlank(flowInst.getFlowOptTag())) {
                if ("{".equals(Lexer.getFirstWord(flowInst.getFlowOptTag()))) {
                    jsonObject.putAll(JSON.parseObject(flowInst.getFlowOptTag()));
                } else {
                    jsonObject.put("optTag", flowInst.getFlowOptTag());
                }
            }
            ddeDubboTaskRun.runTask(nodeInfo.getNoticeUserExp(), jsonObject);
            return 1;
        }
        //TODO 根据 nodeInfo.getNoticeMessage() 判断发送内容
        String msgContent;
        if(StringUtils.isNotBlank(nodeInfo.getNoticeMessage())){
            JSONObject jsonObject = JSONObject.from(nodeInfo);
            jsonObject.putAll(JSONObject.from(nodeInst));
            msgContent = Pretreatment.mapTemplateString(nodeInfo.getNoticeMessage(), jsonObject);
        } else {
            msgContent = "业务" + nodeInst.getFlowOptName() + "(" + nodeInst.getFlowInstId() + ")的" +
                nodeInst.getNodeName() + "(" + nodeInst.getNodeInstId() + ")节点超时预警，请尽快处理。";
        }
        //TODO 根据 nodeInfo.getNoticeUserExp() 判断发送人员 -- nodeInst.getUserCode()
        NoticeMessage noticeMessage = NoticeMessage.create().subject("节点预报警提示")
            .content(msgContent)
            .operation("WF_WARNING").method("NOTIFY").tag(String.valueOf(nodeInstId));
        notificationCenter.sendMessage("system", nodeInst.getUserCode(), noticeMessage);
        return 1;
    }

    /**
     * 根据数据库计算出来的预报警发出对应的通知即可
     */
    @Scheduled(cron = "0 1/5 8-18 * * ?")
    @Transactional
    public void notifyTimeWaring() {
        //直接从  wfRuntimeWarningDao 读取 报警信息，发送通知，设置已通知标志位
        List<FlowWarning> warningList = wfRuntimeWarningDao.listNeedNotifyWarning();
        if (warningList == null) {
            return;
        }
        int nn = 0;
        for (FlowWarning warn : warningList) {
            if ("N".equals(warn.getObjType())) {
                nn += sendNotifyMessage(warn.getNodeInstId());
            } else if ("F".equals(warn.getObjType())) {
                List<NodeInstance> nodelist = nodeInstanceDao.listActiveTimerNodeByFlow(warn.getFlowInstId());
                for (NodeInstance node : nodelist) {
                    nn += sendNotifyMessage(node.getNodeInstId());
                }
            } else if ("P".equals(warn.getObjType())) {
                List<NodeInstance> nodelist = nodeInstanceDao.listActiveTimerNodeByFlowStage(
                    warn.getFlowInstId(), warn.getFlowStage());
                for (NodeInstance node : nodelist) {
                    nn += sendNotifyMessage(node.getNodeInstId());
                }
            }

            warn.setSendMsgTime(DatetimeOpt.currentUtilDate());
            warn.setNoticeState("1");
            wfRuntimeWarningDao.updateObject(warn);
        }
        logger.info("通知中心发现 " + warningList.size() + "预警信息，并通知了" + nn + "个用户。");
    }

    @Scheduled(cron = "0 1/2 5-23 * * ?")
    @Transactional
    public void runEntity() {
        /**这部分内容 也可以放到后台 通过数据库来执行，在程序中执行 如果服务器停止则计时会不正确，
         *  并且如果部署到多个应用服务器 会出现重复扣除时间的问题
         *  在数据库中执行 复杂在要重新实现 当前时间是否是工作时间的问题
         */
        long consumeTime = 2;
        consumeLifeTime(consumeTime);
        runEventTask(500);
    }

    public boolean isWorkTime(Map<String, Boolean> cached, String topUnit) {
        if(cached.containsKey(topUnit)){
            return cached.get(topUnit);
        }
        Date workTime = DatetimeOpt.currentUtilDate();
        boolean workdDay =  workDayManager.isWorkDay(topUnit, workTime);
        if (!workdDay) {
            cached.put(topUnit, false);
            return false;
        }
        int m = DatetimeOpt.getMinute(workTime) + 100 * DatetimeOpt.getHour(workTime);
        //默认朝九晚五
        boolean isWT=  (m > amStart && m < amEnd) || (m > pmStart && m < pmEnd);
        cached.put(topUnit, isWT);
        return isWT;
    }

    private void consumeLifeTime(long consumeTime) {
        // 获取审批中流程并且需求计时的流程实例
        List<FlowInstance> activeFlows = flowInstanceDao.listAllActiveTimerFlowInst();
        if (activeFlows == null || activeFlows.size() < 1) {
            return;
        }

        Map<String, Boolean> cached = new HashMap<>(100);
        for (FlowInstance flowInst : activeFlows) {
            if(! isWorkTime( cached, flowInst.getTopUnit())) // 不是工作日跳过
                continue;

            // 获取计时流程中 办理中节点并且需要计时的节点实例
            List<NodeInstance> nodeList = nodeInstanceDao.listActiveTimerNodeByFlow(flowInst.getFlowInstId());
            if (nodeList == null || nodeList.size() < 1) {
                continue;
            }
            // flowconsume 是否需要更新流程实例的预警时间
            Boolean flowconsume = false;
            // stopFlow 是否结束流程
            Boolean stopFlow = false;
            // T 计时、有期限   F 不计时   H仅环节计时  暂停P
            Set<String> stageCodes = new HashSet<>();
            for (NodeInstance nodeInst : nodeList) {
                NodeInfo nodeInfo = nodeInfoDao.getObjectById(nodeInst.getNodeId());

                //如果节点实例超时或者节点满足预警规则 都进行处理
                String warningRule = nodeInfo.getWarningRule();
                String warningParam = nodeInfo.getWarningParam();
                // 是否根据预警规则进行预警
                boolean warning = false;
                // 预警类别 warningType  W，预警  A  报警  N 提醒  O 其他
                String warningType = "";
                // 预警规则： R：运行时间  L:剩余时间  P：比率
                if ("R".equals(warningRule)) {
                    warning = new WorkTimeSpan(warningParam).toNumberAsMinute() >= nodeInst.getPromiseTime()- nodeInst.getTimeLimit();
                } else if ("L".equals(warningRule)) {
                    warning = new WorkTimeSpan(warningParam).toNumberAsMinute()  >= nodeInst.getTimeLimit();
                } else  if ("P".equals(warningRule)) {
                    if (StringUtils.isNotBlank(warningParam) && nodeInst.getPromiseTime() > 0) {
                        warning = parse(warningParam) >= nodeInst.getTimeLimit().doubleValue() / nodeInst.getPromiseTime().doubleValue();
                    }
                }

                if (nodeInst.getTimeLimit() <= 0) {
                    // 超期预警
                    warningType = "W";
                } else if (warning) {
                    // 预警规则预警
                    warningType = "N";
                }

                if ((NodeInfo.TIME_LIMIT_NORMAL.equals(nodeInst.getIsTimer()) || NodeInfo.TIME_LIMIT_ONLY_NODE.equals(nodeInst.getIsTimer()))
                    && nodeInst.getTimeLimit() <= 0 ) {
                    // 同步节点的方式为时间，时间到达后自动提交节点
                    if (NodeInstance.NODE_STATE_SYNC.equals(nodeInst.getNodeState()) && NodeInfo.SYNC_NODE_TYPE_TIME.equals(nodeInfo.getOptType())) {
                        flowEngine.submitOpt(SubmitOptOptions.create().nodeInst(nodeInst.getNodeInstId())
                            .user(nodeInst.getUserCode()).unit(nodeInst.getUnitCode()).tenant(flowInst.getTopUnit()));
                    } else {
                        //* N：通知（预警）， O:不处理 ， X：挂起， E：终止（流程）， C：完成（强制提交,提交失败就挂起）
                        if (NodeInfo.TIME_EXPIRE_OPT_END_FLOW.equals(nodeInfo.getExpireOpt())) {
                            stopFlow = true;
                            break;
                        } else if (NodeInfo.TIME_EXPIRE_OPT_SUBMIT.equals(nodeInfo.getExpireOpt())) {
                            //自动提交
                            flowEngine.submitOpt(SubmitOptOptions.create().nodeInst(nodeInst.getNodeInstId())
                                .user(nodeInst.getUserCode()).unit(nodeInst.getUnitCode()).tenant(flowInst.getTopUnit()));
                            //} else if ("X".equals(nodeInfo.getExpireOpt())) {
                        } else if (NodeInfo.TIME_EXPIRE_OPT_NOTIFY.equals(nodeInfo.getExpireOpt())) {
                            List<FlowWarning> flowWarnings = wfRuntimeWarningDao.listFlowWarning(flowInst.getFlowInstId(), nodeInst.getNodeInstId(),
                                warningType, "N", null);
                            if (flowWarnings == null || flowWarnings.isEmpty()) {
                                // 存入流程告警表 wf_runtime_warning
                                FlowWarning flowWarning = new FlowWarning(flowInst.getFlowInstId(), nodeInst.getNodeInstId(), warningType, "N");
                                wfRuntimeWarningDao.saveNewObject(flowWarning);
                            }
                        }
                    }
                }

                // 更新节点实例剩余办理时间
                if ((NodeInfo.TIME_LIMIT_NORMAL.equals(nodeInst.getIsTimer()) || NodeInfo.TIME_LIMIT_ONLY_NODE.equals(nodeInst.getIsTimer())) &&
                    (nodeInst.getTimeLimit() != null)) {
                    nodeInst.setTimeLimit(nodeInst.getTimeLimit() - consumeTime);
                    DatabaseOptUtils.doExecuteSql(nodeInstanceDao,
                            "update WF_NODE_INSTANCE set TIME_LIMIT = ? where NODE_INST_ID=?",
                            new Object[] {nodeInst.getTimeLimit(), nodeInst.getNodeInstId()});
                    //nodeInstanceDao.updateObject(nodeInst);
                }

                if(StringUtils.isNotBlank(nodeInst.getStageCode())) {
                    stageCodes.add(nodeInst.getStageCode());
                }
                // 非同步节点计时，则流程也计时
                if (NodeInfo.TIME_LIMIT_NORMAL.equals(nodeInst.getIsTimer()) && !NodeInstance.NODE_STATE_SYNC.equals(nodeInst.getNodeState())
                    /*&& "T".equals( node.getIsTrunkLine())*/) {
                    flowconsume = true;
                }
            }

            // 流程实例预警（不支持流程实例预警规则）
            if (FlowInstance.FLOW_TIMER_STATE_RUN.equals(flowInst.getIsTimer()) && flowInst.getTimeLimit() != null && flowInst.getTimeLimit() <= 0) {
                List<FlowWarning> flowWarnings = wfRuntimeWarningDao.listFlowWarning(flowInst.getFlowInstId(),
                    "W", "F", null);
                if (flowWarnings == null || flowWarnings.isEmpty()) {
                    // 存入流程告警表 wf_runtime_warning
                    FlowWarning flowWarning = new FlowWarning(flowInst.getFlowInstId(), "0", "W", "F");
                    wfRuntimeWarningDao.saveNewObject(flowWarning);
                }
            }

            //如果关闭流程，流程状态置为C
            if (stopFlow) {
                flowInst.setInstState(FlowInstance.FLOW_STATE_COMPLETE);
                flowInstanceDao.updateObject(flowInst);
                continue;
            }
            // FlowInstance.FLOW_TIMER_STATE_RUN.equals(flowInst.getIsTimer()) 流程计时状态是否要考虑其实是一个问题
            // 更新流程实例剩余办理时间
            if (flowconsume && FlowInstance.FLOW_TIMER_STATE_RUN.equals(flowInst.getIsTimer()) && flowInst.getTimeLimit() != null) {
                flowInst.setTimeLimit(flowInst.getTimeLimit() - consumeTime);
                flowInstanceDao.updateObject(flowInst);
            }

            // 更新阶段实例剩余办理时间
            for (String stageCode : stageCodes) {
                StageInstance stageInstance = stageInstanceDao.getStageInstanceByCode(flowInst.getFlowInstId(), stageCode);
                if (null != stageInstance && stageInstance.getPromiseTime() > 0) {
                    // 阶段预警
                    if (stageInstance.getTimeLimit() > 0 && stageInstance.getTimeLimit() - consumeTime < 0) {
                        FlowWarning flowWarning = new FlowWarning(flowInst.getFlowInstId(), stageCode, "W", "P");
                        wfRuntimeWarningDao.saveNewObject(flowWarning);
                    }
                    stageInstance.setTimeLimit(stageInstance.getTimeLimit() - consumeTime);
                    if (StageInstance.STAGE_TIMER_STATE_STARTED.equals(stageInstance.getStageBegin())) {
                        stageInstance.setLastUpdateTime(DatetimeOpt.currentUtilDate());
                    } else {
                        stageInstance.setStageBegin(StageInstance.STAGE_TIMER_STATE_STARTED);
                        stageInstance.setBeginTime(DatetimeOpt.currentUtilDate());
                        stageInstance.setLastUpdateTime(DatetimeOpt.currentUtilDate());
                    }
                    stageInstanceDao.updateObject(stageInstance);
                }
            }
        }
    }

    private double parse(String ratio) {
        if (ratio.contains("/")) {
            String[] rat = ratio.split("/");
            if(rat.length>2) {
                return NumberBaseOpt.castObjectToDouble(rat[0], 0.0) /
                    NumberBaseOpt.castObjectToDouble(rat[1], 100.0);
            }
            return NumberBaseOpt.castObjectToDouble(rat[0], 0.0);
        } else if (ratio.contains("%")) {
            String percent = ratio.substring(0, ratio.indexOf('%'));
            return NumberBaseOpt.castObjectToDouble(percent, 0.0) / 100.0;
        } else {
            return NumberBaseOpt.castObjectToDouble(ratio, 0.0);
        }
    }

    private void runEventTask(int maxRows) {
        // 获取所有事件 来处理
        List<FlowEventInfo> events = flowEventService.listEventForOpt(maxRows);
        // 获取所有 时间事件的同步节点
        if (events != null && events.size() > 0) {
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
                if (hasOptEvent) {
                    eventInfo.setOptState(successOpt);
                    eventInfo.setOptTime(DatetimeOpt.currentUtilDate());
                    flowEventService.updateEvent(eventInfo);
                } else {
                    FlowInstance flowInst = flowInstanceDao.getObjectById(eventInfo.getFlowInstId());
                    if (flowInst == null || !FlowInstance.FLOW_STATE_NORMAL.equals(flowInst.getInstState())) {
                        eventInfo.setOptState(FlowEventInfo.OPT_STATE_EXPIRED);
                        eventInfo.setOptTime(DatetimeOpt.currentUtilDate());
                        eventInfo.setOptResult("流程不在正常运行状态！");
                        flowEventService.updateEvent(eventInfo);
                    }
                }

            }
        }
        //nodeInstanceDao.listNodeInstByState("T");
    }
}
