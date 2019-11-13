package com.centit.workflow.client.service;

import com.alibaba.fastjson.JSONArray;
import com.centit.support.database.utils.PageDesc;
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
    default FlowInfo getFlowInfo(String flowCode){
        return getFlowInfo(flowCode, -1);
    }

    /**
     * 获取 流程信息
     * @param flowCode 流程代码
     * @param version 流程版本
     * @return 流程信息
     */
    FlowInfo getFlowInfo(String flowCode, long version);

    /**
     * 获取全部最新版本流程
     * @param filterMap 过滤条件
     * @param pageDesc 分页信息
     * @return 返回列表
     */
    List<FlowInfo> listLastVersionFlow(Map<String, Object> filterMap,
                                       PageDesc pageDesc);
    /**
     * 获取 流程所有版本
     * @param flowCode 流程代码
     * @return 流程所有版本
     */
    List<FlowInfo> listFlowInfoVersion(String flowCode, PageDesc pageDesc);

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

    JSONArray/*List<UserInfo>*/ listItemRoleFilter(String flowCode, long version, String itemRoleCode);

    default JSONArray listItemRoleFilter(String flowCode, String itemRoleCode){
        // -1 代表最新的版本
        return listItemRoleFilter(flowCode, -1, itemRoleCode);
    }
}
