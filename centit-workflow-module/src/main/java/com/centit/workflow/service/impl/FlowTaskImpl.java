package com.centit.workflow.service.impl;

import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.basedata.NoticeMessage;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.ObjectException;
import com.centit.support.database.utils.PageDesc;
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

import java.util.Date;
import java.util.List;

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
    ActionTaskDao actionTaskDao;

    @Autowired
    NodeInstanceDao nodeInstanceDao;

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

    @Value("${workflow.flowTimeStart:true}")
    private Boolean flowTimeStart;

    private int sendNotifyMessage(String nodeInstId) {
        List<UserTask> taskList = actionTaskDao.listUserTaskByFilter(
            CollectionsOpt.createHashMap("nodeInstId", nodeInstId), new PageDesc(-1, -1));
        if (taskList == null) {
            return 0;
        }
        int nn = 0;
        for (UserTask task : taskList) {
            NoticeMessage noticeMessage = NoticeMessage.create().subject("节点预报警提示")
                .content("业务" + task.getFlowOptName() + "(" + task.getFlowInstId() + ")的" +
                    task.getNodeName() + "(" + task.getNodeInstId() + ")节点超时预警，请尽快处理。办理链接为 " + task.getNodeOptUrl())
                .operation("WF_WARNING").method("NOTIFY").tag(String.valueOf(nodeInstId));
            notificationCenter.sendMessage("system", task.getUserCode(), noticeMessage);
            nn++;
        }
        return nn;
    }

    /**
     * 根据数据库计算出来的预报警发出对应的通知即可
     */
    @Scheduled(cron = "0 0/5 8-18 * * ?")
    @Transactional
    public void notifyTimeWaring() {
        //直接从  wfRuntimeWarningDao 读取 报警信息，发送通知，设置已通知标志位
        List<FlowWarning> warningList = wfRuntimeWarningDao.listNeedNotifyWarning();
        if (warningList == null) {
            return;
        }
        int nw = 0, nn = 0;
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
        nw++;

        logger.info("通知中心发现 " + nw + "预警信息，并通知了" + nn + "个用户。");
    }

    @Scheduled(cron = "0 1/2 5-23 * * ?")
    @Transactional
    public void runEntity() {

        /**这部分内容 也可以放到后台 通过数据库来执行，在程序中执行 如果服务器停止则计时会不正确，
         *  并且如果部署到多个应用服务器 会出现重复扣除时间的问题
         *  在数据库中执行 复杂在要重新实现 当前时间是否是工作时间的问题
         */
        Date runTime = new Date();
        if (isWorkTime(runTime) && flowTimeStart) {
            long consumeTime = 2;
            consumeLifeTime(consumeTime);
            logger.info(runTime.toString() + "工作时间，各个在办件减少一个即时周期" + consumeTime + "分钟。");
        }

        runEventTask(500);
    }

    private void consumeLifeTime(long consumeTime) {
        // 获取审批中流程并且需求计时的流程实例
        List<FlowInstance> activeFlows = flowInstanceDao.listAllActiveTimerFlowInst();
        if (activeFlows == null || activeFlows.size() < 1) {
            return;
        }
        for (FlowInstance flowInst : activeFlows) {
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
            for (NodeInstance nodeInst : nodeList) {
                NodeInfo nodeInfo = nodeInfoDao.getObjectById(nodeInst.getNodeId());
                //* N：通知（预警）， O:不处理 ， X：挂起， E：终止（流程）， C：完成（强制提交,提交失败就挂起）
                //如果节点实例超时或者节点满足预警规则 都进行处理
                // 预警规则： R：运行时间  L:剩余时间  P：比率
                String warningRule = nodeInfo.getWarningRule();
                String warningParam = nodeInfo.getWarningParam();
                // 是否根据预警规则进行预警
                boolean warning = true;
                // 预警类别 warningType  W，预警  A  报警  N 提醒  O 其他
                String warningType = "";
                if ("R".equals(warningRule)) {

                } else if ("L".equals(warningRule)) {

                } else if ("P".equals(warningRule)) {
                    warning = parse(warningParam) >= nodeInst.getTimeLimit().doubleValue() / nodeInst.getPromiseTime().doubleValue();
                }

                if (nodeInst.getTimeLimit() <= 0) {
                    // 超期预警
                    warningType = "W";
                } else if (warning) {
                    // 预警规则预警
                    warningType = "N";
                }

                if (("T".equals(nodeInst.getIsTimer()) || "H".equals(nodeInst.getIsTimer()))
                    && (warning || nodeInst.getTimeLimit() <= 0)) {
                    if ("E".equals(nodeInfo.getExpireOpt())) {
                        stopFlow = true;
                        break;
                    } else if ("C".equals(nodeInfo.getExpireOpt())) {
                    } else if ("X".equals(nodeInfo.getExpireOpt())) {
                    } else if ("N".equals(nodeInfo.getExpireOpt())) {
                        List<FlowWarning> flowWarnings = wfRuntimeWarningDao.listFlowWarning(flowInst.getFlowInstId(), nodeInst.getNodeInstId(),
                             warningType,"N", null);
                        if (flowWarnings == null || flowWarnings.isEmpty()) {
                            // 存入流程告警表 wf_runtime_warning
                            FlowWarning flowWarning = new FlowWarning(flowInst.getFlowInstId(), nodeInst.getNodeInstId(), warningType, "N");
                            wfRuntimeWarningDao.saveNewObject(flowWarning);
                        }
                    }

                    // 同步节点的方式为时间，时间到达后自动提交节点
                    if ("T".equals(nodeInst.getNodeState()) && NodeInfo.SYNC_NODE_TYPE_TIME.equals(nodeInfo.getOptType())) {
                        flowEngine.submitOpt(SubmitOptOptions.create().nodeInst(nodeInst.getNodeInstId()));
                        // 更新节点状态为 C
                        nodeInst.setNodeState("C");
                        nodeInst.setLastUpdateUser(nodeInst.getUserCode());
                        nodeInst.setLastUpdateTime(DatetimeOpt.currentUtilDate());
                        nodeInstanceDao.updateObject(nodeInst);
                    }
                }

                // 更新节点实例剩余办理时间
                if (("T".equals(nodeInst.getIsTimer()) || "H".equals(nodeInst.getIsTimer())) &&
                    (nodeInst.getTimeLimit() != null)) {
                    nodeInst.setTimeLimit(nodeInst.getTimeLimit() - consumeTime);
                    nodeInstanceDao.updateObject(nodeInst);
                }

                if ("T".equals(nodeInst.getIsTimer())
                    /*&& "T".equals( node.getIsTrunkLine())*/) {
                    flowconsume = true;
                }
            }

            // 流程实例预警（不支持流程实例预警规则）
            if ("T".equals(flowInst.getIsTimer()) && flowInst.getTimeLimit() != null && flowInst.getTimeLimit() <= 0) {
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
                flowInst.setInstState("C");
                flowInstanceDao.updateObject(flowInst);
                continue;
            }

            // 更新流程实例剩余办理时间
            if (flowconsume && flowInst.getTimeLimit() != null) {
                flowInst.setTimeLimit(flowInst.getTimeLimit() - consumeTime);
                flowInstanceDao.updateObject(flowInst);
            }

        }
    }

    private double parse(String ratio) {
        if (ratio.contains("/")) {
            String[] rat = ratio.split("/");
            return Double.parseDouble(rat[0]) / Double.parseDouble(rat[1]);
        } else {
            return Double.parseDouble(ratio);
        }
    }

    public boolean isWorkTime(Date workTime) {
        int m = DatetimeOpt.getMinute(workTime) + 100 * DatetimeOpt.getHour(workTime);
        //默认朝九晚五
        return (m > 830 && m < 1200) || (m > 1330 && m < 1800);
    }

    private void runEventTask(int maxRows) {
        // 获取所有事件 来处理
        List<FlowEventInfo> events = flowEventService.listEventForOpt(maxRows);
        // 获取所有 时间事件的同步节点
        if (events != null && events.size() > 0) {
            for (FlowEventInfo eventInfo : events) {
                List<NodeInstance> nodes = nodeInstanceDao.listNodeInstByState(eventInfo.getFlowInstId(), "T");
                boolean hasOptEvent = false;
                String successOpt = "S";
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
                    successOpt = "F";
                    eventInfo.setOptResult(e.getMessage());
                }
                if (hasOptEvent) {
                    eventInfo.setOptState(successOpt);
                    eventInfo.setOptTime(DatetimeOpt.currentUtilDate());
                    flowEventService.updateEvent(eventInfo);
                } else {
                    FlowInstance flowInst = flowInstanceDao.getObjectById(eventInfo.getFlowInstId());
                    if (flowInst == null || !"N".equals(flowInst.getInstState())) {
                        eventInfo.setOptState("E");
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
