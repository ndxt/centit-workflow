package com.centit.workflow.service;

import com.centit.workflow.po.UserTask;

import java.util.List;

/**
 * 获取大平台的数据，整合进工作流。包括待办任务，办理Url等涉及到系统信息的数据
 */
public interface PlatformFlowService {


    /**
     * 查询任务，包含静态任务和动态任务
     * @param userCOde
     * @return
     */
    List<UserTask> queryTask(String userCOde);

    /**
     * 获取节点操作Url
     * @param optCode
     * @return
     */
    String getNodeOptUrl(String optCode);
}
