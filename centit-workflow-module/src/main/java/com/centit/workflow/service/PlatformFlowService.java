package com.centit.workflow.service;

import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.UserTask;

import java.util.List;
import java.util.Map;

/**
 * 获取大平台的数据，整合进工作流。包括待办任务，办理Url等涉及到系统信息的数据
 */
public interface PlatformFlowService {


    /**
     * 查询任务，包含静态任务和动态任务
     * @param searchColumn
     * @param pageDesc
     * @return
     */
    List<UserTask> queryTask(Map<String, Object> searchColumn, PageDesc pageDesc);

    /**
     * 获取节点操作Url
     * @param optCode
     * @return
     */
    String getNodeOptUrl(String optCode);

    /**
     * 获取动态待办
     * @param searchColumn
     * @param pageDesc
     * @return
     */
    List<UserTask> queryDynamicTask(Map<String, Object> searchColumn,PageDesc pageDesc);

    /**
     * 获取动态待办
     * @param searchColumn 包含nodeInstId，unitCode，userStation
     * @param pageDesc
     * @return
     */
    public List<UserTask> queryDynamicTaskByUnitStation(Map<String, Object> searchColumn, PageDesc pageDesc);
}
