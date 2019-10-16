package com.centit.workflow.client.service;

import com.centit.workflow.po.FlowInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by chen_rj on 2018-5-2.
 */
public interface FlowDefineClient {

    /**
     * 获取 流程信息
     * @param flowCode 流程代码
     * @return 流程信息
     */
    FlowInfo getFlowInfo(String flowCode);

    /**
     * 获取 流程信息
     * @param flowCode 流程代码
     * @param version 流程版本
     * @return 流程信息
     */
    FlowInfo getFlowInfo(String flowCode, long version);

    /**
     * 获取 流程所有版本
     * @param flowCode 流程代码
     * @return 流程所有版本
     */
    List<FlowInfo> listFlowInfoVersion(String flowCode);

    /**
     * 获取和业务关联的所有 工作流
     * @param optId 业务代码
     * @return 工作流代码 -> 工作流名称
     */
    Map<String, String> listFlowByOpt(String optId);

    /**
     * 获取流程所有办件角色
     * @param flowCode 流程代码
     * @param version 流程版本
     * @return 办件角色代码 办件角色名称
     */
    Map<String, String> listFlowItemRole(String flowCode, long version);

    default Map<String, String> listFlowItemRole(String flowCode){
        // -1 代表最新的版本
        return listFlowItemRole(flowCode, -1);
    }

    /**
     * 获取流程所有变量
     * @param flowCode 流程代码
     * @param version 流程版本
     * @return 变量 代码 变量 名称
     */
    Map<String, String> listFlowVariable(String flowCode, long version);

    default Map<String, String> listFlowVariable(String flowCode){
        // -1 代表最新的版本
        return listFlowItemRole(flowCode, -1);
    }


    /**
     * 获取流程所有阶段
     * @param flowCode 流程代码
     * @param version 流程版本
     * @return 办件角色代码 办件角色名称
     */
    Map<String, String> listFlowStage(String flowCode, long version);

    default Map<String, String> listFlowStage(String flowCode){
        // -1 代表最新的版本
        return listFlowItemRole(flowCode, -1);
    }
}
