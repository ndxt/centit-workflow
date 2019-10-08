package com.centit.workflow.service;

import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.UserTask;

import java.util.List;
import java.util.Map;

/**
 * 获取大平台的数据，整合进工作流。包括待办任务，办理Url等涉及到系统信息的数据
 * 这个接口 不需要的，
 *
 * 和 任务相关的 迁移到 流程引擎中
 * 和 业务相关的 迁移到 FlowOptService 中
 */
@Deprecated
public interface PlatformFlowService {


    /**
     * 查询任务，包含静态任务和动态任务
     * @param searchColumn
     * @param pageDesc
     * @return
     */
    List<UserTask> queryTask(Map<String, Object> searchColumn, PageDesc pageDesc);

    /**
     * 获取节点操作Url 迁移到 FlowOptService
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
    List<UserTask> queryDynamicTaskByUnitStation(Map<String, Object> searchColumn, PageDesc pageDesc);
}
