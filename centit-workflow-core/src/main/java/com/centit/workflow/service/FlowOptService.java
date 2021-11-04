package com.centit.workflow.service;

import com.alibaba.fastjson.JSONArray;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.FlowOptInfo;
import com.centit.workflow.po.FlowOptPage;
import com.centit.workflow.po.OptTeamRole;
import com.centit.workflow.po.OptVariableDefine;

import java.util.List;
import java.util.Map;

/**
 * 2018年9月12日10:46:54
 * 流程业务服务 —— 业务数据接口
 *
 */
public interface FlowOptService {

    JSONArray listOptInfo(Map<String, Object> filterMap, PageDesc pageDesc);

    FlowOptInfo getFlowOptInfoById(String optId);

    void deleteOptInfoById(String optId);

    void saveOptInfo(FlowOptInfo FlowOptInfo);

    void saveOptPage(FlowOptPage FlowOptDef);

    FlowOptPage getOptPageByCode(String optCode);

    void deleteOptPageByCode(String optCode);

    List<FlowOptPage> listOptPage(Map<String, Object> filterMap, PageDesc pageDesc);

    List<FlowOptPage> listAllOptPageById(String optId);

    List<FlowOptPage> listOptPageById(String optId);

    List<FlowOptPage> listOptAutoRunById(String optId);

    String getOptInfoSequenceId();

    String getOptDefSequenceId();

    List<FlowOptInfo> getListOptInfo();

    FlowOptInfo getOptByModelId(String modelId);

    /**
     * 根据过滤条件查询相关角色定义列表
     * @param filter 过滤条件
     * @param pageDesc 分页参数
     * @return 角色定义列表
     */
    List<OptTeamRole> listOptTeamRolesByFilter(Map<String, Object> filter, PageDesc pageDesc);

    /**
     * 根据主键id获取角色定义
     * @param roleId 主键id
     * @return
     */
    OptTeamRole getOptTeamRoleById(String roleId);

    /**
     * 新增角色定义
     * @param optTeamRole 角色定义
     */
    void saveOptTeamRole(OptTeamRole optTeamRole);

    /**
     * 更新角色定义
     * @param optTeamRole 角色定义
     */
    void updateOptTeamRole(OptTeamRole optTeamRole);

    /**
     * 根据角色定义id删除角色定义
     * @param roleId 角色定义id
     */
    void deleteOptTeamRoleById(String roleId);

    /**
     * 根据过滤条件查询相关变量定义列表
     * @param filter 过滤条件
     * @param pageDesc 分页参数
     * @return 变量定义列表
     */
    List<OptVariableDefine> listOptVariableDefinesByFilter(Map<String, Object> filter, PageDesc pageDesc);

    /**
     * 根据主键id获取变量定义
     * @param variableId 主键id
     * @return
     */
    OptVariableDefine getOptVariableDefineById(String variableId);

    /**
     * 新增变量定义
     * @param optVariableDefine 变量定义
     */
    void saveOptVariableDefine(OptVariableDefine optVariableDefine);

    /**
     * 更新变量定义
     * @param optVariableDefine 变量定义
     */
    void updateOptVariableDefine(OptVariableDefine optVariableDefine);

    /**
     * 根据变量定义id删除变量定义
     * @param variableId 变量定义id
     */
    void deleteOptVariableDefineById(String variableId);

}
