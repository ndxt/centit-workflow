/**
 * 
 */
package com.centit.workflow.service.impl;

import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.database.QueryUtils;
import com.centit.workflow.dao.ActionTaskDao;
import com.centit.workflow.dao.FlowWarningDao;
import com.centit.workflow.dao.NodeInstanceDao;
import com.centit.workflow.po.FlowWarning;
import com.centit.workflow.po.NodeInstance;
import com.centit.workflow.po.UserTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 工作流引擎定时检测任务期限，并发出相应的消息
 * 
 * @author ljy ，codefan
 * @create 2012-2-23
 * @version 
 */
@Component
public class FlowTaskImpl {

    private static final Logger logger = LoggerFactory.getLogger(FlowTaskImpl.class);

    @Resource
    ActionTaskDao actionTaskDao;
    
    @Resource
    NodeInstanceDao nodeInstanceDao;
    
    @Resource
    private FlowWarningDao wfRuntimeWarningDao ;
    /**
     * 这个map需要更换会系统的通知中心
     */
    @Resource
    private NotificationCenter notificationCenter;
  

    public FlowTaskImpl()
    {
    }
    
    private int sendNotifyMessage(Long nodeInstId){
        List<UserTask> taskList = actionTaskDao
                .listUserTaskByFilter(
                        QueryUtils.createSqlParamsMap("nodeInstId",nodeInstId),new PageDesc(-1,-1));
        int nn=0;
        for(UserTask task : taskList){
            notificationCenter.sendMessage("admin",
                    task.getUserCode(), "节点预报警提示", 
                    "业务"+task.getFlowOptName()+"的"+
                    task.getNodeName()+"节点超时预警，请尽快处理。办理链接为"+
                    task.getNodeOptUrl(),"WF_WARNING", "NOTIFY", String.valueOf(nodeInstId));
            nn++;
        }
        return nn;
    }
    /**
     * 根据数据库计算出来的预报警发出对应的通知即可
     * @param runTime
     */
    @Scheduled(cron = "0 0/5 8-18 * * *")
    @Transactional
    public void notifyTimeWaring(Date runTime){
        //直接从  wfRuntimeWarningDao 读取 报警信息，发送通知，设置已通知标志位
        List<FlowWarning> warningList = wfRuntimeWarningDao.listNeedNotifyWarning();
        int nw = 0; int nn=0;
        if(warningList!=null){
            for(FlowWarning warn : warningList){
                if("N".equals(warn.getObjType())){
                    nn += sendNotifyMessage(warn.getNodeInstId());
                }else if("F".equals(warn.getObjType())){
                    List<NodeInstance> nodelist = nodeInstanceDao.listActiveTimerNodeByFlow(warn.getFlowInstId());
                    for(NodeInstance node:nodelist){
                        nn += sendNotifyMessage(node.getNodeInstId());
                    }
                }else if("P".equals(warn.getObjType())){
                    List<NodeInstance> nodelist = nodeInstanceDao.listActiveTimerNodeByFlowStage(
                            warn.getFlowInstId(),warn.getFlowStage());
                    for(NodeInstance node:nodelist){
                        nn += sendNotifyMessage(node.getNodeInstId());
                    }
                }
                
                warn.setSendMsgTime(DatetimeOpt.currentUtilDate());
                warn.setNoticeState("1");
                wfRuntimeWarningDao.updateObject(warn);
            }
            nw++;
        }
        logger.info("通知中心发现 "+nw+"预警信息，并通知了"+nn+"个用户。");
    }
    
 
}
